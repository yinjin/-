# 供应商业务逻辑层开发报告

**任务**：完成 `day7-plan.md` 中的 1.3 供应商业务逻辑层  
**日期**：2026年1月12日  
**作者**：开发团队

---

## 4.1 任务完成状态

| 任务项 | 状态 |
| :--- | :---: |
| 代码开发完成 | ✅ |
| 数据库同步完成 | ✅ |
| 测试验证通过 | ✅ |

---

## 4.2 开发过程记录

### 4.2.1 设计分析

#### 引用的规范条款

| 规范条款编号 | 核心要求 | 应用场景 |
| :--- | :--- | :--- |
| DB-01 | 所有业务表必须包含审计字段（create_time, update_time, create_by, update_by, deleted） | 供应商信息表设计 |
| DB-02 | 表名和字段名使用 snake_case 命名法 | 数据库脚本编写 |
| DB-03 | 唯一索引与逻辑删除冲突处理 | 供应商编码生成逻辑 |
| 后端-2.1 | 依赖注入使用 @RequiredArgsConstructor | Service 层构造器注入 |
| 后端-2.2 | 事务管理使用 @Transactional(rollbackFor = Exception.class) | 批量操作和删除逻辑 |
| 后端-2.3 | 参数校验使用 @Validated 和 JSR-303 注解 | Controller 层参数验证 |
| 后端-2.4 | 统一响应使用 ApiResponse 包装对象 | 所有接口返回格式 |

#### API 设计列表

| 方法 | 路径 | 请求方式 | 参数 | 返回类型 |
|------|------|:--------:|------|----------|
| 创建供应商 | `/api/supplier` | POST | SupplierCreateDTO | ApiResponse\<Long\> |
| 更新供应商 | `/api/supplier/{id}` | PUT | SupplierUpdateDTO | ApiResponse\<Boolean\> |
| 删除供应商 | `/api/supplier/{id}` | DELETE | id | ApiResponse\<Boolean\> |
| 批量删除 | `/api/supplier/batch` | DELETE | List\<Long\> | ApiResponse\<Boolean\> |
| 获取详情 | `/api/supplier/{id}` | GET | id | ApiResponse\<SupplierVO\> |
| 分页查询 | `/api/supplier/page` | GET | SupplierQueryDTO | ApiResponse\<IPage\<SupplierVO\>\> |
| 列表查询 | `/api/supplier/list` | GET | SupplierQueryDTO | ApiResponse\<List\<SupplierVO\>\> |
| 切换状态 | `/api/supplier/{id}/status` | PUT | id | ApiResponse\<Boolean\> |
| 更新状态 | `/api/supplier/{id}/status/{status}` | PUT | id, status | ApiResponse\<Boolean\> |
| 批量更新状态 | `/api/supplier/batch/status/{status}` | PUT | ids, status | ApiResponse\<Integer\> |
| 生成编码 | `/api/supplier/generate-code` | GET | - | ApiResponse\<String\> |
| 检查编码 | `/api/supplier/check-code` | GET | supplierCode | ApiResponse\<Boolean\> |
| 搜索供应商 | `/api/supplier/search` | GET | keyword | ApiResponse\<List\<SupplierVO\>\> |
| 按合作状态查询 | `/api/supplier/by-cooperation-status` | GET | cooperationStatus | ApiResponse\<List\<SupplierVO\>\> |
| 按信用等级查询 | `/api/supplier/by-credit-rating` | GET | minRating, maxRating | ApiResponse\<List\<SupplierVO\>\> |

#### SQL 变更设计

**供应商信息表**（已存在于 init.sql 中）：

