/*
 Navicat Premium Data Transfer

 Source Server         : 8.0.12
 Source Server Type    : MySQL
 Source Server Version : 80012 (8.0.12)
 Source Host           : localhost:3306
 Source Schema         : order_system

 Target Server Type    : MySQL
 Target Server Version : 80012 (8.0.12)
 File Encoding         : 65001

 Date: 24/12/2025 17:59:50
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `product_id` bigint(20) NOT NULL COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_price` decimal(10, 2) NOT NULL COMMENT '商品单价',
  `quantity` int(11) NOT NULL COMMENT '购买数量',
  `subtotal` decimal(10, 2) NOT NULL COMMENT '小计金额',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_order_id`(`order_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单商品关联表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 3, '华为 Mate 60', 6999.00, 1, 6999.00, '2025-12-24 17:52:49');
INSERT INTO `order_item` VALUES (2, 1, 2, 'MacBook Pro 14', 15999.00, 1, 15999.00, '2025-12-24 17:52:49');
INSERT INTO `order_item` VALUES (3, 1, 1, 'iPhone 15 Pro', 8999.00, 1, 8999.00, '2025-12-24 17:52:49');

-- ----------------------------
-- Table structure for order_summary
-- ----------------------------
DROP TABLE IF EXISTS `order_summary`;
CREATE TABLE `order_summary`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `total_amount` decimal(10, 2) NOT NULL,
  `item_count` int(11) NOT NULL,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单摘要表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of order_summary
-- ----------------------------

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '订单编号',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `total_amount` decimal(10, 2) NOT NULL COMMENT '订单总金额',
  `status` tinyint(1) NOT NULL DEFAULT 0 COMMENT '订单状态：0-待付款，1-已付款，2-已发货，3-已完成，4-已取消',
  `payment_method` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '支付方式',
  `payment_time` datetime NULL DEFAULT NULL COMMENT '支付时间',
  `delivery_address` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '收货地址',
  `receiver_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人姓名',
  `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '收货人电话',
  `remark` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '订单备注',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_order_no`(`order_no` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_create_time`(`create_time` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '订单表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, 'ORD17665699695304370', 1, 31997.00, 0, NULL, NULL, '佛山市', '陆明', '17064602953', '', '2025-12-24 17:52:49', '2025-12-24 17:52:49', 0);

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品名称',
  `product_code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '商品编码',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '商品描述',
  `price` decimal(10, 2) NOT NULL COMMENT '商品价格',
  `stock` int(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
  `status` tinyint(1) NOT NULL DEFAULT 1 COMMENT '状态：0-下架，1-上架',
  `category` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品分类',
  `image_url` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '商品图片URL',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `is_deleted` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `product_code`(`product_code` ASC) USING BTREE,
  INDEX `idx_product_code`(`product_code` ASC) USING BTREE,
  INDEX `idx_status`(`status` ASC) USING BTREE,
  INDEX `idx_category`(`category` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '商品表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 'iPhone 15 Pro', 'IPHONE15PRO', 'Apple iPhone 15 Pro 256GB', 8999.00, 99, 1, '手机', NULL, '2025-12-24 15:36:59', '2025-12-24 17:52:49', NULL, NULL, 0);
INSERT INTO `product` VALUES (2, 'MacBook Pro 14', 'MACBOOKPRO14', 'Apple MacBook Pro 14寸 M3芯片', 15999.00, 49, 1, '电脑', NULL, '2025-12-24 15:36:59', '2025-12-24 17:52:49', NULL, NULL, 0);
INSERT INTO `product` VALUES (3, '华为 Mate 60', 'HUAWEIMATE60', '华为 Mate 60 Pro 512GB', 6999.00, 79, 1, '手机', NULL, '2025-12-24 15:36:59', '2025-12-24 17:52:49', NULL, NULL, 0);
INSERT INTO `product` VALUES (4, '小米电视 65寸', 'XIAOMITV65', '小米电视 65寸 4K超高清', 3999.00, 30, 1, '电视', NULL, '2025-12-24 15:36:59', '2025-12-24 15:36:59', NULL, NULL, 0);
INSERT INTO `product` VALUES (5, '索尼 PlayStation 5', 'PS5', '索尼 PlayStation 5 游戏主机', 3899.00, 40, 1, '游戏机', NULL, '2025-12-24 15:36:59', '2025-12-24 15:36:59', NULL, NULL, 0);
INSERT INTO `product` VALUES (6, '戴尔显示器 27寸', 'DELL27', '戴尔 27寸 4K显示器', 2499.00, 60, 1, '显示器', NULL, '2025-12-24 15:36:59', '2025-12-24 15:36:59', NULL, NULL, 0);
INSERT INTO `product` VALUES (7, '罗技键盘', 'LOGITECHKEY', '罗技机械键盘 K845', 499.00, 200, 1, '外设', NULL, '2025-12-24 15:36:59', '2025-12-24 15:36:59', NULL, NULL, 0);
INSERT INTO `product` VALUES (8, '雷蛇鼠标', 'RAZERMOUSE', '雷蛇游戏鼠标 DeathAdder', 399.00, 150, 1, '外设', NULL, '2025-12-24 15:36:59', '2025-12-24 15:36:59', NULL, NULL, 0);

-- ----------------------------
-- Table structure for shopping_cart
-- ----------------------------
DROP TABLE IF EXISTS `shopping_cart`;
CREATE TABLE `shopping_cart`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `product_id` bigint(20) NOT NULL,
  `product_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `product_price` decimal(10, 2) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `add_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_product`(`user_id` ASC, `product_id` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_product_id`(`product_id` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '购物车表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shopping_cart
-- ----------------------------

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `status` tinyint(1) NOT NULL DEFAULT 1,
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `roles` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'ROLE_USER',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$Cp1o7rZOzocUFkGveaH5QuYh9QWEF4epPt3sBQKph5mhJ3MlSwd.S', 'admin@example.com', NULL, 1, '2025-12-24 15:36:59', 'ROLE_ADMIN,ROLE_USER');
INSERT INTO `user` VALUES (2, 'user', '$2a$2a$2a$10$5ab5Qr/maJlKsanDvPu/6.yKvk0YEc.fApLxUw/9hi5TIBSwmMZEe', 'user@example.com', NULL, 1, '2025-12-24 15:36:59', 'ROLE_USER');
INSERT INTO `user` VALUES (3, 'sudo', '123456', NULL, NULL, 1, '2025-12-24 15:50:27', 'ROLE_USER');
INSERT INTO `user` VALUES (4, 'test', '$2a$10$Kih3CM9z8s2Mc.KddtwJUOJx94TU2Yqu43AyITqIDZ9KyNY/J7s2q', '3683579863@qq.com', '17064602953', 1, '2025-12-24 16:55:53', 'ROLE_USER');

SET FOREIGN_KEY_CHECKS = 1;
