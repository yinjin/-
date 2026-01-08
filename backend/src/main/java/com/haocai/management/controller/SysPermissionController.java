package com.haocai.management.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.annotation.Log;
import com.haocai.management.common.ApiResponse;
import com.haocai.management.entity.SysPermission;
import com.haocai.management.entity.SysRolePermission;
import com.haocai.management.mapper.SysPermissionMapper;
import com.haocai.management.mapper.SysRolePermissionMapper;
import com.haocai.management.service.ISysPermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 权限管理控制器
 * 
 * 功能说明：
 * 1. 权限的增删改查操作
 * 2. 权限分页查询
 * 3. 权限树形结构查询
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
@RequestMapping("/api/permission")
@RequiredArgsConstructor
@Validated
@Tag(name = "权限管理", description = "权限管理相关接口")
public class SysPermissionController {

    private final ISysPermissionService permissionService;
    private final SysPermissionMapper permissionMapper;
    private final SysRolePermissionMapper rolePermissionMapper;

    /**
     * 创建权限
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-POST创建资源
     * 
     * @param permission 权限信息
     * @return 创建结果
     */
    @PostMapping
    @Operation(summary = "创建权限", description = "创建新的权限")
    @Log(module = "权限管理", operation = "创建权限")
    public ApiResponse<SysPermission> createPermission(@Valid @RequestBody SysPermission permission) {
        log.info("创建权限，权限名称: {}", permission.getName());
        
        // 检查权限编码是否已存在
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysPermission::getCode, permission.getCode());
        if (permissionService.count(wrapper) > 0) {
            log.warn("权限编码已存在: {}", permission.getCode());
            return ApiResponse.error("权限编码已存在");
        }
        
