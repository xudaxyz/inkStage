package com.inkstage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 异步任务配置
 * <p>
 * 配置异步任务执行器，用于处理通知等异步操作
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    /**
     * 通知任务执行器
     * <p>
     * 用于处理通知相关的异步任务
     */
    @Bean("notificationTaskExecutor")
    public Executor notificationTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        // 核心线程数
        executor.setCorePoolSize(5);
        // 最大线程数
        executor.setMaxPoolSize(20);
        // 队列容量
        executor.setQueueCapacity(1000);
        // 线程名称前缀
        executor.setThreadNamePrefix("notification-");
        // 拒绝策略：由调用线程处理
        executor.setRejectedExecutionHandler(new java.util.concurrent.ThreadPoolExecutor.CallerRunsPolicy());
        // 初始化
        executor.initialize();
        return executor;
    }
}
