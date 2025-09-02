package com.taklip.yoda.content.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

/**
 * Simple Redis health check on startup
 */
@Component
@Slf4j
public class RedisHealthCheck implements CommandLineRunner {

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    @Override
    public void run(String... args) throws Exception {
        try {
            // Test Redis connection
            redisConnectionFactory.getConnection().ping();
            log.info("✅ Redis connection successful");
        } catch (Exception e) {
            log.error("❌ Redis connection failed: {}", e.getMessage());
            log.warn("⚠️ Application will continue without Redis caching");
        }
    }
}
