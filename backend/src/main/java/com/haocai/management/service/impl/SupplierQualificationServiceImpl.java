package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.SupplierQualificationCreateDTO;
import com.haocai.management.dto.SupplierQualificationUpdateDTO;
import com.haocai.management.dto.SupplierQualificationVO;
import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.entity.SupplierQualification;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SupplierInfoMapper;
import com.haocai.management.mapper.SupplierQualificationMapper;
import com.haocai.management.repository.SupplierQualificationRepository;
import com.haocai.management.service.ISupplierQualificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 供应商资质Service实现类
 * 
 * 遵循规范：
 * - 后端开发规范-第2.1条（Service实现类规范）
 * - 后端开发规范-第2.2条（事务管理：@Transactional）
 * - 后端开发规范-第2.3条（异常处理：抛出BusinessException）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SupplierQualificationServiceImpl implements ISupplierQualificationService {

    private final SupplierQualificationRepository qualificationRepository;
    private final SupplierInfoMapper supplierInfoMapper;
    private final SupplierQualificationMapper qualificationMapper;

    /**
     * 创建供应商资质
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createQualification(SupplierQualificationCreateDTO createDTO, Long currentUserId) {
        log.info("创建供应商资质，供应商ID：{}，资质类型：{}", createDTO.getSupplierId(), createDTO.getQualificationType());
        
        // 验证供应商是否存在
        SupplierInfo supplier = supplierInfoMapper.selectById(createDTO.getSupplierId());
        if (supplier == null) {
            throw new BusinessException(1010, "供应商不存在");
        }
        
        // 检查资质类型是否已存在
        // 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
        if (qualificationRepository.existsBySupplierIdAndType(createDTO.getSupplierId(), createDTO.getQualificationType(), null)) {
            throw new BusinessException(1003, "该供应商已存在相同类型的资质");
        }
        
        // 创建资质实体
        SupplierQualification qualification = new SupplierQualification();
        BeanUtils.copyProperties(createDTO, qualification);
        qualification.setStatus(1); // 默认有效
        qualification.setCreateBy(currentUserId);
        qualification.setUpdateBy(currentUserId);
        
        // 设置默认状态：如果已过期则设置为无效
        if (qualification.getExpiryDate() != null && qualification.getExpiryDate().isBefore(LocalDate.now())) {
            qualification.setStatus(0);
        }
        
        Long qualificationId = qualificationRepository.save(qualification);
        
        log.info("创建供应商资质成功，资质ID：{}", qualificationId);
        return qualificationId;
    }

    /**
     * 更新供应商资质
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateQualification(Long id, SupplierQualificationUpdateDTO updateDTO) {
        log.info("更新供应商资质，资质ID：{}", id);
        
        // 验证资质是否存在
        SupplierQualification existingQualification = qualificationRepository.findById(id);
        if (existingQualification == null) {
            throw new BusinessException(1010, "资质不存在");
        }
        
        // 检查资质类型是否与其他资质冲突
        if (updateDTO.getQualificationType() != null && 
            !updateDTO.getQualificationType().equals(existingQualification.getQualificationType())) {
            if (qualificationRepository.existsBySupplierIdAndType(
                    existingQualification.getSupplierId(), 
                    updateDTO.getQualificationType(), 
                    id)) {
                throw new BusinessException(1003, "该供应商已存在相同类型的资质");
            }
        }
        
        // 更新资质信息
        SupplierQualification qualification = new SupplierQualification();
        qualification.setId(id);
        qualification.setUpdateTime(LocalDateTime.now());
        
        // 只更新非空字段
        if (updateDTO.getQualificationName() != null) {
            qualification.setQualificationName(updateDTO.getQualificationName());
        }
        if (updateDTO.getFileUrl() != null) {
            qualification.setFileUrl(updateDTO.getFileUrl());
        }
        if (updateDTO.getFileName() != null) {
            qualification.setFileName(updateDTO.getFileName());
        }
        if (updateDTO.getIssueDate() != null) {
            qualification.setIssueDate(updateDTO.getIssueDate());
        }
        if (updateDTO.getExpiryDate() != null) {
            qualification.setExpiryDate(updateDTO.getExpiryDate());
            // 如果到期日期已过，自动更新状态
            if (updateDTO.getExpiryDate().isBefore(LocalDate.now())) {
                qualification.setStatus(0);
            }
        }
        if (updateDTO.getIssuingAuthority() != null) {
            qualification.setIssuingAuthority(updateDTO.getIssuingAuthority());
        }
        if (updateDTO.getStatus() != null) {
            qualification.setStatus(updateDTO.getStatus());
        }
        if (updateDTO.getDescription() != null) {
            qualification.setDescription(updateDTO.getDescription());
        }
        
        boolean result = qualificationRepository.update(qualification);
        
        log.info("更新供应商资质结果：{}", result);
        return result;
    }

    /**
     * 删除供应商资质
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteQualification(Long id) {
        log.info("删除供应商资质，资质ID：{}", id);
        
        // 验证资质是否存在
        SupplierQualification qualification = qualificationRepository.findById(id);
        if (qualification == null) {
            throw new BusinessException(1010, "资质不存在");
        }
        
        boolean result = qualificationRepository.deleteById(id);
        
        log.info("删除供应商资质结果：{}", result);
        return result;
    }

    /**
     * 批量删除供应商资质
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchDeleteQualifications(List<Long> ids) {
        log.info("批量删除供应商资质，数量：{}", ids.size());
        
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        
        int count = qualificationRepository.batchDelete(ids);
        
        log.info("批量删除供应商资质数量：{}", count);
        return count;
    }

    /**
     * 获取供应商资质详情
     */
    @Override
    public SupplierQualificationVO getQualificationById(Long id) {
        log.info("获取供应商资质详情，资质ID：{}", id);
        
        SupplierQualification qualification = qualificationRepository.findById(id);
        if (qualification == null) {
            throw new BusinessException(1010, "资质不存在");
        }
        
        return convertToVO(qualification);
    }

    /**
     * 获取供应商所有资质
     */
    @Override
    public List<SupplierQualificationVO> getQualificationsBySupplierId(Long supplierId) {
        log.info("获取供应商所有资质，供应商ID：{}", supplierId);
        
        // 验证供应商是否存在
        SupplierInfo supplier = supplierInfoMapper.selectById(supplierId);
        if (supplier == null) {
            throw new BusinessException(1010, "供应商不存在");
        }
        
        List<SupplierQualification> qualifications = qualificationRepository.findBySupplierId(supplierId);
        List<SupplierQualificationVO> voList = new ArrayList<>();
        
        for (SupplierQualification qualification : qualifications) {
            voList.add(convertToVO(qualification));
        }
        
        return voList;
    }

    /**
     * 分页查询供应商资质
     */
    @Override
    public IPage<SupplierQualificationVO> getQualificationPage(int page, int size, Long supplierId, String qualificationType, Integer status) {
        log.info("分页查询供应商资质，页码：{}，每页大小：{}，供应商ID：{}，资质类型：{}，状态：{}", 
                page, size, supplierId, qualificationType, status);
        
        Page<SupplierQualification> pageParam = new Page<>(page, size);
        LambdaQueryWrapper<SupplierQualification> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(SupplierQualification::getDeleted, 0);
        
        if (supplierId != null) {
            queryWrapper.eq(SupplierQualification::getSupplierId, supplierId);
        }
        if (qualificationType != null && !qualificationType.isEmpty()) {
            queryWrapper.eq(SupplierQualification::getQualificationType, qualificationType);
        }
        if (status != null) {
            queryWrapper.eq(SupplierQualification::getStatus, status);
        }
        
        queryWrapper.orderByDesc(SupplierQualification::getCreateTime);
        
        Page<SupplierQualification> qualificationPage = qualificationMapper.selectPage(pageParam, queryWrapper);
        
        // 转换为VO分页
        Page<SupplierQualificationVO> voPage = new Page<>(page, size, qualificationPage.getTotal());
        List<SupplierQualificationVO> voList = new ArrayList<>();
        
        for (SupplierQualification qualification : qualificationPage.getRecords()) {
            voList.add(convertToVO(qualification));
        }
        
        voPage.setRecords(voList);
        return voPage;
    }

    /**
     * 检查供应商资质类型是否已存在
     */
    @Override
    public boolean existsBySupplierIdAndType(Long supplierId, String qualificationType, Long excludeId) {
        return qualificationRepository.existsBySupplierIdAndType(supplierId, qualificationType, excludeId);
    }

    /**
     * 获取即将到期的资质（30天内）
     */
    @Override
    public List<SupplierQualificationVO> getExpiringQualifications() {
        log.info("获取即将到期的资质");
        
        LocalDate expiryDate = LocalDate.now().plusDays(30);
        List<SupplierQualification> qualifications = qualificationRepository.findExpiringSoon(expiryDate);
        List<SupplierQualificationVO> voList = new ArrayList<>();
        
        for (SupplierQualification qualification : qualifications) {
            voList.add(convertToVO(qualification));
        }
        
        return voList;
    }

    /**
     * 更新过期资质状态
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateExpiredQualificationsStatus() {
        log.info("更新过期资质状态");
        
        LocalDate currentDate = LocalDate.now();
        int count = qualificationRepository.batchUpdateExpiredStatus(currentDate);
        
        log.info("更新过期资质数量：{}", count);
        return count;
    }

    /**
     * 将实体转换为VO
     * 
     * @param qualification 资质实体
     * @return 资质VO
     */
    private SupplierQualificationVO convertToVO(SupplierQualification qualification) {
        SupplierQualificationVO vo = new SupplierQualificationVO();
        BeanUtils.copyProperties(qualification, vo);
        
        // 获取供应商名称
        SupplierInfo supplier = supplierInfoMapper.selectById(qualification.getSupplierId());
        if (supplier != null) {
            vo.setSupplierName(supplier.getSupplierName());
        }
        
        // 设置状态描述
        if (qualification.getStatus() != null) {
            vo.setStatusDesc(qualification.getStatus() == 1 ? "有效" : "过期");
        }
        
        // 检查是否即将到期（30天内）
        if (qualification.getExpiryDate() != null) {
            LocalDate now = LocalDate.now();
            LocalDate thirtyDaysLater = now.plusDays(30);
            boolean expiringSoon = qualification.getExpiryDate().isAfter(now) && 
                                   !qualification.getExpiryDate().isAfter(thirtyDaysLater);
            vo.setExpiringSoon(expiringSoon && qualification.getStatus() == 1);
        } else {
            vo.setExpiringSoon(false);
        }
        
        return vo;
    }
}
