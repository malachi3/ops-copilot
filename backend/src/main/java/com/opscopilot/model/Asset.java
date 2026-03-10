package com.opscopilot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 资产实体
 */
@Data
@TableName("t_asset")
public class Asset {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 资产名称
     */
    private String name;
    
    /**
     * 资产类型: SERVER/NETWORK/MIDDLEWARE/DATABASE/STORAGE
     */
    private String type;
    
    /**
     * IP地址
     */
    private String ip;
    
    /**
     * 端口
     */
    private Integer port;
    
    /**
     * 操作系统
     */
    private String os;
    
    /**
     * 规格配置
     */
    private String specification;
    
    /**
     * 所属部门
     */
    private String department;
    
    /**
     * 所属业务
     */
    private String business;
    
    /**
     * 负责人ID
     */
    private Long ownerId;
    
    /**
     * 负责人名称
     */
    private String ownerName;
    
    /**
     * 状态: RUNNING/STOPPED/MAINTENANCE/RETIRED
     */
    private String status;
    
    /**
     * 标签 (JSON数组)
     */
    private String tags;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    
    /**
     * 逻辑删除
     */
    @TableLogic
    private Integer deleted;
}
