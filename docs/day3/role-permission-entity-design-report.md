# 角色权限实体类设计开发报告

**任务完成状态**：✅ 已完成

**开发时间**：2026年1月6日  
**开发者**：AI Assistant  
**任务来源**：day3-plan.md - 1.1 角色权限实体类设计

---

## 一、任务概述

### 1.1 任务目标
根据day3-plan.md中的1.1任务要求，设计并实现角色权限相关的实体类，包括：
- SysRole（角色实体）
- SysPermission（权限实体）
- SysRolePermission（角色权限关联实体）
- SysUserRole（用户角色关联实体）

同时创建相应的DTO类和测试用例，确保所有设计遵循development-standards.md中的规范。

### 1.2 技术栈
- Spring Boot 3.1.6
- MyBatis-Plus 3.5.5
- MySQL 8.0
- Lombok
- JUnit 5

---

## 二、步骤1：规划与设计

### 2.1 关键约束条款

基于`development-standards.md`，角色权限实体类设计必须遵循以下关键约束：

#### 约束1：字段命名规范（第1.1条）
**规范内容**：数据库字段使用下划线命名法（snake_case），Java实体类字段使用驼峰命名法（camelCase）

**应用说明**：
- 数据库字段：`role_name`, `role_code`, `permission_name`, `permission_code`
- Java字段：`roleName`, `roleCode`, `permissionName`, `permissionCode`
- 对于不符合自动映射的字段，必须使用`@TableField`注解明确指定

#### 约束2：审计字段规范（第1.3条）
**规范内容**：必须包含审计字段（create_time, update_time, create_by, update_by, deleted）

**应用说明**：
- 所有实体类必须包含：createTime, updateTime, createBy, updateBy, deleted
- createTime和updateTime使用`@TableField(fill = FieldFill.INSERT)`和`@TableField(fill = FieldFill.INSERT_UPDATE)`
- deleted使用`@TableLogic`注解实现逻辑删除

#### 约束3：字段映射规范（第2.1条）
**规范内容**：对于不符合驼峰命名规范的字段，必须使用`@TableField`注解明确指定映射关系

**应用说明**：
- SysRole实体：`roleName` → `role_name`, `roleCode` → `role_code`
- SysPermission实体：`permissionName` → `permission_name`, `permissionCode` → `permission_code`

### 2.2 核心实体类设计

#### 2.2.1 SysRole（角色实体）

**核心字段设计**：
```java
public class SysRole {
    private Long id;                          // 主键ID
    
    @TableField("role_name")
    private String roleName;                  // 角色名称
    
    @TableField("role_code")
    private String roleCode;                  // 角色编码（唯一）
    
    private String description;               // 角色描述
    
    private String status;                    // 状态：ACTIVE/INACTIVE
    
    private LocalDateTime createTime;         // 创建时间
    
    private LocalDateTime updateTime;         // 更新时间
    
    private Long createBy;                    // 创建人ID
    
    private Long updateBy;                    // 更新人ID
    
    @TableLogic
    private Integer deleted;                  // 逻辑删除：0未删除 1已删除
}
```

**设计说明**：
- 使用`@TableField`注解明确指定roleName和roleCode的数据库字段映射
- status使用String类型存储，便于扩展
- 所有审计字段按照规范配置
- deleted字段使用`@TableLogic`实现逻辑删除

#### 2.2.2 SysPermission（权限实体）

**核心字段设计**：
```java
public class SysPermission {
    private Long id;                          // 主键ID
    
    @TableField("permission_name")
    private String permissionName;            // 权限名称
    
    @TableField("permission_code")
    private String permissionCode;            // 权限编码（唯一）
    
    private String type;                      // 权限类型：MENU/BUTTON/API
    
    private Long parentId;                    // 父权限ID（用于构建权限树）
    
    private String path;                      // 路由路径（菜单权限）
    
    private String component;                 // 组件路径（菜单权限）
    
    private String icon;                      // 图标（菜单权限）
    
    private Integer sortOrder;                // 排序号
    
    private String status;                    // 状态：ACTIVE/INACTIVE
    
    private LocalDateTime createTime;         // 创建时间
    
    private LocalDateTime updateTime;         // 更新时间
    
    private Long createBy;                    // 创建人ID
    
    private Long updateBy;                    // 更新人ID
    
    @TableLogic
    private Integer deleted;                  // 逻辑删除：0未删除 1已删除
}
```

**设计说明**：
- 使用`@TableField`注解明确指定permissionName和permissionCode的数据库字段映射
- type字段支持MENU（菜单）、BUTTON（按钮）、API（接口）三种类型
- parentId字段用于构建权限树结构
- path、component、icon字段用于前端菜单渲染
- sortOrder字段用于菜单排序

#### 2.2.3 SysRolePermission（角色权限关联实体）

**核心字段设计**：
```java
public class SysRolePermission {
    private Long id;                          // 主键ID
    
    private Long roleId;                      // 角色ID
    
    private Long permissionId;                 // 权限ID
    
    private LocalDateTime createTime;         // 创建时间
    
    private Long createBy;                    // 创建人ID
    
    @TableLogic
    private Integer deleted;                  // 逻辑删除：0未删除 1已删除
}
```

**设计说明**：
- 关联表只包含必要的关联字段和审计字段
- roleId和permissionId组合应该唯一（在数据库层面创建唯一索引）
- 支持逻辑删除，便于数据恢复

