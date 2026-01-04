package com.material.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.material.system.entity.Supplier;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商Mapper接口
 */
@Mapper
public interface SupplierMapper extends BaseMapper<Supplier> {
}
