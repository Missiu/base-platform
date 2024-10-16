package com.example.template.manager.email;

import cn.hutool.core.util.ObjectUtil;
import com.example.template.common.base.CommonConstants;
import com.example.template.common.base.ErrorCode;
import com.example.template.common.constant.RegexConstants;
import com.example.template.exception.customize.ClientException;
import com.example.template.exception.customize.RemoteServiceException;
import jakarta.annotation.Resource;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 邮件配置
 *
 * @author hzh
 */
@Component
@EnableAsync
@Slf4j
public class EmailManager {

    @Value("${spring.mail.properties.mail.attachment.size.limit}")
    private Long maxAttachmentSize;

    @Value("${spring.mail.properties.mail.image.size.limit}")
    private Long maxImageSize;

    @Value("${spring.mail.properties.mail.image.types}")
    private String imageType;
    @Resource
    private MailProperties mailProperties;
    @Resource
    private EmailAsyncMethod emailAsyncMethod;
    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 创建验证码邮件内容模板
     *
     * @param code 验证码
     * @return 邮件内容
     */
    public String generateVerificationEmailTemplate(String code, int expireTime) {
        // 这里是 HTML 邮件的模板，可以根据需要自定义
        return "<html>" +
                "<body>" +
                "<h1>验证码</h1>" +
                "<p>您好，</p>" +
                "<p>您的验证码是：<strong>" + code + "</strong></p>" +
                "<p>此验证码在 " + expireTime + " 分钟内有效，请尽快使用。</p>" +
                "<br>" +
                "<p>感谢您的使用！</p>" +
                "<p>如果您没有请求此验证码，请忽略此邮件。</p>" +
                "</body>" +
                "</html>";
    }

    /**
     * 创建验证码主题
     */
    public String generateVerificationEmailSubject() {
        return "模板项目";
    }


    /**
     * 发送简单文本邮件
     *
     * @param emailDTO 邮件数据传输对象
     */
    public void sendTextMail(EmailDTO emailDTO) {
        validateEmailParams(emailDTO);
        SimpleMailMessage simpleMailMessage = createSimpleMailMessage(emailDTO);
        emailAsyncMethod.sendSimpleMail(simpleMailMessage);
    }

