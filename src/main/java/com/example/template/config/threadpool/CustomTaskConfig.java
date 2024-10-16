package com.example.template.config.threadpool;

import com.example.template.common.properties.PoolProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池配置类
 * 提供自定义的 ThreadPoolExecutor 实例
 *
 * @author hzh
 */
@Configuration
@Slf4j
@AllArgsConstructor
public class CustomTaskConfig {

    private final PoolProperties poolProperties;

    /**
     * 创建线程池执行器
     *
     * @return ThreadPoolExecutor 实例
     */
    @Bean(name = "customThreadPoolExecutor")
    public ThreadPoolExecutor customThreadPoolExecutor() {
        ThreadFactory threadFactory = new CustomThreadFactory();

        return new ThreadPoolExecutor(
                poolProperties.getCustom().getCoreSize(),
                poolProperties.getCustom().getMaxSize(),
                poolProperties.getCustom().getKeepAliveTime(),
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(poolProperties.getCustom().getQueueCapacity()),
                threadFactory
        );
    }

    /**
     * 自定义线程工厂类
     */
    private class CustomThreadFactory implements ThreadFactory {
        private int threadCount = 1; // 线程计数器，用于为线程命名

        @Override
        public Thread newThread(@NonNull Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(poolProperties.getCustom().getThreadNamePrefix() + threadCount++); // 设置线程名称
            log.info("===> 新线程被创建：{}", thread.getName());
            return thread;
        }
    }

    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
