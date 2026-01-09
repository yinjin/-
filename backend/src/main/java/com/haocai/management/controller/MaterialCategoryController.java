package com.haocai.management.controller;

import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.service.IMaterialCategoryService;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 耗材分类管理Controller
 * 
 * 遵循development-standards.md中的控制层规范：
 * - Controller命名规范：使用业务名称+Controller后缀
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 权限控制：使用@PreAuthorize注解控制方法级权限
 * - 接口文档：使用Swagger注解描述接口
 * - 日志记录：使用@Slf4j进行日志记录
 * - 依赖注入：使用@RequiredArgsConstructor进行构造器注入
 * 
 * @author haocai
 * @since 2026-01-08
 */
@Slf4j
@RestController
@RequestMapping("/api/material-categories")
@io.swagger.v3.oas.annotations.tags.Tag(name = "耗材分类管理接口")
@RequiredArgsConstructor
public class MaterialCategoryController {

    private final IMaterialCategoryService materialCategoryService;

    // ==================== 基础CRUD接口 ====================

    /**
     * 创建耗材分类
     * 权限：耗材分类管理-创建
     */
    @PostMapping
    @Operation(summary = "创建耗材分类")
    @PreAuthorize("hasAuthority('material-category:create')")
    public ApiResponse<Long> createCategory(
            @Parameter(description = "耗材分类创建请求") @RequestBody MaterialCategoryCreateDTO dto) {
        log.info("========== 创建耗材分类 ==========");
        log.info("请求参数: {}", dto);
        log.info("权限检查: material-category:create");
        
        Long categoryId = materialCategoryService.createCategory(dto);
        
        log.info("创建成功，分类ID: {}", categoryId);
        log.info("========== 创建耗材分类结束 ==========");
        
        return ApiResponse.success(categoryId);
    }

    /**
     * 更新耗材分类
     * 权限：耗材分类管理-编辑
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新耗材分类")
    @PreAuthorize("hasAuthority('material-category:edit')")
    public ApiResponse<Boolean> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Parameter(description = "耗材分类更新请求") @RequestBody MaterialCategoryUpdateDTO dto) {
        log.info("========== 更新耗材分类 ==========");
        log.info("分类ID: {}, 请求参数: {}", id, dto);
        log.info("权限检查: material-category:edit");
        
        boolean result = materialCategoryService.updateCategory(id, dto);
        
        log.info("更新结果: {}", result);
        log.info("========== 更新耗材分类结束 ==========");
        
        return ApiResponse.success(result);
    }

    /**
     * 删除耗材分类
     * 权限：耗材分类管理-删除
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除耗材分类")
    @PreAuthorize("hasAuthority('material-category:delete')")
    public ApiResponse<Boolean> deleteCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 删除耗材分类 ==========");
        log.info("分类ID: {}", id);
        log.info("权限检查: material-category:delete");
        
        boolean result = materialCategoryService.deleteCategory(id);
        
        log.info("删除结果: {}", result);
        log.info("========== 删除耗材分类结束 ==========");
        
        return ApiResponse.success(result);
    }

    /**
     * 获取耗材分类详情
     * 权限：耗材分类管理-查看
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取耗材分类详情")
    @PreAuthorize("hasAuthority('material-category:view')")
    public ApiResponse<MaterialCategoryVO> getCategory(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 获取耗材分类详情 ==========");
        log.info("分类ID: {}", id);
        log.info("权限检查: material-category:view");
        
        MaterialCategoryVO category = materialCategoryService.getCategoryById(id);
        
        log.info("查询成功，分类名称: {}", category.getCategoryName());
        log.info("========== 获取耗材分类详情结束 ==========");
        
        return ApiResponse.success(category);
    }

    // ==================== 批量操作接口 ====================

    /**
     * 批量删除耗材分类
     * 权限：耗材分类管理-删除
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除耗材分类")
    @PreAuthorize("hasAuthority('material-category:delete')")
    public ApiResponse<Boolean> batchDeleteCategories(
            @Parameter(description = "分类ID列表") @RequestBody List<Long> ids) {
        log.info("========== 批量删除耗材分类 ==========");
        log.info("分类ID列表: {}", ids);
        log.info("权限检查: material-category:delete");
        
        boolean result = materialCategoryService.batchDeleteCategories(ids);
        
        log.info("批量删除结果: {}", result);
        log.info("========== 批量删除耗材分类结束 ==========");
        
        return ApiResponse.success(result);
    }

    // ==================== 树形结构接口 ====================

    /**
     * 获取耗材分类树形结构
     * 权限：耗材分类管理-查看
     */
    @GetMapping("/tree")
    @Operation(summary = "获取耗材分类树形结构")
    @PreAuthorize("hasAuthority('material-category:view')")
    public ApiResponse<List<MaterialCategoryTreeVO>> getCategoryTree() {
        log.info("========== 获取耗材分类树形结构 ==========");
        log.info("权限检查: material-category:view");
        
        List<MaterialCategoryTreeVO> tree = materialCategoryService.getCategoryTree();
        
        log.info("查询成功，顶级分类数量: {}", tree.size());
        log.info("========== 获取耗材分类树形结构结束 ==========");
        
        return ApiResponse.success(tree);
    }

