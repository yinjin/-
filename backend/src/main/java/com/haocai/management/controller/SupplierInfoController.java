package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.haocai.management.annotation.RequirePermission;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.*;
import com.haocai.management.entity.SysUser;
import com.haocai.management.service.ISupplierEvaluationService;
import com.haocai.management.service.ISupplierInfoService;
import com.haocai.management.service.ISysUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 供应商信息Controller
 * 
 * 遵循development-standards.md中的Controller层规范：
 * - Controller命名规范：使用业务名称+Controller后缀
 * - 统一响应：所有接口返回ApiResponse包装对象
 * - 参数校验：使用@Validated和JSR-303注解进行参数校验
 * - 权限控制：使用@RequirePermission注解进行权限控制
 * 
 * @author haocai
 * @since 2026-01-12
 */
@Slf4j
@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@Tag(name = "供应商管理", description = "供应商信息的CRUD操作")
public class SupplierInfoController {

    /**
     * 供应商Service接口
     * 遵循：后端开发规范-第2.1条（依赖注入）
     */
    private final ISupplierInfoService supplierInfoService;
    
    /**
     * 供应商评价Service
     * 遵循：后端开发规范-第2.1条（依赖注入）
     */
    private final ISupplierEvaluationService supplierEvaluationService;
    
    /**
     * 用户Service（用于获取用户信息）
     */
    private final ISysUserService sysUserService;

    /**
     * 创建供应商
     * 
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     * 
     * @param createDTO 创建请求DTO
     * @return 创建的供应商ID
     */
    @PostMapping
    @Operation(summary = "创建供应商", description = "创建新的供应商信息")
    @RequirePermission("supplier:create")
    public ResponseEntity<ApiResponse<Long>> createSupplier(
            @Valid @RequestBody SupplierCreateDTO createDTO) {
        log.info("创建供应商请求，供应商名称：{}", createDTO.getSupplierName());
        
        Long supplierId = supplierInfoService.createSupplier(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(supplierId, "供应商创建成功"));
    }

