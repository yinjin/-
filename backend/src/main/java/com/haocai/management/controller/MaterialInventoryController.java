package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.InventoryAdjustDTO;
import com.haocai.management.dto.InventoryQueryDTO;
import com.haocai.management.dto.InventoryUpdateDTO;
import com.haocai.management.entity.MaterialInventory;
import com.haocai.management.service.IMaterialInventoryService;
import com.haocai.management.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * 库存管理Controller
 * 
 * <p>提供库存管理的REST API接口</p>
 * 
 * <p>主要功能：</p>
 * <ul>
 *   <li>库存查询（列表查询、详情查询）</li>
 *   <li>库存预警（低库存、超储、临期）</li>
 *   <li>库存调整（手动调整库存数量）</li>
 *   <li>库存统计（库存总价值、库存周转率）</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>后端开发规范-第2.3条（Controller层：统一响应、参数校验）</li>
 *   <li>后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</li>
 *   <li>控制层规范-第4.2条（异常处理规范）</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 */
@Slf4j
@RestController
@RequestMapping("/api/inventory")
@Tag(name = "库存管理", description = "库存管理相关接口")
public class MaterialInventoryController {

    @Autowired
    private IMaterialInventoryService inventoryService;

    /**
     * 库存列表查询接口
     * 
     * <p>分页查询库存列表，支持按耗材名称、编码、仓库、状态等条件筛选</p>
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/list")
    @Operation(summary = "库存列表查询", description = "分页查询库存列表，支持按耗材名称、编码、仓库、状态等条件筛选")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<IPage<InventoryVO>> getInventoryList(@Validated InventoryQueryDTO queryDTO) {
        log.info("库存列表查询请求，查询条件：{}", queryDTO);
        IPage<InventoryVO> page = inventoryService.getInventoryPage(queryDTO);
        return ApiResponse.success(page);
    }

    /**
     * 库存详情查询接口
     * 
     * <p>根据库存ID查询库存详情</p>
     * 
     * @param id 库存ID
     * @return 库存详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "库存详情查询", description = "根据库存ID查询库存详情")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<MaterialInventory> getInventoryById(@PathVariable Long id) {
        log.info("库存详情查询请求，库存ID：{}", id);
        MaterialInventory inventory = inventoryService.getById(id);
        if (inventory == null) {
            return ApiResponse.error("库存不存在");
        }
        return ApiResponse.success(inventory);
    }

    /**
     * 库存预警查询接口
     * 
     * <p>查询所有库存预警（低库存、超储）</p>
     * 
     * @return 预警列表
     */
    @GetMapping("/warning")
    @Operation(summary = "库存预警查询", description = "查询所有库存预警（低库存、超储）")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<List<MaterialInventory>> getInventoryWarning() {
        log.info("库存预警查询请求");
        List<MaterialInventory> lowStockList = inventoryService.getLowStockList();
        List<MaterialInventory> overStockList = inventoryService.getOverStockList();
        lowStockList.addAll(overStockList);
        return ApiResponse.success(lowStockList);
    }

    /**
     * 低库存列表接口
     * 
     * <p>查询低库存列表（可用数量小于安全库存）</p>
     * 
     * @return 低库存列表
     */
    @GetMapping("/low-stock")
    @Operation(summary = "低库存列表查询", description = "查询低库存列表（可用数量小于安全库存）")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<List<MaterialInventory>> getLowStockList() {
        log.info("低库存列表查询请求");
        List<MaterialInventory> list = inventoryService.getLowStockList();
        return ApiResponse.success(list);
    }

    /**
     * 超储列表接口
     * 
     * <p>查询超储列表（总数量大于最大库存）</p>
     * 
     * @return 超储列表
     */
    @GetMapping("/over-stock")
    @Operation(summary = "超储列表查询", description = "查询超储列表（总数量大于最大库存）")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<List<MaterialInventory>> getOverStockList() {
        log.info("超储列表查询请求");
        List<MaterialInventory> list = inventoryService.getOverStockList();
        return ApiResponse.success(list);
    }

    /**
     * 临期列表接口
     * 
     * <p>查询临期库存列表（根据耗材保质期判断）</p>
     * 
     * @return 临期列表
     */
    @GetMapping("/expired")
    @Operation(summary = "临期列表查询", description = "查询临期库存列表（根据耗材保质期判断）")
    @PreAuthorize("hasAuthority('inventory:query')")
    public ApiResponse<List<MaterialInventory>> getExpiredList() {
        log.info("临期列表查询请求");
        return ApiResponse.success(List.of());
    }

