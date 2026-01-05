package com.material.system;

import com.material.system.dto.RoleCreateDTO;
import com.material.system.dto.RoleUpdateDTO;
import com.material.system.service.SysRoleService;
import com.material.system.vo.RoleVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色服务测试类
 */
@SpringBootTest
@Transactional
public class SysRoleServiceTest extends AbstractMySQLTest {

    @Autowired
    private SysRoleService roleService;

    private RoleCreateDTO roleCreateDTO;

    @BeforeEach
    public void setUp() {
        roleCreateDTO = new RoleCreateDTO();
        roleCreateDTO.setRoleCode("TEST_ROLE");
        roleCreateDTO.setRoleName("测试角色");
        roleCreateDTO.setDescription("这是一个测试角色");
        roleCreateDTO.setStatus(1);
    }

    @Test
    public void testCreateRole() {
        Long roleId = roleService.createRole(roleCreateDTO);
        
        assertNotNull(roleId);
        RoleVO roleVO = roleService.getRoleById(roleId);
        assertNotNull(roleVO);
        assertEquals("TEST_ROLE", roleVO.getRoleCode());
        assertEquals("测试角色", roleVO.getRoleName());
        assertEquals("这是一个测试角色", roleVO.getDescription());
        assertEquals(1, roleVO.getStatus());
    }

    @Test
    public void testCreateRoleWithDuplicateCode() {
        // 创建第一个角色
        roleService.createRole(roleCreateDTO);
        
        // 尝试创建相同编码的角色，应该抛出异常
        RoleCreateDTO duplicateDTO = new RoleCreateDTO();
        duplicateDTO.setRoleCode("TEST_ROLE");
        duplicateDTO.setRoleName("重复角色");
        duplicateDTO.setDescription("重复的角色编码");
        duplicateDTO.setStatus(1);
        
        assertThrows(Exception.class, () -> roleService.createRole(duplicateDTO));
    }

    @Test
    public void testUpdateRole() {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);
        
        // 更新角色
        RoleUpdateDTO updateDTO = new RoleUpdateDTO();
        updateDTO.setId(roleId);
        updateDTO.setRoleName("更新后的角色名称");
        updateDTO.setDescription("更新后的描述");
        updateDTO.setStatus(0);
        
        roleService.updateRole(updateDTO);
        
        RoleVO updatedRole = roleService.getRoleById(roleId);
        assertNotNull(updatedRole);
        assertEquals(roleId, updatedRole.getId());
        assertEquals("TEST_ROLE", updatedRole.getRoleCode()); // 编码不应该改变
        assertEquals("更新后的角色名称", updatedRole.getRoleName());
        assertEquals("更新后的描述", updatedRole.getDescription());
        assertEquals(0, updatedRole.getStatus());
    }

    @Test
    public void testUpdateNonExistentRole() {
        RoleUpdateDTO updateDTO = new RoleUpdateDTO();
        updateDTO.setId(99999L);
        updateDTO.setRoleName("不存在的角色");
        
        assertThrows(Exception.class, () -> roleService.updateRole(updateDTO));
    }

    @Test
    public void testDeleteRole() {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);
        
        // 删除角色
        roleService.deleteRole(roleId);
        
        // 验证角色已被删除
        assertThrows(Exception.class, () -> roleService.getRoleById(roleId));
    }

    @Test
    public void testDeleteNonExistentRole() {
        assertThrows(Exception.class, () -> roleService.deleteRole(99999L));
    }

    @Test
    public void testGetRoleById() {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);
        
        // 获取角色
        RoleVO roleVO = roleService.getRoleById(roleId);
        
        assertNotNull(roleVO);
        assertEquals(roleId, roleVO.getId());
        assertEquals("TEST_ROLE", roleVO.getRoleCode());
        assertEquals("测试角色", roleVO.getRoleName());
    }


    @Test
    public void testGetAllRoles() {
        // 创建多个角色
        roleService.createRole(roleCreateDTO);
        
        RoleCreateDTO role2 = new RoleCreateDTO();
        role2.setRoleCode("TEST_ROLE_2");
        role2.setRoleName("测试角色2");
        role2.setDescription("第二个测试角色");
        role2.setStatus(1);
        roleService.createRole(role2);
        
        // 获取所有角色
        List<RoleVO> roles = roleService.getAllRoles();
        
        assertNotNull(roles);
        assertTrue(roles.size() >= 2);
    }

    @Test
    public void testAssignPermissionsToRole() {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);
        
        // 分配权限
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        roleService.assignPermissions(roleId, permissionIds);
        
        // 验证权限已分配
        List<Long> assignedPermissions = roleService.getRolePermissionIds(roleId);
        assertEquals(3, assignedPermissions.size());
        assertTrue(assignedPermissions.containsAll(permissionIds));
    }

    @Test
    public void testAssignPermissionsToNonExistentRole() {
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        assertThrows(Exception.class, () -> roleService.assignPermissions(99999L, permissionIds));
    }

    @Test
    public void testGetRolePermissions() {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);
        
        // 分配权限
        List<Long> permissionIds = Arrays.asList(1L, 2L, 3L);
        roleService.assignPermissions(roleId, permissionIds);
        
        // 获取角色权限
        List<Long> assignedPermissions = roleService.getRolePermissionIds(roleId);
        
        assertNotNull(assignedPermissions);
        assertEquals(3, assignedPermissions.size());
        assertTrue(assignedPermissions.containsAll(permissionIds));
    }

    @Test
    public void testGetRolePermissionsForNonExistentRole() {
        assertThrows(Exception.class, () -> roleService.getRolePermissionIds(99999L));
    }
}
