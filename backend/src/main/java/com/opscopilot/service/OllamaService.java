package com.opscopilot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Ollama AI 服务
 */
@Slf4j
@Service
public class OllamaService {

    @Value("${ollama.base-url:http://localhost:11434}")
    private String baseUrl;

    @Value("${ollama.model:qwen3.5:9B}")
    private String model;

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * 生成对话回复
     */
    public String chat(String userMessage, List<String> history) {
        String systemPrompt = """
                你是一个专业的IT运维智能助手，名字叫OpsCopilot。
                你可以帮助用户：
                - 查询和分析告警信息
                - 搜索和管理资产信息
                - 创建和处理工单
                - 回答运维知识问题
                - 生成运维报表
                
                请用专业、友好的方式回答问题。如果需要执行操作，请告知用户需要确认。
                """;

        try {
            // 构建消息列表
            List<Message> messages = new ArrayList<>();
            messages.add(new Message("system", systemPrompt));
            
            // 添加历史记录
            for (String h : history) {
                messages.add(new Message("user", h));
            }
            
            // 添加当前消息
            messages.add(new Message("user", userMessage));

            // 构建请求
            ChatRequest request = new ChatRequest(model, messages);
            String json = mapper.writeValueAsString(request);

            HttpUrl url = HttpUrl.parse(baseUrl + "/api/chat");
            Request httpRequest = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(json, MediaType.parse("application/json")))
                    .build();

            // 发送请求
            try (Response response = client.newCall(httpRequest).execute()) {
                if (!response.isSuccessful()) {
                    log.error("Ollama API error: {}", response);
                    return "抱歉，AI服务暂时不可用。";
                }

                String responseBody = response.body().string();
                JsonNode root = mapper.readTree(responseBody);
                String content = root.path("message").path("content").asText("");
                
                return content.isEmpty() ? "抱歉，我没有得到有效的回复。" : content;
            }
        } catch (IOException e) {
            log.error("Ollama API call failed: {}", e.getMessage());
            return "抱歉，连接AI服务失败：" + e.getMessage();
        }
    }

    // 请求和响应类
    public static class ChatRequest {
        public String model;
        public List<Message> messages;
        public boolean stream = false;

        public ChatRequest(String model, List<Message> messages) {
            this.model = model;
            this.messages = messages;
        }
    }

    public static class Message {
        public String role;
        public String content;

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }
    }
}
