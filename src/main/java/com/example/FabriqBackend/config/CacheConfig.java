package com.example.FabriqBackend.config;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;

@Configuration
@EnableCaching
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class CacheConfig {

    @Bean
    public RedisCacheConfiguration redisCacheConfiguration() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10)) // Increased from 60 seconds
                .disableCachingNullValues()
                // 🎯 TENANT-AWARE PREFIX: tenant:t002:cache:attires::
                .computePrefixWith(cacheName -> {
                    String tenantId = TenantContext.getCurrentTenant();
                    String prefix = (tenantId != null && !tenantId.isEmpty())
                            ? "tenant:" + tenantId + ":"
                            : "no-tenant:";
                    System.out.println("🔑 [CACHE KEY] TenantId: " + tenantId + " | CacheName: " + cacheName + " | Prefix: " + prefix);

                    return prefix + "cache:" + cacheName + "::";
                })
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()
                        )
                );
    }

    @Bean(name = "cacheManager")
    @Primary
    public CacheManager cacheManager(RedisConnectionFactory redisConnectionFactory,
                                     RedisCacheConfiguration cacheConfig) {
        return RedisCacheManager.builder(redisConnectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    // Removed in-memory ConcurrentMapCacheManager bean to avoid overshadowing Redis
}
