package com.sky.task;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author Cheng Yihao
 * @version 1.0
 * @date 2024/9/26 20:01
 * @comment
 */
@Slf4j
public class MyTask {
    @Scheduled(cron = "0/5 * * * * ?")
    public void executeTask() {
        log.info("定时任务执行: {}", new Date());
    }
}
