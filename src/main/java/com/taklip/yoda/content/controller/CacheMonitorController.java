package com.taklip.yoda.content.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.github.benmanes.caffeine.cache.stats.CacheStats;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/v1/monitor")
@Tag(name = "Cache Monitoring", description = "Cache monitoring and statistics endpoints")
public class CacheMonitorController {

    @Autowired
    private CacheManager cacheManager;

    @GetMapping("/cache/stats")
    @Operation(summary = "Get detailed cache statistics", description = "Retrieve comprehensive cache statistics including L1 and L2 cache details")
    public ResponseEntity<Map<String, Object>> getDetailedCacheStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        // Get cache names
        String[] cacheNames = cacheManager.getCacheNames().toArray(new String[0]);
        stats.put("totalCaches", cacheNames.length);
        stats.put("cacheNames", cacheNames);
        
        // Detailed cache information
        Map<String, Object> cacheDetails = new HashMap<>();
        long totalHits = 0;
        long totalMisses = 0;
        long totalRequests = 0;
        
        for (String cacheName : cacheNames) {
            Map<String, Object> cacheInfo = new HashMap<>();
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            
            if (cache != null) {
                cacheInfo.put("name", cacheName);
                cacheInfo.put("available", true);
                cacheInfo.put("nativeCacheType", cache.getNativeCache().getClass().getSimpleName());
                
                // Get Caffeine cache statistics for L1 cache
                if (cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                    com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = 
                        (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                    CacheStats caffeineStats = caffeineCache.stats();
                    
                    Map<String, Object> l1Stats = new HashMap<>();
                    l1Stats.put("hitCount", caffeineStats.hitCount());
                    l1Stats.put("missCount", caffeineStats.missCount());
                    l1Stats.put("requestCount", caffeineStats.requestCount());
                    l1Stats.put("hitRate", caffeineStats.hitRate());
                    l1Stats.put("missRate", caffeineStats.missRate());
                    l1Stats.put("averageLoadPenalty", caffeineStats.averageLoadPenalty());
                    l1Stats.put("evictionCount", caffeineStats.evictionCount());
                    l1Stats.put("evictionWeight", caffeineStats.evictionWeight());
                    
                    cacheInfo.put("l1CacheStats", l1Stats);
                    
                    totalHits += caffeineStats.hitCount();
                    totalMisses += caffeineStats.missCount();
                    totalRequests += caffeineStats.requestCount();
                }
                
                // For Redis cache (L2), we can't get detailed stats easily, but we can check availability
                if (cacheName.contains("redis") || cache.getNativeCache().getClass().getName().contains("redis")) {
                    Map<String, Object> l2Stats = new HashMap<>();
                    l2Stats.put("type", "Redis");
                    l2Stats.put("available", cache.getNativeCache() != null);
                    cacheInfo.put("l2CacheStats", l2Stats);
                }
                
            } else {
                cacheInfo.put("name", cacheName);
                cacheInfo.put("available", false);
            }
            
            cacheDetails.put(cacheName, cacheInfo);
        }
        
        // Overall statistics
        Map<String, Object> overallStats = new HashMap<>();
        overallStats.put("totalHits", totalHits);
        overallStats.put("totalMisses", totalMisses);
        overallStats.put("totalRequests", totalRequests);
        overallStats.put("overallHitRate", totalRequests > 0 ? (double) totalHits / totalRequests : 0.0);
        overallStats.put("overallMissRate", totalRequests > 0 ? (double) totalMisses / totalRequests : 0.0);
        
        stats.put("cacheDetails", cacheDetails);
        stats.put("overallStats", overallStats);
        
        // Log the statistics
        log.info("📊 Cache Statistics Requested - Total Caches: {}, Total Requests: {}, Hit Rate: {:.2%}", 
                totalRequests, totalRequests, overallStats.get("overallHitRate"));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/cache/health")
    @Operation(summary = "Check cache health", description = "Check if all caches are available and healthy")
    public ResponseEntity<Map<String, Object>> getCacheHealth() {
        Map<String, Object> health = new HashMap<>();
        
        boolean allCachesAvailable = true;
        Map<String, Boolean> cacheHealth = new HashMap<>();
        int availableCount = 0;
        
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            boolean available = cache != null && cache.getNativeCache() != null;
            cacheHealth.put(cacheName, available);
            
            if (available) {
                availableCount++;
            } else {
                allCachesAvailable = false;
            }
        }
        
        health.put("status", allCachesAvailable ? "UP" : "DEGRADED");
        health.put("cacheHealth", cacheHealth);
        health.put("totalCaches", cacheManager.getCacheNames().size());
        health.put("availableCaches", availableCount);
        health.put("unavailableCaches", cacheManager.getCacheNames().size() - availableCount);
        
        // Log health status
        if (allCachesAvailable) {
            log.info("✅ Cache Health Check - All {} caches are available", availableCount);
        } else {
            log.warn("⚠️ Cache Health Check - {} of {} caches are available", 
                    availableCount, cacheManager.getCacheNames().size());
        }
        
        return ResponseEntity.ok(health);
    }

    @GetMapping("/cache/summary")
    @Operation(summary = "Get cache summary", description = "Get a quick summary of cache performance")
    public ResponseEntity<Map<String, Object>> getCacheSummary() {
        Map<String, Object> summary = new HashMap<>();
        
        long totalHits = 0;
        long totalMisses = 0;
        long totalRequests = 0;
        int l1CacheCount = 0;
        int l2CacheCount = 0;
        
        for (String cacheName : cacheManager.getCacheNames()) {
            org.springframework.cache.Cache cache = cacheManager.getCache(cacheName);
            if (cache != null && cache.getNativeCache() instanceof com.github.benmanes.caffeine.cache.Cache) {
                com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache = 
                    (com.github.benmanes.caffeine.cache.Cache<?, ?>) cache.getNativeCache();
                CacheStats stats = caffeineCache.stats();
                
                totalHits += stats.hitCount();
                totalMisses += stats.missCount();
                totalRequests += stats.requestCount();
                l1CacheCount++;
            } else if (cache != null && cache.getNativeCache().getClass().getName().contains("redis")) {
                l2CacheCount++;
            }
        }
        
        double hitRate = totalRequests > 0 ? (double) totalHits / totalRequests : 0.0;
        
        summary.put("totalRequests", totalRequests);
        summary.put("totalHits", totalHits);
        summary.put("totalMisses", totalMisses);
        summary.put("hitRate", String.format("%.2f%%", hitRate * 100));
        summary.put("missRate", String.format("%.2f%%", (1 - hitRate) * 100));
        summary.put("l1CacheCount", l1CacheCount);
        summary.put("l2CacheCount", l2CacheCount);
        summary.put("totalCacheCount", l1CacheCount + l2CacheCount);
        
        // Performance indicators
        Map<String, String> performance = new HashMap<>();
        if (hitRate >= 0.8) {
            performance.put("status", "EXCELLENT");
            performance.put("color", "green");
        } else if (hitRate >= 0.6) {
            performance.put("status", "GOOD");
            performance.put("color", "yellow");
        } else {
            performance.put("status", "NEEDS_IMPROVEMENT");
            performance.put("color", "red");
        }
        
        summary.put("performance", performance);
        
        log.info("📈 Cache Summary - Requests: {}, Hit Rate: {:.2%}, Performance: {}", 
                totalRequests, hitRate, performance.get("status"));
        
        return ResponseEntity.ok(summary);
    }
}
