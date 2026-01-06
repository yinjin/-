package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.annotation.Log;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.entity.SysPermission;
import com.haocai.management.entity.SysRole;
import com.haocai.management.entity.SysRolePermission;
import com.haocai.management.service.ISysPermissionService;
import com.haocai.management.service.ISysRolePermissionService;
import com.haocai.management.service.ISysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色管理控制器
 * 
 * 功能说明：
 * 1. 角色的增删改查操作
 * 2. 角色分页查询
 * 3. 角色权限管理
 * 4. 操作日志记录
 * 
 * 遵循规范：
 * - 控制层规范：统一的响应格式、参数验证、异常处理
 * - RESTful规范：使用标准HTTP动词和资源路径
 * - 日志规范：记录关键操作日志
 * - 文档规范：Swagger API文档注解
 * 
 * @author 开发团队
 * @since 2026-01-06
 */
@Slf4j
@RestController
@RequestMapping("/api/role")
@RequiredArgsConstructor
@Validated
@Tag(name = "角色管理", description = "角色管理相关接口")
public class SysRoleController {

    private final ISysRoleService roleService;
    private final ISysPermissionService permissionService;
    private final ISysRolePermissionService rolePermissionService;

    /**
     * 创建角色
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-POST创建资源
     * 
     * @param role 角色信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建角色", description = "创建新的角色")
    @Log(module = "角色管理", operation = "创建角色")
    public ApiResponse<SysRole> createRole(@Valid @RequestBody SysRole role) {
        log.info("创建角色，角色名称: {}", role.getRoleName());
        
        // 检查角色编码是否已存在
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
        if (roleService.count(wrapper) > 0) {
            log.warn("角色编码已存在: {}", role.getRoleCode());
            return ApiResponse.error("角色编码已存在");
        }
        
        // 创建角色
        boolean success = roleService.save(role);
        if (success) {
            log.info("角色创建成功，角色ID: {}", role.getId());
            return ApiResponse.success(role, "角色创建成功");
        } else {
            log.error("角色创建失败");
            return ApiResponse.error("角色创建失败");
        }
    }

    /**
     * 更新角色
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-PUT更新资源
     * 
     * @param id 角色ID
     * @param role 角色信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新角色", description = "更新角色信息")
    @Log(module = "角色管理", operation = "更新角色")
    public ApiResponse<SysRole> updateRole(
            @Parameter(description = "角色ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody SysRole role) {
        log.info("更新角色，角色ID: {}", id);
        
        // 检查角色是否存在
        SysRole existingRole = roleService.getById(id);
        if (existingRole == null) {
            log.warn("角色不存在，角色ID: {}", id);
            return ApiResponse.error("角色不存在");
        }
        
        // 如果修改了角色编码，检查新编码是否已存在
        if (!existingRole.getRoleCode().equals(role.getRoleCode())) {
            LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysRole::getRoleCode, role.getRoleCode());
            wrapper.ne(SysRole::getId, id);
            if (roleService.count(wrapper) > 0) {
                log.warn("角色编码已存在: {}", role.getRoleCode());
                return ApiResponse.error("角色编码已存在");
            }
        }
        
        // 更新角色
        role.setId(id);
        boolean success = roleService.updateById(role);
        if (success) {
            log.info("角色更新成功，角色ID: {}", id);
            return ApiResponse.success(role, "角色更新成功");
        } else {
            log.error("角色更新失败，角色ID: {}", id);
            return ApiResponse.error("角色更新失败");
        }
    }

    /**
     * 删除角色
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-DELETE删除资源
     * 遵循：安全规范-检查角色是否被使用
     * 
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除角色", description = "删除指定角色")
    @Log(module = "角色管理", operation = "删除角色")
    public ApiResponse<Void> deleteRole(
            @Parameter(description = "角色ID") @PathVariable @NotNull Long id) {
        log.info("删除角色，角色ID: {}", id);
        
        // 检查角色是否存在
        SysRole role = roleService.getById(id);
        if (role == null) {
            log.warn("角色不存在，角色ID: {}", id);
            return ApiResponse.error("角色不存在");
        }
        
        // 检查是否有用户关联此角色
        // TODO: 添加用户角色关联检查
        // if (userRoleService.count(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getRoleId, id)) > 0) {
        //     log.warn("角色已被用户使用，无法删除，角色ID: {}", id);
        //     return ApiResponse.error("角色已被用户使用，无法删除");
        // }
        
        // 删除角色权限关联
        LambdaQueryWrapper<SysRolePermission> rpWrapper = new LambdaQueryWrapper<>();
        rpWrapper.eq(SysRolePermission::getRoleId, id);
        rolePermissionService.remove(rpWrapper);
        
        // 删除角色
        boolean success = roleService.removeById(id);
        if (success) {
            log.info("角色删除成功，角色ID: {}", id);
            return ApiResponse.success(null, "角色删除成功");
        } else {
            log.error("角色删除失败，角色ID: {}", id);
            return ApiResponse.error("角色删除失败");
        }
    }

    /**
     * 获取角色详情
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-GET获取资源
     * 
     * @param id 角色ID
     * @return 角色详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取角色详情", description = "根据ID获取角色详细信息")
    public ApiResponse<SysRole> getRoleById(
            @Parameter(description = "角色ID") @PathVariable @NotNull Long id) {
        log.info("获取角色详情，角色ID: {}", id);
        
        SysRole role = roleService.getById(id);
        if (role == null) {
            log.warn("角色不存在，角色ID: {}", id);
            return ApiResponse.error("角色不存在");
        }
        
        log.info("获取角色详情成功，角色ID: {}", id);
        return ApiResponse.success(role);
    }

    /**
     * 分页查询角色列表
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-GET获取资源列表
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param roleName 角色名称（可选，模糊查询）
     * @param roleCode 角色编码（可选，模糊查询）
     * @return 角色分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询角色列表", description = "分页查询角色列表，支持按名称和编码筛选")
    public ApiResponse<Page<SysRole>> getRoleList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "角色编码") @RequestParam(required = false) String roleCode) {
        log.info("分页查询角色列表，页码: {}, 每页大小: {}", page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<>();
        
        // 角色名称模糊查询
        if (roleName != null && !roleName.trim().isEmpty()) {
            wrapper.like(SysRole::getRoleName, roleName.trim());
        }
        
        // 角色编码模糊查询
        if (roleCode != null && !roleCode.trim().isEmpty()) {
            wrapper.like(SysRole::getRoleCode, roleCode.trim());
        }
        
        // 按创建时间倒序排序
        wrapper.orderByDesc(SysRole::getCreateTime);
        
        // 分页查询
        Page<SysRole> pageResult = roleService.page(new Page<>(page, size), wrapper);
        
        log.info("分页查询角色列表成功，总记录数: {}", pageResult.getTotal());
        return ApiResponse.success(pageResult);
    }

    /**
     * 更新角色权限
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-PUT更新资源
     * 
     * @param id 角色ID
     * @param permissionIds 权限ID列表
     * @return 更新结果
     */
    @PutMapping("/{id}/permissions")
    @Operation(summary = "更新角色权限", description = "为角色分配或更新权限")
    @Log(module = "角色管理", operation = "更新角色权限")
    public ApiResponse<Void> updateRolePermissions(
            @Parameter(description = "角色ID") @PathVariable @NotNull Long id,
            @RequestBody List<Long> permissionIds) {
        log.info("更新角色权限，角色ID: {}, 权限数量: {}", id, permissionIds.size());
        
        // 检查角色是否存在
        SysRole role = roleService.getById(id);
        if (role == null) {
            log.warn("角色不存在，角色ID: {}", id);
            return ApiResponse.error("角色不存在");
        }
        
        // 检查所有权限是否存在
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysPermission> permissions = permissionService.listByIds(permissionIds);
            if (permissions.size() != permissionIds.size()) {
                log.warn("部分权限不存在");
                return ApiResponse.error("部分权限不存在");
            }
        }
        
