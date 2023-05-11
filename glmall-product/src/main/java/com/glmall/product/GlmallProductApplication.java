package com.glmall.product;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ComponentScan(basePackages = {"com.glmall.common.configuration", "com.glmall.product"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
                @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.glmall.product.feign")
@Slf4j
public class GlmallProductApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(GlmallProductApplication.class, args);
        String[] beans = context.getBeanDefinitionNames();
        // Arrays.stream(beans).forEach(bean -> log.error("bean 的名字,{}", bean));
        List<String> listBean = Arrays.asList(beans);
        List<String> innerInterceptor = listBean.stream().filter(bean -> bean.contains("paginationInnerInterceptor")).collect(Collectors.toList());
        innerInterceptor.forEach(bean -> log.error("bean 的名字,{}", bean));

    }

}
