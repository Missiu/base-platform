package com.example.template.common.constant;

/**
 * 用户常量类
 *
 * @author hzh
 * @data 2024/10/7 16:02
 */
public interface UserConstants {
    String ADMIN = "admin";
    String USER = "user";
    String BANNED = "ban";
    Integer MAX_LOGIN_FAILED_TIMES = 5;

    /**
     * 登录成功
     */
    String LOGIN_SUCCESS = "LoginSuccess";

    /**
     * 注销
     */
    String LOGOUT = "Logout";

    /**
     * 注册
     */
    String REGISTER = "Register";

    /**
     * 登录失败
     */
    String LOGIN_FAIL = "LoginFail";


    /**
     * 存放在缓存中的用户键
     */
    String LOGIN_USER_KEY = "login";


}
