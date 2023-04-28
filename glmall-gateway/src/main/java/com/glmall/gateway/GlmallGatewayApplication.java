package com.glmall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class GlmallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GlmallGatewayApplication.class, args);
    }

}
