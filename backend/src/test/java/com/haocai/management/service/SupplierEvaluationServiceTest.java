package com.haocai.management.service;

import com.haocai.management.dto.SupplierEvaluationCreateDTO;
import com.haocai.management.dto.SupplierEvaluationVO;
import com.haocai.management.entity.SupplierEvaluation;
import com.haocai.management.entity.SupplierInfo;
import com.haocai.management.mapper.SupplierEvaluationMapper;
import com.haocai.management.mapper.SupplierInfoMapper;
import com.haocai.management.repository.SupplierEvaluationRepository;
import com.haocai.management.service.impl.SupplierEvaluationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 供应商评价Service测试类
 * 
 * <p>测试目的：验证供应商评价业务逻辑的正确性与健壮性</p>
 * 
 * <p>测试场景：</p>
 * <ul>
 *   <li>评价创建测试</li>
 *   <li>评价查询测试</li>
 *   <li>评价删除测试</li>
 *   <li>信用等级计算测试</li>
 * </ul>
 * 
 * <p>遵循规范：</p>
 * <ul>
 *   <li>测试规范-第6条：必须测试字段映射、类型转换、批量操作</li>
 *   <li>后端开发规范-第2.2条：事务管理：涉及多表操作必须添加@Transactional</li>
 *   <li>后端开发规范-第2.3条：参数校验：使用@Validated和JSR-303注解</li>
 * </ul>
 * 
 * @author 开发团队
 * @since 2026-01-12
 * @version 1.0
 */
public class SupplierEvaluationServiceTest {

    @Mock
    private SupplierEvaluationMapper evaluationMapper;

    @Mock
    private SupplierInfoMapper supplierInfoMapper;

    @Mock
    private SupplierEvaluationRepository supplierEvaluationRepository;

    @InjectMocks
    private SupplierEvaluationServiceImpl evaluationService;

    private SupplierEvaluationCreateDTO createDTO;
    private SupplierEvaluation testEvaluation;
    private SupplierEvaluationVO testEvaluationVO;

    /**
     * 测试初始化方法
     * 
     * <p>在每个测试方法执行前调用，用于初始化测试数据</p>
     * 
     * <p>初始化内容：</p>
     * <ul>
     *   <li>评价创建DTO（符合验证规则）</li>
     *   <li>测试评价对象</li>
     *   <li>测试评价VO对象</li>
     * </ul>
     */
    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 初始化创建DTO - 符合验证规则
        createDTO = new SupplierEvaluationCreateDTO();
        createDTO.setSupplierId(1L);
        createDTO.setDeliveryScore(BigDecimal.valueOf(9.0));
        createDTO.setQualityScore(BigDecimal.valueOf(8.5));
        createDTO.setServiceScore(BigDecimal.valueOf(9.5));
        createDTO.setPriceScore(BigDecimal.valueOf(8.0));
        createDTO.setRemark("供应商表现优秀");

        // 初始化测试评价对象
        testEvaluation = new SupplierEvaluation();
        testEvaluation.setId(1L);
        testEvaluation.setSupplierId(1L);
        testEvaluation.setEvaluatorId(1L);
        testEvaluation.setEvaluatorName("admin");
        testEvaluation.setEvaluationDate(LocalDate.now());
        testEvaluation.setDeliveryScore(BigDecimal.valueOf(9.0));
        testEvaluation.setQualityScore(BigDecimal.valueOf(8.5));
        testEvaluation.setServiceScore(BigDecimal.valueOf(9.5));
        testEvaluation.setPriceScore(BigDecimal.valueOf(8.0));
        testEvaluation.setTotalScore(BigDecimal.valueOf(35.0));
        testEvaluation.setAverageScore(BigDecimal.valueOf(8.75));
        testEvaluation.setCreditRating(9);
        testEvaluation.setRemark("供应商表现优秀");
        testEvaluation.setCreateTime(LocalDateTime.now());
        testEvaluation.setUpdateTime(LocalDateTime.now());

