package com.example.template.module.domain.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.template.module.domain.groups.auth.Auth;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * @author hzh
 * @data 2024/10/13 22:44
 */

@Data
public class UserAuthDTO implements Serializable {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空", groups = {
            Auth.RegisterByPassword.class,
            Auth.LoginByPassword.class})
    @Size(min = 5, max = 20, message = "账号长度必须在5-20之间")
    @Schema(description = "账号")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空", groups = {
            Auth.RegisterByPassword.class,
            Auth.LoginByPassword.class})
    @Size(min = 6, max = 126, message = "密码长度必须在6-126之间")
    @Schema(description = "密码")
    private String userPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空", groups = {Auth.RegisterByPassword.class})
    @Size(min = 6, max = 126, message = "确认密码长度必须在6-126之间")
    @Schema(description = "确认密码")
    private String confirmPassword;

    /**
     * 邮箱
     */
    @NotBlank(message = "邮箱不能为空", groups = {Auth.AuthByEmail.class})
    @Schema(description = "邮箱")
    private String userEmail;

    /**
     * 手机号
     */
    @NotBlank(message = "手机号不能为空", groups = {Auth.AuthByPhone.class})
    @Schema(description = "手机号")
    private String userPhone;

    /**
     * 验证码
     */
    @Schema(description = "验证码")
    @NotBlank(message = "验证码不能为空", groups = {
            Auth.AuthByEmail.class,
            Auth.AuthByPhone.class})
    private String verifyCode;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
