package com.material.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.dto.MaterialInfoCreateDTO;
import com.material.system.dto.MaterialInfoUpdateDTO;
import com.material.system.entity.MaterialCategory;
import com.material.system.entity.MaterialInfo;
import com.material.system.exception.BusinessException;
import com.material.system.mapper.MaterialCategoryMapper;
import com.material.system.mapper.MaterialInfoMapper;
import com.material.system.service.MaterialInfoService;
import com.material.system.vo.MaterialInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class MaterialInfoServiceImpl implements MaterialInfoService {
    
    private final MaterialInfoMapper materialInfoMapper;
    private final MaterialCategoryMapper materialCategoryMapper;
    
    @Override
    public MaterialInfoVO create(MaterialInfoCreateDTO dto) {
        // 检查物料编码是否已存在
        if (checkCodeExists(dto.getCode(), null)) {
            throw new BusinessException("物料编码已存在");
        }
        
        // 检查分类是否存在
        if (dto.getCategoryId() != null) {
            MaterialCategory category = materialCategoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("物料分类不存在");
            }
        }
        
        MaterialInfo materialInfo = new MaterialInfo();
        BeanUtils.copyProperties(dto, materialInfo);
        
        // 设置默认状态
        if (materialInfo.getStatus() == null) {
            materialInfo.setStatus(1);
        }
        
        // 设置默认库存
        if (materialInfo.getStockQuantity() == null) {
            materialInfo.setStockQuantity(0);
        }
        
        materialInfoMapper.insert(materialInfo);
        
        return convertToVO(materialInfo);
    }
    
    @Override
    public MaterialInfoVO update(MaterialInfoUpdateDTO dto) {
        MaterialInfo materialInfo = materialInfoMapper.selectById(dto.getId());
        if (materialInfo == null) {
            throw new BusinessException("物料信息不存在");
        }
        
        // 检查物料编码是否已存在（排除自己）
        if (StringUtils.hasText(dto.getCode()) && checkCodeExists(dto.getCode(), dto.getId())) {
            throw new BusinessException("物料编码已存在");
        }
        
        // 如果修改了分类ID，检查分类是否存在
        if (dto.getCategoryId() != null && !dto.getCategoryId().equals(materialInfo.getCategoryId())) {
            MaterialCategory category = materialCategoryMapper.selectById(dto.getCategoryId());
            if (category == null) {
                throw new BusinessException("物料分类不存在");
            }
        }
        
        BeanUtils.copyProperties(dto, materialInfo);
        materialInfoMapper.updateById(materialInfo);
        
        return convertToVO(materialInfo);
    }
    
    @Override
    public void delete(Long id) {
        MaterialInfo materialInfo = materialInfoMapper.selectById(id);
        if (materialInfo == null) {
            throw new BusinessException("物料信息不存在");
        }
        
        // 检查是否有库存
        if (materialInfo.getStockQuantity() != null && materialInfo.getStockQuantity() > 0) {
            throw new BusinessException("该物料还有库存，无法删除");
        }
        
        materialInfoMapper.deleteById(id);
    }
    
    @Override
    public MaterialInfoVO getById(Long id) {
        MaterialInfo materialInfo = materialInfoMapper.selectById(id);
        if (materialInfo == null) {
            throw new BusinessException("物料信息不存在");
        }
        return convertToVO(materialInfo);
    }
    
    @Override
    public Page<MaterialInfoVO> page(Integer current, Integer size, String name, String code, Long categoryId, Integer status) {
        Page<MaterialInfo> page = new Page<>(current, size);
        
        LambdaQueryWrapper<MaterialInfo> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(MaterialInfo::getName, name);
        }
        if (StringUtils.hasText(code)) {
            wrapper.like(MaterialInfo::getCode, code);
        }
        if (categoryId != null) {
            wrapper.eq(MaterialInfo::getCategoryId, categoryId);
        }
        if (status != null) {
            wrapper.eq(MaterialInfo::getStatus, status);
        }
        wrapper.orderByDesc(MaterialInfo::getCreateTime);
        
        Page<MaterialInfo> resultPage = materialInfoMapper.selectPage(page, wrapper);
        
        Page<MaterialInfoVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<MaterialInfoVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public List<MaterialInfoVO> getByCategoryId(Long categoryId) {
        LambdaQueryWrapper<MaterialInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialInfo::getCategoryId, categoryId);
        wrapper.eq(MaterialInfo::getStatus, 1);
        wrapper.orderByAsc(MaterialInfo::getName);
        
        List<MaterialInfo> materialInfos = materialInfoMapper.selectList(wrapper);
        return materialInfos.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<MaterialInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialInfo::getCode, code);
        if (excludeId != null) {
            wrapper.ne(MaterialInfo::getId, excludeId);
        }
        return materialInfoMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 转换为VO
     */
    private MaterialInfoVO convertToVO(MaterialInfo materialInfo) {
        MaterialInfoVO vo = new MaterialInfoVO();
        BeanUtils.copyProperties(materialInfo, vo);
        
        // 查询分类名称
        if (materialInfo.getCategoryId() != null) {
            MaterialCategory category = materialCategoryMapper.selectById(materialInfo.getCategoryId());
            if (category != null) {
                vo.setCategoryName(category.getName());
            }
        }
        
        // 判断库存状态：1正常 2低于最小库存 3高于最大库存
        if (materialInfo.getStockQuantity() != null) {
            if (materialInfo.getMinStock() != null && materialInfo.getStockQuantity() <= materialInfo.getMinStock()) {
                vo.setStockStatus(2); // 库存不足
            } else if (materialInfo.getMaxStock() != null && materialInfo.getStockQuantity() >= materialInfo.getMaxStock()) {
                vo.setStockStatus(3); // 库存过多
            } else {
                vo.setStockStatus(1); // 库存正常
            }
        }
        
        return vo;
    }
}
