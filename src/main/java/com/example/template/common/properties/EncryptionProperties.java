package com.example.template.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 加密配置类
 *
 * @author hzh
 * @data 2024/10/7 17:04
 */

@Data
@Component
@ConfigurationProperties(prefix = "encrypt")
public class EncryptionProperties {
    /**
     * 哈希算法的名称，用于确定使用哪种哈希算法进行数据加密
     */
    private String algorithm;

    /**
     * 加盐长度，用于在哈希过程中增加随机数据的长度，提高安全性
     */
    private Integer saltLength;

    /**
     * 哈希迭代次数，表示哈希操作需要执行的次数，用于增加破解难度
     */
    private Integer hashIterations;

    /**
     * 密钥长度，用于确定加密密钥的长度
     */
    private Integer keySize;
}
