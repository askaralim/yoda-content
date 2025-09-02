package com.taklip.yoda.content.config;

import java.text.SimpleDateFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Configuration
public class JacksonConfig {
    @Bean
    public ObjectMapper objectMapper() {
        // ObjectMapper objectMapper = new ObjectMapper();
        // objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
        // false);
        // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // objectMapper.registerModule(new JavaTimeModule());
        // objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        // objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        ObjectMapper mapper = new ObjectMapper();
        return mapper
                .setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY)
                .activateDefaultTyping(mapper.getPolymorphicTypeValidator(), ObjectMapper.DefaultTyping.NON_FINAL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
                .registerModule(new JavaTimeModule()).disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"))
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }
}