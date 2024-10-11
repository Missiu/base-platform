package com.example.template.module.domain.vo.auth;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户表
 *
 * @author hzh
 */
@Data
public class LoginVO implements Serializable {

    /**
     * 用户ID
     */
    private Long id;
    /**
     * 账号
     */
    private String userAccount;

    /**
     * token
     */
    private String token;

    /**
     * 手机号 定长16位
     */
    private String userPhone;

    /**
     * 邮箱
     */
    private String userEmail;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像URL
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin/ban
     */
    private String userRole;

    /**
     * 数据创建时间
     */
    private Date gmtCreate;


    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}