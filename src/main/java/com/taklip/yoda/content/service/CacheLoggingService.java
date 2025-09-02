package com.taklip.yoda.content.service;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CacheLoggingService {

    /**
     * Log cache hit
     */
    public void logCacheHit(String cacheName, String key, long durationMs) {
        log.info("🟢 CACHE HIT  - Cache: {} | Key: {} | Duration: {}ms | Layer: L1/L2", 
                cacheName, key, durationMs);
    }

    /**
     * Log cache miss
     */
    public void logCacheMiss(String cacheName, String key, long durationMs, String reason) {
        log.info("🔴 CACHE MISS - Cache: {} | Key: {} | Duration: {}ms | Layer: L1/L2 | Reason: {}", 
                cacheName, key, durationMs, reason);
    }

    /**
     * Log cache put
     */
    public void logCachePut(String cacheName, String key, long durationMs) {
        log.info("🟡 CACHE PUT  - Cache: {} | Key: {} | Duration: {}ms | Layer: L1/L2", 
                cacheName, key, durationMs);
    }

    /**
     * Log cache evict
     */
    public void logCacheEvict(String cacheName, String key, long durationMs) {
        log.info("🟠 CACHE EVICT - Cache: {} | Key: {} | Duration: {}ms | Layer: L1/L2", 
                cacheName, key, durationMs);
    }

    /**
     * Log cache error
     */
    public void logCacheError(String cacheName, String key, long durationMs, String error) {
        log.error("❌ CACHE ERROR - Cache: {} | Key: {} | Duration: {}ms | Error: {}", 
                cacheName, key, durationMs, error);
    }

    /**
     * Log cache operation with timing
     */
    public <T> T logCacheOperation(String cacheName, String key, String operation, 
                                  CacheOperation<T> cacheOp) {
        long startTime = System.nanoTime();
        
        try {
            T result = cacheOp.execute();
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            
            switch (operation.toLowerCase()) {
                case "hit":
                    logCacheHit(cacheName, key, duration);
                    break;
                case "miss":
                    logCacheMiss(cacheName, key, duration, "Not found");
                    break;
                case "put":
                    logCachePut(cacheName, key, duration);
                    break;
                case "evict":
                    logCacheEvict(cacheName, key, duration);
                    break;
                default:
                    log.info("🔍 CACHE OP - Cache: {} | Key: {} | Operation: {} | Duration: {}ms", 
                            cacheName, key, operation, duration);
            }
            
            return result;
            
        } catch (RuntimeException e) {
            long duration = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startTime);
            logCacheError(cacheName, key, duration, e.getMessage());
            throw e;
        }
    }

    /**
     * Functional interface for cache operations
     */
    @FunctionalInterface
    public interface CacheOperation<T> {
        T execute();
    }
}
