package com.glmall.thirdparty;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@Slf4j
@SpringBootApplication
public class GlmallThirdpartyApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(GlmallThirdpartyApplication.class, args);
//        String[] beans = context.getBeanDefinitionNames();
//        Arrays.stream(beans).forEach(bean-> log.info(bean));
    }

}
