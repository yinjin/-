# 耗材分类管理模块后端开发教程

## 1. 概述

本教程详细记录了耗材分类管理模块的后端开发过程，包括实体类设计、数据访问层、业务逻辑层、控制层、数据库表结构设计等完整开发流程。

### 1.1 开发目标

- 实现耗材分类的增删改查功能
- 支持多级分类（最多3级）
- 支持分类树形结构展示
- 支持分类编码自动生成
- 支持批量操作
- 完整的权限控制

### 1.2 技术栈

- Spring Boot 3.1.6
- MyBatis-Plus 3.5.5
- MySQL 8.0
- JDK 17
- Lombok
- Spring Security + JWT

### 1.3 开发规范遵循

本开发过程严格遵循 `docs/common/development-standards.md` 中的规范：

- **数据库设计规范**：字段命名使用下划线命名法（snake_case）
- **实体类设计规范**：字段命名使用驼峰命名法（camelCase），使用MyBatis-Plus注解
- **审计字段规范**：create_time, update_time, create_by, update_by, deleted
- **参数验证规范**：使用jakarta.validation.constraints（Spring Boot 3.x）
- **权限控制规范**：使用@PreAuthorize注解

## 2. 数据库设计

### 2.1 表结构设计

根据 `docs/common/database-design.md` 中的设计，material_category表结构如下：

```sql
CREATE TABLE `material_category` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `category_name` varchar(100) NOT NULL COMMENT '分类名称',
  `category_code` varchar(50) NOT NULL COMMENT '分类编码',
  `parent_id` bigint NOT NULL DEFAULT '0' COMMENT '父分类ID（0表示顶级分类）',
  `level` tinyint NOT NULL DEFAULT '1' COMMENT '分类层级（1-一级，2-二级，3-三级）',
  `description` varchar(500) DEFAULT NULL COMMENT '分类描述',
  `sort_order` int NOT NULL DEFAULT '0' COMMENT '排序号',
  `status` tinyint NOT NULL DEFAULT '1' COMMENT '状态（0-禁用，1-启用）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` varchar(50) DEFAULT NULL COMMENT '创建人',
  `update_by` varchar(50) DEFAULT NULL COMMENT '更新人',
  `deleted` tinyint NOT NULL DEFAULT '0' COMMENT '逻辑删除标记（0-未删除，1-已删除）',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_category_code` (`category_code`),
  KEY `idx_parent_id` (`parent_id`),
  KEY `idx_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='耗材分类表';
```

### 2.2 索引设计

- **PRIMARY KEY**：主键索引，基于id字段
- **UNIQUE KEY uk_category_code**：唯一索引，确保分类编码唯一
- **INDEX idx_parent_id**：普通索引，优化根据父分类ID查询子分类的性能
- **INDEX idx_level**：普通索引，优化根据层级查询分类的性能

### 2.3 分类编码规则

- **一级分类**：A01, A02, A03, ...
- **二级分类**：A01-01, A01-02, A01-03, ...
- **三级分类**：A01-01-01, A01-01-02, A01-01-03, ...

### 2.4 初始化数据

```sql
-- 一级分类（4个）
INSERT INTO material_category (category_name, category_code, parent_id, level, description, sort_order, status, create_by) VALUES
('硬件类', 'A01', 0, 1, '计算机硬件、网络设备等', 1, 1, 'system'),
('软件类', 'A02', 0, 1, '操作系统、办公软件等', 2, 1, 'system'),
('工具类', 'A03', 0, 1, '维修工具、测试工具等', 3, 1, 'system'),
('材料类', 'A04', 0, 1, '线缆、配件等', 4, 1, 'system');

-- 二级分类（12个）
INSERT INTO material_category (category_name, category_code, parent_id, level, description, sort_order, status, create_by) VALUES
('计算机硬件', 'A01-01', 1, 2, 'CPU、内存、硬盘等', 1, 1, 'system'),
('网络设备', 'A01-02', 1, 2, '路由器、交换机等', 2, 1, 'system'),
('外设设备', 'A01-03', 1, 2, '键盘、鼠标、显示器等', 3, 1, 'system'),
('操作系统', 'A02-01', 2, 2, 'Windows、Linux等', 1, 1, 'system'),
('办公软件', 'A02-02', 2, 2, 'Office、WPS等', 2, 1, 'system'),
('开发工具', 'A02-03', 2, 2, 'IDE、编辑器等', 3, 1, 'system'),
('维修工具', 'A03-01', 3, 2, '螺丝刀、钳子等', 1, 1, 'system'),
('测试工具', 'A03-02', 3, 2, '万用表、示波器等', 2, 1, 'system'),
('线缆类', 'A04-01', 4, 2, '网线、电源线等', 1, 1, 'system'),
('配件类', 'A04-02', 4, 2, '螺丝、垫片等', 2, 1, 'system'),
('存储介质', 'A04-03', 4, 2, 'U盘、移动硬盘等', 3, 1, 'system'),
('其他材料', 'A04-04', 4, 2, '其他耗材', 4, 1, 'system');

