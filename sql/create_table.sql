# 数据库初始化

-- 创建库
create database if not exists template_db;

-- 切换库
use template_db;

CREATE TABLE IF NOT EXISTS `t_user`
(
    `id`            BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    `user_account`  VARCHAR(256)        NOT NULL COMMENT '账号 建议大于4位',
    `user_password` VARCHAR(512)        NULL COMMENT '密码',
    `password_salt` VARCHAR(256)        NULL COMMENT '密码盐',
    `union_id`      CHAR(29)            NULL COMMENT '微信开放平台ID 定长29位',
    `mp_open_id`    CHAR(28)            NULL COMMENT '公众号OpenID 定长28位',
    `user_phone`    CHAR(16)            NULL COMMENT '手机号 定长16位',
    `user_email`    VARCHAR(256)        NULL COMMENT '邮箱',
    `user_name`     VARCHAR(256)        NULL COMMENT '用户昵称',
    `user_avatar`   VARCHAR(512)       NULL COMMENT '用户头像URL',
    `user_profile`  VARCHAR(512)        NULL COMMENT '用户简介',
    `user_role`     VARCHAR(256)        NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `gmt_create`    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
    `gmt_modified`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
    `is_deleted`    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除：1表示是，0表示否',
    UNIQUE KEY `uk_user_account` (`user_account`(20)), -- 取前20位作为唯一索引
    UNIQUE KEY `uk_user_email` (`user_email`(20)),
    UNIQUE KEY `idx_union_id` (`union_id`), -- 创建完整字段的索引
    UNIQUE KEY `idx_mp_open_id` (`mp_open_id`),
    UNIQUE KEY `idx_user_phone` (`user_phone`)
) COMMENT ='用户表' COLLATE = utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS `t_file`
(
    `id`                 BIGINT UNSIGNED AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    `unique_key`         VARCHAR(256)        NOT NULL COMMENT '文件唯一摘要值,存储时截取前25位作为唯一索引',
    `file_name`          VARCHAR(256)        NOT NULL COMMENT '文件存储名称',
    `file_original_name` VARCHAR(256)        NOT NULL COMMENT '文件原名称',
    `file_suffix`        VARCHAR(256)        NOT NULL COMMENT '文件扩展名',
    `file_size`          BIGINT UNSIGNED     NOT NULL COMMENT '文件大小',
    `file_url`           VARCHAR(512)        NOT NULL COMMENT '文件地址',
    `file_storage_type`  VARCHAR(256)        NOT NULL COMMENT '文件存储类型',
    `gmt_create`         DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
    `gmt_modified`       DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
    `is_deleted`         TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除：1表示是，0表示否',
    UNIQUE KEY `uk_unique_key` (`unique_key`(25)) -- 取前25位作为唯一索引
) COMMENT ='文件表' COLLATE = utf8mb4_unicode_ci;