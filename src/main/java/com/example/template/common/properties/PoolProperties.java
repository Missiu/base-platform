package com.example.template.common.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * PoolProperties 类用于配置和管理线程池相关属性
 * 通过注解 @Data、@Component 和 @ConfigurationProperties 映射并管理配置属性
 *
 * @author hzh
 * @data 2024/10/16 14:16
 */
@Data
@Component
@ConfigurationProperties(prefix = "spring.pool")
public class PoolProperties {

    /**
     * 定义用于调度任务的线程池配置
     */

    private ScheduledPool scheduled;
    /**
     * 定义用于异步任务的线程池配置
     */
    private AsyncPool async;

    /**
     * 用于配置自定义threadPool线程池的属性
     */
    private CustomPool custom;


    /**
     * ScheduledPool 类用于配置调度任务线程池的属性
     */
    @Data
    public static class ScheduledPool {
        /**
         * 核心线程数，用于配置调度任务线程池的核心线程数量
         */
        private int coreSize;
    }

    /**
     * AsyncPool 类用于配置异步任务线程池的属性
     */
    @Data
    public static class AsyncPool {
        /**
         * 核心线程数，用于配置异步任务线程池的核心线程数量
         */
        private int coreSize;
        /**
         * 最大线程数，用于配置异步任务线程池的最大线程数量
         */
        private int maxSize;
        /**
         * 队列容量，用于配置异步任务线程池的工作队列大小
         */
        private int queueCapacity;
        /**
         * 线程名称前缀，用于配置异步任务线程池中线程的名称前缀
         */
        private String threadNamePrefix;
    }

    /**
     * BasicPool 类用于配置基本线程池的属性
     */
    @Data
    public static class CustomPool {
        /**
         * 核心线程数，用于配置基本线程池的核心线程数量
         */
        private int coreSize;
        /**
         * 最大线程数，用于配置基本线程池的最大线程数量
         */
        private int maxSize;
        /**
         * 队列容量，用于配置基本线程池的工作队列大小
         */
        private int queueCapacity;
        /**
         * 线程名称前缀，用于配置基本线程池中线程的名称前缀
         */
        private String threadNamePrefix;
        /**
         * 线程存活时间，用于配置基本线程池中线程的存活时间
         */
        private long keepAliveTime;
    }
}
