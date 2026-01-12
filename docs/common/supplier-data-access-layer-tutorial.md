# 供应商数据访问层开发教程

## 文档信息

- **文档名称**：供应商数据访问层开发教程
- **模块名称**：供应商管理模块
- **版本**：v1.0
- **创建日期**：2026年1月12日
- **维护人员**：开发团队

---

## 一、概述

### 1.1 教程目的

本教程旨在帮助新开发者快速理解和掌握供应商管理模块的数据访问层开发，包括：
- Mapper 层接口设计
- Repository 层封装
- 异常处理机制
- 遵循的开发规范

### 1.2 技术栈

- **数据访问框架**：MyBatis-Plus 3.5.7
- **数据库**：MySQL 8.0+
- **ORM 框架**：MyBatis-Plus（不包含 Spring Data JPA）

### 1.3 遵循规范

本模块开发严格遵循 `development-standards.md` 中的规范：

| 规范条款 | 核心要求 | 应用场景 |
|---------|---------|---------|
| **DB-01** | 所有业务表必须包含审计字段 | `supplier_info` 表包含 `create_time`, `update_time`, `deleted` 等字段 |
| **DB-02** | 表名和字段名使用 snake_case 命名法 | `supplier_code`, `supplier_name` 等 |
| **DB-03** | 唯一索引与逻辑删除冲突处理 | 供应商编码唯一性检查 |
| **2.2 枚举处理** | 使用 `@EnumValue` 注解标记数据库存储值 | `CooperationStatus` 枚举 |
| **2.3 Controller层** | 统一响应 `ApiResponse`、参数校验 `@Validated` | 异常处理配置 |

---

## 二、数据结构

### 2.1 供应商信息表结构