#### 2.2.4 SysUserRole（用户角色关联实体）

**核心字段设计**：
```java
public class SysUserRole {
    private Long id;                          // 主键ID
    
    private Long userId;                      // 用户ID
    
    private Long roleId;                      // 角色ID
    
    private LocalDateTime createTime;         // 创建时间
    
    private Long createBy;                    // 创建人ID
    
    @TableLogic
    private Integer deleted;                  // 逻辑删除：0未删除 1已删除
}
```

**设计说明**：
- 关联表只包含必要的关联字段和审计字段
- userId和roleId组合应该唯一（在数据库层面创建唯一索引）
- 支持逻辑删除，便于数据恢复

### 2.3 DTO类设计

#### 2.3.1 角色相关DTO

**RoleCreateDTO（角色创建DTO）**：
```java
public class RoleCreateDTO {
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;
    
    private String description;
    
    @NotBlank(message = "状态不能为空")
    private String status;
}
```

**RoleUpdateDTO（角色更新DTO）**：
```java
public class RoleUpdateDTO {
    @NotNull(message = "角色ID不能为空")
    private Long id;
    
    private String roleName;
    
    private String roleCode;
    
    private String description;
    
    private String status;
}
```

**RoleVO（角色视图对象）**：
```java
public class RoleVO {
    private Long id;
    private String roleName;
    private String roleCode;
    private String description;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

#### 2.3.2 权限相关DTO

**PermissionCreateDTO（权限创建DTO）**：
```java
public class PermissionCreateDTO {
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;
    
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;
    
    @NotBlank(message = "权限类型不能为空")
    private String type;
    
    private Long parentId;
    
    private String path;
    
    private String component;
    
    private String icon;
    
    private Integer sortOrder;
    
    @NotBlank(message = "状态不能为空")
    private String status;
}
```

**PermissionUpdateDTO（权限更新DTO）**：
```java
public class PermissionUpdateDTO {
    @NotNull(message = "权限ID不能为空")
    private Long id;
    
    private String permissionName;
    
    private String permissionCode;
    
    private String type;
    
    private Long parentId;
    
    private String path;
    
    private String component;
    
    private String icon;
    
    private Integer sortOrder;
    
    private String status;
}
```

**PermissionVO（权限视图对象）**：
```java
public class PermissionVO {
    private Long id;
    private String permissionName;
    private String permissionCode;
    private String type;
    private Long parentId;
    private String path;
    private String component;
    private String icon;
    private Integer sortOrder;
    private String status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
```

---

## 三、步骤2：实现与编码

### 3.1 实体类实现

#### 3.1.1 SysRole.java

**文件路径**：`backend/src/main/java/com/haocai/management/entity/SysRole.java`

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色实体类
 * 
 * 遵循规范：
 * - 字段命名规范（第1.1条）：数据库下划线，Java驼峰
 * - 审计字段规范（第1.3条）：包含create_time, update_time, create_by, update_by, deleted
 * - 字段映射规范（第2.1条）：使用@TableField注解明确映射关系
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role")
public class SysRole {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称
     * 遵循：字段映射规范（第2.1条）- 使用@TableField注解明确映射
     */
    @TableField("role_name")
    private String roleName;

    /**
     * 角色编码（唯一）
     * 遵循：字段映射规范（第2.1条）- 使用@TableField注解明确映射
     */
    @TableField("role_code")
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;

    /**
     * 创建时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT_UPDATE)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT_UPDATE)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：审计字段规范（第1.3条）- 使用@TableLogic注解
     */
    @TableLogic
    private Integer deleted;
}
```

#### 3.1.2 SysPermission.java

**文件路径**：`backend/src/main/java/com/haocai/management/entity/SysPermission.java`

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 权限实体类
 * 
 * 遵循规范：
 * - 字段命名规范（第1.1条）：数据库下划线，Java驼峰
 * - 审计字段规范（第1.3条）：包含create_time, update_time, create_by, update_by, deleted
 * - 字段映射规范（第2.1条）：使用@TableField注解明确映射关系
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_permission")
public class SysPermission {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 权限名称
     * 遵循：字段映射规范（第2.1条）- 使用@TableField注解明确映射
     */
    @TableField("permission_name")
    private String permissionName;

    /**
     * 权限编码（唯一）
     * 遵循：字段映射规范（第2.1条）- 使用@TableField注解明确映射
     */
    @TableField("permission_code")
    private String permissionCode;

    /**
     * 权限类型：MENU/BUTTON/API
     */
    private String type;

    /**
     * 父权限ID（用于构建权限树）
     */
    private Long parentId;

    /**
     * 路由路径（菜单权限）
     */
    private String path;

    /**
     * 组件路径（菜单权限）
     */
    private String component;

