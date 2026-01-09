package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.entity.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 耗材Mapper接口
 * 
 * 遵循development-standards.md中的数据访问层规范：
 * - Mapper命名规范：使用业务名称+Mapper后缀
 * - 继承BaseMapper：获得MyBatis-Plus提供的CRUD方法
 * - 自定义查询方法：使用@Select注解定义SQL
 * - 参数命名：使用@Param注解明确参数名称
 * 
 * @author haocai
 * @since 2026-01-09
 */
@Mapper
public interface MaterialMapper extends BaseMapper<Material> {
    
    /**
     * 根据分类ID查询耗材列表
     * 
     * @param categoryId 分类ID
     * @return 耗材列表
     */
    @Select("SELECT * FROM material WHERE category_id = #{categoryId} AND deleted = 0 ORDER BY id DESC")
    List<Material> selectByCategoryId(@Param("categoryId") Long categoryId);
    
    /**
     * 根据耗材编码查询耗材
     * 
     * @param materialCode 耗材编码
     * @return 耗材信息
     */
    @Select("SELECT * FROM material WHERE material_code = #{materialCode} AND deleted = 0")
    Material selectByMaterialCode(@Param("materialCode") String materialCode);
    
    /**
     * 检查耗材编码是否存在（排除指定ID）
     * 
     * @param materialCode 耗材编码
     * @param excludeId 排除的耗材ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM material WHERE material_code = #{materialCode} AND id != #{excludeId} AND deleted = 0")
    int countByMaterialCodeExcludeId(@Param("materialCode") String materialCode, @Param("excludeId") Long excludeId);
    
    /**
     * 搜索耗材（根据耗材名称或编码）
     * 
     * @param keyword 搜索关键词
     * @return 耗材列表
     */
    @Select("SELECT * FROM material WHERE (material_name LIKE CONCAT('%', #{keyword}, '%') OR material_code LIKE CONCAT('%', #{keyword}, '%')) AND deleted = 0 ORDER BY id DESC")
    List<Material> searchMaterials(@Param("keyword") String keyword);
    
    /**
     * 分页查询耗材列表（带条件）
     * 使用MyBatis-Plus的QueryWrapper在Service层实现动态查询
     * 
     * @param page 分页对象
     * @param materialName 耗材名称（可选）
     * @param materialCode 耗材编码（可选）
     * @param categoryId 分类ID（可选）
     * @param brand 品牌（可选）
     * @param manufacturer 生产厂家（可选）
     * @param status 状态（可选）
     * @param startTime 开始时间（可选）
     * @param endTime 结束时间（可选）
     * @return 分页结果
     * @deprecated 建议在Service层使用QueryWrapper实现动态查询
     */
    @Deprecated
    IPage<Material> selectPageWithConditions(
            Page<Material> page,
            @Param("materialName") String materialName,
            @Param("materialCode") String materialCode,
            @Param("categoryId") Long categoryId,
            @Param("brand") String brand,
            @Param("manufacturer") String manufacturer,
            @Param("status") Integer status,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
}
