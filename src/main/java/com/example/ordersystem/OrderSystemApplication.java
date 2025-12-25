package com.example.ordersystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@ServletComponentScan
public class OrderSystemApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderSystemApplication.class, args);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

// 生成不同密码的哈希
        System.out.println("admin123: " + encoder.encode("admin123"));
        System.out.println("123456: " + encoder.encode("123456"));
        System.out.println("Test@2024: " + encoder.encode("Test@2024"));
    }
}
