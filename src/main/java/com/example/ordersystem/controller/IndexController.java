package com.example.ordersystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class IndexController {
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/ai")
    public String aiDashboard() {
        return "ai-dashboard";
    }

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @GetMapping("/shop")
    public String shop() {
        return "shop";
    }
}

