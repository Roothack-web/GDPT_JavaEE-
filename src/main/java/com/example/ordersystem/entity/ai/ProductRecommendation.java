package com.example.ordersystem.entity.ai;

import com.example.ordersystem.entity.Product;
import lombok.Data;

@Data
public class ProductRecommendation {
    private Product product;
    private String recommendationReason;
    private Double matchScore;
}
