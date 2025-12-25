package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class OrderAnalysis {
    private String analysis;
    private Integer orderCount;
    private BigDecimal totalAmount;
    private Date analysisDate;
}
