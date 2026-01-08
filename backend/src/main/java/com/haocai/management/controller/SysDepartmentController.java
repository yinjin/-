package com.haocai.management.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.*;
import com.haocai.management.service.ISysDepartmentService;
import com.haocai.management.common.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 部门管理Controller
 * 
 * 遵循规范：
 * - Controller命名规范：使用业务名称+Controller后缀
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 权限控制：使用@PreAuthorize注解控制方法级权限
 * - 接口文档：使用Swagger注解描述接口
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Slf4j
@RestController
@RequestMapping("/api/department")
@io.swagger.v3.oas.annotations.tags.Tag(name = "部门管理接口")
public class SysDepartmentController {

    private final ISysDepartmentService departmentService;

    public SysDepartmentController(ISysDepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    // ==================== 基础CRUD接口 ====================

    /**
     * 创建部门
     * 权限：部门管理-创建
     */
    @PostMapping
    @Operation(summary = "创建部门")
    @PreAuthorize("hasAuthority('department:create')")
    public ApiResponse<DepartmentVO> createDepartment(
            @Parameter(description = "部门创建请求") @RequestBody DepartmentCreateDTO dto) {
        // 从安全上下文中获取当前用户ID
        Long currentUserId = getCurrentUserId();
        return departmentService.createDepartment(dto, currentUserId);
    }

    /**
     * 更新部门
     * 权限：部门管理-编辑
     */
    @PutMapping
    @Operation(summary = "更新部门")
    @PreAuthorize("hasAuthority('department:edit')")
    public ApiResponse<DepartmentVO> updateDepartment(
            @Parameter(description = "部门更新请求") @RequestBody DepartmentUpdateDTO dto) {
        Long currentUserId = getCurrentUserId();
        return departmentService.updateDepartment(dto, currentUserId);
    }

    /**
     * 删除部门
     * 权限：部门管理-删除
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除部门")
    @PreAuthorize("hasAuthority('department:delete')")
    public ApiResponse<Void> deleteDepartment(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        return departmentService.deleteDepartment(id, currentUserId);
    }

    /**
     * 获取部门详情
     * 权限：部门管理-查看
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取部门详情")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<DepartmentVO> getDepartment(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        return departmentService.getDepartment(id);
    }

    /**
     * 分页查询部门列表
     * 权限：部门管理-查看
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询部门列表")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<Page<DepartmentVO>> listDepartments(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "关键词搜索（名称或编码）") @RequestParam(required = false) String keyword,
            @Parameter(description = "部门状态") @RequestParam(required = false) String status) {
        DepartmentQueryDTO queryDTO = new DepartmentQueryDTO();
        queryDTO.setPage(page);
        queryDTO.setSize(size);
        queryDTO.setKeyword(keyword);
        queryDTO.setStatus(status);
        return departmentService.listDepartments(queryDTO);
    }

    // ==================== 批量操作接口 ====================

    /**
     * 批量删除部门
     * 权限：部门管理-删除
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除部门")
    @PreAuthorize("hasAuthority('department:delete')")
    public ApiResponse<Map<String, Object>> batchDeleteDepartments(
            @Parameter(description = "部门ID列表") @RequestBody List<Long> ids) {
        Long currentUserId = getCurrentUserId();
        return departmentService.batchDeleteDepartments(ids, currentUserId);
    }

    /**
     * 批量更新部门状态
     * 权限：部门管理-编辑
     */
    @PutMapping("/batch/status")
    @Operation(summary = "批量更新部门状态")
    @PreAuthorize("hasAuthority('department:edit')")
    public ApiResponse<Map<String, Object>> batchUpdateStatus(
            @Parameter(description = "部门ID列表") @RequestBody Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        List<Long> ids = (List<Long>) request.get("ids");
        String status = (String) request.get("status");
        Long currentUserId = getCurrentUserId();
        return departmentService.batchUpdateStatus(ids, status, currentUserId);
    }

    // ==================== 树形结构接口 ====================

    /**
     * 获取部门树形结构
     * 权限：部门管理-查看
     */
    @GetMapping("/tree")
    @Operation(summary = "获取部门树形结构")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<List<DepartmentTreeVO>> getDepartmentTree(
            @Parameter(description = "是否包含禁用部门") @RequestParam(required = false, defaultValue = "false") Boolean includeDisabled) {
        return departmentService.getDepartmentTree(includeDisabled);
    }

    /**
     * 获取指定部门的树形结构（包含所有子部门）
     * 权限：部门管理-查看
     */
    @GetMapping("/{id}/tree")
    @Operation(summary = "获取指定部门的树形结构")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<DepartmentTreeVO> getDepartmentTreeById(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        return departmentService.getDepartmentTreeById(id);
    }

    /**
     * 懒加载获取子部门
     * 权限：部门管理-查看
     */
    @GetMapping("/{parentId}/children")
    @Operation(summary = "懒加载获取子部门")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<List<DepartmentTreeVO>> getChildrenByParentId(
            @Parameter(description = "父部门ID") @PathVariable Long parentId) {
        return departmentService.getChildrenByParentId(parentId);
    }

    // ==================== 部门移动接口 ====================

    /**
     * 移动部门（调整部门层级）
     * 权限：部门管理-编辑
     */
    @PutMapping("/{id}/move")
    @Operation(summary = "移动部门")
    @PreAuthorize("hasAuthority('department:edit')")
    public ApiResponse<DepartmentVO> moveDepartment(
            @Parameter(description = "要移动的部门ID") @PathVariable Long id,
            @Parameter(description = "新的父部门ID") @RequestParam(required = false) Long newParentId) {
        Long currentUserId = getCurrentUserId();
        return departmentService.moveDepartment(id, newParentId, currentUserId);
    }

    // ==================== 部门负责人管理接口 ====================

    /**
     * 设置部门负责人
     * 权限：部门管理-编辑
     */
    @PutMapping("/{id}/leader")
    @Operation(summary = "设置部门负责人")
    @PreAuthorize("hasAuthority('department:edit')")
    public ApiResponse<Void> setDepartmentLeader(
            @Parameter(description = "部门ID") @PathVariable Long id,
            @Parameter(description = "负责人ID") @RequestParam(required = false) Long leaderId) {
        Long currentUserId = getCurrentUserId();
        return departmentService.setDepartmentLeader(id, leaderId, currentUserId);
    }

    /**
     * 移除部门负责人
     * 权限：部门管理-编辑
     */
    @DeleteMapping("/{id}/leader")
    @Operation(summary = "移除部门负责人")
    @PreAuthorize("hasAuthority('department:edit')")
    public ApiResponse<Void> removeDepartmentLeader(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        Long currentUserId = getCurrentUserId();
        return departmentService.removeDepartmentLeader(id, currentUserId);
    }

    // ==================== 部门关联查询接口 ====================

    /**
     * 查询部门下的所有用户ID
     * 权限：部门管理-查看
     */
    @GetMapping("/{id}/users")
    @Operation(summary = "查询部门下的所有用户ID")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<List<Long>> getUserIdsByDepartmentId(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        return departmentService.getUserIdsByDepartmentId(id);
    }

    /**
     * 查询用户所属部门
     * 权限：部门管理-查看
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "查询用户所属部门")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<List<DepartmentVO>> getDepartmentsByUserId(
            @Parameter(description = "用户ID") @PathVariable Long userId) {
        return departmentService.getDepartmentsByUserId(userId);
    }

    // ==================== 验证接口 ====================

    /**
     * 检查部门编码是否存在
     * 权限：无需登录（用于表单验证）
     */
    @GetMapping("/check/code")
    @Operation(summary = "检查部门编码是否存在")
    public ApiResponse<Boolean> checkDepartmentCode(
            @Parameter(description = "部门编码") @RequestParam String code,
            @Parameter(description = "排除的部门ID（更新时使用）") @RequestParam(required = false) Long excludeId) {
        return ApiResponse.success(departmentService.isDepartmentCodeExists(code, excludeId));
    }

    /**
     * 检查部门名称在父部门下是否重复
     * 权限：无需登录（用于表单验证）
     */
    @GetMapping("/check/name")
    @Operation(summary = "检查部门名称在父部门下是否重复")
    public ApiResponse<Boolean> checkDepartmentName(
            @Parameter(description = "部门名称") @RequestParam String name,
            @Parameter(description = "父部门ID") @RequestParam(required = false) Long parentId,
            @Parameter(description = "排除的部门ID（更新时使用）") @RequestParam(required = false) Long excludeId) {
        return ApiResponse.success(departmentService.isDepartmentNameExistsInParent(name, parentId, excludeId));
    }

    /**
     * 检查部门是否可以删除
     * 权限：部门管理-查看
     */
    @GetMapping("/{id}/can-delete")
    @Operation(summary = "检查部门是否可以删除")
    @PreAuthorize("hasAuthority('department:view')")
    public ApiResponse<Map<String, Object>> checkCanDelete(
            @Parameter(description = "部门ID") @PathVariable Long id) {
        return ApiResponse.success(departmentService.checkCanDelete(id));
    }

    // ==================== 私有方法 ====================

    /**
     * 从安全上下文中获取当前用户ID
     * 遵循：安全规范-第2条（从安全上下文获取用户信息）
     */
    private Long getCurrentUserId() {
        // TODO: 从SecurityContextHolder获取当前用户ID
        // 当前返回固定值，后续集成Spring Security后从上下文中获取
        return 1L;
    }
}
