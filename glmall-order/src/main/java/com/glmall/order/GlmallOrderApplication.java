package com.glmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.AutoConfigurationExcludeFilter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.TypeExcludeFilter;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import java.util.function.BiConsumer;

@ComponentScan(basePackages = {"com.glmall.common.configuration", "com.glmall.order"},
        excludeFilters = {@ComponentScan.Filter(type = FilterType.CUSTOM, classes = {TypeExcludeFilter.class}),
                @ComponentScan.Filter(type = FilterType.CUSTOM, classes = {AutoConfigurationExcludeFilter.class})})
@SpringBootApplication
@EnableDiscoveryClient
public class GlmallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallOrderApplication.class, args);
        new BiConsumer<Object, Object>() {
            @Override
            public void accept(Object o, Object o2) {

            }
        };

    }

}