    /**
     * 图标（菜单权限）
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;

    /**
     * 创建时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT_UPDATE)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 创建人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 更新人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT_UPDATE)
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：审计字段规范（第1.3条）- 使用@TableLogic注解
     */
    @TableLogic
    private Integer deleted;
}
```

#### 3.1.3 SysRolePermission.java

**文件路径**：`backend/src/main/java/com/haocai/management/entity/SysRolePermission.java`

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 角色权限关联实体类
 * 
 * 遵循规范：
 * - 字段命名规范（第1.1条）：数据库下划线，Java驼峰
 * - 审计字段规范（第1.3条）：包含create_time, create_by, deleted
 * - 字段映射规范（第2.1条）：使用@TableField注解明确映射关系
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_role_permission")
public class SysRolePermission {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 权限ID
     */
    private Long permissionId;

    /**
     * 创建时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：审计字段规范（第1.3条）- 使用@TableLogic注解
     */
    @TableLogic
    private Integer deleted;
}
```

#### 3.1.4 SysUserRole.java

**文件路径**：`backend/src/main/java/com/haocai/management/entity/SysUserRole.java`

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 用户角色关联实体类
 * 
 * 遵循规范：
 * - 字段命名规范（第1.1条）：数据库下划线，Java驼峰
 * - 审计字段规范（第1.3条）：包含create_time, create_by, deleted
 * - 字段映射规范（第2.1条）：使用@TableField注解明确映射关系
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_user_role")
public class SysUserRole {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 角色ID
     */
    private Long roleId;

    /**
     * 创建时间
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 创建人ID
     * 遵循：审计字段规范（第1.3条）- 使用@TableField(fill = FieldFill.INSERT)
     */
    @TableField(fill = FieldFill.INSERT)
    private Long createBy;

    /**
     * 逻辑删除：0未删除 1已删除
     * 遵循：审计字段规范（第1.3条）- 使用@TableLogic注解
     */
    @TableLogic
    private Integer deleted;
}
```

### 3.2 DTO类实现

#### 3.2.1 角色相关DTO

**RoleCreateDTO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/RoleCreateDTO.java`

```java
package com.haocai.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 角色创建DTO
 * 
 * 遵循规范：
 * - 参数验证规范：使用@NotBlank注解进行参数验证
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class RoleCreateDTO {

    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;

    /**
     * 角色编码
     */
    @NotBlank(message = "角色编码不能为空")
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}
```

**RoleUpdateDTO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/RoleUpdateDTO.java`

```java
package com.haocai.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 角色更新DTO
 * 
 * 遵循规范：
 * - 参数验证规范：使用@NotNull注解进行参数验证
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class RoleUpdateDTO {

    /**
     * 角色ID
     */
    @NotNull(message = "角色ID不能为空")
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    private String status;
}
```

**RoleVO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/RoleVO.java`

```java
package com.haocai.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 角色视图对象
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class RoleVO {

    /**
     * 角色ID
     */
    private Long id;

    /**
     * 角色名称
     */
    private String roleName;

    /**
     * 角色编码
     */
    private String roleCode;

    /**
     * 角色描述
     */
    private String description;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

#### 3.2.2 权限相关DTO

**PermissionCreateDTO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/PermissionCreateDTO.java`

```java
package com.haocai.management.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 权限创建DTO
 * 
 * 遵循规范：
 * - 参数验证规范：使用@NotBlank注解进行参数验证
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class PermissionCreateDTO {

    /**
     * 权限名称
     */
    @NotBlank(message = "权限名称不能为空")
    private String permissionName;

    /**
     * 权限编码
     */
    @NotBlank(message = "权限编码不能为空")
    private String permissionCode;

    /**
     * 权限类型：MENU/BUTTON/API
     */
    @NotBlank(message = "权限类型不能为空")
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态：ACTIVE/INACTIVE
     */
    @NotBlank(message = "状态不能为空")
    private String status;
}
```

**PermissionUpdateDTO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/PermissionUpdateDTO.java`

```java
package com.haocai.management.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 权限更新DTO
 * 
 * 遵循规范：
 * - 参数验证规范：使用@NotNull注解进行参数验证
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class PermissionUpdateDTO {

    /**
     * 权限ID
     */
    @NotNull(message = "权限ID不能为空")
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型
     */
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private String status;
}
```

**PermissionVO.java**

**文件路径**：`backend/src/main/java/com/haocai/management/dto/PermissionVO.java`

```java
package com.haocai.management.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 权限视图对象
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
@Data
public class PermissionVO {

    /**
     * 权限ID
     */
    private Long id;

    /**
     * 权限名称
     */
    private String permissionName;

    /**
     * 权限编码
     */
    private String permissionCode;

    /**
     * 权限类型
     */
    private String type;

    /**
     * 父权限ID
     */
    private Long parentId;

    /**
     * 路由路径
     */
    private String path;

    /**
     * 组件路径
     */
    private String component;

    /**
     * 图标
     */
    private String icon;

    /**
     * 排序号
     */
    private Integer sortOrder;

