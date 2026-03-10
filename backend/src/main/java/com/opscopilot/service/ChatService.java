package com.opscopilot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opscopilot.dto.*;
import com.opscopilot.model.Alert;
import com.opscopilot.model.Asset;
import com.opscopilot.model.Knowledge;
import com.opscopilot.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * AI对话服务 - 智能体核心
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {
    
    @Value("${ops-copilot.version:MVP}")
    private String version;
    
    private final OllamaService ollamaService;
    private final AlertService alertService;
    private final AssetService assetService;
    private final TicketService ticketService;
    private final KnowledgeService knowledgeService;
    
    // 简单会话历史 (生产环境应使用数据库/Redis)
    private final Map<String, List<String>> sessionHistory = new HashMap<>();
    
    /**
     * 处理用户消息
     */
    public Result<ChatResponse> chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        String message = request.getMessage();
        String sessionId = request.getSessionId() != null ? request.getSessionId() : "default";
        
        log.info("收到用户消息: {}", message);
        
        // 获取会话历史
        List<String> history = sessionHistory.computeIfAbsent(sessionId, k -> new ArrayList<>());
        
        // 简单意图识别
        String intent = recognizeIntent(message);
        log.info("识别意图: {}", intent);
        
        String aiResponse;
        
        // 根据意图选择处理方式
        if (isSimpleQuery(message)) {
            // 简单查询直接调用AI
            history.add(message);
            aiResponse = ollamaService.chat(message, history);
        } else {
            // 结构化查询先获取数据，再让AI总结
            aiResponse = handleStructuredIntent(intent, message);
        }
        
        history.add(message);
        
        ChatResponse response = ChatResponse.builder()
                .content(aiResponse)
                .sessionId(sessionId)
                .timestamp(LocalDateTime.now())
                .duration(System.currentTimeMillis() - startTime)
                .build();
        
        return Result.success(response);
    }
    
    /**
     * 判断是否为简单查询
     */
    private boolean isSimpleQuery(String message) {
        String lower = message.toLowerCase();
        return lower.contains("怎么") || lower.contains("如何") || lower.contains("是什么") 
                || lower.contains("为什么") || lower.contains("?") || lower.contains("帮我")
                || lower.contains("给我") || lower.contains("看看");
    }
    
    /**
     * 处理结构化意图
     */
    private String handleStructuredIntent(String intent, String message) {
        switch (intent) {
            case "alert_query":
                return handleAlertQuery(message);
            case "asset_query":
                return handleAssetQuery(message);
            case "ticket_query":
                return handleTicketQuery(message);
            case "statistics":
                return handleStatistics(message);
            case "knowledge_query":
                return handleKnowledgeQuery(message);
            default:
                // 其他情况调用AI
                return ollamaService.chat(message, new ArrayList<>());
        }
    }
    
    /**
     * 意图识别
     */
    private String recognizeIntent(String message) {
        String lowerMsg = message.toLowerCase();
        
        // 告警相关
        if (lowerMsg.contains("告警") || lowerMsg.contains("alert") || lowerMsg.contains("报警")) {
            if (lowerMsg.contains("分析") || lowerMsg.contains("原因") || lowerMsg.contains("怎么回事")) {
                return "alert_analysis";
            }
            return "alert_query";
        }
        
        // 资产相关
        if (lowerMsg.contains("资产") || lowerMsg.contains("服务器") || lowerMsg.contains("ip") 
                || lowerMsg.contains("机器") || lowerMsg.contains("数据库")) {
            return "asset_query";
        }
        
        // 工单相关
        if (lowerMsg.contains("工单") || lowerMsg.contains("申请") || lowerMsg.contains("ticket")) {
            if (lowerMsg.contains("创建") || lowerMsg.contains("提交") || lowerMsg.contains("帮我")) {
                return "ticket_create";
            }
            return "ticket_query";
        }
        
        // 知识库相关
        if (lowerMsg.contains("怎么") || lowerMsg.contains("如何") || lowerMsg.contains("方法")
                || lowerMsg.contains("解决") || lowerMsg.contains("?") || lowerMsg.contains("吗")) {
            return "knowledge_query";
        }
        
        // 统计相关
        if (lowerMsg.contains("统计") || lowerMsg.contains("报表") || lowerMsg.contains("周报")
                || lowerMsg.contains("数量") || lowerMsg.contains("多少")) {
            return "statistics";
        }
        
        return "general";
    }
    
    /**
     * 处理告警查询
     */
    private String handleAlertQuery(String message) {
        AlertQueryRequest request = new AlertQueryRequest();
        request.setPage(1);
        request.setPageSize(5);
        
        Result<Page<Alert>> result = alertService.query(request);
        Page<Alert> page = result.getData();
        
        StringBuilder content = new StringBuilder();
        content.append("📊 告警查询结果：\n\n");
        content.append("共找到 ").append(page.getTotal()).append(" 条告警\n\n");
        
        for (Alert alert : page.getRecords()) {
            content.append("• **").append(alert.getTitle()).append("**\n");
            content.append("  级别: ").append(alert.getLevel()).append(" | ");
            content.append("IP: ").append(alert.getIp()).append(" | ");
            content.append("状态: ").append(alert.getStatus()).append("\n\n");
        }
        
        content.append("需要我帮你分析具体某个告警吗？");
        
        return content.toString();
    }
    
    /**
     * 处理资产查询
     */
    private String handleAssetQuery(String message) {
        AssetQueryRequest request = new AssetQueryRequest();
        request.setPage(1);
        request.setPageSize(10);
        
        // 简单解析
        if (message.contains("MySQL") || message.contains("mysql") || message.contains("数据库")) {
            request.setType("DATABASE");
        } else if (message.contains("服务器") || message.contains("server")) {
            request.setType("SERVER");
        }
        
        Result<Page<Asset>> result = assetService.query(request);
        Page<Asset> page = result.getData();
        
        StringBuilder content = new StringBuilder();
        content.append("💻 资产查询结果：\n\n");
        content.append("共找到 ").append(page.getTotal()).append(" 台资产\n\n");
        
        content.append("| 名称 | IP | 类型 | 业务 | 状态 | 负责人 |\n");
        content.append("|------|-----|------|------|------|--------|\n");
        
        for (Asset asset : page.getRecords()) {
            content.append("| ").append(asset.getName()).append(" | ");
            content.append(asset.getIp()).append(" | ");
            content.append(asset.getType()).append(" | ");
            content.append(asset.getBusiness()).append(" | ");
            content.append(asset.getStatus()).append(" | ");
            content.append(asset.getOwnerName()).append(" |\n");
        }
        
        return content.toString();
    }
    
    /**
     * 处理工单查询
     */
    private String handleTicketQuery(String message) {
        Result<Page<Ticket>> result = ticketService.query(null, null, null, 1, 5);
        Page<Ticket> page = result.getData();
        
        StringBuilder content = new StringBuilder();
        content.append("📋 你的工单列表：\n\n");
        
        for (Ticket ticket : page.getRecords()) {
            content.append("**").append(ticket.getTicketNo()).append("** ");
            content.append(ticket.getTitle()).append("\n");
            content.append("  状态: ").append(ticket.getStatus()).append(" | ");
            content.append("类型: ").append(ticket.getType()).append("\n\n");
        }
        
        return content.toString();
    }
    
    /**
     * 处理统计查询
     */
    private String handleStatistics(String message) {
        Result<Map<String, Object>> alertStats = alertService.getStatistics();
        Result<Map<String, Object>> ticketStats = ticketService.getStatistics();
        
        return """
                📊 运维数据统计：

                **告警统计**
                - 总告警: %d
                - 严重告警: %d
                - 已解决: %d

                **工单统计**
                - 总工单: %d
                - 处理中: %d
                - 平均处理时长: %s
                """.formatted(
                (Integer) alertStats.getData().get("total"),
                (Integer) alertStats.getData().get("critical"),
                (Integer) alertStats.getData().get("resolved"),
                (Integer) ticketStats.getData().get("total"),
                (Integer) ticketStats.getData().get("processing"),
                ticketStats.getData().get("avgResolveTime")
        );
    }
    
    /**
     * 处理知识库查询
     */
    private String handleKnowledgeQuery(String message) {
        String keyword = message.replace("怎么", "").replace("如何", "").replace("?", "").trim();
        
        Result<List<Knowledge>> result = knowledgeService.search(keyword);
        
        StringBuilder content = new StringBuilder();
        
        if (result.getData() != null && !result.getData().isEmpty()) {
            content.append("📚 找到以下相关内容：\n\n");
            
            for (Knowledge knowledge : result.getData()) {
                content.append("**").append(knowledge.getTitle()).append("**\n");
                String preview = knowledge.getContent();
                if (preview != null && preview.length() > 100) {
                    preview = preview.substring(0, 100) + "...";
                }
                content.append(preview).append("\n\n");
            }
        } else {
            content.append("抱歉，我没有找到相关内容。\n");
            content.append("你可以尝试联系运维团队获取帮助。\n");
        }
        
        return content.toString();
    }
}
