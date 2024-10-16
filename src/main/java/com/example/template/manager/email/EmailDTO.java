package com.example.template.manager.email;

import com.baomidou.mybatisplus.annotation.TableField;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 *
 * @author hzh
 * @data 2024/10/14 10:46
 */
@Data
public class EmailDTO implements Serializable {
    /**
     * 收件人邮箱地址
     */
    @NotBlank(message = "收件人邮箱地址不能为空")
    private String to;

    /**
     * 邮件主题
     */
    @NotBlank(message = "邮件主题不能为空")
    private String subject;

    /**
     * 邮件内容
     */
    @NotBlank(message = "邮件内容不能为空")
    private String content;

    /**
     * 图片的 Content-ID 列表，用于嵌入 HTML 邮件中的图片，与 filePathList 或 attachments 对应
     */
    private List<String> contentIds;

    /**
     * 服务器上文件或图片的路径列表
     */
    private List<String> filePathList;

    /**
     * 客户端上传的附件或图片文件列表
     */
    private List<MultipartFile> attachments;

    /**
     * 是否使用 HTML 格式 ,false 表示纯文本格式
     */
    @NotNull(message = "邮件格式不能为空")
    private Boolean htmlFormat;
    /**
     * 抄送
     */
    private String[] cc;
    /**
     * 密送
     */
    private String[] bcc;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