        // 删除原有的角色权限关联
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, id);
        rolePermissionService.remove(wrapper);
        
        // 添加新的角色权限关联
        if (permissionIds != null && !permissionIds.isEmpty()) {
            List<SysRolePermission> rolePermissions = permissionIds.stream()
                .map(permissionId -> {
                    SysRolePermission rp = new SysRolePermission();
                    rp.setRoleId(id);
                    rp.setPermissionId(permissionId);
                    return rp;
                })
                .collect(Collectors.toList());
            
            boolean success = rolePermissionService.saveBatch(rolePermissions);
            if (!success) {
                log.error("角色权限关联失败，角色ID: {}", id);
                return ApiResponse.error("角色权限关联失败");
            }
        }
        
        log.info("角色权限更新成功，角色ID: {}", id);
        return ApiResponse.success(null, "角色权限更新成功");
    }

    /**
     * 获取角色权限
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-GET获取资源
     * 
     * @param id 角色ID
     * @return 权限列表
     */
    @GetMapping("/{id}/permissions")
    @Operation(summary = "获取角色权限", description = "获取角色拥有的所有权限")
    public ApiResponse<List<SysPermission>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable @NotNull Long id) {
        log.info("获取角色权限，角色ID: {}", id);
        
        // 检查角色是否存在
        SysRole role = roleService.getById(id);
        if (role == null) {
            log.warn("角色不存在，角色ID: {}", id);
            return ApiResponse.error("角色不存在");
        }
        
        // 查询角色权限
        LambdaQueryWrapper<SysRolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysRolePermission::getRoleId, id);
        List<SysRolePermission> rolePermissions = rolePermissionService.list(wrapper);
        
        // 获取权限详情
        if (rolePermissions.isEmpty()) {
            log.info("角色无权限，角色ID: {}", id);
            return ApiResponse.success(List.of());
        }
        
        List<Long> permissionIds = rolePermissions.stream()
            .map(SysRolePermission::getPermissionId)
            .collect(Collectors.toList());
        
        List<SysPermission> permissions = permissionService.listByIds(permissionIds);
        
        log.info("获取角色权限成功，角色ID: {}, 权限数量: {}", id, permissions.size());
        return ApiResponse.success(permissions);
    }
}
