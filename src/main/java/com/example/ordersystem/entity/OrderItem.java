package com.example.ordersystem.entity;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderItem {
    private Long id;
    
    @NotNull(message = "订单ID不能为空")
    private Long orderId;
    
    @NotNull(message = "商品ID不能为空")
    private Long productId;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过100个字符")
    private String productName;
    
    @NotNull(message = "商品单价不能为空")
    @DecimalMin(value = "0.01", message = "商品单价必须大于0")
    private BigDecimal productPrice;
    
    @NotNull(message = "购买数量不能为空")
    @Min(value = 1, message = "购买数量必须大于0")
    private Integer quantity;
    
    @NotNull(message = "小计金额不能为空")
    @DecimalMin(value = "0.01", message = "小计金额必须大于0")
    private BigDecimal subtotal;
    
    private Date createTime;
    
    private Product product;
}