```sql
CREATE TABLE `supplier_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `supplier_code` VARCHAR(50) NOT NULL COMMENT '供应商编码',
  `supplier_name` VARCHAR(100) NOT NULL COMMENT '供应商名称',
  `contact_person` VARCHAR(50) COMMENT '联系人',
  `phone` VARCHAR(20) COMMENT '联系电话',
  `email` VARCHAR(100) COMMENT '电子邮箱',
  `address` VARCHAR(255) COMMENT '地址',
  `business_license` VARCHAR(255) COMMENT '营业执照号',
  `tax_number` VARCHAR(50) COMMENT '税号',
  `bank_account` VARCHAR(50) COMMENT '银行账号',
  `bank_name` VARCHAR(100) COMMENT '开户银行',
  `credit_rating` INT DEFAULT 3 COMMENT '信用评级(1-5)',
  `cooperation_status` TINYINT DEFAULT 1 COMMENT '合作状态: 0-已终止, 1-合作中',
  `status` TINYINT DEFAULT 1 COMMENT '状态: 0-禁用, 1-启用',
  `description` TEXT COMMENT '备注描述',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `create_by` BIGINT COMMENT '创建人ID',
  `update_by` BIGINT COMMENT '更新人ID',
  `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除: 0-未删除, 1-已删除',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_supplier_code` (`supplier_code`),
  KEY `idx_supplier_name` (`supplier_name`),
  KEY `idx_cooperation_status` (`cooperation_status`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='供应商信息表';
```

### 2.2 索引设计说明

| 索引名称 | 索引字段 | 索引类型 | 说明 |
|---------|---------|---------|------|
| PRIMARY | id | 主键 | 聚簇索引 |
| uk_supplier_code | supplier_code | 唯一索引 | 供应商编码唯一性约束 |
| idx_supplier_name | supplier_name | 普通索引 | 供应商名称模糊搜索 |
| idx_cooperation_status | cooperation_status | 普通索引 | 合作状态筛选 |
| idx_status | status | 普通索引 | 状态筛选 |

---

## 三、代码结构

### 3.1 文件清单

```
backend/src/main/java/com/haocai/management/
├── entity/
│   └── SupplierInfo.java              # 供应商实体类
├── mapper/
│   └── SupplierInfoMapper.java        # Mapper接口（继承BaseMapper）
├── repository/
│   └── SupplierInfoRepository.java    # Repository实现类
├── exception/
│   └── SupplierException.java         # 供应商业务异常类
├── dto/
│   ├── SupplierQueryDTO.java          # 查询请求DTO
│   ├── SupplierCreateDTO.java         # 创建请求DTO
│   ├── SupplierUpdateDTO.java         # 更新请求DTO
│   └── SupplierVO.java                # 响应VO
└── enums/
    └── CooperationStatus.java         # 合作状态枚举
```

### 3.2 类关系图

```
┌─────────────────┐       ┌──────────────────┐       ┌─────────────────┐
│   Controller    │──────▶│   Service        │──────▶│  Repository     │
└─────────────────┘       └──────────────────┘       └─────────────────┘
                                                                  │
                                                                  ▼
                                                         ┌─────────────────┐
                                                         │  SupplierInfo   │
                                                         │    Mapper       │
                                                         └─────────────────┘
                                                                  │
                                                                  ▼
                                                         ┌─────────────────┐
                                                         │  supplier_info  │
                                                         │     Table       │
                                                         └─────────────────┘
```

---

## 四、Mapper 层开发

### 4.1 SupplierInfoMapper 接口

SupplierInfoMapper 继承 MyBatis-Plus 的 `BaseMapper`，自动获得基础 CRUD 方法：

```java
@Mapper
public interface SupplierInfoMapper extends BaseMapper<SupplierInfo> {
    
    // 根据信用等级范围查询
    @Select("SELECT * FROM supplier_info WHERE credit_rating >= #{min} AND credit_rating <= #{max} AND deleted = 0 ORDER BY credit_rating DESC")
    List<SupplierInfo> selectByCreditRatingRange(@Param("min") Integer min, @Param("max") Integer max);
    
    // 根据合作状态查询
    @Select("SELECT * FROM supplier_info WHERE cooperation_status = #{status} AND deleted = 0 ORDER BY create_time DESC")
    List<SupplierInfo> selectByCooperationStatus(@Param("status") Integer status);
    
    // 分页查询（带条件）
    @Select("""
        <script>
        SELECT * FROM supplier_info WHERE deleted = 0
        <if test="query.supplierName != null and query.supplierName != ''">
            AND supplier_name LIKE CONCAT('%', #{query.supplierName}, '%')
        </if>
        ...
        ORDER BY ${query.orderBy} ${query.orderDirection}
        LIMIT #{query.size} OFFSET #{query.offset}
        </script>
        """)
    List<SupplierInfo> selectPageList(@Param("query") SupplierQueryDTO query);
    
    // 模糊搜索供应商名称
    @Select("SELECT * FROM supplier_info WHERE supplier_name LIKE CONCAT('%', #{keyword}, '%') AND deleted = 0 ORDER BY supplier_name ASC")
    List<SupplierInfo> selectBySupplierNameLike(@Param("keyword") String keyword);
    
    // 检查供应商编码是否存在（排除指定ID）
    @Select("SELECT COUNT(*) FROM supplier_info WHERE supplier_code = #{supplierCode} AND id != #{excludeId} AND deleted = 0")
    int countBySupplierCodeExcludeId(@Param("supplierCode") String supplierCode, @Param("excludeId") Long excludeId);
    
    // 物理删除供应商（用于唯一索引冲突处理）
    @Select("DELETE FROM supplier_info WHERE supplier_code = #{supplierCode}")
    int physicalDeleteBySupplierCode(@Param("supplierCode") String supplierCode);
}
```

### 4.2 关键规范说明

#### 4.2.1 逻辑删除处理

**遵循规范**：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）

MyBatis-Plus 的逻辑删除会自动在查询时添加 `deleted = 0` 条件：

```java
// 自动添加 WHERE deleted = 0
List<SupplierInfo> list = mapper.selectList(wrapper);
```

#### 4.2.2 唯一索引冲突处理

**遵循规范**：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）

当唯一索引字段可能重复时，必须先物理删除旧记录：

```java
// ❌ 错误：直接插入会导致唯一索引冲突
lambdaQueryWrapper.eq(Entity::getUniqueCode, code);
// 不要加 .eq(Entity::getDeleted, 0);
mapper.delete(lambdaQueryWrapper); 

// ✅ 正确：物理删除旧数据以释放唯一索引
mapper.physicalDeleteBySupplierCode(code);
mapper.insert(newSupplier);
```

---

## 五、Repository 层开发

### 5.1 SupplierInfoRepository 实现类

由于项目只使用 MyBatis-Plus，不使用 Spring Data JPA，Repository 以实现类的形式封装数据访问逻辑：

```java
@Repository
@RequiredArgsConstructor
public class SupplierInfoRepository {
    
    private final SupplierInfoMapper supplierInfoMapper;
    
    /**
     * 根据供应商编码查找供应商
     */
    public Optional<SupplierInfo> findBySupplierCode(String supplierCode) {
        SupplierInfo supplier = supplierInfoMapper.selectOne(
            new LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierCode, supplierCode)
        );
        return Optional.ofNullable(supplier);
    }
    
    /**
     * 根据供应商名称模糊查找（忽略大小写）
     */
    public List<SupplierInfo> findBySupplierNameContainingIgnoreCase(String supplierName) {
        return supplierInfoMapper.selectList(
            new LambdaQueryWrapper<SupplierInfo>()
                .like(SupplierInfo::getSupplierName, supplierName)
                .orderByAsc(SupplierInfo::getSupplierName)
        );
    }
    
    /**
     * 检查供应商编码是否存在
     */
    public boolean existsBySupplierCode(String supplierCode) {
        Long count = supplierInfoMapper.selectCount(
            new LambdaQueryWrapper<SupplierInfo>()
                .eq(SupplierInfo::getSupplierCode, supplierCode)
        );
        return count != null && count > 0;
    }
    
    /**
     * 物理删除供应商（用于唯一索引冲突处理）
     */
    public int physicalDeleteBySupplierCode(String supplierCode) {
        return supplierInfoMapper.physicalDeleteBySupplierCode(supplierCode);
    }
}
```

### 5.2 LambdaQueryWrapper 使用说明

MyBatis-Plus 的 `LambdaQueryWrapper` 提供了类型安全的查询方式：

| 方法 | 说明 | 示例 |
|-----|------|-----|
| `eq()` | 等于 | `.eq(SupplierInfo::getStatus, 1)` |
| `ne()` | 不等于 | `.ne(SupplierInfo::getStatus, 0)` |
| `like()` | 模糊匹配 | `.like(SupplierInfo::getName, "张")` |
| `gt()` | 大于 | `.gt(SupplierInfo::getCreditRating, 3)` |
| `lt()` | 小于 | `.lt(SupplierInfo::getCreditRating, 5)` |
| `between()` | 区间 | `.between(SupplierInfo::getCreditRating, 3, 5)` |
| `orderByAsc()` | 升序排序 | `.orderByAsc(SupplierInfo::getName)` |
| `orderByDesc()` | 降序排序 | `.orderByDesc(SupplierInfo::getCreateTime)` |

---

## 六、异常处理

### 6.1 SupplierException 异常类

```java
@Getter
public class SupplierException extends RuntimeException {
    
