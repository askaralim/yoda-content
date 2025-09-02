package com.taklip.yoda.content.constant;

public class ContentConstants {
    
    // Content Status
    public static final String STATUS_DRAFT = "draft";
    public static final String STATUS_PUBLISHED = "published";
    public static final String STATUS_ARCHIVED = "archived";
    public static final String STATUS_SCHEDULED = "scheduled";
    
    // Content Types
    public static final String TYPE_ARTICLE = "article";
    public static final String TYPE_PAGE = "page";
    public static final String TYPE_BLOG = "blog";
    public static final String TYPE_NEWS = "news";
    public static final String TYPE_PRODUCT = "product";
    
    // Default Values
    public static final String DEFAULT_LANGUAGE = "en";
    public static final Integer DEFAULT_SORT_ORDER = 0;
    public static final Integer DEFAULT_HIT_COUNTER = 0;
    public static final Integer DEFAULT_SCORE = 0;
    public static final Boolean DEFAULT_PUBLISHED = true;
    public static final Boolean DEFAULT_FEATURE_DATA = false;
    public static final Boolean DEFAULT_ALLOW_COMMENTS = true;
    
    // Pagination
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    public static final Integer MAX_PAGE_SIZE = 100;
    public static final String DEFAULT_SORT_BY = "createDate";
    public static final String DEFAULT_SORT_ORDER_DESC = "desc";
    
    // Search
    public static final String SEARCH_SORT_BY_TITLE = "title";
    public static final String SEARCH_SORT_BY_PUBLISH_DATE = "publishDate";
    public static final String SEARCH_SORT_BY_HIT_COUNTER = "hitCounter";
    public static final String SEARCH_SORT_BY_SCORE = "score";
    public static final String SEARCH_SORT_BY_SORT_ORDER = "sortOrder";
    public static final String SEARCH_SORT_BY_CREATE_DATE = "createDate";
    
    // Error Codes
    public static final String ERROR_CONTENT_NOT_FOUND = "CONTENT_NOT_FOUND";
    public static final String ERROR_CONTENT_ALREADY_EXISTS = "CONTENT_ALREADY_EXISTS";
    public static final String ERROR_INVALID_CONTENT_DATA = "INVALID_CONTENT_DATA";
    public static final String ERROR_CONTENT_OPERATION_FAILED = "CONTENT_OPERATION_FAILED";
    
    // Cache Keys
    public static final String CACHE_CONTENT_BY_ID = "content:by:id:";
    public static final String CACHE_CONTENT_BY_NATURAL_KEY = "content:by:natural:key:";
    public static final String CACHE_CONTENT_BY_CATEGORY = "content:by:category:";
    public static final String CACHE_FEATURED_CONTENT = "content:featured";
    public static final String CACHE_PUBLISHED_CONTENT = "content:published";
    
    // Cache TTL (Time To Live) in seconds
    public static final long CACHE_TTL_CONTENT = 3600; // 1 hour
    public static final long CACHE_TTL_FEATURED = 1800; // 30 minutes
    public static final long CACHE_TTL_PUBLISHED = 900; // 15 minutes
    
    // Validation
    public static final int MAX_TITLE_LENGTH = 255;
    public static final int MAX_SHORT_DESCRIPTION_LENGTH = 500;
    public static final int MAX_DESCRIPTION_LENGTH = 10000;
    public static final int MAX_TAGS_LENGTH = 1000;
    public static final int MAX_META_KEYWORDS_LENGTH = 500;
    public static final int MAX_META_DESCRIPTION_LENGTH = 160;
    public static final int MAX_CANONICAL_URL_LENGTH = 500;
    public static final int MAX_SEO_TITLE_LENGTH = 60;
    public static final int MAX_SEO_DESCRIPTION_LENGTH = 160;
    
    // File Upload
    public static final String UPLOAD_CONTENT_IMAGES = "content/images";
    public static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    public static final String[] ALLOWED_IMAGE_TYPES = {
        "image/jpeg", "image/png", "image/gif", "image/webp"
    };
    
    // API Paths
    public static final String API_BASE_PATH = "/api/v1/content";
    public static final String API_HEALTH_PATH = "/actuator/health";
    public static final String API_DOCS_PATH = "/swagger-ui.html";
    
    // Rate Limiting
    public static final int RATE_LIMIT_CREATE = 10; // requests per minute
    public static final int RATE_LIMIT_UPDATE = 20; // requests per minute
    public static final int RATE_LIMIT_READ = 100; // requests per minute
    public static final int RATE_LIMIT_SEARCH = 50; // requests per minute
}