-- 三级分类（10个）
INSERT INTO material_category (category_name, category_code, parent_id, level, description, sort_order, status, create_by) VALUES
('CPU', 'A01-01-01', 5, 3, '中央处理器', 1, 1, 'system'),
('内存', 'A01-01-02', 5, 3, '内存条', 2, 1, 'system'),
('硬盘', 'A01-01-03', 5, 3, '固态硬盘、机械硬盘', 3, 1, 'system'),
('路由器', 'A01-02-01', 6, 3, '无线路由器、企业路由器', 1, 1, 'system'),
('交换机', 'A01-02-02', 6, 3, '二层交换机、三层交换机', 2, 1, 'system'),
('Windows', 'A02-01-01', 8, 3, 'Windows操作系统', 1, 1, 'system'),
('Linux', 'A02-01-02', 8, 3, 'Linux操作系统', 2, 1, 'system'),
('Office', 'A02-02-01', 9, 3, 'Microsoft Office', 1, 1, 'system'),
('WPS', 'A02-02-02', 9, 3, 'WPS Office', 2, 1, 'system'),
('IDE', 'A02-03-01', 10, 3, '集成开发环境', 1, 1, 'system');
```

### 2.5 数据库更新

在 `backend/src/main/resources/init.sql` 中添加上述表结构和初始化数据。

执行SQL脚本（PowerShell）：
```powershell
Get-Content backend/src/main/resources/init.sql -Encoding UTF8 | mysql -u root -p123456 haocai_management --default-character-set=utf8mb4
```

## 3. 实体类设计

### 3.1 创建实体类

文件路径：`backend/src/main/java/com/haocai/management/entity/MaterialCategory.java`

```java
package com.haocai.management.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 耗材分类实体类
 * 
 * 设计规范遵循：
 * - 使用@TableName注解指定表名
 * - 使用@TableId注解指定主键，类型为自增
 * - 使用@TableField注解配置字段映射和自动填充
 * - 使用@TableLogic注解配置逻辑删除
 * - 审计字段：create_time, update_time, create_by, update_by, deleted
 * - 字段命名使用驼峰命名法（camelCase）
 */
@Data
@TableName("material_category")
public class MaterialCategory {
    
    /**
     * 分类ID
     * 使用自增主键
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 分类名称
     * 对应数据库字段：category_name
     */
    private String categoryName;
    
    /**
     * 分类编码
     * 对应数据库字段：category_code
     * 唯一标识，用于自动生成
     */
    private String categoryCode;
    
    /**
     * 父分类ID
     * 对应数据库字段：parent_id
     * 0表示顶级分类
     */
    private Long parentId;
    
    /**
     * 分类层级
     * 对应数据库字段：level
     * 1-一级，2-二级，3-三级
     */
    private Integer level;
    
    /**
     * 排序号
     * 对应数据库字段：sort_order
     */
    private Integer sortOrder;
    
    /**
     * 分类描述
     * 对应数据库字段：description
     */
    private String description;
    
    /**
     * 状态
     * 对应数据库字段：status
     * 0-禁用，1-启用
     */
    private Integer status;
    
    /**
     * 创建时间
     * 对应数据库字段：create_time
     * 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     * 对应数据库字段：update_time
     * 插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
    
    /**
     * 创建人
     * 对应数据库字段：create_by
     * 插入时自动填充
     */
    @TableField(fill = FieldFill.INSERT)
    private String createBy;
    
    /**
     * 更新人
     * 对应数据库字段：update_by
     * 插入和更新时自动填充
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateBy;
    
    /**
     * 逻辑删除标记
     * 对应数据库字段：deleted
     * 0-未删除，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
```

### 3.2 设计要点

1. **字段映射**：使用MyBatis-Plus的自动映射，数据库下划线命名自动转换为Java驼峰命名
2. **主键策略**：使用自增主键（IdType.AUTO）
3. **自动填充**：审计字段使用@TableField注解配置自动填充
4. **逻辑删除**：使用@TableLogic注解配置逻辑删除
5. **Lombok**：使用@Data注解简化getter/setter方法

## 4. DTO设计

### 4.1 创建DTO

#### 4.1.1 MaterialCategoryCreateDTO

文件路径：`backend/src/main/java/com/haocai/management/dto/MaterialCategoryCreateDTO.java`

```java
package com.haocai.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * 耗材分类创建请求DTO
 * 
 * 设计规范遵循：
 * - 使用jakarta.validation.constraints进行参数验证（Spring Boot 3.x）
 * - @NotBlank：字符串不能为空
 * - @NotNull：对象不能为null
 */
@Data
public class MaterialCategoryCreateDTO {
    
    /**
     * 分类名称
     * 必填
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    
    /**
     * 分类编码
     * 可选，如果不提供则自动生成
     */
    private String categoryCode;
    
    /**
     * 父分类ID
     * 必填，0表示顶级分类
     */
    @NotNull(message = "父分类ID不能为空")
    private Long parentId;
    
    /**
     * 分类描述
     * 可选
     */
    private String description;
    
    /**
     * 排序号
     * 可选，默认为0
     */
    private Integer sortOrder;
    
    /**
     * 状态
     * 可选，默认为1（启用）
     */
    private Integer status;
}
```

#### 4.1.2 MaterialCategoryUpdateDTO

文件路径：`backend/src/main/java/com/haocai/management/dto/MaterialCategoryUpdateDTO.java`

```java
package com.haocai.management.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * 耗材分类更新请求DTO
 * 
 * 设计规范遵循：
 * - 使用jakarta.validation.constraints进行参数验证
 * - 不包含id，id通过URL路径传递
 */
@Data
public class MaterialCategoryUpdateDTO {
    
    /**
     * 分类名称
     * 必填
     */
    @NotBlank(message = "分类名称不能为空")
    private String categoryName;
    
    /**
     * 分类编码
     * 可选
     */
    private String categoryCode;
    
    /**
     * 分类描述
     * 可选
     */
    private String description;
    
    /**
     * 排序号
     * 可选
     */
    private Integer sortOrder;
    
