package com.example.template.module.domain.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 账号密码注册DTO
 * @author hzh
 */
@Data
public class RegisterByAccountDTO implements Serializable {

    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String userPassword;

    /**
     * 确认密码
     */
    @NotBlank(message = "确认密码不能为空")
    private String confirmPassword;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}