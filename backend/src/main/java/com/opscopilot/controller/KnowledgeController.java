package com.opscopilot.controller;

import com.opscopilot.dto.*;
import com.opscopilot.service.KnowledgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 知识库控制器
 */
@Tag(name = "知识库", description = "运维知识查询接口")
@RestController
@RequestMapping("/api/knowledge")
@RequiredArgsConstructor
public class KnowledgeController {
    
    private final KnowledgeService knowledgeService;
    
    @GetMapping("/search")
    @Operation(summary = "搜索知识库")
    public Result<java.util.List<com.opscopilot.model.Knowledge>> search(
            @RequestParam String keyword) {
        return knowledgeService.search(keyword);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取知识详情")
    public Result<com.opscopilot.model.Knowledge> getById(@PathVariable Long id) {
        return knowledgeService.getById(id);
    }
    
    @PostMapping("/{id}/like")
    @Operation(summary = "点赞")
    public Result<Void> like(@PathVariable Long id) {
        return knowledgeService.like(id);
    }
    
    @GetMapping("/hot")
    @Operation(summary = "热门知识")
    public Result<java.util.List<com.opscopilot.model.Knowledge>> getHot(
            @RequestParam(defaultValue = "10") int limit) {
        return knowledgeService.getHot(limit);
    }
}
