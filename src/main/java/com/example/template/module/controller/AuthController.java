package com.example.template.module.controller;

import com.example.template.common.base.ErrorCode;
import com.example.template.common.base.response.BaseResponse;
import com.example.template.module.domain.dto.auth.UserAuthDTO;
import com.example.template.module.domain.groups.auth.Auth;
import com.example.template.module.domain.vo.auth.UserAuthVO;
import com.example.template.module.service.AuthService;
import com.example.template.util.ThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户认证模块
 *
 * @author hzh
 * @data 2024/10/7 14:52
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "AuthController", description = "用户认证模块")
public class AuthController {
    @Autowired
    private AuthService authService;

    /**
     * 发送-验证码
     */
    @PostMapping("/send/verifyCode")
    @Operation(description = "发送验证码，可以是手机验证码、邮箱验证码，其中都不为空优先发送邮箱验证码",summary = "发送-验证码")
    public BaseResponse<String> sendVerifyCode(@Validated @RequestBody UserAuthDTO userAuthDTO) {
        authService.sendVerifyCode(userAuthDTO);
        return BaseResponse.success("发送成功");
    }

    /**
     * 注册-账号密码
     *
     * @return 返回注册结果
     */
    @PostMapping("/register/password")
    @Operation(description = "注册-账号密码", summary = "注册-账号密码")
    public BaseResponse<String> registerByPassword(@Validated({Auth.RegisterByPassword.class}) @RequestBody UserAuthDTO userAuthDTO) {
        String userPassword = userAuthDTO.getUserPassword();
        String confirmPassword = userAuthDTO.getConfirmPassword();

        // 判断两次输入的密码是否一致
        ThrowUtils.clientExceptionThrowIf(!StringUtils.equals(userPassword, confirmPassword),
                ErrorCode.USER_ERROR_A0120, "两次输入的密码不一致");
        authService.registerByPassword(userAuthDTO);

        return BaseResponse.success("注册成功");
    }

    /**
     * 认证-邮箱验证码
     */
    @PostMapping("/email")
    @Operation(description = "有账号就是直接登录，无账号就是注册", summary = "认证-邮箱验证码")
    public BaseResponse<UserAuthVO> authByEmail(@Validated({Auth.AuthByEmail.class}) @RequestBody UserAuthDTO userAuthDTO) {
        UserAuthVO userAuthVO = authService.authByEmail(userAuthDTO);
        return BaseResponse.success(userAuthVO);
    }

    /**
     * 认证-手机验证码
     */
    @PostMapping("/phone")
    @Operation(description = "有账号就是直接登录，无账号就是注册",
            summary = "认证-手机验证码")
    public BaseResponse<UserAuthVO> authByPhone(@Validated({Auth.AuthByPhone.class}) @RequestBody UserAuthDTO userAuthDTO) {
        UserAuthVO userAuthVO = authService.authByPhone(userAuthDTO);
        return BaseResponse.success(userAuthVO);
    }

    /**
     * 可以通过账号、邮箱、手机号 配合密码的方式进行登录
     *
     * @return 返回登录结果
     */
    @PostMapping("/login/password")
    @Operation(description = "可以通过账号、邮箱、手机号 配合密码的方式进行登录", summary = "登录-账号密码")
    public BaseResponse<UserAuthVO> loginByPassword(@Validated({Auth.LoginByPassword.class}) @RequestBody UserAuthDTO userAuthDTO) {
        UserAuthVO userAuthVO = authService.loginByPassword(userAuthDTO);
        return BaseResponse.success(userAuthVO);
    }

}
