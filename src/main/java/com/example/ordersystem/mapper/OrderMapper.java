package com.example.ordersystem.mapper;

import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderItem;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Mapper
@Repository
public interface OrderMapper {
    
    List<Order> findAll(Map<String, Object> params);
    
    @Select("SELECT * FROM orders WHERE id = #{id} AND is_deleted = 0")
    Order findById(Long id);
    
    @Select("SELECT * FROM orders WHERE order_no = #{orderNo} AND is_deleted = 0")
    Order findByOrderNo(String orderNo);
    
    List<Order> findByUserId(Map<String, Object> params);
    
    @Insert("INSERT INTO orders(order_no, user_id, total_amount, status, payment_method, " +
            "payment_time, delivery_address, receiver_name, receiver_phone, remark) " +
            "VALUES(#{orderNo}, #{userId}, #{totalAmount}, #{status}, #{paymentMethod}, " +
            "#{paymentTime}, #{deliveryAddress}, #{receiverName}, #{receiverPhone}, #{remark})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Order order);
    
    @Update("UPDATE orders SET status = #{status}, payment_method = #{paymentMethod}, " +
            "payment_time = #{paymentTime} WHERE id = #{id} AND is_deleted = 0")
    int updateStatus(Order order);
    
    @Update("UPDATE orders SET is_deleted = 1 WHERE id = #{id} AND is_deleted = 0")
    int deleteById(Long id);
    
    @Insert("INSERT INTO order_item(order_id, product_id, product_name, product_price, " +
            "quantity, subtotal) VALUES(#{orderId}, #{productId}, #{productName}, " +
            "#{productPrice}, #{quantity}, #{subtotal})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertOrderItem(OrderItem orderItem);
    
    @Select("SELECT oi.*, p.* FROM order_item oi " +
            "LEFT JOIN product p ON oi.product_id = p.id " +
            "WHERE oi.order_id = #{orderId} AND p.is_deleted = 0")
    @Results({
        @Result(property = "id", column = "id"),
        @Result(property = "orderId", column = "order_id"),
        @Result(property = "productId", column = "product_id"),
        @Result(property = "productName", column = "product_name"),
        @Result(property = "productPrice", column = "product_price"),
        @Result(property = "quantity", column = "quantity"),
        @Result(property = "subtotal", column = "subtotal"),
        @Result(property = "product.id", column = "product_id"),
        @Result(property = "product.productName", column = "product_name"),
        @Result(property = "product.productCode", column = "product_code"),
        @Result(property = "product.price", column = "price"),
        @Result(property = "product.category", column = "category")
    })
    List<OrderItem> findOrderItemsByOrderId(Long orderId);
    
    @Select("SELECT COUNT(*) FROM orders WHERE is_deleted = 0")
    int count();
}
