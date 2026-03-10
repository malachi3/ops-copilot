package com.opscopilot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.opscopilot.dto.Result;
import com.opscopilot.dto.TicketCreateRequest;
import com.opscopilot.model.Ticket;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 工单服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TicketService {
    
    private static final AtomicLong TICKET_ID = new AtomicLong(20260310000L);
    
    /**
     * 创建工单
     */
    public Result<Ticket> create(TicketCreateRequest request, Long applicantId, String applicantName, String applicantDept) {
        Ticket ticket = new Ticket();
        
        // 生成工单编号
        String ticketNo = "TKT-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")) 
                + "-" + TICKET_ID.incrementAndGet();
        
        ticket.setTicketNo(ticketNo);
        ticket.setTitle(request.getTitle());
        ticket.setContent(request.getContent());
        ticket.setType(request.getType());
        ticket.setPriority(request.getPriority());
        ticket.setStatus("PENDING");
        
        ticket.setApplicantId(applicantId);
        ticket.setApplicantName(applicantName);
        ticket.setApplicantDept(applicantDept);
        
        ticket.setDueTime(request.getDueTime());
        ticket.setCreateTime(LocalDateTime.now());
        ticket.setUpdateTime(LocalDateTime.now());
        
        // TODO: 保存到数据库
        // ticketMapper.insert(ticket);
        
        return Result.success(ticket);
    }
    
    /**
     * 查询工单列表
     */
    public Result<Page<Ticket>> query(Long applicantId, String status, String type, Integer page, Integer pageSize) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        
        if (applicantId != null) {
            wrapper.eq(Ticket::getApplicantId, applicantId);
        }
        if (status != null) {
            wrapper.eq(Ticket::getStatus, status);
        }
        if (type != null) {
            wrapper.eq(Ticket::getType, type);
        }
        
        wrapper.orderByDesc(Ticket::getCreateTime);
        
        Page<Ticket> pageObj = new Page<>(page, pageSize);
        // TODO: 替换为实际的Mapper调用
        
        // 模拟数据
        List<Ticket> mockList = new ArrayList<>();
        
        Ticket ticket1 = new Ticket();
        ticket1.setId(1L);
        ticket1.setTicketNo("TKT-20260310-001");
        ticket1.setTitle("订单服务CPU告警处理");
        ticket1.setContent("服务器CPU使用率超过90%，需要扩容");
        ticket1.setType("INCIDENT");
        ticket1.setStatus("PROCESSING");
        ticket1.setPriority("HIGH");
        ticket1.setApplicantName("张三");
        ticket1.setApplicantDept("电商技术部");
        ticket1.setHandlerName("李四");
        ticket1.setCreateTime(LocalDateTime.now().minusHours(2));
        mockList.add(ticket1);
        
        Ticket ticket2 = new Ticket();
        ticket2.setId(2L);
        ticket2.setTicketNo("TKT-20260310-002");
        ticket2.setTitle("申请MongoDB运维权限");
        ticket2.setContent("需要开通MongoDB读写权限，用于新业务上线调试");
        ticket2.setType("PERMISSION");
        ticket2.setStatus("PENDING");
        ticket2.setPriority("MEDIUM");
        ticket2.setApplicantName("张三");
        ticket2.setApplicantDept("电商技术部");
        ticket2.setApproverName("王五");
        ticket2.setCreateTime(LocalDateTime.now().minusHours(1));
        mockList.add(ticket2);
        
        pageObj.setRecords(mockList);
        pageObj.setTotal(2);
        
        return Result.success(pageObj);
    }
    
    /**
     * 获取工单详情
     */
    public Result<Ticket> getById(Long id) {
        // TODO: 替换为实际的Mapper调用
        // Ticket ticket = ticketMapper.selectById(id);
        
        Ticket ticket = new Ticket();
        ticket.setId(id);
        ticket.setTicketNo("TKT-20260310-001");
        ticket.setTitle("订单服务CPU告警处理");
        ticket.setContent("服务器CPU使用率超过90%，需要扩容");
        ticket.setType("INCIDENT");
        ticket.setStatus("PROCESSING");
        ticket.setPriority("HIGH");
        ticket.setApplicantId(1L);
        ticket.setApplicantName("张三");
        ticket.setApplicantDept("电商技术部");
        ticket.setHandlerId(2L);
        ticket.setHandlerName("李四");
        ticket.setCreateTime(LocalDateTime.now().minusHours(2));
        
        return Result.success(ticket);
    }
    
    /**
     * 催办工单
     */
    public Result<Void> remind(Long id) {
        // TODO: 发送催办通知
        log.info("催办工单: {}", id);
        return Result.success();
    }
    
    /**
     * 获取工单统计
     */
    public Result<Map<String, Object>> getStatistics() {
        return Result.success(Map.of(
                "total", 89,
                "pending", 12,
                "processing", 5,
                "closed", 72,
                "avgResolveTime", "2.3h"
        ));
    }
}