    /**
     * 状态
     * 可选
     */
    private Integer status;
}
```

### 4.2 设计要点

1. **参数验证**：使用jakarta.validation.constraints（Spring Boot 3.x使用jakarta而非javax）
2. **必填字段**：使用@NotBlank和@NotNull注解
3. **可选字段**：不添加验证注解
4. **错误消息**：在注解中提供清晰的错误消息

## 5. VO设计

### 5.1 创建VO

#### 5.1.1 MaterialCategoryVO

文件路径：`backend/src/main/java/com/haocai/management/vo/MaterialCategoryVO.java`

```java
package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 耗材分类信息响应VO
 * 
 * 设计规范遵循：
 * - 使用@Schema注解提供Swagger API文档
 * - 包含完整的分类信息
 * - 用于返回单个分类的详细信息
 */
@Data
@Schema(description = "耗材分类信息响应VO")
public class MaterialCategoryVO {
    
    @Schema(description = "分类ID")
    private Long id;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "分类编码")
    private String categoryCode;
    
    @Schema(description = "父分类ID（0表示顶级分类）")
    private Long parentId;
    
    @Schema(description = "分类层级（1-一级，2-二级，3-三级）")
    private Integer level;
    
    @Schema(description = "排序号")
    private Integer sortOrder;
    
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建人")
    private String createBy;
    
    @Schema(description = "更新人")
    private String updateBy;
}
```

#### 5.1.2 MaterialCategoryTreeVO

文件路径：`backend/src/main/java/com/haocai/management/vo/MaterialCategoryTreeVO.java`

```java
package com.haocai.management.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 耗材分类树形结构响应VO
 * 
 * 设计规范遵循：
 * - 使用@Schema注解提供Swagger API文档
 * - 包含children字段，用于构建树形结构
 * - 用于返回分类树形结构数据
 */
@Data
@Schema(description = "耗材分类树形结构响应VO")
public class MaterialCategoryTreeVO {
    
    @Schema(description = "分类ID")
    private Long id;
    
    @Schema(description = "分类名称")
    private String categoryName;
    
    @Schema(description = "分类编码")
    private String categoryCode;
    
    @Schema(description = "父分类ID（0表示顶级分类）")
    private Long parentId;
    
    @Schema(description = "分类层级（1-一级，2-二级，3-三级）")
    private Integer level;
    
    @Schema(description = "排序号")
    private Integer sortOrder;
    
    @Schema(description = "分类描述")
    private String description;
    
    @Schema(description = "状态（0-禁用，1-启用）")
    private Integer status;
    
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
    
    @Schema(description = "更新时间")
    private LocalDateTime updateTime;
    
    @Schema(description = "创建人")
    private String createBy;
    
    @Schema(description = "更新人")
    private String updateBy;
    
    @Schema(description = "子分类列表")
    private List<MaterialCategoryTreeVO> children;
}
```

### 5.2 设计要点

1. **API文档**：使用@Schema注解提供Swagger API文档
2. **树形结构**：MaterialCategoryTreeVO包含children字段，用于构建树形结构
3. **完整信息**：包含所有必要的分类信息
4. **时间格式**：使用LocalDateTime类型

## 6. 数据访问层设计

### 6.1 创建Mapper接口

文件路径：`backend/src/main/java/com/haocai/management/mapper/MaterialCategoryMapper.java`

```java
package com.haocai.management.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.haocai.management.entity.MaterialCategory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 耗材分类Mapper接口
 * 
 * 设计规范遵循：
 * - 继承BaseMapper，获得MyBatis-Plus提供的CRUD方法
 * - 使用@Mapper注解标记为Mapper接口
 * - 自定义查询方法使用@Select注解
 * - 使用@Param注解标记参数
 */
@Mapper
public interface MaterialCategoryMapper extends BaseMapper<MaterialCategory> {
    
    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    @Select("SELECT * FROM material_category WHERE parent_id = #{parentId} AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<MaterialCategory> selectByParentId(@Param("parentId") Long parentId);
    
    /**
     * 查询顶级分类列表
     * 
     * @return 顶级分类列表
     */
    @Select("SELECT * FROM material_category WHERE parent_id = 0 AND deleted = 0 ORDER BY sort_order ASC, id ASC")
    List<MaterialCategory> selectTopLevelCategories();
    
    /**
     * 根据分类编码查询分类
     * 
     * @param categoryCode 分类编码
     * @return 分类信息
     */
    @Select("SELECT * FROM material_category WHERE category_code = #{categoryCode} AND deleted = 0")
    MaterialCategory selectByCategoryCode(@Param("categoryCode") String categoryCode);
    
    /**
     * 统计指定分类编码的数量（排除指定ID）
     * 用于更新时检查分类编码是否重复
     * 
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID
     * @return 数量
     */
    @Select("SELECT COUNT(*) FROM material_category WHERE category_code = #{categoryCode} AND id != #{excludeId} AND deleted = 0")
    int countByCategoryCodeExcludeId(@Param("categoryCode") String categoryCode, @Param("excludeId") Long excludeId);
    
    /**
     * 统计指定父分类的子分类数量
     * 用于删除前检查是否有子分类
     * 
     * @param parentId 父分类ID
     * @return 子分类数量
     */
    @Select("SELECT COUNT(*) FROM material_category WHERE parent_id = #{parentId} AND deleted = 0")
    int countChildrenByParentId(@Param("parentId") Long parentId);
}
```

### 6.2 设计要点

1. **继承BaseMapper**：获得MyBatis-Plus提供的CRUD方法
2. **自定义查询**：使用@Select注解定义自定义SQL
3. **参数绑定**：使用@Param注解标记参数
4. **逻辑删除**：所有查询都包含deleted = 0条件
5. **排序**：使用sort_order和id进行排序

## 7. 业务逻辑层设计

### 7.1 创建Service接口

文件路径：`backend/src/main/java/com/haocai/management/service/IMaterialCategoryService.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.entity.MaterialCategory;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;

import java.util.List;

/**
 * 耗材分类Service接口
 * 
 * 设计规范遵循：
 * - 继承IService，获得MyBatis-Plus提供的Service方法
 * - 定义业务方法
 * - 方法命名清晰，符合业务语义
 */
public interface IMaterialCategoryService extends IService<MaterialCategory> {
    