    private final String errorCode;
    private final String message;
    
    public SupplierException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.message = message;
    }
    
    // 静态工厂方法
    public static SupplierException notFound(Long supplierId) {
        return new SupplierException("SUPPLIER_NOT_FOUND", 
            String.format("供应商不存在，ID: %d", supplierId));
    }
    
    public static SupplierException codeDuplicate(String supplierCode) {
        return new SupplierException("SUPPLIER_CODE_DUPLICATE",
            String.format("供应商编码已存在: %s", supplierCode));
    }
    
    public static SupplierException hasRelatedMaterials(Long supplierId, int materialCount) {
        return new SupplierException("SUPPLIER_HAS_RELATED_MATERIALS",
            String.format("供应商有关联耗材，无法删除（关联耗材数: %d）", materialCount));
    }
}
```

### 6.2 全局异常处理

在 `GlobalExceptionHandler` 中添加供应商异常处理：

```java
@ExceptionHandler(SupplierException.class)
public ResponseEntity<ApiResponse<Void>> handleSupplierException(SupplierException e) {
    log.warn("供应商异常: code={}, message={}", e.getErrorCode(), e.getMessage());
    int errorCode = Math.abs(e.getErrorCode().hashCode()) % 10000;
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiResponse.error(errorCode, e.getMessage()));
}
```

---

## 七、枚举处理

### 7.1 CooperationStatus 枚举

```java
@Getter
@AllArgsConstructor
public enum CooperationStatus {
    
