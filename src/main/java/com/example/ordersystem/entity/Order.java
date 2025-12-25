package com.example.ordersystem.entity;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Data
public class Order {
    private Long id;

    // 这里不要加 @NotBlank，因为会在service层自动生成
    private String orderNo;

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    // 这里不要加 @NotNull，因为会在service层计算
    private BigDecimal totalAmount;

    private Integer status = 0;

    @Size(max = 20, message = "支付方式不能超过20个字符")
    private String paymentMethod;

    private Date paymentTime;

    @NotBlank(message = "收货地址不能为空")
    @Size(max = 500, message = "收货地址不能超过500个字符")
    private String deliveryAddress;

    @NotBlank(message = "收货人姓名不能为空")
    @Size(max = 50, message = "收货人姓名不能超过50个字符")
    private String receiverName;

    @NotBlank(message = "收货人电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入有效的手机号码")
    private String receiverPhone;

    @Size(max = 500, message = "备注不能超过500个字符")
    private String remark;

    private Date createTime;
    private Date updateTime;
    private Integer isDeleted = 0;

    private List<OrderItem> orderItems;
}