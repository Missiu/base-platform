package com.example.template.config.threadpool;

import com.example.template.common.properties.PoolProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步任务线程池配置类
 * 主要用于处理 @Async 注解标注的异步任务
 *
 * @author hzh
 */
@Configuration
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(PoolProperties.class)
public class AsyncTaskConfig implements AsyncConfigurer {

    private final PoolProperties poolProperties;

    /**
     * 配置异步任务执行器
     * 用于处理 @Async 注解标注的异步任务
     *
     * @return 异步任务执行器
     */
    @Override
    @Bean
    public Executor getAsyncExecutor() {
        // 创建并配置一个线程池任务执行器
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        executor.setCorePoolSize(poolProperties.getAsync().getCoreSize());
        // 设置最大线程数
        executor.setMaxPoolSize(poolProperties.getAsync().getMaxSize());
        // 设置队列容量
        executor.setQueueCapacity(poolProperties.getAsync().getQueueCapacity());
        // 设置线程名称前缀
        executor.setThreadNamePrefix(poolProperties.getAsync().getThreadNamePrefix());
        // 设置拒绝策略：当线程池无法处理更多任务时，调用者线程执行任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化线程池任务执行器
        executor.initialize();
        return executor;
    }

    /**
     * 配置异步任务的未捕获异常处理器
     * 用于处理异步任务执行过程中未捕获的异常
     * 记录异常日志，包括方法名、参数和异常信息
     *
     * @return 异步任务的未捕获异常处理器
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            // 更加详细的日志输出，包括异常类型、方法名、参数和异常信息
            log.error("==> 异步任务执行异常：", ex);
            log.error("==> 异常方法：{}，参数：{}，异常信息：{}", method.getName(), params, ex.getMessage());
        };
    }

    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
