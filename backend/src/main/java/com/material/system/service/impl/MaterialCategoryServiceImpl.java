package com.material.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.dto.MaterialCategoryCreateDTO;
import com.material.system.dto.MaterialCategoryUpdateDTO;
import com.material.system.entity.MaterialCategory;
import com.material.system.exception.BusinessException;
import com.material.system.mapper.MaterialCategoryMapper;
import com.material.system.service.MaterialCategoryService;
import com.material.system.vo.MaterialCategoryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 物料分类服务实现类
 */
@Service
@RequiredArgsConstructor
public class MaterialCategoryServiceImpl implements MaterialCategoryService {
    
    private final MaterialCategoryMapper materialCategoryMapper;
    
    @Override
    public MaterialCategoryVO create(MaterialCategoryCreateDTO dto) {
        // 检查分类编码是否已存在
        if (checkCodeExists(dto.getCode(), null)) {
            throw new BusinessException("分类编码已存在");
        }
        
        // 如果有父分类ID，检查父分类是否存在
        if (dto.getParentId() != null && dto.getParentId() > 0) {
            MaterialCategory parent = materialCategoryMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException("父分类不存在");
            }
        }
        
        MaterialCategory category = new MaterialCategory();
        BeanUtils.copyProperties(dto, category);
        
        materialCategoryMapper.insert(category);
        
        return convertToVO(category);
    }
    
    @Override
    public MaterialCategoryVO update(MaterialCategoryUpdateDTO dto) {
        MaterialCategory category = materialCategoryMapper.selectById(dto.getId());
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查分类编码是否已存在（排除自己）
        if (StringUtils.hasText(dto.getCode()) && checkCodeExists(dto.getCode(), dto.getId())) {
            throw new BusinessException("分类编码已存在");
        }
        
        // 如果修改了父分类ID，检查父分类是否存在
        if (dto.getParentId() != null && !dto.getParentId().equals(category.getParentId())) {
            if (dto.getParentId() > 0) {
                MaterialCategory parent = materialCategoryMapper.selectById(dto.getParentId());
                if (parent == null) {
                    throw new BusinessException("父分类不存在");
                }
                // 不能将分类设置为自己的子分类
                if (isChildCategory(dto.getId(), dto.getParentId())) {
                    throw new BusinessException("不能将分类设置为自己的子分类");
                }
            }
        }
        
        BeanUtils.copyProperties(dto, category);
        materialCategoryMapper.updateById(category);
        
        return convertToVO(category);
    }
    
    @Override
    public void delete(Long id) {
        MaterialCategory category = materialCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        
        // 检查是否有子分类
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, id);
        Long childCount = materialCategoryMapper.selectCount(wrapper);
        if (childCount > 0) {
            throw new BusinessException("该分类下存在子分类，无法删除");
        }
        
        materialCategoryMapper.deleteById(id);
    }
    
    @Override
    public MaterialCategoryVO getById(Long id) {
        MaterialCategory category = materialCategoryMapper.selectById(id);
        if (category == null) {
            throw new BusinessException("分类不存在");
        }
        return convertToVO(category);
    }
    
    @Override
    public Page<MaterialCategoryVO> page(Integer current, Integer size, String name, Integer status) {
        Page<MaterialCategory> page = new Page<>(current, size);
        
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(MaterialCategory::getName, name);
        }
        if (status != null) {
            wrapper.eq(MaterialCategory::getStatus, status);
        }
        wrapper.orderByAsc(MaterialCategory::getSortOrder);
        
        Page<MaterialCategory> resultPage = materialCategoryMapper.selectPage(page, wrapper);
        
        Page<MaterialCategoryVO> voPage = new Page<>(resultPage.getCurrent(), resultPage.getSize(), resultPage.getTotal());
        List<MaterialCategoryVO> voList = resultPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        voPage.setRecords(voList);
        
        return voPage;
    }
    
    @Override
    public List<MaterialCategoryVO> tree() {
        // 查询所有启用的分类
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getStatus, 1);
        wrapper.orderByAsc(MaterialCategory::getSortOrder);
        
        List<MaterialCategory> allCategories = materialCategoryMapper.selectList(wrapper);
        List<MaterialCategoryVO> allVOs = allCategories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        // 构建树形结构
        return buildTree(allVOs, 0L);
    }
    
    @Override
    public List<MaterialCategoryVO> getByParentId(Long parentId) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, parentId);
        wrapper.eq(MaterialCategory::getStatus, 1);
        wrapper.orderByAsc(MaterialCategory::getSortOrder);
        
        List<MaterialCategory> categories = materialCategoryMapper.selectList(wrapper);
        return categories.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean checkCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getCode, code);
        if (excludeId != null) {
            wrapper.ne(MaterialCategory::getId, excludeId);
        }
        return materialCategoryMapper.selectCount(wrapper) > 0;
    }
    
    /**
     * 构建树形结构
     */
    private List<MaterialCategoryVO> buildTree(List<MaterialCategoryVO> allCategories, Long parentId) {
        List<MaterialCategoryVO> tree = new ArrayList<>();
        
        for (MaterialCategoryVO category : allCategories) {
            if ((parentId == 0 && category.getParentId() == null) || 
                (parentId != 0 && parentId.equals(category.getParentId()))) {
                List<MaterialCategoryVO> children = buildTree(allCategories, category.getId());
                category.setChildren(children);
                tree.add(category);
            }
        }
        
        return tree;
    }
    
    /**
     * 检查是否是子分类
     */
    private boolean isChildCategory(Long parentId, Long childId) {
        if (parentId.equals(childId)) {
            return true;
        }
        
        LambdaQueryWrapper<MaterialCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MaterialCategory::getParentId, parentId);
        List<MaterialCategory> children = materialCategoryMapper.selectList(wrapper);
        
        for (MaterialCategory child : children) {
            if (isChildCategory(child.getId(), childId)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * 转换为VO
     */
    private MaterialCategoryVO convertToVO(MaterialCategory category) {
        MaterialCategoryVO vo = new MaterialCategoryVO();
        BeanUtils.copyProperties(category, vo);
        
        // 查询父分类名称
        if (category.getParentId() != null && category.getParentId() > 0) {
            MaterialCategory parent = materialCategoryMapper.selectById(category.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getName());
            }
        }
        
        return vo;
    }
}
