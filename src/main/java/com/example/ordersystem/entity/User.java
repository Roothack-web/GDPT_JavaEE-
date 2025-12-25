package com.example.ordersystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class User {

    private Long id;
    private String username;
    private String password;
    private String email;
    private String phone;
    private Integer status;
    private Date createTime;
    private String roles;
    private String realName;
    private String address;
}
