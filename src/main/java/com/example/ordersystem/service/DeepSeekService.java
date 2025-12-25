package com.example.ordersystem.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.example.ordersystem.entity.ApiResponse;
import com.example.ordersystem.entity.ai.*;
import com.example.ordersystem.entity.Product;
import com.example.ordersystem.entity.Order;
import com.example.ordersystem.entity.OrderItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class DeepSeekService {

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com}")
    private String apiUrl;

    @Autowired
    private com.example.ordersystem.mapper.ProductMapper productMapper;

    @Autowired
    private com.example.ordersystem.mapper.OrderMapper orderMapper;

    private final RestTemplate restTemplate = new RestTemplate();

    // 智能商品推荐
    public ApiResponse<List<ProductRecommendation>> recommendProducts(Long userId, String userQuery) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("userId", userId);
            List<Order> userOrders = orderMapper.findByUserId(params);

            List<Product> allProducts = productMapper.findAll(Collections.emptyMap());

            String prompt = buildRecommendationPrompt(userQuery, userOrders, allProducts);
            String aiResponse = callDeepSeekAPI(prompt);

            List<Long> recommendedProductIds = parseProductRecommendations(aiResponse, allProducts);

            List<ProductRecommendation> recommendations = new ArrayList<>();
            for (Long productId : recommendedProductIds) {
                Product product = productMapper.findById(productId);
                if (product != null && product.getStatus() == 1) {
                    ProductRecommendation rec = new ProductRecommendation();
                    rec.setProduct(product);
                    rec.setRecommendationReason(getRecommendationReason(product, userOrders));
                    rec.setMatchScore(calculateMatchScore(product, userQuery, userOrders));
                    recommendations.add(rec);
                }
            }

            recommendations.sort((a, b) -> b.getMatchScore().compareTo(a.getMatchScore()));

            return ApiResponse.success(recommendations);

        } catch (Exception e) {
            e.printStackTrace();
            return getFallbackRecommendations();
        }
    }

    // 定价建议
    public ApiResponse<PricingSuggestion> suggestPricing(Long productId) {
        try {
            Product product = productMapper.findById(productId);
            if (product == null) {
                return ApiResponse.error("商品不存在");
            }

            Map<String, Object> params = new HashMap<>();
            params.put("category", product.getCategory());
            List<Product> similarProducts = productMapper.findAll(params);

            String prompt = buildPricingPrompt(product, similarProducts);
            String aiResponse = callDeepSeekAPI(prompt);

            PricingSuggestion suggestion = parsePricingSuggestion(aiResponse, product, similarProducts);

            return ApiResponse.success(suggestion);

        } catch (Exception e) {
            e.printStackTrace();
            return getFallbackPricingSuggestion(productId);
        }
    }

    // 订单分析
    public ApiResponse<OrderAnalysis> analyzeOrders(Date startDate, Date endDate) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("startTime", startDate);
            params.put("endTime", endDate);
            List<Order> orders = orderMapper.findAll(params);

            String prompt = buildOrderAnalysisPrompt(orders, startDate, endDate);
            String aiResponse = callDeepSeekAPI(prompt);

            OrderAnalysis analysis = parseOrderAnalysis(aiResponse, orders);

            return ApiResponse.success(analysis);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("订单分析失败: " + e.getMessage());
        }
    }

    // 智能客服
    public ApiResponse<CustomerServiceResponse> generateCustomerServiceResponse(String userQuestion, String context) {
        try {
            String prompt = buildCustomerServicePrompt(userQuestion, context);
            String aiResponse = callDeepSeekAPI(prompt);

            CustomerServiceResponse response = parseCustomerServiceResponse(aiResponse);

            return ApiResponse.success(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ApiResponse.error("客服回复生成失败: " + e.getMessage());
        }
    }

    // 直接调用DeepSeek API
    public String callDeepSeekAPI(String prompt) {
        try {
            if (apiKey == null || apiKey.isEmpty() || apiKey.contains("your-api-key")) {
                throw new RuntimeException("DeepSeek API密钥未配置");
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");

            // 创建消息列表 - 修改这里，避免使用Map.of()
            List<Map<String, String>> messages = new ArrayList<>();

            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的电商AI助手，擅长商品推荐、价格分析、销售预测和客户服务。");
            messages.add(systemMessage);

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);

            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 2000);
            requestBody.put("temperature", 0.7);
            requestBody.put("stream", false);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            String url = apiUrl + "/chat/completions";

            ResponseEntity<String> response = restTemplate.exchange(
                    url, HttpMethod.POST, entity, String.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject jsonResponse = JSON.parseObject(response.getBody());
                String content = jsonResponse.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");

                return content;
            } else {
                throw new RuntimeException("DeepSeek API调用失败: " + response.getStatusCode());
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("API调用异常: " + e.getMessage());
        }
    }

    // 以下为辅助方法
    private String buildRecommendationPrompt(String userQuery, List<Order> userOrders, List<Product> allProducts) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("根据以下信息推荐商品：\n\n");

        if (userQuery != null && !userQuery.isEmpty()) {
            prompt.append("用户查询：").append(userQuery).append("\n\n");
        }

        if (userOrders != null && !userOrders.isEmpty()) {
            prompt.append("用户历史订单：\n");
            for (Order order : userOrders) {
                prompt.append("- 订单号：").append(order.getOrderNo())
                        .append("，总金额：").append(order.getTotalAmount())
                        .append("\n");
            }
            prompt.append("\n");
        }

        prompt.append("所有可用商品：\n");
        for (Product product : allProducts) {
            if (product.getStatus() == 1) {
                prompt.append("- ID:").append(product.getId())
                        .append("，名称：").append(product.getProductName())
                        .append("，价格：").append(product.getPrice())
                        .append("，分类：").append(product.getCategory())
                        .append("，库存：").append(product.getStock())
                        .append("\n");
            }
        }

        prompt.append("\n请分析用户需求和历史购买记录，推荐最合适的5个商品ID。");
        prompt.append("返回格式为JSON数组：[{\"productId\": 1}, ...]");

        return prompt.toString();
    }

    private String buildPricingPrompt(Product product, List<Product> similarProducts) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("为以下商品提供定价建议：\n\n");
        prompt.append("当前商品信息：\n");
        prompt.append("- 名称：").append(product.getProductName()).append("\n");
        prompt.append("- 当前价格：").append(product.getPrice()).append("\n");
        prompt.append("- 库存：").append(product.getStock()).append("\n");
        prompt.append("- 分类：").append(product.getCategory()).append("\n");

        prompt.append("\n同类商品价格参考：\n");
        for (Product similar : similarProducts) {
            if (!similar.getId().equals(product.getId())) {
                prompt.append("- ").append(similar.getProductName())
                        .append("：").append(similar.getPrice()).append("\n");
            }
        }

        prompt.append("\n请提供定价建议，包括：\n");
        prompt.append("1. 建议售价范围\n");
        prompt.append("2. 促销价格建议\n");
        prompt.append("3. 定价策略说明\n");

        return prompt.toString();
    }

    private String buildOrderAnalysisPrompt(List<Order> orders, Date startDate, Date endDate) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("分析以下时间段内的订单数据：\n\n");
        prompt.append("分析时段：").append(startDate).append(" 至 ").append(endDate).append("\n");
        prompt.append("订单总数：").append(orders.size()).append("\n\n");

        if (!orders.isEmpty()) {
            prompt.append("订单数据摘要：\n");
            double totalAmount = orders.stream()
                    .mapToDouble(o -> o.getTotalAmount().doubleValue())
                    .sum();
            prompt.append("- 总销售额：").append(totalAmount).append("\n");

            Map<Integer, Long> statusCount = new HashMap<>();
            for (Order order : orders) {
                Integer status = order.getStatus();
                statusCount.put(status, statusCount.getOrDefault(status, 0L) + 1);
            }

            prompt.append("- 订单状态分布：\n");
            for (Map.Entry<Integer, Long> entry : statusCount.entrySet()) {
                prompt.append("  ").append(getStatusText(entry.getKey())).append("：").append(entry.getValue()).append("单\n");
            }
        }

        prompt.append("\n请分析：\n");
        prompt.append("1. 销售趋势\n");
        prompt.append("2. 热门商品\n");
        prompt.append("3. 客户行为模式\n");
        prompt.append("4. 改进建议\n");

        return prompt.toString();
    }

    private String buildCustomerServicePrompt(String userQuestion, String context) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为电商客服，请回复以下客户咨询：\n\n");

        if (context != null && !context.isEmpty()) {
            prompt.append("对话上下文：\n").append(context).append("\n\n");
        }

        prompt.append("客户问题：\n").append(userQuestion).append("\n\n");

        prompt.append("请提供：\n");
        prompt.append("1. 专业、友好的回复\n");
        prompt.append("2. 解决问题的具体建议\n");
        prompt.append("3. 如有需要，提供进一步协助的方案\n");

        return prompt.toString();
    }

    private List<Long> parseProductRecommendations(String aiResponse, List<Product> allProducts) {
        List<Long> productIds = new ArrayList<>();

        try {
            // 简单解析逻辑
            String[] lines = aiResponse.split("\n");
            for (String line : lines) {
                if (line.contains("productId") || line.contains("ID:")) {
                    String[] parts = line.split("[:：]");
                    if (parts.length > 1) {
                        try {
                            Long id = Long.parseLong(parts[1].trim().split("[,\\s}]")[0]);
                            if (!productIds.contains(id)) {
                                productIds.add(id);
                            }
                        } catch (NumberFormatException e) {
                            // 忽略解析错误
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (productIds.isEmpty()) {
            // 使用Java 8兼容的流处理
            List<Product> filteredProducts = new ArrayList<>();
            for (Product p : allProducts) {
                if (p.getStatus() == 1 && p.getStock() > 0) {
                    filteredProducts.add(p);
                }
            }

            // 按库存排序
            filteredProducts.sort((a, b) -> b.getStock() - a.getStock());

            // 取前5个
            for (int i = 0; i < Math.min(5, filteredProducts.size()); i++) {
                productIds.add(filteredProducts.get(i).getId());
            }
        }

        return productIds;
    }

    private PricingSuggestion parsePricingSuggestion(String aiResponse, Product product, List<Product> similarProducts) {
        PricingSuggestion suggestion = new PricingSuggestion();
        suggestion.setProductId(product.getId());
        suggestion.setCurrentPrice(product.getPrice());
        suggestion.setAnalysis(aiResponse);
        suggestion.setGeneratedAt(new Date());

        // 计算平均价格 - Java 8兼容方式
        double totalPrice = 0;
        int count = 0;
        for (Product p : similarProducts) {
            if (!p.getId().equals(product.getId())) {
                totalPrice += p.getPrice().doubleValue();
                count++;
            }
        }
        double avgPrice = count > 0 ? totalPrice / count : product.getPrice().doubleValue();

        suggestion.setMarketAveragePrice(new java.math.BigDecimal(avgPrice));

        return suggestion;
    }

    private OrderAnalysis parseOrderAnalysis(String aiResponse, List<Order> orders) {
        OrderAnalysis analysis = new OrderAnalysis();
        analysis.setAnalysis(aiResponse);
        analysis.setOrderCount(orders.size());
        analysis.setAnalysisDate(new Date());

        double totalAmount = 0;
        for (Order order : orders) {
            totalAmount += order.getTotalAmount().doubleValue();
        }
        analysis.setTotalAmount(new java.math.BigDecimal(totalAmount));

        return analysis;
    }

    private CustomerServiceResponse parseCustomerServiceResponse(String aiResponse) {
        CustomerServiceResponse response = new CustomerServiceResponse();
        response.setResponse(aiResponse);
        response.setGeneratedAt(new Date());
        response.setConfidence(0.9);
        return response;
    }

    private ApiResponse<List<ProductRecommendation>> getFallbackRecommendations() {
        List<Product> products = productMapper.findAll(Collections.emptyMap());

        // 使用Java 8兼容方式
        List<ProductRecommendation> recommendations = new ArrayList<>();
        int count = 0;
        for (Product product : products) {
            if (product.getStatus() == 1 && product.getStock() > 0 && count < 5) {
                ProductRecommendation rec = new ProductRecommendation();
                rec.setProduct(product);
                rec.setRecommendationReason("热门商品推荐");
                rec.setMatchScore(0.8);
                recommendations.add(rec);
                count++;
            }
        }

        return ApiResponse.success(recommendations);
    }

    private ApiResponse<PricingSuggestion> getFallbackPricingSuggestion(Long productId) {
        Product product = productMapper.findById(productId);
        if (product == null) {
            return ApiResponse.error("商品不存在");
        }

        PricingSuggestion suggestion = new PricingSuggestion();
        suggestion.setProductId(productId);
        suggestion.setCurrentPrice(product.getPrice());
        suggestion.setAnalysis("基于市场平均价格的建议");
        suggestion.setGeneratedAt(new Date());

        return ApiResponse.success(suggestion);
    }

    private String getStatusText(Integer status) {
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(0, "待付款");
        statusMap.put(1, "已付款");
        statusMap.put(2, "已发货");
        statusMap.put(3, "已完成");
        statusMap.put(4, "已取消");
        return statusMap.getOrDefault(status, "未知");
    }

    private String getRecommendationReason(Product product, List<Order> userOrders) {
        if (userOrders != null && !userOrders.isEmpty()) {
            return "根据您的购买历史推荐";
        } else if (product.getPrice().compareTo(new java.math.BigDecimal("1000")) < 0) {
            return "性价比高的热门商品";
        } else {
            return "优质商品推荐";
        }
    }

    private Double calculateMatchScore(Product product, String userQuery, List<Order> userOrders) {
        double score = 0.5;

        if (userQuery != null && product.getProductName().contains(userQuery)) {
            score += 0.3;
        }

        if (userOrders != null && !userOrders.isEmpty()) {
            score += 0.2;
        }

        if (product.getStock() > 10) {
            score += 0.1;
        }

        return Math.min(score, 1.0);
    }
}