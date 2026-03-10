package com.opscopilot.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * AI对话请求
 */
@Data
public class ChatRequest {
    
    /**
     * 用户消息
     */
    @NotBlank(message = "消息内容不能为空")
    private String message;
    
    /**
     * 会话ID (可选)
     */
    private String sessionId;
    
    /**
     * 上下文消息历史
     */
    private List<ChatMessage> history;
    
    /**
     * 附加参数
     */
    private Map<String, Object> params;
    
    /**
     * 聊天消息
     */
    @Data
    public static class ChatMessage {
        private String role;
        private String content;
    }
}
