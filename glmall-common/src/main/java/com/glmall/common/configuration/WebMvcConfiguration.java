package com.glmall.common.configuration;

import com.glmall.common.configuration.properties.ViewControllerConfigProperties;
import com.glmall.common.exception.GlmallExceptionControllerAdvice;
import com.glmall.common.interceptor.LogInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@EnableConfigurationProperties(ViewControllerConfigProperties.class)
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {


    @Resource
    private ViewControllerConfigProperties viewControllerConfigProperties;


    @Bean
    public LogInterceptor logInterceptor() {
        return new LogInterceptor();
    }

    @Bean
    public GlmallExceptionControllerAdvice glmallExceptionControllerAdvice() {
        return new GlmallExceptionControllerAdvice();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        List<Map<String, String>> viewControllers =
                viewControllerConfigProperties.getViewControllers();
        if (!CollectionUtils.isEmpty(viewControllers)) {
            viewControllers.forEach(viewControllerMap ->
                    registry.addViewController(viewControllerMap.get("urlPath"))
                            .setViewName(viewControllerMap.get("viewName")));
        }
    }
}
