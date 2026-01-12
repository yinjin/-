package com.haocai.management.repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.haocai.management.entity.SupplierQualification;
import com.haocai.management.mapper.SupplierQualificationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * 供应商资质数据访问仓储类
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（数据访问层封装）
 * - 数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class SupplierQualificationRepository {

    private final SupplierQualificationMapper qualificationMapper;

    /**
     * 保存资质信息
     * 
     * @param qualification 资质实体
     * @return 保存后的资质ID
     */
    public Long save(SupplierQualification qualification) {
        qualificationMapper.insert(qualification);
        return qualification.getId();
    }

    /**
     * 根据ID查询资质
     * 
     * @param id 资质ID
     * @return 资质信息
     */
    public SupplierQualification findById(Long id) {
        return qualificationMapper.selectById(id);
    }

    /**
     * 根据供应商ID查询所有资质
     * 
     * @param supplierId 供应商ID
     * @return 资质列表
     */
    public List<SupplierQualification> findBySupplierId(Long supplierId) {
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierQualification::getSupplierId, supplierId)
                    .eq(SupplierQualification::getDeleted, 0)
                    .orderByDesc(SupplierQualification::getCreateTime);
        return qualificationMapper.selectList(queryWrapper);
    }

    /**
     * 根据供应商ID和资质类型查询资质
     * 
     * @param supplierId 供应商ID
     * @param qualificationType 资质类型
     * @return 资质信息
     */
    public SupplierQualification findBySupplierIdAndType(Long supplierId, String qualificationType) {
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierQualification::getSupplierId, supplierId)
                    .eq(SupplierQualification::getQualificationType, qualificationType)
                    .eq(SupplierQualification::getDeleted, 0)
                    .last("LIMIT 1");
        return qualificationMapper.selectOne(queryWrapper);
    }

    /**
     * 检查供应商资质类型是否存在
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 
     * @param supplierId 供应商ID
     * @param qualificationType 资质类型
     * @param excludeId 排除的资质ID（用于更新时检查）
     * @return 是否存在
     */
    public boolean existsBySupplierIdAndType(Long supplierId, String qualificationType, Long excludeId) {
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SupplierQualification::getSupplierId, supplierId)
                    .eq(SupplierQualification::getQualificationType, qualificationType)
                    .eq(SupplierQualification::getDeleted, 0);
        
        if (excludeId != null) {
            queryWrapper.ne(SupplierQualification::getId, excludeId);
        }
        
        return qualificationMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 查询即将到期的资质（30天内）
     * 
     * @param expiryDate 截止日期
     * @return 资质列表
     */
    public List<SupplierQualification> findExpiringSoon(LocalDate expiryDate) {
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.le(SupplierQualification::getExpiryDate, expiryDate)
                    .eq(SupplierQualification::getStatus, 1)
                    .eq(SupplierQualification::getDeleted, 0)
                    .orderByAsc(SupplierQualification::getExpiryDate);
        return qualificationMapper.selectList(queryWrapper);
    }

    /**
     * 查询已过期的资质
     * 
     * @param currentDate 当前日期
     * @return 资质列表
     */
    public List<SupplierQualification> findExpired(LocalDate currentDate) {
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.lt(SupplierQualification::getExpiryDate, currentDate)
                    .eq(SupplierQualification::getStatus, 1)
                    .eq(SupplierQualification::getDeleted, 0);
        return qualificationMapper.selectList(queryWrapper);
    }

    /**
     * 更新资质信息
     * 
     * @param qualification 资质实体
     * @return 是否成功
     */
    public boolean update(SupplierQualification qualification) {
        return qualificationMapper.updateById(qualification) > 0;
    }

    /**
     * 根据ID删除资质（逻辑删除）
     * 
     * @param id 资质ID
     * @return 是否成功
     */
    public boolean deleteById(Long id) {
        SupplierQualification qualification = new SupplierQualification();
        qualification.setId(id);
        qualification.setDeleted(1);
        return qualificationMapper.updateById(qualification) > 0;
    }

    /**
     * 批量删除资质
     * 
     * @param ids 资质ID列表
     * @return 删除数量
     */
    public int batchDelete(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        LambdaUpdateWrapper<SupplierQualification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.in(SupplierQualification::getId, ids)
                     .set(SupplierQualification::getDeleted, 1);
        return qualificationMapper.update(null, updateWrapper);
    }

    /**
     * 更新资质状态
     * 
     * @param id 资质ID
     * @param status 新状态
     * @return 是否成功
     */
    public boolean updateStatus(Long id, Integer status) {
        SupplierQualification qualification = new SupplierQualification();
        qualification.setId(id);
        qualification.setStatus(status);
        return qualificationMapper.updateById(qualification) > 0;
    }

    /**
     * 批量更新过期资质状态
     * 遵循：后端开发规范-第2.1条（批量操作规范）
     * 
     * @param currentDate 当前日期
     * @return 更新数量
     */
    public int batchUpdateExpiredStatus(LocalDate currentDate) {
        LambdaUpdateWrapper<SupplierQualification> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.lt(SupplierQualification::getExpiryDate, currentDate)
                     .eq(SupplierQualification::getStatus, 1)
                     .set(SupplierQualification::getStatus, 0);
        return qualificationMapper.update(null, updateWrapper);
    }
}
