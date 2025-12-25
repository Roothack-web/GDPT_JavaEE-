package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class CustomerBehavior {
    private Long userId;
    private String analysis;
    private Integer totalOrders;
    private BigDecimal totalSpent;
    private Date analysisDate;
}
