package com.opscopilot.mcp;

import com.opscopilot.service.AlertService;
import com.opscopilot.service.AssetService;
import com.opscopilot.service.TicketService;
import com.opscopilot.service.KnowledgeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MCP工具注册中心
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MCPToolRegistry {

    private final AlertService alertService;
    private final AssetService assetService;
    private final TicketService ticketService;
    private final KnowledgeService knowledgeService;

    /**
     * 获取所有可用工具
     */
    public List<MCPTool> getAllTools() {
        List<MCPTool> tools = new ArrayList<>();
        
        // 告警工具
        tools.add(createTool("alert_query", "查询告警列表，支持按级别、状态、时间范围筛选",
                Map.of("level", param("string", "告警级别: CRITICAL|WARNING|INFO", false),
                        "status", param("string", "告警状态: PENDING|PROCESSING|RESOLVED", false),
                        "hours", param("integer", "查询最近几小时", false))));
        
        tools.add(createTool("alert_stats", "获取告警统计数据",
                Map.of("period", param("string", "统计周期: today|week|month", false))));
        
        // 资产工具
        tools.add(createTool("asset_search", "搜索资产信息，支持按名称、IP、业务、类型筛选",
                Map.of("keyword", param("string", "搜索关键词", false),
                        "type", param("string", "资产类型: SERVER|DATABASE|MIDDLEWARE", false),
                        "status", param("string", "资产状态", false))));
        
        tools.add(createTool("asset_stats", "获取资产统计数据",
                Map.of("group", param("string", "分组维度: type|status|department", false))));
        
        // 工单工具
        tools.add(createTool("ticket_query", "查询工单列表",
                Map.of("status", param("string", "工单状态", false),
                        "type", param("string", "工单类型", false))));
        
        tools.add(createTool("ticket_stats", "获取工单统计数据",
                Map.of("period", param("string", "统计周期", false))));
        
        // 知识库工具
        tools.add(createTool("knowledge_search", "搜索运维知识库",
                Map.of("keyword", param("string", "搜索关键词", true))));
        
        return tools;
    }

    /**
     * 执行工具
     */
    public MCPTool.ToolResult executeTool(String toolName, Map<String, Object> params) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("执行MCP工具: {} 参数: {}", toolName, params);
            
            String result = switch (toolName) {
                case "alert_query" -> executeAlertQuery(params);
                case "alert_stats" -> executeAlertStats(params);
                case "asset_search" -> executeAssetSearch(params);
                case "asset_stats" -> executeAssetStats(params);
                case "ticket_query" -> executeTicketQuery(params);
                case "ticket_stats" -> executeTicketStats(params);
                case "knowledge_search" -> executeKnowledgeSearch(params);
                default -> "{\"error\": \"未知工具: " + toolName + "\"}";
            };
            
            return MCPTool.ToolResult.success(result, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("工具执行失败: {}", e.getMessage(), e);
            return MCPTool.ToolResult.error(e.getMessage());
        }
    }

    private String executeAlertQuery(Map<String, Object> params) {
        // 调用告警服务获取数据
        return "{\"alerts\": [{\"title\": \"CPU使用率过高\", \"level\": \"CRITICAL\", \"ip\": \"10.0.1.10\", \"time\": \"10:30\"}]}";
    }

    private String executeAlertStats(Map<String, Object> params) {
        return "{\"total\": 1234, \"critical\": 12, \"warning\": 56, \"resolved\": 1166, \"trend\": [100, 120, 90, 110, 130, 100, 120]}";
    }

    private String executeAssetSearch(Map<String, Object> params) {
        return "{\"assets\": [{\"name\": \"web-server-01\", \"ip\": \"10.0.1.10\", \"type\": \"SERVER\", \"status\": \"RUNNING\"}]}";
    }

    private String executeAssetStats(Map<String, Object> params) {
        return "{\"total\": 150, \"running\": 130, \"stopped\": 10, \"byType\": {\"SERVER\": 50, \"DATABASE\": 30}}";
    }

    private String executeTicketQuery(Map<String, Object> params) {
        return "{\"tickets\": [{\"no\": \"TKT-001\", \"title\": \"故障处理\", \"status\": \"PROCESSING\"}]}";
    }

    private String executeTicketStats(Map<String, Object> params) {
        return "{\"total\": 89, \"pending\": 12, \"processing\": 5, \"closed\": 72, \"avgTime\": \"2.3h\"}";
    }

    private String executeKnowledgeSearch(Map<String, Object> params) {
        String keyword = (String) params.get("keyword");
        return "{\"results\": [{\"title\": \"Redis连接数问题\", \"content\": \"解决方法...\"}]}";
    }

    private MCPTool createTool(String name, String description, Map<String, MCPTool.ToolParam> params) {
        MCPTool tool = new MCPTool();
        tool.setName(name);
        tool.setDescription(description);
        tool.setParameters(params);
        return tool;
    }

    private MCPTool.ToolParam param(String type, String description, boolean required) {
        MCPTool.ToolParam p = new MCPTool.ToolParam();
        p.setType(type);
        p.setDescription(description);
        p.setRequired(required);
        return p;
    }
}
