package com.example.template.module.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.common.constant.RedisKeyConstants;
import com.example.template.common.constant.RegexConstants;
import com.example.template.common.constant.UserConstants;
import com.example.template.exception.customize.ClientException;
import com.example.template.module.domain.dto.auth.UserLoginDTO;
import com.example.template.module.domain.dto.auth.UserRegisterDTO;
import com.example.template.module.domain.entity.User;
import com.example.template.module.domain.vo.auth.UserInfoVO;
import com.example.template.module.mapper.UserMapper;
import com.example.template.module.service.AuthService;
import com.example.template.util.ThrowUtils;
import com.example.template.util.encrypt.EncryptSHAWithSaltUtils;
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

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedissonClient singleClient;

    @Override
    public void registerByAccount(UserRegisterDTO userRegisterDTO) {
        String userAccount = userRegisterDTO.getUserAccount();
        String userPassword = userRegisterDTO.getUserPassword();

        // 1. 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        queryWrapper.eq(User::getUserRole, UserConstants.USER);
        User selectUserByAccount = userMapper.selectOne(queryWrapper);
        // 账户已存在
        ThrowUtils.clientExceptionThrowIf(Objects.nonNull(selectUserByAccount), ErrorCodeEnum.USER_ERROR_A0111);

        // 账号只能包含字母、数字和下划线，且不能以数字开头
        ThrowUtils.clientExceptionThrowIf(!userAccount.matches(RegexConstants.VALID_USER_ACCOUNT_REGEX), ErrorCodeEnum.USER_ERROR_A0113);
        // 验证密码强度 至少包含 1 个大写字母、1 个小写字母、1 个数字和 1 个特殊字符
        ThrowUtils.clientExceptionThrowIf(!userPassword.matches(RegexConstants.VALID_USER_PASSWORD_REGEX), ErrorCodeEnum.USER_ERROR_A0122);

        // 密码加密
        String salt = EncryptSHAWithSaltUtils.generateSalt();
        String encryptPassword = EncryptSHAWithSaltUtils.hashPassword(userPassword, salt);
        ThrowUtils.clientExceptionThrowIf(StringUtils.isEmpty(encryptPassword), ErrorCodeEnum.SYSTEM_ERROR_B0001);

        // 插入新用户数据
        User user = new User()
                .setUserAccount(userAccount)
                .setUserPassword(encryptPassword)
                .setPasswordSalt(salt)
                .setUserRole(UserConstants.USER);

        int insert = userMapper.insert(user);
        // 插入失败
        ThrowUtils.serverExceptionThrowIf(insert <= 0, ErrorCodeEnum.SERVICE_ERROR_C0300);
    }

    @Override
    public UserInfoVO loginByAccount(UserLoginDTO userLoginDTO) {
        String userAccount = userLoginDTO.getUserAccount();
        String userPassword = userLoginDTO.getUserPassword();

        // 判断用户是否存在
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserAccount, userAccount);
        User selectUserByAccount = userMapper.selectOne(queryWrapper);
        ThrowUtils.clientExceptionThrowIf(Objects.isNull(selectUserByAccount), ErrorCodeEnum.USER_ERROR_A0201);

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
        String salt = selectUserByAccount.getPasswordSalt();
        String hashPassword = selectUserByAccount.getUserPassword();
        boolean verified = EncryptSHAWithSaltUtils.verifyPassword(userPassword, hashPassword, salt);
        ThrowUtils.clientExceptionThrowIf(!verified, ErrorCodeEnum.USER_ERROR_A0120);

        // 登录成功，记录登录信息,构建登录返回对象
        UserInfoVO userInfoVO = new UserInfoVO();
        BeanUtils.copyProperties(selectUserByAccount, userInfoVO);

        // 登录成功, 删除登录失败次数记录
        loginFailedTimesBucket.delete();

        // 保存到 redis 中 key为saToken自动设定
        Long loginId = userInfoVO.getId();
        StpUtil.login(loginId);
        String token = StpUtil.getTokenValueByLoginId(loginId);
        userInfoVO.setToken(token);
        return userInfoVO;
    }
}




