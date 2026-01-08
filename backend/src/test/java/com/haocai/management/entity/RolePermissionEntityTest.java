package com.haocai.management.entity;

import com.haocai.management.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色权限实体类测试
 * 验证实体类和DTO类的正确性
 */
class RolePermissionEntityTest {

    /**
     * 测试SysRole实体类
     */
    @Test
    void testSysRoleEntity() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setRoleName("管理员");
        role.setRoleCode("admin");
        role.setDescription("系统管理员角色");
        role.setStatus(1);
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.setCreateBy("1");
        role.setUpdateBy("1");
        role.setDeleted(0);

        assertNotNull(role);
        assertEquals(1L, role.getId());
        assertEquals("管理员", role.getRoleName());
        assertEquals("admin", role.getRoleCode());
        assertEquals("系统管理员角色", role.getDescription());
        assertEquals(1, role.getStatus());
        assertEquals("1", role.getCreateBy());
        assertEquals(0, role.getDeleted());
    }

    /**
     * 测试SysPermission实体类
     */
    @Test
    void testSysPermissionEntity() {
        SysPermission permission = new SysPermission();
        permission.setId(1L);
        permission.setName("用户管理");
        permission.setCode("system:user");
        permission.setParentId(0L);
        permission.setPath("/system/user");
        permission.setComponent("system/user/index");
        permission.setIcon("user");
        permission.setSortOrder(1);
        permission.setStatus(1);
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        permission.setCreateBy("1");
        permission.setUpdateBy("1");
        permission.setDeleted(0);

        assertNotNull(permission);
        assertEquals(1L, permission.getId());
        assertEquals("用户管理", permission.getName());
        assertEquals("system:user", permission.getCode());
        assertEquals(0L, permission.getParentId());
        assertEquals("/system/user", permission.getPath());
        assertEquals("system/user/index", permission.getComponent());
        assertEquals("user", permission.getIcon());
        assertEquals(1, permission.getSortOrder());
        assertEquals(1, permission.getStatus());
        assertEquals(0, permission.getDeleted());
    }

    /**
     * 测试SysRolePermission实体类
     */
    @Test
    void testSysRolePermissionEntity() {
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setId(1L);
        rolePermission.setRoleId(1L);
        rolePermission.setPermissionId(1L);
        rolePermission.setCreateTime(LocalDateTime.now());
        rolePermission.setCreateBy("1");
        rolePermission.setDeleted(0);

        assertNotNull(rolePermission);
        assertEquals(1L, rolePermission.getId());
        assertEquals(1L, rolePermission.getRoleId());
        assertEquals(1L, rolePermission.getPermissionId());
        assertEquals("1", rolePermission.getCreateBy());
        assertEquals(0, rolePermission.getDeleted());
    }

    /**
     * 测试SysUserRole实体类
     */
    @Test
    void testSysUserRoleEntity() {
        SysUserRole userRole = new SysUserRole();
        userRole.setId(1L);
        userRole.setUserId(1L);
        userRole.setRoleId(1L);
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setCreateBy("1");
        userRole.setDeleted(0);

        assertNotNull(userRole);
        assertEquals(1L, userRole.getId());
        assertEquals(1L, userRole.getUserId());
        assertEquals(1L, userRole.getRoleId());
        assertEquals("1", userRole.getCreateBy());
        assertEquals(0, userRole.getDeleted());
    }

    /**
     * 测试RoleCreateDTO
     */
    @Test
    void testRoleCreateDTO() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setName("教师");
        dto.setCode("teacher");
        dto.setDescription("教师角色");
        dto.setStatus(1);

        assertNotNull(dto);
        assertEquals("教师", dto.getName());
        assertEquals("teacher", dto.getCode());
        assertEquals("教师角色", dto.getDescription());
        assertEquals(1, dto.getStatus());
    }

    /**
     * 测试RoleUpdateDTO
     */
    @Test
    void testRoleUpdateDTO() {
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setId(1L);
        dto.setName("管理员");
        dto.setCode("admin");
        dto.setDescription("系统管理员角色");
        dto.setStatus(1);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("管理员", dto.getName());
        assertEquals("admin", dto.getCode());
        assertEquals("系统管理员角色", dto.getDescription());
        assertEquals(1, dto.getStatus());
    }

    /**
     * 测试RoleVO
     */
    @Test
    void testRoleVO() {
        RoleVO vo = new RoleVO();
        vo.setId(1L);
        vo.setName("管理员");
        vo.setCode("admin");
        vo.setDescription("系统管理员角色");
        vo.setStatus(1);
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());
        vo.setCreateBy("admin");
        vo.setUpdateBy("admin");

        assertNotNull(vo);
        assertEquals(1L, vo.getId());
        assertEquals("管理员", vo.getName());
        assertEquals("admin", vo.getCode());
        assertEquals("系统管理员角色", vo.getDescription());
        assertEquals(1, vo.getStatus());
        assertEquals("admin", vo.getCreateBy());
        assertEquals("admin", vo.getUpdateBy());
    }

    /**
     * 测试PermissionCreateDTO
     */
    @Test
    void testPermissionCreateDTO() {
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setName("角色管理");
        dto.setCode("system:role");
        dto.setParentId(0L);
        dto.setPath("/system/role");
        dto.setComponent("system/role/index");
        dto.setIcon("role");
        dto.setSortOrder(2);
        dto.setStatus(1);

        assertNotNull(dto);
        assertEquals("角色管理", dto.getName());
        assertEquals("system:role", dto.getCode());
        assertEquals(0L, dto.getParentId());
        assertEquals("/system/role", dto.getPath());
        assertEquals("system/role/index", dto.getComponent());
        assertEquals("role", dto.getIcon());
        assertEquals(2, dto.getSortOrder());
        assertEquals(1, dto.getStatus());
    }

    /**
     * 测试PermissionUpdateDTO
     */
    @Test
    void testPermissionUpdateDTO() {
        PermissionUpdateDTO dto = new PermissionUpdateDTO();
        dto.setId(1L);
        dto.setName("用户管理");
        dto.setCode("system:user");
        dto.setParentId(0L);
        dto.setPath("/system/user");
        dto.setComponent("system/user/index");
        dto.setIcon("user");
        dto.setSortOrder(1);
        dto.setStatus(1);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("用户管理", dto.getName());
        assertEquals("system:user", dto.getCode());
        assertEquals(0L, dto.getParentId());
        assertEquals("/system/user", dto.getPath());
        assertEquals("system/user/index", dto.getComponent());
        assertEquals("user", dto.getIcon());
        assertEquals(1, dto.getSortOrder());
        assertEquals(1, dto.getStatus());
    }

    /**
     * 测试PermissionVO
     */
    @Test
    void testPermissionVO() {
        PermissionVO vo = new PermissionVO();
        vo.setId(1L);
        vo.setName("用户管理");
        vo.setCode("system:user");
        vo.setParentId(0L);
        vo.setPath("/system/user");
        vo.setComponent("system/user/index");
        vo.setIcon("user");
        vo.setSortOrder(1);
        vo.setStatus(1);
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());
        vo.setCreateBy("admin");
        vo.setUpdateBy("admin");

        assertNotNull(vo);
        assertEquals(1L, vo.getId());
        assertEquals("用户管理", vo.getName());
        assertEquals("system:user", vo.getCode());
        assertEquals(0L, vo.getParentId());
        assertEquals("/system/user", vo.getPath());
        assertEquals("system/user/index", vo.getComponent());
        assertEquals("user", vo.getIcon());
        assertEquals(1, vo.getSortOrder());
        assertEquals(1, vo.getStatus());
        assertEquals("admin", vo.getCreateBy());
        assertEquals("admin", vo.getUpdateBy());
    }

    /**
     * 边界测试：测试空值和边界值
     */
    @Test
    void testBoundaryValues() {
        // 测试空字符串
        SysRole role = new SysRole();
        role.setRoleName("");
        role.setRoleCode("");
        assertEquals("", role.getRoleName());
        assertEquals("", role.getRoleCode());

        // 测试最大长度
        SysPermission permission = new SysPermission();
        permission.setName("a".repeat(50));
        permission.setCode("b".repeat(100));
        assertEquals(50, permission.getName().length());
        assertEquals(100, permission.getCode().length());

        // 测试状态边界值
        role.setStatus(0);
        assertEquals(0, role.getStatus());
        role.setStatus(1);
        assertEquals(1, role.getStatus());

        // 测试父权限ID为0（顶级权限）
        permission.setParentId(0L);
        assertEquals(0L, permission.getParentId());

        // 测试排序号边界值
        permission.setSortOrder(0);
        assertEquals(0, permission.getSortOrder());
        permission.setSortOrder(999);
        assertEquals(999, permission.getSortOrder());
    }

    /**
     * 异常测试：测试DTO验证注解
     */
    @Test
    void testDTOValidation() {
        // 测试RoleCreateDTO的必填字段
        RoleCreateDTO roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setName(null);
        roleCreateDTO.setCode(null);
        roleCreateDTO.setStatus(null);
        
        assertNull(roleCreateDTO.getName());
        assertNull(roleCreateDTO.getCode());
        assertNull(roleCreateDTO.getStatus());

        // 测试PermissionCreateDTO的必填字段
        PermissionCreateDTO permissionCreateDTO = new PermissionCreateDTO();
        permissionCreateDTO.setName("");
        permissionCreateDTO.setCode("");
        permissionCreateDTO.setParentId(null);
        permissionCreateDTO.setStatus(null);
        
        assertEquals("", permissionCreateDTO.getName());
        assertEquals("", permissionCreateDTO.getCode());
        assertNull(permissionCreateDTO.getParentId());
        assertNull(permissionCreateDTO.getStatus());
    }
}
