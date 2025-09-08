package com.taklip.yoda.content.config;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.taklip.yoda.content.constant.ContentConstants;
import com.taklip.yoda.content.service.CacheLoggingService;

import lombok.extern.slf4j.Slf4j;

@Configuration
@EnableCaching
@Slf4j
public class CacheConfig {
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * L1 Cache Manager (Caffeine - In-Memory)
     */
    @Bean("caffeineCacheManager")
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000) // Maximum 1000 entries
                .expireAfterWrite(Duration.ofMinutes(10)) // 10 minutes TTL
                .recordStats()); // Enable statistics

        return cacheManager;
    }

    /**
     * L2 Cache Manager (Redis)
     */
    @Bean("redisCacheManager")
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        // Default cache configuration
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_CONTENT))
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(jsonSerializer))
                .disableCachingNullValues();

        // Custom cache configurations
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();

        // Content by ID cache (1 hour)
        cacheConfigurations.put("content:by:id",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_CONTENT)));

        // Featured content cache (30 minutes)
        cacheConfigurations.put("content:featured",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_FEATURED)));

        cacheConfigurations.put("content:no:featured",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_FEATURED)));

        // Published content cache (15 minutes)
        cacheConfigurations.put("content:published",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_PUBLISHED)));

        // Content by category cache (30 minutes)
        cacheConfigurations.put("content:by:category",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_FEATURED)));

        // Content by tags cache (30 minutes)
        cacheConfigurations.put("content:by:tags",
                defaultConfig.entryTtl(Duration.ofSeconds(ContentConstants.CACHE_TTL_FEATURED)));

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    /**
     * Redis Template for manual cache operations
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key serializer
        template.setKeySerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        
        // Value serializer with Java 8 date/time support
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer(objectMapper);
        
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * Two-Level Cache Manager (Primary)
     */
    @Bean
    @Primary
    public CacheManager twoLevelCacheManager(CaffeineCacheManager caffeineCacheManager,
                                           RedisCacheManager redisCacheManager,
                                           CacheLoggingService cacheLoggingService) {
        try {
            return new TwoLevelCacheManager(caffeineCacheManager, redisCacheManager, cacheLoggingService);
        } catch (Exception e) {
            log.warn("⚠️ Redis not available - falling back to Caffeine only: {}", e.getMessage());
            return caffeineCacheManager;
        }
    }
}
