package com.material.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.common.Result;
import com.material.system.dto.RoleCreateDTO;
import com.material.system.dto.RoleUpdateDTO;
import com.material.system.service.SysRoleService;
import com.material.system.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色管理控制器
 */
@Tag(name = "角色管理", description = "角色管理相关接口")
@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {
    
    private final SysRoleService roleService;
    
    @Operation(summary = "创建角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:create')")
    public Result<Long> createRole(@Valid @RequestBody RoleCreateDTO dto) {
        Long roleId = roleService.createRole(dto);
        return Result.success(roleId);
    }
    
    @Operation(summary = "更新角色")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:update')")
    public Result<Void> updateRole(
            @Parameter(description = "角色ID") @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDTO dto) {
        dto.setId(id);
        roleService.updateRole(dto);
        return Result.success();
    }
    
    @Operation(summary = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:delete')")
    public Result<Void> deleteRole(@Parameter(description = "角色ID") @PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success();
    }
    
    @Operation(summary = "获取角色详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<RoleVO> getRoleById(@Parameter(description = "角色ID") @PathVariable Long id) {
        RoleVO roleVO = roleService.getRoleById(id);
        return Result.success(roleVO);
    }
    
    @Operation(summary = "分页查询角色列表")
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Page<RoleVO>> getRolePage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName) {
        Page<RoleVO> page = roleService.getRolePage(current, size, roleName);
        return Result.success(page);
    }
    
    @Operation(summary = "获取所有角色")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
    
    @Operation(summary = "为角色分配权限")
    @PostMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:assign')")
    public Result<Void> assignPermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId,
            @Parameter(description = "权限ID列表") @RequestBody List<Long> permissionIds) {
        roleService.assignPermissions(roleId, permissionIds);
        return Result.success();
    }
    
    @Operation(summary = "获取角色的权限列表")
    @GetMapping("/{roleId}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<Long>> getRolePermissions(
            @Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Long> permissionIds = roleService.getRolePermissionIds(roleId);
        return Result.success(permissionIds);
    }
}
