// ShoppingCart.java
package com.example.ordersystem.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class ShoppingCart {
    private Long id;
    private Long userId;
    private Long productId;
    private String productName;
    private BigDecimal productPrice;
    private Integer quantity;
    private Date addTime;

    // 关联的商品信息
    private Product product;
}