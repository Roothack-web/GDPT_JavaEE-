package com.example.ordersystem.service;

import com.example.ordersystem.entity.*;
import com.example.ordersystem.mapper.OrderMapper;
import com.example.ordersystem.mapper.ProductMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Validated
public class OrderService {
    
    @Autowired
    private OrderMapper orderMapper;
    
    @Autowired
    private ProductMapper productMapper;
    
    @Autowired
    private ProductService productService;
    
    public ApiResponse<List<Order>> findAllOrders(PageParam pageParam, Map<String, Object> params) {
        PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        List<Order> orders = orderMapper.findAll(params);
        
        for (Order order : orders) {
            List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return ApiResponse.success(
            orders,
            pageInfo.getTotal(),
            pageParam.getPageNum(),
            pageParam.getPageSize()
        );
    }
    
    public ApiResponse<Order> findOrderById(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(id);
        order.setOrderItems(orderItems);
        
        return ApiResponse.success(order);
    }
    
    public ApiResponse<Order> findOrderByNo(String orderNo) {
        Order order = orderMapper.findByOrderNo(orderNo);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(order.getId());
        order.setOrderItems(orderItems);
        
        return ApiResponse.success(order);
    }
    
    @Transactional

    public ApiResponse<Order> createOrder(@Valid Order order, List<OrderItem> orderItems) {
        if (orderItems == null || orderItems.isEmpty()) {
            return ApiResponse.error("订单项不能为空");
        }

        // 1. 生成订单号（如果未提供）
        if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
            String orderNo = generateOrderNo();
            order.setOrderNo(orderNo);
        }

        // 2. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        Set<Long> productIds = new HashSet<>();

        for (OrderItem item : orderItems) {
            Product product = productMapper.findById(item.getProductId());
            if (product == null || product.getStatus() != 1) {
                return ApiResponse.error("商品不存在或已下架: " + item.getProductId());
            }
            if (product.getStock() < item.getQuantity()) {
                return ApiResponse.error("商品库存不足: " + product.getProductName());
            }

            if (productIds.contains(item.getProductId())) {
                return ApiResponse.error("商品ID重复: " + item.getProductId());
            }
            productIds.add(item.getProductId());

            // 设置商品信息
            item.setProductName(product.getProductName());
            item.setProductPrice(product.getPrice());

            // 计算小计（确保小计已正确计算）
            if (item.getSubtotal() == null) {
                BigDecimal subtotal = product.getPrice().multiply(
                        BigDecimal.valueOf(item.getQuantity()));
                item.setSubtotal(subtotal);
            }

            totalAmount = totalAmount.add(item.getSubtotal());
        }

        // 3. 设置总金额
        order.setTotalAmount(totalAmount);

        // 4. 设置默认状态（如果未设置）
        if (order.getStatus() == null) {
            order.setStatus(0); // 待付款
        }

        // 5. 保存订单
        int result = orderMapper.insert(order);
        if (result == 0) {
            return ApiResponse.error("创建订单失败");
        }

        // 6. 保存订单项并扣减库存
        for (OrderItem item : orderItems) {
            item.setOrderId(order.getId());
            orderMapper.insertOrderItem(item);

            // 扣减库存
            ApiResponse<Void> stockResult = productService.updateStock(
                    item.getProductId(), item.getQuantity()
            );
            if (stockResult.getCode() != 200) {
                throw new RuntimeException("库存扣减失败: " + stockResult.getMessage());
            }
        }

        // 7. 返回完整的订单信息
        Order savedOrder = orderMapper.findById(order.getId());
        List<OrderItem> savedOrderItems = orderMapper.findOrderItemsByOrderId(order.getId());
        savedOrder.setOrderItems(savedOrderItems);

        return ApiResponse.success(savedOrder);
    }
    
    @Transactional
    public ApiResponse<Order> updateOrderStatus(Long orderId, Integer status, String paymentMethod) {
        Order order = orderMapper.findById(orderId);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        order.setStatus(status);
        order.setPaymentMethod(paymentMethod);
        if (status == 1) {
            order.setPaymentTime(new Date());
        }
        
        int result = orderMapper.updateStatus(order);
        if (result > 0) {
            return ApiResponse.success(order);
        }
        return ApiResponse.error("更新订单状态失败");
    }
    
    @Transactional
    public ApiResponse<Void> deleteOrder(Long id) {
        Order order = orderMapper.findById(id);
        if (order == null) {
            return ApiResponse.error("订单不存在");
        }
        
        int result = orderMapper.deleteById(id);
        if (result > 0) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error("删除订单失败");
    }
    
    public ApiResponse<List<Order>> findUserOrders(PageParam pageParam, Long userId, Map<String, Object> params) {
        PageHelper.startPage(pageParam.getPageNum(), pageParam.getPageSize());
        params.put("userId", userId);
        List<Order> orders = orderMapper.findByUserId(params);
        
        for (Order order : orders) {
            List<OrderItem> orderItems = orderMapper.findOrderItemsByOrderId(order.getId());
            order.setOrderItems(orderItems);
        }
        
        PageInfo<Order> pageInfo = new PageInfo<>(orders);
        return ApiResponse.success(
            orders,
            pageInfo.getTotal(),
            pageParam.getPageNum(),
            pageParam.getPageSize()
        );
    }
    
    private String generateOrderNo() {
        return "ORD" + System.currentTimeMillis() + String.format("%04d", new Random().nextInt(10000));
    }
}
