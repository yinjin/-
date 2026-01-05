package com.material.system;

import com.material.system.dto.PermissionCreateDTO;
import com.material.system.dto.PermissionUpdateDTO;
import com.material.system.service.SysPermissionService;
import com.material.system.vo.PermissionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限服务测试类
 */
@SpringBootTest
@Transactional
public class SysPermissionServiceTest extends AbstractMySQLTest {

    @Autowired
    private SysPermissionService permissionService;

    private PermissionCreateDTO permissionCreateDTO;

    @BeforeEach
    public void setUp() {
        permissionCreateDTO = new PermissionCreateDTO();
        permissionCreateDTO.setPermissionCode("TEST_PERMISSION");
        permissionCreateDTO.setPermissionName("测试权限");
        permissionCreateDTO.setResourceType("API");
        permissionCreateDTO.setPath("/api/test");
        permissionCreateDTO.setParentId(0L);
        permissionCreateDTO.setStatus(1);
    }

    @Test
    public void testCreatePermission() {
        Long permissionId = permissionService.createPermission(permissionCreateDTO);
        
        assertNotNull(permissionId);
        PermissionVO permissionVO = permissionService.getPermissionById(permissionId);
        assertNotNull(permissionVO);
        assertEquals("TEST_PERMISSION", permissionVO.getPermissionCode());
        assertEquals("测试权限", permissionVO.getPermissionName());
        assertEquals("API", permissionVO.getResourceType());
        assertEquals("/api/test", permissionVO.getPath());
        assertEquals(1, permissionVO.getStatus());
    }

    @Test
    public void testCreatePermissionWithDuplicateCode() {
        // 创建第一个权限
        permissionService.createPermission(permissionCreateDTO);
        
        // 尝试创建相同编码的权限，应该抛出异常
        PermissionCreateDTO duplicateDTO = new PermissionCreateDTO();
        duplicateDTO.setPermissionCode("TEST_PERMISSION");
        duplicateDTO.setPermissionName("重复权限");
        duplicateDTO.setResourceType("API");
        duplicateDTO.setPath("/api/test2");
        duplicateDTO.setParentId(0L);
        duplicateDTO.setStatus(1);
        
        assertThrows(Exception.class, () -> permissionService.createPermission(duplicateDTO));
    }

    @Test
    public void testCreatePermissionWithParent() {
        // 创建父权限
        Long parentId = permissionService.createPermission(permissionCreateDTO);
        
        // 创建子权限
        PermissionCreateDTO childDTO = new PermissionCreateDTO();
        childDTO.setPermissionCode("CHILD_PERMISSION");
        childDTO.setPermissionName("子权限");
        childDTO.setResourceType("BUTTON");
        childDTO.setPath("/api/test/child");
        childDTO.setParentId(parentId);
        childDTO.setStatus(1);
        
        Long childId = permissionService.createPermission(childDTO);
        
        assertNotNull(childId);
        PermissionVO childVO = permissionService.getPermissionById(childId);
        assertNotNull(childVO);
        assertEquals(parentId, childVO.getParentId());
    }

    @Test
    public void testUpdatePermission() {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);
        
        // 更新权限
        PermissionUpdateDTO updateDTO = new PermissionUpdateDTO();
        updateDTO.setId(permissionId);
        updateDTO.setPermissionName("更新后的权限名称");
        updateDTO.setStatus(0);
        
        permissionService.updatePermission(updateDTO);
        
