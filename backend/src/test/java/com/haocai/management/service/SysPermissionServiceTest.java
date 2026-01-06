package com.haocai.management.service;

import com.haocai.management.dto.BatchUpdateResult;
import com.haocai.management.dto.PermissionCreateDTO;
import com.haocai.management.dto.PermissionUpdateDTO;
import com.haocai.management.dto.PermissionVO;
import com.haocai.management.entity.SysPermission;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 权限服务层测试类
 * 
 * 测试范围：
 * 1. 权限CRUD操作
 * 2. 权限树形结构查询
 * 3. 批量操作
 * 4. 边界条件和异常场景
 * 
 * 遵循：测试规范-第1条（测试类命名规范）
 * 遵循：测试规范-第2条（测试方法命名规范）
 */
@SpringBootTest
@Transactional
public class SysPermissionServiceTest {

    @Autowired
    private ISysPermissionService permissionService;

    /**
     * 测试创建权限
     */
    @Test
    public void testCreatePermission() {
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setCode("user:create");
        dto.setName("创建用户");
        dto.setType("button");
        dto.setPath("/api/user/create");
        dto.setStatus(1);

        Long permissionId = permissionService.createPermission(dto, 1L);
        
        assertNotNull(permissionId, "权限ID不应为空");
        
        SysPermission permission = permissionService.getById(permissionId);
        assertNotNull(permission, "权限应存在");
        assertEquals("user:create", permission.getCode(), "权限代码应匹配");
        assertEquals("创建用户", permission.getName(), "权限名称应匹配");
        assertEquals("button", permission.getType(), "权限类型应匹配");
    }

    /**
     * 测试创建子权限
     */
    @Test
    public void testCreateChildPermission() {
        // 先创建父权限
        PermissionCreateDTO parentDto = new PermissionCreateDTO();
        parentDto.setCode("user");
        parentDto.setName("用户管理");
        parentDto.setType("menu");
        parentDto.setStatus(1);
        Long parentId = permissionService.createPermission(parentDto, 1L);

        // 创建子权限
        PermissionCreateDTO childDto = new PermissionCreateDTO();
        childDto.setCode("user:create");
        childDto.setName("创建用户");
        childDto.setType("button");
        childDto.setParentId(parentId);
        childDto.setPath("/api/user/create");
        childDto.setStatus(1);
        Long childId = permissionService.createPermission(childDto, 1L);

        assertNotNull(childId, "子权限ID不应为空");
        
        SysPermission childPermission = permissionService.getById(childId);
        assertEquals(parentId, childPermission.getParentId(), "父权限ID应匹配");
    }

    /**
     * 测试创建重复权限代码
     */
    @Test
    public void testCreatePermissionWithDuplicateCode() {
        // 先创建一个权限
        PermissionCreateDTO dto1 = new PermissionCreateDTO();
        dto1.setCode("DUPLICATE_PERM");
        dto1.setName("权限1");
        dto1.setType("button");
        dto1.setStatus(1);
        permissionService.createPermission(dto1, 1L);

        // 尝试创建相同代码的权限
        PermissionCreateDTO dto2 = new PermissionCreateDTO();
        dto2.setCode("DUPLICATE_PERM");
        dto2.setName("权限2");
        dto2.setType("button");
        dto2.setStatus(1);

        assertThrows(RuntimeException.class, () -> {
            permissionService.createPermission(dto2, 1L);
        }, "应抛出异常");
    }

    /**
     * 测试更新权限
     */
    @Test
    public void testUpdatePermission() {
        // 先创建权限
        PermissionCreateDTO createDto = new PermissionCreateDTO();
        createDto.setCode("UPDATE_PERM");
        createDto.setName("原始名称");
        createDto.setType("button");
        createDto.setPath("/api/old");
        createDto.setStatus(1);
        Long permissionId = permissionService.createPermission(createDto, 1L);

        // 更新权限
        PermissionUpdateDTO updateDto = new PermissionUpdateDTO();
        updateDto.setId(permissionId);
        updateDto.setName("更新后的名称");
        updateDto.setCode("UPDATE_PERM");
        updateDto.setPath("/api/new");
        updateDto.setStatus(1);

        permissionService.updatePermission(permissionId, updateDto, 1L);

        // 验证更新
        SysPermission permission = permissionService.getById(permissionId);
        assertEquals("更新后的名称", permission.getName(), "名称应已更新");
        assertEquals("/api/new", permission.getPath(), "路径应已更新");
        assertEquals("UPDATE_PERM", permission.getCode(), "代码应保持不变");
    }

    /**
     * 测试删除权限
     */
    @Test
    public void testDeletePermission() {
        // 先创建权限
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setCode("DELETE_PERM");
        dto.setName("待删除权限");
        dto.setType("button");
        dto.setStatus(1);
        Long permissionId = permissionService.createPermission(dto, 1L);

        // 删除权限
        permissionService.deletePermission(permissionId);

        // 验证删除（逻辑删除）
        SysPermission permission = permissionService.getById(permissionId);
        assertNull(permission, "权限应已被删除");
    }

