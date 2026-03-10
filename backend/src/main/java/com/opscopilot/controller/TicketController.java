package com.opscopilot.controller;

import com.opscopilot.dto.*;
import com.opscopilot.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 工单管理控制器
 */
@Tag(name = "工单管理", description = "工单创建与查询接口")
@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    @PostMapping
    @Operation(summary = "创建工单")
    public Result<com.opscopilot.model.Ticket> create(
            @Valid @RequestBody TicketCreateRequest request,
            @RequestHeader(value = "X-User-Id", defaultValue = "1") Long userId,
            @RequestHeader(value = "X-User-Name", defaultValue = "张三") String userName,
            @RequestHeader(value = "X-User-Dept", defaultValue = "电商技术部") String userDept) {
        return ticketService.create(request, userId, userName, userDept);
    }
    
    @GetMapping
    @Operation(summary = "查询工单列表")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Ticket>> query(
            @RequestParam(required = false) Long applicantId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return ticketService.query(applicantId, status, type, page, pageSize);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取工单详情")
    public Result<com.opscopilot.model.Ticket> getById(@PathVariable Long id) {
        return ticketService.getById(id);
    }
    
    @PostMapping("/{id}/remind")
    @Operation(summary = "催办工单")
    public Result<Void> remind(@PathVariable Long id) {
        return ticketService.remind(id);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "工单统计")
    public Result<java.util.Map<String, Object>> getStatistics() {
        return ticketService.getStatistics();
    }
}
