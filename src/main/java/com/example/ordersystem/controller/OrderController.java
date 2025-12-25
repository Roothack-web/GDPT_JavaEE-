package com.example.ordersystem.controller;

import com.example.ordersystem.entity.*;
import com.example.ordersystem.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// ... 其他导入 ...

@RestController
@RequestMapping("/api/orders")
@Validated
public class OrderController {

    @Autowired
    private OrderService orderService;

    // 查询所有订单（分页）
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<?> findAllOrders(
            @Valid PageParam pageParam,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String receiverName,
            @RequestParam(required = false) String receiverPhone,
            @RequestParam(required = false) Date startTime,
            @RequestParam(required = false) Date endTime) {

        // 使用 HashMap 替代 Map.of()
        Map<String, Object> params = new HashMap<>();
        if (orderNo != null) params.put("orderNo", orderNo);
        if (userId != null) params.put("userId", userId);
        if (status != null) params.put("status", status);
        if (receiverName != null) params.put("receiverName", receiverName);
        if (receiverPhone != null) params.put("receiverPhone", receiverPhone);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null) params.put("endTime", endTime);

        return orderService.findAllOrders(pageParam, params);
    }

    // 查询用户订单
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ApiResponse<?> findUserOrders(
            @PathVariable Long userId,
            @Valid PageParam pageParam,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Date startTime,
            @RequestParam(required = false) Date endTime) {

        // 使用 HashMap 替代 Map.of()
        Map<String, Object> params = new HashMap<>();
        if (status != null) params.put("status", status);
        if (startTime != null) params.put("startTime", startTime);
        if (endTime != null) params.put("endTime", endTime);

        return orderService.findUserOrders(pageParam, userId, params);
    }

    // ... 其他方法不变 ...
}

