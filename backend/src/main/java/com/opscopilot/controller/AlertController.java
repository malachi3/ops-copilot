package com.opscopilot.controller;

import com.opscopilot.dto.*;
import com.opscopilot.service.AlertService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 告警管理控制器
 */
@Tag(name = "告警管理", description = "告警查询与分析接口")
@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {
    
    private final AlertService alertService;
    
    @GetMapping
    @Operation(summary = "查询告警列表")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Alert>> query(
            AlertQueryRequest request) {
        return alertService.query(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取告警详情")
    public Result<com.opscopilot.model.Alert> getById(@PathVariable Long id) {
        return alertService.getById(id);
    }
    
    @PostMapping("/{id}/ticket")
    @Operation(summary = "创建告警工单")
    public Result<com.opscopilot.model.Alert> createTicket(@PathVariable Long id) {
        return alertService.createTicket(id);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "告警统计")
    public Result<java.util.Map<String, Object>> getStatistics() {
        return alertService.getStatistics();
    }
}
