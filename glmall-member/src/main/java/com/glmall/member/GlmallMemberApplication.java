package com.glmall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;


/**
 * @EnableFeignClients：启用feign远程调用
 * @EnableDiscoveryClient：启用nacos服务发现
 */
@ComponentScan(basePackages = {"com.glmall.common.configuration", "com.glmall.member"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
                @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
@EnableFeignClients(basePackages = "com.glmall.member.feign")
@SpringBootApplication
@EnableDiscoveryClient
public class GlmallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallMemberApplication.class, args);
    }

}
