package com.opscopilot.service;

import com.opscopilot.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
    
    private final AlertService alertService;
    private final AssetService assetService;
    private final TicketService ticketService;
    private final KnowledgeService knowledgeService;
    
    /**
     * 处理用户消息
     */
    public Result<ChatResponse> chat(ChatRequest request) {
        long startTime = System.currentTimeMillis();
        String message = request.getMessage();
        
        log.info("收到用户消息: {}", message);
        
        // 意图识别
        String intent = recognizeIntent(message);
        log.info("识别意图: {}", intent);
        
        // 根据意图处理
        ChatResponse response = switch (intent) {
            case "alert_query" -> handleAlertQuery(message);
            case "alert_analysis" -> handleAlertAnalysis(message);
            case "asset_query" -> handleAssetQuery(message);
            case "ticket_create" -> handleTicketCreate(message);
            case "ticket_query" -> handleTicketQuery(message);
            case "knowledge_query" -> handleKnowledgeQuery(message);
            case "statistics" -> handleStatistics(message);
            default -> handleGeneralQuestion(message);
        };
        
        response.setDuration(System.currentTimeMillis() - startTime);
        response.setTimestamp(LocalDateTime.now());
        
        return Result.success(response);
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
    private ChatResponse handleAlertQuery(String message) {
        AlertQueryRequest request = new AlertQueryRequest();
        request.setPage(1);
        request.setPageSize(5);
        
        // 解析时间
        if (message.contains("最近") || message.contains("刚才")) {
            request.setStartTime(LocalDateTime.now().minusHours(1));
        }
        
        // 解析级别
        if (message.contains("严重") || message.contains("critical")) {
            request.setLevel("CRITICAL");
        }
        
        Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Alert>> result = alertService.query(request);
        
        StringBuilder content = new StringBuilder();
        content.append("📊 告警查询结果：\n\n");
        
        var page = result.getData();
        content.append("共找到 ").append(page.getTotal()).append(" 条告警\n\n");
        
        for (var alert : page.getRecords()) {
            content.append("• **").append(alert.getTitle()).append("**\n");
            content.append("  级别: ").append(alert.getLevel()).append(" | ");
            content.append("IP: ").append(alert.getIp()).append(" | ");
            content.append("状态: ").append(alert.getStatus()).append("\n");
            content.append("  时间: ").append(alert.getAlertTime()).append("\n\n");
        }
        
        content.append("需要我帮你分析具体某个告警吗？");
        
        return ChatResponse.builder()
                .content(content.toString())
                .sessionId(UUID.randomUUID().toString())
                .suggestions(List.of(
                        ChatResponse.Suggestion.builder()
                                .label("查看告警详情")
                                .action("alert_detail")
                                .params(Map.of("id", 1))
                                .build()
                ))
                .build();
    }
    
    /**
     * 处理告警分析
     */
    private ChatResponse handleAlertAnalysis(String message) {
        String content = """
                🔍 告警分析结果：

                **服务器**: web-server-01
                **告警**: CPU使用率 92%
                **持续时间**: 12分钟

                **可能原因**:
                1. 促销活动流量激增（当前流量是平时3倍）
                2. 可能有慢查询导致CPU占用高
                3. 连接数过多

                **建议操作**:
                1. ✅ 扩容1台服务器（推荐）
                2. ✅ 开启限流策略
                3. ⏸️ 创建故障工单跟踪

                需要我帮你创建故障工单吗？
                """;
        
        return ChatResponse.builder()
                .content(content)
                .sessionId(UUID.randomUUID().toString())
                .suggestions(List.of(
                        ChatResponse.Suggestion.builder()
                                .label("创建故障工单")
                                .action("ticket_create")
                                .params(Map.of("type", "INCIDENT"))
                                .build()
                ))
                .build();
    }
    
    /**
     * 处理资产查询
     */
    private ChatResponse handleAssetQuery(String message) {
        AssetQueryRequest request = new AssetQueryRequest();
        request.setPage(1);
        request.setPageSize(10);
        
        // 解析查询条件
        if (message.contains("MySQL") || message.contains("mysql") || message.contains("数据库")) {
            request.setType("DATABASE");
        } else if (message.contains("服务器") || message.contains("server")) {
            request.setType("SERVER");
        }
        
        if (message.contains("运行") || message.contains("running")) {
            request.setStatus("RUNNING");
        }
        
        // 提取关键词
        if (message.contains("订单")) {
            request.setBusiness("订单服务");
        }
        
        Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Asset>> result = assetService.query(request);
        
        StringBuilder content = new StringBuilder();
        content.append("💻 资产查询结果：\n\n");
        
        var page = result.getData();
        content.append("共找到 ").append(page.getTotal()).append(" 台资产\n\n");
        
        content.append("| 名称 | IP | 类型 | 业务 | 状态 | 负责人 |\n");
        content.append("|------|-----|------|------|------|--------|\n");
        
        for (var asset : page.getRecords()) {
            content.append("| ").append(asset.getName()).append(" | ");
            content.append(asset.getIp()).append(" | ");
            content.append(asset.getType()).append(" | ");
            content.append(asset.getBusiness()).append(" | ");
            content.append(asset.getStatus()).append(" | ");
            content.append(asset.getOwnerName()).append(" |\n");
        }
        
        content.append("\n需要查看某个资产的详细信息吗？");
        
        return ChatResponse.builder()
                .content(content.toString())
                .sessionId(UUID.randomUUID().toString())
                .build();
    }
    
    /**
     * 处理工单创建
     */
    private ChatResponse handleTicketCreate(String message) {
        String content = """
                📝 好的，我来帮你创建工单。

                请确认以下信息：
                1. **工单类型**: 故障处理
                2. **标题**: 订单服务CPU告警处理
                3. **优先级**: 高

                确认提交吗？（回复"确认"或"取消"）
                """;
        
        return ChatResponse.builder()
                .content(content)
                .sessionId(UUID.randomUUID().toString())
                .suggestions(List.of(
                        ChatResponse.Suggestion.builder()
                                .label("确认提交")
                                .action("ticket_confirm")
                                .build(),
                        ChatResponse.Suggestion.builder()
                                .label("取消")
                                .action("ticket_cancel")
                                .build()
                ))
                .build();
    }
    
    /**
     * 处理工单查询
     */
    private ChatResponse handleTicketQuery(String message) {
        Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Ticket>> result = 
                ticketService.query(null, null, null, 1, 5);
        
        StringBuilder content = new StringBuilder();
        content.append("📋 你的工单列表：\n\n");
        
        var page = result.getData();
        
        for (var ticket : page.getRecords()) {
            content.append("**").append(ticket.getTicketNo()).append("** ");
            content.append(ticket.getTitle()).append("\n");
            content.append("  状态: ").append(ticket.getStatus()).append(" | ");
            content.append("类型: ").append(ticket.getType()).append(" | ");
            content.append("优先级: ").append(ticket.getPriority()).append("\n");
            content.append("  创建时间: ").append(ticket.getCreateTime()).append("\n\n");
        }
        
        return ChatResponse.builder()
                .content(content.toString())
                .sessionId(UUID.randomUUID().toString())
                .build();
    }
    
    /**
     * 处理知识库查询
     */
    private ChatResponse handleKnowledgeQuery(String message) {
        // 提取关键词
        String keyword = message.replace("怎么", "").replace("如何", "").replace("?", "").trim();
        
        Result<List<com.opscopilot.model.Knowledge>> result = knowledgeService.search(keyword);
        
        StringBuilder content = new StringBuilder();
        
        if (result.getData() != null && !result.getData().isEmpty()) {
            content.append("📚 找到以下相关内容：\n\n");
            
            for (var knowledge : result.getData()) {
                content.append("**").append(knowledge.getTitle()).append("**\n");
                content.append(knowledge.getContent().substring(0, Math.min(200, knowledge.getContent().length())));
                content.append("...\n\n");
            }
        } else {
            content.append("抱歉，我没有找到相关内容。\n");
            content.append("你可以尝试：\n");
            content.append("1. 调整搜索关键词\n");
            content.append("2. 联系运维团队获取帮助\n");
        }
        
        return ChatResponse.builder()
                .content(content.toString())
                .sessionId(UUID.randomUUID().toString())
                .build();
    }
    
    /**
     * 处理统计查询
     */
    private ChatResponse handleStatistics(String message) {
        Result<Map<String, Object>> alertStats = alertService.getStatistics();
        Result<Map<String, Object>> ticketStats = ticketService.getStatistics();
        
        String content = """
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
        
        return ChatResponse.builder()
                .content(content)
                .sessionId(UUID.randomUUID().toString())
                .build();
    }
    
    /**
     * 处理通用问题
     */
    private ChatResponse handleGeneralQuestion(String message) {
        String content = """
                👋 你好！我是 OpsCopilot 运维智能体 🤖

                我可以帮你：
                - 🔍 **查询告警** - "最近有哪些严重告警？"
                - 💻 **查询资产** - "给我们部门所有MySQL服务器"
                - 📝 **创建工单** - "帮我申请开通数据库权限"
                - 📋 **查询工单** - "我的工单到哪了？"
                - 📚 **知识问答** - "Redis连接数满了怎么解决？"
                - 📊 **数据统计** - "本周告警数量多少？"

                有什么可以帮你的？
                """;
        
        return ChatResponse.builder()
                .content(content)
                .sessionId(UUID.randomUUID().toString())
                .build();
    }
}
