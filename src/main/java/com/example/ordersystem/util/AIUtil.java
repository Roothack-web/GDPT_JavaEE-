package com.example.ordersystem.util;

import org.springframework.stereotype.Component;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Component
public class AIUtil {
    
    /**
     * 验证API密钥格式
     */
    public static boolean isValidApiKey(String apiKey) {
        if (apiKey == null || apiKey.isEmpty()) {
            return false;
        }
        
        // DeepSeek API密钥通常以"sk-"开头
        return apiKey.startsWith("sk-") && apiKey.length() > 20;
    }
    
    /**
     * 格式化AI响应
     */
    public static String formatAIResponse(String response) {
        if (response == null || response.isEmpty()) {
            return "暂无响应";
        }
        
        // 清理响应文本
        String formatted = response
            .replaceAll("```(?:json)?", "")
            .replaceAll("\\*\\*", "")
            .replaceAll("\\*", "• ")
            .trim();
        
        // 确保段落之间有合适的间距
        formatted = formatted.replaceAll("\n{3,}", "\n\n");
        
        return formatted;
    }
    
    /**
     * 从文本中提取商品ID
     */
    public static Long extractProductIdFromText(String text) {
        if (text == null || text.isEmpty()) {
            return null;
        }
        
        // 多种可能的格式
        Pattern[] patterns = {
            Pattern.compile("ID[：:]\\s*(\\d+)"),
            Pattern.compile("商品ID[：:]\\s*(\\d+)"),
            Pattern.compile("product[\\s_]*id[：:]\\s*(\\d+)", Pattern.CASE_INSENSITIVE)
        };
        
        for (Pattern pattern : patterns) {
            Matcher matcher = pattern.matcher(text);
            if (matcher.find()) {
                try {
                    return Long.parseLong(matcher.group(1).trim());
                } catch (NumberFormatException e) {
                    // 忽略解析错误
                }
            }
        }
        
        return null;
    }
}
