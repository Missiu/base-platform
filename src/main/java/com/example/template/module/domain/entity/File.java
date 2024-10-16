package com.example.template.module.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;

/**
 * 文件表
 * @TableName t_file
 */
@TableName(value ="t_file")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class File implements Serializable {
    /**
     * 文件ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文件唯一摘要值,存储时截取前25位作为唯一索引
     */
    private String uniqueKey;

    /**
     * 文件存储名称
     */
    private String fileName;

    /**
     * 文件原名称
     */
    private String fileOriginalName;

    /**
     * 文件扩展名
     */
    private String fileSuffix;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件地址
     */
    private String fileUrl;

    /**
     * 文件存储类型
     */
    private String fileStorageType;

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
    private Integer isDeleted;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}