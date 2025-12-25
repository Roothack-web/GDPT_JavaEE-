package com.example.ordersystem.entity;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class Product {
    private Long id;
    
    @NotBlank(message = "商品名称不能为空")
    @Size(max = 100, message = "商品名称不能超过100个字符")
    private String productName;
    
    @NotBlank(message = "商品编码不能为空")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "商品编码只能包含大写字母、数字和下划线")
    private String productCode;
    
    @Size(max = 500, message = "描述不能超过500个字符")
    private String description;
    
    @NotNull(message = "价格不能为空")
    @DecimalMin(value = "0.01", message = "价格必须大于0")
    private BigDecimal price;
    
    @NotNull(message = "库存不能为空")
    @Min(value = 0, message = "库存不能为负数")
    private Integer stock;
    
    private Integer status = 1;
    
    @Size(max = 50, message = "分类不能超过50个字符")
    private String category;
    
    private String imageUrl;
    private Date createTime;
    private Date updateTime;
    private String createBy;
    private String updateBy;
    private Integer isDeleted = 0;
}
