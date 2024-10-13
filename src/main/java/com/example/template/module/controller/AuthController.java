package com.example.template.module.controller;

import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.common.base.response.BaseResponse;
import com.example.template.module.domain.dto.auth.UserLoginDTO;
import com.example.template.module.domain.dto.auth.UserRegisterDTO;
import com.example.template.module.domain.groups.auth.LoginByAccount;
import com.example.template.module.domain.groups.auth.RegisterByAccount;
import com.example.template.module.domain.vo.auth.UserInfoVO;
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
     * 注册
     *
     * @return 返回注册结果
     */
    @PostMapping("/register-account")
    @Operation(description = "注册-账号密码")
    public BaseResponse<String> registerByAccount(@Validated({RegisterByAccount.class}) @RequestBody UserRegisterDTO userRegisterDTO) {
        String userPassword = userRegisterDTO.getUserPassword();
        String confirmPassword = userRegisterDTO.getConfirmPassword();
        // 判断两次输入的密码是否一致
        ThrowUtils.clientExceptionThrowIf(!StringUtils.equals(userPassword, confirmPassword),
                ErrorCodeEnum.USER_ERROR_A0120, "两次输入的密码不一致");
        authService.registerByAccount(userRegisterDTO);

        return BaseResponse.success("注册成功");
    }

    /**
     * 登录
     *
     * @return 返回登录结果
     */
    @PostMapping("/login-account")
    @Operation(description = "登录-账号密码")
    public BaseResponse<UserInfoVO> loginByAccount(@Validated({LoginByAccount.class}) @RequestBody UserLoginDTO userLoginDTO) {
        UserInfoVO userInfoVO = authService.loginByAccount(userLoginDTO);
        return BaseResponse.success(userInfoVO);
    }


}
