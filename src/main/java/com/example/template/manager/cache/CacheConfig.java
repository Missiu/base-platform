package com.example.template.manager.cache;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * CacheConfig 是自定义的缓存注解，用于配置不同的缓存策略、过期时间等信息。
 * <p>
 * 使用该注解时，可以指定缓存策略、缓存过期时间、随机失效时间以及缓存的key和结构。
 *
 * @author hzh
 */
@Target({ElementType.METHOD})  // 该注解可用于方法上
@Retention(RetentionPolicy.RUNTIME)  // 在运行时通过反射读取该注解
public @interface CacheConfig {

    /**
     * 缓存策略类型。
     * 需要指定枚举类型 CacheSolutionType，来确定采用何种缓存策略（如：本地缓存、Redis缓存等）。
     */
    CacheSolutionType strategy() default CacheSolutionType.REDIS_CACHE_ASIDE;

    /**
     * 缓存过期时间。
     * 默认为 60 秒，可通过该参数指定缓存的存活时间。
     *
     * @return 缓存的存活时间，单位是时间单位参数中指定的单位（默认为秒）。
     */
    long expireTime() default 60;

    /**
     * 过期时间的时间单位。
     * 默认为秒，可指定其他时间单位（如分钟、小时等）。
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * 是否启用随机失效时间，避免缓存雪崩。
     * 启用后，实际过期时间会在指定的失效时间基础上添加一个随机值。
     *
     * @return 是否启用随机失效时间。
     */
    boolean enableRandomExpire() default false;

    /**
     * 随机失效时间的最大范围（秒）。
     * 如果启用了随机失效时间，系统会在指定的失效时间基础上，随机增加 0 到该范围的秒数。
     * 默认为 0，表示不添加随机失效时间。
     *
     * @return 随机失效时间的最大范围，单位是秒。
     */
    int randomExpireMaxRange() default 0;

    /**
     * 缓存的 key。
     * 可以通过 SpEL 表达式指定缓存的 key，例如 "#userId" 表示以 userId 作为缓存键。
     *
     * @return 缓存的 key 表达式。
     */
    String key() default "";

    /**
     * 缓存结构。
     * 使用 CacheStructure 常量来指定缓存的结构类型（如：STRING、LIST、SET等）。
     *
     * @return 缓存结构的描述。
     */
    String structure() default CacheStructure.STRING;
}
