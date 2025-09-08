package com.taklip.yoda.content.config;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

/**
 * Custom cache key generator to ensure consistent cache keys
 */
@Component("customCacheKeyGenerator")
public class CustomCacheKeyGenerator implements KeyGenerator {

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(target.getClass().getSimpleName());
        keyBuilder.append(".");
        keyBuilder.append(method.getName());
        
        if (params != null && params.length > 0) {
            keyBuilder.append("(");
            for (int i = 0; i < params.length; i++) {
                if (i > 0) {
                    keyBuilder.append(",");
                }
                keyBuilder.append(params[i] != null ? params[i].toString() : "null");
            }
            keyBuilder.append(")");
        }
        
        return keyBuilder.toString();
    }
}
