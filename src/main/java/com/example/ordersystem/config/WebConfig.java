package com.example.ordersystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 映射上传文件的访问路径
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:uploads/");

        // 如果上面的不行，尝试绝对路径
        // registry.addResourceHandler("/uploads/**")
        //         .addResourceLocations("file:" + System.getProperty("user.dir") + "/uploads/");
    }
}