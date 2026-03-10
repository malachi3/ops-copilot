package com.opscopilot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 告警实体
 */
@Data
@TableName("t_alert")
public class Alert {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 告警标题
     */
    private String title;
    
    /**
     * 告警内容
     */
    private String content;
    
    /**
     * 告警级别: CRITICAL/WARNING/INFO
     */
    private String level;
    
    /**
     * 告警来源
     */
    private String source;
    
    /**
     * 关联资产ID
     */
    private Long assetId;
    
    /**
     * 关联服务器IP
     */
    private String ip;
    
    /**
     * 状态: PENDING/PROCESSING/RESOLVED/CLOSED
     */
    private String status;
    
    /**
     * 告警时间
     */
    private LocalDateTime alertTime;
    
    /**
     * 解决时间
     */
    private LocalDateTime resolveTime;
    
    /**
     * 负责人ID
     */
    private Long assigneeId;
    
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
