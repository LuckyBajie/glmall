package com.glmall.common.configuration.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @ConfigurationProperties：说明这个类是配置属性类
 * @Component：配置属性类一定要注入到容器中才能生效;
 * 如果在这里不写@Component就需要在其他配置类中添加
 * 注解@EnableConfigurationProperties(ThreadPoolConfigProperties.class)，
 * 将这个配置类注入到容器中
 */
@ConfigurationProperties(prefix = "glmall.common-thread-pool")
// @Component
@Data
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
