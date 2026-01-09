package com.haocai.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.entity.MaterialCategory;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;

import java.util.List;

/**
 * 耗材分类Service接口
 * 
 * @author haocai
 * @since 2026-01-08
 */
public interface IMaterialCategoryService extends IService<MaterialCategory> {
    
    /**
     * 创建耗材分类
     * 
     * @param createDTO 创建请求DTO
     * @return 创建的分类ID
     */
    Long createCategory(MaterialCategoryCreateDTO createDTO);
    
    /**
     * 更新耗材分类
     * 
     * @param id 分类ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    boolean updateCategory(Long id, MaterialCategoryUpdateDTO updateDTO);
    
    /**
     * 删除耗材分类（逻辑删除）
     * 
     * @param id 分类ID
     * @return 是否成功
     */
    boolean deleteCategory(Long id);
    
    /**
     * 批量删除耗材分类（逻辑删除）
     * 
     * @param ids 分类ID列表
     * @return 是否成功
     */
    boolean batchDeleteCategories(List<Long> ids);
    
    /**
     * 根据ID查询耗材分类
     * 
     * @param id 分类ID
     * @return 分类信息
     */
    MaterialCategoryVO getCategoryById(Long id);
    
    /**
     * 查询耗材分类树形结构
     * 
     * @return 分类树形结构
     */
    List<MaterialCategoryTreeVO> getCategoryTree();
    
    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<MaterialCategoryVO> getChildrenByParentId(Long parentId);
    
    /**
     * 查询所有顶级分类
     * 
     * @return 顶级分类列表
     */
    List<MaterialCategoryVO> getTopLevelCategories();
    
    /**
     * 切换分类状态
     * 
     * @param id 分类ID
     * @return 是否成功
     */
    boolean toggleCategoryStatus(Long id);
    
    /**
     * 检查分类编码是否存在
     * 
     * @param categoryCode 分类编码
     * @return 是否存在
     */
    boolean existsByCategoryCode(String categoryCode);
    
    /**
     * 检查分类编码是否存在（排除指定ID）
     * 
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    boolean existsByCategoryCodeExcludeId(String categoryCode, Long excludeId);
    
    /**
     * 检查分类下是否有子分类
     * 
     * @param parentId 父分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Long parentId);
}
