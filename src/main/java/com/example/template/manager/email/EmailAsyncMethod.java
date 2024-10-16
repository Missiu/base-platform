package com.example.template.manager.email;

import com.example.template.common.base.ErrorCode;
import com.example.template.exception.customize.RemoteServiceException;
import jakarta.annotation.Resource;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 邮件异步发送方法
 *
 * @author hzh
 */
@Component
@Async
@Slf4j
public class EmailAsyncMethod {

    @Resource
    private JavaMailSender javaMailSender;

    /**
     * 发送简单文本邮件
     *
     * @param simpleMailMessage 简单文本邮件封装实体
     */
    public void sendSimpleMail(SimpleMailMessage simpleMailMessage) {
        try {
            javaMailSender.send(simpleMailMessage);
            log.info("The message was successfully sent from {} to {}", simpleMailMessage.getFrom(), Arrays.toString(simpleMailMessage.getTo()));
        } catch (Exception e) {
            log.error("An error occurred in the message sent from {} to {} ==> {}", simpleMailMessage.getFrom(), Arrays.toString(simpleMailMessage.getTo()), e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0500, "Failed to send simple mail " + e.getMessage());
        }
    }

    /**
     * 发送复杂附件邮件
     *
     * @param mimeMailMessage 复杂附件邮件封装实体
     */
    public void sendMimeMail(MimeMailMessage mimeMailMessage) {
        String from = "";
        String to = "";
        try {
            MimeMessage mimeMessage = mimeMailMessage.getMimeMessage();
            from = mimeMessage.getFrom()[0].toString();
            to = Arrays.toString(mimeMessage.getRecipients(MimeMessage.RecipientType.TO));
            javaMailSender.send(mimeMessage);
            log.info("The message was successfully sent from {} to {}", from, to);
        } catch (Exception e) {
            log.error("An error occurred in the message sent from {} to {} ==> {}", from, to, e.getMessage());
            throw new RemoteServiceException(ErrorCode.SERVICE_ERROR_C0500, "Failed to send simple mail " + e.getMessage());
        }
    }

}