    /**
     * 创建分类
     * 
     * @param createDTO 创建请求DTO
     * @return 是否创建成功
     */
    boolean createCategory(MaterialCategoryCreateDTO createDTO);
    
    /**
     * 更新分类
     * 
     * @param id 分类ID
     * @param updateDTO 更新请求DTO
     * @return 是否更新成功
     */
    boolean updateCategory(Long id, MaterialCategoryUpdateDTO updateDTO);
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 是否删除成功
     */
    boolean deleteCategory(Long id);
    
    /**
     * 批量删除分类
     * 
     * @param ids 分类ID列表
     * @return 是否删除成功
     */
    boolean batchDeleteCategories(List<Long> ids);
    
    /**
     * 根据ID查询分类详情
     * 
     * @param id 分类ID
     * @return 分类详情VO
     */
    MaterialCategoryVO getCategoryById(Long id);
    
    /**
     * 获取分类树形结构
     * 
     * @return 分类树形结构
     */
    List<MaterialCategoryTreeVO> getCategoryTree();
    
    /**
     * 根据父分类ID查询子分类列表
     * 
     * @param parentId 父分类ID
     * @return 子分类列表
     */
    List<MaterialCategoryVO> getChildrenByParentId(Long parentId);
    
    /**
     * 查询顶级分类列表
     * 
     * @return 顶级分类列表
     */
    List<MaterialCategoryVO> getTopLevelCategories();
    
    /**
     * 切换分类状态
     * 
     * @param id 分类ID
     * @return 是否切换成功
     */
    boolean toggleCategoryStatus(Long id);
    
    /**
     * 检查分类编码是否存在
     * 
     * @param categoryCode 分类编码
     * @return 是否存在
     */
    boolean existsByCategoryCode(String categoryCode);
    
    /**
     * 检查分类编码是否存在（排除指定ID）
     * 
     * @param categoryCode 分类编码
     * @param excludeId 排除的分类ID
     * @return 是否存在
     */
    boolean existsByCategoryCodeExcludeId(String categoryCode, Long excludeId);
    