        // 创建权限
        boolean success = permissionService.save(permission);
        if (success) {
            log.info("权限创建成功，权限ID: {}", permission.getId());
            return ApiResponse.success(permission, "权限创建成功");
        } else {
            log.error("权限创建失败");
            return ApiResponse.error("权限创建失败");
        }
    }

    /**
     * 更新权限
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-PUT更新资源
     * 
     * @param id 权限ID
     * @param permission 权限信息
     * @return 更新结果
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新权限", description = "更新权限信息")
    @Log(module = "权限管理", operation = "更新权限")
    public ApiResponse<SysPermission> updatePermission(
            @Parameter(description = "权限ID") @PathVariable @NotNull Long id,
            @Valid @RequestBody SysPermission permission) {
        log.info("更新权限，权限ID: {}", id);
        
        // 检查权限是否存在
        SysPermission existingPermission = permissionService.getById(id);
        if (existingPermission == null) {
            log.warn("权限不存在，权限ID: {}", id);
            return ApiResponse.error("权限不存在");
        }
        
        // 如果修改了权限编码，检查新编码是否已存在
        if (!existingPermission.getCode().equals(permission.getCode())) {
            LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SysPermission::getCode, permission.getCode());
            wrapper.ne(SysPermission::getId, id);
            if (permissionService.count(wrapper) > 0) {
                log.warn("权限编码已存在: {}", permission.getCode());
                return ApiResponse.error("权限编码已存在");
            }
        }
        
        // 更新权限
        permission.setId(id);
        boolean success = permissionService.updateById(permission);
        if (success) {
            log.info("权限更新成功，权限ID: {}", id);
            return ApiResponse.success(permission, "权限更新成功");
        } else {
            log.error("权限更新失败，权限ID: {}", id);
            return ApiResponse.error("权限更新失败");
        }
    }

    /**
     * 删除权限
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-DELETE删除资源
     * 遵循：安全规范-检查权限是否被使用
     * 
     * @param id 权限ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除权限", description = "删除指定权限")
    @Log(module = "权限管理", operation = "删除权限")
    public ApiResponse<Void> deletePermission(
            @Parameter(description = "权限ID") @PathVariable @NotNull Long id) {
        log.info("删除权限，权限ID: {}", id);
        
        // 检查权限是否存在
        SysPermission permission = permissionService.getById(id);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", id);
            return ApiResponse.error("权限不存在");
        }
        
        // 检查是否有子权限
        LambdaQueryWrapper<SysPermission> childWrapper = new LambdaQueryWrapper<>();
        childWrapper.eq(SysPermission::getParentId, id);
        if (permissionService.count(childWrapper) > 0) {
            log.warn("权限下有子权限，无法删除，权限ID: {}", id);
            return ApiResponse.error("权限下有子权限，无法删除");
        }
        
        // 检查是否有角色关联此权限
        int roleCount = rolePermissionMapper.countRolesByPermissionId(id);
        if (roleCount > 0) {
            log.warn("权限已被 {} 个角色使用，无法删除，权限ID: {}", roleCount, id);
            return ApiResponse.error("权限已被 " + roleCount + " 个角色使用，无法删除");
        }
        
        // 删除权限
        boolean success = permissionService.removeById(id);
        if (success) {
            log.info("权限删除成功，权限ID: {}", id);
            return ApiResponse.success(null, "权限删除成功");
        } else {
            log.error("权限删除失败，权限ID: {}", id);
            return ApiResponse.error("权限删除失败");
        }
    }

    /**
     * 获取权限详情
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-GET获取资源
     * 
     * @param id 权限ID
     * @return 权限详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取权限详情", description = "根据ID获取权限详细信息")
    public ApiResponse<SysPermission> getPermissionById(
            @Parameter(description = "权限ID") @PathVariable @NotNull Long id) {
        log.info("获取权限详情，权限ID: {}", id);
        
        SysPermission permission = permissionService.getById(id);
        if (permission == null) {
            log.warn("权限不存在，权限ID: {}", id);
            return ApiResponse.error("权限不存在");
        }
        
        log.info("获取权限详情成功，权限ID: {}", id);
        return ApiResponse.success(permission);
    }

    /**
     * 获取权限树形结构
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：RESTful规范-GET获取资源
     * 遵循：树形结构规范-递归构建树
     * 
     * @return 权限树
     */
    @GetMapping("/tree")
    @Operation(summary = "获取权限树", description = "获取权限的树形结构")
    public ApiResponse<List<SysPermission>> getPermissionTree() {
        log.info("获取权限树形结构");
        
        // 使用自定义查询方法，确保返回正确的JSON字段名
        List<SysPermission> allPermissions = permissionMapper.selectAllForTreeWithCorrectAliases();
        
        log.info("从数据库查询到的权限数量: {}", allPermissions.size());
        if (allPermissions.isEmpty()) {
            log.warn("数据库中没有权限数据");
        } else {
            for (SysPermission permission : allPermissions) {
                log.info("权限详情 - ID: {}, 名称: {}, 编码: {}, 父ID: {}", 
                    permission.getId(), permission.getName(), permission.getCode(), permission.getParentId());
            }
        }
        
        // 构建树形结构，根节点的parent_id为0
        List<SysPermission> tree = buildPermissionTree(allPermissions, 0L);
        
        log.info("获取权限树形结构成功，根节点数量: {}", tree.size());
        return ApiResponse.success(tree);
    }

    /**
     * 分页查询权限列表
     * 
     * 遵循：控制层规范-统一的响应格式
     * 遵循：控制层规范-参数验证
     * 遵循：RESTful规范-GET获取资源列表
     * 
     * @param page 页码（从1开始）
     * @param size 每页大小
     * @param permissionName 权限名称（可选，模糊查询）
     * @param permissionType 权限类型（可选）
     * @return 权限分页列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询权限列表", description = "分页查询权限列表，支持按名称和类型筛选")
    public ApiResponse<Page<SysPermission>> getPermissionList(
            @Parameter(description = "页码") @RequestParam(defaultValue = "1") Integer page,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size,
            @Parameter(description = "权限名称") @RequestParam(required = false) String permissionName,
            @Parameter(description = "权限类型") @RequestParam(required = false) String permissionType) {
        log.info("分页查询权限列表，页码: {}, 每页大小: {}", page, size);
        
        // 构建查询条件
        LambdaQueryWrapper<SysPermission> wrapper = new LambdaQueryWrapper<>();
        
        // 权限名称模糊查询
        if (permissionName != null && !permissionName.trim().isEmpty()) {
            wrapper.like(SysPermission::getName, permissionName.trim());
        }
        
        // 权限类型查询
        if (permissionType != null && !permissionType.trim().isEmpty()) {
            wrapper.eq(SysPermission::getType, permissionType.trim());
        }
        
        // 按排序字段升序排序
        wrapper.orderByAsc(SysPermission::getSortOrder);
        
        // 分页查询
        Page<SysPermission> pageResult = permissionService.page(new Page<>(page, size), wrapper);
        
        log.info("分页查询权限列表成功，总记录数: {}", pageResult.getTotal());
        return ApiResponse.success(pageResult);
    }

    /**
     * 构建权限树
     * 
     * 遵循：树形结构规范-递归构建
     * 
     * @param permissions 所有权限列表
     * @param parentId 父权限ID
     * @return 权限树
     */
    private List<SysPermission> buildPermissionTree(List<SysPermission> permissions, Long parentId) {
        List<SysPermission> tree = new ArrayList<>();
        
        for (SysPermission permission : permissions) {
            // 使用 Objects.equals 来安全地比较，避免空指针异常
            if (java.util.Objects.equals(permission.getParentId(), parentId)) {
                // 递归查找子权限
                List<SysPermission> children = buildPermissionTree(permissions, permission.getId());
                permission.setChildren(children);
                tree.add(permission);
            }
        }
        
        return tree;
    }
}
