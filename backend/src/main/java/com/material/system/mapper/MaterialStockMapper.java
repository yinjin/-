package com.material.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.system.entity.MaterialStock;
import org.apache.ibatis.annotations.Mapper;

/**
 * 耗材库存Mapper接口
 */
@Mapper
public interface MaterialStockMapper extends BaseMapper<MaterialStock> {
}