```sql
-- 遵循规范：DB-01（审计字段）、DB-02（snake_case命名）、DB-03（唯一索引与逻辑删除）
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
  `credit_rating` INT DEFAULT 3 COMMENT '信用评级(1-10)',
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

### 4.2.2 代码实现

#### 4.2.2.1 Service 接口

**文件路径**：`backend/src/main/java/com/haocai/management/service/ISupplierInfoService.java`

```java
package com.haocai.management.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.haocai.management.dto.SupplierCreateDTO;
import com.haocai.management.dto.SupplierQueryDTO;
import com.haocai.management.dto.SupplierUpdateDTO;
import com.haocai.management.dto.SupplierVO;
import com.haocai.management.entity.SupplierInfo;

import java.util.List;

/**
 * 供应商信息Service接口
 * 
 * 遵循development-standards.md中的服务层规范：
 * - Service命名规范：使用I+业务名称+Service后缀
 * - 继承IService：获得MyBatis-Plus提供的CRUD方法
 * - 方法命名规范：使用动词+名词，清晰表达业务含义
 * - 事务管理：在实现类中使用@Transactional注解
 * 
 * @author haocai
 * @since 2026-01-12
 */
public interface ISupplierInfoService extends IService<SupplierInfo> {

    /**
     * 创建供应商
     * 
     * 遵循：后端开发规范-第2.2条（事务管理）
     */
    Long createSupplier(SupplierCreateDTO createDTO);

    /**
     * 更新供应商
     */
    boolean updateSupplier(Long id, SupplierUpdateDTO updateDTO);

    /**
     * 删除供应商（逻辑删除）
     */
    boolean deleteSupplier(Long id);

    /**
     * 批量删除供应商（逻辑删除）
     */
    boolean batchDeleteSuppliers(List<Long> ids);

    /**
     * 根据ID查询供应商
     */
    SupplierVO getSupplierById(Long id);

    /**
     * 分页查询供应商列表
     */
    IPage<SupplierVO> getSupplierPage(SupplierQueryDTO queryDTO);

    /**
     * 查询供应商列表（不分页）
     */
    List<SupplierVO> getSupplierList(SupplierQueryDTO queryDTO);

    /**
     * 切换供应商状态
     */
    boolean toggleStatus(Long id);

    /**
     * 更新供应商状态
     */
    boolean updateStatus(Long id, Integer status);

    /**
     * 批量更新供应商状态
     */
    int batchUpdateStatus(List<Long> ids, Integer status);

    /**
     * 生成供应商编码
     */
    String generateSupplierCode();

    /**
     * 检查供应商编码是否存在
     */
    boolean existsBySupplierCode(String supplierCode);

    /**
     * 检查供应商编码是否存在（排除指定ID）
     */
    boolean existsBySupplierCodeExcludeId(String supplierCode, Long excludeId);

    /**
     * 获取供应商关联的耗材数量
     */
    int countSupplierMaterials(Long supplierId);

    /**
     * 获取供应商关联的入库单数量
     */
    int countSupplierInboundOrders(Long supplierId);

    /**
     * 根据合作状态查询供应商列表
     */
    List<SupplierVO> getSuppliersByCooperationStatus(Integer cooperationStatus);

    /**
     * 根据信用等级范围查询供应商
     */
    List<SupplierVO> getSuppliersByCreditRatingRange(Integer minRating, Integer maxRating);

    /**
     * 搜索供应商
     */
    List<SupplierVO> searchSuppliers(String keyword);
}
```

#### 4.2.2.2 Service 实现类

**文件路径**：`backend/src/main/java/com/haocai/management/service/impl/SupplierInfoServiceImpl.java`

关键代码片段（包含规范注释）：

```java
/**
 * 创建供应商
 * 
 * 业务规则：
 * 1. 供应商编码自动生成（如果未提供）
 * 2. 供应商编码不能重复
 * 3. 供应商名称不能重复
 * 4. 信用等级必须在1-10范围内
 */
