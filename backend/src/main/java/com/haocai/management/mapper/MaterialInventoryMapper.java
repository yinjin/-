package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.InventoryQueryDTO;
import com.haocai.management.entity.MaterialInventory;
import com.haocai.management.vo.InventoryVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 库存Mapper接口
 * 
 * <p>提供库存相关的数据访问操作</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>基础CRUD操作（继承BaseMapper）</li>
 *   <li>自定义查询方法（根据耗材ID、仓库、状态查询）</li>
 *   <li>库存预警查询方法（低库存、超储、临期）</li>
 *   <li>库存统计查询方法（库存总价值、库存周转率）</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>数据访问层规范-第3.1条（批量操作规范）</li>
 *   <li>数据库设计规范-第1.2条（查询索引：外键字段及高频查询条件字段必须建立索引）</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Mapper
public interface MaterialInventoryMapper extends BaseMapper<MaterialInventory> {

    /**
     * 根据耗材ID查询库存
     * 
     * <p>查询指定耗材在所有仓库的库存情况</p>
     * 
     * @param materialId 耗材ID
     * @return 库存列表
     */
    @Select("SELECT * FROM material_inventory WHERE material_id = #{materialId} AND deleted = 0")
    List<MaterialInventory> selectByMaterialId(@Param("materialId") Long materialId);

    /**
     * 根据仓库查询库存
     * 
     * <p>查询指定仓库的所有库存情况</p>
     * 
     * @param warehouse 仓库编号
     * @return 库存列表
     */
    @Select("SELECT * FROM material_inventory WHERE warehouse = #{warehouse} AND deleted = 0")
    List<MaterialInventory> selectByWarehouse(@Param("warehouse") String warehouse);

    /**
     * 分页查询库存
     * 
     * <p>支持按耗材名称、编码、仓库、状态等条件分页查询</p>
     * 
     * @param page 分页对象
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<InventoryVO> selectInventoryPage(Page<InventoryVO> page, @Param("query") InventoryQueryDTO queryDTO);

    /**
     * 查询低库存列表
     * 
     * <p>查询可用数量小于安全库存的库存记录</p>
     * 
     * @return 低库存列表
     */
    @Select("SELECT * FROM material_inventory WHERE available_quantity < safe_quantity AND deleted = 0")
    List<MaterialInventory> selectLowStockList();

    /**
     * 查询超储列表
     * 
     * <p>查询总数量大于最大库存的库存记录</p>
     * 
     * @return 超储列表
     */
    @Select("SELECT * FROM material_inventory WHERE quantity > max_quantity AND deleted = 0")
    List<MaterialInventory> selectOverStockList();

    /**
     * 统计库存总价值
     * 
     * <p>计算所有库存的总价值（数量 * 单价）</p>
     * 
     * @return 库存总价值
     */
    @Select("SELECT SUM(mi.quantity * m.unit_price) FROM material_inventory mi " +
            "LEFT JOIN material m ON mi.material_id = m.id " +
            "WHERE mi.deleted = 0")
    java.math.BigDecimal selectTotalInventoryValue();

    /**
     * 统计库存周转率
     * 
     * <p>计算库存周转率（总出库量 / 平均库存量）</p>
     * 
     * @return 库存周转率
     */
    @Select("SELECT " +
            "CASE " +
            "  WHEN SUM(total_in_quantity) = 0 THEN 0 " +
            "  ELSE ROUND(SUM(total_out_quantity) * 100.0 / SUM(total_in_quantity), 2) " +
            "END AS turnover_rate " +
            "FROM material_inventory " +
            "WHERE deleted = 0")
    java.math.BigDecimal selectInventoryTurnoverRate();
}
