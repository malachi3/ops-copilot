package com.opscopilot.controller;

import com.opscopilot.dto.*;
import com.opscopilot.service.AssetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 资产管理控制器
 */
@Tag(name = "资产管理", description = "资产查询与管理接口")
@RestController
@RequestMapping("/api/assets")
@RequiredArgsConstructor
public class AssetController {
    
    private final AssetService assetService;
    
    @GetMapping
    @Operation(summary = "查询资产列表")
    public Result<com.baomidou.mybatisplus.extension.plugins.pagination.Page<com.opscopilot.model.Asset>> query(
            AssetQueryRequest request) {
        return assetService.query(request);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "获取资产详情")
    public Result<com.opscopilot.model.Asset> getById(@PathVariable Long id) {
        return assetService.getById(id);
    }
    
    @GetMapping("/ip/{ip}")
    @Operation(summary = "根据IP查询资产")
    public Result<com.opscopilot.model.Asset> getByIp(@PathVariable String ip) {
        return assetService.getByIp(ip);
    }
    
    @GetMapping("/statistics")
    @Operation(summary = "资产统计")
    public Result<Object> getStatistics() {
        return assetService.getStatistics();
    }
    
    @GetMapping("/departments")
    @Operation(summary = "获取部门列表")
    public Result<java.util.List<String>> getDepartments() {
        return assetService.getDepartments();
    }
    
    @GetMapping("/businesses")
    @Operation(summary = "获取业务列表")
    public Result<java.util.List<String>> getBusinesses() {
        return assetService.getBusinesses();
    }
}
