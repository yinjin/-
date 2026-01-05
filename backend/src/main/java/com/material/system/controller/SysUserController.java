package com.material.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.material.system.common.Result;
import com.material.system.dto.UserCreateDTO;
import com.material.system.dto.UserLoginDTO;
import com.material.system.dto.UserUpdateDTO;
import com.material.system.service.SysUserService;
import com.material.system.vo.UserVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 */
@Tag(name = "用户管理", description = "用户管理相关接口")
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Validated
public class SysUserController {
    
    private final SysUserService userService;
    
    /**
     * 用户登录
     */
    @Operation(summary = "用户登录")
    @PostMapping("/login")
    public Result<String> login(@Validated @RequestBody UserLoginDTO loginDTO) {
        String token = userService.login(loginDTO);
        return Result.success(token);
    }
    
    /**
     * 创建用户
     */
    @Operation(summary = "创建用户")
    @PostMapping
    public Result<Long> createUser(@Validated @RequestBody UserCreateDTO createDTO) {
        Long userId = userService.createUser(createDTO);
        return Result.success(userId);
    }
    
    /**
     * 更新用户
     */
    @Operation(summary = "更新用户")
    @PutMapping("/{id}")
    public Result<Void> updateUser(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Validated @RequestBody UserUpdateDTO updateDTO) {
        updateDTO.setId(id);
        userService.updateUser(updateDTO);
        return Result.success();
    }
    
    /**
     * 删除用户
     */
    @Operation(summary = "删除用户")
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
    
    /**
     * 获取用户详情
     */
    @Operation(summary = "获取用户详情")
    @GetMapping("/{id}")
    public Result<UserVO> getUserById(@Parameter(description = "用户ID") @PathVariable Long id) {
        UserVO userVO = userService.getUserById(id);
        return Result.success(userVO);
    }
    
    /**
     * 分页查询用户列表
     */
    @Operation(summary = "分页查询用户列表")
    @GetMapping("/page")
    public Result<Page<UserVO>> getUserPage(
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "真实姓名") @RequestParam(required = false) String realName,
            @Parameter(description = "状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "部门ID") @RequestParam(required = false) Long departmentId) {
        Page<UserVO> page = userService.getUserPage(current, size, username, realName, status, departmentId);
        return Result.success(page);
    }
    
    /**
     * 修改密码
     */
    @Operation(summary = "修改密码")
    @PutMapping("/{id}/password")
    public Result<Void> changePassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "旧密码") @RequestParam String oldPassword,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.changePassword(id, oldPassword, newPassword);
        return Result.success();
    }
    
    /**
     * 重置密码
     */
    @Operation(summary = "重置密码")
    @PutMapping("/{id}/reset-password")
    public Result<Void> resetPassword(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "新密码") @RequestParam String newPassword) {
        userService.resetPassword(id, newPassword);
        return Result.success();
    }
    
    /**
     * 更新用户状态
     */
    @Operation(summary = "更新用户状态")
    @PutMapping("/{id}/status")
    public Result<Void> updateUserStatus(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "状态") @RequestParam Integer status) {
        userService.updateUserStatus(id, status);
        return Result.success();
    }
    
    /**
     * 分配角色
     */
    @Operation(summary = "分配角色")
    @PostMapping("/{id}/role")
    public Result<Void> assignRole(
            @Parameter(description = "用户ID") @PathVariable Long id,
            @Parameter(description = "角色ID") @RequestParam Long roleId) {
        userService.assignRole(id, roleId);
        return Result.success();
    }
    
    /**
     * 移除角色
     */
    @Operation(summary = "移除角色")
    @DeleteMapping("/{id}/role")
    public Result<Void> removeRole(@Parameter(description = "用户ID") @PathVariable Long id) {
        userService.removeRole(id);
        return Result.success();
    }
    
    /**
     * 获取用户角色ID列表
     */
    @Operation(summary = "获取用户角色ID列表")
    @GetMapping("/{id}/roles")
    public Result<java.util.List<Long>> getUserRoleIds(@Parameter(description = "用户ID") @PathVariable Long id) {
        java.util.List<Long> roleIds = userService.getUserRoleIds(id);
        return Result.success(roleIds);
    }
}
