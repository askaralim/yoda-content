package com.taklip.yoda.content.config;

import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import com.taklip.yoda.content.service.CacheLoggingService;

import lombok.extern.slf4j.Slf4j;

/**
 * Two-Level Cache Manager that coordinates L1 (Caffeine) and L2 (Redis) caches
 * 
 * Flow:
 * 1. Check L1 (Caffeine) first - if hit, return immediately
 * 2. If L1 miss, check L2 (Redis) - if hit, store in L1 and return
 * 3. If L2 miss, load from database, store in both L1 and L2
 */
@Slf4j
public class TwoLevelCacheManager implements CacheManager {
    private final CaffeineCacheManager l1CacheManager;
    private final RedisCacheManager l2CacheManager;
    private final CacheLoggingService cacheLoggingService;
    private final ConcurrentMap<String, TwoLevelCache> caches = new ConcurrentHashMap<>();

    public TwoLevelCacheManager(CaffeineCacheManager l1CacheManager,
            RedisCacheManager l2CacheManager,
            CacheLoggingService cacheLoggingService) {
        this.l1CacheManager = l1CacheManager;
        this.l2CacheManager = l2CacheManager;
        this.cacheLoggingService = cacheLoggingService;
    }

    @Override
    public Cache getCache(String name) {
        return caches.computeIfAbsent(name, this::createTwoLevelCache);
    }

    @Override
    public Collection<String> getCacheNames() {
        return caches.keySet();
    }

    private TwoLevelCache createTwoLevelCache(String name) {
        Cache l1Cache = l1CacheManager.getCache(name);
        Cache l2Cache = l2CacheManager.getCache(name);

        if (l1Cache == null || l2Cache == null) {
            log.warn("Cache '{}' not found in one of the cache managers", name);
            return null;
        }

        return new TwoLevelCache(name, l1Cache, l2Cache, cacheLoggingService);
    }

    /**
     * Two-Level Cache implementation
     */
    private static class TwoLevelCache implements Cache {

        private final String name;
        private final Cache l1Cache;
        private final Cache l2Cache;
        private final CacheLoggingService cacheLoggingService;

        public TwoLevelCache(String name, Cache l1Cache, Cache l2Cache, CacheLoggingService cacheLoggingService) {
            this.name = name;
            this.l1Cache = l1Cache;
            this.l2Cache = l2Cache;
            this.cacheLoggingService = cacheLoggingService;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Object getNativeCache() {
            return this;
        }

        @Override
        public ValueWrapper get(Object key) {
            String keyStr = key.toString();

            // Step 1: Check L1 (Caffeine)
            ValueWrapper l1Value = l1Cache.get(key);
            if (l1Value != null) {
                cacheLoggingService.logCacheHit("L1:" + name, keyStr, 0);
                return l1Value;
            }

            // Step 2: Check L2 (Redis)
            ValueWrapper l2Value = l2Cache.get(key);
            if (l2Value != null) {
                // Store in L1 for future requests
                l1Cache.put(key, l2Value.get());
                cacheLoggingService.logCacheHit("L2:" + name, keyStr, 0);
                return l2Value;
            }

            // Step 3: Cache miss
            cacheLoggingService.logCacheMiss("L1+L2:" + name, keyStr, 0, "Not found in either cache");
            return null;
        }

        @Override
        public <T> T get(Object key, Class<T> type) {
            ValueWrapper wrapper = get(key);
            return wrapper != null ? (T) wrapper.get() : null;
        }

        @Override
        public <T> T get(Object key, Callable<T> valueLoader) {
            ValueWrapper wrapper = get(key);
            if (wrapper != null) {
                return (T) wrapper.get();
            }

            // Load from database and store in both caches
            try {
                T value = valueLoader.call();
                put(key, value);
                return value;
            } catch (Exception e) {
                log.error("Error loading value for key: {}", key, e);
                throw new RuntimeException("Error loading value", e);
            }
        }

        @Override
        public void put(Object key, Object value) {
            // Store in both L1 and L2
            l1Cache.put(key, value);
            l2Cache.put(key, value);
            cacheLoggingService.logCachePut("L1+L2:" + name, key.toString(), 0);
        }

        @Override
        public void evict(Object key) {
            // Evict from both L1 and L2
            l1Cache.evict(key);
            l2Cache.evict(key);
            cacheLoggingService.logCacheEvict("L1+L2:" + name, key.toString(), 0);
        }

        @Override
        public void clear() {
            // Clear both L1 and L2
            l1Cache.clear();
            l2Cache.clear();
            cacheLoggingService.logCacheEvict("L1+L2:" + name, "ALL", 0);
        }
    }
}
