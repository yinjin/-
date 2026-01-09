package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.MaterialCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 耗材分类Mapper接口
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {
    
    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM material_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<MaterialCategory> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询所有顶级分类（parent_id = 0）
     * 
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM material_category WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<MaterialCategory> selectTopLevelCategories();
    
    /**
     * 根据分类编码查询分类
     * 
     * @param categoryCode 分类编码
     * @return 分类信息
     */
    @Select("SELECT * FROM material_category WHERE category_code = #{categoryCode} AND deleted = 0")
    MaterialCategory selectByCategoryCode(@Param("categoryCode") String categoryCode);
    
    /**
     * 检查分类编码是否存在（排除指定ID）
     * 
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM material_category WHERE category_code = #{categoryCode} AND id != #{excludeId} AND deleted = 0")
    int countByCategoryCodeExcludeId(@Param("categoryCode") String categoryCode, @Param("excludeId") Long excludeId);
    
    /**
     * 检查分类下是否有子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    @Select("SELECT COUNT(*) FROM material_category WHERE parent_id = #{parentId} AND deleted = 0")
    int countChildrenByParentId(@Param("parentId") Long parentId);
}
