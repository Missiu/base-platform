package com.example.template.module.domain.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 用户表
 * @author hzh
 */
@Data
public class LoginByAccountDTO implements Serializable {

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

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}