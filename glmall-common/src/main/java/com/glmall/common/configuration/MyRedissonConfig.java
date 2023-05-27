package com.glmall.common.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ConditionalOnProperty: 根据配置文件是否有指定属性配置，来决定是否加载类对象到容器中
 */
@Configuration
@ConditionalOnProperty(prefix = "config", name = "redisson", matchIfMissing = true)
public class MyRedissonConfig {

    /**
     * 所有对redisson的使用都是通过redissonClient操作
     * @return
     */
    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        // 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.1.158:6379");
        /*
        如果是redis集群
        config.useClusterServers()
                .addNodeAddress("redis://192.168.1.158:7001","redis://192.168.1.158:7002"...);*/
        return Redisson.create(config);
    }
}
