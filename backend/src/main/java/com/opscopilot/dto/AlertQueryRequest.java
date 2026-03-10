package com.opscopilot.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警查询请求
 */
@Data
public class AlertQueryRequest {
    
    /**
     * 搜索关键词
     */
    private String keyword;
    
    /**
     * 告警级别: CRITICAL/WARNING/INFO
     */
    private String level;
    
    /**
     * 告警状态
     */
    private String status;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
