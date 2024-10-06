package com.example.template.common.propertie;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 服务配置部分属性
 *
 * @author hzh
 * @data 2024/10/5 22:24
 */
@Data
public class ServerProperties {
    @Value(value = "${server.domain}")
    private String domain;
}