    /**
     * 根据父分类ID查询子分类列表
     * 权限：耗材分类管理-查看
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "根据父分类ID查询子分类列表")
    @PreAuthorize("hasAuthority('material-category:view')")
    public ApiResponse<List<MaterialCategoryVO>> getChildrenByParentId(
            @Parameter(description = "父分类ID（0表示顶级分类）") @PathVariable Long parentId) {
        log.info("========== 根据父分类ID查询子分类列表 ==========");
        log.info("父分类ID: {}", parentId);
        log.info("权限检查: material-category:view");
        
        List<MaterialCategoryVO> children = materialCategoryService.getChildrenByParentId(parentId);
        
        log.info("查询成功，子分类数量: {}", children.size());
        log.info("========== 根据父分类ID查询子分类列表结束 ==========");
        
        return ApiResponse.success(children);
    }

    /**
     * 查询所有顶级分类
     * 权限：耗材分类管理-查看
     */
    @GetMapping("/top-level")
    @Operation(summary = "查询所有顶级分类")
    @PreAuthorize("hasAuthority('material-category:view')")
    public ApiResponse<List<MaterialCategoryVO>> getTopLevelCategories() {
        log.info("========== 查询所有顶级分类 ==========");
        log.info("权限检查: material-category:view");
        
        List<MaterialCategoryVO> topCategories = materialCategoryService.getTopLevelCategories();
        
        log.info("查询成功，顶级分类数量: {}", topCategories.size());
        log.info("========== 查询所有顶级分类结束 ==========");
        
        return ApiResponse.success(topCategories);
    }

    // ==================== 状态管理接口 ====================

    /**
     * 切换分类状态
     * 权限：耗材分类管理-编辑
     */
    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "切换分类状态")
    @PreAuthorize("hasAuthority('material-category:edit')")
    public ApiResponse<Boolean> toggleCategoryStatus(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 切换分类状态 ==========");
        log.info("分类ID: {}", id);
        log.info("权限检查: material-category:edit");
        
        boolean result = materialCategoryService.toggleCategoryStatus(id);
        
        log.info("切换状态结果: {}", result);
        log.info("========== 切换分类状态结束 ==========");
        
        return ApiResponse.success(result);
    }

    // ==================== 验证接口 ====================

    /**
     * 检查分类编码是否存在
     * 权限：无需登录（用于表单验证）
     */
    @GetMapping("/check/code")
    @Operation(summary = "检查分类编码是否存在")
    public ApiResponse<Boolean> checkCategoryCode(
            @Parameter(description = "分类编码") @RequestParam String categoryCode,
            @Parameter(description = "排除的分类ID（更新时使用）") @RequestParam(required = false) Long excludeId) {
        log.info("========== 检查分类编码是否存在 ==========");
        log.info("分类编码: {}, 排除ID: {}", categoryCode, excludeId);
        
        boolean exists;
        if (excludeId != null) {
            exists = materialCategoryService.existsByCategoryCodeExcludeId(categoryCode, excludeId);
        } else {
            exists = materialCategoryService.existsByCategoryCode(categoryCode);
        }
        
        log.info("检查结果: {}", exists);
        log.info("========== 检查分类编码是否存在结束 ==========");
        
        return ApiResponse.success(exists);
    }

    /**
     * 检查分类下是否有子分类
     * 权限：耗材分类管理-查看
     */
    @GetMapping("/{id}/has-children")
    @Operation(summary = "检查分类下是否有子分类")
    @PreAuthorize("hasAuthority('material-category:view')")
    public ApiResponse<Boolean> hasChildren(
            @Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 检查分类下是否有子分类 ==========");
        log.info("分类ID: {}", id);
        log.info("权限检查: material-category:view");
        
        boolean hasChildren = materialCategoryService.hasChildren(id);
        
        log.info("检查结果: {}", hasChildren);
        log.info("========== 检查分类下是否有子分类结束 ==========");
        
        return ApiResponse.success(hasChildren);
    }
}
