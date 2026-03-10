package com.opscopilot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opscopilot.dto.AssetQueryRequest;
import com.opscopilot.dto.Result;
import com.opscopilot.model.Asset;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 资产服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetService {
    
    /**
     * 查询资产列表
     */
    public Result<Page<Asset>> query(AssetQueryRequest request) {
        LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        
        if (request.getKeyword() != null) {
            wrapper.and(w -> w.like(Asset::getName, request.getKeyword())
                    .or().like(Asset::getIp, request.getKeyword())
                    .or().like(Asset::getBusiness, request.getKeyword()));
        }
        if (request.getType() != null) {
            wrapper.eq(Asset::getType, request.getType());
        }
        if (request.getStatus() != null) {
            wrapper.eq(Asset::getStatus, request.getStatus());
        }
        if (request.getDepartment() != null) {
            wrapper.eq(Asset::getDepartment, request.getDepartment());
        }
        if (request.getBusiness() != null) {
            wrapper.eq(Asset::getBusiness, request.getBusiness());
        }
        
        wrapper.orderByDesc(Asset::getCreateTime);
        
        Page<Asset> page = new Page<>(request.getPage(), request.getPageSize());
        // TODO: 替换为实际的Mapper调用
        // Page<Asset> result = assetMapper.selectPage(page, wrapper);
        
        // 模拟数据
        List<Asset> mockList = new ArrayList<>();
        Asset asset1 = new Asset();
        asset1.setId(1L);
        asset1.setName("web-server-01");
        asset1.setType("SERVER");
        asset1.setIp("10.0.1.10");
        asset1.setOs("CentOS 7.9");
        asset1.setSpecification("4核8G");
        asset1.setDepartment("电商技术部");
        asset1.setBusiness("订单服务");
        asset1.setOwnerName("张三");
        asset1.setStatus("RUNNING");
        asset1.setCreateTime(LocalDateTime.now());
        mockList.add(asset1);
        
        Asset asset2 = new Asset();
        asset2.setId(2L);
        asset2.setName("db-mysql-01");
        asset2.setType("DATABASE");
        asset2.setIp("10.0.2.10");
        asset2.setOs("CentOS 7.9");
        asset2.setSpecification("8核16G");
        asset2.setDepartment("电商技术部");
        asset2.setBusiness("订单服务");
        asset2.setOwnerName("李四");
        asset2.setStatus("RUNNING");
        asset2.setCreateTime(LocalDateTime.now());
        mockList.add(asset2);
        
        page.setRecords(mockList);
        page.setTotal(2);
        
        return Result.success(page);
    }
    
    /**
     * 获取资产详情
     */
    public Result<Asset> getById(Long id) {
        // TODO: 替换为实际的Mapper调用
        // Asset asset = assetMapper.selectById(id);
        
        Asset asset = new Asset();
        asset.setId(id);
        asset.setName("web-server-01");
        asset.setType("SERVER");
        asset.setIp("10.0.1.10");
        asset.setPort(80);
        asset.setOs("CentOS 7.9");
        asset.setSpecification("4核8G");
        asset.setDepartment("电商技术部");
        asset.setBusiness("订单服务");
        asset.setOwnerId(1L);
        asset.setOwnerName("张三");
        asset.setStatus("RUNNING");
        asset.setTags("[\"web\", \"线上\", \"订单\"]");
        asset.setRemark("Web服务器，用于订单服务");
        asset.setCreateTime(LocalDateTime.now());
        
        return Result.success(asset);
    }
    
    /**
     * 根据IP查询资产
     */
    public Result<Asset> getByIp(String ip) {
        // TODO: 替换为实际的Mapper调用
        // LambdaQueryWrapper<Asset> wrapper = new LambdaQueryWrapper<>();
        // wrapper.eq(Asset::getIp, ip);
        // Asset asset = assetMapper.selectOne(wrapper);
        
        Asset asset = new Asset();
        asset.setIp(ip);
        asset.setName("web-server-01");
        
        return Result.success(asset);
    }
    
    /**
     * 获取资产统计
     */
    public Result<Object> getStatistics() {
        return Result.success(java.util.Map.of(
                "total", 150,
                "running", 130,
                "stopped", 10,
                "maintenance", 5,
                "byType", java.util.Map.of(
                        "SERVER", 50,
                        "DATABASE", 30,
                        "NETWORK", 20,
                        "MIDDLEWARE", 30,
                        "STORAGE", 20
                )
        ));
    }
    
    /**
     * 获取部门列表
     */
    public Result<List<String>> getDepartments() {
        List<String> depts = List.of(
                "电商技术部",
                "支付技术部",
                "物流技术部",
                "基础架构部",
                "安全部"
        );
        return Result.success(depts);
    }
    
    /**
     * 获取业务列表
     */
    public Result<List<String>> getBusinesses() {
        List<String> businesses = List.of(
                "订单服务",
                "支付服务",
                "用户服务",
                "物流服务",
                "商品服务"
        );
        return Result.success(businesses);
    }
}