    COOPERATING(1, "合作中"),
    TERMINATED(0, "已终止");
    
    @EnumValue  // 标记存储到数据库的值
    private final Integer value;
    
    @JsonValue  // JSON序列化/反序列化的值
    private final String description;
    
    public static CooperationStatus fromValue(Integer value) {
        if (value == null) return COOPERATING;
        for (CooperationStatus status : values()) {
            if (status.getValue().equals(value)) return status;
        }
        return COOPERATING;
    }
}
```

### 7.2 实体类中的枚举映射

```java
@Data
@TableName("supplier_info")
public class SupplierInfo {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String supplierCode;
    
    // 枚举字段需要配置 TypeHandler
    @TableField(value = "cooperation_status", typeHandler = CooperationStatusHandler.class)
    private CooperationStatus cooperationStatus;
}
```

---

## 八、常见问题

### 8.1 唯一索引冲突

**问题**：将数据逻辑删除后再次插入相同编码的数据，报 "Duplicate key" 错误。

**解决方案**：插入前先物理删除旧记录。

```java
// 在插入前执行物理删除
supplierInfoRepository.physicalDeleteBySupplierCode(supplierCode);
supplierInfoMapper.insert(supplier);
```

### 8.2 枚举值存储

**问题**：枚举值无法正确存储到数据库。

**解决方案**：确保实体类字段上配置了 `@TableField` 的 `typeHandler`。

```java
@TableField(value = "cooperation_status", typeHandler = CooperationStatusHandler.class)
private CooperationStatus cooperationStatus;
```

### 8.3 逻辑删除查询

**问题**：查询时包含了已删除的数据。

**解决方案**：MyBatis-Plus 默认会自动过滤 `deleted = 0` 的记录，无需额外处理。

---

## 九、测试验证

### 9.1 编译测试

```bash
cd backend
mvn clean compile
```

### 9.2 单元测试示例

```java
@SpringBootTest
class SupplierInfoMapperTest {
    
    @Autowired
    private SupplierInfoMapper supplierInfoMapper;
    
    @Test
    void testSelectByCreditRatingRange() {
        List<SupplierInfo> list = supplierInfoMapper.selectByCreditRatingRange(3, 5);
        assertNotNull(list);
        list.forEach(supplier -> {
            assertTrue(supplier.getCreditRating() >= 3);
            assertTrue(supplier.getCreditRating() <= 5);
        });
    }
    
    @Test
    void testExistsBySupplierCode() {
        boolean exists = supplierInfoMapper.existsBySupplierCode("SUP001");
        assertTrue(exists);
    }
}
```

---

## 十、参考文档

- [MyBatis-Plus 官方文档](https://baomidou.com/)
- [开发规范文档](../common/development-standards.md)
- [数据库设计文档](../common/database-design.md)
- [用户数据访问层教程](./user-data-access-layer-tutorial.md)

---

## 十一、变更记录

| 版本 | 日期 | 变更内容 | 变更人 |
|------|------|----------|--------|
| v1.0 | 2026-01-12 | 初始版本 | 开发团队 |
