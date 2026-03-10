package com.opscopilot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * AI对话响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {
    
    /**
     * 回复内容
     */
    private String content;
    
    /**
     * 会话ID
     */
    private String sessionId;
    
    /**
     * 图表配置
     */
    private Map<String, Object> chart;
    
    /**
     * 表格配置
     */
    private Map<String, Object> table;
    
    /**
     * 数据卡片
     */
    private List<Map<String, Object>> cards;
    
    /**
     * 建议操作
     */
    private List<Suggestion> suggestions;
    
    /**
     * 耗时(ms)
     */
    private Long duration;
    
    /**
     * 时间戳
     */
    private LocalDateTime timestamp;
    
    /**
     * 建议操作
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Suggestion {
        private String label;
        private String action;
        private Map<String, Object> params;
    }
}
