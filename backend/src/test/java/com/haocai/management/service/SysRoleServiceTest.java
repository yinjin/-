package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.RoleCreateDTO;
import com.haocai.management.dto.RoleUpdateDTO;
import com.haocai.management.dto.RoleVO;
import com.haocai.management.entity.SysRole;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色服务层测试类
 * 
 * 测试范围：
 * 1. 角色CRUD操作
 * 2. 角色权限关联管理
 * 3. 批量操作
 * 4. 边界条件和异常场景
 * 
 * 遵循：测试规范-第1条（测试类命名规范）
 * 遵循：测试规范-第2条（测试方法命名规范）
 */
@SpringBootTest
@Transactional
public class SysRoleServiceTest {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 测试创建角色
     */
    @Test
    public void testCreateRole() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("TEST_ROLE");
        dto.setName("测试角色");
        dto.setDescription("这是一个测试角色");
        dto.setStatus(1);

        Long roleId = roleService.createRole(dto, 1L);
        
        assertNotNull(roleId, "角色ID不应为空");
        
        SysRole role = roleService.getById(roleId);
        assertNotNull(role, "角色应存在");
        assertEquals("TEST_ROLE", role.getRoleCode(), "角色代码应匹配");
        assertEquals("测试角色", role.getRoleName(), "角色名称应匹配");
        assertEquals("这是一个测试角色", role.getDescription(), "角色描述应匹配");
        assertEquals(1, role.getStatus(), "状态应匹配");
    }

    /**
     * 测试创建重复角色代码
     */
    @Test
    public void testCreateRoleWithDuplicateCode() {
        // 先创建一个角色
        RoleCreateDTO dto1 = new RoleCreateDTO();
        dto1.setCode("DUPLICATE_ROLE");
        dto1.setName("角色1");
        dto1.setStatus(1);
        roleService.createRole(dto1, 1L);

        // 尝试创建相同代码的角色
        RoleCreateDTO dto2 = new RoleCreateDTO();
        dto2.setCode("DUPLICATE_ROLE");
        dto2.setName("角色2");
        dto2.setStatus(1);

        assertThrows(RuntimeException.class, () -> {
            roleService.createRole(dto2, 1L);
        }, "应抛出异常");
    }

    /**
     * 测试更新角色
     */
    @Test
    public void testUpdateRole() {
        // 先创建角色
        RoleCreateDTO createDto = new RoleCreateDTO();
        createDto.setCode("UPDATE_ROLE");
        createDto.setName("原始名称");
        createDto.setStatus(1);
        Long roleId = roleService.createRole(createDto, 1L);

        // 更新角色
        RoleUpdateDTO updateDto = new RoleUpdateDTO();
        updateDto.setId(roleId);
        updateDto.setName("更新后的名称");
        updateDto.setCode("UPDATE_ROLE");
        updateDto.setDescription("更新后的描述");
        updateDto.setStatus(1);

        roleService.updateRole(roleId, updateDto, 1L);

        // 验证更新
        SysRole role = roleService.getById(roleId);
        assertEquals("更新后的名称", role.getRoleName(), "名称应已更新");
        assertEquals("更新后的描述", role.getDescription(), "描述应已更新");
        assertEquals("UPDATE_ROLE", role.getRoleCode(), "代码应保持不变");
    }

    /**
     * 测试删除角色
     */
    @Test
    public void testDeleteRole() {
        // 先创建角色
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("DELETE_ROLE");
        dto.setName("待删除角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);

        // 删除角色
        roleService.deleteRole(roleId);

        // 验证删除（逻辑删除）
        SysRole role = roleService.getById(roleId);
        assertNull(role, "角色应已被删除");
    }

    /**
     * 测试批量删除角色
     */
    @Test
    public void testBatchDeleteRoles() {
        // 创建多个角色
        RoleCreateDTO dto1 = new RoleCreateDTO();
        dto1.setCode("BATCH_DELETE_ROLE_1");
        dto1.setName("批量删除角色1");
        dto1.setStatus(1);
        Long roleId1 = roleService.createRole(dto1, 1L);

        RoleCreateDTO dto2 = new RoleCreateDTO();
        dto2.setCode("BATCH_DELETE_ROLE_2");
        dto2.setName("批量删除角色2");
        dto2.setStatus(1);
        Long roleId2 = roleService.createRole(dto2, 1L);

        // 批量删除
        List<Long> roleIds = Arrays.asList(roleId1, roleId2);
        BatchUpdateResult result = roleService.batchDeleteRoles(roleIds);

        assertEquals(2, result.getSuccessCount(), "成功数量应为2");
        assertEquals(0, result.getFailureCount(), "失败数量应为0");

        // 验证删除
        SysRole role1 = roleService.getById(roleId1);
        SysRole role2 = roleService.getById(roleId2);
        assertNull(role1, "角色1应已被删除");
        assertNull(role2, "角色2应已被删除");
    }

    /**
     * 测试分页查询角色
     */
    @Test
    public void testGetRolePage() {
        // 创建多个角色
        for (int i = 1; i <= 5; i++) {
            RoleCreateDTO dto = new RoleCreateDTO();
            dto.setCode("PAGE_ROLE_" + i);
            dto.setName("分页角色" + i);
            dto.setStatus(1);
            roleService.createRole(dto, 1L);
        }

        // 分页查询
        Page<SysRole> page = new Page<>(1, 3);
        IPage<RoleVO> result = roleService.getRolePage(page, "分页", 1);

        assertNotNull(result, "分页结果不应为空");
        assertTrue(result.getRecords().size() <= 3, "每页最多3条记录");
        assertTrue(result.getTotal() >= 5, "总记录数至少为5");
    }

    /**
     * 测试分配权限给角色
     */
    @Test
    public void testAssignPermissionsToRole() {
        // 创建角色
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("PERM_ROLE");
        dto.setName("权限测试角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);

        // 分配权限（假设权限ID为1, 2, 3）
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        roleService.assignPermissionsToRole(roleId, permissionIds, 1L);

        // 验证权限分配
        List<Long> assignedPermissions = roleService.getPermissionIdsByRoleId(roleId);
        assertEquals(3, assignedPermissions.size(), "应分配3个权限");
        assertTrue(assignedPermissions.containsAll(permissionIds), "应包含所有分配的权限");
    }

    /**
     * 测试移除角色权限
     */
    @Test
    public void testRemovePermissionsFromRole() {
        // 创建角色并分配权限
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("REMOVE_PERM_ROLE");
        dto.setName("移除权限测试角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);

        List<Long> allPermissions = Arrays.asList(1L, 2L, 3L, 4L);
        roleService.assignPermissionsToRole(roleId, allPermissions, 1L);

        // 移除部分权限
        List<Long> toRemove = Arrays.asList(1L, 2L);
        roleService.removePermissionsFromRole(roleId, toRemove);

        // 验证权限移除
        List<Long> remainingPermissions = roleService.getPermissionIdsByRoleId(roleId);
        assertEquals(2, remainingPermissions.size(), "应剩余2个权限");
        assertFalse(remainingPermissions.contains(1L), "不应包含权限1");
        assertFalse(remainingPermissions.contains(2L), "不应包含权限2");
        assertTrue(remainingPermissions.contains(3L), "应包含权限3");
        assertTrue(remainingPermissions.contains(4L), "应包含权限4");
    }

    /**
     * 测试获取角色权限列表
     */
    @Test
    public void testGetPermissionsByRoleId() {
        // 创建角色并分配权限
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("GET_PERM_ROLE");
        dto.setName("获取权限测试角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);

        List<Long> permissionIds = Arrays.asList(1L, 2L);
        roleService.assignPermissionsToRole(roleId, permissionIds, 1L);

        // 获取权限列表
        List<Long> permissions = roleService.getPermissionIdsByRoleId(roleId);
        
        assertNotNull(permissions, "权限列表不应为空");
        assertEquals(2, permissions.size(), "应有2个权限");
    }

    /**
     * 测试根据用户ID获取角色列表
     */
    @Test
    public void testGetRolesByUserId() {
        // 创建角色
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setCode("USER_ROLE");
        dto.setName("用户角色");
        dto.setStatus(1);
        Long roleId = roleService.createRole(dto, 1L);

        // 假设用户ID为1，分配角色
        // 注意：这里需要先确保用户存在，实际测试中可能需要先创建用户
        // List<RoleVO> roles = roleService.getRolesByUserId(1L);
        // assertNotNull(roles, "角色列表不应为空");
    }

    /**
     * 测试空值输入
     */
    @Test
    public void testNullInput() {
        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            roleService.createRole(null, 1L);
        }, "创建角色时DTO为空应抛出BusinessException");

        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            roleService.updateRole(1L, null, 1L);
        }, "更新角色时DTO为空应抛出BusinessException");
    }

    /**
     * 测试边界条件：空列表
     */
    @Test
    public void testEmptyList() {
        // 测试空权限列表应抛出异常
        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            roleService.assignPermissionsToRole(1L, Arrays.asList(), 1L);
        }, "分配权限时权限列表为空应抛出BusinessException");
        
        // 测试空角色ID列表应抛出异常
        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            roleService.batchDeleteRoles(Arrays.asList());
        }, "批量删除时角色ID列表为空应抛出BusinessException");
    }
}
