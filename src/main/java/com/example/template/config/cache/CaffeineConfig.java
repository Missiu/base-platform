package com.example.template.config.cache;

import com.example.template.common.properties.CaffeineProperties;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine配置，提供本地缓存
 *
 * @author hzh
 */
@Configuration
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(CaffeineProperties.class)
public class CaffeineConfig {

    private final CaffeineProperties caffeineProperties;

    /**
     * 创建 Caffeine 本地缓存实例
     */
    @Bean(name = "localCache")
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
                // 设置缓存过期时间
                .expireAfterWrite(caffeineProperties.getExpired(), TimeUnit.SECONDS)
                // 设置初始容量
                .initialCapacity(caffeineProperties.getInitCapacity())
                // 设置最大容量
                .maximumSize(caffeineProperties.getMaxCapacity())
                .build();
    }

    /**
     * 配置完成后的日志输出
     */
    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
