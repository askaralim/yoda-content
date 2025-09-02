package com.taklip.yoda.content.config;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;

import com.taklip.yoda.content.service.CacheLoggingService;

@ExtendWith(MockitoExtension.class)
class TwoLevelCacheManagerTest {

    @Mock
    private CaffeineCacheManager l1CacheManager;

    @Mock
    private RedisCacheManager l2CacheManager;

    @Mock
    private CacheLoggingService cacheLoggingService;

    @Mock
    private Cache l1Cache;

    @Mock
    private Cache l2Cache;

    private TwoLevelCacheManager twoLevelCacheManager;

    @BeforeEach
    void setUp() {
        twoLevelCacheManager = new TwoLevelCacheManager(l1CacheManager, l2CacheManager, cacheLoggingService);

        // Setup mocks
        when(l1CacheManager.getCache("test-cache")).thenReturn(l1Cache);
        when(l2CacheManager.getCache("test-cache")).thenReturn(l2Cache);
    }

    @Test
    void testGetCache() {
        // When
        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // Then
        assertNotNull(cache);
        assertEquals("test-cache", cache.getName());
    }

    @Test
    void testGetCacheNames() {
        // Given
        twoLevelCacheManager.getCache("test-cache");

        // When
        var cacheNames = twoLevelCacheManager.getCacheNames();

        // Then
        assertTrue(cacheNames.contains("test-cache"));
    }

    @Test
    void testL1CacheHit() {
        // Given
        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(valueWrapper.get()).thenReturn("test-value");
        when(l1Cache.get("test-key")).thenReturn(valueWrapper);

        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        Cache.ValueWrapper result = cache.get("test-key");

        // Then
        assertNotNull(result);
        assertEquals("test-value", result.get());
        verify(cacheLoggingService).logCacheHit("L1:test-cache", "test-key", 0);
        verify(l2Cache, never()).get(any());
    }

    @Test
    void testL2CacheHit() {
        // Given
        when(l1Cache.get("test-key")).thenReturn(null); // L1 miss

        Cache.ValueWrapper valueWrapper = mock(Cache.ValueWrapper.class);
        when(valueWrapper.get()).thenReturn("test-value");
        when(l2Cache.get("test-key")).thenReturn(valueWrapper); // L2 hit

        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        Cache.ValueWrapper result = cache.get("test-key");

        // Then
        assertNotNull(result);
        assertEquals("test-value", result.get());
        verify(cacheLoggingService).logCacheHit("L2:test-cache", "test-key", 0);
        verify(l1Cache).put("test-key", "test-value"); // Should populate L1
    }

    @Test
    void testCacheMiss() {
        // Given
        when(l1Cache.get("test-key")).thenReturn(null); // L1 miss
        when(l2Cache.get("test-key")).thenReturn(null); // L2 miss

        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        Cache.ValueWrapper result = cache.get("test-key");

        // Then
        assertNull(result);
        verify(cacheLoggingService).logCacheMiss("L1+L2:test-cache", "test-key", 0, "Not found in either cache");
    }

    @Test
    void testPut() {
        // Given
        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        cache.put("test-key", "test-value");

        // Then
        verify(l1Cache).put("test-key", "test-value");
        verify(l2Cache).put("test-key", "test-value");
        verify(cacheLoggingService).logCachePut("L1+L2:test-cache", "test-key", 0);
    }

    @Test
    void testEvict() {
        // Given
        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        cache.evict("test-key");

        // Then
        verify(l1Cache).evict("test-key");
        verify(l2Cache).evict("test-key");
        verify(cacheLoggingService).logCacheEvict("L1+L2:test-cache", "test-key", 0);
    }

    @Test
    void testClear() {
        // Given
        Cache cache = twoLevelCacheManager.getCache("test-cache");

        // When
        cache.clear();

        // Then
        verify(l1Cache).clear();
        verify(l2Cache).clear();
        verify(cacheLoggingService).logCacheEvict("L1+L2:test-cache", "ALL", 0);
    }
}
