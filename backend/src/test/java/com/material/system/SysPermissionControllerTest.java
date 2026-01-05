package com.material.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.material.system.dto.PermissionCreateDTO;
import com.material.system.dto.PermissionUpdateDTO;
import com.material.system.service.SysPermissionService;
import com.material.system.vo.PermissionVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 权限控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SysPermissionControllerTest extends AbstractMySQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(username = "admin", authorities = {"system:permission:create"})
    public void testCreatePermission() throws Exception {
        mockMvc.perform(post("/api/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:create"})
    public void testCreatePermissionWithDuplicateCode() throws Exception {
        // 创建第一个权限
        permissionService.createPermission(permissionCreateDTO);

        // 尝试创建相同编码的权限
        mockMvc.perform(post("/api/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(not(200)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:update"})
    public void testUpdatePermission() throws Exception {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);

        // 更新权限
        PermissionUpdateDTO updateDTO = new PermissionUpdateDTO();
        updateDTO.setId(permissionId);
        updateDTO.setPermissionName("更新后的权限名称");
        updateDTO.setPermissionCode("TEST_PERMISSION_UPDATED");
        updateDTO.setResourceType("API");
        updateDTO.setPath("/api/test/updated");
        updateDTO.setParentId(0L);
        updateDTO.setStatus(0);

        mockMvc.perform(put("/api/permissions/{id}", permissionId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:update"})
    public void testUpdateNonExistentPermission() throws Exception {
        PermissionUpdateDTO updateDTO = new PermissionUpdateDTO();
        updateDTO.setId(99999L);
        updateDTO.setPermissionName("不存在的权限");
        updateDTO.setPermissionCode("NON_EXISTENT_PERMISSION");
        updateDTO.setResourceType("API");
        updateDTO.setPath("/api/non-existent");
        updateDTO.setParentId(0L);
        updateDTO.setStatus(1);

        mockMvc.perform(put("/api/permissions/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:delete"})
    public void testDeletePermission() throws Exception {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);

        // 删除权限
        mockMvc.perform(delete("/api/permissions/{id}", permissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:delete"})
    public void testDeleteNonExistentPermission() throws Exception {
        mockMvc.perform(delete("/api/permissions/{id}", 99999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:query"})
    public void testGetPermissionById() throws Exception {
        // 创建权限
        Long permissionId = permissionService.createPermission(permissionCreateDTO);

        // 获取权限
        mockMvc.perform(get("/api/permissions/{id}", permissionId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(permissionId))
                .andExpect(jsonPath("$.data.permissionCode").value("TEST_PERMISSION"))
                .andExpect(jsonPath("$.data.permissionName").value("测试权限"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:query"})
    public void testGetAllPermissions() throws Exception {
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
        mockMvc.perform(get("/api/permissions/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:permission:query"})
    public void testGetPermissionTree() throws Exception {
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
        mockMvc.perform(get("/api/permissions/tree"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"no-permission"})
    public void testUnauthorizedAccess() throws Exception {
        // 普通用户尝试创建权限，应该被拒绝
        mockMvc.perform(post("/api/permissions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(permissionCreateDTO)))
                .andExpect(status().isForbidden());
    }
}