    /**
     * 测试批量删除权限
     */
    @Test
    public void testBatchDeletePermissions() {
        // 创建多个权限
        PermissionCreateDTO dto1 = new PermissionCreateDTO();
        dto1.setCode("BATCH_DELETE_PERM_1");
        dto1.setName("批量删除权限1");
        dto1.setType("button");
        dto1.setStatus(1);
        Long permissionId1 = permissionService.createPermission(dto1, 1L);

        PermissionCreateDTO dto2 = new PermissionCreateDTO();
        dto2.setCode("BATCH_DELETE_PERM_2");
        dto2.setName("批量删除权限2");
        dto2.setType("button");
        dto2.setStatus(1);
        Long permissionId2 = permissionService.createPermission(dto2, 1L);

        // 批量删除
        List<Long> permissionIds = Arrays.asList(permissionId1, permissionId2);
        BatchUpdateResult result = permissionService.batchDeletePermissions(permissionIds);

        assertEquals(2, result.getSuccessCount(), "成功数量应为2");
        assertEquals(0, result.getFailureCount(), "失败数量应为0");

        // 验证删除
        SysPermission permission1 = permissionService.getById(permissionId1);
        SysPermission permission2 = permissionService.getById(permissionId2);
        assertNull(permission1, "权限1应已被删除");
        assertNull(permission2, "权限2应已被删除");
    }

    /**
     * 测试获取权限树
     */
    @Test
    public void testGetPermissionTree() {
        // 创建父权限
        PermissionCreateDTO parentDto = new PermissionCreateDTO();
        parentDto.setCode("user");
        parentDto.setName("用户管理");
        parentDto.setType("menu");
        parentDto.setStatus(1);
        Long parentId = permissionService.createPermission(parentDto, 1L);

        // 创建子权限
        PermissionCreateDTO childDto1 = new PermissionCreateDTO();
        childDto1.setCode("user:create");
        childDto1.setName("创建用户");
        childDto1.setType("button");
        childDto1.setParentId(parentId);
        childDto1.setPath("/api/user/create");
        childDto1.setStatus(1);
        permissionService.createPermission(childDto1, 1L);

        PermissionCreateDTO childDto2 = new PermissionCreateDTO();
        childDto2.setCode("user:update");
        childDto2.setName("更新用户");
        childDto2.setType("button");
        childDto2.setParentId(parentId);
        childDto2.setPath("/api/user/update");
        childDto2.setStatus(1);
        permissionService.createPermission(childDto2, 1L);

        // 获取权限树
        List<PermissionVO> tree = permissionService.getPermissionTree();
        
        assertNotNull(tree, "权限树不应为空");
        assertTrue(tree.size() > 0, "权限树应包含数据");
    }

    /**
     * 测试根据用户ID获取权限列表
     */
    @Test
    public void testGetPermissionsByUserId() {
        // 创建权限
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setCode("USER_PERM");
        dto.setName("用户权限");
        dto.setType("api");
        dto.setPath("/api/user");
        dto.setStatus(1);
        Long permissionId = permissionService.createPermission(dto, 1L);

        // 假设用户ID为1，分配权限
        // 注意：这里需要先确保用户和角色存在，实际测试中可能需要先创建用户和角色
        // List<PermissionVO> permissions = permissionService.getPermissionsByUserId(1L);
        // assertNotNull(permissions, "权限列表不应为空");
    }

    /**
     * 测试根据角色ID获取权限列表
     */
    @Test
    public void testGetPermissionsByRoleId() {
        // 创建权限
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setCode("ROLE_PERM");
        dto.setName("角色权限");
        dto.setType("api");
        dto.setPath("/api/role");
        dto.setStatus(1);
        Long permissionId = permissionService.createPermission(dto, 1L);

        // 假设角色ID为1，分配权限
        // 注意：这里需要先确保角色存在，实际测试中可能需要先创建角色
        // List<PermissionVO> permissions = permissionService.getPermissionsByRoleId(1L);
        // assertNotNull(permissions, "权限列表不应为空");
    }

    /**
     * 测试空值输入
     */
    @Test
    public void testNullInput() {
        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            permissionService.createPermission(null, 1L);
        }, "创建权限时DTO为空应抛出BusinessException");

        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            permissionService.updatePermission(1L, null, 1L);
        }, "更新权限时DTO为空应抛出BusinessException");
    }

    /**
     * 测试边界条件：空列表
     */
    @Test
    public void testEmptyList() {
        // 测试空权限ID列表应抛出异常
        assertThrows(com.haocai.management.exception.BusinessException.class, () -> {
            permissionService.batchDeletePermissions(Arrays.asList());
        }, "批量删除时权限ID列表为空应抛出BusinessException");
    }

    /**
     * 测试权限类型
     */
    @Test
    public void testPermissionTypes() {
        // 测试菜单类型
        PermissionCreateDTO menuDto = new PermissionCreateDTO();
        menuDto.setCode("menu");
        menuDto.setName("菜单");
        menuDto.setType("menu");
        menuDto.setStatus(1);
        Long menuId = permissionService.createPermission(menuDto, 1L);

        SysPermission menu = permissionService.getById(menuId);
        assertEquals("menu", menu.getType(), "菜单类型应为menu");

        // 测试按钮类型
        PermissionCreateDTO buttonDto = new PermissionCreateDTO();
        buttonDto.setCode("button");
        buttonDto.setName("按钮");
        buttonDto.setType("button");
        buttonDto.setStatus(1);
        Long buttonId = permissionService.createPermission(buttonDto, 1L);

        SysPermission button = permissionService.getById(buttonId);
        assertEquals("button", button.getType(), "按钮类型应为button");
    }
}
