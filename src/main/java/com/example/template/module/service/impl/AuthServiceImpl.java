package com.example.template.module.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.common.base.CommonConstants;
import com.example.template.common.base.ErrorCode;
import com.example.template.common.constant.CacheConstants;
import com.example.template.common.constant.RegexConstants;
import com.example.template.common.constant.UserConstants;
import com.example.template.exception.customize.ClientException;
import com.example.template.manager.email.EmailDTO;
import com.example.template.manager.email.EmailManager;
import com.example.template.manager.sms.SMSManager;
import com.example.template.module.domain.dto.auth.UserAuthDTO;
import com.example.template.module.domain.entity.User;
import com.example.template.module.domain.vo.auth.UserAuthVO;
import com.example.template.module.mapper.UserMapper;
import com.example.template.module.service.AuthService;
import com.example.template.util.EncryptUtils;
import com.example.template.util.ThrowUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;

/**
 * @author hzh
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-10-04 20:52:02
 */
@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, User>
        implements AuthService {

    private final UserMapper userMapper;
    private final RedissonClient singleClient;
    private final EmailManager emailManager;
    private final SMSManager smsManager;

    /**
     * 构造函数注入
     *
     * @param userMapper   数据库操作对象
     * @param singleClient Redisson 客户端
     * @param emailManager 邮件管理器
     * @param smsManager   短信管理器
     */
    @Autowired
    public AuthServiceImpl(UserMapper userMapper, RedissonClient singleClient, EmailManager emailManager, SMSManager smsManager) {
        this.userMapper = userMapper;
        this.singleClient = singleClient;
        this.emailManager = emailManager;
        this.smsManager = smsManager;
    }

    /**
     * 通过账号密码注册用户
     *
     * @param userAuthDTO 注册信息
     */
    @Override
    public void registerByPassword(UserAuthDTO userAuthDTO) {
        // 获取必要数据
        String userAccount = userAuthDTO.getUserAccount();
        String userPassword = userAuthDTO.getUserPassword();

        // 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        boolean isExists = userMapper.exists(queryWrapper);

        // 账户已存在
        ThrowUtils.clientExceptionThrowIf(isExists, ErrorCode.USER_ERROR_A0111);

        // 账号只能包含字母、数字和下划线，且不能以数字开头
        boolean isStrongAccount = userAccount.matches(RegexConstants.VALID_USER_ACCOUNT_REGEX);
        ThrowUtils.clientExceptionThrowIfNot(isStrongAccount, ErrorCode.USER_ERROR_A0113);

        // 验证密码强度 至少包含 1 个大写字母、1 个小写字母、1 个数字和 1 个特殊字符
        boolean isStrongPassword = userPassword.matches(RegexConstants.VALID_USER_PASSWORD_REGEX);
        ThrowUtils.clientExceptionThrowIfNot(isStrongPassword, ErrorCode.USER_ERROR_A0122);

        // 密码加密
        String salt = EncryptUtils.generateSalt();
        String encryptPassword = EncryptUtils.hashPasswordWithSalt(userPassword, salt);
        ThrowUtils.clientExceptionThrowIf(StringUtils.isEmpty(encryptPassword), ErrorCode.SYSTEM_ERROR_B0001);

        // 插入新用户数据
        User user = new User()
                .setUserAccount(userAccount)
                .setUserPassword(encryptPassword)
                .setPasswordSalt(salt);

        int insert = userMapper.insert(user);
        // 插入失败
        ThrowUtils.serverExceptionThrowIf(insert <= 0, ErrorCode.SERVICE_ERROR_C0300);
    }

    /**
     * 使用账号、邮箱、手机号结合密码登录
     *
     * @param userAuthDTO 登录信息
     * @return 登录成功返回用户信息
     */
    @Override
    public UserAuthVO loginByPassword(UserAuthDTO userAuthDTO) {
        // 获取必要数据
        String userAccount = userAuthDTO.getUserAccount();
        String userPassword = userAuthDTO.getUserPassword();
        String userPhone = userAuthDTO.getUserPhone();
        String userEmail = userAuthDTO.getUserEmail();


        // 判断用户是否存在,并获取用户信息
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserRole, UserConstants.USER);

        // 获取要操作的目标数据
        String targetAccount;
        if (StringUtils.isNotEmpty(userAccount)) {
            queryWrapper.eq(User::getUserAccount, userAccount);
            targetAccount = userAccount;
        } else if (StringUtils.isNotEmpty(userPhone)) {
            queryWrapper.eq(User::getUserPhone, userPhone);
            targetAccount = userPhone;
        } else if (StringUtils.isNotEmpty(userEmail)) {
            queryWrapper.eq(User::getUserEmail, userEmail);
            targetAccount = userEmail;
        } else {
            throw new ClientException(ErrorCode.USER_ERROR_A0201, "账号、手机号、邮箱都为空 无法登录");
        }
        User userInfo = userMapper.selectOne(queryWrapper);

        // 用户不存在
        ThrowUtils.clientExceptionThrowIf(Objects.isNull(userInfo), ErrorCode.USER_ERROR_A0201);

        // 记录用户登录失败次数的 Redis 键
        String loginFailedTimesKey = CacheConstants.LOGIN_FAILED_TIMES + targetAccount;

        // 限制登录失败次数
        RAtomicLong loginFailedTimesAtomic = loginFailedTimesLimit(loginFailedTimesKey);

        // 验证密码
        String salt = userInfo.getPasswordSalt();
        String hashPassword = userInfo.getUserPassword();
        boolean verified = EncryptUtils.verifyHashedPassword(userPassword, hashPassword, salt);
        ThrowUtils.clientExceptionThrowIfNot(verified, ErrorCode.USER_ERROR_A0120);

        // 登录成功删除登录失败次数记录
        loginFailedTimesAtomic.delete();

        // 登录成功，记录登录信息,构建登录返回对象
        return buildUserAuthVO(userInfo);
    }


    /**
     * 通过邮箱和验证码登录或注册
     *
     * @param userAuthDTO 登录信息
     * @return 登录成功返回用户信息
     */
    @Override
    public UserAuthVO authByEmail(UserAuthDTO userAuthDTO) {
        // 获取必要数据
        String userEmail = userAuthDTO.getUserEmail();
        String verifyCode = userAuthDTO.getVerifyCode();

        // 校验验证码
        String verificationCodeRedisKey = CacheConstants.CODE_KEY + userEmail;
        checkVerificationCode(verifyCode, verificationCodeRedisKey);

        // 查询用户信息，判断是否已注册
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserEmail, userEmail);
        User userInfo = userMapper.selectOne(queryWrapper);

        // 没有查到用户信息，注册
        if (ObjectUtil.isNull(userInfo)) {
            // 把邮箱作为账号，保存用户信息
            User user = new User()
                    .setUserAccount(userEmail)
                    .setUserEmail(userEmail);
            int insert = userMapper.insert(user);
            // 插入失败
            ThrowUtils.serverExceptionThrowIf(insert <= 0, ErrorCode.SERVICE_ERROR_C0300);
            // 查询用户信息
            userInfo = userMapper.selectOne(queryWrapper);
        }

        // 记录用户登录失败次数的 Redis 键
        String loginFailedTimesKey = CacheConstants.LOGIN_FAILED_TIMES + userEmail;

        // 限制登录失败次数
        RAtomicLong loginFailedTimesAtomic = loginFailedTimesLimit(loginFailedTimesKey);

        // 登录成功删除登录失败次数记录
        loginFailedTimesAtomic.delete();

        // 登录成功，记录登录信息,构建登录返回对象
        return buildUserAuthVO(userInfo);
    }

    /**
     * 通过手机号和验证码登录或注册
     *
     * @param userAuthDTO 登录信息
     * @return 登录成功返回用户信息
     */
    @Override
    public UserAuthVO authByPhone(UserAuthDTO userAuthDTO) {
        // 获取必要数据
        String userPhone = userAuthDTO.getUserPhone();
        String verifyCode = userAuthDTO.getVerifyCode();

        // 校验验证码
        String verificationCodeRedisKey = CacheConstants.CODE_KEY + userPhone;
        checkVerificationCode(verifyCode, verificationCodeRedisKey);

        // 登录成功，记录登录信息,构建登录返回对象
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserPhone, userPhone);
        User userInfo = userMapper.selectOne(queryWrapper);

        // 没有查到用户信息，注册
        if (ObjectUtil.isNull(userInfo)) {
            // 把邮箱作为账号，保存用户信息
            User user = new User()
                    .setUserAccount(userPhone)
                    .setUserPhone(userPhone);
            int insert = userMapper.insert(user);
            // 插入失败
            ThrowUtils.serverExceptionThrowIf(insert <= 0, ErrorCode.SERVICE_ERROR_C0300);
            // 查询用户信息
            userInfo = userMapper.selectOne(queryWrapper);
        }

        // 记录用户登录失败次数的 Redis 键
        String loginFailedTimesKey = CacheConstants.LOGIN_FAILED_TIMES + userPhone;

        // 限制登录失败次数
        RAtomicLong loginFailedTimesAtomic = loginFailedTimesLimit(loginFailedTimesKey);

        // 登录成功删除登录失败次数记录
        loginFailedTimesAtomic.delete();

        // 登录成功，记录登录信息,构建登录返回对象
        return buildUserAuthVO(userInfo);
    }

    /**
     * 发送验证码
     *
     * @param userAuthDTO 手机号或者验证码
     */
    @Override
    public void sendVerifyCode(UserAuthDTO userAuthDTO) {
        // 获取必要数据
        String userEmail = userAuthDTO.getUserEmail();
        String userPhone = userAuthDTO.getUserPhone();

        // 都不为空，优先进入邮箱发送验证码
        if (StringUtils.isNotEmpty(userEmail)) {
            // 发送注册验证码邮件
            EmailDTO emailDTO = new EmailDTO();
            emailDTO.setTo(userEmail);
            emailDTO.setSubject(emailManager.generateVerificationEmailSubject());
            // 使用 html 格式
            emailDTO.setHtmlFormat(true);
            // 生成验证码
            String verificationCodeRedisKey = CacheConstants.CODE_KEY + userEmail;
            String verificationCode = generateVerificationCode(verificationCodeRedisKey, CommonConstants.DEFAULT_CODE_LENGTH, CacheConstants.EMAIL_CODE_EXPIRATION);
            // 构造邮件内容
            String content = emailManager.generateVerificationEmailTemplate(verificationCode, CacheConstants.EMAIL_CODE_EXPIRATION);
            emailDTO.setContent(content);
            emailManager.sendTextMail(emailDTO);
        } else if (StringUtils.isNotEmpty(userPhone)) {
            // 生成验证码
            String verificationCodeRedisKey = CacheConstants.CODE_KEY + userPhone;
            String verificationCode = generateVerificationCode(verificationCodeRedisKey, CommonConstants.DEFAULT_SMS_CODE_LENGTH, CacheConstants.SMS_CODE_EXPIRATION);

            // 发送注册验证码邮件
            smsManager.sendSms(userPhone, verificationCode);
        } else {
            // 邮箱和手机号都为空，抛出异常
            throw new ClientException(ErrorCode.USER_ERROR_A0100, "邮箱或手机号都为空 无法发送验证码");
        }
    }

    /**
     * 登录失败次数限制
     *
     * @param loginFailedTimesKey 登录失败次数 Redis 键
     * @return 登录失败次数原子计数器
     */
    private RAtomicLong loginFailedTimesLimit(String loginFailedTimesKey) {
        // 获取 Redis 中的登录失败次数原子计数器
        RAtomicLong loginFailedTimesAtomic = singleClient.getAtomicLong(loginFailedTimesKey);

        // 获取当前失败次数，初始化时可能为 0
        long currentFailedTimes = loginFailedTimesAtomic.get();

        // 如果是第一次登录失败，设置过期时间
        if (currentFailedTimes == 0L) {
            loginFailedTimesAtomic.set(1L);  // 初始化为 1
            loginFailedTimesAtomic.expire(Duration.ofMinutes(CacheConstants.LOGIN_FAILED_TIMES_EXPIRATION));
        } else if (currentFailedTimes < UserConstants.MAX_LOGIN_FAILED_TIMES) {
            // 如果失败次数未超过最大允许次数，递增失败次数
            loginFailedTimesAtomic.incrementAndGet();
        } else {
            // 登录失败次数超出限制，抛出异常禁止登录
            throw new ClientException(ErrorCode.USER_ERROR_A0211, "登录失败次数过多，请稍后再试");
        }

        return loginFailedTimesAtomic;
    }

    /**
     * 登录成功，记录登录信息,构建登录返回对象
     *
     * @param user 用户信息
     * @return 登录返回对象
     */
    private UserAuthVO buildUserAuthVO(User user) {
        UserAuthVO userAuthVO = new UserAuthVO();
        BeanUtils.copyProperties(user, userAuthVO);

        Long loginId = userAuthVO.getId();

        // 保存到 redis 中 key为saToken自动设定
        StpUtil.login(loginId);

        // 获取并设置token
        String token = StpUtil.getTokenValueByLoginId(loginId);
        userAuthVO.setToken(token);

        return userAuthVO;
    }

    /**
     * 校验验证码
     *
     * @param verificationCodeRedisKey 保存验证码的 Redis 键
     * @param verifyCode               验证码
     */
    private void checkVerificationCode(String verifyCode, String verificationCodeRedisKey) {
        RBucket<Object> verificationCodeBucket = singleClient.getBucket(verificationCodeRedisKey);
        Object verificationCodeInRedis = verificationCodeBucket.get();
        // 验证码过期
        ThrowUtils.clientExceptionThrowIf(ObjectUtil.isNull(verificationCodeInRedis), ErrorCode.USER_ERROR_A0100, "验证码已过期");
        // 验证码错误
        boolean verified = verificationCodeInRedis.toString().equals(verifyCode);
        ThrowUtils.clientExceptionThrowIfNot(verified, ErrorCode.USER_ERROR_A0132);
    }

    /**
     * 生成验证码
     *
     * @param key        验证码 key
     * @param length     验证码长度
     * @param expireTime 验证码过期时间 单位：分钟
     * @return 验证码
     */
    private String generateVerificationCode(String key, int length, int expireTime) {
        // 生成 length 位数字验证码
        StringBuilder verificationCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            // 生成 0-9 之间的随机数字，并追加到验证码中
            verificationCode.append((int) (Math.random() * 10));
        }
        // 保存验证码到 Redis
        singleClient.getBucket(key).set(verificationCode.toString(), Duration.ofMinutes(expireTime));
        return verificationCode.toString();
    }
}