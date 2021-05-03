package com.self.library.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @Author Administrator
 * @Title: 线程池配置
 * @Description:
 * @Date 2021-04-17 18:31
 * @Version: 1.0
 */
@Configuration
public class ThreadPoolTaskExecutorConfig
{
    @Bean
    public ThreadPoolTaskExecutor executor()
    {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setMaxPoolSize(20);
        executor.setKeepAliveSeconds(3600);
        executor.setQueueCapacity(50);
        executor.setThreadFactory(Executors.defaultThreadFactory());
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        return executor;
    }
}
