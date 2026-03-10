package com.opscopilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opscopilot.model.Knowledge;
import org.apache.ibatis.annotations.Mapper;

/**
 * 知识库Mapper
 */
@Mapper
public interface KnowledgeMapper extends BaseMapper<Knowledge> {
}
