package com.example.template.config.redis;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
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
 * @author AntonyCheng
 */
@Configuration
@Slf4j
public class RedisConfiguration implements CachingConfigurer {

    @Resource
    private RedisConnectionFactory redisConnectionFactory;

    @Bean
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> redisTemplate = new RedisTemplate<>();
        // 没有改变序列化之前的默认的序列化规则是JdkSerializationRedisSerializer();
        // 这里使用 Jackson2 工具进行序列化
        // 设置Key的序列化方式为String
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        // 设置Value的序列化方式为GenericJackson2JsonRedisSerializer
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory);
        return redisTemplate;
    }

    /**
     * 依赖注入日志输出
     */
    @PostConstruct
    private void initConfig() {
        log.info("=====> {} Configuration DI.", this.getClass().getSimpleName().split("\\$\\$")[0]);
    }

}