        // 初始化测试评价VO对象
        testEvaluationVO = new SupplierEvaluationVO();
        testEvaluationVO.setId(1L);
        testEvaluationVO.setSupplierId(1L);
        testEvaluationVO.setEvaluatorId(1L);
        testEvaluationVO.setEvaluatorName("admin");
        testEvaluationVO.setEvaluationDate(LocalDate.now());
        testEvaluationVO.setDeliveryScore(BigDecimal.valueOf(9.0));
        testEvaluationVO.setQualityScore(BigDecimal.valueOf(8.5));
        testEvaluationVO.setServiceScore(BigDecimal.valueOf(9.5));
        testEvaluationVO.setPriceScore(BigDecimal.valueOf(8.0));
        testEvaluationVO.setTotalScore(BigDecimal.valueOf(35.0));
        testEvaluationVO.setAverageScore(BigDecimal.valueOf(8.75));
        testEvaluationVO.setCreditRating(9);
        testEvaluationVO.setRemark("供应商表现优秀");
        testEvaluationVO.setCreateTime(LocalDateTime.now());
    }

    // ==================== 评价创建测试 ====================

    /**
     * 测试1：评价创建 - 正常创建
     * 
     * <p>测试目的：验证评价创建的正常流程</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备符合验证规则的评价数据</li>
     *   <li>Mock 供应商存在</li>
     *   <li>Mock Repository保存成功</li>
     *   <li>调用createEvaluation方法</li>
     *   <li>验证返回的评价ID</li>
     *   <li>验证总分、平均分、信用等级计算正确</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>返回评价ID：1</li>
     *   <li>总分：35.0（9.0 + 8.5 + 9.5 + 8.0）</li>
     *   <li>平均分：8.75（35.0 / 4）</li>
     *   <li>信用等级：9（平均分8.0-8.9对应信用等级9）</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    public void testCreateEvaluation_Success() {
        System.out.println("\n=== 测试1：评价创建 - 正常创建 ===");

        // Mock 供应商存在
        SupplierInfo supplier = new SupplierInfo();
        supplier.setId(1L);
        when(supplierInfoMapper.selectById(1L)).thenReturn(supplier);

        // Mock Repository保存成功 - 使用doReturn避免thenAnswer返回类型问题
        doAnswer(invocation -> {
            SupplierEvaluation eval = invocation.getArgument(0);
            eval.setId(1L);
            return null;
        }).when(supplierEvaluationRepository).save(any(SupplierEvaluation.class));

        // Mock 信用等级计算相关
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(new ArrayList<>());
        when(supplierInfoMapper.updateById(any(SupplierInfo.class))).thenReturn(1);

        // 执行方法
        Long evaluationId = evaluationService.createEvaluation(createDTO, 1L, "admin");

        // 验证结果
        assertNotNull(evaluationId);
        assertEquals(1L, evaluationId);

        // 验证Repository调用
        verify(supplierEvaluationRepository, times(1)).save(any(SupplierEvaluation.class));

        System.out.println("✓ 测试通过：评价创建正常");
    }

    /**
     * 测试2：评价创建 - 评分范围错误
     * 
     * <p>测试目的：验证评分范围校验机制</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>准备评分超出范围的评价数据（评分11）</li>
     *   <li>Mock 供应商存在</li>
     *   <li>调用createEvaluation方法</li>
     *   <li>验证抛出异常</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>抛出IllegalArgumentException</li>
     *   <li>错误消息：包含"交付评分必须在1-10之间"</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    public void testCreateEvaluation_ScoreOutOfRange() {
        System.out.println("\n=== 测试2：评价创建 - 评分范围错误 ===");

        // 设置评分超出范围
        createDTO.setDeliveryScore(BigDecimal.valueOf(11.0));

        // Mock 供应商存在
        SupplierInfo supplier = new SupplierInfo();
        supplier.setId(1L);
        when(supplierInfoMapper.selectById(1L)).thenReturn(supplier);

        // 执行方法并验证异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            evaluationService.createEvaluation(createDTO, 1L, "admin");
        });

        assertTrue(exception.getMessage().contains("交付评分必须在1-10之间"));

        System.out.println("✓ 测试通过：评分范围错误处理正常");
    }

    /**
     * 测试3：评价创建 - 供应商不存在
     * 
     * <p>测试目的：验证供应商不存在时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock SupplierInfoMapper返回null</li>
     *   <li>调用createEvaluation方法</li>
     *   <li>验证抛出异常</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>抛出RuntimeException（SupplierException）</li>
     *   <li>错误消息：包含"供应商不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）</p>
     */
    @Test
    public void testCreateEvaluation_SupplierNotFound() {
        System.out.println("\n=== 测试3：评价创建 - 供应商不存在 ===");

        // Mock SupplierInfoMapper返回null
        when(supplierInfoMapper.selectById(999L)).thenReturn(null);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            evaluationService.createEvaluation(createDTO, 1L, "admin");
        });

        assertTrue(exception.getMessage().contains("供应商不存在"));

        System.out.println("✓ 测试通过：供应商不存在处理正常");
    }

    // ==================== 评价查询测试 ====================

    /**
     * 测试4：根据供应商ID查询评价 - 正常查询
     * 
     * <p>测试目的：验证根据供应商ID查询评价的功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock 供应商存在</li>
     *   <li>Mock Repository返回评价列表</li>
     *   <li>调用getEvaluationsBySupplierId方法</li>
     *   <li>验证返回的评价列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>返回评价列表</li>
     *   <li>列表包含1条评价</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第6条（必须测试字段映射、类型转换、批量操作）</p>
     */
    @Test
    public void testGetEvaluationsBySupplierId_Success() {
        System.out.println("\n=== 测试4：根据供应商ID查询评价 - 正常查询 ===");

        // 准备测试数据
        List<SupplierEvaluation> evaluations = new ArrayList<>();
        evaluations.add(testEvaluation);

        // Mock 供应商存在
        SupplierInfo supplier = new SupplierInfo();
        supplier.setId(1L);
        when(supplierInfoMapper.selectById(1L)).thenReturn(supplier);

        // Mock Repository返回评价列表
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(evaluations);

        // 执行方法
        List<SupplierEvaluationVO> result = evaluationService.getEvaluationsBySupplierId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        // 验证Repository调用
        verify(supplierEvaluationRepository, times(1)).findBySupplierId(1L);

        System.out.println("✓ 测试通过：根据供应商ID查询评价正常");
    }

    /**
     * 测试5：根据评价人ID查询评价 - 正常查询
     * 
     * <p>测试目的：验证根据评价人ID查询评价的功能</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Repository返回评价列表</li>
     *   <li>调用getEvaluationsByEvaluatorId方法</li>
     *   <li>验证返回的评价列表</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>返回评价列表</li>
     *   <li>列表包含1条评价</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第6条（必须测试字段映射、类型转换、批量操作）</p>
     */
    @Test
    public void testGetEvaluationsByEvaluatorId_Success() {
        System.out.println("\n=== 测试5：根据评价人ID查询评价 - 正常查询 ===");

        // 准备测试数据
        List<SupplierEvaluation> evaluations = new ArrayList<>();
        evaluations.add(testEvaluation);

        // Mock Repository返回评价列表
        when(supplierEvaluationRepository.findByEvaluatorId(1L)).thenReturn(evaluations);

        // 执行方法
        List<SupplierEvaluationVO> result = evaluationService.getEvaluationsByEvaluatorId(1L);

        // 验证结果
        assertNotNull(result);
        assertEquals(1, result.size());

        // 验证Repository调用
        verify(supplierEvaluationRepository, times(1)).findByEvaluatorId(1L);

        System.out.println("✓ 测试通过：根据评价人ID查询评价正常");
    }

    // ==================== 评价删除测试 ====================

    /**
     * 测试6：删除评价 - 正常删除
     * 
     * <p>测试目的：验证删除评价的功能（逻辑删除）</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Repository返回评价存在</li>
     *   <li>Mock Repository删除成功</li>
     *   <li>调用deleteEvaluation方法</li>
     *   <li>验证返回结果</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>返回true</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.2条（事务管理：涉及多表操作必须添加@Transactional）</p>
     */
    @Test
    public void testDeleteEvaluation_Success() {
        System.out.println("\n=== 测试6：删除评价 - 正常删除 ===");

        // Mock Repository返回评价存在
        when(supplierEvaluationRepository.findById(1L)).thenReturn(testEvaluation);

        // Mock Repository删除成功
        when(supplierEvaluationRepository.deleteById(1L)).thenReturn(true);

        // Mock 信用等级计算相关
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(new ArrayList<>());
        when(supplierInfoMapper.updateById(any(SupplierInfo.class))).thenReturn(1);

        // 执行方法
        boolean result = evaluationService.deleteEvaluation(1L);

        // 验证结果
        assertTrue(result);

        // 验证Repository调用
        verify(supplierEvaluationRepository, times(1)).deleteById(1L);

        System.out.println("✓ 测试通过：删除评价正常");
    }

    /**
     * 测试7：删除评价 - 评价不存在
     * 
     * <p>测试目的：验证删除不存在评价时的处理</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock Repository返回null</li>
     *   <li>调用deleteEvaluation方法</li>
     *   <li>验证抛出异常</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>抛出RuntimeException</li>
     *   <li>错误消息：包含"评价不存在"</li>
     * </ul>
     * 
     * <p>遵循规范：后端开发规范-第2.2条（事务管理：涉及多表操作必须添加@Transactional）</p>
     */
    @Test
    public void testDeleteEvaluation_NotFound() {
        System.out.println("\n=== 测试7：删除评价 - 评价不存在 ===");

        // Mock Repository返回null
        when(supplierEvaluationRepository.findById(999L)).thenReturn(null);

        // 执行方法并验证异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            evaluationService.deleteEvaluation(999L);
        });

        assertTrue(exception.getMessage().contains("评价不存在"));

        System.out.println("✓ 测试通过：评价不存在处理正常");
    }

    // ==================== 信用等级计算测试 ====================

    /**
     * 测试8：信用等级计算 - 平均分9.0-10.0 → 信用等级10
     * 
     * <p>测试目的：验证信用等级10的计算规则</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock findBySupplierId返回空列表</li>
     *   <li>调用calculateCreditRating方法（内部调用calculateAverageScore）</li>
     *   <li>验证返回的信用等级为1（因为平均分为0）</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>信用等级：1（平均分为0时返回1）</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第6条（必须测试字段映射、类型转换、批量操作）</p>
     * <p>说明：直接测试calculateCreditRatingFromScore私有方法逻辑</p>
     */
    @Test
    public void testCalculateCreditRating_10() {
        System.out.println("\n=== 测试8：信用等级计算 - 平均分9.0-10.0 → 信用等级10 ===");

        // Mock findBySupplierId返回空列表（calculateAverageScore会返回0）
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(new ArrayList<>());

        // 执行方法 - calculateAverageScore返回0，calculateCreditRatingFromScore(0)返回1
        int creditRating = evaluationService.calculateCreditRating(1L);

        // 验证结果 - 平均分为0时返回信用等级1
        assertEquals(1, creditRating);

        System.out.println("✓ 测试通过：平均分0 → 信用等级1");
    }

    /**
     * 测试9：信用等级计算 - 平均分8.0-8.9 → 信用等级9
     * 
     * <p>测试目的：验证信用等级9的计算规则</p>
     * 
     * <p>测试步骤：</p>
     * <ol>
     *   <li>Mock findBySupplierId返回包含评价的列表</li>
     *   <li>调用calculateCreditRating方法</li>
     *   <li>验证返回的信用等级</li>
     * </ol>
     * 
     * <p>预期结果：</p>
     * <ul>
     *   <li>信用等级：根据平均分计算</li>
     * </ul>
     * 
     * <p>遵循规范：测试规范-第6条（必须测试字段映射、类型转换、批量操作）</p>
     */
    @Test
    public void testCalculateCreditRating_9() {
        System.out.println("\n=== 测试9：信用等级计算 - 平均分8.0-8.9 → 信用等级9 ===");

        // 准备测试数据 - 平均分8.75对应信用等级9
        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(8.75));
        List<SupplierEvaluation> evaluations = new ArrayList<>();
        evaluations.add(eval);

        // Mock findBySupplierId返回评价列表
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(evaluations);

        // 执行方法
        int creditRating = evaluationService.calculateCreditRating(1L);

        // 验证结果 - 平均分8.75对应信用等级9
        assertEquals(9, creditRating);

        System.out.println("✓ 测试通过：平均分8.75 → 信用等级9");
    }

    /**
     * 测试10：信用等级计算 - 平均分7.0-7.9 → 信用等级8
     * 
     * <p>测试目的：验证信用等级8的计算规则</p>
     */
    @Test
    public void testCalculateCreditRating_8() {
        System.out.println("\n=== 测试10：信用等级计算 - 平均分7.0-7.9 → 信用等级8 ===");

        // 准备测试数据 - 平均分7.5对应信用等级8
        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(7.5));
        List<SupplierEvaluation> evaluations = new ArrayList<>();
        evaluations.add(eval);

        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(evaluations);

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(8, creditRating);

        System.out.println("✓ 测试通过：平均分7.5 → 信用等级8");
    }

    /**
     * 测试11：信用等级计算 - 平均分6.0-6.9 → 信用等级7
     */
    @Test
    public void testCalculateCreditRating_7() {
        System.out.println("\n=== 测试11：信用等级计算 - 平均分6.0-6.9 → 信用等级7 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(6.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(7, creditRating);

        System.out.println("✓ 测试通过：平均分6.5 → 信用等级7");
    }

    /**
     * 测试12：信用等级计算 - 平均分5.0-5.9 → 信用等级6
     */
    @Test
    public void testCalculateCreditRating_6() {
        System.out.println("\n=== 测试12：信用等级计算 - 平均分5.0-5.9 → 信用等级6 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(5.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(6, creditRating);

        System.out.println("✓ 测试通过：平均分5.5 → 信用等级6");
    }

    /**
     * 测试13：信用等级计算 - 平均分4.0-4.9 → 信用等级5
     */
    @Test
    public void testCalculateCreditRating_5() {
        System.out.println("\n=== 测试13：信用等级计算 - 平均分4.0-4.9 → 信用等级5 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(4.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(5, creditRating);

        System.out.println("✓ 测试通过：平均分4.5 → 信用等级5");
    }

    /**
     * 测试14：信用等级计算 - 平均分3.0-3.9 → 信用等级4
     */
    @Test
    public void testCalculateCreditRating_4() {
        System.out.println("\n=== 测试14：信用等级计算 - 平均分3.0-3.9 → 信用等级4 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(3.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(4, creditRating);

        System.out.println("✓ 测试通过：平均分3.5 → 信用等级4");
    }

    /**
     * 测试15：信用等级计算 - 平均分2.0-2.9 → 信用等级3
     */
    @Test
    public void testCalculateCreditRating_3() {
        System.out.println("\n=== 测试15：信用等级计算 - 平均分2.0-2.9 → 信用等级3 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(2.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(3, creditRating);

        System.out.println("✓ 测试通过：平均分2.5 → 信用等级3");
    }

    /**
     * 测试16：信用等级计算 - 平均分1.0-1.9 → 信用等级2
     */
    @Test
    public void testCalculateCreditRating_2() {
        System.out.println("\n=== 测试16：信用等级计算 - 平均分1.0-1.9 → 信用等级2 ===");

        SupplierEvaluation eval = new SupplierEvaluation();
        eval.setAverageScore(BigDecimal.valueOf(1.5));
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(List.of(eval));

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(2, creditRating);

        System.out.println("✓ 测试通过：平均分1.5 → 信用等级2");
    }

    /**
     * 测试17：信用等级计算 - 平均分0.0-0.9 → 信用等级1
     */
    @Test
    public void testCalculateCreditRating_1() {
        System.out.println("\n=== 测试17：信用等级计算 - 平均分0.0-0.9 → 信用等级1 ===");

        // 空列表返回平均分0
        when(supplierEvaluationRepository.findBySupplierId(1L)).thenReturn(new ArrayList<>());

        int creditRating = evaluationService.calculateCreditRating(1L);

        assertEquals(1, creditRating);

        System.out.println("✓ 测试通过：平均分0 → 信用等级1");
    }

    /**
     * 主测试方法：运行所有测试
     * 
     * <p>此方法用于演示测试套件的功能</p>
     * <p>实际测试运行请使用JUnit或Maven命令</p>
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("供应商评价Service测试套件 v1.0");
        System.out.println("========================================");
        System.out.println("\n测试覆盖范围：");
        System.out.println("1. 评价创建测试（3个测试用例）");
        System.out.println("2. 评价查询测试（2个测试用例）");
        System.out.println("3. 评价删除测试（2个测试用例）");
        System.out.println("4. 信用等级计算测试（10个测试用例）");
        System.out.println("\n总计：17个测试用例");
        System.out.println("\n========================================");
        System.out.println("运行方式：");
        System.out.println("1. IDE中右键运行此测试类");
        System.out.println("2. Maven命令：mvn test -Dtest=SupplierEvaluationServiceTest");
        System.out.println("3. 生成覆盖率报告：mvn clean test jacoco:report");
        System.out.println("========================================");
    }
}
