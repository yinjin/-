package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.InventoryAdjustDTO;
import com.haocai.management.dto.InventoryQueryDTO;
import com.haocai.management.dto.InventoryUpdateDTO;
import com.haocai.management.entity.Material;
import com.haocai.management.entity.MaterialInventory;
import com.haocai.management.enums.InventoryStatus;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.MaterialInventoryMapper;
import com.haocai.management.mapper.MaterialMapper;
import com.haocai.management.service.IMaterialInventoryService;
import com.haocai.management.vo.InventoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 库存Service实现类
 * 
 * <p>实现库存相关的业务逻辑</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>库存查询逻辑（根据耗材ID、仓库、状态查询）</li>
 *   <li>库存更新逻辑（入库、出库时更新库存）</li>
 *   <li>库存调整逻辑（手动调整库存数量）</li>
 *   <li>库存预警逻辑（低库存、超储、临期预警）</li>
 *   <li>库存统计逻辑（库存总价值、库存周转率）</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>后端开发规范-第2.2条（事务管理：涉及多表操作必须添加@Transactional）</li>
 *   <li>后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</li>
 *   <li>数据访问层规范-第3.1条（批量操作规范）</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Slf4j
@Service
public class MaterialInventoryServiceImpl extends ServiceImpl<MaterialInventoryMapper, MaterialInventory> implements IMaterialInventoryService {

    @Autowired
    private MaterialInventoryMapper inventoryMapper;

    @Autowired
    private MaterialMapper materialMapper;

    @Override
    public MaterialInventory getById(Long id) {
        return inventoryMapper.selectById(id);
    }

    @Override
    public IPage<InventoryVO> getInventoryPage(InventoryQueryDTO queryDTO) {
        Page<InventoryVO> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        return inventoryMapper.selectInventoryPage(page, queryDTO);
    }

    @Override
    public List<MaterialInventory> getByMaterialId(Long materialId) {
        return inventoryMapper.selectByMaterialId(materialId);
    }

