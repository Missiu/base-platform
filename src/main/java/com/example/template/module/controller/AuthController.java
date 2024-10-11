package com.example.template.module.controller;

import com.example.template.common.base.BaseResponse;
import com.example.template.common.base.ErrorCodeEnum;
import com.example.template.module.domain.dto.auth.LoginByAccountDTO;
import com.example.template.module.domain.dto.auth.RegisterByAccountDTO;
import com.example.template.module.domain.vo.auth.LoginVO;
import com.example.template.module.service.AuthService;
import com.example.template.util.ExceptionThrowUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
    public BaseResponse<String> registerByAccount(@Validated @RequestBody RegisterByAccountDTO registerByAccountDTO) {
        // 判断两次输入的密码是否一致
        ExceptionThrowUtils.clientExceptionThrowIf(!StringUtils.equals(registerByAccountDTO.getUserPassword(), registerByAccountDTO.getConfirmPassword()), ErrorCodeEnum.USER_ERROR_A0120);
        authService.registerByAccount(registerByAccountDTO);
        // 这里应该返回注册成功的token，前端根据token获取用户信息
        return BaseResponse.success("注册成功");
    }

    /**
     * 登录
     *
     * @return 返回登录结果
     */
    @PostMapping("/login-account")
    @Operation(description = "登录-账号密码")
    public BaseResponse<LoginVO> loginByAccount(@Validated @RequestBody LoginByAccountDTO loginByAccountDTO) {
        LoginVO loginVO = authService.loginByAccount(loginByAccountDTO);
        return BaseResponse.success(loginVO);
    }

}
