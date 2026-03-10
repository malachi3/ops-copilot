package com.opscopilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opscopilot.model.Alert;
import org.apache.ibatis.annotations.Mapper;

/**
 * 告警Mapper
 */
@Mapper
public interface AlertMapper extends BaseMapper<Alert> {
}