    /**
     * 更新供应商
     * 
     * @param id 供应商ID
     * @param updateDTO 更新请求DTO
     * @return 是否成功
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新供应商", description = "更新指定供应商的信息")
    @RequirePermission("supplier:update")
    public ResponseEntity<ApiResponse<Boolean>> updateSupplier(
            @Parameter(description = "供应商ID") @PathVariable Long id,
            @Valid @RequestBody SupplierUpdateDTO updateDTO) {
        log.info("更新供应商请求，供应商ID：{}", id);
        
        // 确保ID一致
        updateDTO.setId(id);
        
        boolean result = supplierInfoService.updateSupplier(id, updateDTO);
        
        return ResponseEntity.ok(ApiResponse.success(result, "供应商更新成功"));
    }

    /**
     * 删除供应商
     * 
     * @param id 供应商ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除供应商", description = "删除指定的供应商（逻辑删除）")
    @RequirePermission("supplier:delete")
    public ResponseEntity<ApiResponse<Boolean>> deleteSupplier(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        log.info("删除供应商请求，供应商ID：{}", id);
        
        boolean result = supplierInfoService.deleteSupplier(id);
        
        return ResponseEntity.ok(ApiResponse.success(result, "供应商删除成功"));
    }

    /**
     * 批量删除供应商
     * 
     * @param ids 供应商ID列表
     * @return 是否成功
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除供应商", description = "批量删除指定的供应商（逻辑删除）")
    @RequirePermission("supplier:delete")
    public ResponseEntity<ApiResponse<Boolean>> batchDeleteSuppliers(
            @Parameter(description = "供应商ID列表") @RequestBody List<Long> ids) {
        log.info("批量删除供应商请求，供应商ID列表：{}", ids);
        
        boolean result = supplierInfoService.batchDeleteSuppliers(ids);
        
        return ResponseEntity.ok(ApiResponse.success(result, "批量删除供应商成功"));
    }

    /**
     * 获取供应商详情
     * 
     * @param id 供应商ID
     * @return 供应商信息
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取供应商详情", description = "获取指定供应商的详细信息")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<SupplierVO>> getSupplierById(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        log.info("获取供应商详情请求，供应商ID：{}", id);
        
        SupplierVO supplier = supplierInfoService.getSupplierById(id);
        
        return ResponseEntity.ok(ApiResponse.success(supplier));
    }

    /**
     * 分页查询供应商列表
     * 
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询供应商", description = "根据条件分页查询供应商列表")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<IPage<SupplierVO>>> getSupplierPage(
            @ModelAttribute SupplierQueryDTO queryDTO) {
        log.info("分页查询供应商请求，查询条件：{}", queryDTO);
        
        IPage<SupplierVO> page = supplierInfoService.getSupplierPage(queryDTO);
        
        return ResponseEntity.ok(ApiResponse.success(page));
    }

    /**
     * 获取供应商列表（不分页）
     * 
     * @param queryDTO 查询条件
     * @return 供应商列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取供应商列表", description = "根据条件查询供应商列表（不分页）")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierVO>>> getSupplierList(
            @ModelAttribute SupplierQueryDTO queryDTO) {
        log.info("获取供应商列表请求，查询条件：{}", queryDTO);
        
        List<SupplierVO> list = supplierInfoService.getSupplierList(queryDTO);
        
        return ResponseEntity.ok(ApiResponse.success(list));
    }

    /**
     * 切换供应商状态
     * 
     * @param id 供应商ID
     * @return 是否成功
     */
    @PutMapping("/{id}/status")
    @Operation(summary = "切换供应商状态", description = "切换指定供应商的启用/禁用状态")
    @RequirePermission("supplier:update")
    public ResponseEntity<ApiResponse<Boolean>> toggleStatus(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        log.info("切换供应商状态请求，供应商ID：{}", id);
        
        boolean result = supplierInfoService.toggleStatus(id);
        
        return ResponseEntity.ok(ApiResponse.success(result, "状态切换成功"));
    }

    /**
     * 更新供应商状态
     * 
     * @param id 供应商ID
     * @param status 新状态
     * @return 是否成功
     */
    @PutMapping("/{id}/status/{status}")
    @Operation(summary = "更新供应商状态", description = "更新指定供应商的状态")
    @RequirePermission("supplier:update")
    public ResponseEntity<ApiResponse<Boolean>> updateStatus(
            @Parameter(description = "供应商ID") @PathVariable Long id,
            @Parameter(description = "状态（0-禁用，1-启用）") @PathVariable Integer status) {
        log.info("更新供应商状态请求，供应商ID：{}，新状态：{}", id, status);
        
        boolean result = supplierInfoService.updateStatus(id, status);
        
        return ResponseEntity.ok(ApiResponse.success(result, "状态更新成功"));
    }

    /**
     * 批量更新供应商状态
     * 
     * @param ids 供应商ID列表
     * @param status 新状态
     * @return 成功数量
     */
    @PutMapping("/batch/status/{status}")
    @Operation(summary = "批量更新供应商状态", description = "批量更新指定供应商的状态")
    @RequirePermission("supplier:update")
    public ApiResponse<Integer> batchUpdateStatus(
            @Parameter(description = "供应商ID列表") @RequestBody List<Long> ids,
            @Parameter(description = "状态（0-禁用，1-启用）") @PathVariable Integer status) {
        log.info("批量更新供应商状态请求，供应商ID列表：{}，新状态：{}", ids, status);
        
        int successCount = supplierInfoService.batchUpdateStatus(ids, status);
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 使用中间变量确保Integer类型正确推断
        Integer result = Integer.valueOf(successCount);
        return ApiResponse.success(result, "批量更新状态成功");
    }

