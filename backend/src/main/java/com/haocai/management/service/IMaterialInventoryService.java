package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haocai.management.dto.InventoryAdjustDTO;
import com.haocai.management.dto.InventoryQueryDTO;
import com.haocai.management.dto.InventoryUpdateDTO;
import com.haocai.management.entity.MaterialInventory;
import com.haocai.management.vo.InventoryVO;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存Service接口
 * 
 * <p>定义库存相关的业务方法</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>库存查询（根据耗材ID、仓库、状态查询）</li>
 *   <li>库存更新（入库、出库时更新库存）</li>
 *   <li>库存调整（手动调整库存数量）</li>
 *   <li>库存预警（低库存、超储、临期预警）</li>
 *   <li>库存统计（库存总价值、库存周转率）</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
public interface IMaterialInventoryService {

    /**
     * 根据ID查询库存
     * 
     * @param id 库存ID
     * @return 库存信息
     */
    MaterialInventory getById(Long id);

    /**
     * 分页查询库存
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    IPage<InventoryVO> getInventoryPage(InventoryQueryDTO queryDTO);

    /**
     * 根据耗材ID查询库存列表
     * 
     * @param materialId 耗材ID
     * @return 库存列表
     */
    List<MaterialInventory> getByMaterialId(Long materialId);

    /**
     * 根据仓库查询库存列表
     * 
     * @param warehouse 仓库编号
     * @return 库存列表
     */
    List<MaterialInventory> getByWarehouse(String warehouse);

    /**
     * 更新库存信息
     * 
     * @param id 库存ID
     * @param updateDTO 更新数据
     * @return 是否成功
     */
    boolean updateInventory(Long id, InventoryUpdateDTO updateDTO);

    /**
     * 调整库存数量
     * 
     * @param adjustDTO 调整数据
     * @return 是否成功
     */
    boolean adjustInventory(InventoryAdjustDTO adjustDTO);

    /**
     * 查询低库存列表
     * 
     * @return 低库存列表
     */
    List<MaterialInventory> getLowStockList();

    /**
     * 查询超储列表
     * 
     * @return 超储列表
     */
    List<MaterialInventory> getOverStockList();

    /**
     * 统计库存总价值
     * 
     * @return 库存总价值
     */
    BigDecimal getTotalInventoryValue();

    /**
     * 统计库存周转率
     * 
     * @return 库存周转率
     */
    BigDecimal getInventoryTurnoverRate();

    /**
     * 初始化库存（新增耗材时自动创建库存记录）
     * 
     * @param materialId 耗材ID
     * @param warehouse 仓库编号
     * @return 是否成功
     */
    boolean initInventory(Long materialId, String warehouse);

    /**
     * 入库操作
     * 
     * @param materialId 耗材ID
     * @param warehouse 仓库编号
     * @param quantity 入库数量
     * @return 是否成功
     */
    boolean inbound(Long materialId, String warehouse, Integer quantity);

    /**
     * 出库操作
     * 
     * @param materialId 耗材ID
     * @param warehouse 仓库编号
     * @param quantity 出库数量
     * @return 是否成功
     */
    boolean outbound(Long materialId, String warehouse, Integer quantity);
}
