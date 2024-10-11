package com.example.template.common.constant;

/**
 * redis key 常量
 *
 * @author hzh
 * @data 2024/10/7 14:48
 */
public interface RedisKeyConstants {
    /**
     * 登录失败次数
     */
    String LOGIN_FAILED_TIMES = "SA-TOKEN:login:failed-times:";

    /**
     * 登录失败次数过期时间（分钟）
     */
    int LOGIN_FAILED_TIMES_EXPIRATION = 10;

    /**
     * 验证码有效期（分钟）
     */
    int CAPTCHA_EXPIRATION = 2;
}
