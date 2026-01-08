package com.haocai.management.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.*;
import com.haocai.management.entity.SysDepartment;
import com.haocai.management.common.ApiResponse;

import java.util.List;
import java.util.Map;

/**
 * 部门服务接口
 * 
 * 遵循规范：
 * - 服务命名规范：使用I+业务名称+Service后缀
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 继承规范：继承IService接口获得基础CRUD能力
 * 
 * @author haocai
 * @date 2026-01-08
 */
public interface ISysDepartmentService extends IService<SysDepartment> {

    // ==================== 基础CRUD操作 ====================

    /**
     * 创建部门
     * 
     * 遵循：业务逻辑规范-第1条（参数验证）
     * 遵循：业务逻辑规范-第2条（唯一性检查）
     * 
     * @param dto 部门创建请求
     * @param currentUserId 当前用户ID
     * @return 创建后的部门信息
     */
    ApiResponse<DepartmentVO> createDepartment(DepartmentCreateDTO dto, Long currentUserId);

    /**
     * 更新部门
     * 
     * @param dto 部门更新请求
     * @param currentUserId 当前用户ID
     * @return 更新后的部门信息
     */
    ApiResponse<DepartmentVO> updateDepartment(DepartmentUpdateDTO dto, Long currentUserId);

    /**
     * 删除部门
     * 
     * 遵循：业务逻辑规范-第3条（删除前检查）
     * 
     * @param id 部门ID
     * @param currentUserId 当前用户ID
     * @return 删除结果
     */
    ApiResponse<Void> deleteDepartment(Long id, Long currentUserId);

    /**
     * 获取部门详情
     * 
     * @param id 部门ID
     * @return 部门详情
     */
    ApiResponse<DepartmentVO> getDepartment(Long id);

    /**
     * 分页查询部门列表
     * 
     * 遵循：分页查询规范-第1条（使用Page+条件构造器）
     * 
     * @param queryDTO 分页查询参数
     * @return 分页结果
     */
    ApiResponse<Page<DepartmentVO>> listDepartments(DepartmentQueryDTO queryDTO);

    // ==================== 批量操作 ====================

    /**
     * 批量删除部门
     * 
     * 遵循：批量操作规范-第1条（先查询后删除）
     * 遵循：批量操作规范-第2条（返回详细结果）
     * 
     * @param ids 部门ID列表
     * @param currentUserId 当前用户ID
     * @return 批量操作结果
     */
    ApiResponse<Map<String, Object>> batchDeleteDepartments(List<Long> ids, Long currentUserId);

    /**
     * 批量更新部门状态
     * 
     * @param ids 部门ID列表
     * @param status 目标状态
     * @param currentUserId 当前用户ID
     * @return 批量操作结果
     */
    ApiResponse<Map<String, Object>> batchUpdateStatus(List<Long> ids, String status, Long currentUserId);

    // ==================== 树形结构操作 ====================

    /**
     * 获取部门树形结构
     * 
     * 遵循：树形结构规范-第1条（递归构建树形结构）
     * 
     * @param includeDisabled 是否包含禁用的部门
     * @return 部门树形结构列表
     */
    ApiResponse<List<DepartmentTreeVO>> getDepartmentTree(Boolean includeDisabled);

    /**
     * 获取指定部门的树形结构（包含所有子部门）
     * 
     * @param id 部门ID
     * @return 部门树形结构
     */
    ApiResponse<DepartmentTreeVO> getDepartmentTreeById(Long id);

    /**
     * 懒加载获取子部门
     * 
     * 遵循：树形结构规范-第3条（支持懒加载）
     * 
     * @param parentId 父部门ID
     * @return 子部门列表
     */
    ApiResponse<List<DepartmentTreeVO>> getChildrenByParentId(Long parentId);

    // ==================== 部门移动操作 ====================

    /**
     * 移动部门（调整部门层级）
     * 
     * 遵循：层级管理规范-第1条（移动前验证）
     * 遵循：层级管理规范-第2条（更新所有子部门层级）
     * 
     * @param id 要移动的部门ID
     * @param newParentId 新的父部门ID
     * @param currentUserId 当前用户ID
     * @return 移动后的部门信息
     */
    ApiResponse<DepartmentVO> moveDepartment(Long id, Long newParentId, Long currentUserId);

    // ==================== 部门负责人管理 ====================

    /**
     * 设置部门负责人
     * 
     * @param id 部门ID
     * @param leaderId 负责人ID
     * @param currentUserId 当前用户ID
     * @return 设置结果
     */
    ApiResponse<Void> setDepartmentLeader(Long id, Long leaderId, Long currentUserId);

    /**
     * 移除部门负责人
     * 
     * @param id 部门ID
     * @param currentUserId 当前用户ID
     * @return 移除结果
     */
    ApiResponse<Void> removeDepartmentLeader(Long id, Long currentUserId);

    // ==================== 部门关联查询 ====================

    /**
     * 查询部门下的所有用户
     * 
     * @param id 部门ID
     * @return 用户ID列表
     */
    ApiResponse<List<Long>> getUserIdsByDepartmentId(Long id);

    /**
     * 查询用户所属部门
     * 
     * @param userId 用户ID
     * @return 部门列表（支持多部门场景）
     */
    ApiResponse<List<DepartmentVO>> getDepartmentsByUserId(Long userId);

    // ==================== 验证操作 ====================

    /**
     * 检查部门编码是否存在
     * 
     * @param code 部门编码
     * @param excludeId 排除的部门ID（用于更新时验证）
     * @return 是否存在
     */
    boolean isDepartmentCodeExists(String code, Long excludeId);

    /**
     * 检查部门名称在父部门下是否重复
     * 
     * @param name 部门名称
     * @param parentId 父部门ID
     * @param excludeId 排除的部门ID
     * @return 是否重复
     */
    boolean isDepartmentNameExistsInParent(String name, Long parentId, Long excludeId);

    /**
     * 检查是否可以删除部门
     * 
     * 遵循：业务逻辑规范-第4条（删除前检查子部门和关联用户）
     * 
     * @param id 部门ID
     * @return 检查结果（canDelete=true表示可删除，false表示不可删除）
     */
    Map<String, Object> checkCanDelete(Long id);
}
