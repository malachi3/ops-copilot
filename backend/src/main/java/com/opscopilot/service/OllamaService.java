package com.opscopilot.service;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Ollama AI 服务 - 优化版
 */
@Slf4j
@Service
public class OllamaService {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:qwen3.5:9B}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(180, TimeUnit.SECONDS)  // 增加到3分钟
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    /**
     * 生成对话回复 - 带重试
     */
    public String chat(String userMessage) {
        int maxRetries = 2;
        String lastError = null;
        
        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                log.info("调用Ollama (尝试 {}/{}): {}", attempt, maxRetries, model);
                String response = callOllama(userMessage);
                if (response != null && !response.isEmpty()) {
                    return response;
                }
            } catch (Exception e) {
                lastError = e.getMessage();
                log.warn("Ollama调用失败 (尝试 {}): {}", attempt, e.getMessage());
                if (attempt < maxRetries) {
                    try {
                        Thread.sleep(2000); // 等待2秒重试
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        
        // 返回友好错误信息
        return "抱歉，AI服务响应较慢或暂时不可用。请稍后重试，或尝试查询具体数据（如告警、资产等）。\n\n" +
               "如需紧急帮助，可以直接点击左侧菜单查看各模块数据。";
    }

    private String callOllama(String userMessage) throws IOException {
        String prompt = buildPrompt(userMessage);
        
        String json = String.format(
            "{\"model\":\"%s\",\"prompt\":\"%s\",\"stream\":false,\"options\":{\"temperature\":0.7,\"num_predict\":500}}",
            model, prompt.replace("\"", "\\\"")
        );

        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url(baseUrl + "/api/generate")
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                log.error("Ollama返回错误: {}", response);
                return null;
            }

            String responseBody = response.body().string();
            
            // 解析简单响应
            try {
                com.fasterxml.jackson.databind.JsonNode root = 
                    new com.fasterxml.jackson.databind.ObjectMapper().readTree(responseBody);
                return root.path("response").asText("");
            } catch (Exception e) {
                log.error("解析Ollama响应失败: {}", e.getMessage());
                return null;
            }
        }
    }

    private String buildPrompt(String userMessage) {
        return """
                你是一个专业的IT运维智能助手，名字叫OpsCopilot。
                请用简洁、专业的语言回答用户问题。
                如果用户询问具体数据（如告警、资产、工单），请告知需要通过对应功能查询。
                
                用户问题: %s
                
                回答:
                """.formatted(userMessage);
    }
}
