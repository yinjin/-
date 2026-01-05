package com.material.system.controller;

import com.material.system.common.Result;
import com.material.system.dto.PermissionCreateDTO;
import com.material.system.dto.PermissionUpdateDTO;
import com.material.system.service.SysPermissionService;
import com.material.system.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理控制器
 */
@Tag(name = "权限管理", description = "权限管理相关接口")
@RestController
@RequestMapping("/api/permissions")
@RequiredArgsConstructor
public class SysPermissionController {
    
    private final SysPermissionService permissionService;
    
    @Operation(summary = "创建权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:create')")
    public Result<Long> createPermission(@Valid @RequestBody PermissionCreateDTO dto) {
        Long permissionId = permissionService.createPermission(dto);
        return Result.success(permissionId);
    }
    
    @Operation(summary = "更新权限")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:update')")
    public Result<Void> updatePermission(
            @Parameter(description = "权限ID") @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateDTO dto) {
        dto.setId(id);
        permissionService.updatePermission(dto);
        return Result.success();
    }
    
    @Operation(summary = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:delete')")
    public Result<Void> deletePermission(@Parameter(description = "权限ID") @PathVariable Long id) {
        permissionService.deletePermission(id);
        return Result.success();
    }
    
    @Operation(summary = "获取权限详情")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<PermissionVO> getPermissionById(@Parameter(description = "权限ID") @PathVariable Long id) {
        PermissionVO permissionVO = permissionService.getPermissionById(id);
        return Result.success(permissionVO);
    }
    
    @Operation(summary = "获取所有权限列表")
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<PermissionVO>> getAllPermissions() {
        List<PermissionVO> permissions = permissionService.getAllPermissions();
        return Result.success(permissions);
    }
    
    @Operation(summary = "获取权限树")
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<PermissionVO>> getPermissionTree() {
        List<PermissionVO> tree = permissionService.getPermissionTree();
        return Result.success(tree);
    }
}
