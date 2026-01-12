package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.SupplierQualification;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商资质Mapper接口
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（Mapper接口继承BaseMapper）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Mapper
public interface SupplierQualificationMapper extends BaseMapper<SupplierQualification> {
}
