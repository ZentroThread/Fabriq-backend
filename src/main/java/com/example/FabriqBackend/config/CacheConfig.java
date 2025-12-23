package com.example.FabriqBackend.config;

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
                .entryTtl(Duration.ofHours(24)) // Cache for 24 hours instead of 10 minutes
                .disableCachingNullValues()
                // IMPORTANT: cache-level prefix is evaluated when a cache is created. The tenant
                // context is request-scoped (ThreadLocal) and may be null when caches are first
                // created, which would permanently bake a "no-tenant" prefix into that cache.
                // Instead, keep a static cache prefix here and include tenant information in
                // cache keys (the codebase already uses TenantContext in @Cacheable keys).
                .computePrefixWith(cacheName -> "cache:" + cacheName + "::")
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
