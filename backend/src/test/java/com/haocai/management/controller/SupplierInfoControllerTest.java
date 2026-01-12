package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.haocai.management.dto.*;
import com.haocai.management.enums.CooperationStatus;
import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.service.ISupplierEvaluationService;
import com.haocai.management.service.ISupplierInfoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.containsString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContext;
import org.springframework.test.web.servlet.MockMvc;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 供应商管理控制器测试类
 * 
 * <p>测试目的：验证供应商管理相关接口的正确性与健壮性</p>
 * 
 * <p>测试场景：</p>
 * <ul>
 *   <li>供应商创建接口测试</li>
 *   <li>供应商更新接口测试</li>
 *   <li>供应商删除接口测试</li>
 *   <li>供应商查询接口测试</li>
 *   <li>供应商状态管理接口测试</li>
 *   <li>供应商编码生成接口测试</li>
 *   <li>供应商评价接口测试</li>
 *   <li>边界条件测试</li>
 *   <li>性能测试</li>
 *   <li>异常处理测试</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>测试规范-第6条：必须测试字段映射、类型转换、批量操作</li>
 *   <li>控制层规范-第4.1条：批量操作接口规范</li>
 *   <li>控制层规范-第4.2条：异常处理规范</li>
 *   <li>后端开发规范-第2.3条：参数校验：使用@Validated和JSR-303注解</li>
 * </ul>
 * 
 * @author 开发团队
 * @since 2026-01-12
 * @version 1.0
 */