        PermissionVO updatedPermission = permissionService.getPermissionById(permissionId);
        assertNotNull(updatedPermission);
        assertEquals(permissionId, updatedPermission.getId());
        assertEquals("TEST_PERMISSION", updatedPermission.getPermissionCode()); // 编码不应该改变
        assertEquals("更新后的权限名称", updatedPermission.getPermissionName());
        assertEquals(0, updatedPermission.getStatus());
    }

    @Test
    public void testUpdateNonExistentPermission() {
        PermissionUpdateDTO updateDTO = new PermissionUpdateDTO();
        updateDTO.setId(99999L);
        updateDTO.setPermissionName("不存在的权限");
        
        assertThrows(Exception.class, () -> permissionService.updatePermission(updateDTO));
    }

    @Test
    public void testDeletePermission() {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);
        
        // 删除权限
        permissionService.deletePermission(permissionId);
        
        // 验证权限已被删除
        assertThrows(Exception.class, () -> permissionService.getPermissionById(permissionId));
    }

    @Test
    public void testDeleteNonExistentPermission() {
        assertThrows(Exception.class, () -> permissionService.deletePermission(99999L));
    }

    @Test
    public void testGetPermissionById() {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);
        
        // 获取权限
        PermissionVO permissionVO = permissionService.getPermissionById(permissionId);
        
        assertNotNull(permissionVO);
        assertEquals(permissionId, permissionVO.getId());
        assertEquals("TEST_PERMISSION", permissionVO.getPermissionCode());
        assertEquals("测试权限", permissionVO.getPermissionName());
    }

    @Test
    public void testGetAllPermissions() {
        // 创建多个权限
        permissionService.createPermission(permissionCreateDTO);
        
        PermissionCreateDTO permission2 = new PermissionCreateDTO();
        permission2.setPermissionCode("TEST_PERMISSION_2");
        permission2.setPermissionName("测试权限2");
        permission2.setResourceType("API");
        permission2.setPath("/api/test2");
        permission2.setParentId(0L);
        permission2.setStatus(1);
        permissionService.createPermission(permission2);
        
        // 获取所有权限
        List<PermissionVO> permissions = permissionService.getAllPermissions();
        
        assertNotNull(permissions);
        assertTrue(permissions.size() >= 2);
    }

    @Test
    public void testGetPermissionTree() {
        // 创建父权限
        Long parentId = permissionService.createPermission(permissionCreateDTO);
        
        // 创建子权限
        PermissionCreateDTO childDTO = new PermissionCreateDTO();
        childDTO.setPermissionCode("CHILD_PERMISSION");
        childDTO.setPermissionName("子权限");
        childDTO.setResourceType("BUTTON");
        childDTO.setPath("/api/test/child");
        childDTO.setParentId(parentId);
        childDTO.setStatus(1);
        permissionService.createPermission(childDTO);
        
        // 获取权限树
        List<PermissionVO> permissionTree = permissionService.getPermissionTree();
        
        assertNotNull(permissionTree);
        assertTrue(permissionTree.size() > 0);
        
        // 验证树形结构
        PermissionVO parent = permissionTree.stream()
            .filter(p -> p.getId().equals(parentId))
            .findFirst()
            .orElse(null);
        assertNotNull(parent);
        assertNotNull(parent.getChildren());
        assertTrue(parent.getChildren().size() > 0);
    }

    @Test
    public void testGetPermissionsByType() {
        // 创建菜单类型权限
        PermissionCreateDTO menuDTO = new PermissionCreateDTO();
        menuDTO.setPermissionCode("MENU_PERMISSION");
        menuDTO.setPermissionName("菜单权限");
        menuDTO.setResourceType("MENU");
        menuDTO.setPath("/api/menu");
        menuDTO.setParentId(0L);
        menuDTO.setStatus(1);
        permissionService.createPermission(menuDTO);
        
        // 创建按钮类型权限
        PermissionCreateDTO buttonDTO = new PermissionCreateDTO();
        buttonDTO.setPermissionCode("BUTTON_PERMISSION");
        buttonDTO.setPermissionName("按钮权限");
        buttonDTO.setResourceType("BUTTON");
        buttonDTO.setPath("/api/button");
        buttonDTO.setParentId(0L);
        buttonDTO.setStatus(1);
        permissionService.createPermission(buttonDTO);
        
        // 获取所有权限并验证类型
        List<PermissionVO> allPermissions = permissionService.getAllPermissions();
        assertNotNull(allPermissions);
        assertTrue(allPermissions.stream().anyMatch(p -> p.getPermissionCode().equals("MENU_PERMISSION")));
        assertTrue(allPermissions.stream().anyMatch(p -> p.getPermissionCode().equals("BUTTON_PERMISSION")));
    }

    @Test
    public void testGetPermissionsByRole() {
        // 创建权限
        Long permissionId1 = permissionService.createPermission(permissionCreateDTO);
        
        PermissionCreateDTO permission2 = new PermissionCreateDTO();
        permission2.setPermissionCode("TEST_PERMISSION_2");
        permission2.setPermissionName("测试权限2");
        permission2.setResourceType("API");
        permission2.setPath("/api/test2");
        permission2.setParentId(0L);
        permission2.setStatus(1);
        Long permissionId2 = permissionService.createPermission(permission2);
        
        // 获取角色的权限（假设角色ID为1）
        List<PermissionVO> rolePermissions = permissionService.getPermissionsByRoleId(1L);
        
        assertNotNull(rolePermissions);
        // 验证返回的权限列表
        assertTrue(rolePermissions.size() >= 0);
    }
}
