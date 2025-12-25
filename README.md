# 基于SSM框架的商品订单系统

## 项目简介
这是一个基于SSM（Spring + Spring MVC + MyBatis）框架开发的商品订单管理系统。
集成了DeepSeek AI智能分析功能。

## 功能特性
- 商品管理（增删改查、分页查询）
- 订单管理（下单、查询、状态管理）
- 用户认证与权限控制
- DeepSeek AI智能推荐和分析
- 安全防护（SQL注入预防、XSS防护）

## 技术栈
- 后端：Spring Boot 2.7, MyBatis, Spring Security
- 前端：Thymeleaf, Bootstrap 5, JavaScript
- 数据库：MySQL 8.0
- AI集成：DeepSeek API

## 快速开始
1. 配置数据库：修改 application.properties
2. 运行：mvn spring-boot:run
3. 访问：http://localhost:8080

## 默认账户
- 管理员：admin/admin123
- 普通用户：test/test123
