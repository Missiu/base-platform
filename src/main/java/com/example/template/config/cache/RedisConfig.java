package com.example.template.config.cache;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CachingConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis配置类
 *
 * @author hzh
 */
@Configuration
@Slf4j
public class RedisConfig implements CachingConfigurer {

    /**
     * 配置 RedisTemplate，设置序列化方式
     *
     * @return 配置好的 RedisTemplate 实例
     */
    @Bean
    public RedisTemplate<Object, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        // 创建 RedisTemplate 实例
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 设置键和设置 Hash 键的序列化器，使用 String 类型
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置值和 Hash 值的序列化器，使用 JSON 类型，支持复杂对象
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());

        // 设置 Redis 连接工厂，用于建立 Redis 连接
        redisTemplate.setConnectionFactory(redisConnectionFactory);

        return redisTemplate;
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
