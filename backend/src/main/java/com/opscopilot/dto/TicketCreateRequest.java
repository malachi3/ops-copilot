package com.opscopilot.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单创建请求
 */
@Data
public class TicketCreateRequest {
    
    /**
     * 工单标题
     */
    @NotBlank(message = "工单标题不能为空")
    private String title;
    
    /**
     * 工单内容
     */
    @NotBlank(message = "工单内容不能为空")
    private String content;
    
    /**
     * 工单类型
     */
    @NotBlank(message = "工单类型不能为空")
    private String type;
    
    /**
     * 优先级
     */
    private String priority = "MEDIUM";
    
    /**
     * 期望完成时间
     */
    private LocalDateTime dueTime;
    
    /**
     * 附加字段 (JSON)
     */
    private String extra;
}
