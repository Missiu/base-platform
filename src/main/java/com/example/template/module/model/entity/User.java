package com.example.template.module.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 用户表
 * @TableName user
 */
@TableName(value = "user")
@Data
public class User implements Serializable {
    /**
     * 用户ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String userPassword;

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
    private String phone;

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
    private Object userRole;

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
    private Integer deleted;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userAccount='" + userAccount + '\'' +
                ", userPassword='" + userPassword + '\'' +
                ", unionId='" + unionId + '\'' +
                ", mpOpenId='" + mpOpenId + '\'' +
                ", phone='" + phone + '\'' +
                ", userName='" + userName + '\'' +
                ", userAvatar='" + userAvatar + '\'' +
                ", userProfile='" + userProfile + '\'' +
                ", userRole=" + userRole +
                ", gmtCreate=" + gmtCreate +
                ", gmtModified=" + gmtModified +
                ", deleted=" + deleted +
                '}';
    }
}