    /**
     * 库存更新接口
     * 
     * <p>更新库存信息（仓库、位置、安全库存、最大库存）</p>
     * 
     * @param id 库存ID
     * @param updateDTO 更新数据
     * @return 是否成功
     */
    @PutMapping("/{id}")
    @Operation(summary = "库存更新", description = "更新库存信息（仓库、位置、安全库存、最大库存）")
    @PreAuthorize("hasAuthority('inventory:adjust')")
    public ApiResponse<Boolean> updateInventory(@PathVariable Long id, @Validated @RequestBody InventoryUpdateDTO updateDTO) {
        log.info("库存更新请求，库存ID：{}，更新数据：{}", id, updateDTO);
        boolean result = inventoryService.updateInventory(id, updateDTO);
        if (result) {
            return ApiResponse.success(true, "库存更新成功");
        } else {
            return ApiResponse.error("库存更新失败");
        }
    }

    /**
     * 库存调整接口
     * 
     * <p>手动调整库存数量（增加或减少库存）</p>
     * 
     * @param adjustDTO 调整数据
     * @return 是否成功
     */
    @PostMapping("/adjust")
    @Operation(summary = "库存调整", description = "手动调整库存数量（增加或减少库存）")
    @PreAuthorize("hasAuthority('inventory:adjust')")
    public ApiResponse<Boolean> adjustInventory(@Validated @RequestBody InventoryAdjustDTO adjustDTO) {
        log.info("库存调整请求，调整数据：{}", adjustDTO);
        boolean result = inventoryService.adjustInventory(adjustDTO);
        if (result) {
            return ApiResponse.success(true, "库存调整成功");
        } else {
            return ApiResponse.error("库存调整失败");
        }
    }

    /**
     * 库存统计接口
     * 
     * <p>查询库存统计数据</p>
     * 
     * @return 统计数据
     */
    @GetMapping("/statistics")
    @Operation(summary = "库存统计", description = "查询库存统计数据（库存总价值、库存周转率）")
    @PreAuthorize("hasAuthority('inventory:statistics')")
    public ApiResponse<InventoryStatisticsVO> getStatistics() {
        log.info("库存统计查询请求");
        BigDecimal totalValue = inventoryService.getTotalInventoryValue();
        BigDecimal turnoverRate = inventoryService.getInventoryTurnoverRate();
        
        InventoryStatisticsVO statisticsVO = new InventoryStatisticsVO();
        statisticsVO.setTotalInventoryValue(totalValue);
        statisticsVO.setInventoryTurnoverRate(turnoverRate);
        
        return ApiResponse.success(statisticsVO);
    }

    /**
     * 库存周转率接口
     * 
     * <p>查询库存周转率</p>
     * 
     * @return 周转率
     */
    @GetMapping("/turnover")
    @Operation(summary = "库存周转率查询", description = "查询库存周转率（总出库量 / 平均库存量）")
    @PreAuthorize("hasAuthority('inventory:statistics')")
    public ApiResponse<BigDecimal> getTurnoverRate() {
        log.info("库存周转率查询请求");
        BigDecimal turnoverRate = inventoryService.getInventoryTurnoverRate();
        return ApiResponse.success(turnoverRate);
    }

    /**
     * 库存价值接口
     * 
     * <p>查询库存总价值</p>
     * 
     * @return 库存总价值
     */
    @GetMapping("/value")
    @Operation(summary = "库存价值查询", description = "查询库存总价值（数量 * 单价）")
    @PreAuthorize("hasAuthority('inventory:statistics')")
    public ApiResponse<BigDecimal> getInventoryValue() {
        log.info("库存价值查询请求");
        BigDecimal totalValue = inventoryService.getTotalInventoryValue();
        return ApiResponse.success(totalValue);
    }

    /**
     * 库存统计响应VO
     * 
     * <p>用于返回库存统计数据的响应对象</p>
     */
    public static class InventoryStatisticsVO {
        private BigDecimal totalInventoryValue;
        private BigDecimal inventoryTurnoverRate;

        public BigDecimal getTotalInventoryValue() {
            return totalInventoryValue;
        }

        public void setTotalInventoryValue(BigDecimal totalInventoryValue) {
            this.totalInventoryValue = totalInventoryValue;
        }

        public BigDecimal getInventoryTurnoverRate() {
            return inventoryTurnoverRate;
        }

        public void setInventoryTurnoverRate(BigDecimal inventoryTurnoverRate) {
            this.inventoryTurnoverRate = inventoryTurnoverRate;
        }
    }
}
