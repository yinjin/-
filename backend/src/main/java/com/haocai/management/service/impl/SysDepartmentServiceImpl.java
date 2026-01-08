package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.*;
import com.haocai.management.entity.SysDepartment;
import com.haocai.management.entity.SysUser;
import com.haocai.management.enums.DepartmentStatus;
import com.haocai.management.exception.BusinessException;
import com.haocai.management.mapper.SysDepartmentMapper;
import com.haocai.management.mapper.SysUserMapper;
import com.haocai.management.service.ISysDepartmentService;
import com.haocai.management.common.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 部门服务实现类
 * 
 * 遵循规范：
 * - 实现命名规范：使用业务名称+ServiceImpl后缀
 * - 继承规范：继承ServiceImpl获得基础CRUD能力
 * - 事务规范：使用@Transactional注解保证事务原子性
 * 
 * @author haocai
 * @date 2026-01-08
 */
@Slf4j
@Service
public class SysDepartmentServiceImpl extends ServiceImpl<SysDepartmentMapper, SysDepartment> 
        implements ISysDepartmentService {

    /**
     * 最大部门层级
     * 遵循：层级管理规范-第3条（限制最大层级）
     */
    private static final int MAX_DEPARTMENT_LEVEL = 10;

    private final SysDepartmentMapper departmentMapper;
    private final SysUserMapper userMapper;

    public SysDepartmentServiceImpl(SysDepartmentMapper departmentMapper, SysUserMapper userMapper) {
        this.departmentMapper = departmentMapper;
        this.userMapper = userMapper;
    }

    // ==================== 基础CRUD操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DepartmentVO> createDepartment(DepartmentCreateDTO dto, Long currentUserId) {
        log.info("创建部门请求: name={}, code={}, parentId={}", dto.getName(), dto.getCode(), dto.getParentId());
        
        // 遵循：业务逻辑规范-第1条（参数验证）
        // 验证部门编码唯一性
        if (isDepartmentCodeExists(dto.getCode(), null)) {
            throw new BusinessException(400, "部门编码已存在: " + dto.getCode());
        }
        
        // 验证父部门存在性
        if (dto.getParentId() != null) {
            SysDepartment parent = departmentMapper.selectById(dto.getParentId());
            if (parent == null) {
                throw new BusinessException(400, "父部门不存在: " + dto.getParentId());
            }
            // 遵循：数据库设计规范-第1.2条（枚举类型字段使用VARCHAR存储）
            if (DepartmentStatus.DISABLED.name().equals(parent.getStatus().name())) {
                throw new BusinessException(400, "父部门已被禁用，无法在其下创建子部门");
            }
        }
        
        // 创建部门
        SysDepartment department = new SysDepartment();
        department.setName(dto.getName());
        department.setCode(dto.getCode());
        department.setParentId(dto.getParentId());
        department.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        department.setStatus(DepartmentStatus.NORMAL);
        department.setLeader(dto.getLeader());
        department.setPhone(dto.getPhone());
        department.setEmail(dto.getEmail());
        department.setDescription(dto.getDescription());
        department.setCreateTime(LocalDateTime.now());
        department.setCreateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
        
        // 遵循：数据访问层规范-第3.3条（事务管理规范）
        departmentMapper.insert(department);
        
        log.info("部门创建成功: id={}, name={}", department.getId(), department.getName());
        
        return ApiResponse.success(convertToVO(department));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DepartmentVO> updateDepartment(DepartmentUpdateDTO dto, Long currentUserId) {
        log.info("更新部门请求: id={}, name={}", dto.getId(), dto.getName());
        
        // 验证部门存在
        SysDepartment department = departmentMapper.selectById(dto.getId());
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + dto.getId());
        }
        
        // 验证部门编码唯一性（排除自身）
        if (StringUtils.hasText(dto.getCode()) && isDepartmentCodeExists(dto.getCode(), dto.getId())) {
            throw new BusinessException(400, "部门编码已存在: " + dto.getCode());
        }
        
        // 更新字段
        if (StringUtils.hasText(dto.getName())) {
            department.setName(dto.getName());
        }
        if (StringUtils.hasText(dto.getCode())) {
            department.setCode(dto.getCode());
        }
        if (dto.getParentId() != null) {
            // 验证不能将部门设置为自己的子部门
            if (dto.getParentId().equals(dto.getId())) {
                throw new BusinessException(400, "不能将部门设置为自己的子部门");
            }
            department.setParentId(dto.getParentId());
            
            // 重新计算层级
            if (dto.getParentId() == null) {
                // 顶级部门，无需额外处理
            } else {
                SysDepartment newParent = departmentMapper.selectById(dto.getParentId());
                if (newParent == null) {
                    throw new BusinessException(400, "父部门不存在: " + dto.getParentId());
                }
            }
        }
        if (dto.getSortOrder() != null) {
            department.setSortOrder(dto.getSortOrder());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            department.setStatus(DepartmentStatus.valueOf(dto.getStatus()));
        }
        if (StringUtils.hasText(dto.getLeader())) {
            department.setLeader(dto.getLeader());
        }
        if (StringUtils.hasText(dto.getPhone())) {
            department.setPhone(dto.getPhone());
        }
        if (StringUtils.hasText(dto.getEmail())) {
            department.setEmail(dto.getEmail());
        }
        if (StringUtils.hasText(dto.getDescription())) {
            department.setDescription(dto.getDescription());
        }
        
        department.setUpdateTime(LocalDateTime.now());
        department.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
        
        departmentMapper.updateById(department);
        
        log.info("部门更新成功: id={}", department.getId());
        
        return ApiResponse.success(convertToVO(department));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> deleteDepartment(Long id, Long currentUserId) {
        log.info("删除部门请求: id={}", id);
        
        // 验证部门存在
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        
        // 遵循：业务逻辑规范-第4条（删除前检查子部门和关联用户）
        Map<String, Object> checkResult = checkCanDelete(id);
        if (!(Boolean) checkResult.get("canDelete")) {
            throw new BusinessException(400, (String) checkResult.get("reason"));
        }
        
        // 逻辑删除
        department.setDeleted(1);
        department.setUpdateTime(LocalDateTime.now());
        department.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
        departmentMapper.updateById(department);
        
        log.info("部门删除成功: id={}", id);
        
        return ApiResponse.success();
    }

    @Override
    public ApiResponse<DepartmentVO> getDepartment(Long id) {
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        return ApiResponse.success(convertToVO(department));
    }

    @Override
    public ApiResponse<Page<DepartmentVO>> listDepartments(DepartmentQueryDTO queryDTO) {
        log.info("分页查询部门列表: page={}, size={}, keyword={}, status={}", 
                queryDTO.getPage(), queryDTO.getSize(), queryDTO.getKeyword(), queryDTO.getStatus());
        
        // 构建分页对象
        Page<SysDepartment> page = new Page<>(queryDTO.getPage(), queryDTO.getSize());
        
        // 构建查询条件
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        
        // 状态筛选
        if (queryDTO.getStatus() != null && !queryDTO.getStatus().isEmpty()) {
            try {
                queryWrapper.eq(SysDepartment::getStatus, com.haocai.management.enums.DepartmentStatus.valueOf(queryDTO.getStatus()));
            } catch (IllegalArgumentException e) {
                throw new BusinessException(400, "无效的状态值: " + queryDTO.getStatus());
            }
        }
        
        // 关键词搜索（名称或编码）
        if (queryDTO.getKeyword() != null && !queryDTO.getKeyword().isEmpty()) {
            queryWrapper.and(wrapper -> wrapper
                    .like(SysDepartment::getName, queryDTO.getKeyword())
                    .or()
                    .like(SysDepartment::getCode, queryDTO.getKeyword()));
        }
        
        // 父部门筛选
        if (queryDTO.getParentId() != null) {
            queryWrapper.eq(SysDepartment::getParentId, queryDTO.getParentId());
        }
        
        // 排序
        String orderBy = queryDTO.getOrderBy() != null ? queryDTO.getOrderBy() : "sortOrder";
        String orderDir = queryDTO.getOrderDir() != null ? queryDTO.getOrderDir() : "asc";
        if ("asc".equalsIgnoreCase(orderDir)) {
            queryWrapper.orderByAsc(SysDepartment::getSortOrder);
            queryWrapper.orderByAsc(SysDepartment::getCreateTime);
        } else {
            queryWrapper.orderByDesc(SysDepartment::getSortOrder);
            queryWrapper.orderByDesc(SysDepartment::getCreateTime);
        }
        
        // 执行分页查询
        Page<SysDepartment> departmentPage = departmentMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        List<DepartmentVO> voList = departmentPage.getRecords().stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        
        Page<DepartmentVO> voPage = new Page<>(departmentPage.getCurrent(), departmentPage.getSize(), departmentPage.getTotal());
        voPage.setRecords(voList);
        
        log.info("分页查询部门列表完成: total={}", voPage.getTotal());
        
        return ApiResponse.success(voPage);
    }

    // ==================== 批量操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Map<String, Object>> batchDeleteDepartments(List<Long> ids, Long currentUserId) {
        log.info("批量删除部门请求: ids={}", ids);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "部门ID列表不能为空");
        }
        
        // 遵循：批量操作规范-第1条（先查询后操作）
        List<SysDepartment> departments = departmentMapper.selectBatchIds(ids);
        
        if (departments.isEmpty()) {
            throw new BusinessException(400, "批量删除失败，所有部门都不存在");
        }
        
        int total = ids.size();
        int success = 0;
        int failed = 0;
        List<String> failedReasons = new ArrayList<>();
        
        for (SysDepartment dept : departments) {
            try {
                Map<String, Object> result = checkCanDelete(dept.getId());
                if ((Boolean) result.get("canDelete")) {
                    dept.setDeleted(1);
                    dept.setUpdateTime(LocalDateTime.now());
                    dept.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
                    departmentMapper.updateById(dept);
                    success++;
                } else {
                    failed++;
                    failedReasons.add(dept.getName() + ": " + result.get("reason"));
                }
            } catch (Exception e) {
                log.error("删除部门失败: id={}, error={}", dept.getId(), e.getMessage());
                failed++;
                failedReasons.add(dept.getName() + ": " + e.getMessage());
            }
        }
        
        // 遵循：批量操作规范-第2条（返回详细结果）
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("success", success);
        result.put("failed", failed);
        result.put("failedDetails", failedReasons);
        
        log.info("批量删除部门完成: total={}, success={}, failed={}", total, success, failed);
        
        return ApiResponse.success(result);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Map<String, Object>> batchUpdateStatus(List<Long> ids, String status, Long currentUserId) {
        log.info("批量更新部门状态请求: ids={}, status={}", ids, status);
        
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "部门ID列表不能为空");
        }
        
        DepartmentStatus departmentStatus;
        try {
            departmentStatus = DepartmentStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BusinessException(400, "无效的状态值: " + status);
        }
        
        List<SysDepartment> departments = departmentMapper.selectBatchIds(ids);
        
        if (departments.isEmpty()) {
            throw new BusinessException(400, "批量更新失败，所有部门都不存在");
        }
        
        int total = ids.size();
        int success = 0;
        int failed = 0;
        
        for (SysDepartment department : departments) {
            try {
                department.setStatus(departmentStatus);
                department.setUpdateTime(LocalDateTime.now());
                department.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
                departmentMapper.updateById(department);
                success++;
            } catch (Exception e) {
                log.error("更新部门状态失败: id={}, error={}", department.getId(), e.getMessage());
                failed++;
            }
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("success", success);
        result.put("failed", failed);
        
        log.info("批量更新部门状态完成: total={}, success={}, failed={}", total, success, failed);
        
        return ApiResponse.success(result);
    }

    // ==================== 树形结构操作 ====================

    @Override
    public ApiResponse<List<DepartmentTreeVO>> getDepartmentTree(Boolean includeDisabled) {
        log.info("获取部门树形结构请求: includeDisabled={}", includeDisabled);
        
        // 查询所有部门
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        if (!Boolean.TRUE.equals(includeDisabled)) {
            queryWrapper.eq(SysDepartment::getStatus, DepartmentStatus.NORMAL);
        }
        queryWrapper.orderByAsc(SysDepartment::getSortOrder);
        
        List<SysDepartment> departments = departmentMapper.selectList(queryWrapper);
        
        // 遵循：树形结构规范-第1条（递归构建树形结构）
        List<DepartmentTreeVO> rootDepartments = buildDepartmentTree(departments, null);
        
        return ApiResponse.success(rootDepartments);
    }

    @Override
    public ApiResponse<DepartmentTreeVO> getDepartmentTreeById(Long id) {
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        
        // 查询所有子部门
        List<SysDepartment> allChildren = getAllChildren(id);
        allChildren.add(department);
        
        DepartmentTreeVO treeVO = convertToTreeVO(department);
        // 递归构建子部门
        buildTreeRecursive(treeVO, allChildren);
        
        return ApiResponse.success(treeVO);
    }

    @Override
    public ApiResponse<List<DepartmentTreeVO>> getChildrenByParentId(Long parentId) {
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        queryWrapper.eq(SysDepartment::getStatus, DepartmentStatus.NORMAL);
        queryWrapper.eq(SysDepartment::getParentId, parentId);
        queryWrapper.orderByAsc(SysDepartment::getSortOrder);
        
        List<SysDepartment> children = departmentMapper.selectList(queryWrapper);
        
        List<DepartmentTreeVO> result = children.stream()
                .map(this::convertToTreeVO)
                .collect(Collectors.toList());
        
        return ApiResponse.success(result);
    }

    // ==================== 部门移动操作 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<DepartmentVO> moveDepartment(Long id, Long newParentId, Long currentUserId) {
        log.info("移动部门请求: id={}, newParentId={}", id, newParentId);
        
        // 验证部门存在
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        
        // 遵循：层级管理规范-第1条（移动前验证）
        // 不能移动到自己或自己的子部门下
        if (id.equals(newParentId)) {
            throw new BusinessException(400, "不能将部门移动到自身下");
        }
        
        // 检查新父部门是否是自己的子部门
        List<SysDepartment> children = getAllChildren(id);
        if (children.stream().anyMatch(c -> c.getId().equals(newParentId))) {
            throw new BusinessException(400, "不能将部门移动到自己的子部门下");
        }
        
        // 验证新父部门存在
        if (newParentId != null) {
            SysDepartment newParent = departmentMapper.selectById(newParentId);
            if (newParent == null) {
                throw new BusinessException(400, "新父部门不存在: " + newParentId);
            }
        }
        
        // 更新父部门
        department.setParentId(newParentId);
        department.setUpdateTime(LocalDateTime.now());
        department.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
        departmentMapper.updateById(department);
        
        log.info("部门移动成功: id={}", id);
        
        return ApiResponse.success(convertToVO(department));
    }

    // ==================== 部门负责人管理 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> setDepartmentLeader(Long id, Long leaderId, Long currentUserId) {
        log.info("设置部门负责人请求: departmentId={}, leaderId={}", id, leaderId);
        
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        
        // 验证用户存在并获取用户名
        if (leaderId != null) {
            SysUser user = userMapper.selectById(leaderId);
            if (user == null) {
                throw new BusinessException(400, "用户不存在: " + leaderId);
            }
            // 设置用户名作为负责人
            department.setLeader(user.getName());
        } else {
            department.setLeader(null);
        }
        
        department.setUpdateTime(LocalDateTime.now());
        department.setUpdateBy(currentUserId != null ? String.valueOf(currentUserId) : null);
        departmentMapper.updateById(department);
        
        return ApiResponse.success();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResponse<Void> removeDepartmentLeader(Long id, Long currentUserId) {
        return setDepartmentLeader(id, null, currentUserId);
    }

    // ==================== 部门关联查询 ====================

    @Override
    public ApiResponse<List<Long>> getUserIdsByDepartmentId(Long id) {
        SysDepartment department = departmentMapper.selectById(id);
        if (department == null) {
            throw new BusinessException(400, "部门不存在: " + id);
        }
        
        // 获取当前部门及其所有子部门的用户ID
        List<SysDepartment> allDepartments = getAllChildren(id);
        allDepartments.add(department);
        
        // 由于leader字段现在是String类型（用户名），需要通过用户名查询用户ID
        List<Long> userIds = new ArrayList<>();
        for (SysDepartment dept : allDepartments) {
            if (StringUtils.hasText(dept.getLeader())) {
                // 通过用户名查询用户ID
                LambdaQueryWrapper<SysUser> userWrapper = new LambdaQueryWrapper<>();
                userWrapper.eq(SysUser::getName, dept.getLeader());
                SysUser user = userMapper.selectOne(userWrapper);
                if (user != null) {
                    userIds.add(user.getId());
                }
            }
        }
        
        return ApiResponse.success(userIds);
    }

    @Override
    public ApiResponse<List<DepartmentVO>> getDepartmentsByUserId(Long userId) {
        // 先查询用户信息获取用户名
        SysUser user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(400, "用户不存在: " + userId);
        }
        
        // 通过用户名查询部门
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        queryWrapper.eq(SysDepartment::getLeader, user.getName());
        List<SysDepartment> departments = departmentMapper.selectList(queryWrapper);
        List<DepartmentVO> result = departments.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
        return ApiResponse.success(result);
    }

    // ==================== 私有辅助方法 ====================

    /**
     * 将部门实体转换为VO
     * 遵循：数据转换规范-第1条（实体与VO之间的转换）
     */
    private DepartmentVO convertToVO(SysDepartment department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        
        // 手动转换枚举类型为字符串
        if (department.getStatus() != null) {
            vo.setStatus(department.getStatus().name());
        }
        
        // 设置父部门名称
        if (department.getParentId() != null) {
            SysDepartment parent = departmentMapper.selectById(department.getParentId());
            if (parent != null) {
                vo.setParentName(parent.getName());
            }
        }
        
        // 设置负责人名称（leader字段已经是用户名，直接使用）
        if (StringUtils.hasText(department.getLeader())) {
            vo.setLeaderName(department.getLeader());
        }
        
        // 设置子部门数量
        LambdaQueryWrapper<SysDepartment> countWrapper = new LambdaQueryWrapper<>();
        countWrapper.eq(SysDepartment::getParentId, department.getId());
        countWrapper.eq(SysDepartment::getDeleted, 0);
        vo.setChildrenCount(Math.toIntExact(departmentMapper.selectCount(countWrapper)));
        
        return vo;
    }

    /**
     * 将部门实体转换为树形VO
     */
    private DepartmentTreeVO convertToTreeVO(SysDepartment department) {
        log.debug("开始转换部门为树形VO: id={}, name={}, status={}", 
                department.getId(), department.getName(), department.getStatus());
        
        DepartmentTreeVO vo = new DepartmentTreeVO();
        BeanUtils.copyProperties(department, vo);
        
        log.debug("BeanUtils复制完成: vo.id={}, vo.name={}, vo.status={}", 
                vo.getId(), vo.getName(), vo.getStatus());
        
        // 手动转换枚举类型为字符串
        if (department.getStatus() != null) {
            vo.setStatus(department.getStatus().name());
            log.debug("枚举转换完成: status={}", vo.getStatus());
        }
        
        vo.setChildren(new ArrayList<>());
        log.debug("树形VO转换完成: id={}, name={}, status={}", 
                vo.getId(), vo.getName(), vo.getStatus());
        
        return vo;
    }

    /**
     * 构建部门树形结构
     * 遵循：树形结构规范-第1条（递归构建树形结构）
     */
    private List<DepartmentTreeVO> buildDepartmentTree(List<SysDepartment> departments, Long parentId) {
        log.debug("构建部门树形结构: parentId={}, 部门总数={}", parentId, departments.size());
        
        List<DepartmentTreeVO> result = departments.stream()
                .filter(d -> {
                    // 处理顶级部门：parentId为null或0都视为顶级部门
                    boolean match = (parentId == null && (d.getParentId() == null || d.getParentId() == 0L)) 
                            || Objects.equals(d.getParentId(), parentId);
                    log.debug("部门过滤: id={}, name={}, parentId={}, 匹配={}", 
                            d.getId(), d.getName(), d.getParentId(), match);
                    return match;
                })
                .map(this::convertToTreeVO)
                .peek(vo -> vo.setChildren(buildDepartmentTree(departments, vo.getId())))
                .collect(Collectors.toList());
        
        log.debug("构建部门树形结构完成: parentId={}, 子节点数量={}", parentId, result.size());
        
        return result;
    }

    /**
     * 递归构建树形结构
     * 遵循：树形结构规范-第2条（为每个节点设置子部门列表）
     */
    private void buildTreeRecursive(DepartmentTreeVO parentVO, List<SysDepartment> allDepartments) {
        List<DepartmentTreeVO> children = allDepartments.stream()
                .filter(d -> Objects.equals(d.getParentId(), parentVO.getId()))
                .map(this::convertToTreeVO)
                .peek(child -> buildTreeRecursive(child, allDepartments))
                .collect(Collectors.toList());
        parentVO.setChildren(children);
    }

    /**
     * 获取部门的所有子部门（递归）
     * 遵循：层级管理规范-第3条（递归查询子部门）
     */
    private List<SysDepartment> getAllChildren(Long departmentId) {
        List<SysDepartment> allChildren = new ArrayList<>();
        
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getParentId, departmentId);
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        
        List<SysDepartment> directChildren = departmentMapper.selectList(queryWrapper);
        
        for (SysDepartment child : directChildren) {
            allChildren.add(child);
            // 递归获取子部门的子部门
            allChildren.addAll(getAllChildren(child.getId()));
        }
        
        return allChildren;
    }


    // ==================== 公共验证方法（接口定义） ====================

    /**
     * 检查部门编码是否存在
     * 遵循：业务逻辑规范-第2条（唯一性检查）
     */
    public boolean isDepartmentCodeExists(String code, Long excludeId) {
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getCode, code);
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        if (excludeId != null) {
            queryWrapper.ne(SysDepartment::getId, excludeId);
        }
        return departmentMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查父部门下是否存在同名部门
     */
    public boolean isDepartmentNameExistsInParent(String name, Long parentId, Long excludeId) {
        LambdaQueryWrapper<SysDepartment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysDepartment::getName, name);
        queryWrapper.eq(SysDepartment::getParentId, parentId);
        queryWrapper.eq(SysDepartment::getDeleted, 0);
        if (excludeId != null) {
            queryWrapper.ne(SysDepartment::getId, excludeId);
        }
        return departmentMapper.selectCount(queryWrapper) > 0;
    }

    /**
     * 检查部门是否可以删除
     * 遵循：业务逻辑规范-第4条（删除前检查子部门和关联用户）
     */
    public Map<String, Object> checkCanDelete(Long departmentId) {
        Map<String, Object> result = new HashMap<>();
        result.put("canDelete", true);
        result.put("reason", "");
        
        // 检查是否有子部门
        LambdaQueryWrapper<SysDepartment> childrenWrapper = new LambdaQueryWrapper<>();
        childrenWrapper.eq(SysDepartment::getParentId, departmentId);
        childrenWrapper.eq(SysDepartment::getDeleted, 0);
        long childrenCount = departmentMapper.selectCount(childrenWrapper);
        
        if (childrenCount > 0) {
            result.put("canDelete", false);
            result.put("reason", "该部门下存在" + childrenCount + "个子部门，请先删除或移动子部门");
            return result;
        }
        
        // 检查是否有用户属于该部门
        long usersCount = getUserIdsByDepartmentId(departmentId).getData().size();
        
        if (usersCount > 0) {
            result.put("canDelete", false);
            result.put("reason", "该部门下存在" + usersCount + "个用户，请先将用户移动到其他部门");
            return result;
        }
        
        return result;
    }
}