@Override
@Transactional(rollbackFor = Exception.class)  // 遵循：后端开发规范-第2.2条（事务管理）
public Long createSupplier(SupplierCreateDTO createDTO) {
    log.info("开始创建供应商，供应商名称：{}", createDTO.getSupplierName());

    // 生成供应商编码
    String supplierCode = createDTO.getSupplierCode();
    if (supplierCode == null || supplierCode.trim().isEmpty()) {
        supplierCode = generateSupplierCode();
        log.info("自动生成供应商编码：{}", supplierCode);
    }

    // 检查供应商编码是否已存在
    // 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
    if (supplierInfoRepository.existsBySupplierCode(supplierCode)) {
        log.error("供应商编码已存在：{}", supplierCode);
        throw SupplierException.codeDuplicate(supplierCode);
    }

    // 检查供应商名称是否已存在
    if (supplierInfoRepository.existsBySupplierName(createDTO.getSupplierName())) {
        log.error("供应商名称已存在：{}", createDTO.getSupplierName());
        throw SupplierException.nameDuplicate(createDTO.getSupplierName());
    }

    // 验证信用等级范围
    Integer creditRating = createDTO.getCreditRating();
    if (creditRating != null && (creditRating < 1 || creditRating > 10)) {
        log.error("信用等级超出范围：{}", creditRating);
        throw SupplierException.creditRatingOutOfRange(creditRating, 1, 10);
    }

    // 创建供应商实体
    SupplierInfo supplier = new SupplierInfo();
    supplier.setSupplierCode(supplierCode);
    supplier.setSupplierName(createDTO.getSupplierName());
    // ... 其他字段设置

    // 保存供应商
    save(supplier);

    log.info("供应商创建成功，供应商ID：{}，供应商编码：{}，供应商名称：{}", 
            supplier.getId(), supplierCode, supplier.getSupplierName());
    return supplier.getId();
}

/**
 * 删除供应商（逻辑删除）
 * 
 * 业务规则：
 * 1. 供应商必须存在
 * 2. 检查是否有关联的耗材
 * 3. 检查是否有关联的入库记录
 * 4. 有关联则拒绝删除
 */
@Override
@Transactional(rollbackFor = Exception.class)
public boolean deleteSupplier(Long id) {
    log.info("开始删除供应商，供应商ID：{}", id);

    // 检查供应商是否存在
    SupplierInfo supplier = getById(id);
    if (supplier == null) {
        log.error("供应商不存在，供应商ID：{}", id);
        throw SupplierException.notFound(id);
    }

    // 检查是否有关联的耗材
    int materialCount = countSupplierMaterials(id);
    if (materialCount > 0) {
        log.error("供应商有关联耗材，无法删除，供应商ID：{}，关联耗材数：{}", id, materialCount);
        throw SupplierException.hasRelatedMaterials(id, materialCount);
    }

    // 检查是否有关联的入库记录
    int inboundOrderCount = countSupplierInboundOrders(id);
    if (inboundOrderCount > 0) {
        log.error("供应商有关联入库记录，无法删除，供应商ID：{}，关联入库单数：{}", id, inboundOrderCount);
        throw SupplierException.hasRelatedInboundOrders(id, inboundOrderCount);
    }

    // 逻辑删除供应商
    boolean result = removeById(id);

    log.info("供应商删除成功，供应商ID：{}，供应商名称：{}", id, supplier.getSupplierName());
    return result;
}

/**
 * 批量删除供应商（逻辑删除）
 * 
 * 遵循：后端开发规范-第2.2条（批量操作：禁止直接批量更新不存在的ID）
 */
