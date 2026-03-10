package com.opscopilot.mcp;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * MCP工具定义
 */
@Data
public class MCPTool {
    
    private String name;
    private String description;
    private Map<String, ToolParam> parameters;
    
    @Data
    public static class ToolParam {
        private String type;
        private String description;
        private boolean required;
    }
    
    /**
     * 工具执行结果
     */
    @Data
    public static class ToolResult {
        private boolean success;
        private String data;
        private String error;
        private long duration;
        
        public static ToolResult success(String data, long duration) {
            ToolResult result = new ToolResult();
            result.setSuccess(true);
            result.setData(data);
            result.setDuration(duration);
            return result;
        }
        
        public static ToolResult error(String error) {
            ToolResult result = new ToolResult();
            result.setSuccess(false);
            result.setError(error);
            return result;
        }
    }
}