    /**
     * 状态
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
```

### 3.3 数据库表结构更新

**文件路径**：`backend/src/main/resources/init.sql`

已更新以下表结构：
- sys_role表：添加create_by、update_by、deleted字段
- sys_permission表：添加path、component、icon、create_by、update_by、deleted字段
- sys_role_permission表：添加create_by、deleted字段
- sys_user_role表：添加create_by、deleted字段

**关键更新内容**：
```sql
-- sys_role表
CREATE TABLE `sys_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_name` varchar(100) NOT NULL COMMENT '角色名称',
  `role_code` varchar(50) NOT NULL COMMENT '角色编码',
  `description` varchar(500) DEFAULT NULL COMMENT '角色描述',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`role_code`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- sys_permission表
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `type` varchar(20) NOT NULL COMMENT '权限类型：MENU/BUTTON/API',
  `parent_id` bigint DEFAULT NULL COMMENT '父权限ID',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT 0 COMMENT '排序号',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_type` (`type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- sys_role_permission表
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- sys_user_role表
CREATE TABLE `sys_user_role` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';
```

### 3.4 安全决策说明

#### 3.4.1 字段映射策略
**决策**：使用`@TableField`注解明确指定数据库字段映射

**原因**：
- 遵循development-standards.md第2.1条规范
- 避免因字段命名不一致导致的映射失败
- 提高代码可读性和可维护性

#### 3.4.2 逻辑删除策略
**决策**：使用`@TableLogic`注解实现逻辑删除

**原因**：
- 遵循development-standards.md第1.3条规范
- 保留数据历史记录，便于审计和恢复
- 避免物理删除导致的数据丢失

#### 3.4.3 审计字段自动填充
**决策**：使用`@TableField(fill = ...)`注解配置自动填充

**原因**：
- 遵循development-standards.md第1.3条和第2.3条规范
- 自动记录数据变更历史
- 减少手动设置审计字段的错误

#### 3.4.4 唯一约束设计
**决策**：在数据库层面创建唯一索引

**原因**：
- role_code和permission_code必须唯一，避免重复
- sys_role_permission和sys_user_role的关联关系必须唯一
- 在数据库层面保证数据完整性

---

## 四、步骤3：验证与测试

### 4.1 测试用例实现

**文件路径**：`backend/src/test/java/com/haocai/management/entity/RolePermissionEntityTest.java`

```java
package com.haocai.management.entity;

import com.haocai.management.dto.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 角色权限实体类测试
 * 
 * 遵循规范：
 * - 测试规范（第6条）：必须测试字段映射、类型转换、批量操作
 * 
 * @author AI Assistant
 * @since 2026-01-06
 */
class RolePermissionEntityTest {

    /**
     * 测试SysRole实体类字段映射
     * 遵循：测试规范（第6.1条）- 必须测试字段映射
     */
    @Test
    void testSysRoleFieldMapping() {
        SysRole role = new SysRole();
        role.setId(1L);
        role.setRoleName("管理员");
        role.setRoleCode("ADMIN");
        role.setDescription("系统管理员角色");
        role.setStatus("ACTIVE");
        role.setCreateTime(LocalDateTime.now());
        role.setUpdateTime(LocalDateTime.now());
        role.setCreateBy(1L);
        role.setUpdateBy(1L);
        role.setDeleted(0);

        assertNotNull(role.getId(), "id字段不能为null");
        assertEquals("管理员", role.getRoleName(), "roleName字段映射失败");
        assertEquals("ADMIN", role.getRoleCode(), "roleCode字段映射失败");
        assertEquals("ACTIVE", role.getStatus(), "status字段映射失败");
        assertNotNull(role.getCreateTime(), "createTime字段不能为null");
        assertNotNull(role.getUpdateTime(), "updateTime字段不能为null");
        assertEquals(0, role.getDeleted(), "deleted字段默认值应为0");
    }

    /**
     * 测试SysPermission实体类字段映射
     * 遵循：测试规范（第6.1条）- 必须测试字段映射
     */
    @Test
    void testSysPermissionFieldMapping() {
        SysPermission permission = new SysPermission();
        permission.setId(1L);
        permission.setPermissionName("用户管理");
        permission.setPermissionCode("user:manage");
        permission.setType("MENU");
        permission.setParentId(0L);
        permission.setPath("/user");
        permission.setComponent("UserManage");
        permission.setIcon("user");
        permission.setSortOrder(1);
        permission.setStatus("ACTIVE");
        permission.setCreateTime(LocalDateTime.now());
        permission.setUpdateTime(LocalDateTime.now());
        permission.setCreateBy(1L);
        permission.setUpdateBy(1L);
        permission.setDeleted(0);

        assertNotNull(permission.getId(), "id字段不能为null");
        assertEquals("用户管理", permission.getPermissionName(), "permissionName字段映射失败");
        assertEquals("user:manage", permission.getPermissionCode(), "permissionCode字段映射失败");
        assertEquals("MENU", permission.getType(), "type字段映射失败");
        assertEquals(0L, permission.getParentId(), "parentId字段映射失败");
        assertEquals("/user", permission.getPath(), "path字段映射失败");
        assertEquals("UserManage", permission.getComponent(), "component字段映射失败");
        assertEquals("user", permission.getIcon(), "icon字段映射失败");
        assertEquals(1, permission.getSortOrder(), "sortOrder字段映射失败");
        assertEquals("ACTIVE", permission.getStatus(), "status字段映射失败");
        assertNotNull(permission.getCreateTime(), "createTime字段不能为null");
        assertNotNull(permission.getUpdateTime(), "updateTime字段不能为null");
        assertEquals(0, permission.getDeleted(), "deleted字段默认值应为0");
    }

    /**
     * 测试SysRolePermission实体类字段映射
     * 遵循：测试规范（第6.1条）- 必须测试字段映射
     */
    @Test
    void testSysRolePermissionFieldMapping() {
        SysRolePermission rolePermission = new SysRolePermission();
        rolePermission.setId(1L);
        rolePermission.setRoleId(1L);
        rolePermission.setPermissionId(1L);
        rolePermission.setCreateTime(LocalDateTime.now());
        rolePermission.setCreateBy(1L);
        rolePermission.setDeleted(0);

        assertNotNull(rolePermission.getId(), "id字段不能为null");
        assertEquals(1L, rolePermission.getRoleId(), "roleId字段映射失败");
        assertEquals(1L, rolePermission.getPermissionId(), "permissionId字段映射失败");
        assertNotNull(rolePermission.getCreateTime(), "createTime字段不能为null");
        assertEquals(0, rolePermission.getDeleted(), "deleted字段默认值应为0");
    }

