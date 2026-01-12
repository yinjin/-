package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.InventoryAdjustDTO;
import com.haocai.management.dto.InventoryQueryDTO;
import com.haocai.management.dto.InventoryUpdateDTO;
import com.haocai.management.entity.MaterialInventory;
import com.haocai.management.service.IMaterialInventoryService;
import com.haocai.management.vo.InventoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;

/**
 * 库存管理控制器测试类
 * 
 * <p>测试目的：验证库存管理相关接口的正确性与健壮性</p>
 * 
 * <p>测试场景：</p>
 * <ul>
 *   <li>库存列表查询接口测试</li>
 *   <li>库存预警接口测试</li>
 *   <li>库存调整接口测试</li>
 *   <li>库存统计接口测试</li>
 *   <li>边界条件测试</li>
 *   <li>异常处理测试</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>测试规范-第1条：必须测试字段映射、类型转换、批量操作</li>
 *   <li>控制层规范-第4.1条：批量操作接口规范</li>
 *   <li>控制层规范-第4.2条：异常处理规范</li>
 * </ul>
 * 
 * @author haocai
 * @since 2026-01-13
 * @version 2.0
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class MaterialInventoryControllerTest {

    private static final Logger log = LoggerFactory.getLogger(MaterialInventoryControllerTest.class);

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private IMaterialInventoryService inventoryService;

    private MaterialInventory testInventory;
    private InventoryQueryDTO queryDTO;
    private InventoryUpdateDTO updateDTO;
    private InventoryAdjustDTO adjustDTO;
    private IPage<InventoryVO> testPage;

    /**
     * 测试初始化方法
     * 
     * <p>在每个测试方法执行前调用，用于初始化测试数据</p>
     * 
     * <p>初始化内容：</p>
     * <ul>
     *   <li>测试库存对象（包含完整的库存信息）</li>
     *   <li>库存查询DTO（符合查询规则）</li>
     *   <li>库存更新DTO（符合更新规则）</li>
     *   <li>库存调整DTO（符合调整规则）</li>
     *   <li>测试分页数据（用于分页查询）</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() {
        testInventory = new MaterialInventory();
        testInventory.setId(1L);
        testInventory.setMaterialId(1L);
        testInventory.setMaterialName("测试耗材");
        testInventory.setMaterialCode("MAT001");
        testInventory.setQuantity(100);
        testInventory.setAvailableQuantity(100);
        testInventory.setSafeQuantity(50);
        testInventory.setMaxQuantity(200);
        testInventory.setWarehouse("主仓库");
        testInventory.setLocation("A区1排");
        testInventory.setLastInTime(LocalDate.now());
        testInventory.setLastOutTime(LocalDate.now());
        testInventory.setTotalInQuantity(1000);
        testInventory.setTotalOutQuantity(500);
        testInventory.setStatus("NORMAL");
        testInventory.setCreateTime(LocalDateTime.now());
        testInventory.setUpdateTime(LocalDateTime.now());

        queryDTO = new InventoryQueryDTO();
        queryDTO.setMaterialId(1L);
        queryDTO.setMaterialName("测试耗材");
        queryDTO.setMaterialCode("MAT001");
        queryDTO.setWarehouse("主仓库");
        queryDTO.setStatus("NORMAL");
        queryDTO.setCurrent(1);
        queryDTO.setSize(10);
        queryDTO.setOrderBy("id");
        queryDTO.setOrderDirection("desc");

        updateDTO = new InventoryUpdateDTO();
        updateDTO.setMaterialId(1L);
        updateDTO.setWarehouse("主仓库");
        updateDTO.setLocation("A区2排");
        updateDTO.setSafeQuantity(50);
        updateDTO.setMaxQuantity(200);
        updateDTO.setRemark("测试更新");

        adjustDTO = new InventoryAdjustDTO();
        adjustDTO.setMaterialId(1L);
        adjustDTO.setAdjustQuantity(10);
        adjustDTO.setAdjustType("MANUAL");
        adjustDTO.setReason("测试调整");

        testPage = new Page<>(1, 10);
        testPage.setRecords(new ArrayList<>());
        testPage.setTotal(1);
        testPage.setCurrent(1);
        testPage.setSize(10);
        testPage.setPages(1);
    }

    // ==================== 库存列表查询接口测试 ====================

    /**
     * 测试1：库存列表查询接口 - 正常查询
     * 
     * <p>测试目的：验证库存列表查询接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备查询条件</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的分页数据正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回分页数据（total、records等）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryPage_Success() throws Exception {
        System.out.println("\n=== 测试1：库存列表查询接口 - 正常查询 ===");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：库存列表查询接口正常");
    }

    /**
     * 测试2：库存列表查询接口 - 空结果查询
     * 
     * <p>测试目的：验证空结果查询的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备查询条件</li>
     *   <li>Mock Service层返回空分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回空数据</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryPage_EmptyResult() throws Exception {
        System.out.println("\n=== 测试2：库存列表查询接口 - 空结果查询 ===");
        IPage<InventoryVO> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0);
        emptyPage.setCurrent(1);
        emptyPage.setSize(10);
        emptyPage.setPages(0);
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(emptyPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：空结果查询正常");
    }

    /**
     * 测试3：库存列表查询接口 - 带筛选条件查询
     * 
     * <p>测试目的：验证带筛选条件的查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备带筛选条件的查询DTO</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证筛选条件生效</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回筛选后的数据</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第1条（测试覆盖范围：必须测试字段映射、类型转换、批量操作）</p>
     */
    @Test
    public void testGetInventoryPage_WithFilters() throws Exception {
        System.out.println("\n=== 测试3：库存列表查询接口 - 带筛选条件查询 ===");
        queryDTO.setWarehouse("主仓库");
        queryDTO.setStatus("NORMAL");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10")
                .param("warehouse", "主仓库")
                .param("status", "NORMAL"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
        System.out.println("✓ 测试通过：带筛选条件查询正常");
    }

    /**
     * 测试4：库存列表查询接口 - 带排序查询
     * 
     * <p>测试目的：验证排序功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备带排序参数的查询DTO</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证排序生效</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回排序后的数据</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第1条（测试覆盖范围：必须测试字段映射、类型转换、批量操作）</p>
     */
    @Test
    public void testGetInventoryPage_WithSorting() throws Exception {
        System.out.println("\n=== 测试4：库存列表查询接口 - 带排序查询 ===");
        queryDTO.setOrderBy("quantity");
        queryDTO.setOrderDirection("desc");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10")
                .param("orderBy", "quantity")
                .param("orderDirection", "desc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        System.out.println("✓ 测试通过：带排序查询正常");
    }

    /**
     * 测试5：库存列表查询接口 - 分页参数验证
     * 
     * <p>测试目的：验证分页参数的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备分页查询参数</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证分页信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回分页数据（total、records等）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    public void testGetInventoryPage_WithPagination() throws Exception {
        System.out.println("\n=== 测试5：库存列表查询接口 - 分页参数验证 ===");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：分页参数验证正常");
    }

    // ==================== 库存预警接口测试 ====================

    /**
     * 测试6：库存预警查询接口 - 获取全部预警
     * 
     * <p>测试目的：验证库存预警查询接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回低库存列表</li>
     *   <li>Mock Service层返回超储列表</li>
     *   <li>发送GET请求到/api/inventory/warning</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的预警列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回预警列表（包含低库存和超储）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryWarning_Success() throws Exception {
        System.out.println("\n=== 测试6：库存预警查询接口 - 获取全部预警 ===");
        List<MaterialInventory> lowStockList = new ArrayList<>();
        lowStockList.add(testInventory);
        List<MaterialInventory> overStockList = new ArrayList<>();
        when(inventoryService.getLowStockList()).thenReturn(lowStockList);
        when(inventoryService.getOverStockList()).thenReturn(overStockList);
        mockMvc.perform(get("/api/inventory/warning"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        System.out.println("✓ 测试通过：库存预警查询接口正常");
    }

    /**
     * 测试7：库存预警查询接口 - 获取低库存列表
     * 
     * <p>测试目的：验证低库存列表查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回低库存列表</li>
     *   <li>发送GET请求到/api/inventory/low-stock</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的库存列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回低库存列表</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetLowStockList_Success() throws Exception {
        System.out.println("\n=== 测试7：库存预警查询接口 - 获取低库存列表 ===");
        List<MaterialInventory> lowStockList = new ArrayList<>();
        lowStockList.add(testInventory);
        when(inventoryService.getLowStockList()).thenReturn(lowStockList);
        mockMvc.perform(get("/api/inventory/low-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        System.out.println("✓ 测试通过：低库存列表查询正常");
    }

    /**
     * 测试8：库存预警查询接口 - 获取超储列表
     * 
     * <p>测试目的：验证超储列表查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回超储列表</li>
     *   <li>发送GET请求到/api/inventory/over-stock</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的库存列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回超储列表</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetOverStockList_Success() throws Exception {
        System.out.println("\n=== 测试8：库存预警查询接口 - 获取超储列表 ===");
        List<MaterialInventory> overStockList = new ArrayList<>();
        overStockList.add(testInventory);
        when(inventoryService.getOverStockList()).thenReturn(overStockList);
        mockMvc.perform(get("/api/inventory/over-stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        System.out.println("✓ 测试通过：超储列表查询正常");
    }

    /**
     * 测试9：库存预警查询接口 - 获取临期列表
     * 
     * <p>测试目的：验证临期列表查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回空列表</li>
     *   <li>发送GET请求到/api/inventory/expired</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回空列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空列表</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetExpiredList_Success() throws Exception {
        System.out.println("\n=== 测试9：库存预警查询接口 - 获取临期列表 ===");
        mockMvc.perform(get("/api/inventory/expired"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
        System.out.println("✓ 测试通过：临期列表查询正常");
    }

    // ==================== 库存调整接口测试 ====================

    /**
     * 测试10：库存调整接口 - 正常调整（正数）
     * 
     * <p>测试目的：验证库存调整接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备调整数据（正数调整）</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送POST请求到/api/inventory/adjust</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回成功消息</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回成功消息："库存调整成功"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testAdjustInventory_Success() throws Exception {
        System.out.println("\n=== 测试10：库存调整接口 - 正常调整（正数） ===");
        adjustDTO.setAdjustQuantity(10);
        when(inventoryService.adjustInventory(any(InventoryAdjustDTO.class))).thenReturn(true);
        mockMvc.perform(post("/api/inventory/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(containsString("库存调整成功")));
        System.out.println("✓ 测试通过：库存调整接口正常");
    }

    /**
     * 测试11：库存调整接口 - 负数调整（减少库存）
     * 
     * <p>测试目的：验证库存减少的调整功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备调整数据（负数调整）</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送POST请求到/api/inventory/adjust</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回成功消息</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回成功消息："库存调整成功"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testAdjustInventory_NegativeQuantity() throws Exception {
        System.out.println("\n=== 测试11：库存调整接口 - 负数调整（减少库存） ===");
        adjustDTO.setAdjustQuantity(-10);
        when(inventoryService.adjustInventory(any(InventoryAdjustDTO.class))).thenReturn(true);
        mockMvc.perform(post("/api/inventory/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value(containsString("库存调整成功")));
        System.out.println("✓ 测试通过：库存调整接口正常");
    }

    /**
     * 测试12：库存调整接口 - 库存不足异常
     * 
     * <p>测试目的：验证库存不足时的调整处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备调整数据（调整数量超过当前库存）</li>
     *   <li>Mock Service层抛出业务异常</li>
     *   <li>发送POST请求到/api/inventory/adjust</li>
     *   <li>验证返回业务状态码为500</li>
     *   <li>验证错误消息包含"库存不足"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"库存不足"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分业务异常和系统异常</p>
     */
    @Test
    public void testAdjustInventory_InsufficientStock() throws Exception {
        System.out.println("\n=== 测试12：库存调整接口 - 库存不足异常 ===");
        adjustDTO.setAdjustQuantity(-200);
        when(inventoryService.adjustInventory(any(InventoryAdjustDTO.class)))
                .thenThrow(new RuntimeException("库存不足"));
        mockMvc.perform(post("/api/inventory/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("库存不足")));
        System.out.println("✓ 测试通过：库存不足异常处理正常");
    }

    // ==================== 库存统计接口测试 ====================

    /**
     * 测试13：库存统计接口 - 获取库存统计数据
     * 
     * <p>测试目的：验证库存统计查询接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回统计数据</li>
     *   <li>发送GET请求到/api/inventory/statistics</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的统计数据正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回库存总价值和周转率</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryStatistics_Success() throws Exception {
        System.out.println("\n=== 测试13：库存统计接口 - 获取库存统计数据 ===");
        when(inventoryService.getTotalInventoryValue()).thenReturn(new BigDecimal("10000.00"));
        when(inventoryService.getInventoryTurnoverRate()).thenReturn(new BigDecimal("50.50"));
        mockMvc.perform(get("/api/inventory/statistics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.totalInventoryValue").value(10000.00))
                .andExpect(jsonPath("$.data.inventoryTurnoverRate").value(50.50));
        System.out.println("✓ 测试通过：库存统计接口正常");
    }

    /**
     * 测试14：库存统计接口 - 获取库存价值
     * 
     * <p>测试目的：验证库存价值查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回库存价值</li>
     *   <li>发送GET请求到/api/inventory/value</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的库存价值正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回库存价值</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryValue_Success() throws Exception {
        System.out.println("\n=== 测试14：库存统计接口 - 获取库存价值 ===");
        when(inventoryService.getTotalInventoryValue()).thenReturn(new BigDecimal("10000.00"));
        mockMvc.perform(get("/api/inventory/value"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(10000.00));
        System.out.println("✓ 测试通过：库存价值查询正常");
    }

    /**
     * 测试15：库存统计接口 - 获取库存周转率
     * 
     * <p>测试目的：验证库存周转率查询功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回周转率</li>
     *   <li>发送GET请求到/api/inventory/turnover</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的周转率正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回库存周转率</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryTurnover_Success() throws Exception {
        System.out.println("\n=== 测试15：库存统计接口 - 获取库存周转率 ===");
        when(inventoryService.getInventoryTurnoverRate()).thenReturn(new BigDecimal("50.50"));
        mockMvc.perform(get("/api/inventory/turnover"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(50.50));
        System.out.println("✓ 测试通过：库存周转率查询正常");
    }

    // ==================== 边界条件测试 ====================

    /**
     * 测试16：库存列表查询接口 - 超大页码
     * 
     * <p>测试目的：验证超大页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备超大页码（999999）</li>
     *   <li>Mock Service层返回空分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回空数据</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    public void testGetInventoryPage_LargePageNumber() throws Exception {
        System.out.println("\n=== 测试16：库存列表查询接口 - 超大页码 ===");
        IPage<InventoryVO> emptyPage = new Page<>(999999, 10);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0);
        emptyPage.setCurrent(999999);
        emptyPage.setSize(10);
        emptyPage.setPages(0);
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(emptyPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "999999")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：超大页码处理正常");
    }

    /**
     * 测试17：库存列表查询接口 - 负数页码
     * 
     * <p>测试目的：验证负数页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备负数页码（-1）</li>
     *   <li>Mock Service层返回空的分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回空数据或由MyBatis-Plus处理负数页码</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据或由MyBatis-Plus处理负数页码</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     * <p>注：控制器不验证页码参数，负数页码由MyBatis-Plus的Page类处理，通常返回空数据</p>
     */
    @Test
    public void testGetInventoryPage_NegativePageNumber() throws Exception {
        System.out.println("\n=== 测试17：库存列表查询接口 - 负数页码 ===");
        IPage<InventoryVO> emptyPage = new Page<>(1, 10);
        emptyPage.setRecords(new ArrayList<>());
        emptyPage.setTotal(0);
        emptyPage.setCurrent(1);
        emptyPage.setSize(10);
        emptyPage.setPages(0);
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(emptyPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "-1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：负数页码处理正常");
    }

    /**
     * 测试18：库存列表查询接口 - 空参数
     * 
     * <p>测试目的：验证空参数的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备空参数的查询DTO</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回分页数据</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回分页数据</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    public void testGetInventoryPage_EmptyParameters() throws Exception {
        System.out.println("\n=== 测试18：库存列表查询接口 - 空参数 ===");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：空参数处理正常");
    }

    /**
     * 测试19：库存列表查询接口 - 无效库存状态
     * 
     * <p>测试目的：验证无效库存状态的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备无效库存状态的查询DTO</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/inventory/list</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回分页数据</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回分页数据</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    public void testGetInventoryPage_InvalidStatus() throws Exception {
        System.out.println("\n=== 测试19：库存列表查询接口 - 无效库存状态 ===");
        queryDTO.setStatus("INVALID_STATUS");
        when(inventoryService.getInventoryPage(any(InventoryQueryDTO.class))).thenReturn(testPage);
        mockMvc.perform(get("/api/inventory/list")
                .param("page", "1")
                .param("size", "10")
                .param("status", "INVALID_STATUS"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());
        System.out.println("✓ 测试通过：无效库存状态处理正常");
    }

    // ==================== 异常处理测试 ====================

    /**
     * 测试20：库存详情查询接口 - 库存不存在
     * 
     * <p>测试目的：验证库存不存在时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回null</li>
     *   <li>发送GET请求到/api/inventory/{id}（不存在的ID）</li>
     *   <li>验证返回业务状态码为404</li>
     *   <li>验证错误消息："库存不存在"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：404</li>
     *   <li>错误消息："库存不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    public void testGetInventoryById_NotFound() throws Exception {
        System.out.println("\n=== 测试20：库存详情查询接口 - 库存不存在 ===");
        when(inventoryService.getById(999L)).thenReturn(null);
        mockMvc.perform(get("/api/inventory/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(404))
                .andExpect(jsonPath("$.message").value("库存不存在"));
        System.out.println("✓ 测试通过：库存不存在处理正常");
    }

    /**
     * 测试21：库存调整接口 - 耗材不存在
     * 
     * <p>测试目的：验证调整不存在的库存时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出业务异常</li>
     *   <li>发送POST请求到/api/inventory/adjust</li>
     *   <li>验证返回业务状态码为500</li>
     *   <li>验证错误消息包含"库存不存在"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"库存不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分业务异常和系统异常</p>
     */
    @Test
    public void testAdjustInventory_NotFound() throws Exception {
        System.out.println("\n=== 测试21：库存调整接口 - 耗材不存在 ===");
        adjustDTO.setMaterialId(999L);
        when(inventoryService.adjustInventory(any(InventoryAdjustDTO.class)))
                .thenThrow(new RuntimeException("库存不存在"));
        mockMvc.perform(post("/api/inventory/adjust")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adjustDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("库存不存在")));
        System.out.println("✓ 测试通过：耗材不存在异常处理正常");
    }

    /**
     * 测试22：库存更新接口 - 更新不存在的库存
     * 
     * <p>测试目的：验证更新不存在的库存时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出业务异常</li>
     *   <li>发送PUT请求到/api/inventory/{id}（不存在的ID）</li>
     *   <li>验证返回业务状态码为500</li>
     *   <li>验证错误消息包含"库存不存在"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"库存不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>注：控制器捕获所有异常并返回500，不区分业务异常和系统异常</p>
     */
    @Test
    public void testUpdateInventory_NotFound() throws Exception {
        System.out.println("\n=== 测试22：库存更新接口 - 更新不存在的库存 ===");
        when(inventoryService.updateInventory(eq(999L), any(InventoryUpdateDTO.class)))
                .thenThrow(new RuntimeException("库存不存在"));
        mockMvc.perform(put("/api/inventory/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("库存不存在")));
        System.out.println("✓ 测试通过：更新不存在库存处理正常");
    }

    /**
     * 主测试方法：运行所有测试
     * 
     * <p>此方法用于演示测试套件的功能</p>
     * 
     * <p>实际测试运行请使用JUnit或Maven命令</p>
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("库存管理控制器测试套件 v2.0");
        System.out.println("========================================");
        System.out.println("\n测试覆盖范围：");
        System.out.println("1. 库存列表查询接口测试（5个测试用例）");
        System.out.println("2. 库存预警接口测试（4个测试用例）");
        System.out.println("3. 库存调整接口测试（3个测试用例）");
        System.out.println("4. 库存统计接口测试（3个测试用例）");
        System.out.println("5. 边界条件测试（4个测试用例）");
        System.out.println("6. 异常处理测试（3个测试用例）");
        System.out.println("\n总计：22个测试用例");
        System.out.println("\n========================================");
        System.out.println("运行方式：");
        System.out.println("1. IDE中右键运行此测试类");
        System.out.println("2. Maven命令：mvn test -Dtest=MaterialInventoryControllerTest");
        System.out.println("3. 生成覆盖率报告：mvn clean test jacoco:report");
        System.out.println("========================================");
    }
}
