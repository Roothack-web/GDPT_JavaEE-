package com.example.ordersystem.exception;

import com.example.ordersystem.entity.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.BindException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    @ExceptionHandler(Exception.class)
    public ApiResponse<Object> handleException(Exception e) {
        logger.error("系统异常", e);
        return ApiResponse.error(500, "系统异常: " + e.getMessage());
    }
    
    @ExceptionHandler(BindException.class)
    public ApiResponse<Object> handleBindException(BindException e) {
        String errorMsg = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("参数验证失败");
        return ApiResponse.error(400, errorMsg);
    }
    
    @ExceptionHandler(AIException.class)
    public ApiResponse<Object> handleAIException(AIException e) {
        return ApiResponse.error(500, "AI服务异常: " + e.getMessage());
    }
    
    @ExceptionHandler(BusinessException.class)
    public ApiResponse<Object> handleBusinessException(BusinessException e) {
        return ApiResponse.error(e.getCode(), e.getMessage());
    }
}
