package com.example.ordersystem.entity.ai;

import lombok.Data;
import java.util.Date;

@Data
public class CustomerServiceResponse {
    private String response;
    private Double confidence;
    private Date generatedAt;
}
