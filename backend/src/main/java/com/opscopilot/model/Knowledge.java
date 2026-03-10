package com.opscopilot.model;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 知识库文档实体
 */
@Data
@TableName("t_knowledge")
public class Knowledge {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 文档标题
     */
    private String title;
    
    /**
     * 文档内容
     */
    private String content;
    
    /**
     * 文档类型: OPERATION/FAQ/INCIDENT/DOCUMENT
     */
    private String type;
    
    /**
     * 标签 (JSON数组)
     */
    private String tags;
    
    /**
     * 关键词
     */
    private String keywords;
    
    /**
     * 作者
     */
    private String author;
    
    /**
     * 浏览次数
     */
    private Integer viewCount;
    
    /**
     * 点赞次数
     */
    private Integer likeCount;
    
    /**
     * 状态: DRAFT/PUBLISHED/ARCHIVED
     */
    private String status;
    
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
