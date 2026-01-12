package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.SupplierEvaluation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 供应商评价Mapper接口
 * 
 * 遵循development-standards.md中的数据访问层规范：
 * - Mapper命名规范：使用业务名称+Mapper后缀
 * - 继承BaseMapper：获得MyBatis-Plus提供的基础CRUD方法
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Mapper
public interface SupplierEvaluationMapper extends BaseMapper<SupplierEvaluation> {
}