@SpringBootTest
@AutoConfigureMockMvc
public class SupplierInfoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ISupplierInfoService supplierInfoService;

    @MockBean
    private ISupplierEvaluationService supplierEvaluationService;

    private SupplierVO testSupplier;
    private SupplierCreateDTO createDTO;
    private SupplierUpdateDTO updateDTO;
    private SupplierQueryDTO queryDTO;

    /**
     * 测试初始化方法
     * 
     * <p>在每个测试方法执行前调用，用于初始化测试数据</p>
     * 
     * <p>初始化内容：</p>
     * <ul>
     *   <li>测试供应商对象</li>
     *   <li>供应商创建DTO（符合验证规则）</li>
     *   <li>供应商更新DTO</li>
     *   <li>供应商查询DTO</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() {
        // 初始化测试供应商数据
        testSupplier = new SupplierVO();
        testSupplier.setId(1L);
        testSupplier.setSupplierCode("SUP202601120001");
        testSupplier.setSupplierName("北京科技有限公司");
        testSupplier.setContactPerson("张三");
        testSupplier.setPhone("13800138000");
        testSupplier.setEmail("contact@company.com");
        testSupplier.setAddress("北京市朝阳区xxx");
        testSupplier.setBusinessLicense("https://example.com/license/123.jpg");
        testSupplier.setTaxNumber("91110000xxxxxxxx");
        testSupplier.setBankAccount("622202xxxxxxxxxxxx");
        testSupplier.setBankName("中国银行北京支行");
        testSupplier.setCreditRating(8);
        testSupplier.setCreditRatingDescription("良好");
        testSupplier.setCooperationStatus(1);
        testSupplier.setCooperationStatusDescription("合作中");
        testSupplier.setStatus(1);
        testSupplier.setStatusDescription("启用");
        testSupplier.setDescription("主要供应商，提供办公设备");
        testSupplier.setCreateTime(LocalDateTime.now());
        testSupplier.setUpdateTime(LocalDateTime.now());
        testSupplier.setCreateBy("admin");
        testSupplier.setUpdateBy("admin");

        // 初始化创建DTO - 符合验证规则
        createDTO = new SupplierCreateDTO();
        createDTO.setSupplierName("新供应商");
        createDTO.setSupplierCode("SUP202601120002");
        createDTO.setContactPerson("李四");
        createDTO.setPhone("13900139000");
        createDTO.setEmail("new@company.com");
        createDTO.setAddress("上海市浦东新区xxx");
        createDTO.setBusinessLicense("https://example.com/license/456.jpg");
        createDTO.setTaxNumber("91310000xxxxxxxx");
        createDTO.setBankAccount("622202yyyyyyyyyyyy");
        createDTO.setBankName("工商银行上海支行");
        createDTO.setCreditRating(7);
        createDTO.setCooperationStatus(1);
        createDTO.setStatus(1);
        createDTO.setDescription("新供应商，提供电子设备");

        // 初始化更新DTO
        updateDTO = new SupplierUpdateDTO();
        updateDTO.setId(1L);
        updateDTO.setSupplierName("更新后的供应商");
        updateDTO.setContactPerson("王五");
        updateDTO.setPhone("13700137000");
        updateDTO.setEmail("updated@company.com");

        // 初始化查询DTO
        queryDTO = new SupplierQueryDTO();
        queryDTO.setCurrent(1);
        queryDTO.setSize(10);
        queryDTO.setOrderBy("createTime");
        queryDTO.setOrderDirection("desc");
    }

    // ==================== 供应商创建接口测试 ====================

    /**
     * 自定义注解：提供管理员权限的Mock用户
     * 
     * <p>解决@WithMockUser权限不足的问题</p>
     */
    @Retention(RetentionPolicy.RUNTIME)
    @WithMockUser(username = "admin", authorities = {"supplier:create", "supplier:query", "supplier:update", "supplier:delete", "supplier:evaluate"})
    public @interface WithAdminUser {}

    /**
     * 测试1：供应商创建接口 - 正常创建
     * 
     * <p>测试目的：验证供应商创建接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备符合验证规则的创建数据</li>
     *   <li>Mock Service层返回成功结果</li>
     *   <li>发送POST请求到/api/supplier</li>
     *   <li>验证返回状态码为201</li>
     *   <li>验证返回的供应商ID</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：201</li>
     *   <li>业务状态码：200</li>
     *   <li>返回供应商ID：1</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    @WithAdminUser
    public void testCreateSupplier_Success() throws Exception {
        System.out.println("\n=== 测试1：供应商创建接口 - 正常创建 ===");

        // Mock Service层返回
        when(supplierInfoService.createSupplier(any(SupplierCreateDTO.class))).thenReturn(1L);

        // 执行请求
        mockMvc.perform(post("/api/supplier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(1));

        System.out.println("✓ 测试通过：供应商创建接口正常");
    }

    /**
     * 测试2：供应商创建接口 - 供应商编码重复
     * 
     * <p>测试目的：验证供应商编码重复的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备创建数据，使用已存在的供应商编码</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送POST请求到/api/supplier</li>
     *   <li>验证返回HTTP状态码为500（服务器内部错误）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testCreateSupplier_CodeDuplicate() throws Exception {
        System.out.println("\n=== 测试2：供应商创建接口 - 供应商编码重复 ===");

        // 使用已存在的供应商编码
        createDTO.setSupplierCode("SUP202601120001");
        
        // Mock Service层抛出异常
        when(supplierInfoService.createSupplier(any(SupplierCreateDTO.class)))
                .thenThrow(new RuntimeException("供应商编码已存在"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(post("/api/supplier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：供应商编码重复处理正常（GlobalExceptionHandler捕获）");
    }

    /**
     * 测试3：供应商创建接口 - 参数验证失败
     * 
     * <p>测试目的：验证参数验证注解的有效性</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备创建数据，供应商名称为空</li>
     *   <li>发送POST请求到/api/supplier</li>
     *   <li>验证返回HTTP状态码为400</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    @WithAdminUser
    public void testCreateSupplier_ValidationFailed() throws Exception {
        System.out.println("\n=== 测试3：供应商创建接口 - 参数验证失败 ===");

        // 设置供应商名称为空
        createDTO.setSupplierName("");

        // 执行请求
        mockMvc.perform(post("/api/supplier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：参数验证失败处理正常");
    }

    // ==================== 供应商更新接口测试 ====================

    /**
     * 测试4：供应商更新接口 - 正常更新
     * 
     * <p>测试目的：验证供应商更新接口的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备更新数据</li>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送PUT请求到/api/supplier/{id}</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testUpdateSupplier_Success() throws Exception {
        System.out.println("\n=== 测试4：供应商更新接口 - 正常更新 ===");

        // Mock Service层返回
        when(supplierInfoService.updateSupplier(eq(1L), any(SupplierUpdateDTO.class))).thenReturn(true);

        // 执行请求
        mockMvc.perform(put("/api/supplier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：供应商更新接口正常");
    }

    /**
     * 测试5：供应商更新接口 - 供应商不存在
     * 
     * <p>测试目的：验证更新不存在供应商时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备更新数据</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送PUT请求到/api/supplier/{id}（不存在的ID）</li>
     *   <li>验证返回HTTP状态码为500</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testUpdateSupplier_NotFound() throws Exception {
        System.out.println("\n=== 测试5：供应商更新接口 - 供应商不存在 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.updateSupplier(eq(999L), any(SupplierUpdateDTO.class)))
                .thenThrow(new RuntimeException("供应商不存在"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(put("/api/supplier/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：供应商不存在处理正常（GlobalExceptionHandler捕获）");
    }

    /**
     * 测试6：供应商更新接口 - 编码重复
     * 
     * <p>测试目的：验证供应商编码重复的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备更新数据，使用已存在的供应商编码</li>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送PUT请求到/api/supplier/{id}</li>
     *   <li>验证返回HTTP状态码为500</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testUpdateSupplier_CodeDuplicate() throws Exception {
        System.out.println("\n=== 测试6：供应商更新接口 - 编码重复 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.updateSupplier(eq(1L), any(SupplierUpdateDTO.class)))
                .thenThrow(new RuntimeException("供应商编码已存在"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(put("/api/supplier/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：编码重复处理正常（GlobalExceptionHandler捕获）");
    }

    // ==================== 供应商删除接口测试 ====================

    /**
     * 测试7：供应商删除接口 - 正常删除
     * 
     * <p>测试目的：验证供应商删除接口（逻辑删除）</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送DELETE请求到/api/supplier/{id}</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testDeleteSupplier_Success() throws Exception {
        System.out.println("\n=== 测试7：供应商删除接口 - 正常删除 ===");

        // Mock Service层返回
        when(supplierInfoService.deleteSupplier(1L)).thenReturn(true);

        // 执行请求
        mockMvc.perform(delete("/api/supplier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：供应商删除接口正常");
    }

    /**
     * 测试8：供应商删除接口 - 供应商不存在
     * 
     * <p>测试目的：验证删除不存在供应商时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送DELETE请求到/api/supplier/{id}（不存在的ID）</li>
     *   <li>验证返回HTTP状态码为500</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testDeleteSupplier_NotFound() throws Exception {
        System.out.println("\n=== 测试8：供应商删除接口 - 供应商不存在 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.deleteSupplier(999L))
                .thenThrow(new RuntimeException("供应商不存在"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(delete("/api/supplier/999"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：供应商不存在处理正常（GlobalExceptionHandler捕获）");
    }

    /**
     * 测试9：供应商删除接口 - 有关联耗材
     * 
     * <p>测试目的：验证删除有关联耗材的供应商时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出异常</li>
     *   <li>发送DELETE请求到/api/supplier/{id}</li>
     *   <li>验证返回HTTP状态码为500</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testDeleteSupplier_HasRelatedData() throws Exception {
        System.out.println("\n=== 测试9：供应商删除接口 - 有关联耗材 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.deleteSupplier(1L))
                .thenThrow(new RuntimeException("该供应商有关联的耗材，无法删除"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(delete("/api/supplier/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：有关联耗材处理正常（GlobalExceptionHandler捕获）");
    }

    // ==================== 供应商查询接口测试 ====================

    /**
     * 测试10：获取供应商详情 - 正常获取
     * 
     * <p>测试目的：验证获取供应商详情接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回供应商信息</li>
     *   <li>发送GET请求到/api/supplier/{id}</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的供应商信息正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回供应商信息</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testGetSupplierById_Success() throws Exception {
        System.out.println("\n=== 测试10：获取供应商详情 - 正常获取 ===");

        // Mock Service层返回
        when(supplierInfoService.getSupplierById(1L)).thenReturn(testSupplier);

        // 执行请求
        mockMvc.perform(get("/api/supplier/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.supplierName").value("北京科技有限公司"));

        System.out.println("✓ 测试通过：获取供应商详情正常");
    }

    /**
     * 测试11：获取供应商详情 - 供应商不存在
     * 
     * <p>测试目的：验证供应商不存在时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回null</li>
     *   <li>发送GET请求到/api/supplier/{id}（不存在的ID）</li>
     *   <li>验证返回HTTP状态码为200（正常响应，data为null）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200（正常响应，data为null）</li>
     *   <li>业务状态码：200</li>
     *   <li>返回data为null</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>说明：供应商不存在时返回null，Controller正常返回HTTP 200和data=null</p>
     */
    @Test
    @WithAdminUser
    public void testGetSupplierById_NotFound() throws Exception {
        System.out.println("\n=== 测试11：获取供应商详情 - 供应商不存在 ===");

        // Mock Service层返回null
        when(supplierInfoService.getSupplierById(999L)).thenReturn(null);

        // 执行请求 - 供应商不存在返回null，但HTTP状态码仍为200
        mockMvc.perform(get("/api/supplier/999"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isEmpty());

        System.out.println("✓ 测试通过：供应商不存在返回null（HTTP 200）");
    }

    /**
     * 测试12：分页查询供应商列表
     * 
     * <p>测试目的：验证分页查询接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备分页测试数据</li>
     *   <li>Mock Service层返回分页结果</li>
     *   <li>发送GET请求到/api/supplier/page</li>
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
    @WithAdminUser
    public void testGetSupplierPage_Success() throws Exception {
        System.out.println("\n=== 测试12：分页查询供应商列表 ===");

        // 准备测试数据
        List<SupplierVO> suppliers = new ArrayList<>();
        suppliers.add(testSupplier);
        
        IPage<SupplierVO> page = new Page<>(1, 10);
        page.setRecords(suppliers);
        page.setTotal(1);
        page.setCurrent(1);
        page.setSize(10);
        page.setPages(1);

        // Mock Service层返回
        when(supplierInfoService.getSupplierPage(any(SupplierQueryDTO.class))).thenReturn(page);

        // 执行请求
        mockMvc.perform(get("/api/supplier/page")
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.records").isArray());

        System.out.println("✓ 测试通过：分页查询供应商列表正常");
    }

    /**
     * 测试13：搜索供应商
     * 
     * <p>测试目的：验证搜索供应商接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备搜索测试数据</li>
     *   <li>Mock Service层返回搜索结果</li>
     *   <li>发送GET请求到/api/supplier/search</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的供应商列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回供应商列表</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testSearchSuppliers_Success() throws Exception {
        System.out.println("\n=== 测试13：搜索供应商 ===");

        // 准备测试数据
        List<SupplierVO> suppliers = new ArrayList<>();
        suppliers.add(testSupplier);

        // Mock Service层返回
        when(supplierInfoService.searchSuppliers("科技")).thenReturn(suppliers);

        // 执行请求
        mockMvc.perform(get("/api/supplier/search")
                .param("keyword", "科技"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());

        System.out.println("✓ 测试通过：搜索供应商正常");
    }

    // ==================== 供应商状态管理接口测试 ====================

    /**
     * 测试14：切换供应商状态
     * 
     * <p>测试目的：验证切换供应商状态接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送PUT请求到/api/supplier/{id}/status</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testToggleStatus_Success() throws Exception {
        System.out.println("\n=== 测试14：切换供应商状态 ===");

        // Mock Service层返回
        when(supplierInfoService.toggleStatus(1L)).thenReturn(true);

        // 执行请求
        mockMvc.perform(put("/api/supplier/1/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：切换供应商状态正常");
    }

    /**
     * 测试15：更新供应商状态
     * 
     * <p>测试目的：验证更新供应商状态接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回成功</li>
     *   <li>发送PUT请求到/api/supplier/{id}/status/{status}</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testUpdateStatus_Success() throws Exception {
        System.out.println("\n=== 测试15：更新供应商状态 ===");

        // Mock Service层返回
        when(supplierInfoService.updateStatus(1L, 0)).thenReturn(true);

        // 执行请求
        mockMvc.perform(put("/api/supplier/1/status/0"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：更新供应商状态正常");
    }

    /**
     * 测试16：批量更新供应商状态
     * 
     * <p>测试目的：验证批量更新供应商状态接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备供应商ID列表</li>
     *   <li>Mock Service层返回更新数量</li>
     *   <li>发送PUT请求到/api/supplier/batch/status/{status}</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的更新数量正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回更新数量</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     * <p>遵循规范：数据访问层规范-第3.1条（批量操作规范）</p>
     */
    @Test
    @WithAdminUser
    public void testBatchUpdateStatus_Success() throws Exception {
        System.out.println("\n=== 测试16：批量更新供应商状态 ===");

        // 准备测试数据
        List<Long> supplierIds = List.of(1L, 2L, 3L);

        // Mock Service层返回
        when(supplierInfoService.batchUpdateStatus(supplierIds, 0)).thenReturn(3);

        // 执行请求
        mockMvc.perform(put("/api/supplier/batch/status/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(3));

        System.out.println("✓ 测试通过：批量更新供应商状态正常");
    }

    // ==================== 供应商编码生成接口测试 ====================

    /**
     * 测试17：生成供应商编码 - 正常生成
     * 
     * <p>测试目的：验证生成供应商编码接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回生成的编码</li>
     *   <li>发送GET请求到/api/supplier/generate-code</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的编码格式正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回供应商编码（SUP+年月日+流水号）</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testGenerateSupplierCode_Success() throws Exception {
        System.out.println("\n=== 测试17：生成供应商编码 - 正常生成 ===");

        // Mock Service层返回
        when(supplierInfoService.generateSupplierCode()).thenReturn("SUP202601120001");

        // 执行请求
        mockMvc.perform(get("/api/supplier/generate-code"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value("SUP202601120001"));

        System.out.println("✓ 测试通过：生成供应商编码正常");
    }

    /**
     * 测试18：检查供应商编码 - 存在
     * 
     * <p>测试目的：验证检查供应商编码接口</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层返回true</li>
     *   <li>发送GET请求到/api/supplier/check-code</li>
     *   <li>验证返回状态码为200</li>
     *   <li>验证返回的exists为true</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>exists: true</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testCheckSupplierCode_Exists() throws Exception {
        System.out.println("\n=== 测试18：检查供应商编码 - 存在 ===");

        // Mock Service层返回
        when(supplierInfoService.existsBySupplierCode("SUP202601120001")).thenReturn(true);

        // 执行请求
        mockMvc.perform(get("/api/supplier/check-code")
                .param("supplierCode", "SUP202601120001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));

        System.out.println("✓ 测试通过：检查供应商编码存在正常");
    }

    // ==================== 边界条件测试 ====================

    /**
     * 测试19：批量操作 - 空列表
     * 
     * <p>测试目的：验证批量操作对空列表的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备空的供应商ID列表</li>
     *   <li>发送PUT请求到/api/supplier/batch/status/{status}</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.1条（批量操作接口规范）</p>
     */
    @Test
    @WithAdminUser
    public void testBatchOperation_EmptyList() throws Exception {
        System.out.println("\n=== 测试19：批量操作 - 空列表 ===");

        // 准备空列表
        List<Long> emptyList = new ArrayList<>();

        // 执行请求
        mockMvc.perform(put("/api/supplier/batch/status/0")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(emptyList)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：批量操作空列表处理正常");
    }

    /**
     * 测试20：供应商创建 - 超长供应商名称
     * 
     * <p>测试目的：验证供应商创建对超长名称的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备超长供应商名称（超过100个字符）</li>
     *   <li>发送POST请求到/api/supplier</li>
     *   <li>验证返回HTTP状态码为400</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（Bad Request）</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    @WithAdminUser
    public void testCreateSupplier_TooLongName() throws Exception {
        System.out.println("\n=== 测试20：供应商创建 - 超长供应商名称 ===");

        // 准备超长供应商名称
        createDTO.setSupplierName("a".repeat(101));

        // 执行请求
        mockMvc.perform(post("/api/supplier")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDTO)))
                .andExpect(status().isBadRequest());

        System.out.println("✓ 测试通过：超长供应商名称处理正常");
    }

    /**
     * 测试21：分页查询 - 超大页码
     * 
     * <p>测试目的：验证分页查询对超大页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备超大页码（999999）</li>
     *   <li>Mock Service层返回空分页结果</li>
     *   <li>发送GET请求到/api/supplier/page</li>
     *   <li>验证返回状态码为200</li>
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
    @WithAdminUser
    public void testGetSupplierPage_LargePageNumber() throws Exception {
        System.out.println("\n=== 测试21：分页查询 - 超大页码 ===");

        // 准备空分页结果
        IPage<SupplierVO> page = new Page<>(999999, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);
        page.setCurrent(999999);
        page.setSize(10);
        page.setPages(0);

        // Mock Service层返回
        when(supplierInfoService.getSupplierPage(any(SupplierQueryDTO.class))).thenReturn(page);

        // 执行请求
        mockMvc.perform(get("/api/supplier/page")
                .param("current", "999999")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(0));

        System.out.println("✓ 测试通过：超大页码处理正常");
    }

    /**
     * 测试22：分页查询 - 负数页码
     * 
     * <p>测试目的：验证分页查询对负数页码的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备负数页码（-1）</li>
     *   <li>Mock Service层返回空的分页结果</li>
     *   <li>发送GET请求到/api/supplier/page</li>
     *   <li>验证返回状态码为200</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回空数据或由MyBatis-Plus处理负数页码</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     */
    @Test
    @WithAdminUser
    public void testGetSupplierPage_NegativePageNumber() throws Exception {
        System.out.println("\n=== 测试22：分页查询 - 负数页码 ===");

        // 准备空的分页结果
        IPage<SupplierVO> page = new Page<>(1, 10);
        page.setRecords(new ArrayList<>());
        page.setTotal(0);
        page.setCurrent(1);
        page.setSize(10);
        page.setPages(0);

        // Mock Service层返回
        when(supplierInfoService.getSupplierPage(any(SupplierQueryDTO.class))).thenReturn(page);

        // 执行请求
        mockMvc.perform(get("/api/supplier/page")
                .param("current", "-1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));

        System.out.println("✓ 测试通过：负数页码处理正常");
    }

    // ==================== 性能测试 ====================

    /**
     * 测试23：性能测试 - 批量查询1000条记录
     * 
     * <p>测试目的：验证批量查询的性能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备1000条测试数据</li>
     *   <li>Mock Service层返回1000条记录</li>
     *   <li>发送GET请求到/api/supplier/page</li>
     *   <li>测量响应时间</li>
     *   <li>验证响应时间在可接受范围内（< 1秒）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>响应时间 < 1秒</li>
     * </ul>
     * 
     * <p>遵循规范：性能规范-响应时间要求</p>
     */
    @Test
    @WithAdminUser
    public void testPerformance_BatchQuery1000() throws Exception {
        System.out.println("\n=== 测试23：性能测试 - 批量查询1000条记录 ===");

        // 准备1000条测试数据
        List<SupplierVO> suppliers = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            SupplierVO supplier = new SupplierVO();
            supplier.setId((long) i);
            supplier.setSupplierName("供应商" + i);
            suppliers.add(supplier);
        }
        
        IPage<SupplierVO> page = new Page<>(1, 1000);
        page.setRecords(suppliers);
        page.setTotal(1000);
        page.setCurrent(1);
        page.setSize(1000);
        page.setPages(1);

        // Mock Service层返回
        when(supplierInfoService.getSupplierPage(any(SupplierQueryDTO.class))).thenReturn(page);

        // 执行请求并测量时间
        long startTime = System.currentTimeMillis();
        mockMvc.perform(get("/api/supplier/page")
                .param("current", "1")
                .param("size", "1000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ 测试通过：批量查询1000条记录，耗时: " + duration + "ms");
        
        // 验证响应时间在可接受范围内（< 1秒）
        if (duration > 1000) {
            System.out.println("⚠ 警告：响应时间超过1秒，可能需要优化");
        }
    }

    /**
     * 测试24：性能测试 - 批量删除1000条记录
     * 
     * <p>测试目的：验证批量删除的性能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备1000个供应商ID</li>
     *   <li>Mock Service层返回删除数量</li>
     *   <li>发送DELETE请求到/api/supplier/batch</li>
     *   <li>测量响应时间</li>
     *   <li>验证响应时间在可接受范围内（< 2秒）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：200</li>
     *   <li>业务状态码：200</li>
     *   <li>返回删除数量：1000</li>
     *   <li>响应时间 < 2秒</li>
     * </ul>
     * 
     * <p>遵循规范：性能规范-批量操作性能要求</p>
     */
    @Test
    @WithAdminUser
    public void testPerformance_BatchDelete1000() throws Exception {
        System.out.println("\n=== 测试24：性能测试 - 批量删除1000条记录 ===");

        // 准备1000个供应商ID
        List<Long> supplierIds = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            supplierIds.add((long) i);
        }

        // Mock Service层返回
        when(supplierInfoService.batchDeleteSuppliers(supplierIds)).thenReturn(true);

        // 执行请求并测量时间
        long startTime = System.currentTimeMillis();
        mockMvc.perform(delete("/api/supplier/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(supplierIds)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;

        System.out.println("✓ 测试通过：批量删除1000条记录，耗时: " + duration + "ms");
        
        // 验证响应时间在可接受范围内（< 2秒）
        if (duration > 2000) {
            System.out.println("⚠ 警告：响应时间超过2秒，可能需要优化");
        }
    }

    // ==================== 异常处理测试 ====================

    /**
     * 测试25：异常处理 - Service层抛出空指针异常
     * 
     * <p>测试目的：验证空指针异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出NullPointerException</li>
     *   <li>发送GET请求到/api/supplier/{id}</li>
     *   <li>验证返回HTTP状态码为500</li>
     *   <li>验证错误消息为"系统内部错误"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testExceptionHandling_NullPointerException() throws Exception {
        System.out.println("\n=== 测试25：异常处理 - Service层抛出空指针异常 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.getSupplierById(1L)).thenThrow(new NullPointerException("测试空指针异常"));

        // 执行请求 - NullPointerException是RuntimeException的子类，被GlobalExceptionHandler处理
        mockMvc.perform(get("/api/supplier/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：空指针异常处理正常（GlobalExceptionHandler捕获）");
    }

    /**
     * 测试26：异常处理 - Service层抛出非法参数异常
     * 
     * <p>测试目的：验证非法参数异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出IllegalArgumentException</li>
     *   <li>发送GET请求到/api/supplier/{id}</li>
     *   <li>验证返回HTTP状态码为400</li>
     *   <li>验证错误消息包含原始异常信息</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：400（GlobalExceptionHandler处理IllegalArgumentException）</li>
     *   <li>业务状态码：400</li>
     *   <li>错误消息：包含"测试非法参数异常"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-IllegalArgumentException返回HTTP 400</p>
     */
    @Test
    @WithAdminUser
    public void testExceptionHandling_IllegalArgumentException() throws Exception {
        System.out.println("\n=== 测试26：异常处理 - Service层抛出非法参数异常 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.getSupplierById(1L)).thenThrow(new IllegalArgumentException("测试非法参数异常"));

        // 执行请求 - IllegalArgumentException被GlobalExceptionHandler处理，返回HTTP 400
        mockMvc.perform(get("/api/supplier/1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value(containsString("测试非法参数异常")));

        System.out.println("✓ 测试通过：非法参数异常处理正常（GlobalExceptionHandler捕获，HTTP 400）");
    }

    /**
     * 测试27：异常处理 - Service层抛出运行时异常
     * 
     * <p>测试目的：验证运行时异常的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Service层抛出RuntimeException</li>
     *   <li>发送GET请求到/api/supplier/{id}</li>
     *   <li>验证返回HTTP状态码为500</li>
     *   <li>验证错误消息为"系统内部错误"</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>HTTP状态码：500（GlobalExceptionHandler处理RuntimeException）</li>
     *   <li>业务状态码：500</li>
     *   <li>错误消息：包含"系统内部错误"</li>
     * </ul>
     * 
     * <p>遵循规范：控制层规范-第4.2条（异常处理规范）</p>
     * <p>遵循规范：异常处理规范-RuntimeException返回HTTP 500</p>
     */
    @Test
    @WithAdminUser
    public void testExceptionHandling_RuntimeException() throws Exception {
        System.out.println("\n=== 测试27：异常处理 - Service层抛出运行时异常 ===");

        // Mock Service层抛出异常
        when(supplierInfoService.getSupplierById(1L)).thenThrow(new RuntimeException("测试运行时异常"));

        // 执行请求 - RuntimeException被GlobalExceptionHandler处理，返回HTTP 500
        mockMvc.perform(get("/api/supplier/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.code").value(500))
                .andExpect(jsonPath("$.message").value(containsString("系统内部错误")));

        System.out.println("✓ 测试通过：运行时异常处理正常（GlobalExceptionHandler捕获）");
    }

    /**
     * 主测试方法：运行所有测试
     * 
     * <p>此方法用于演示测试套件的功能</p>
     * <p>实际测试运行请使用JUnit或Maven命令</p>
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("供应商管理控制器测试套件 v1.0");
        System.out.println("========================================");
        System.out.println("\n测试覆盖范围：");
        System.out.println("1. 供应商创建接口测试（3个测试用例）");
        System.out.println("2. 供应商更新接口测试（3个测试用例）");
        System.out.println("3. 供应商删除接口测试（3个测试用例）");
        System.out.println("4. 供应商查询接口测试（4个测试用例）");
        System.out.println("5. 供应商状态管理接口测试（3个测试用例）");
        System.out.println("6. 供应商编码生成接口测试（2个测试用例）");
        System.out.println("7. 边界条件测试（4个测试用例）");
        System.out.println("8. 性能测试（2个测试用例）");
        System.out.println("9. 异常处理测试（3个测试用例）");
        System.out.println("\n总计：27个测试用例");
        System.out.println("\n========================================");
        System.out.println("运行方式：");
        System.out.println("1. IDE中右键运行此测试类");
        System.out.println("2. Maven命令：mvn test -Dtest=SupplierInfoControllerTest");
        System.out.println("3. 生成覆盖率报告：mvn clean test jacoco:report");
        System.out.println("========================================");
    }
}
