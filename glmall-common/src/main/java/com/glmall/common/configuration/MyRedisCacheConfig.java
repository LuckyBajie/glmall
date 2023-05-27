package com.glmall.common.configuration;

import com.alibaba.fastjson.support.spring.GenericFastJsonRedisSerializer;
import com.glmall.common.interceptor.CustomizedRedisCacheManager;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * @EnableConfigurationProperties(CacheProperties.class)
 * 从容器中获取properties配置类对象
 * @ConditionalOnProperty: 根据配置文件是否有指定属性配置，来决定是否加载类对象到容器中
 */
@EnableConfigurationProperties(CacheProperties.class)
@Configuration
@EnableCaching
@ConditionalOnProperty(prefix = "config", name = "redis", matchIfMissing = true)
public class MyRedisCacheConfig {

    @Resource
    private CacheProperties cacheProperties;
    /**
     * 1、和配置文件配置的配置类
     * @ConfigurationProperties(prefix = "spring.cache")
     * public class CacheProperties {
     *
     * 2、要让它生效
     * @return
     */
    @Bean
    RedisCacheConfiguration redisCacheConfiguration(){
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig();
        cacheConfig = cacheConfig.serializeKeysWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new StringRedisSerializer()));
        cacheConfig = cacheConfig.serializeValuesWith(RedisSerializationContext
                .SerializationPair.fromSerializer(new GenericFastJsonRedisSerializer()));
//        cacheConfig = cacheConfig.serializeValuesWith(RedisSerializationContext
//                .SerializationPair.fromSerializer(new GenericJackson2JsonRedisSerializer()));
        // 配置文件中的东西没用上，将配置文件中的所有配置都让它生效

        CacheProperties.Redis redisProperties = this.cacheProperties.getRedis();

        if (redisProperties.getTimeToLive() != null) {
            cacheConfig = cacheConfig.entryTtl(redisProperties.getTimeToLive());
        }
        if (redisProperties.getKeyPrefix() != null) {
            cacheConfig = cacheConfig.prefixKeysWith(redisProperties.getKeyPrefix());
        }
        if (!redisProperties.isCacheNullValues()) {
            cacheConfig = cacheConfig.disableCachingNullValues();
        }
        if (!redisProperties.isUseKeyPrefix()) {
            cacheConfig = cacheConfig.disableKeyPrefix();
        }

        return cacheConfig;
    }

    @Bean
    public CacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultCacheConfig = redisCacheConfiguration();
        defaultCacheConfig.entryTtl(Duration.ofDays(1));

        CustomizedRedisCacheManager redisCacheManager = new CustomizedRedisCacheManager(RedisCacheWriter
                .nonLockingRedisCacheWriter(connectionFactory), defaultCacheConfig);
        return redisCacheManager;
    }
}
