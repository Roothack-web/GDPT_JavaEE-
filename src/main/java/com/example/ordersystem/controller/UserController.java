package com.example.ordersystem.controller;

import com.example.ordersystem.entity.ApiResponse;
import com.example.ordersystem.entity.User;
import com.example.ordersystem.entity.UserRegisterRequest;
import com.example.ordersystem.mapper.UserMapper;
import com.example.ordersystem.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    // 用户注册
    @PostMapping("/register")
    public ApiResponse<User> register(@Valid @RequestBody UserRegisterRequest request) {
        // 验证密码是否一致
        if (!request.isPasswordMatch()) {
            return ApiResponse.error("两次输入的密码不一致");
        }

        // 检查用户名是否已存在
        User existingUser = userMapper.findByUsername(request.getUsername());
        if (existingUser != null) {
            return ApiResponse.error("用户名已存在");
        }

        // 创建新用户
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setRoles("ROLE_USER");
        user.setStatus(1);

        int result = userMapper.insert(user);
        if (result > 0) {
            // 不返回密码
            user.setPassword(null);
            return ApiResponse.success(user);
        }

        return ApiResponse.error("注册失败");
    }

    // 检查用户名是否可用
    @GetMapping("/check-username/{username}")
    public ApiResponse<Boolean> checkUsername(@PathVariable String username) {
        User user = userMapper.findByUsername(username);
        return ApiResponse.success(user == null);
    }
}