    @Override
    public List<MaterialInventory> getByWarehouse(String warehouse) {
        return inventoryMapper.selectByWarehouse(warehouse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateInventory(Long id, InventoryUpdateDTO updateDTO) {
        MaterialInventory inventory = inventoryMapper.selectById(id);
        if (inventory == null) {
            throw new BusinessException(1001, "库存不存在");
        }

        if (updateDTO.getWarehouse() != null) {
            inventory.setWarehouse(updateDTO.getWarehouse());
        }
        if (updateDTO.getLocation() != null) {
            inventory.setLocation(updateDTO.getLocation());
        }
        if (updateDTO.getSafeQuantity() != null) {
            inventory.setSafeQuantity(updateDTO.getSafeQuantity());
        }
        if (updateDTO.getMaxQuantity() != null) {
            inventory.setMaxQuantity(updateDTO.getMaxQuantity());
        }

        updateInventoryStatus(inventory);

        int result = inventoryMapper.updateById(inventory);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean adjustInventory(InventoryAdjustDTO adjustDTO) {
        MaterialInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<MaterialInventory>().eq(MaterialInventory::getMaterialId, adjustDTO.getMaterialId())
        );

        if (inventory == null) {
            throw BusinessException.dataNotFound("库存信息不存在");
        }

        Integer adjustQuantity = adjustDTO.getAdjustQuantity();
        if (adjustQuantity == null || adjustQuantity == 0) {
            throw new BusinessException(1009, "调整数量不能为0");
        }

        Integer newQuantity = inventory.getQuantity() + adjustQuantity;
        if (newQuantity < 0) {
            throw new BusinessException(1009, "调整后库存数量不能为负");
        }

        inventory.setQuantity(newQuantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + adjustQuantity);

        updateInventoryStatus(inventory);

        int result = inventoryMapper.updateById(inventory);
        return result > 0;
    }

    @Override
    public List<MaterialInventory> getLowStockList() {
        return inventoryMapper.selectLowStockList();
    }

    @Override
    public List<MaterialInventory> getOverStockList() {
        return inventoryMapper.selectOverStockList();
    }

    @Override
    public BigDecimal getTotalInventoryValue() {
        return inventoryMapper.selectTotalInventoryValue();
    }

    @Override
    public BigDecimal getInventoryTurnoverRate() {
        return inventoryMapper.selectInventoryTurnoverRate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean initInventory(Long materialId, String warehouse) {
        Material material = materialMapper.selectById(materialId);
        if (material == null) {
            throw new BusinessException(1010, "耗材不存在");
        }

        MaterialInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<MaterialInventory>()
                        .eq(MaterialInventory::getMaterialId, materialId)
                        .eq(MaterialInventory::getWarehouse, warehouse)
        );

        if (inventory != null) {
            log.warn("库存已存在，跳过初始化：materialId={}, warehouse={}", materialId, warehouse);
            return true;
        }

        inventory = new MaterialInventory();
        inventory.setMaterialId(materialId);
        inventory.setMaterialName(material.getMaterialName());
        inventory.setMaterialCode(material.getMaterialCode());
        inventory.setQuantity(0);
        inventory.setAvailableQuantity(0);
        inventory.setSafeQuantity(material.getSafetyStock());
        inventory.setMaxQuantity(material.getMaxStock());
        inventory.setWarehouse(warehouse);
        inventory.setTotalInQuantity(0);
        inventory.setTotalOutQuantity(0);
        inventory.setStatus(InventoryStatus.OUT_OF_STOCK.name());

        int result = inventoryMapper.insert(inventory);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean inbound(Long materialId, String warehouse, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(1009, "入库数量必须大于0");
        }

        MaterialInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<MaterialInventory>()
                        .eq(MaterialInventory::getMaterialId, materialId)
                        .eq(MaterialInventory::getWarehouse, warehouse)
        );

        if (inventory == null) {
            throw new BusinessException(1010, "库存不存在，请先初始化库存");
        }

        inventory.setQuantity(inventory.getQuantity() + quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() + quantity);
        inventory.setTotalInQuantity(inventory.getTotalInQuantity() + quantity);
        inventory.setLastInTime(LocalDate.now());

        updateInventoryStatus(inventory);

        int result = inventoryMapper.updateById(inventory);
        return result > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean outbound(Long materialId, String warehouse, Integer quantity) {
        if (quantity == null || quantity <= 0) {
            throw new BusinessException(1009, "出库数量必须大于0");
        }

        MaterialInventory inventory = inventoryMapper.selectOne(
                new LambdaQueryWrapper<MaterialInventory>()
                        .eq(MaterialInventory::getMaterialId, materialId)
                        .eq(MaterialInventory::getWarehouse, warehouse)
        );

        if (inventory == null) {
            throw new BusinessException(1010, "库存不存在");
        }

        if (inventory.getAvailableQuantity() < quantity) {
            throw new BusinessException(1009, "库存不足，当前可用库存：" + inventory.getAvailableQuantity());
        }

        inventory.setQuantity(inventory.getQuantity() - quantity);
        inventory.setAvailableQuantity(inventory.getAvailableQuantity() - quantity);
        inventory.setTotalOutQuantity(inventory.getTotalOutQuantity() + quantity);
        inventory.setLastOutTime(LocalDate.now());

        updateInventoryStatus(inventory);

        int result = inventoryMapper.updateById(inventory);
        return result > 0;
    }

    /**
     * 更新库存状态
     * 
     * <p>根据库存数量自动判断并更新库存状态</p>
     * 
     * <p>判断逻辑：</p>
     * <ul>
     *   <li>如果可用数量为0，状态为缺货</li>
     *   <li>如果可用数量小于安全库存，状态为低库存</li>
     *   <li>如果总数量大于最大库存，状态为超储</li>
     *   <li>否则状态为正常</li>
     * </ul>
     * 
     * @param inventory 库存对象
     */
    private void updateInventoryStatus(MaterialInventory inventory) {
        InventoryStatus status = InventoryStatus.judgeStatus(
                inventory.getAvailableQuantity(),
                inventory.getQuantity(),
                inventory.getSafeQuantity(),
                inventory.getMaxQuantity()
        );
        inventory.setStatus(status.name());
    }
}
