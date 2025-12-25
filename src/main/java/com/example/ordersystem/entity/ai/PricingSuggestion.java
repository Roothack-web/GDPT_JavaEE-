package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class PricingSuggestion {
    private Long productId;
    private BigDecimal currentPrice;
    private BigDecimal marketAveragePrice;
    private String suggestedPriceRange;
    private String analysis;
    private Date generatedAt;
}
