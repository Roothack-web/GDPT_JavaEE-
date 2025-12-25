package com.example.ordersystem.exception;

public class ErrorCode {
    
    // 成功
    public static final int SUCCESS = 200;
    
    // 客户端错误
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int FORBIDDEN = 403;
    public static final int NOT_FOUND = 404;
    
    // 服务端错误
    public static final int INTERNAL_SERVER_ERROR = 500;
    
    // 业务错误
    public static final int PRODUCT_NOT_FOUND = 1001;
    public static final int PRODUCT_CODE_EXISTS = 1002;
    public static final int STOCK_NOT_ENOUGH = 1003;
    public static final int ORDER_NOT_FOUND = 2001;
    public static final int USER_NOT_FOUND = 3001;
    
    // AI错误
    public static final int AI_SERVICE_ERROR = 9001;
    public static final int AI_API_KEY_ERROR = 9002;
}
