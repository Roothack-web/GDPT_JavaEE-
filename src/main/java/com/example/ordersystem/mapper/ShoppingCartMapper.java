// ShoppingCartMapper.java
package com.example.ordersystem.mapper;

import com.example.ordersystem.entity.ShoppingCart;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ShoppingCartMapper {

    @Select("SELECT sc.*, p.* FROM shopping_cart sc " +
            "LEFT JOIN product p ON sc.product_id = p.id " +
            "WHERE sc.user_id = #{userId} AND p.is_deleted = 0 " +
            "ORDER BY sc.add_time DESC")
    @Results({
            @Result(property = "id", column = "id"),
            @Result(property = "userId", column = "user_id"),
            @Result(property = "productId", column = "product_id"),
            @Result(property = "productName", column = "product_name"),
            @Result(property = "productPrice", column = "product_price"),
            @Result(property = "quantity", column = "quantity"),
            @Result(property = "product.id", column = "product_id"),
            @Result(property = "product.productName", column = "product_name"),
            @Result(property = "product.productCode", column = "product_code"),
            @Result(property = "product.price", column = "price"),
            @Result(property = "product.stock", column = "stock"),
            @Result(property = "product.imageUrl", column = "image_url"),
            @Result(property = "product.category", column = "category")
    })
    List<ShoppingCart> findByUserId(Long userId);

    @Select("SELECT sc.* FROM shopping_cart sc " +
            "WHERE sc.user_id = #{userId} AND sc.product_id = #{productId}")
    ShoppingCart findByUserIdAndProductId(@Param("userId") Long userId,
                                          @Param("productId") Long productId);

    @Insert("INSERT INTO shopping_cart(user_id, product_id, product_name, product_price, quantity) " +
            "VALUES(#{userId}, #{productId}, #{productName}, #{productPrice}, #{quantity})")
    int insert(ShoppingCart cart);

    @Update("UPDATE shopping_cart SET quantity = #{quantity} " +
            "WHERE id = #{id} AND user_id = #{userId}")
    int updateQuantity(@Param("id") Long id, @Param("userId") Long userId,
                       @Param("quantity") Integer quantity);

    @Delete("DELETE FROM shopping_cart WHERE id = #{id} AND user_id = #{userId}")
    int deleteById(@Param("id") Long id, @Param("userId") Long userId);

    @Delete("DELETE FROM shopping_cart WHERE user_id = #{userId}")
    int clearByUserId(Long userId);

    @Select("SELECT COUNT(*) FROM shopping_cart WHERE user_id = #{userId}")
    int countByUserId(Long userId);
}