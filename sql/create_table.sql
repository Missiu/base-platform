# 数据库初始化

-- 创建库
create database if not exists template_db;

-- 切换库
use template_db;

CREATE TABLE IF NOT EXISTS `user`
(
    `id`            BIGINT UNSIGNED AUTO_INCREMENT COMMENT '用户ID' PRIMARY KEY,
    `user_account`  VARCHAR(256)        NOT NULL COMMENT '账号 建议大于4位',
    `user_password` VARCHAR(512)        NOT NULL COMMENT '密码',
    `password_salt` VARCHAR(256)        NOT NULL COMMENT '密码盐',
    `union_id`      CHAR(29)            NULL COMMENT '微信开放平台ID 定长29位',
    `mp_open_id`    CHAR(28)            NULL COMMENT '公众号OpenID 定长28位',
    `user_phone`    CHAR(16)            NULL COMMENT '手机号 定长16位',
    `user_email`    VARCHAR(256)        NULL COMMENT '邮箱',
    `user_name`     VARCHAR(256)        NULL COMMENT '用户昵称',
    `user_avatar`   VARCHAR(1024)       NULL COMMENT '用户头像URL',
    `user_profile`  VARCHAR(512)        NULL COMMENT '用户简介',
    `user_role`     VARCHAR(256)        NOT NULL DEFAULT 'user' COMMENT '用户角色：user/admin/ban',
    `gmt_create`    DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '数据创建时间',
    `gmt_modified`  DATETIME            NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '数据最后修改时间',
    `is_deleted`    TINYINT(1) UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已删除：1表示是，0表示否',
#     UNIQUE KEY `uk_user_account` (`user_account`(5)),
#     UNIQUE KEY `uk_user_email` (`user_email`(4)),
#     UNIQUE KEY `idx_union_id` (`union_id`), -- 创建完整字段的索引
#     UNIQUE KEY `idx_mp_open_id` (`mp_open_id`),
#     UNIQUE KEY `idx_user_phone` (`user_phone`)
) COMMENT ='用户表' COLLATE = utf8mb4_unicode_ci;