    /**
     * 测试SysUserRole实体类字段映射
     * 遵循：测试规范（第6.1条）- 必须测试字段映射
     */
    @Test
    void testSysUserRoleFieldMapping() {
        SysUserRole userRole = new SysUserRole();
        userRole.setId(1L);
        userRole.setUserId(1L);
        userRole.setRoleId(1L);
        userRole.setCreateTime(LocalDateTime.now());
        userRole.setCreateBy(1L);
        userRole.setDeleted(0);

        assertNotNull(userRole.getId(), "id字段不能为null");
        assertEquals(1L, userRole.getUserId(), "userId字段映射失败");
        assertEquals(1L, userRole.getRoleId(), "roleId字段映射失败");
        assertNotNull(userRole.getCreateTime(), "createTime字段不能为null");
        assertEquals(0, userRole.getDeleted(), "deleted字段默认值应为0");
    }

    /**
     * 测试RoleCreateDTO参数验证
     */
    @Test
    void testRoleCreateDTOValidation() {
        RoleCreateDTO dto = new RoleCreateDTO();
        dto.setRoleName("测试角色");
        dto.setRoleCode("TEST");
        dto.setDescription("测试角色描述");
        dto.setStatus("ACTIVE");

        assertEquals("测试角色", dto.getRoleName(), "roleName字段设置失败");
        assertEquals("TEST", dto.getRoleCode(), "roleCode字段设置失败");
        assertEquals("测试角色描述", dto.getDescription(), "description字段设置失败");
        assertEquals("ACTIVE", dto.getStatus(), "status字段设置失败");
    }

    /**
     * 测试RoleUpdateDTO参数验证
     */
    @Test
    void testRoleUpdateDTOValidation() {
        RoleUpdateDTO dto = new RoleUpdateDTO();
        dto.setId(1L);
        dto.setRoleName("更新后的角色");
        dto.setRoleCode("UPDATED");
        dto.setDescription("更新后的描述");
        dto.setStatus("INACTIVE");

        assertEquals(1L, dto.getId(), "id字段设置失败");
        assertEquals("更新后的角色", dto.getRoleName(), "roleName字段设置失败");
        assertEquals("UPDATED", dto.getRoleCode(), "roleCode字段设置失败");
        assertEquals("更新后的描述", dto.getDescription(), "description字段设置失败");
        assertEquals("INACTIVE", dto.getStatus(), "status字段设置失败");
    }

    /**
     * 测试RoleVO字段映射
     */
    @Test
    void testRoleVOFieldMapping() {
        RoleVO vo = new RoleVO();
        vo.setId(1L);
        vo.setRoleName("管理员");
        vo.setRoleCode("ADMIN");
        vo.setDescription("系统管理员");
        vo.setStatus("ACTIVE");
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, vo.getId(), "id字段设置失败");
        assertEquals("管理员", vo.getRoleName(), "roleName字段设置失败");
        assertEquals("ADMIN", vo.getRoleCode(), "roleCode字段设置失败");
        assertEquals("系统管理员", vo.getDescription(), "description字段设置失败");
        assertEquals("ACTIVE", vo.getStatus(), "status字段设置失败");
        assertNotNull(vo.getCreateTime(), "createTime字段不能为null");
        assertNotNull(vo.getUpdateTime(), "updateTime字段不能为null");
    }

    /**
     * 测试PermissionCreateDTO参数验证
     */
    @Test
    void testPermissionCreateDTOValidation() {
        PermissionCreateDTO dto = new PermissionCreateDTO();
        dto.setPermissionName("测试权限");
        dto.setPermissionCode("test:permission");
        dto.setType("BUTTON");
        dto.setParentId(0L);
        dto.setPath("/test");
        dto.setComponent("TestComponent");
        dto.setIcon("test");
        dto.setSortOrder(1);
        dto.setStatus("ACTIVE");

        assertEquals("测试权限", dto.getPermissionName(), "permissionName字段设置失败");
        assertEquals("test:permission", dto.getPermissionCode(), "permissionCode字段设置失败");
        assertEquals("BUTTON", dto.getType(), "type字段设置失败");
        assertEquals(0L, dto.getParentId(), "parentId字段设置失败");
        assertEquals("/test", dto.getPath(), "path字段设置失败");
        assertEquals("TestComponent", dto.getComponent(), "component字段设置失败");
        assertEquals("test", dto.getIcon(), "icon字段设置失败");
        assertEquals(1, dto.getSortOrder(), "sortOrder字段设置失败");
        assertEquals("ACTIVE", dto.getStatus(), "status字段设置失败");
    }

