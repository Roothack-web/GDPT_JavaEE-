// ShoppingCartController.java
package com.example.ordersystem.controller;

import com.example.ordersystem.entity.*;
import com.example.ordersystem.service.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/cart")
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    // 获取当前用户的购物车
    @GetMapping
    public ApiResponse<List<ShoppingCart>> getCart() {
        Long userId = getCurrentUserId();
        List<ShoppingCart> cartItems = shoppingCartService.getCartByUserId(userId);
        return ApiResponse.success(cartItems);
    }

    // 添加商品到购物车
    @PostMapping("/add")
    public ApiResponse<ShoppingCart> addToCart(@Valid @RequestBody ShoppingCartRequest request) {
        Long userId = getCurrentUserId();
        return shoppingCartService.addToCart(userId, request);
    }

    // 更新购物车商品数量
    @PutMapping("/{cartId}/quantity")
    public ApiResponse<ShoppingCart> updateQuantity(@PathVariable Long cartId,
                                                    @RequestParam Integer quantity) {
        Long userId = getCurrentUserId();
        return shoppingCartService.updateCartItem(userId, cartId, quantity);
    }

    // 从购物车移除商品
    @DeleteMapping("/{cartId}")
    public ApiResponse<Void> removeFromCart(@PathVariable Long cartId) {
        Long userId = getCurrentUserId();
        return shoppingCartService.removeFromCart(userId, cartId);
    }

    // 清空购物车
    @DeleteMapping("/clear")
    public ApiResponse<Void> clearCart() {
        Long userId = getCurrentUserId();
        return shoppingCartService.clearCart(userId);
    }

    // 获取购物车数量
    @GetMapping("/count")
    public ApiResponse<Integer> getCartCount() {
        Long userId = getCurrentUserId();
        int count = shoppingCartService.getCartCount(userId);
        return ApiResponse.success(count);
    }

    // 结算购物车（创建订单）
    @PostMapping("/checkout")
    public ApiResponse<Order> checkout(@RequestBody CheckoutRequest request) {
        Long userId = getCurrentUserId();
        return shoppingCartService.checkout(
                userId,
                request.getDeliveryAddress(),
                request.getReceiverName(),
                request.getReceiverPhone(),
                request.getRemark()
        );
    }

    // 获取当前用户ID
    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // 这里需要根据实际用户信息获取用户ID
        // 暂时返回1，实际应该从数据库查询
        return 1L;
    }
}

// CheckoutRequest.java
class CheckoutRequest {
    private String deliveryAddress;
    private String receiverName;
    private String receiverPhone;
    private String remark;

    // getters and setters
    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }

    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }

    public String getReceiverPhone() { return receiverPhone; }
    public void setReceiverPhone(String receiverPhone) { this.receiverPhone = receiverPhone; }

    public String getRemark() { return remark; }
    public void setRemark(String remark) { this.remark = remark; }
}