package com.example.template.config.caffeine;

import com.example.template.common.properties.CaffeineProperties;
import com.example.template.config.caffeine.condition.CaffeineCondition;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.*;

import java.util.concurrent.TimeUnit;

/**
 * Caffeine配置
 * 只有当 CaffeineCondition 中定义的条件返回 true 时，Spring 才会激活该类。
 * 从CaffeineProperties中读取配置，并创建Cache实例。
 *
 * @author hzh
 * @data 2024/10/5 11:40
 */
@Configuration
@AllArgsConstructor
@Slf4j
@Conditional(CaffeineCondition.class)
@EnableConfigurationProperties(CaffeineProperties.class)
public class CaffeineConfiguration {

    private final CaffeineProperties caffeineProperties;

    /**
     * 创建本地缓存实例
     *
     * @return 本地缓存实例
     */
    @Bean
    public Cache<String, Object> localCache() {
        return Caffeine.newBuilder()
                // 设置缓存过期时间
                .expireAfterWrite(caffeineProperties.getExpired(), TimeUnit.SECONDS)
                // 设置缓存的访问时间
                .expireAfterAccess(caffeineProperties.getExpired(), TimeUnit.SECONDS)
                // 初始的缓存空间大小
                .initialCapacity(caffeineProperties.getInitCapacity())
                // 缓存最大容量，超过之后会按照最近最少策略进行缓存剔除
                .maximumSize(caffeineProperties.getMaxCapacity())
                .build();
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initConfig() {
        log.info("=====> {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}