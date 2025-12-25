package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.util.Date;

@Data
public class InventoryForecast {
    private Long productId;
    private Integer currentStock;
    private Integer recommendedRestockQuantity;
    private String analysis;
    private Date forecastDate;
}