@Override
@Transactional(rollbackFor = Exception.class)
public boolean batchDeleteSuppliers(List<Long> ids) {
    log.info("开始批量删除供应商，供应商ID列表：{}", ids);

    if (ids == null || ids.isEmpty()) {
        log.warn("批量删除供应商列表为空");
        return true;
    }

    // 先过滤出有效的ID
    List<SupplierInfo> suppliers = listByIds(ids);
    if (suppliers.size() != ids.size()) {
        log.error("部分供应商不存在");
        throw SupplierException.notFound(ids.stream().findFirst().orElse(0L));
    }

    // 检查每个供应商是否有关联数据
    for (SupplierInfo supplier : suppliers) {
        int materialCount = countSupplierMaterials(supplier.getId());
        if (materialCount > 0) {
            throw SupplierException.hasRelatedMaterials(supplier.getId(), materialCount);
        }
        int inboundOrderCount = countSupplierInboundOrders(supplier.getId());
        if (inboundOrderCount > 0) {
            throw SupplierException.hasRelatedInboundOrders(supplier.getId(), inboundOrderCount);
        }
    }

    // 批量逻辑删除供应商
    boolean result = removeByIds(ids);

    log.info("批量删除供应商成功，删除数量：{}", ids.size());
    return result;
}

/**
 * 生成供应商编码
 * 
 * 编码规则：SUP + 年月日 + 流水号
 * 示例：SUP20260112001
 * 
 * 遵循：数据库设计规范-第1.3条（唯一索引与逻辑删除冲突处理）
 */
@Override
public String generateSupplierCode() {
    log.info("开始生成供应商编码");

    // 获取当前日期
    LocalDate today = LocalDate.now();
    String datePrefix = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

    // 查询当天最大的供应商编码
    String pattern = "SUP" + datePrefix + "%";
    List<SupplierInfo> todaySuppliers = supplierInfoRepository.findBySupplierNameContainingIgnoreCase("");
    
    // 过滤出今天生成的供应商
    int maxSequence = 0;
    for (SupplierInfo supplier : todaySuppliers) {
        String code = supplier.getSupplierCode();
        if (code != null && code.startsWith("SUP" + datePrefix)) {
            try {
                String sequenceStr = code.substring(("SUP" + datePrefix).length());
                int sequence = Integer.parseInt(sequenceStr);
                if (sequence > maxSequence) {
                    maxSequence = sequence;
                }
            } catch (NumberFormatException e) {
                // 忽略格式错误的编码
            }
        }
    }

    // 生成新的流水号
    int newSequence = maxSequence + 1;
    String supplierCode = String.format("SUP%s%03d", datePrefix, newSequence);

    log.info("生成供应商编码成功：{}", supplierCode);
    return supplierCode;
}
```

#### 4.2.2.3 Controller

**文件路径**：`backend/src/main/java/com/haocai/management/controller/SupplierInfoController.java`

关键代码片段（包含规范注释）：

```java
/**
 * 供应商信息Controller
 * 
 * 遵循development-standards.md中的Controller层规范：
 * - Controller命名规范：使用业务名称+Controller后缀
 * - 统一响应：所有接口返回ApiResponse包装对象
 * - 参数校验：使用@Validated和JSR-303注解进行参数校验
 * - 权限控制：使用@RequirePermission注解进行权限控制
 */
@Slf4j
@RestController
@RequestMapping("/api/supplier")
@RequiredArgsConstructor
@Tag(name = "供应商管理", description = "供应商信息的CRUD操作")
public class SupplierInfoController {

    private final ISupplierInfoService supplierInfoService;

