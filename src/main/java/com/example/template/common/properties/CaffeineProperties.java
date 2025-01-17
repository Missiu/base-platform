package com.example.template.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Caffeine配置属性
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@Data
@Component
@ConfigurationProperties(prefix = "caffeine")
public class CaffeineProperties {

    /**
     * 最后一次写入或访问后经过固定时间过期，单位：秒
     */
    private Long expired = 1800L;

    /**
     * 缓存初始容量
     */
    private Integer initCapacity = 256;

    /**
     * 缓存最大容量，超过之后会按照最近最少策略进行缓存剔除
     */
    private Integer maxCapacity = 10000;

    /**
     * 是否允许空值null作为缓存的value
     */
    private Boolean allowNullValue = true;

}