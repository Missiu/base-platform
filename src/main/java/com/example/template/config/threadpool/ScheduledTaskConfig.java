package com.example.template.config.threadpool;

import com.example.template.common.properties.PoolProperties;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 定时任务线程池配置类
 * 主要用于处理 @Scheduled 注解标注的定时任务
 *
 * @author hzh
 */
@Configuration
@AllArgsConstructor
@Slf4j
@EnableConfigurationProperties(PoolProperties.class)
public class ScheduledTaskConfig implements SchedulingConfigurer {

    private final PoolProperties poolProperties;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        // 创建一个 ScheduledExecutorService，使用 coreSize 作为线程池大小
        ScheduledExecutorService scheduledExecutor = Executors.newScheduledThreadPool(poolProperties.getScheduled().getCoreSize());
        taskRegistrar.setScheduler(scheduledExecutor); // 设置定时任务调度器
    }

    @PostConstruct
    private void initConfig() {
        log.info("===================== {} 注入完成 =====================",
                this.getClass().getSimpleName().split("\\$\\$")[0]);
    }
}
