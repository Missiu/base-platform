package com.example.template.module.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 用户表
 * @author hzh
 * @TableName user
 */
@TableName(value = "user")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class User implements Serializable {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 账号 建议大于4位
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

    /**
     * 密码盐
     */
    private String passwordSalt;

    /**
     * 微信开放平台ID 定长29位
     */
    private String unionId;

    /**
     * 公众号OpenID 定长28位
     */
    private String mpOpenId;

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

    /**
     * 数据最后修改时间
     */
    private Date gmtModified;

    /**
     * 是否已删除：1表示是，0表示否
     */
    @TableField(value = "is_deleted")
    private Integer deleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}