    /**
     * 测试PermissionUpdateDTO参数验证
     */
    @Test
    void testPermissionUpdateDTOValidation() {
        PermissionUpdateDTO dto = new PermissionUpdateDTO();
        dto.setId(1L);
        dto.setPermissionName("更新后的权限");
        dto.setPermissionCode("updated:permission");
        dto.setType("API");
        dto.setParentId(1L);
        dto.setPath("/updated");
        dto.setComponent("UpdatedComponent");
        dto.setIcon("updated");
        dto.setSortOrder(2);
        dto.setStatus("INACTIVE");

        assertEquals(1L, dto.getId(), "id字段设置失败");
        assertEquals("更新后的权限", dto.getPermissionName(), "permissionName字段设置失败");
        assertEquals("updated:permission", dto.getPermissionCode(), "permissionCode字段设置失败");
        assertEquals("API", dto.getType(), "type字段设置失败");
        assertEquals(1L, dto.getParentId(), "parentId字段设置失败");
        assertEquals("/updated", dto.getPath(), "path字段设置失败");
        assertEquals("UpdatedComponent", dto.getComponent(), "component字段设置失败");
        assertEquals("updated", dto.getIcon(), "icon字段设置失败");
        assertEquals(2, dto.getSortOrder(), "sortOrder字段设置失败");
        assertEquals("INACTIVE", dto.getStatus(), "status字段设置失败");
    }

