package com.opscopilot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 工单实体
 */
@Data
@TableName("t_ticket")
public class Ticket {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 工单编号
     */
    private String ticketNo;
    
    /**
     * 工单标题
     */
    private String title;
    
    /**
     * 工单内容
     */
    private String content;
    
    /**
     * 工单类型: INCIDENT/PERMISSION/SERVICE_CHANGE/QUESTION
     */
    private String type;
    
    /**
     * 工单状态: DRAFT/PENDING/APPROVED/PROCESSING/CLOSED/REJECTED
     */
    private String status;
    
    /**
     * 优先级: LOW/MEDIUM/HIGH/URGENT
     */
    private String priority;
    
    /**
     * 申请人ID
     */
    private Long applicantId;
    
    /**
     * 申请人名称
     */
    private String applicantName;
    
    /**
     * 申请人部门
     */
    private String applicantDept;
    
    /**
     * 当前审批人ID
     */
    private Long approverId;
    
    /**
     * 当前审批人名称
     */
    private String approverName;
    
    /**
     * 负责人ID (处理人)
     */
    private Long handlerId;
    
    /**
     * 负责人名称
     */
    private String handlerName;
    
    /**
     * 预计完成时间
     */
    private LocalDateTime dueTime;
    
    /**
     * 实际完成时间
     */
    private LocalDateTime completeTime;
    
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
