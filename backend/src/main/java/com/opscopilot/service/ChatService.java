package com.opscopilot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    
    private final ObjectMapper objectMapper = new ObjectMapper();
    
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
        
        // 根据意图处理
        ChatResponse response = handleIntent(intent, message, history);
        
        response.setSessionId(sessionId);
        response.setTimestamp(LocalDateTime.now());
        response.setDuration(System.currentTimeMillis() - startTime);
        
        return Result.success(response);
    }
    
    /**
     * 识别意图
     */
    private String recognizeIntent(String message) {
        String lowerMsg = message.toLowerCase();
        
        // 告警相关
        if (lowerMsg.contains("告警") || lowerMsg.contains("alert") || lowerMsg.contains("报警")) {
            if (lowerMsg.contains("分析") || lowerMsg.contains("原因") || lowerMsg.contains("怎么回事")) {
                return "alert_analysis";
            }
            if (lowerMsg.contains("统计") || lowerMsg.contains("趋势") || lowerMsg.contains("图表")) {
                return "alert_stats";
            }
            return "alert_query";
        }
        
        // 资产相关
        if (lowerMsg.contains("资产") || lowerMsg.contains("服务器") || lowerMsg.contains("ip") 
                || lowerMsg.contains("机器") || lowerMsg.contains("数据库")) {
            if (lowerMsg.contains("统计") || lowerMsg.contains("分布") || lowerMsg.contains("图表")) {
                return "asset_stats";
            }
            return "asset_query";
        }
        
        // 工单相关
        if (lowerMsg.contains("工单") || lowerMsg.contains("申请") || lowerMsg.contains("ticket")) {
            if (lowerMsg.contains("创建") || lowerMsg.contains("提交") || lowerMsg.contains("帮我")) {
                return "ticket_create";
            }
            if (lowerMsg.contains("统计") || lowerMsg.contains("报表")) {
                return "ticket_stats";
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
                || lowerMsg.contains("数量") || lowerMsg.contains("多少") || lowerMsg.contains(" overview")) {
            return "overall_stats";
        }
        
        return "general";
    }
    
    /**
     * 处理意图
     */
    private ChatResponse handleIntent(String intent, String message, List<String> history) {
        return switch (intent) {
            case "alert_query" -> handleAlertQuery(message);
            case "alert_analysis" -> handleAlertAnalysis(message);
            case "alert_stats" -> handleAlertStats(message);
            case "asset_query" -> handleAssetQuery(message);
            case "asset_stats" -> handleAssetStats(message);
            case "ticket_query" -> handleTicketQuery(message);
            case "ticket_stats" -> handleTicketStats(message);
            case "knowledge_query" -> handleKnowledgeQuery(message);
            case "overall_stats" -> handleOverallStats(message);
            default -> handleGeneralQuestion(message, history);
        };
    }
    
    /**
     * 处理告警查询
     */
    private ChatResponse handleAlertQuery(String message) {
        AlertQueryRequest request = new AlertQueryRequest();
        request.setPage(1);
        request.setPageSize(5);
        
        // 解析筛选条件
        if (message.contains("严重") || message.contains("critical")) {
            request.setLevel("CRITICAL");
        }
        
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
        
        // 构建表格数据
        Map<String, Object> table = new HashMap<>();
        table.put("columns", List.of(
                Map.of("prop", "title", "label", "告警标题"),
                Map.of("prop", "level", "label", "级别"),
                Map.of("prop", "ip", "label", "IP"),
                Map.of("prop", "status", "label", "状态")
        ));
        
        List<Map<String, Object>> tableData = new ArrayList<>();
        for (Alert alert : page.getRecords()) {
            tableData.add(Map.of(
                    "title", alert.getTitle(),
                    "level", alert.getLevel(),
                    "ip", alert.getIp() != null ? alert.getIp() : "-",
                    "status", alert.getStatus()
            ));
        }
        table.put("data", tableData);
        
        return ChatResponse.builder()
                .content(content.toString())
                .table(table)
                .build();
    }
    
    /**
     * 处理告警统计（带图表）
     */
    private ChatResponse handleAlertStats(String message) {
        Result<Map<String, Object>> stats = alertService.getStatistics();
        Map<String, Object> data = stats.getData();
        
        StringBuilder content = new StringBuilder();
        content.append("📊 告警统计分析：\n\n");
        content.append("| 指标 | 数值 |\n");
        content.append("|------|------|\n");
        content.append("| 总告警 | ").append(data.get("total")).append(" |\n");
        content.append("| 严重告警 | ").append(data.get("critical")).append(" |\n");
        content.append("| 警告告警 | ").append(data.get("warning")).append(" |\n");
        content.append("| 已解决 | ").append(data.get("resolved")).append(" |\n");
        
        // 构建图表数据
        Map<String, Object> chart = new HashMap<>();
        chart.put("type", "bar");
        chart.put("xData", List.of("严重", "警告", "信息", "已解决"));
        chart.put("series", List.of(
                Map.of("name", "告警数量", "data", List.of(12, 56, 1166, 1166), "color", "#F56C6C")
        ));
        
        // 数据卡片
        List<Map<String, Object>> cards = List.of(
                Map.of("value", data.get("total"), "label", "总告警"),
                Map.of("value", data.get("critical"), "label", "严重"),
                Map.of("value", data.get("warning"), "label", "警告"),
                Map.of("value", data.get("resolved"), "label", "已解决")
        );
        
        return ChatResponse.builder()
                .content(content.toString())
                .chart(chart)
                .cards(cards)
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
                .build();
    }
    
    /**
     * 处理资产查询
     */
    private ChatResponse handleAssetQuery(String message) {
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
            content.append(asset.getIp() != null ? asset.getIp() : "-").append(" | ");
            content.append(asset.getType()).append(" | ");
            content.append(asset.getBusiness() != null ? asset.getBusiness() : "-").append(" | ");
            content.append(asset.getStatus()).append(" | ");
            content.append(asset.getOwnerName() != null ? asset.getOwnerName() : "-").append(" |\n");
        }
        
        return ChatResponse.builder()
                .content(content.toString())
                .build();
    }
    
    /**
     * 处理资产统计（带图表）
     */
    private ChatResponse handleAssetStats(String message) {
        Result<Object> stats = assetService.getStatistics();
        
        StringBuilder content = new StringBuilder();
        content.append("💻 资产统计分析：\n\n");
        
        // 构建饼图
        Map<String, Object> chart = new HashMap<>();
        chart.put("type", "pie");
        chart.put("data", List.of(
                Map.of("value", 50, "name", "服务器", "color", "#409EFF"),
                Map.of("value", 30, "name", "数据库", "color", "#67C23A"),
                Map.of("value", 25, "name", "中间件", "color", "#E6A23C"),
                Map.of("value", 20, "name", "网络设备", "color", "#909399"),
                Map.of("value", 25, "name", "存储", "color", "#F56C6C")
        ));
        
        return ChatResponse.builder()
                .content(content.toString())
                .chart(chart)
                .build();
    }
    
    /**
     * 处理工单查询
     */
    private ChatResponse handleTicketQuery(String message) {
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
        
        return ChatResponse.builder()
                .content(content.toString())
                .build();
    }
    
    /**
     * 处理工单统计（带图表）
     */
    private ChatResponse handleTicketStats(String message) {
        Result<Map<String, Object>> stats = ticketService.getStatistics();
        Map<String, Object> data = stats.getData();
        
        StringBuilder content = new StringBuilder();
        content.append("📋 工单统计分析：\n\n");
        content.append("| 指标 | 数值 |\n");
        content.append("|------|------|\n");
        content.append("| 总工单 | ").append(data.get("total")).append(" |\n");
        content.append("| 待处理 | ").append(data.get("pending")).append(" |\n");
        content.append("| 处理中 | ").append(data.get("processing")).append(" |\n");
        content.append("| 已关闭 | ").append(data.get("closed")).append(" |\n");
        content.append("| 平均处理时长 | ").append(data.get("avgResolveTime")).append(" |\n");
        
        // 构建横向柱状图
        Map<String, Object> chart = new HashMap<>();
        chart.put("type", "bar");
        chart.put("xData", List.of("已关闭", "处理中", "待处理"));
        chart.put("series", List.of(
                Map.of("name", "工单数", "data", List.of(72, 5, 12), "color", "#67C23A")
        ));
        
        return ChatResponse.builder()
                .content(content.toString())
                .chart(chart)
                .build();
    }
    
    /**
     * 处理知识库查询
     */
    private ChatResponse handleKnowledgeQuery(String message) {
        String keyword = message.replace("怎么", "").replace("如何", "").replace("?", "").trim();
        
        Result<List<Knowledge>> result = knowledgeService.search(keyword);
        
        StringBuilder content = new StringBuilder();
        
        if (result.getData() != null && !result.getData().isEmpty()) {
            content.append("📚 找到以下相关内容：\n\n");
            
            for (Knowledge knowledge : result.getData()) {
                content.append("**").append(knowledge.getTitle()).append("**\n");
                String preview = knowledge.getContent();
                if (preview != null && preview.length() > 150) {
                    preview = preview.substring(0, 150) + "...";
                }
                content.append(preview).append("\n\n");
            }
        } else {
            content.append("抱歉，我没有找到相关内容。\n");
            content.append("你可以尝试调整搜索关键词，或联系运维团队获取帮助。\n");
        }
        
        return ChatResponse.builder()
                .content(content.toString())
                .build();
    }
    
    /**
     * 处理整体统计
     */
    private ChatResponse handleOverallStats(String message) {
        Result<Map<String, Object>> alertStats = alertService.getStatistics();
        Result<Map<String, Object>> ticketStats = ticketService.getStatistics();
        Result<Object> assetStats = assetService.getStatistics();
        
        StringBuilder content = new StringBuilder();
        content.append("📊 运维整体态势\n\n");
        
        content.append("**告警统计**\n");
        content.append("- 总告警: ").append(alertStats.getData().get("total")).append("\n");
        content.append("- 严重告警: ").append(alertStats.getData().get("critical")).append("\n");
        content.append("- 已解决: ").append(alertStats.getData().get("resolved")).append("\n\n");
        
        content.append("**工单统计**\n");
        content.append("- 总工单: ").append(ticketStats.getData().get("total")).append("\n");
        content.append("- 处理中: ").append(ticketStats.getData().get("processing")).append("\n");
        content.append("- 平均处理时长: ").append(ticketStats.getData().get("avgResolveTime")).append("\n\n");
        
        content.append("**资产统计**\n");
        content.append("- 总资产: 150台\n");
        content.append("- 运行中: 130台\n");
        
        // 数据卡片
        List<Map<String, Object>> cards = List.of(
                Map.of("value", alertStats.getData().get("total"), "label", "告警总数"),
                Map.of("value", ticketStats.getData().get("total"), "label", "工单总数"),
                Map.of("value", 150, "label", "资产总数"),
                Map.of("value", "99.95%", "label", "可用率")
        );
        
        return ChatResponse.builder()
                .content(content.toString())
                .cards(cards)
                .build();
    }
    
    /**
     * 处理通用问题 - 只有真正需要AI的问题才调用Ollama
     */
    private ChatResponse handleGeneralQuestion(String message, List<String> history) {
        // 检查是否真的需要AI回答
        if (needsAI(message)) {
            history.add(message);
            String aiResponse = ollamaService.chat(message);
            return ChatResponse.builder()
                    .content(aiResponse)
                    .build();
        }
        
        // 简单问题直接返回引导
        return ChatResponse.builder()
                .content("""
                    我可以帮你完成以下操作：
                    
                    🔍 **查询告警** - "最近有哪些严重告警？"
                    📊 **数据统计** - "本周告警数量多少？"
                    💻 **查询资产** - "给我们部门所有MySQL服务器"
                    📝 **创建工单** - "帮我申请开通数据库权限"
                    📚 **知识问答** - "Redis连接数满了怎么解决？"
                    
                    请直接说出你的需求，或点击左侧菜单查看各模块数据。
                    """)
                .build();
    }
    
    /**
     * 判断是否需要AI处理
     */
    private boolean needsAI(String message) {
        String lower = message.toLowerCase();
        // 真正需要AI回答的问题
        return lower.contains("怎么办") || lower.contains("怎么处理") 
                || lower.contains("为什么") || lower.contains("如何优化")
                || lower.contains("建议") || lower.contains("分析一下");
    }
}
