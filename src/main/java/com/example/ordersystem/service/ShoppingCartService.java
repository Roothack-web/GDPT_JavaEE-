package com.example.ordersystem.service;

import com.example.ordersystem.entity.*;
import com.example.ordersystem.mapper.ProductMapper;
import com.example.ordersystem.mapper.ShoppingCartMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class ShoppingCartService {

    @Autowired
    private ShoppingCartMapper shoppingCartMapper;

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private OrderService orderService;

    // 获取用户购物车
    public List<ShoppingCart> getCartByUserId(Long userId) {
        return shoppingCartMapper.findByUserId(userId);
    }

    // 添加商品到购物车
    @Transactional
    public ApiResponse<ShoppingCart> addToCart(Long userId, ShoppingCartRequest request) {
        Product product = productMapper.findById(request.getProductId());

        if (product == null || product.getStatus() != 1) {
            return ApiResponse.error("商品不存在或已下架");
        }

        if (product.getStock() < request.getQuantity()) {
            return ApiResponse.error("库存不足");
        }

        // 检查是否已经在购物车
        ShoppingCart existing = shoppingCartMapper.findByUserIdAndProductId(userId, request.getProductId());

        if (existing != null) {
            // 更新数量
            int newQuantity = existing.getQuantity() + request.getQuantity();
            if (newQuantity > product.getStock()) {
                return ApiResponse.error("超过库存限制");
            }

            shoppingCartMapper.updateQuantity(existing.getId(), userId, newQuantity);
            existing.setQuantity(newQuantity);
            return ApiResponse.success(existing);
        } else {
            // 新增
            ShoppingCart cart = new ShoppingCart();
            cart.setUserId(userId);
            cart.setProductId(product.getId());
            cart.setProductName(product.getProductName());
            cart.setProductPrice(product.getPrice());
            cart.setQuantity(request.getQuantity());

            shoppingCartMapper.insert(cart);
            cart.setProduct(product);
            return ApiResponse.success(cart);
        }
    }

    // 更新购物车商品数量
    @Transactional
    public ApiResponse<ShoppingCart> updateCartItem(Long userId, Long cartId, Integer quantity) {
        if (quantity <= 0) {
            return ApiResponse.error("数量必须大于0");
        }

        // 查找购物车项
        ShoppingCart cart = null;
        List<ShoppingCart> userCartItems = shoppingCartMapper.findByUserId(userId);
        for (ShoppingCart item : userCartItems) {
            if (item.getId().equals(cartId)) {
                cart = item;
                break;
            }
        }

        if (cart == null) {
            return ApiResponse.error("购物车项不存在");
        }

        Product product = productMapper.findById(cart.getProductId());
        if (product.getStock() < quantity) {
            return ApiResponse.error("库存不足");
        }

        shoppingCartMapper.updateQuantity(cartId, userId, quantity);
        cart.setQuantity(quantity);
        return ApiResponse.success(cart);
    }

    // 从购物车删除商品
    @Transactional
    public ApiResponse<Void> removeFromCart(Long userId, Long cartId) {
        int result = shoppingCartMapper.deleteById(cartId, userId);
        if (result > 0) {
            return ApiResponse.success(null);
        }
        return ApiResponse.error("删除失败");
    }

    // 清空购物车
    @Transactional
    public ApiResponse<Void> clearCart(Long userId) {
        shoppingCartMapper.clearByUserId(userId);
        return ApiResponse.success(null);
    }

    // 获取购物车商品数量
    public int getCartCount(Long userId) {
        return shoppingCartMapper.countByUserId(userId);
    }

    // 结算购物车（创建订单）- 修复toList()问题
    // 结算购物车（创建订单）
    @Transactional
    public ApiResponse<Order> checkout(Long userId, String deliveryAddress,
                                       String receiverName, String receiverPhone,
                                       String remark) {
        List<ShoppingCart> cartItems = getCartByUserId(userId);

        if (cartItems.isEmpty()) {
            return ApiResponse.error("购物车为空");
        }

        // 创建订单 - 这里不需要手动生成订单号和计算总金额
        // 这些会在 OrderService.createOrder 中自动完成
        Order order = new Order();
        order.setUserId(userId);
        order.setDeliveryAddress(deliveryAddress);
        order.setReceiverName(receiverName);
        order.setReceiverPhone(receiverPhone);
        order.setRemark(remark);
        order.setStatus(0); // 待付款
        // 不要设置 orderNo 和 totalAmount，让service层处理

        // 将购物车项转换为订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (ShoppingCart cart : cartItems) {
            OrderItem item = new OrderItem();
            item.setProductId(cart.getProductId());
            item.setProductName(cart.getProductName());
            item.setProductPrice(cart.getProductPrice());
            item.setQuantity(cart.getQuantity());

            // 计算小计金额
            BigDecimal subtotal = cart.getProductPrice().multiply(
                    BigDecimal.valueOf(cart.getQuantity()));
            item.setSubtotal(subtotal);

            orderItems.add(item);
        }

        // 调用订单服务创建订单
        ApiResponse<Order> response = orderService.createOrder(order, orderItems);

        if (response.getCode() == 200) {
            // 清空购物车
            clearCart(userId);
        }

        return response;
    }
}