package com.opscopilot.service;

import com.opscopilot.dto.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 预测分析服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PredictionService {
    
    /**
     * 预测告警趋势
     */
    public Result<Map<String, Object>> predictAlerts(String period) {
        // 基于历史数据的简单预测（实际生产应使用机器学习模型）
        
        List<String> dates = new ArrayList<>();
        List<Integer> predicted = new ArrayList<>();
        List<Integer> historical = new ArrayList<>();
        
        // 生成过去7天和未来7天的数据
        LocalDateTime now = LocalDateTime.now();
        for (int i = -6; i <= 7; i++) {
            LocalDateTime date = now.plusDays(i);
            dates.add(date.getMonthValue() + "-" + date.getDayOfMonth());
            
            if (i <= 0) {
                // 历史数据（模拟）
                historical.add(100 + (int)(Math.random() * 50));
                predicted.add(0);
            } else {
                // 预测数据
                historical.add(0);
                int base = 120;
                int trend = (int)(Math.random() * 30 - 15);
                predicted.add(base + trend);
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("dates", dates);
        result.put("historical", historical);
        result.put("predicted", predicted);
        result.put("trend", "上升"); // 简单趋势判断
        result.put("confidence", "85%");
        
        // 预测结论
        List<String> conclusions = new ArrayList<>();
        conclusions.add("未来7天告警数量预计呈上升趋势");
        conclusions.add("周三、周五可能出现告警峰值");
        conclusions.add("建议提前做好容量准备");
        result.put("conclusions", conclusions);
        
        return Result.success(result);
    }
    
    /**
     * 预测容量需求
     */
    public Result<Map<String, Object>> predictCapacity(String business) {
        Map<String, Object> result = new HashMap<>();
        
        // CPU预测
        Map<String, Object> cpu = new HashMap<>();
        cpu.put("current", "65%");
        cpu.put("predicted", "82%");
        cpu.put("threshold", "90%");
        cpu.put("recommendation", "建议2周内扩容");
        result.put("cpu", cpu);
        
        // 内存预测
        Map<String, Object> memory = new HashMap<>();
        memory.put("current", "72%");
        memory.put("predicted", "88%");
        memory.put("threshold", "90%");
        memory.put("recommendation", "建议1个月内扩容");
        result.put("memory", memory);
        
        // 磁盘预测
        Map<String, Object> disk = new HashMap<>();
        disk.put("current", "45%");
        disk.put("predicted", "65%");
        disk.put("threshold", "80%");
        disk.put("recommendation", "暂无扩容计划");
        result.put("disk", disk);
        
        // 扩容建议
        List<Map<String, String>> suggestions = new ArrayList<>();
        suggestions.add(Map.of("item", "应用服务器", "current", "3台", "suggested", "4台", "reason", "CPU使用率预测超过80%"));
        suggestions.add(Map.of("item", "数据库", "current", "2台", "suggested", "2台", "reason", "当前容量充足"));
        result.put("suggestions", suggestions);
        
        return Result.success(result);
    }
    
    /**
     * 故障预测
     */
    public Result<Map<String, Object>> predictFailure() {
        Map<String, Object> result = new HashMap<>();
        
        // 高风险服务
        List<Map<String, Object>> highRisk = new ArrayList<>();
        
        Map<String, Object> risk1 = new HashMap<>();
        risk1.put("service", "订单服务-API");
        risk1.put("riskLevel", "高");
        risk1.put("indicator", "错误率上升");
        risk1.put("probability", "75%");
        risk1.put("suggestion", "建议检查最近部署是否有问题");
        highRisk.add(risk1);
        
        Map<String, Object> risk2 = new HashMap<>();
        risk2.put("service", "数据库-主库");
        risk2.put("riskLevel", "中");
        risk2.put("indicator", "连接数接近上限");
        risk2.put("probability", "45%");
        risk2.put("suggestion", "建议扩容连接池");
        highRisk.add(risk2);
        
        result.put("highRisk", highRisk);
        
        // 异常指标
        List<Map<String, String>> anomalies = new ArrayList<>();
        anomalies.add(Map.of("metric", "API响应时间", "current", "1.2s", "normal", "<500ms", "trend", "上升"));
        anomalies.add(Map.of("metric", "订单失败率", "current", "2.1%", "normal", "<1%", "trend", "上升"));
        result.put("anomalies", anomalies);
        
        // 整体评估
        result.put("overallRisk", "中等");
        result.put("updateTime", LocalDateTime.now().toString());
        
        return Result.success(result);
    }
    
    /**
     * 获取预测概览
     */
    public Result<Map<String, Object>> getOverview() {
        Map<String, Object> result = new HashMap<>();
        
        // 关键指标趋势
        Map<String, String> trends = new HashMap<>();
        trends.put("alertTrend", "上升 ↑");
        trends.put("capacityTrend", "平稳 →");
        trends.put("failureRisk", "降低 ↓");
        result.put("trends", trends);
        
        // 本周预测
        List<String> predictions = new ArrayList<>();
        predictions.add("预计产生告警约120-150条");
        predictions.add("可能有2-3台服务器需要扩容");
        predictions.add("订单服务存在较高故障风险");
        result.put("weeklyPredictions", predictions);
        
        // 建议行动
        List<String> actions = new ArrayList<>();
        actions.add("1. 提前做好订单服务扩容准备");
        actions.add("2. 本周安排一次系统巡检");
        actions.add("3. 检查监控告警阈值设置");
        result.put("recommendedActions", actions);
        
        return Result.success(result);
    }
}
