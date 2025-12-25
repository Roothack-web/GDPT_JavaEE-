package com.example.ordersystem.controller;

import com.example.ordersystem.entity.ApiResponse;
import com.example.ordersystem.service.DeepSeekService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
public class DeepSeekAIController {
    
    @Autowired
    private DeepSeekService deepSeekService;
    
    @GetMapping("/recommend/products")
    public ApiResponse<?> recommendProducts(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) String query) {
        
        return deepSeekService.recommendProducts(userId, query);
    }
    
    @GetMapping("/pricing/suggest/{productId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> suggestPricing(@PathVariable Long productId) {
        return deepSeekService.suggestPricing(productId);
    }
    
    @GetMapping("/analysis/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> analyzeOrders(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate) {
        
        return deepSeekService.analyzeOrders(startDate, endDate);
    }
    
    @PostMapping("/customer-service/response")
    public ApiResponse<?> getCustomerServiceResponse(
            @RequestBody Map<String, String> request) {
        
        String question = request.get("question");
        String context = request.get("context");
        
        return deepSeekService.generateCustomerServiceResponse(question, context);
    }
    
    @PostMapping("/direct")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> callDeepSeekDirectly(@RequestBody Map<String, String> request) {
        try {
            String prompt = request.get("prompt");
            String response = deepSeekService.callDeepSeekAPI(prompt);
            return ApiResponse.success(response);
        } catch (Exception e) {
            return ApiResponse.error("调用失败: " + e.getMessage());
        }
    }
}
