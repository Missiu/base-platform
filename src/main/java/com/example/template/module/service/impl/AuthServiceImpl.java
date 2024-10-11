package com.example.template.module.service.impl;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.common.constant.RedisKeyConstants;
import com.example.template.common.constant.RegexConstants;
import com.example.template.common.constant.UserConstants;
import com.example.template.exception.customize.ClientException;
import com.example.template.module.domain.dto.auth.LoginByAccountDTO;
import com.example.template.module.domain.dto.auth.RegisterByAccountDTO;
import com.example.template.module.domain.entity.User;
import com.example.template.module.domain.vo.auth.LoginVO;
import com.example.template.module.mapper.UserMapper;
import com.example.template.module.service.AuthService;
import com.example.template.util.ExceptionThrowUtils;
import com.example.template.util.UserLoginUtils;
import com.example.template.util.encrypt.EncryptSHAWithSaltUtils;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RAtomicLong;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author hzh
 * @description 针对表【user(用户表)】的数据库操作Service实现
 * @createDate 2024-10-04 20:52:02
 */
@Service
public class AuthServiceImpl extends ServiceImpl<UserMapper, User>
        implements AuthService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedissonClient singleClient;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void registerByAccount(RegisterByAccountDTO registerByAccountDTO) {
        String userAccount = registerByAccountDTO.getUserAccount();
        String userPassword = registerByAccountDTO.getUserPassword();

        // 1. 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserRole, UserConstants.USER);
        User selectUserByAccount = userMapper.selectOne(queryWrapper);
        // 账户已存在
        ExceptionThrowUtils.clientExceptionThrowIf(Objects.nonNull(selectUserByAccount), ErrorCodeEnum.USER_ERROR_A0111);

        // 账号只能包含字母、数字和下划线，且不能以数字开头
        ExceptionThrowUtils.clientExceptionThrowIf(!userAccount.matches(RegexConstants.VALID_USER_ACCOUNT_REGEX), ErrorCodeEnum.USER_ERROR_A0113);
        // 账号长度校验 5-25
        ExceptionThrowUtils.clientExceptionThrowIf(userAccount.length() < 5 || userAccount.length() > 25, ErrorCodeEnum.USER_ERROR_A0114);
        // 验证密码长度 6-126
        ExceptionThrowUtils.clientExceptionThrowIf(userPassword.length() < 6 || userPassword.length() > 126, ErrorCodeEnum.USER_ERROR_A0121);
        // 验证密码强度  验证密码长度 至少 8 个字符，至少包含 1 个大写字母、1 个小写字母、1 个数字和 1 个特殊字符
        ExceptionThrowUtils.clientExceptionThrowIf(!userPassword.matches(RegexConstants.VALID_USER_PASSWORD_REGEX), ErrorCodeEnum.USER_ERROR_A0122);

        // 密码加密
        String salt = EncryptSHAWithSaltUtils.generateSalt();
        String encryptPassword = EncryptSHAWithSaltUtils.hashPassword(userPassword, salt);
        ExceptionThrowUtils.clientExceptionThrowIf(StringUtils.isEmpty(encryptPassword), ErrorCodeEnum.SYSTEM_ERROR_B0001);

        // 插入新用户数据
        User user = new User()
                .setUserAccount(userAccount)
                .setUserPassword(encryptPassword)
                .setPasswordSalt(salt)
                .setUserRole(UserConstants.USER);

        int insert = userMapper.insert(user);
        // 插入失败
        ExceptionThrowUtils.serverExceptionThrowIf(insert <= 0, ErrorCodeEnum.SERVICE_ERROR_C0300);
    }

    @Override
    public LoginVO loginByAccount(LoginByAccountDTO loginByAccountDTO) {
        String userAccount = loginByAccountDTO.getUserAccount();

        // 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User selectUserByAccount = userMapper.selectOne(queryWrapper);
        ExceptionThrowUtils.clientExceptionThrowIf(Objects.isNull(selectUserByAccount), ErrorCodeEnum.USER_ERROR_A0201);

        // 记录用户登录失败次数的 Redis 键
        String loginFailedTimesKey = RedisKeyConstants.LOGIN_FAILED_TIMES + userAccount;

        // 从 Redis 获取登录失败次数
        RBucket<Object> loginFailedTimesBucket = singleClient.getBucket(loginFailedTimesKey);
        Object failedLoginCount = loginFailedTimesBucket.get();
        /* if timesInRedis 为 null 时，说明该用户第一次登录，设置登录失败次数为 0
           else if 当前登录失败次数小于最大登录失败次数，则登录失败次数加 1
           else 登录失败次数超过最大次数，禁止登录  */

        // 判断 Redis 中是否存在失败次数记录
        if (ObjectUtil.isNull(failedLoginCount)) {
            // 第一次登录失败，初始化为 0 并设置过期时间
            loginFailedTimesBucket.set(0L, Duration.ofMinutes(RedisKeyConstants.LOGIN_FAILED_TIMES_EXPIRATION));
        } else {
            // 将 Redis 中的失败次数转换为 Long 类型
            long currentFailedTimes = Long.parseLong(failedLoginCount.toString());

            // 如果失败次数未超过最大允许次数，递增失败次数
            if (currentFailedTimes < UserConstants.MAX_LOGIN_FAILED_TIMES) {
                // 获取原子计数器
                RAtomicLong loginFailedTimesAtomic = singleClient.getAtomicLong(loginFailedTimesKey);

                // 递增失败次数并更新过期时间（确保原子性操作）
                loginFailedTimesAtomic.incrementAndGet();
                loginFailedTimesAtomic.expire(Duration.ofMinutes(RedisKeyConstants.LOGIN_FAILED_TIMES_EXPIRATION));
            } else {
                // 登录失败次数超出限制，抛出异常禁止登录
                throw new ClientException(ErrorCodeEnum.USER_ERROR_A0211);
            }
        }

        // 验证密码
        String userPassword = loginByAccountDTO.getUserPassword();
        String salt = selectUserByAccount.getPasswordSalt();
        String hashPassword = selectUserByAccount.getUserPassword();
        boolean verified = EncryptSHAWithSaltUtils.verifyPassword(userPassword, hashPassword, salt);
        ExceptionThrowUtils.clientExceptionThrowIf(!verified, ErrorCodeEnum.USER_ERROR_A0120);

        // 登录成功，记录登录信息,构建登录返回对象
        LoginVO loginVO = new LoginVO();
        BeanUtils.copyProperties(selectUserByAccount, loginVO);

        // 登录成功, 删除登录失败次数记录
        loginFailedTimesBucket.delete();

        // 保存到 redis 中 key为saToken设定，自动设置
        Long loginId = loginVO.getId();
        StpUtil.login(loginId);
        String token = StpUtil.getTokenValueByLoginId(loginId);
        loginVO.setToken(token);
        return loginVO;
    }
}




