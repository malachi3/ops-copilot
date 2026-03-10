package com.opscopilot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opscopilot.dto.Result;
import com.opscopilot.model.Knowledge;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 知识库服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class KnowledgeService {
    
    /**
     * 搜索知识库
     */
    public Result<List<Knowledge>> search(String keyword) {
        // TODO: 实现向量检索 + 关键词检索
        // 这里使用简单的模糊匹配作为演示
        
        LambdaQueryWrapper<Knowledge> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(w -> w.like(Knowledge::getTitle, keyword)
                .or().like(Knowledge::getContent, keyword)
                .or().like(Knowledge::getKeywords, keyword));
        wrapper.eq(Knowledge::getStatus, "PUBLISHED");
        wrapper.orderByDesc(Knowledge::getViewCount);
        
        // 模拟搜索结果
        List<Knowledge> results = new ArrayList<>();
        
        if (keyword.toLowerCase().contains("redis") || keyword.contains("连接")) {
            Knowledge k1 = new Knowledge();
            k1.setId(1L);
            k1.setTitle("Redis连接数满了怎么解决");
            k1.setContent("""
                    Redis连接数满了，一般有以下几种原因和解决方法：

                    **1. 连接泄漏（最常见）**
                    - 症状：连接数逐渐增长到上限
                    - 排查：`redis-cli info clients`
                    - 解决：检查代码是否正确关闭连接，配置连接池超时

                    **2. 连接池配置过小**
                    - 症状：业务高峰时连接数飙升
                    - 解决：调整 `maxclients` 参数，或增加Redis实例分摊压力

                    **3. 慢查询阻塞**
                    - 症状：某条命令执行很慢，导致连接堆积
                    - 排查：`redis-cli slowlog get 10`
                    - 解决：优化慢查询
                    """);
            k1.setType("OPERATION");
            k1.setTags("[\"Redis\", \"数据库\", \"连接\"]");
            k1.setKeywords("redis,连接数,maxclients,连接池");
            k1.setViewCount(156);
            results.add(k1);
        }
        
        if (keyword.toLowerCase().contains("nginx") || keyword.contains("重启")) {
            Knowledge k2 = new Knowledge();
            k2.setId(2L);
            k2.setTitle("Nginx服务重启方法");
            k2.setContent("""
                    Nginx重启方法：

                    **1. 优雅重启（推荐）**
                    ```bash
                    nginx -s reload
                    ```

                    **2. 完全重启**
                    ```bash
                    systemctl restart nginx
                    ```

                    **3. 检查配置**
                    ```bash
                    nginx -t
                    ```
                    """);
            k2.setType("OPERATION");
            k2.setTags("[\"Nginx\", \"运维\", \"重启\"]");
            k2.setKeywords("nginx,重启,reload, systemctl");
            k2.setViewCount(89);
            results.add(k2);
        }
        
        return Result.success(results);
    }
    
    /**
     * 获取知识详情
     */
    public Result<Knowledge> getById(Long id) {
        // TODO: 替换为实际的Mapper调用
        Knowledge knowledge = new Knowledge();
        knowledge.setId(id);
        knowledge.setTitle("Redis连接数满了怎么解决");
        knowledge.setContent("详细内容...");
        knowledge.setViewCount(156);
        
        return Result.success(knowledge);
    }
    
    /**
     * 点赞
     */
    public Result<Void> like(Long id) {
        // TODO: 更新点赞数
        log.info("知识库点赞: {}", id);
        return Result.success();
    }
    
    /**
     * 获取热门知识
     */
    public Result<List<Knowledge>> getHot(int limit) {
        // TODO: 查询热门知识
        List<Knowledge> list = new ArrayList<>();
        
        Knowledge k1 = new Knowledge();
        k1.setId(1L);
        k1.setTitle("Linux常用命令大全");
        k1.setViewCount(1234);
        list.add(k1);
        
        Knowledge k2 = new Knowledge();
        k2.setId(2L);
        k2.setTitle("MySQL备份恢复最佳实践");
        k2.setViewCount(987);
        list.add(k2);
        
        return Result.success(list);
    }
}