    /**
     * 生成供应商编码
     * 
     * @return 生成的供应商编码
     */
    @GetMapping("/generate-code")
    @Operation(summary = "生成供应商编码", description = "自动生成唯一的供应商编码")
    @RequirePermission("supplier:create")
    public ResponseEntity<ApiResponse<String>> generateSupplierCode() {
        log.info("生成供应商编码请求");
        
        String supplierCode = supplierInfoService.generateSupplierCode();
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 直接构造ApiResponse以避免方法重载歧义
        ApiResponse<String> response = new ApiResponse<>(200, "success", supplierCode, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    /**
     * 检查供应商编码是否存在
     * 
     * @param supplierCode 供应商编码
     * @return 是否存在
     */
    @GetMapping("/check-code")
    @Operation(summary = "检查供应商编码", description = "检查供应商编码是否已存在")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<Boolean>> checkSupplierCode(
            @Parameter(description = "供应商编码") @RequestParam String supplierCode) {
        log.info("检查供应商编码请求，供应商编码：{}", supplierCode);
        
        boolean exists = supplierInfoService.existsBySupplierCode(supplierCode);
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 直接构造ApiResponse以避免方法重载歧义
        ApiResponse<Boolean> response = new ApiResponse<>(200, "success", Boolean.valueOf(exists), java.time.LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    /**
     * 搜索供应商
     * 
     * @param keyword 搜索关键词
     * @return 供应商列表
     */
    @GetMapping("/search")
    @Operation(summary = "搜索供应商", description = "根据关键词搜索供应商")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierVO>>> searchSuppliers(
            @Parameter(description = "搜索关键词") @RequestParam String keyword) {
        log.info("搜索供应商请求，关键词：{}", keyword);
        
        List<SupplierVO> list = supplierInfoService.searchSuppliers(keyword);
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 使用中间变量确保List类型正确推断
        List<SupplierVO> result = list;
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 根据合作状态获取供应商列表
     * 
     * @param cooperationStatus 合作状态
     * @return 供应商列表
     */
    @GetMapping("/by-cooperation-status")
    @Operation(summary = "根据合作状态获取供应商", description = "根据合作状态查询供应商列表")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierVO>>> getSuppliersByCooperationStatus(
            @Parameter(description = "合作状态（1-合作中，0-已终止）") @RequestParam Integer cooperationStatus) {
        log.info("根据合作状态获取供应商请求，合作状态：{}", cooperationStatus);
        
        List<SupplierVO> list = supplierInfoService.getSuppliersByCooperationStatus(cooperationStatus);
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 使用中间变量确保List类型正确推断
        List<SupplierVO> result = list;
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 根据信用等级范围获取供应商
     * 
     * @param minRating 最小信用等级
     * @param maxRating 最大信用等级
     * @return 供应商列表
     */
    @GetMapping("/by-credit-rating")
    @Operation(summary = "根据信用等级获取供应商", description = "根据信用等级范围查询供应商列表")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierVO>>> getSuppliersByCreditRatingRange(
            @Parameter(description = "最小信用等级") @RequestParam Integer minRating,
            @Parameter(description = "最大信用等级") @RequestParam Integer maxRating) {
        log.info("根据信用等级获取供应商请求，最小等级：{}，最大等级：{}", minRating, maxRating);
        
        List<SupplierVO> list = supplierInfoService.getSuppliersByCreditRatingRange(minRating, maxRating);
        
        // 遵循：后端开发规范-第2.1条（泛型类型安全）
        // 直接构造ApiResponse以避免方法重载歧义
        ApiResponse<List<SupplierVO>> response = new ApiResponse<>(200, "success", list, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }

    /**
     * 评价供应商
     * 
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     * 
     * @param supplierId 供应商ID
     * @param createDTO 评价创建请求
     * @param request HTTP请求
     * @return 创建的评价ID
     */
    @PostMapping("/{id}/evaluate")
    @Operation(summary = "评价供应商", description = "对供应商进行多维度评价")
    @RequirePermission("supplier:evaluate")
    public ResponseEntity<ApiResponse<Long>> evaluateSupplier(
            @Parameter(description = "供应商ID") @PathVariable Long id,
            @Valid @RequestBody SupplierEvaluationCreateDTO createDTO,
            HttpServletRequest request) {
        log.info("评价供应商请求，供应商ID：{}", id);
        
        // 确保供应商ID一致
        createDTO.setSupplierId(id);
        
        // 获取当前登录用户信息
        // 遵循：后端开发规范-第2.1条（从SecurityContext获取认证信息）
        Long evaluatorId = null;
        String evaluatorName = "系统";
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                SysUser user = sysUserService.findByUsername(username);
                if (user != null) {
                    evaluatorId = user.getId();
                    evaluatorName = user.getUsername();
                }
            }
        } catch (Exception e) {
            log.warn("获取当前用户信息失败，使用默认用户：{}", e.getMessage());
        }
        
        Long evaluationId = supplierEvaluationService.createEvaluation(createDTO, evaluatorId, evaluatorName);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(evaluationId, "评价成功"));
    }

    /**
     * 获取供应商评价历史
     * 
     * @param supplierId 供应商ID
     * @return 评价列表
     */
    @GetMapping("/{id}/evaluations")
    @Operation(summary = "获取供应商评价历史", description = "获取指定供应商的所有评价记录")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierEvaluationVO>>> getSupplierEvaluations(
            @Parameter(description = "供应商ID") @PathVariable Long id) {
        log.info("获取供应商评价历史请求，供应商ID：{}", id);
        
        List<SupplierEvaluationVO> evaluations = supplierEvaluationService.getEvaluationsBySupplierId(id);
        
        return ResponseEntity.ok(ApiResponse.success(evaluations));
    }

    /**
     * 获取当前用户对供应商的评价
     * 
     * @param supplierId 供应商ID
     * @param request HTTP请求
     * @return 评价列表
     */
    @GetMapping("/{id}/my-evaluations")
    @Operation(summary = "获取当前用户对供应商的评价", description = "获取当前登录用户对指定供应商的评价记录")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierEvaluationVO>>> getMyEvaluations(
            @Parameter(description = "供应商ID") @PathVariable Long id,
            HttpServletRequest request) {
        log.info("获取当前用户对供应商的评价请求，供应商ID：{}", id);
        
        // 获取当前登录用户信息
        // 遵循：后端开发规范-第2.1条（从SecurityContext获取认证信息）
        Long currentUserId = null;
        
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                SysUser user = sysUserService.findByUsername(username);
                if (user != null) {
                    currentUserId = user.getId();
                }
            }
        } catch (Exception e) {
            log.warn("获取当前用户信息失败：{}", e.getMessage());
        }
        
        if (currentUserId == null) {
            return ResponseEntity.ok(ApiResponse.success(java.util.Collections.emptyList()));
        }
        
        List<SupplierEvaluationVO> evaluations = supplierEvaluationService.getEvaluationsByEvaluatorId(currentUserId);
        
        // 过滤出当前供应商的评价
        List<SupplierEvaluationVO> filteredEvaluations = evaluations.stream()
                .filter(e -> e.getSupplierId().equals(id))
                .collect(java.util.stream.Collectors.toList());
        
        return ResponseEntity.ok(ApiResponse.success(filteredEvaluations));
    }

    /**
     * 删除供应商评价
     * 
     * @param evaluationId 评价ID
     * @return 是否成功
     */
    @DeleteMapping("/evaluation/{evaluationId}")
    @Operation(summary = "删除供应商评价", description = "删除指定的供应商评价记录")
    @RequirePermission("supplier:evaluate")
    public ResponseEntity<ApiResponse<Boolean>> deleteEvaluation(
            @Parameter(description = "评价ID") @PathVariable Long evaluationId) {
        log.info("删除供应商评价请求，评价ID：{}", evaluationId);
        
        boolean result = supplierEvaluationService.deleteEvaluation(evaluationId);
        
        return ResponseEntity.ok(ApiResponse.success(result, "评价删除成功"));
    }
}
