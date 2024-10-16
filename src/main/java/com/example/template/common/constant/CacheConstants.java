package com.example.template.common.constant;

/**
 * redis key 常量
 *
 * @author hzh
 * @data 2024/10/7 14:48
 */
public interface CacheConstants {
    /**
     * 登录失败次数
     */
    String LOGIN_FAILED_TIMES = "SA-TOKEN:login:failed-times:";

    /**
     * 登录失败次数过期时间（分钟）
     */
    int LOGIN_FAILED_TIMES_EXPIRATION = 10;

    /**
     * 验证码key
     */
    String CODE_KEY = "SA-TOKEN:code:";

    /**
     * 短信验证码有效期（分钟）
     */
    int SMS_CODE_EXPIRATION = 2;

    /**
     * 邮箱验证码有效期（分钟）
     */
    int EMAIL_CODE_EXPIRATION = 5;

    /**
     * redis 默认过期时间（秒）
     */
    int DEFAULT_EXPIRATION = 60 * 10;


    /**
     * 热点数据key列表
     */
    String[] HOT_KEYS = {};

}
