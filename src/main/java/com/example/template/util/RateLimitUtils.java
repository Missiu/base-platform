package com.example.template.util;


import com.example.template.common.base.ErrorCode;
import com.example.template.common.properties.RedissonProperties;
import com.example.template.config.redisson.RedissonConfig;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import org.redisson.api.RRateLimiter;
import org.redisson.api.RateIntervalUnit;
import org.redisson.api.RateType;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;


/**
 * 限流工具类
 *
 * @author AntonyCheng
 */
@Component
public class RateLimitUtils {
    @Resource
    private RedissonConfig redissonConfig;

    @Resource
    private RedissonProperties redissonProperties;

    // 使用Map缓存RRateLimiter实例
    private final Map<String, RRateLimiter> rateLimiterCache = new HashMap<>();

    /**
     * 限流
     *
     * @param key 限流key
     */
    public void doRateLimit(String key, boolean isVip) {
        RRateLimiter rateLimiter = getRateLimiter(key);

        boolean acquire;
        if (isVip) {
            acquire = rateLimiter.tryAcquire(1); // VIP用户每次操作消耗1个令牌，每分钟20次操作
        } else {
            acquire = rateLimiter.tryAcquire(2); // 普通用户每次操作消耗2个令牌
        }

        ThrowUtils.clientExceptionThrowIfNot(acquire, ErrorCode.USER_ERROR_A0501);
    }

    /**
     * 获取或创建一个限流器，key可以是用户ID
     */
    private RRateLimiter getRateLimiter(String key) {
        synchronized (rateLimiterCache) {
            return rateLimiterCache.computeIfAbsent(key, k -> {
                RRateLimiter newRateLimiter = redissonConfig.singleClient().getRateLimiter(k);
                newRateLimiter.trySetRate(RateType.PER_CLIENT, 1,2, RateIntervalUnit.MINUTES);
                return newRateLimiter;
            });
        }
    }

    @PreDestroy
    public void cleanUp() {
        rateLimiterCache.clear();
    }
}
