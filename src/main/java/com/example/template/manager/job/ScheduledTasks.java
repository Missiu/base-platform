package com.example.template.manager.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;


/**
 * 定时任务
 *
 * @author hzh
 */
@Slf4j
//@Service
public class ScheduledTasks {

    @Scheduled(fixedRate = 5000) // 每5秒执行一次
    @Async("asyncTaskExecutor")
    public void reportCurrentTime() {
        log.info("当前时间：{}", System.currentTimeMillis());
    }

    @Scheduled(cron = "0 0/1 * * * ?") // 每分钟开始时执行
    @Async("asyncTaskExecutor")
    public void scheduledTaskUsingCron() {
        log.info("每分钟的任务被执行");
    }
}