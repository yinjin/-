package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.dto.SupplierQueryDTO;
import com.haocai.management.entity.SupplierInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 供应商信息Mapper接口
 * 
 * 遵循规范：
 * - 数据库设计规范-第1.1条（字段命名规范：下划线命名法）
 * - 数据库设计规范-第1.2条（枚举存储：数据库使用VARCHAR存储枚举名称）
 * - 数据库设计规范-第1.3条（审计字段规范：包含审计字段）
 * - 后端开发规范-第2.1条（继承BaseMapper获得基础CRUD方法）
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Mapper
public interface SupplierInfoMapper extends BaseMapper<SupplierInfo> {
    
    /**
     * 根据信用等级范围查询供应商
     * 
     * 遵循：数据库设计规范-第1.4条（查询索引：外键字段及高频查询条件字段必须建立普通索引）
     * 
     * @param min 最小信用等级
     * @param max 最大信用等级
     * @return 供应商列表
     */
    @Select("SELECT * FROM supplier_info WHERE credit_rating >= #{min} AND credit_rating <= #{max} AND deleted = 0 ORDER BY credit_rating DESC")
    List<SupplierInfo> selectByCreditRatingRange(@Param("min") Integer min, @Param("max") Integer max);
    
    /**
     * 根据合作状态查询供应商
     * 
     * @param status 合作状态（1-合作中，0-已终止）
     * @return 供应商列表
     */
    @Select("SELECT * FROM supplier_info WHERE cooperation_status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<SupplierInfo> selectByCooperationStatus(@Param("status") Integer status);
    
    /**
     * 分页查询供应商列表（带条件）
     * 
     * 遵循：后端开发规范-第2.2条（批量操作：禁止直接批量更新不存在的ID）
     * 注意：动态SQL在 SupplierInfoMapper.xml 中定义
     * 
     * @param query 查询条件
     * @return 供应商列表
     */
    List<SupplierInfo> selectPageList(@Param("query") SupplierQueryDTO query);
    
    /**
     * 模糊搜索供应商名称
     * 
     * @param keyword 搜索关键词
     * @return 供应商列表
     */
    @Select("SELECT * FROM supplier_info WHERE supplier_name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 ORDER BY supplier_name ASC")
    List<SupplierInfo> selectBySupplierNameLike(@Param("keyword") String keyword);
    
    /**
     * 检查供应商编码是否存在（排除指定ID）
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 用于更新时检查编码重复，排除自身
     * 
     * @param supplierCode 供应商编码
     * @param excludeId 排除的供应商ID
     * @return 存在的数量
     */
    @Select("SELECT COUNT(*) FROM supplier_info WHERE supplier_code = #{supplierCode} AND id != #{excludeId} AND deleted = 0")
    int countBySupplierCodeExcludeId(@Param("supplierCode") String supplierCode, @Param("excludeId") Long excludeId);
    
    /**
     * 物理删除供应商（用于唯一索引冲突处理）
     * 
     * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
     * 在插入"唯一键可能重复"的数据前，必须物理删除该键值对应的旧记录
     * 
     * @param supplierCode 供应商编码
     * @return 删除的记录数
     */
    @Select("DELETE FROM supplier_info WHERE supplier_code = #{supplierCode}")
    int physicalDeleteBySupplierCode(@Param("supplierCode") String supplierCode);
    
    /**
     * 统计符合条件的供应商数量
     * 
     * 注意：动态SQL在 SupplierInfoMapper.xml 中定义
     * 
     * @param query 查询条件
     * @return 供应商数量
     */
    int countPageList(@Param("query") SupplierQueryDTO query);
}
