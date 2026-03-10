package com.opscopilot.dto;

import lombok.Data;

import java.util.List;

/**
 * 资产查询请求
 */
@Data
public class AssetQueryRequest {
    
    /**
     * 搜索关键词 (名称/IP/业务)
     */
    private String keyword;
    
    /**
     * 资产类型
     */
    private String type;
    
    /**
     * 状态
     */
    private String status;
    
    /**
     * 部门
     */
    private String department;
    
    /**
     * 业务
     */
    private String business;
    
    /**
     * 标签
     */
    private List<String> tags;
    
    /**
     * 页码
     */
    private Integer page = 1;
    
    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}