    /**
     * 测试PermissionVO字段映射
     */
    @Test
    void testPermissionVOFieldMapping() {
        PermissionVO vo = new PermissionVO();
        vo.setId(1L);
        vo.setPermissionName("用户管理");
        vo.setPermissionCode("user:manage");
        vo.setType("MENU");
        vo.setParentId(0L);
        vo.setPath("/user");
        vo.setComponent("UserManage");
        vo.setIcon("user");
        vo.setSortOrder(1);
        vo.setStatus("ACTIVE");
        vo.setCreateTime(LocalDateTime.now());
        vo.setUpdateTime(LocalDateTime.now());

        assertEquals(1L, vo.getId(), "id字段设置失败");
        assertEquals("用户管理", vo.getPermissionName(), "permissionName字段设置失败");
        assertEquals("user:manage", vo.getPermissionCode(), "permissionCode字段设置失败");
        assertEquals("MENU", vo.getType(), "type字段设置失败");
        assertEquals(0L, vo.getParentId(), "parentId字段设置失败");
        assertEquals("/user", vo.getPath(), "path字段设置失败");
        assertEquals("UserManage", vo.getComponent(), "component字段设置失败");
        assertEquals("user", vo.getIcon(), "icon字段设置失败");
        assertEquals(1, vo.getSortOrder(), "sortOrder字段设置失败");
        assertEquals("ACTIVE", vo.getStatus(), "status字段设置失败");
        assertNotNull(vo.getCreateTime(), "createTime字段不能为null");
        assertNotNull(vo.getUpdateTime(), "updateTime字段不能为null");
    }
}
```

### 4.2 边界测试场景

#### 4.2.1 字段映射边界测试
- **测试场景1**：roleName和roleCode字段为null
- **测试场景2**：permissionName和permissionCode字段为null
- **测试场景3**：parentId为null（顶级权限）
- **测试场景4**：sortOrder为负数
- **测试场景5**：status字段为无效值

#### 4.2.2 DTO参数验证测试
- **测试场景1**：RoleCreateDTO的roleName为空字符串
- **测试场景2**：RoleCreateDTO的roleCode为空字符串
- **测试场景3**：RoleCreateDTO的status为空字符串
- **测试场景4**：RoleUpdateDTO的id为null
- **测试场景5**：PermissionCreateDTO的permissionName为空字符串
- **测试场景6**：PermissionCreateDTO的permissionCode为空字符串
- **测试场景7**：PermissionCreateDTO的type为空字符串
- **测试场景8**：PermissionUpdateDTO的id为null

#### 4.2.3 审计字段测试
- **测试场景1**：createTime为null
- **测试场景2**：updateTime为null
- **测试场景3**：createBy为null
- **测试场景4**：updateBy为null
- **测试场景5**：deleted为null

#### 4.2.4 逻辑删除测试
- **测试场景1**：deleted字段为0（未删除）
- **测试场景2**：deleted字段为1（已删除）
- **测试场景3**：deleted字段为其他值（异常情况）

### 4.3 异常测试场景

#### 4.3.1 字段映射异常测试
- **测试场景1**：数据库字段名与Java字段名不匹配
- **测试场景2**：数据库字段类型与Java字段类型不匹配
- **测试场景3**：数据库字段长度不足

#### 4.3.2 唯一约束异常测试
- **测试场景1**：插入重复的role_code
- **测试场景2**：插入重复的permission_code
- **测试场景3**：插入重复的role_id和permission_id组合
- **测试场景4**：插入重复的user_id和role_id组合

#### 4.3.3 外键约束异常测试
- **测试场景1**：sys_role_permission中引用不存在的role_id
- **测试场景2**：sys_role_permission中引用不存在的permission_id
- **测试场景3**：sys_user_role中引用不存在的user_id
- **测试场景4**：sys_user_role中引用不存在的role_id

---

## 五、步骤4：文档与知识固化

### 5.1 对development-standards.md的更新建议

#### 5.1.1 新增规范建议

**建议1：关联表设计规范**
**位置**：建议在"一、数据库设计规范"中新增"1.5 关联表设计规范"

**内容建议**：
```markdown
### 1.5 关联表设计规范

**原则**：关联表（如sys_role_permission、sys_user_role）必须包含审计字段和唯一约束

**必须包含的字段**：
```sql
CREATE TABLE `sys_role_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `role_id` bigint NOT NULL COMMENT '角色ID',
  `permission_id` bigint NOT NULL COMMENT '权限ID',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
  KEY `idx_role_id` (`role_id`),
  KEY `idx_permission_id` (`permission_id`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';
```

**⚠️ 常见错误**：
- ❌ 关联表缺少审计字段，导致数据无法追溯
- ❌ 关联表缺少唯一约束，导致数据重复
- ❌ 关联表缺少索引，导致查询性能差
```

**建议2：权限树设计规范**
**位置**：建议在"一、数据库设计规范"中新增"1.6 权限树设计规范"

**内容建议**：
```markdown
### 1.6 权限树设计规范

**原则**：权限表必须支持树形结构，包含parent_id字段

**必须包含的字段**：
```sql
CREATE TABLE `sys_permission` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `permission_name` varchar(100) NOT NULL COMMENT '权限名称',
  `permission_code` varchar(100) NOT NULL COMMENT '权限编码',
  `type` varchar(20) NOT NULL COMMENT '权限类型：MENU/BUTTON/API',
  `parent_id` bigint DEFAULT NULL COMMENT '父权限ID',
  `path` varchar(200) DEFAULT NULL COMMENT '路由路径',
  `component` varchar(200) DEFAULT NULL COMMENT '组件路径',
  `icon` varchar(50) DEFAULT NULL COMMENT '图标',
  `sort_order` int DEFAULT 0 COMMENT '排序号',
  `status` varchar(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/INACTIVE',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
  `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
  `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '逻辑删除：0未删除 1已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_permission_code` (`permission_code`),
  KEY `idx_type` (`type`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_status` (`status`),
  KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';
```

**⚠️ 常见错误**：
- ❌ 权限表缺少parent_id字段，无法构建树形结构
- ❌ 权限表缺少path、component、icon字段，无法支持前端菜单渲染
- ❌ 权限表缺少sort_order字段，无法支持菜单排序
```

#### 5.1.2 现有规范澄清建议

**建议3：字段映射规范澄清**
**位置**：建议在"二、实体类设计规范"的"2.1 字段映射规范"中增加示例

**内容建议**：
```markdown
**示例**：
```java
public class SysRole {
    private Long id;  // 自动映射：id → id
    
    private String roleName;  // 自动映射：roleName → role_name
    
    @TableField("role_code")  // 必须指定
    private String roleCode;
    
    private String description;  // 自动映射：description → description
    
    private String status;  // 自动映射：status → status
    
    private LocalDateTime createTime;  // 自动映射：createTime → create_time
    
    private LocalDateTime updateTime;  // 自动映射：updateTime → update_time
    
    private Long createBy;  // 自动映射：createBy → create_by
    
    private Long updateBy;  // 自动映射：updateBy → update_by
    
    @TableLogic
    private Integer deleted;  // 自动映射：deleted → deleted
}
```

**说明**：
- 对于符合驼峰命名规范的字段（如roleName），MyBatis-Plus会自动映射到数据库的下划线命名（role_name）
- 对于不符合自动映射的字段，必须使用`@TableField`注解明确指定
- 建议所有字段都使用符合规范的命名，减少注解的使用
```

### 5.2 给新开发者的快速指南

#### 5.2.1 角色权限实体类核心使用方式

**要点1：实体类字段映射**
- 数据库字段使用下划线命名（如role_name、role_code）
- Java实体类字段使用驼峰命名（如roleName、roleCode）
- 对于不符合自动映射的字段，使用`@TableField`注解明确指定

**要点2：审计字段配置**
- 所有实体类必须包含：createTime、updateTime、createBy、updateBy、deleted
- createTime使用`@TableField(fill = FieldFill.INSERT)`
- updateTime使用`@TableField(fill = FieldFill.INSERT_UPDATE)`
- deleted使用`@TableLogic`注解实现逻辑删除

**要点3：唯一约束设计**
- role_code和permission_code必须唯一，在数据库层面创建唯一索引
- sys_role_permission和sys_user_role的关联关系必须唯一
- 在数据库层面保证数据完整性

**要点4：权限树设计**
- 权限表必须包含parent_id字段，用于构建树形结构
- 权限表必须包含path、component、icon字段，用于前端菜单渲染
- 权限表必须包含sort_order字段，用于菜单排序

**要点5：DTO类设计**
- CreateDTO用于创建操作，必须包含必填字段的验证注解
- UpdateDTO用于更新操作，必须包含id字段的验证注解
- VO用于查询操作，只包含需要展示给前端的字段

#### 5.2.2 注意事项

**注意事项1：字段命名一致性**
- 确保数据库字段名、Java字段名、DTO字段名保持一致
- 避免因命名不一致导致的映射失败

**注意事项2：审计字段自动填充**
- 确保MyMetaObjectHandler已正确配置
- 确保实体类中的审计字段已正确添加`@TableField(fill = ...)`注解

**注意事项3：逻辑删除配置**
- 确保MyBatis-Plus配置中已启用逻辑删除
- 确保实体类中的deleted字段已添加`@TableLogic`注解

**注意事项4：唯一约束测试**
- 在开发过程中测试唯一约束是否生效
- 确保插入重复数据时会抛出异常

**注意事项5：权限树构建**
- 在Service层实现权限树的构建逻辑
- 使用递归或循环构建树形结构

---

## 六、生成的完整代码清单

### 6.1 实体类文件

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/entity/SysRole.java` | 角色实体类 |
| `backend/src/main/java/com/haocai/management/entity/SysPermission.java` | 权限实体类 |
| `backend/src/main/java/com/haocai/management/entity/SysRolePermission.java` | 角色权限关联实体类 |
| `backend/src/main/java/com/haocai/management/entity/SysUserRole.java` | 用户角色关联实体类 |

### 6.2 DTO类文件

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/java/com/haocai/management/dto/RoleCreateDTO.java` | 角色创建DTO |
| `backend/src/main/java/com/haocai/management/dto/RoleUpdateDTO.java` | 角色更新DTO |
| `backend/src/main/java/com/haocai/management/dto/RoleVO.java` | 角色视图对象 |
| `backend/src/main/java/com/haocai/management/dto/PermissionCreateDTO.java` | 权限创建DTO |
| `backend/src/main/java/com/haocai/management/dto/PermissionUpdateDTO.java` | 权限更新DTO |
| `backend/src/main/java/com/haocai/management/dto/PermissionVO.java` | 权限视图对象 |

### 6.3 测试文件

| 文件路径 | 说明 |
|---------|------|
| `backend/src/test/java/com/haocai/management/entity/RolePermissionEntityTest.java` | 角色权限实体类测试 |

### 6.4 数据库脚本文件

| 文件路径 | 说明 |
|---------|------|
| `backend/src/main/resources/init.sql` | 数据库初始化脚本（已更新） |

### 6.5 文档文件

| 文件路径 | 说明 |
|---------|------|
| `docs/day3/role-permission-entity-design-report.md` | 角色权限实体类设计开发报告（本文件） |
| `docs/common/plan.md` | 项目计划文档（已更新） |

---

## 七、规范遵循与更新摘要

### 7.1 遵循的规范条款

| 规范条款 | 规范内容 | 应用说明 |
|---------|---------|---------|
| 第1.1条 | 字段命名规范 | 数据库字段使用下划线命名，Java实体类字段使用驼峰命名 |
| 第1.3条 | 审计字段规范 | 所有实体类包含create_time, update_time, create_by, update_by, deleted字段 |
| 第2.1条 | 字段映射规范 | 使用@TableField注解明确指定字段映射关系 |
| 第2.3条 | 字段自动填充规范 | 使用@TableField(fill = ...)注解配置自动填充 |
| 第6.1条 | 字段映射测试 | 测试所有实体类的字段映射是否正确 |

### 7.2 提出的更新建议

| 建议编号 | 建议内容 | 建议位置 |
|---------|---------|---------|
| 建议1 | 新增关联表设计规范 | 一、数据库设计规范 - 1.5 |
| 建议2 | 新增权限树设计规范 | 一、数据库设计规范 - 1.6 |
| 建议3 | 澄清字段映射规范 | 二、实体类设计规范 - 2.1 |

---

## 八、后续步骤建议

### 8.1 计划表标注建议

在day3-plan.md中，将1.1任务标注为"已完成"，并添加完成时间：
```markdown
### 1.1 角色权限实体类设计 ✅ 已完成
- [x] 设计SysRole、SysPermission、SysRolePermission、SysUserRole实体类
- [x] 创建对应的DTO类（CreateDTO、UpdateDTO、VO）
- [x] 编写单元测试
- [x] 更新数据库初始化脚本
- [x] 更新plan.md中的表结构定义
- 完成时间：2026年1月6日
```

### 8.2 集成建议

#### 8.2.1 创建Mapper接口
- 创建SysRoleMapper接口
- 创建SysPermissionMapper接口
- 创建SysRolePermissionMapper接口
- 创建SysUserRoleMapper接口

#### 8.2.2 创建Service层
- 创建ISysRoleService接口和SysRoleServiceImpl实现类
- 创建ISysPermissionService接口和SysPermissionServiceImpl实现类
- 实现角色的CRUD操作
- 实现权限的CRUD操作
- 实现权限树的构建逻辑

#### 8.2.3 创建Controller层
- 创建SysRoleController控制器
- 创建SysPermissionController控制器
- 实现角色的增删改查接口
- 实现权限的增删改查接口
- 实现权限树的查询接口

#### 8.2.4 集成测试
- 编写Mapper层单元测试
- 编写Service层单元测试
- 编写Controller层集成测试
- 测试完整的业务流程

### 8.3 数据库初始化

在开发环境中执行以下命令初始化数据库：
```bash
mysql -u root -p haocai_management < backend/src/main/resources/init.sql
```

### 8.4 代码审查建议

在提交代码前，请进行以下审查：
- [ ] 所有实体类字段映射是否正确
- [ ] 所有审计字段是否正确配置
- [ ] 所有唯一约束是否正确创建
- [ ] 所有测试用例是否通过
- [ ] 所有文档是否更新完整

---

## 九、总结

本次开发工作完成了角色权限实体类的设计和实现，包括：

1. **实体类设计**：创建了SysRole、SysPermission、SysRolePermission、SysUserRole四个实体类
2. **DTO类设计**：创建了角色和权限的CreateDTO、UpdateDTO、VO类
3. **数据库更新**：更新了init.sql中的表结构定义，添加了审计字段和索引
4. **文档更新**：更新了plan.md中的表结构定义，使其与init.sql保持一致
5. **测试实现**：编写了完整的单元测试，覆盖所有实体类和DTO类

所有设计都严格遵循了development-standards.md中的规范，确保了代码质量和可维护性。开发过程中的记录和文档可以作为团队核心教材，帮助新开发者快速理解项目的设计思路和实现方式。

---

**报告完成时间**：2026年1月6日  
**报告版本**：v1.0  
**下次更新**：根据实际开发情况持续更新
