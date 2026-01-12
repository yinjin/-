package com.haocai.management.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.haocai.management.entity.SupplierEvaluation;
import com.haocai.management.mapper.SupplierEvaluationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 供应商评价Repository
 * 
 * 遵循development-standards.md中的数据访问层规范：
 * - Repository命名规范：使用业务名称+Repository后缀
 * - 依赖注入：使用@RequiredArgsConstructor进行构造器注入
 * - 数据访问：使用MyBatis-Plus进行数据访问
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Repository
@RequiredArgsConstructor
public class SupplierEvaluationRepository {

    private final SupplierEvaluationMapper supplierEvaluationMapper;

    /**
     * 根据供应商ID查询评价列表
     * 
     * @param supplierId 供应商ID
     * @return 评价列表
     */
    public List<SupplierEvaluation> findBySupplierId(Long supplierId) {
        LambdaQueryWrapper<SupplierEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierEvaluation::getSupplierId, supplierId);
        queryWrapper.eq(SupplierEvaluation::getDeleted, 0);
        queryWrapper.orderByDesc(SupplierEvaluation::getCreateTime);
        return supplierEvaluationMapper.selectList(queryWrapper);
    }

    /**
     * 根据评价人ID查询评价列表
     * 
     * @param evaluatorId 评价人ID
     * @return 评价列表
     */
    public List<SupplierEvaluation> findByEvaluatorId(Long evaluatorId) {
        LambdaQueryWrapper<SupplierEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierEvaluation::getEvaluatorId, evaluatorId);
        queryWrapper.eq(SupplierEvaluation::getDeleted, 0);
        queryWrapper.orderByDesc(SupplierEvaluation::getCreateTime);
        return supplierEvaluationMapper.selectList(queryWrapper);
    }

    /**
     * 根据供应商ID统计评价数量
     * 
     * @param supplierId 供应商ID
     * @return 评价数量
     */
    public int countBySupplierId(Long supplierId) {
        LambdaQueryWrapper<SupplierEvaluation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierEvaluation::getSupplierId, supplierId);
        queryWrapper.eq(SupplierEvaluation::getDeleted, 0);
        Long count = supplierEvaluationMapper.selectCount(queryWrapper);
        return count != null ? count.intValue() : 0;
    }

    /**
     * 保存评价
     * 
     * @param evaluation 评价实体
     * @return 是否成功
     */
    public boolean save(SupplierEvaluation evaluation) {
        return supplierEvaluationMapper.insert(evaluation) > 0;
    }

    /**
     * 更新评价
     * 
     * @param evaluation 评价实体
     * @return 是否成功
     */
    public boolean update(SupplierEvaluation evaluation) {
        return supplierEvaluationMapper.updateById(evaluation) > 0;
    }

    /**
     * 删除评价（逻辑删除）
     * 
     * @param id 评价ID
     * @return 是否成功
     */
    public boolean deleteById(Long id) {
        SupplierEvaluation evaluation = new SupplierEvaluation();
        evaluation.setId(id);
        evaluation.setDeleted(1);
        return supplierEvaluationMapper.updateById(evaluation) > 0;
    }

    /**
     * 根据ID查询评价
     * 
     * @param id 评价ID
     * @return 评价实体
     */
    public SupplierEvaluation findById(Long id) {
        return supplierEvaluationMapper.selectById(id);
    }
}