    /**
     * 发送 HTML 邮件
     *
     * @param emailDTO 邮件数据传输对象
     */
    public void sendHtmlMail(EmailDTO emailDTO) {
        validateEmailParams(emailDTO);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CommonConstants.UTF8);
            setCommonMailProperties(helper, emailDTO);
            helper.setText(emailDTO.getContent(), true);
            emailAsyncMethod.sendMimeMail(new MimeMailMessage(mimeMessage));
        } catch (MessagingException e) {
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0500, "发送HTML邮件失败 " + e.getMessage());
        }
    }

    /**
     * 发送带有图片的 HTML 邮件
     *
     * @param emailDTO 邮件数据传输对象
     */
    public void sendHtmlEmailWithImages(EmailDTO emailDTO) {
        validateEmailParams(emailDTO);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CommonConstants.UTF8);
            setCommonMailProperties(helper, emailDTO);
            helper.setText(emailDTO.getContent(), true);
            addInlineImages(helper, emailDTO);
            emailAsyncMethod.sendMimeMail(new MimeMailMessage(mimeMessage));
        } catch (MessagingException | IOException e) {
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0500, "发送带图片的HTML邮件失败 " + e.getMessage());
        }
    }

    /**
     * 发送带附件的邮件
     *
     * @param emailDTO 邮件数据传输对象
     */
    public void sendEmailWithAttachments(EmailDTO emailDTO) {
        validateEmailParams(emailDTO);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, CommonConstants.UTF8);
            setCommonMailProperties(helper, emailDTO);
            addAttachments(helper, emailDTO);
            emailAsyncMethod.sendMimeMail(new MimeMailMessage(mimeMessage));
        } catch (MessagingException | IOException e) {
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0500, "发送带附件的邮件失败 " + e.getMessage());
        }
    }




    /**
     * 校验邮件参数
     *
     * @param emailDTO 邮件数据传输对象
     */
    private void validateEmailParams(EmailDTO emailDTO) {
        if (ObjectUtil.isNull(emailDTO)) {
            throw new ClientException(ErrorCode.USER_ERROR_A0153, "邮件数据传输对象不能为空");
        }

        if (!isValidEmail(emailDTO.getTo())) {
            throw new ClientException(ErrorCode.USER_ERROR_A0153, "收件人邮箱地址格式不正确");
        }

        if (emailDTO.getHtmlFormat() == null) {
            throw new ClientException(ErrorCode.USER_ERROR_A0153, "邮件格式不能为空");
        }
    }

    /**
     * 校验邮件格式
     *
     * @param email 邮箱地址
     * @return true 格式正确 false 格式错误
     */
    private boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(RegexConstants.REGEX_MAIL);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    /**
     * 校验抄送人和密送人邮箱地址格式
     *
     * @param cc  抄送人邮箱地址
     * @param bcc 密送人邮箱地址
     * @return
     */
    private boolean validateCcAndBcc(String[] cc, String[] bcc) {
        if (ObjectUtils.isEmpty(cc) && ObjectUtils.isEmpty(bcc)) {
            return false;
        }
        if (ObjectUtils.isNotEmpty(cc)) {
            for (String email : cc) {
                if (Boolean.FALSE.equals(isValidEmail(email))) {
                    throw new ClientException(ErrorCode.USER_ERROR_A0153, "抄送人邮箱地址格式不正确");
                }
            }
        }
        if (ObjectUtils.isNotEmpty(bcc)) {
            for (String email : bcc) {
                if (Boolean.FALSE.equals(isValidEmail(email))) {
                    throw new ClientException(ErrorCode.USER_ERROR_A0153, "密送人邮箱地址格式不正确");
                }
            }
        }
        return true;
    }

    /**
     * 创建 简单文本邮件
     *
     * @param emailDTO 邮件数据传输对象
     * @return SimpleMailMessage 简单文本邮件
     */
    private SimpleMailMessage createSimpleMailMessage(EmailDTO emailDTO) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailProperties.getUsername());
        message.setTo(emailDTO.getTo());
        message.setSubject(emailDTO.getSubject());
        message.setText(emailDTO.getContent());
        if (emailDTO.getHtmlFormat()) {
            log.warn("sendTextMail 方法不支持 HTML 格式邮件，已经自动调整为 TEXT 格式");
        }
        if (validateCcAndBcc(emailDTO.getCc(), emailDTO.getBcc())) {
            message.setCc(emailDTO.getCc());
        }
        return message;
    }

    /**
     * 创建 复杂体邮件
     *
     * @param helper   MimeMessageHelper
     * @param emailDTO 邮件数据传输对象
     * @throws MessagingException MessagingException
     */
    private void setCommonMailProperties(MimeMessageHelper helper, EmailDTO emailDTO) throws MessagingException {
        helper.setFrom(mailProperties.getUsername());
        helper.setTo(emailDTO.getTo());
        helper.setSubject(emailDTO.getSubject());
        if (validateCcAndBcc(emailDTO.getCc(), emailDTO.getBcc())) {
            helper.setCc(emailDTO.getCc());
            helper.setBcc(emailDTO.getBcc());
        }
    }

    /**
     * 添加图片到邮件中
     *
     * @param helper   MimeMessageHelper
     * @param emailDTO 邮件数据传输对象
     * @throws MessagingException MessagingException
     * @throws IOException        IOException
     */
    private void addInlineImages(MimeMessageHelper helper, EmailDTO emailDTO) throws MessagingException, IOException {
        List<MultipartFile> attachments = emailDTO.getAttachments();
        List<String> contentIds = emailDTO.getContentIds();

        // 将逗号分隔的字符串转换为列表
        List<String> allowedFormats = Arrays.asList(imageType.split(","));

        // 检查附件和 contentId 是否为空并且数量是否一致
        if (ObjectUtil.isAllNotEmpty(attachments, contentIds) && attachments.size() == contentIds.size()) {
            for (int i = 0; i < attachments.size(); i++) {
                MultipartFile attachment = attachments.get(i);
                String contentId = contentIds.get(i);

                // 验证图片大小
                if (attachment.getSize() > maxImageSize) {
                    throw new IllegalArgumentException("Image size exceeds the maximum limit of " + maxImageSize + " bytes.");
                }

                // 验证图片格式
                String contentType = Objects.requireNonNull(attachment.getContentType(), "Content type must not be null.");
                if (!allowedFormats.contains(contentType)) {
                    throw new IllegalArgumentException("Unsupported image format: " + contentType);
                }

                // 添加图片到邮件中
                helper.addInline(contentId, new ByteArrayResource(attachment.getBytes()), contentType);
            }
        }
    }

    /**
     * 添加附件到邮件中
     *
     * @param helper   MimeMessageHelper
     * @param emailDTO 邮件数据传输对象
     * @throws MessagingException MessagingException
     * @throws IOException        IOException
     */
    private void addAttachments(MimeMessageHelper helper, EmailDTO emailDTO) throws MessagingException, IOException {
        List<MultipartFile> attachments = emailDTO.getAttachments();
        // 校验并添加客户端上传的附件
        if (ObjectUtil.isNotEmpty(attachments)) {
            for (MultipartFile attachment : attachments) {
                validateAttachmentSize(attachment.getSize(), attachment.getOriginalFilename());
                helper.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()), new ByteArrayResource(attachment.getBytes()));
            }
        }

        List<String> filePathList = emailDTO.getFilePathList();
        // 校验并添加服务器上的文件
        if (ObjectUtil.isNotEmpty(filePathList)) {
            for (String filePath : filePathList) {
                File file = new File(filePath);
                validateAttachmentSize(file.length(), file.getName());
                helper.addAttachment(file.getName(), file);
            }
        }
    }

    /**
     * 校验附件大小
     *
     * @param fileSize 文件大小（字节）
     * @param fileName 文件名
     */
    private void validateAttachmentSize(long fileSize, String fileName) {
        if (fileSize > maxAttachmentSize) {
            throw new ClientException(ErrorCode.USER_ERROR_A0153,
                    String.format("附件 %s 的大小超出限制，最大允许 %d MB", fileName, maxAttachmentSize / (1024 * 1024)));
        }
    }
}