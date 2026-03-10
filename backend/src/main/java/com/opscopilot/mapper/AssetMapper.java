package com.opscopilot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.opscopilot.model.Asset;
import org.apache.ibatis.annotations.Mapper;

/**
 * 资产Mapper
 */
@Mapper
public interface AssetMapper extends BaseMapper<Asset> {
}
