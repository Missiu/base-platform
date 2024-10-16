package com.example.template.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 短信配置类
 * 用于配置短信服务的相关属性
 *
 * @author hzh
 * @data 2024/10/14 9:17
 */
@Data
@Component
@ConfigurationProperties(prefix = "sms")
public class SMSProperties {
    /**
     * Access Key ID，用于访问短信服务的唯一标识
     */
    private String accessKeyId;

    /**
     * Access Key Secret，用于访问短信服务的密钥
     */
    private String accessKeySecret;

    /**
     * 地域ID，用于指定短信服务的地域
     */
    private String regionId;

    /**
     * 终端节点，用于访问短信服务的地址
     */
    private String endpoint;

    /**
     * 短信签名名称，发送短信时使用的签名
     */
    private String signName;

    /**
     * 短信模板代码，发送短信时使用的模板
     */
    private String templateCode;
}