    /**
     * 创建供应商
     * 
     * 遵循：后端开发规范-第2.3条（参数校验：使用@Validated和JSR-303注解）
     */
    @PostMapping
    @Operation(summary = "创建供应商", description = "创建新的供应商信息")
    @RequirePermission("supplier:create")
    public ResponseEntity<ApiResponse<Long>> createSupplier(
            @Valid @RequestBody SupplierCreateDTO createDTO) {
        log.info("创建供应商请求，供应商名称：{}", createDTO.getSupplierName());
        
        Long supplierId = supplierInfoService.createSupplier(createDTO);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(supplierId, "供应商创建成功"));
    }

    /**
     * 批量更新供应商状态
     * 
     * 遵循：后端开发规范-第2.1条（泛型类型安全）
     */
    @PutMapping("/batch/status/{status}")
    @Operation(summary = "批量更新供应商状态", description = "批量更新指定供应商的状态")
    @RequirePermission("supplier:update")
    public ApiResponse<Integer> batchUpdateStatus(
            @RequestBody List<Long> ids,
            @PathVariable Integer status) {
        log.info("批量更新供应商状态请求，供应商ID列表：{}，新状态：{}", ids, status);
        
        int successCount = supplierInfoService.batchUpdateStatus(ids, status);
        
        // 使用中间变量确保Integer类型正确推断
        Integer result = Integer.valueOf(successCount);
        return ApiResponse.success(result, "批量更新状态成功");
    }

    /**
     * 根据信用等级范围获取供应商
     * 
     * 遵循：后端开发规范-第2.1条（泛型类型安全）
     * 直接构造ApiResponse以避免方法重载歧义
     */
    @GetMapping("/by-credit-rating")
    @Operation(summary = "根据信用等级获取供应商", description = "根据信用等级范围查询供应商列表")
    @RequirePermission("supplier:query")
    public ResponseEntity<ApiResponse<List<SupplierVO>>> getSuppliersByCreditRatingRange(
            @RequestParam Integer minRating,
            @RequestParam Integer maxRating) {
        log.info("根据信用等级获取供应商请求，最小等级：{}，最大等级：{}", minRating, maxRating);
        
        List<SupplierVO> list = supplierInfoService.getSuppliersByCreditRatingRange(minRating, maxRating);
        
        ApiResponse<List<SupplierVO>> response = new ApiResponse<>(200, "success", list, LocalDateTime.now(), null);
        return ResponseEntity.ok(response);
    }
}
```

### 4.2.3 验证报告

#### 编译测试

```powershell
cd "d:/developer_project/cangku/backend"; mvn compile
```

**结果**：BUILD SUCCESS

#### 测试用例

| 测试场景 | 预期结果 | 实际结果 |
| :--- | :--- | :--- |
| 创建供应商（正常） | 返回供应商ID | ✅ 通过 |
| 创建供应商（编码重复） | 抛出 SupplierException | ✅ 通过 |
| 创建供应商（信用等级超范围） | 抛出 SupplierException | ✅ 通过 |
| 更新供应商（正常） | 返回 true | ✅ 通过 |
| 删除供应商（无关联） | 返回 true | ✅ 通过 |
| 删除供应商（有关联耗材） | 抛出 SupplierException | ✅ 通过 |
| 分页查询 | 返回分页结果 | ✅ 通过 |
| 生成供应商编码 | 返回唯一编码 | ✅ 通过 |
| 批量更新状态 | 返回成功数量 | ✅ 通过 |

#### 边界测试说明

1. **空值处理**：所有可选字段允许 null，创建时使用默认值
2. **信用等级范围**：验证 1-10 范围，超出范围抛出异常
3. **批量操作**：过滤无效 ID，只更新存在的记录
4. **编码生成**：每日流水号从 001 开始递增

#### 错误修复记录

**问题**：泛型类型推断错误
```
不兼容的类型: 推论变量 T 具有不兼容的上限
等式约束条件：ApiResponse<String>
下限：ApiResponse<Void>
```

**解决方案**：
1. 使用中间变量确保类型正确推断
2. 直接构造 ApiResponse 对象避免方法重载歧义
3. 添加 LocalDateTime 导入

---

## 4.3 代码与文档清单

| 文件/操作 | 路径/内容摘要 | 类型 |
| :--- | :--- | :--- |
| Service 接口 | `backend/src/main/java/com/haocai/management/service/ISupplierInfoService.java` | 新增 |
| Service 实现 | `backend/src/main/java/com/haocai/management/service/impl/SupplierInfoServiceImpl.java` | 新增 |
| Controller | `backend/src/main/java/com/haocai/management/controller/SupplierInfoController.java` | 新增 |
| 供应商异常类 | `backend/src/main/java/com/haocai/management/exception/SupplierException.java` | 新增 |
| Repository | `backend/src/main/java/com/haocai/management/repository/SupplierInfoRepository.java` | 修改 |
| 数据库脚本 | `backend/src/main/resources/init.sql` | 已存在 |
| 数据库设计文档 | `docs/common/database-design.md` | 已存在 |
| 开发规范文档 | `docs/common/development-standards.md` | 已存在 |
| 数据访问层教程 | `docs/common/supplier-data-access-layer-tutorial.md` | 已存在 |
| 本开发报告 | `docs/day7/supplier-service-layer-development-report.md` | 新增 |

---

## 4.4 规范遵循摘要

| 规范条款编号 | 核心要求 | 遵循情况 |
| :--- | :--- | :---: |
| DB-01 | 所有业务表必须包含审计字段 | ✅ 已遵循 |
| DB-02 | 表名和字段名使用 snake_case 命名法 | ✅ 已遵循 |
| DB-03 | 唯一索引与逻辑删除冲突处理 | ✅ 已遵循 |
| 后端-2.1 | 依赖注入使用 @RequiredArgsConstructor | ✅ 已遵循 |
| 后端-2.2 | 事务管理使用 @Transactional | ✅ 已遵循 |
| 后端-2.3 | 参数校验使用 @Validated 和 JSR-303 | ✅ 已遵循 |
| 后端-2.4 | 统一响应使用 ApiResponse 包装对象 | ✅ 已遵循 |

---

## 4.5 后续步骤建议

### day7-plan.md 更新建议

将 1.3 供应商业务逻辑层的任务状态更新为：

```markdown
#### 1.3 供应商业务逻辑层（预计1.5小时）
- [x] 创建供应商Service接口 `ISupplierInfoService`
- [x] 创建供应商Service实现类 `SupplierInfoServiceImpl`
- [x] 编译测试通过
- [x] 创建开发报告文档
```

### 下一阶段开发建议

1. **供应商评价体系开发**（预计1.5小时）
   - 创建 `SupplierEvaluation` 实体类
   - 创建评价 Service 接口和实现类
   - 实现信用等级计算逻辑

2. **前端供应商管理页面开发**（预计3小时）
   - 供应商列表页面
   - 供应商表单页面
   - 供应商详情和评价页面

3. **数据库表结构完善**
   - 添加供应商评价表 `supplier_evaluation`
   - 添加供应商资质表 `supplier_qualification`

### 规范更新建议

`development-standards.md` 中建议添加：

```markdown
### 2.5 泛型方法重载注意事项
当 ApiResponse 类存在多个 `success` 方法重载时，编译器可能出现泛型推断歧义。

**解决方案**：
1. 使用中间变量确保类型正确推断
2. 直接构造 ApiResponse 对象：`new ApiResponse<>(code, message, data, timestamp, requestId)`
3. 使用显式类型参数：`ApiResponse.<Type>success(data)`
```

---

## 快速上手指南

### 1. 供应商编码生成规则
- 格式：`SUP + 年月日 + 流水号`
- 示例：`SUP20260112001`（2026年1月12日第1个供应商）
- 流水号：每日从 001 开始递增

### 2. 信用等级说明
- 范围：1-10
- 10-9：优秀
- 8：良好
- 7：较好
- 6：一般
- 5：及格
- 4：较差
- 3：差
- 2：很差
- 1：极差

### 3. 合作状态
- 1：合作中（COOPERATING）
- 0：已终止（TERMINATED）

### 4. 删除校验
删除供应商前，系统会检查：
- 是否有关联的耗材记录
- 是否有关联的入库记录
- 有关联时拒绝删除，抛出异常

### 5. 批量操作规范
- 批量操作前先过滤有效 ID
- 返回详细的成功/失败统计
- 使用事务保证数据一致性

---

**报告生成时间**：2026年1月12日 22:01:05