    /**
     * 检查分类是否有子分类
     * 
     * @param parentId 父分类ID
     * @return 是否有子分类
     */
    boolean hasChildren(Long parentId);
}
```

### 7.2 创建Service实现类

文件路径：`backend/src/main/java/com/haocai/management/service/impl/MaterialCategoryServiceImpl.java`

```java
package com.haocai.management.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.entity.MaterialCategory;
import com.haocai.management.mapper.MaterialCategoryMapper;
import com.haocai.management.service.IMaterialCategoryService;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 耗材分类业务逻辑层实现类
 * 
 * 设计规范遵循：
 * - 继承ServiceImpl，获得MyBatis-Plus提供的Service实现
 * - 实现IMaterialCategoryService接口
 * - 使用@Transactional注解管理事务
 * - 使用@Slf4j注解记录日志
 * - 使用BeanUtils.copyProperties进行对象拷贝
 */
@Slf4j
@Service
public class MaterialCategoryServiceImpl extends ServiceImpl<MaterialCategoryMapper, MaterialCategory> 
        implements IMaterialCategoryService {
    
    private final MaterialCategoryMapper materialCategoryMapper;
    
    public MaterialCategoryServiceImpl(MaterialCategoryMapper materialCategoryMapper) {
        this.materialCategoryMapper = materialCategoryMapper;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createCategory(MaterialCategoryCreateDTO createDTO) {
        log.info("创建分类，分类名称：{}", createDTO.getCategoryName());
        
        // 1. 检查分类编码是否已存在
        if (createDTO.getCategoryCode() != null && !createDTO.getCategoryCode().isEmpty()) {
            if (existsByCategoryCode(createDTO.getCategoryCode())) {
                log.warn("分类编码已存在：{}", createDTO.getCategoryCode());
                throw new RuntimeException("分类编码已存在");
            }
        }
        
        // 2. 创建分类实体
        MaterialCategory category = new MaterialCategory();
        BeanUtils.copyProperties(createDTO, category);
        
        // 3. 自动生成分类编码（如果未提供）
        if (category.getCategoryCode() == null || category.getCategoryCode().isEmpty()) {
            category.setCategoryCode(generateCategoryCode(category.getParentId()));
        }
        
        // 4. 计算分类层级
        category.setLevel(calculateLevel(category.getParentId()));
        
        // 5. 设置默认值
        if (category.getSortOrder() == null) {
            category.setSortOrder(0);
        }
        if (category.getStatus() == null) {
            category.setStatus(1);
        }
        
        // 6. 保存到数据库
        boolean result = save(category);
        log.info("创建分类成功，分类ID：{}", category.getId());
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateCategory(Long id, MaterialCategoryUpdateDTO updateDTO) {
        log.info("更新分类，分类ID：{}", id);
        
        // 1. 检查分类是否存在
        MaterialCategory category = getById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new RuntimeException("分类不存在");
        }
        
        // 2. 检查分类编码是否已存在（排除当前分类）
        if (updateDTO.getCategoryCode() != null && !updateDTO.getCategoryCode().isEmpty()) {
            if (existsByCategoryCodeExcludeId(updateDTO.getCategoryCode(), id)) {
                log.warn("分类编码已存在：{}", updateDTO.getCategoryCode());
                throw new RuntimeException("分类编码已存在");
            }
        }
        
        // 3. 更新分类信息
        BeanUtils.copyProperties(updateDTO, category);
        
        // 4. 保存到数据库
        boolean result = updateById(category);
        log.info("更新分类成功，分类ID：{}", id);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteCategory(Long id) {
        log.info("删除分类，分类ID：{}", id);
        
        // 1. 检查分类是否存在
        MaterialCategory category = getById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new RuntimeException("分类不存在");
        }
        
        // 2. 检查是否有子分类
        if (hasChildren(id)) {
            log.warn("分类存在子分类，无法删除，分类ID：{}", id);
            throw new RuntimeException("分类存在子分类，无法删除");
        }
        
        // 3. 逻辑删除
        boolean result = removeById(id);
        log.info("删除分类成功，分类ID：{}", id);
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean batchDeleteCategories(List<Long> ids) {
        log.info("批量删除分类，分类ID列表：{}", ids);
        
        // 1. 检查是否有子分类
        for (Long id : ids) {
            if (hasChildren(id)) {
                log.warn("分类存在子分类，无法删除，分类ID：{}", id);
                throw new RuntimeException("分类ID " + id + " 存在子分类，无法删除");
            }
        }
        
        // 2. 批量逻辑删除
        boolean result = removeByIds(ids);
        log.info("批量删除分类成功，删除数量：{}", ids.size());
        return result;
    }
    
    @Override
    public MaterialCategoryVO getCategoryById(Long id) {
        log.info("查询分类详情，分类ID：{}", id);
        
        MaterialCategory category = getById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new RuntimeException("分类不存在");
        }
        
        MaterialCategoryVO vo = new MaterialCategoryVO();
        BeanUtils.copyProperties(category, vo);
        return vo;
    }
    
    @Override
    public List<MaterialCategoryTreeVO> getCategoryTree() {
        log.info("获取分类树形结构");
        
        // 1. 查询所有分类
        List<MaterialCategory> allCategories = list();
        
        // 2. 转换为VO
        List<MaterialCategoryTreeVO> categoryVOs = allCategories.stream()
                .map(category -> {
                    MaterialCategoryTreeVO vo = new MaterialCategoryTreeVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        // 3. 构建树形结构
        List<MaterialCategoryTreeVO> tree = buildTree(categoryVOs, 0L);
        
        log.info("获取分类树形结构成功，根节点数量：{}", tree.size());
        return tree;
    }
    
    @Override
    public List<MaterialCategoryVO> getChildrenByParentId(Long parentId) {
        log.info("查询子分类列表，父分类ID：{}", parentId);
        
        List<MaterialCategory> children = materialCategoryMapper.selectByParentId(parentId);
        
        List<MaterialCategoryVO> result = children.stream()
                .map(category -> {
                    MaterialCategoryVO vo = new MaterialCategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("查询子分类列表成功，子分类数量：{}", result.size());
        return result;
    }
    
    @Override
    public List<MaterialCategoryVO> getTopLevelCategories() {
        log.info("查询顶级分类列表");
        
        List<MaterialCategory> topCategories = materialCategoryMapper.selectTopLevelCategories();
        
        List<MaterialCategoryVO> result = topCategories.stream()
                .map(category -> {
                    MaterialCategoryVO vo = new MaterialCategoryVO();
                    BeanUtils.copyProperties(category, vo);
                    return vo;
                })
                .collect(Collectors.toList());
        
        log.info("查询顶级分类列表成功，顶级分类数量：{}", result.size());
        return result;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean toggleCategoryStatus(Long id) {
        log.info("切换分类状态，分类ID：{}", id);
        
        // 1. 检查分类是否存在
        MaterialCategory category = getById(id);
        if (category == null) {
            log.warn("分类不存在，分类ID：{}", id);
            throw new RuntimeException("分类不存在");
        }
        
        // 2. 切换状态
        category.setStatus(category.getStatus() == 1 ? 0 : 1);
        
        // 3. 保存到数据库
        boolean result = updateById(category);
        log.info("切换分类状态成功，分类ID：{}，新状态：{}", id, category.getStatus());
        return result;
    }
    
    @Override
    public boolean existsByCategoryCode(String categoryCode) {
        MaterialCategory category = materialCategoryMapper.selectByCategoryCode(categoryCode);
        return category != null;
    }
    
    @Override
    public boolean existsByCategoryCodeExcludeId(String categoryCode, Long excludeId) {
        int count = materialCategoryMapper.countByCategoryCodeExcludeId(categoryCode, excludeId);
        return count > 0;
    }
    
    @Override
    public boolean hasChildren(Long parentId) {
        int count = materialCategoryMapper.countChildrenByParentId(parentId);
        return count > 0;
    }
    
    /**
     * 生成分类编码
     * 
     * @param parentId 父分类ID
     * @return 分类编码
     */
    private String generateCategoryCode(Long parentId) {
        if (parentId == 0) {
            // 顶级分类：A01, A02, A03, ...
            List<MaterialCategory> topCategories = materialCategoryMapper.selectTopLevelCategories();
            int nextNumber = topCategories.size() + 1;
            return String.format("A%02d", nextNumber);
        } else {
            // 子分类：A01-01, A01-02, A01-01-01, ...
            MaterialCategory parent = getById(parentId);
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
            
            List<MaterialCategory> siblings = materialCategoryMapper.selectByParentId(parentId);
            int nextNumber = siblings.size() + 1;
            
            return parent.getCategoryCode() + "-" + String.format("%02d", nextNumber);
        }
    }
    
    /**
     * 计算分类层级
     * 
     * @param parentId 父分类ID
     * @return 分类层级
     */
    private Integer calculateLevel(Long parentId) {
        if (parentId == 0) {
            return 1;
        } else {
            MaterialCategory parent = getById(parentId);
            if (parent == null) {
                throw new RuntimeException("父分类不存在");
            }
            return parent.getLevel() + 1;
        }
    }
    
    /**
     * 构建树形结构
     * 
     * @param categories 分类列表
     * @param parentId 父分类ID
     * @return 树形结构
     */
    private List<MaterialCategoryTreeVO> buildTree(List<MaterialCategoryTreeVO> categories, Long parentId) {
        List<MaterialCategoryTreeVO> tree = new ArrayList<>();
        
        for (MaterialCategoryTreeVO category : categories) {
            if (category.getParentId().equals(parentId)) {
                // 递归构建子树
                List<MaterialCategoryTreeVO> children = buildTree(categories, category.getId());
                category.setChildren(children);
                tree.add(category);
            }
        }
        
        return tree;
    }
}
```

### 7.3 设计要点

1. **事务管理**：使用@Transactional注解管理事务
2. **日志记录**：使用@Slf4j注解记录日志
3. **对象拷贝**：使用BeanUtils.copyProperties进行对象拷贝
4. **业务逻辑**：
   - 分类编码自动生成
   - 层级自动计算
   - 树形结构构建
   - 业务规则验证（如删除前检查子分类）
5. **异常处理**：抛出RuntimeException，由全局异常处理器处理

## 8. 控制层设计

### 8.1 创建Controller

文件路径：`backend/src/main/java/com/haocai/management/controller/MaterialCategoryController.java`

```java
package com.haocai.management.controller;

import com.haocai.management.common.ApiResponse;
import com.haocai.management.dto.MaterialCategoryCreateDTO;
import com.haocai.management.dto.MaterialCategoryUpdateDTO;
import com.haocai.management.service.IMaterialCategoryService;
import com.haocai.management.vo.MaterialCategoryTreeVO;
import com.haocai.management.vo.MaterialCategoryVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 耗材分类控制层
 * 
 * 设计规范遵循：
 * - 使用@RestController注解标记为REST控制器
 * - 使用@RequestMapping注解指定基础路径
 * - 使用@Tag注解提供Swagger API文档
 * - 使用@Operation注解提供接口文档
 * - 使用@PreAuthorize注解进行权限控制
 * - 使用@Valid注解进行参数验证
 * - 使用@Slf4j注解记录日志
 * - 统一返回ApiResponse
 */
@Slf4j
@RestController
@RequestMapping("/api/material-category")
@Tag(name = "耗材分类管理", description = "耗材分类管理接口")
public class MaterialCategoryController {
    
    private final IMaterialCategoryService materialCategoryService;
    
    public MaterialCategoryController(IMaterialCategoryService materialCategoryService) {
        this.materialCategoryService = materialCategoryService;
    }
    
    /**
     * 创建分类
     */
    @PostMapping
    @Operation(summary = "创建分类", description = "创建新的耗材分类")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<MaterialCategoryVO> createCategory(@Valid @RequestBody MaterialCategoryCreateDTO createDTO) {
        log.info("========== 创建分类 ==========");
        log.info("分类名称：{}", createDTO.getCategoryName());
        log.info("父分类ID：{}", createDTO.getParentId());
        log.info("权限检查: material");
        
        boolean success = materialCategoryService.createCategory(createDTO);
        
        if (success) {
            log.info("创建分类成功");
            return ApiResponse.success("创建成功");
        } else {
            log.warn("创建分类失败");
            return ApiResponse.error("创建失败");
        }
    }
    
    /**
     * 更新分类
     */
    @PutMapping("/{id}")
    @Operation(summary = "更新分类", description = "更新耗材分类信息")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<MaterialCategoryVO> updateCategory(
            @Parameter(description = "分类ID") @PathVariable Long id,
            @Valid @RequestBody MaterialCategoryUpdateDTO updateDTO) {
        log.info("========== 更新分类 ==========");
        log.info("分类ID：{}", id);
        log.info("分类名称：{}", updateDTO.getCategoryName());
        log.info("权限检查: material");
        
        boolean success = materialCategoryService.updateCategory(id, updateDTO);
        
        if (success) {
            log.info("更新分类成功");
            return ApiResponse.success("更新成功");
        } else {
            log.warn("更新分类失败");
            return ApiResponse.error("更新失败");
        }
    }
    
    /**
     * 删除分类
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除分类", description = "删除耗材分类")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<Void> deleteCategory(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 删除分类 ==========");
        log.info("分类ID：{}", id);
        log.info("权限检查: material");
        
        boolean success = materialCategoryService.deleteCategory(id);
        
        if (success) {
            log.info("删除分类成功");
            return ApiResponse.success("删除成功");
        } else {
            log.warn("删除分类失败");
            return ApiResponse.error("删除失败");
        }
    }
    
    /**
     * 批量删除分类
     */
    @DeleteMapping("/batch")
    @Operation(summary = "批量删除分类", description = "批量删除耗材分类")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<Void> batchDeleteCategories(@RequestBody List<Long> ids) {
        log.info("========== 批量删除分类 ==========");
        log.info("分类ID列表：{}", ids);
        log.info("权限检查: material");
        
        boolean success = materialCategoryService.batchDeleteCategories(ids);
        
        if (success) {
            log.info("批量删除分类成功");
            return ApiResponse.success("批量删除成功");
        } else {
            log.warn("批量删除分类失败");
            return ApiResponse.error("批量删除失败");
        }
    }
    
    /**
     * 查询分类详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询分类详情", description = "根据ID查询耗材分类详情")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<MaterialCategoryVO> getCategoryById(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 查询分类详情 ==========");
        log.info("分类ID：{}", id);
        log.info("权限检查: material");
        
        MaterialCategoryVO category = materialCategoryService.getCategoryById(id);
        
        log.info("查询分类详情成功");
        return ApiResponse.success(category);
    }
    
    /**
     * 获取分类树形结构
     */
    @GetMapping("/tree")
    @Operation(summary = "获取分类树形结构", description = "获取耗材分类的树形结构")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<List<MaterialCategoryTreeVO>> getCategoryTree() {
        log.info("========== 获取分类树形结构 ==========");
        log.info("权限检查: material");
        
        List<MaterialCategoryTreeVO> tree = materialCategoryService.getCategoryTree();
        
        log.info("获取分类树形结构成功，根节点数量：{}", tree.size());
        return ApiResponse.success(tree);
    }
    
    /**
     * 查询子分类列表
     */
    @GetMapping("/children/{parentId}")
    @Operation(summary = "查询子分类列表", description = "根据父分类ID查询子分类列表")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<List<MaterialCategoryVO>> getChildrenByParentId(
            @Parameter(description = "父分类ID") @PathVariable Long parentId) {
        log.info("========== 根据父分类ID查询子分类列表 ==========");
        log.info("父分类ID：{}", parentId);
        log.info("权限检查: material");
        
        List<MaterialCategoryVO> children = materialCategoryService.getChildrenByParentId(parentId);
        
        log.info("查询成功，子分类数量: {}", children.size());
        return ApiResponse.success(children);
    }
    
    /**
     * 查询顶级分类列表
     */
    @GetMapping("/top-level")
    @Operation(summary = "查询顶级分类列表", description = "查询所有顶级分类")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<List<MaterialCategoryVO>> getTopLevelCategories() {
        log.info("========== 查询顶级分类列表 ==========");
        log.info("权限检查: material");
        
        List<MaterialCategoryVO> topCategories = materialCategoryService.getTopLevelCategories();
        
        log.info("查询成功，顶级分类数量: {}", topCategories.size());
        return ApiResponse.success(topCategories);
    }
    
    /**
     * 切换分类状态
     */
    @PutMapping("/{id}/toggle-status")
    @Operation(summary = "切换分类状态", description = "切换耗材分类的启用/禁用状态")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<Void> toggleCategoryStatus(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 切换分类状态 ==========");
        log.info("分类ID：{}", id);
        log.info("权限检查: material");
        
        boolean success = materialCategoryService.toggleCategoryStatus(id);
        
        if (success) {
            log.info("切换分类状态成功");
            return ApiResponse.success("切换状态成功");
        } else {
            log.warn("切换分类状态失败");
            return ApiResponse.error("切换状态失败");
        }
    }
    
    /**
     * 检查分类编码是否存在
     */
    @GetMapping("/check/code")
    @Operation(summary = "检查分类编码", description = "检查分类编码是否已存在")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<Boolean> checkCategoryCode(
            @Parameter(description = "分类编码") @RequestParam String categoryCode) {
        log.info("========== 检查分类编码 ==========");
        log.info("分类编码：{}", categoryCode);
        log.info("权限检查: material");
        
        boolean exists = materialCategoryService.existsByCategoryCode(categoryCode);
        
        log.info("检查分类编码成功，是否存在：{}", exists);
        return ApiResponse.success(exists);
    }
    
    /**
     * 检查分类是否有子分类
     */
    @GetMapping("/{id}/has-children")
    @Operation(summary = "检查子分类", description = "检查分类是否有子分类")
    @PreAuthorize("hasAuthority('material')")
    public ApiResponse<Boolean> hasChildren(@Parameter(description = "分类ID") @PathVariable Long id) {
        log.info("========== 检查子分类 ==========");
        log.info("分类ID：{}", id);
        log.info("权限检查: material");
        
        boolean hasChildren = materialCategoryService.hasChildren(id);
        
        log.info("检查子分类成功，是否有子分类：{}", hasChildren);
        return ApiResponse.success(hasChildren);
    }
}
```

### 8.2 设计要点

1. **RESTful风格**：使用HTTP方法（GET、POST、PUT、DELETE）表示操作类型
2. **权限控制**：使用@PreAuthorize注解进行权限控制
3. **参数验证**：使用@Valid注解进行参数验证
4. **API文档**：使用@Tag和@Operation注解提供Swagger API文档
5. **统一返回**：统一返回ApiResponse
6. **日志记录**：使用@Slf4j注解记录日志
7. **路径变量**：使用@PathVariable注解获取路径变量
8. **请求参数**：使用@RequestParam注解获取请求参数

## 9. 接口测试

### 9.1 登录获取Token

```bash
# 登录接口
curl -X POST http://localhost:8081/api/users/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

响应：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8",
    "user": {
      "id": 1,
      "username": "admin",
      "name": "???",
      "email": "admin@haocai.com",
      "phone": "13800138000"
    }
  }
}
```

### 9.2 测试接口

#### 9.2.1 获取分类树形结构

```bash
curl -X GET http://localhost:8081/api/material-category/tree \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.2 创建分类

```bash
curl -X POST http://localhost:8081/api/material-category \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "测试分类",
    "parentId": 0,
    "description": "这是一个测试分类",
    "sortOrder": 10,
    "status": 1
  }'
```

#### 9.2.3 更新分类

```bash
curl -X PUT http://localhost:8081/api/material-category/27 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8" \
  -H "Content-Type: application/json" \
  -d '{
    "categoryName": "测试分类（已更新）",
    "description": "这是一个测试分类（已更新）",
    "sortOrder": 20,
    "status": 1
  }'
```

#### 9.2.4 删除分类

```bash
curl -X DELETE http://localhost:8081/api/material-category/27 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.5 批量删除分类

```bash
curl -X DELETE http://localhost:8081/api/material-category/batch \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8" \
  -H "Content-Type: application/json" \
  -d '[1, 2, 3]'
```

#### 9.2.6 查询分类详情

```bash
curl -X GET http://localhost:8081/api/material-category/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.7 查询顶级分类列表

```bash
curl -X GET http://localhost:8081/api/material-category/top-level \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.8 查询子分类列表

```bash
curl -X GET http://localhost:8081/api/material-category/children/1 \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.9 切换分类状态

```bash
curl -X PUT http://localhost:8081/api/material-category/1/toggle-status \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.10 检查分类编码

```bash
curl -X GET "http://localhost:8081/api/material-category/check/code?categoryCode=A01" \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

#### 9.2.11 检查子分类

```bash
curl -X GET http://localhost:8081/api/material-category/1/has-children \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJuYW1lIjoiPz8_IiwidXNlcklkIjoxLCJ1c2VybmFtZSI6ImFkbWluIiwiaWF0IjoxNzY3ODgzMDY0LCJleHAiOjE3Njc5Njk0NjR9.JaHd6cMQPDKldtbOrhBdxCrW4pT0eP3anic7ODSbqA8"
```

### 9.3 测试结果

所有接口测试通过，功能正常：

1. ✅ 获取分类树形结构：成功返回26条数据的完整树形结构
2. ✅ 创建分类：成功创建测试分类，返回ID 27
3. ✅ 更新分类：成功更新分类信息
4. ✅ 删除分类：成功删除分类
5. ✅ 批量删除分类：因为有子分类，返回错误（正常的业务逻辑验证）
6. ✅ 查询分类详情：成功返回分类详情
7. ✅ 查询顶级分类列表：成功返回4个顶级分类
8. ✅ 查询子分类列表：成功返回3个子分类
9. ✅ 切换分类状态：成功切换状态（从1切换到0）
10. ✅ 检查分类编码：成功返回true表示编码已存在
11. ✅ 检查子分类：成功返回true表示有子分类

## 10. 开发总结

### 10.1 完成的工作

1. ✅ 数据库表结构设计（material_category表）
2. ✅ 数据库初始化数据（26条记录）
3. ✅ 实体类设计（MaterialCategory）
4. ✅ DTO设计（MaterialCategoryCreateDTO、MaterialCategoryUpdateDTO）
5. ✅ VO设计（MaterialCategoryVO、MaterialCategoryTreeVO）
6. ✅ 数据访问层设计（MaterialCategoryMapper）
7. ✅ 业务逻辑层设计（IMaterialCategoryService、MaterialCategoryServiceImpl）
8. ✅ 控制层设计（MaterialCategoryController）
9. ✅ 接口测试（11个接口全部测试通过）

### 10.2 技术要点

1. **MyBatis-Plus**：使用BaseMapper和IService简化CRUD操作
2. **自动填充**：使用@TableField注解配置审计字段自动填充
3. **逻辑删除**：使用@TableLogic注解配置逻辑删除
4. **参数验证**：使用jakarta.validation.constraints进行参数验证
5. **权限控制**：使用@PreAuthorize注解进行权限控制
6. **事务管理**：使用@Transactional注解管理事务
7. **树形结构**：递归构建树形结构
8. **分类编码自动生成**：根据父分类自动生成分类编码
9. **层级自动计算**：根据父分类自动计算分类层级

### 10.3 遵循的规范

1. ✅ 数据库设计规范（字段命名、字段类型、审计字段、索引规范）
2. ✅ 实体类设计规范（字段映射、类型转换器、字段自动填充）
3. ✅ 数据访问层规范（批量操作、异常处理、事务管理）
4. ✅ 控制层规范（批量操作接口、异常处理）
5. ✅ 参数验证规范（使用jakarta.validation.constraints）

### 10.4 待完成的工作

1. ⏳ 单元测试
2. ⏳ E2E测试
3. ⏳ 前端页面开发
4. ⏳ 前端API接口开发
5. ⏳ 前端类型定义
6. ⏳ 前端路由配置
7. ⏳ 前端功能测试
8. ⏳ 前后端集成测试

## 11. 参考资料

- [MyBatis-Plus官方文档](https://baomidou.com/)
- [Spring Boot官方文档](https://spring.io/projects/spring-boot)
- [Spring Security官方文档](https://spring.io/projects/spring-security)
- [Swagger官方文档](https://swagger.io/)
- [Lombok官方文档](https://projectlombok.org/)
- [development-standards.md](../common/development-standards.md)
- [database-design.md](../common/database-design.md)
- [day5-plan.md](./day5-plan.md)
