package com.example.template.module.domain.dto.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import com.example.template.module.domain.groups.auth.LoginByAccount;
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
public class UserLoginDTO implements Serializable {
    /**
     * 账号
     */
    @NotBlank(message = "账号不能为空",groups = {LoginByAccount.class})
    @Size(min = 5, max = 25, message = "账号长度必须在5-25之间", groups = {LoginByAccount.class})
    @Schema(description = "账号",example = "admin")
    private String userAccount;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空",groups = {LoginByAccount.class})
    @Size(min = 6, max = 126, message = "确认密码长度必须在6-126之间", groups = {LoginByAccount.class})
    @Schema(description = "密码",example = "Admin1234.")
    private String userPassword;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
