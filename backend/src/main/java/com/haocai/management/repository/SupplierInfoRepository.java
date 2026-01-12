package com.haocai.management.repository;

import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.mapper.SupplierInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 供应商信息Repository实现类
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（数据访问层封装）
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * 
 * 技术说明：
 * - 使用 MyBatis-Plus 作为数据访问层
 * - 不使用 Spring Data JPA，遵循项目整体架构
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Repository
@RequiredArgsConstructor
public class SupplierInfoRepository {
    
    /**
     * 供应商Mapper接口
     * 遵循：后端开发规范-第2.1条（继承BaseMapper获得基础CRUD方法）
     */
    private final SupplierInfoMapper supplierInfoMapper;
    
    /**
     * 根据供应商编码查找供应商
     * 
     * @param supplierCode 供应商编码
     * @return 供应商信息（如果存在）
     */
    public Optional<SupplierInfo> findBySupplierCode(String supplierCode) {
        // 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
        // 使用逻辑删除查询，自动过滤 deleted=0 的记录
        SupplierInfo supplier = supplierInfoMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierCode, supplierCode)
        );
        return Optional.ofNullable(supplier);
    }
    
    /**
     * 根据供应商名称模糊查找（忽略大小写）
     * 
     * @param supplierName 供应商名称关键词
     * @return 供应商列表
     */
    public List<SupplierInfo> findBySupplierNameContainingIgnoreCase(String supplierName) {
        return supplierInfoMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .like(SupplierInfo::getSupplierName, supplierName)
                .orderByAsc(SupplierInfo::getSupplierName)
        );
    }
    
    /**
     * 检查供应商编码是否存在
     * 
     * @param supplierCode 供应商编码
     * @return true-存在，false-不存在
     */
    public boolean existsBySupplierCode(String supplierCode) {
        // 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
        // 查询时自动过滤 deleted=0 的记录
        Long count = supplierInfoMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierCode, supplierCode)
        );
        return count != null && count > 0;
    }
    
    /**
     * 检查供应商名称是否存在
     * 
     * @param supplierName 供应商名称
     * @return true-存在，false-不存在
     */
    public boolean existsBySupplierName(String supplierName) {
        Long count = supplierInfoMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierName, supplierName)
        );
        return count != null && count > 0;
    }
    
    /**
     * 根据供应商编码查找供应商（包含已删除的）
     * 用于唯一索引冲突处理时查找旧记录
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 在插入"唯一键可能重复"的数据前，必须物理删除该键值对应的旧记录
     * 
     * @param supplierCode 供应商编码
     * @return 供应商信息（如果存在）
     */
    public Optional<SupplierInfo> findBySupplierCodeIncludeDeleted(String supplierCode) {
        // 不使用逻辑删除过滤器，查询所有记录（包括已删除的）
        SupplierInfo supplier = supplierInfoMapper.selectOne(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierCode, supplierCode)
                .eq(SupplierInfo::getDeleted, 1)
        );
        return Optional.ofNullable(supplier);
    }
    
    /**
     * 物理删除供应商（用于唯一索引冲突处理）
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 在插入"唯一键可能重复"的数据前，必须物理删除该键值对应的旧记录
     * 
     * @param supplierCode 供应商编码
     * @return 删除的记录数
     */
    public int physicalDeleteBySupplierCode(String supplierCode) {
        return supplierInfoMapper.physicalDeleteBySupplierCode(supplierCode);
    }
    
    /**
     * 根据合作状态查询供应商数量
     * 
     * @param cooperationStatus 合作状态
     * @return 供应商数量
     */
    public long countByCooperationStatus(Integer cooperationStatus) {
        Long count = supplierInfoMapper.selectCount(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getCooperationStatus, cooperationStatus)
        );
        return count != null ? count : 0;
    }
    
    /**
     * 根据状态查询供应商列表
     * 
     * @param status 状态（0-禁用，1-启用）
     * @return 供应商列表
     */
    public List<SupplierInfo> findByStatus(Integer status) {
        return supplierInfoMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getStatus, status)
        );
    }
    
    /**
     * 根据信用等级查询供应商
     * 
     * @param creditRating 信用等级
     * @return 供应商列表
     */
    public List<SupplierInfo> findByCreditRating(Integer creditRating) {
        return supplierInfoMapper.selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getCreditRating, creditRating)
        );
    }
    
    /**
     * 根据信用等级范围查询供应商
     * 
     * @param minCreditRating 最小信用等级
     * @param maxCreditRating 最大信用等级
     * @return 供应商列表
     */
    public List<SupplierInfo> findByCreditRatingBetween(Integer minCreditRating, Integer maxCreditRating) {
        return supplierInfoMapper.selectByCreditRatingRange(minCreditRating, maxCreditRating);
    }
    
    /**
     * 根据合作状态查询供应商列表
     * 
     * @param cooperationStatus 合作状态
     * @return 供应商列表
     */
    public List<SupplierInfo> findByCooperationStatus(Integer cooperationStatus) {
        return supplierInfoMapper.selectByCooperationStatus(cooperationStatus);
    }
    
    /**
     * 检查供应商编码是否存在（排除指定ID）
     * 用于更新时检查编码重复，排除自身
     * 
     * @param supplierCode 供应商编码
     * @param excludeId 排除的供应商ID
     * @return true-存在，false-不存在
     */
    public boolean existsBySupplierCodeExcludeId(String supplierCode, Long excludeId) {
        int count = supplierInfoMapper.countBySupplierCodeExcludeId(supplierCode, excludeId);
        return count > 0;
    }
}
