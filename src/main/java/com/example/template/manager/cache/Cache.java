package com.example.template.manager.cache;

import cn.hutool.core.util.ObjectUtil;
import com.example.template.common.constant.CacheConstants;
import com.example.template.manager.rabbitmq.RabbitMqServer;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * CacheManager 用于管理不同缓存策略的实现
 *
 * @author hzh
 */
@Component
@AllArgsConstructor
public class Cache {
    // 本地缓存
    private final com.github.benmanes.caffeine.cache.Cache<String, Object> localCache;
    // Redis 缓存模板
    private final RedisTemplate<Object, Object> redisTemplate;
    // Redisson 客户端
    private final RedissonClient redissonClient;

    // 使用 RabbitMQ 发送消息的生产者
    private final RabbitMqServer rabbitMqServer;

    // 自动加载本地缓存锁名称常量
    private static final String AUTO_CACHE_LOCK = "autoCacheLock";

    // 二级缓存锁常量
    private static final String TWO_LEVEL_CACHE_LOCK = "twoLevelCacheLock";

    /**
     * 手动加载本地缓存
     *
     * @param key   缓存的键
     * @param value 缓存的值
     */
    public void setLocalCache(String key, Object value) {
        localCache.put(key, value);
    }

    /**
     * 自动加载本地缓存
     *
     * @param key        缓存的键
     * @param dataLoader 数据加载器，负责从数据源加载数据
     * @return 缓存的值
     */
    public Object getAutoLoadLocalCache(String key, DataLoader dataLoader) {
        // 先尝试从本地缓存中获取数据
        Object value = localCache.getIfPresent(key);

        if (ObjectUtil.isNull(value)) {
            // 使用分布式锁防止缓存击穿
            RLock lock = redissonClient.getLock(AUTO_CACHE_LOCK);
            lock.lock();
            try {
                // 再次检查缓存
                value = localCache.getIfPresent(key);
                if (ObjectUtil.isNull(value)) {
                    // 加载数据
                    value = dataLoader.load();

                    // 如果加载的数据为 null，则直接返回 null，不进行缓存
                    if (ObjectUtil.isNotNull(value)) {
                        // 正常数据放入本地缓存
                        localCache.put(key, value);

                        // 发送消息到 RabbitMQ，通知其他服务
                        // rabbitMqServer.sendCacheUpdateMessage(key, value);
                    }
                }
            } finally {
                // 释放锁
                lock.unlock();
            }
        }

        return value;
    }

    /**
     * 从本地缓存中获取数据
     *
     * @param key 缓存的键
     * @return 缓存的值
     */
    public Object getLocalCache(String key) {
        return localCache.getIfPresent(key);
    }

    /**
     * 旁路缓存模式 - Cache-Aside Pattern
     *
     * @param key        缓存的键
     * @param dataLoader 数据加载器，负责从数据源加载数据
     * @param ttl        缓存的过期时间（秒）
     * @return 缓存的值
     */
    public Object getRedisCache(String key, DataLoader dataLoader, long ttl) {
        // 尝试从Redis缓存中获取数据
        Object value = redisTemplate.opsForValue().get(key);

        if (ObjectUtil.isNull(value)) {
            // 如果缓存未命中，从数据源加载数据
            value = dataLoader.load();

            // 数据加载成功后，缓存到Redis
            if (ObjectUtil.isNotNull(value)) {
                redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);

                // 发送缓存更新消息到RabbitMQ，通知其他服务
                // rabbitMqServer.sendCacheUpdateMessage(key, value);
            }
        }

        return value;
    }

    /**
     * 获取两级缓存中的数据（本地缓存优先）。
     *
     * @param key 缓存的键
     * @param ttl 过期时间
     * @return 缓存的值
     */
    public Object getTwoLevelCache(String key, DataLoader dataLoader, long ttl) {
        // 1. 尝试从本地缓存获取
        Object value = localCache.getIfPresent(key);

        // 2. 如果本地缓存未命中，尝试从 Redis 获取
        if (ObjectUtil.isNull(value)) {
            value = redisTemplate.opsForValue().get(key);

            // 3. 如果 Redis 缓存未命中，从数据库加载，并使用分布式锁防止缓存击穿
            if (ObjectUtil.isNull(value)) {
                RLock lock = redissonClient.getLock(TWO_LEVEL_CACHE_LOCK + key);
                try {
                    // 尝试获取锁，避免多个线程同时加载数据
                    if (lock.tryLock(10, 5, TimeUnit.SECONDS)) {
                        // 再次检查 Redis，防止并发情况下重复加载
                        value = redisTemplate.opsForValue().get(key);
                        if (ObjectUtil.isNull(value)) {
                            // 从数据库加载数据
                            value = dataLoader.load();
                            if (ObjectUtil.isNotNull(value)) {
                                // 写入 Redis 和本地缓存
                                redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.SECONDS);
                                localCache.put(key, value);
                            }
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlock();
                }
            } else {
                // 将 Redis 的数据同步到本地缓存
                localCache.put(key, value);
            }
        }

        return value;
    }

    /**
     * 在应用启动时预加载热点数据到本地缓存
     */
    @PostConstruct
    public void preloadHotData() {
        for (String key : CacheConstants.HOT_KEYS) {
            // 尝试从 Redis 获取热点数据
            Object value = redisTemplate.opsForValue().get(key);

            // 如果Redis中有对应的值，则预加载到本地缓存
            if (ObjectUtil.isNotNull(value)) {
                localCache.put(key, value);
            }
        }
    }

    /**
     * 数据加载器接口
     * 允许使用不同的数据源进行数据加载
     */
    @FunctionalInterface
    public interface DataLoader {
        Object load();
    }
}
