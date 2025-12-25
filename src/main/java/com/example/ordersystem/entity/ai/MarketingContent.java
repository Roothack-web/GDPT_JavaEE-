package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.util.Date;

@Data
public class MarketingContent {
    private Long productId;
    private String title;
    private String body;
    private String content;
    private Date generatedAt;
}
