package com.opscopilot.controller;

import com.opscopilot.dto.Result;
import com.opscopilot.service.PredictionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 预测分析控制器
 */
@Tag(name = "预测分析", description = "AI预测分析接口")
@RestController
@RequestMapping("/api/prediction")
@RequiredArgsConstructor
public class PredictionController {
    
    private final PredictionService predictionService;
    
    @GetMapping("/alerts")
    @Operation(summary = "告警趋势预测")
    public Result<java.util.Map<String, Object>> predictAlerts(
            @RequestParam(defaultValue = "week") String period) {
        return predictionService.predictAlerts(period);
    }
    
    @GetMapping("/capacity")
    @Operation(summary = "容量需求预测")
    public Result<java.util.Map<String, Object>> predictCapacity(
            @RequestParam(required = false) String business) {
        return predictionService.predictCapacity(business);
    }
    
    @GetMapping("/failure")
    @Operation(summary = "故障预测")
    public Result<java.util.Map<String, Object>> predictFailure() {
        return predictionService.predictFailure();
    }
    
    @GetMapping("/overview")
    @Operation(summary = "预测概览")
    public Result<java.util.Map<String, Object>> getOverview() {
        return predictionService.getOverview();
    }
}
