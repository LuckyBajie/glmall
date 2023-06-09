package com.glmall.common.configuration;

import com.glmall.common.configuration.properties.ThreadPoolConfigProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@EnableConfigurationProperties(ThreadPoolConfigProperties.class)
@Configuration
public class MyThreadPoolConfig {

    @Bean("commonThreadPoolExecutor")
    @ConditionalOnProperty(prefix = "config", name = "threadPool", havingValue = "load")
    public ThreadPoolExecutor threadPoolExecutor(ThreadPoolConfigProperties threadPoolConfigProperties){
        return new ThreadPoolExecutor(threadPoolConfigProperties.getCoreSize(),
                threadPoolConfigProperties.getMaxSize(),
                threadPoolConfigProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(300),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }
}
