package com.taklip.yoda.content.exception;

public class ContentServiceException extends RuntimeException {
    
    private final String errorCode;
    
    public ContentServiceException(String message) {
        super(message);
        this.errorCode = "CONTENT_ERROR";
    }
    
    public ContentServiceException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "CONTENT_ERROR";
    }
    
    public ContentServiceException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public ContentServiceException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
