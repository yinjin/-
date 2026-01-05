package com.material.system;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.material.system.dto.RoleCreateDTO;
import com.material.system.dto.RoleUpdateDTO;
import com.material.system.service.SysRoleService;
import com.material.system.vo.RoleVO;
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
 * 角色控制器测试类
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class SysRoleControllerTest extends AbstractMySQLTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
    @WithMockUser(username = "admin", authorities = {"system:role:create"})
    public void testCreateRole() throws Exception {
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:create"})
    public void testCreateRoleWithDuplicateCode() throws Exception {
        // 创建第一个角色
        roleService.createRole(roleCreateDTO);

        // 尝试创建相同编码的角色
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleCreateDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(not(200)));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:update"})
    public void testUpdateRole() throws Exception {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);

        // 更新角色
        RoleUpdateDTO updateDTO = new RoleUpdateDTO();
        updateDTO.setId(roleId);
        updateDTO.setRoleName("更新后的角色名称");
        updateDTO.setDescription("更新后的描述");
        updateDTO.setStatus(0);

        mockMvc.perform(put("/api/roles/{id}", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:update"})
    public void testUpdateNonExistentRole() throws Exception {
        RoleUpdateDTO updateDTO = new RoleUpdateDTO();
        updateDTO.setId(99999L);
        updateDTO.setRoleName("不存在的角色");

        mockMvc.perform(put("/api/roles/{id}", 99999L)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:delete"})
    public void testDeleteRole() throws Exception {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);

        // 删除角色
        mockMvc.perform(delete("/api/roles/{id}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:delete"})
    public void testDeleteNonExistentRole() throws Exception {
        mockMvc.perform(delete("/api/roles/{id}", 99999L))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:query"})
    public void testGetRoleById() throws Exception {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);

        // 获取角色
        mockMvc.perform(get("/api/roles/{id}", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(roleId))
                .andExpect(jsonPath("$.data.roleCode").value("TEST_ROLE"))
                .andExpect(jsonPath("$.data.roleName").value("测试角色"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:query"})
    public void testGetAllRoles() throws Exception {
        // 创建多个角色
        roleService.createRole(roleCreateDTO);

        RoleCreateDTO role2 = new RoleCreateDTO();
        role2.setRoleCode("TEST_ROLE_2");
        role2.setRoleName("测试角色2");
        role2.setDescription("第二个测试角色");
        role2.setStatus(1);
        roleService.createRole(role2);

        // 获取所有角色
        mockMvc.perform(get("/api/roles/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:query"})
    public void testGetRolePage() throws Exception {
        // 创建多个角色
        roleService.createRole(roleCreateDTO);

        RoleCreateDTO role2 = new RoleCreateDTO();
        role2.setRoleCode("TEST_ROLE_2");
        role2.setRoleName("测试角色2");
        role2.setDescription("第二个测试角色");
        role2.setStatus(1);
        roleService.createRole(role2);

        // 分页查询
        mockMvc.perform(get("/api/roles/page")
                .param("current", "1")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.records", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:assign"})
    public void testAssignPermissionsToRole() throws Exception {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);

        // 分配权限
        mockMvc.perform(post("/api/roles/{id}/permissions", roleId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("[1, 2, 3]"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"));
    }

    @Test
    @WithMockUser(username = "admin", authorities = {"system:role:query"})
    public void testGetRolePermissions() throws Exception {
        // 创建角色
        Long roleId = roleService.createRole(roleCreateDTO);

        // 获取角色权限
        mockMvc.perform(get("/api/roles/{id}/permissions", roleId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @WithMockUser(username = "user", authorities = {"no-permission"})
    public void testUnauthorizedAccess() throws Exception {
        // 普通用户尝试创建角色，应该被拒绝
        mockMvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(roleCreateDTO)))
                .andExpect(status().isForbidden());
    }
}
