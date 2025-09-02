package com.taklip.yoda.content.exception;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ContentServiceException.class)
    public ResponseEntity<Map<String, Object>> handleContentServiceException(
            ContentServiceException ex, WebRequest request) {
        
        String path = request.getDescription(false);
        
        log.warn("Content service exception occurred - ErrorCode: {}, Message: {}, Path: {}", 
                ex.getErrorCode(), ex.getMessage(), path);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("errorCode", ex.getErrorCode());
        errorDetails.put("message", ex.getMessage());
        errorDetails.put("path", path);
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDetails);
    }
    
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        String path = request.getDescription(false);
        
        log.error("Runtime exception occurred - Exception: {}, Message: {}, Path: {}", 
                ex.getClass().getSimpleName(), ex.getMessage(), path, ex);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("errorCode", "INTERNAL_ERROR");
        errorDetails.put("message", "An internal error occurred");
        errorDetails.put("path", path);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex, WebRequest request) {
        
        String path = request.getDescription(false);
        
        log.error("Unexpected exception occurred - Exception: {}, Message: {}, Path: {}", 
                ex.getClass().getSimpleName(), ex.getMessage(), path, ex);
        
        Map<String, Object> errorDetails = new HashMap<>();
        errorDetails.put("timestamp", LocalDateTime.now());
        errorDetails.put("errorCode", "UNKNOWN_ERROR");
        errorDetails.put("message", "An unexpected error occurred");
        errorDetails.put("path", path);
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}
