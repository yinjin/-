package com.material.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.dto.MaterialCategoryCreateDTO;
import com.material.system.dto.MaterialCategoryUpdateDTO;
import com.material.system.vo.MaterialCategoryVO;

import java.util.List;

/**
 * 物料分类服务接口
 */
public interface MaterialCategoryService {
    
    /**
     * 创建物料分类
     */
    MaterialCategoryVO create(MaterialCategoryCreateDTO dto);
    
    /**
     * 更新物料分类
     */
    MaterialCategoryVO update(MaterialCategoryUpdateDTO dto);
    
    /**
     * 删除物料分类
     */
    void delete(Long id);
    
    /**
     * 根据ID查询物料分类
     */
    MaterialCategoryVO getById(Long id);
    
    /**
     * 分页查询物料分类
     */
    Page<MaterialCategoryVO> page(Integer current, Integer size, String name, Integer status);
    
    /**
     * 查询所有物料分类（树形结构）
     */
    List<MaterialCategoryVO> tree();
    
    /**
     * 根据父分类ID查询子分类
     */
    List<MaterialCategoryVO> getByParentId(Long parentId);
    
    /**
     * 检查分类编码是否存在
     */
    boolean checkCodeExists(String code, Long excludeId);
}
