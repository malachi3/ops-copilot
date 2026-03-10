package com.opscopilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opscopilot.model.Ticket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工单Mapper
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
