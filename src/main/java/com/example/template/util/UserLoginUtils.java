package com.example.template.util;

import cn.dev33.satoken.context.SaHolder;
import cn.dev33.satoken.stp.StpUtil;
import com.example.template.common.constant.UserConstants;
import com.example.template.module.domain.vo.auth.LoginVO;
import com.example.template.module.service.AuthService;
import lombok.extern.slf4j.Slf4j;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


import java.util.Objects;

/**
 * 登录鉴权工具类
 *
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class UserLoginUtils {

    private static final AuthService AUTH_SERVICE = SpringContextUtils.getBean(AuthService.class);

    /**
     * 用户登录
     *
     * @param loginUser 登录用户信息
     */
    public static void login(LoginVO loginUser) {
        if (!StpUtil.isLogin()) {
            // 存入一份到缓存中
            SaHolder.getStorage()
                    .set(UserConstants.LOGIN_USER_KEY, loginUser);
            StpUtil.login(loginUser.getId());
            StpUtil.getSession().set(UserConstants.LOGIN_USER_KEY, loginUser);
        } else {
            // 如果重复登录，就需要验证当前登录账号和将要登录账号是否相同，不相同则挤掉原账号，确保一个浏览器会话只存有一个账户信息
            if (!Objects.equals(Long.parseLong((String) StpUtil.getLoginId()), loginUser.getId())) {
                StpUtil.logout();
                // 存入一份到缓存中
                SaHolder.getStorage()
                        .set(UserConstants.LOGIN_USER_KEY, loginUser);
                StpUtil.login(loginUser.getId());
                StpUtil.getSession().set(UserConstants.LOGIN_USER_KEY, loginUser);
            }
        }
    }


    /**
     * 获取当前登录用户信息
     * <p>
     * 该方法首先从SaHolder存储中尝试获取登录用户信息如果未找到，则从StpUtil的会话中尝试获取并将结果存储到SaHolder中
     * 这是为了确保在不同的请求处理中，能够统一获取到登录用户信息
     *
     * @return LoginVO 返回登录用户信息对象，如果未找到则返回null
     */
    public static LoginVO getLoginUser() {
        // 从SaHolder存储中获取登录用户信息
        LoginVO loginUser = (LoginVO) SaHolder.getStorage().get(UserConstants.LOGIN_USER_KEY);
        if (Objects.nonNull(loginUser)) {
            // 如果找到登录用户信息，则直接返回
            return loginUser;
        }
        // 如果未找到，则从StpUtil的会话中尝试获取
        loginUser = (LoginVO) StpUtil.getSession().get(UserConstants.LOGIN_USER_KEY);
        if (Objects.isNull(loginUser)) {
            // 当前类名+方法名+日志信息
            log.warn("当前类名：{}，方法名：{}，日志信息：{}", UserLoginUtils.class.getName(), "getLoginUser()", "未找到登录用户信息，请先登录！");
            return null;
        }
        // 将从会话中获取的登录用户信息存储到SaHolder中，以便后续请求可以直接获取
        SaHolder.getStorage().set(UserConstants.LOGIN_USER_KEY, loginUser);
        // 返回登录用户信息
        return loginUser;
    }

    /**
     * 从缓存中获取登录用户ID
     */
    public static Long getLoginUserId() {
        if (Objects.isNull(getLoginUser())) {
            // 当前类名+方法名+日志信息
            log.warn("当前类名：{}，方法名：{}，日志信息：{}", UserLoginUtils.class.getName(), "getLoginUserId()", "未找到登录用户信息，请先登录！");
            return null;
        }
        return getLoginUser().getId();
    }

    /**
     * 从缓存中获取登录用户账户
     */
    public static String getLoginUserAccount() {
        if (Objects.isNull(getLoginUser())) {
            // 当前类名+方法名+日志信息
            log.warn("当前类名：{}，方法名：{}，日志信息：{}", UserLoginUtils.class.getName(), "getLoginUserAccount()", "未找到登录用户信息，请先登录！");
            return null;
        }
        return getLoginUser().getUserAccount();
    }

    /**
     * 登录用户账户角色是否为管理员
     */
    public static boolean isAdmin() {
        if (Objects.isNull(getLoginUser())) {
            // 当前类名+方法名+日志信息
            log.warn("当前类名：{}，方法名：{}，日志信息：{}", UserLoginUtils.class.getName(), "getLoginUserId()", "未找到登录用户信息，请先登录！");
            return false;
        }
        return UserConstants.ADMIN.equals(getLoginUser().getUserRole());
    }

    /**
     * 根据用户LoginId指定用户退出
     */
    public static void logout(Object loginId) {
        StpUtil.logout(loginId);
    }

    /**
     * 用户退出
     */
    public static void logout() {
        StpUtil.logout();
    }

}