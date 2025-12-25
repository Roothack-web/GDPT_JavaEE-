package com.example.ordersystem.service;

import com.example.ordersystem.entity.User;
import com.example.ordersystem.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userMapper.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        List<GrantedAuthority> authorities = Arrays.stream(user.getRoles().split(","))
                .map(role -> role.startsWith("ROLE_") ? role : "ROLE_" + role)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        // 创建自定义UserDetails，包含用户ID
        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    // 自定义UserDetails类，包含用户ID
    private static class CustomUserDetails extends org.springframework.security.core.userdetails.User {
        private final Long userId;

        public CustomUserDetails(Long userId, String username, String password,
                                 List<GrantedAuthority> authorities) {
            super(username, password, authorities);
            this.userId = userId;
        }

        public Long getUserId() {
            return userId;
        }
    }

    // 提供获取当前用户ID的方法
    public static Long getCurrentUserId() {
        // 这个方法需要在Controller中使用SecurityContextHolder来获取
        // 这里只是一个示例
        return null;
    }
}