package com.taklip.yoda.content.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.taklip.yoda.content.model.Content;

public class ContentUtil {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Generate natural key from title
     */
    public static String generateNaturalKey(String title) {
        if (title == null || title.trim().isEmpty()) {
            return null;
        }
        
        return title.toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .trim();
    }
    
    /**
     * Format date for display
     */
    public static String formatDate(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        return dateTime.format(DATE_FORMATTER);
    }
    
    /**
     * Parse tags string to list
     */
    public static List<String> parseTags(String tags) {
        if (tags == null || tags.trim().isEmpty()) {
            return Arrays.asList();
        }
        
        return Arrays.stream(tags.split(","))
                .map(String::trim)
                .filter(tag -> !tag.isEmpty())
                .collect(Collectors.toList());
    }
    
    /**
     * Convert tags list to string
     */
    public static String tagsToString(List<String> tags) {
        if (tags == null || tags.isEmpty()) {
            return "";
        }
        
        return String.join(", ", tags);
    }
    
    /**
     * Validate content before save
     */
    public static void validateContent(Content content) {
        if (content == null) {
            throw new IllegalArgumentException("Content cannot be null");
        }
        
        if (content.getTitle() == null || content.getTitle().trim().isEmpty()) {
            throw new IllegalArgumentException("Content title is required");
        }
        
        if (content.getDescription() == null || content.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Content description is required");
        }
    }
    
    /**
     * Check if content is published and not expired
     */
    public static boolean isContentActive(Content content) {
        if (content == null) {
            return false;
        }
        
        if (!content.isPublished()) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        
        // Check if content is published
        if (content.getPublishDate() != null && content.getPublishDate().isAfter(now)) {
            return false;
        }
        
        // Check if content is expired
        if (content.getExpireDate() != null && content.getExpireDate().isBefore(now)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Generate SEO-friendly URL
     */
    public static String generateSeoUrl(String title) {
        return generateNaturalKey(title);
    }
    
    /**
     * Truncate text for short description
     */
    public static String truncateText(String text, int maxLength) {
        if (text == null || text.length() <= maxLength) {
            return text;
        }
        
        return text.substring(0, maxLength - 3) + "...";
    }
}
