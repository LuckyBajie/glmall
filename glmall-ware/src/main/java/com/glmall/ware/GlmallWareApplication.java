package com.glmall.ware;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@EnableFeignClients("com.glmall.ware.feign")
@ComponentScan(basePackages = {"com.glmall.common.configuration", "com.glmall.ware"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
                @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
@SpringBootApplication
@EnableDiscoveryClient
public class GlmallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallWareApplication.class, args);
    }

}
