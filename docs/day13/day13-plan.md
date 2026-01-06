# 第十三天工作计划：库存盘点模块开发

## 日期：2026年1月18日

## 总体目标
完成库存盘点模块的开发，实现盘点单的创建、盘点明细录入、盘盈盘亏计算、库存调整等核心功能，建立完整的库存盘点流程，为库存管理提供数据支持。

## 详细工作任务

### 1. 后端盘点单管理功能开发（预计2.5小时）

#### 1.1 盘点单实体类和DTO设计（预计0.5小时）
- [ ] 创建盘点单实体类`InventoryCheck`
  - 盘点单ID（主键）
  - 盘点单号（唯一索引）
  - 盘点日期（必填）
  - 盘点人ID（必填）
  - 盘点人部门ID（必填）
  - 盘点类型（必填：全盘/抽盘/专项盘）
  - 盘点范围（JSON格式，存储盘点范围配置）
  - 状态（必填：进行中/已完成/已作废）
  - 总项数（默认0）
  - 差异项数（默认0）
  - 备注信息（可选）
  - 完成时间（可选）
  - 创建时间、更新时间
  - 使用Lombok注解简化代码
  - 配置JPA注解和表映射
- [ ] 创建盘点单DTO类
  - `InventoryCheckCreateDTO`：盘点单创建请求
    - 盘点日期（必填）
    - 盘点人ID（必填）
    - 盘点类型（必填）
    - 盘点范围（必填，JSON格式）
    - 备注信息（可选）
  - `InventoryCheckUpdateDTO`：盘点单更新请求
    - 盘点单ID（必填）
    - 盘点日期（可选）
    - 盘点类型（可选）
    - 盘点范围（可选）
    - 备注信息（可选）
    - 状态（可选）
  - `InventoryCheckVO`：盘点单响应
    - 盘点单基本信息
    - 盘点人信息
    - 盘点人部门信息
    - 盘点明细统计信息
    - 盘点进度信息
- [ ] 创建盘点类型枚举类`CheckType`
  - 定义盘点类型枚举（全盘、抽盘、专项盘）
  - 提供类型转换方法
  - 提供类型描述方法
- [ ] 创建盘点状态枚举类`CheckStatus`
  - 定义盘点状态枚举（进行中、已完成、已作废）
  - 提供状态转换方法
  - 提供状态描述方法
- [ ] 配置DTO验证注解
  - 盘点日期非空验证
  - 盘点人ID非空验证
  - 盘点类型非空验证
  - 盘点范围非空验证
- [ ] 创建开发文档

#### 1.2 盘点单Mapper层开发（预计0.5小时）
- [ ] 创建盘点单Mapper接口`InventoryCheckMapper`
  - 继承BaseMapper获得基础CRUD方法
  - 自定义查询方法：
    - 根据盘点单号查询
    - 根据盘点日期查询
    - 根据盘点人ID查询
    - 根据盘点状态查询
    - 根据盘点类型查询
    - 分页查询盘点单列表
    - 统计盘点单数量
  - 配置MyBatis-Plus注解
- [ ] 创建开发文档

#### 1.3 盘点单Service层开发（预计1小时）
- [ ] 创建盘点单Service接口`IInventoryCheckService`
  ```java
  - createInventoryCheck(InventoryCheckCreateDTO dto) - 创建盘点单
  - updateInventoryCheck(InventoryCheckUpdateDTO dto) - 更新盘点单
  - deleteInventoryCheck(Long id) - 删除盘点单
  - getInventoryCheckById(Long id) - 根据ID查询盘点单
  - getInventoryCheckByNo(String checkNo) - 根据单号查询盘点单
  - getInventoryCheckList(PageParam pageParam) - 分页查询盘点单列表
  - startInventoryCheck(Long id) - 开始盘点
  - completeInventoryCheck(Long id) - 完成盘点
  - cancelInventoryCheck(Long id) - 作废盘点单
  - generateCheckNo() - 生成盘点单号
  - generateCheckDetails(Long checkId) - 生成盘点明细
  - calculateCheckStatistics(Long checkId) - 计算盘点统计信息
  ```
- [ ] 创建盘点单Service实现类`InventoryCheckServiceImpl`
  - 实现盘点单CRUD基本功能
  - 实现盘点单号生成逻辑
    - 格式：CHK + yyyyMMdd + 6位流水号
    - 使用Redis分布式锁保证单号唯一性
  - 实现盘点明细生成逻辑
    - 根据盘点范围生成盘点明细
    - 全盘：生成所有耗材的盘点明细
    - 抽盘：根据抽样规则生成盘点明细
    - 专项盘：根据分类或耗材ID生成盘点明细
    - 从库存表获取系统库存数量
  - 实现盘点开始逻辑
    - 验证盘点单状态（必须是待开始状态）
    - 更新盘点单状态为进行中
    - 记录盘点开始时间
  - 实现盘点完成逻辑
    - 验证盘点单状态（必须是进行中状态）
    - 验证所有盘点明细是否已录入
    - 计算盘点统计信息
    - 更新盘点单状态为已完成
    - 记录盘点完成时间
  - 实现盘点作废逻辑
    - 验证盘点单状态（必须是进行中状态）
    - 更新盘点单状态为已作废
    - 记录作废原因
  - 实现盘点统计计算逻辑
    - 统计总项数
    - 统计差异项数
    - 统计盘盈项数
    - 统计盘亏项数
    - 统计盘盈总金额
    - 统计盘亏总金额
  - 添加业务异常处理
  - 添加事务控制
  - 添加详细日志记录
- [ ] 创建开发文档

#### 1.4 盘点单Controller层开发（预计0.5小时）
- [ ] 创建盘点单Controller`InventoryCheckController`
  ```java
  @RestController
  @RequestMapping("/api/inventory-check")
  @Tag(name = "盘点单管理", description = "盘点单管理接口")
  ```
- [ ] 实现以下API接口：
  ```java
  POST   /api/inventory-check                    - 创建盘点单
  PUT    /api/inventory-check/{id}               - 更新盘点单
  DELETE /api/inventory-check/{id}               - 删除盘点单
  GET    /api/inventory-check/{id}               - 获取盘点单详情
  GET    /api/inventory-check/no/{checkNo}       - 根据单号查询盘点单
  GET    /api/inventory-check/list               - 分页查询盘点单列表
  PUT    /api/inventory-check/{id}/start          - 开始盘点
  PUT    /api/inventory-check/{id}/complete       - 完成盘点
  PUT    /api/inventory-check/{id}/cancel         - 作废盘点单
  POST   /api/inventory-check/{id}/generate      - 生成盘点明细
  GET    /api/inventory-check/{id}/statistics    - 获取盘点统计信息
  ```
- [ ] 添加接口文档注解
  - 使用@Tag、@Operation、@Parameter等注解
  - 完善接口说明文档
- [ ] 添加权限控制注解
  - 使用@PreAuthorize控制接口访问权限
  - 例如：@PreAuthorize("hasAuthority('inventory-check:create')")
- [ ] 创建开发文档

### 2. 后端盘点明细管理功能开发（预计2.5小时）

#### 2.1 盘点明细实体类和DTO设计（预计0.5小时）
- [ ] 创建盘点明细实体类`InventoryCheckDetail`
  - 盘点明细ID（主键）
  - 盘点单ID（外键）
  - 耗材ID（外键）
  - 系统库存数量（必填）
  - 实际盘点数量（必填）
  - 差异数量（必填，自动计算）
  - 差异金额（可选，自动计算）
  - 盘点状态（必填：待确认/已确认/已调整）
  - 调整数量（默认0）
  - 调整原因（可选）
  - 调整时间（可选）
  - 调整人ID（可选）
  - 备注信息（可选）
  - 创建时间、更新时间
  - 使用Lombok注解简化代码
  - 配置JPA注解和表映射
- [ ] 创建盘点明细DTO类
  - `InventoryCheckDetailCreateDTO`：盘点明细创建请求
    - 盘点单ID（必填）
    - 耗材ID（必填）
    - 实际盘点数量（必填）
    - 备注信息（可选）
  - `InventoryCheckDetailUpdateDTO`：盘点明细更新请求
    - 盘点明细ID（必填）
    - 实际盘点数量（可选）
    - 盘点状态（可选）
    - 调整数量（可选）
    - 调整原因（可选）
    - 备注信息（可选）
  - `InventoryCheckDetailVO`：盘点明细响应
    - 盘点明细基本信息
    - 耗材信息
    - 盘点单信息
    - 差异信息
    - 调整信息
- [ ] 创建盘点状态枚举类`DetailCheckStatus`
  - 定义盘点状态枚举（待确认、已确认、已调整）
  - 提供状态转换方法
  - 提供状态描述方法
- [ ] 配置DTO验证注解
  - 盘点单ID非空验证
  - 耗材ID非空验证
  - 实际盘点数量非空验证
  - 实际盘点数量范围验证（大于等于0）
- [ ] 创建开发文档

#### 2.2 盘点明细Mapper层开发（预计0.5小时）
- [ ] 创建盘点明细Mapper接口`InventoryCheckDetailMapper`
  - 继承BaseMapper获得基础CRUD方法
  - 自定义查询方法：
    - 根据盘点单ID查询盘点明细列表
    - 根据耗材ID查询盘点明细
    - 根据盘点状态查询盘点明细
    - 根据差异类型查询盘点明细（盘盈/盘亏/无差异）
    - 分页查询盘点明细列表
    - 批量插入盘点明细
    - 批量更新盘点明细
    - 统计盘点明细数量
  - 配置MyBatis-Plus注解
- [ ] 创建开发文档

#### 2.3 盘点明细Service层开发（预计1小时）
- [ ] 创建盘点明细Service接口`IInventoryCheckDetailService`
  ```java
  - createInventoryCheckDetail(InventoryCheckDetailCreateDTO dto) - 创建盘点明细
  - updateInventoryCheckDetail(InventoryCheckDetailUpdateDTO dto) - 更新盘点明细
  - deleteInventoryCheckDetail(Long id) - 删除盘点明细
  - getInventoryCheckDetailById(Long id) - 根据ID查询盘点明细
  - getInventoryCheckDetailList(PageParam pageParam) - 分页查询盘点明细列表
  - getCheckDetailsByCheckId(Long checkId) - 根据盘点单ID查询盘点明细列表
  - batchCreateCheckDetails(List<InventoryCheckDetailCreateDTO> dtos) - 批量创建盘点明细
  - batchUpdateCheckDetails(List<InventoryCheckDetailUpdateDTO> dtos) - 批量更新盘点明细
  - calculateDifference(Long detailId) - 计算差异数量和金额
  - confirmCheckDetail(Long detailId) - 确认盘点明细
  - adjustInventory(Long detailId, Integer adjustQuantity, String adjustReason) - 调整库存
  ```
- [ ] 创建盘点明细Service实现类`InventoryCheckDetailServiceImpl`
  - 实现盘点明细CRUD基本功能
  - 实现差异数量和金额计算逻辑
    - 差异数量 = 实际盘点数量 - 系统库存数量
    - 差异金额 = 差异数量 × 耗材单价
    - 自动更新盘点明细的差异数量和金额
  - 实现盘点明细确认逻辑
    - 验证盘点明细状态（必须是待确认状态）
    - 更新盘点明细状态为已确认
    - 记录确认时间
  - 实现库存调整逻辑
    - 验证盘点明细状态（必须是已确认状态）
    - 验证调整数量合理性
    - 更新库存数量
    - 更新可用库存数量
    - 更新盘点明细状态为已调整
    - 记录调整时间和调整人
    - 记录调整原因
  - 实现批量创建和更新逻辑
    - 使用批量插入提高性能
    - 使用批量更新提高性能
    - 添加事务控制
  - 添加业务异常处理
  - 添加事务控制
  - 添加详细日志记录
- [ ] 创建开发文档

#### 2.4 盘点明细Controller层开发（预计0.5小时）
- [ ] 创建盘点明细Controller`InventoryCheckDetailController`
  ```java
  @RestController
  @RequestMapping("/api/inventory-check-detail")
  @Tag(name = "盘点明细管理", description = "盘点明细管理接口")
  ```
- [ ] 实现以下API接口：
  ```java
  POST   /api/inventory-check-detail                    - 创建盘点明细
  PUT    /api/inventory-check-detail/{id}               - 更新盘点明细
  DELETE /api/inventory-check-detail/{id}               - 删除盘点明细
  GET    /api/inventory-check-detail/{id}               - 获取盘点明细详情
  GET    /api/inventory-check-detail/list               - 分页查询盘点明细列表
  GET    /api/inventory-check-detail/check/{checkId}     - 根据盘点单ID查询盘点明细列表
  POST   /api/inventory-check-detail/batch              - 批量创建盘点明细
  PUT    /api/inventory-check-detail/batch              - 批量更新盘点明细
  PUT    /api/inventory-check-detail/{id}/confirm       - 确认盘点明细
  PUT    /api/inventory-check-detail/{id}/adjust        - 调整库存
  GET    /api/inventory-check-detail/{id}/difference    - 计算差异
  ```
- [ ] 添加接口文档注解
  - 使用@Tag、@Operation、@Parameter等注解
  - 完善接口说明文档
- [ ] 添加权限控制注解
  - 使用@PreAuthorize控制接口访问权限
  - 例如：@PreAuthorize("hasAuthority('inventory-check-detail:create')")
- [ ] 创建开发文档

### 3. 数据库表结构完善（预计1小时）

#### 3.1 盘点单表结构
- [ ] 检查并完善盘点单表`inventory_check`
  - 确认所有字段定义正确
  - 检查索引配置
  - 添加盘点单号唯一索引
  - 添加盘点日期索引
  - 添加盘点人ID索引
  - 添加盘点状态索引
  - 添加盘点类型索引
  - 验证外键约束
- [ ] 检查并完善盘点明细表`inventory_check_detail`
  - 确认所有字段定义正确
  - 检查索引配置
  - 添加盘点单ID索引
  - 添加耗材ID索引
  - 添加盘点状态索引
  - 验证外键约束
- [ ] 执行数据库脚本更新
  - 更新init.sql脚本
  - 执行数据库迁移
- [ ] 创建开发文档

### 4. 前端盘点管理页面开发（预计3小时）

#### 4.1 盘点单列表页面（预计1小时）
- [ ] 创建盘点单列表页面组件`InventoryCheckList.vue`
  - 盘点单列表表格（支持分页）
  - 搜索功能（按盘点单号、盘点日期、盘点人、状态搜索）
  - 状态筛选（进行中/已完成/已作废）
  - 类型筛选（全盘/抽盘/专项盘）
  - 创建盘点单按钮
  - 查看详情按钮
  - 开始盘点按钮（进行中状态）
  - 完成盘点按钮（进行中状态）
  - 作废盘点单按钮（进行中状态）
  - 导出盘点单按钮
- [ ] 配置盘点单列表路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现盘点单列表API调用
  - 获取盘点单列表
  - 创建盘点单
  - 开始盘点
  - 完成盘点
  - 作废盘点单
  - 导出盘点单
- [ ] 创建开发文档

#### 4.2 创建盘点单页面（预计1小时）
- [ ] 创建创建盘点单页面组件`InventoryCheckCreate.vue`
  - 盘点单基本信息表单
    - 盘点日期选择器
    - 盘点人选择器（自动填充当前用户）
    - 盘点类型选择（全盘/抽盘/专项盘）
  - 盘点范围配置
    - 全盘：无需配置
    - 抽盘：配置抽样规则（随机抽样/按分类抽样/按库存量抽样）
    - 专项盘：配置耗材分类或耗材ID
  - 备注信息输入框
  - 表单验证
  - 提交按钮
  - 取消按钮
- [ ] 配置创建盘点单路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现创建盘点单逻辑
  - 调用后端创建盘点单接口
  - 创建成功后跳转到盘点单列表
  - 创建失败显示错误提示
- [ ] 创建开发文档

#### 4.3 盘点明细录入页面（预计1小时）
- [ ] 创建盘点明细录入页面组件`InventoryCheckDetail.vue`
  - 盘点单基本信息展示
  - 盘点明细列表表格
    - 耗材信息
    - 系统库存数量
    - 实际盘点数量输入框
    - 差异数量（自动计算）
    - 差异金额（自动计算）
    - 盘点状态
    - 操作按钮（确认/调整）
  - 批量导入按钮（支持Excel导入）
  - 扫码盘点功能（可选）
  - 保存按钮
  - 提交按钮
  - 盘点进度显示
- [ ] 配置盘点明细录入路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现盘点明细录入逻辑
  - 调用后端获取盘点明细列表
  - 实时计算差异数量和金额
  - 调用后端更新盘点明细接口
  - 调用后端确认盘点明细接口
  - 调用后端调整库存接口
  - 批量导入盘点明细
  - 扫码盘点功能（如实现）
- [ ] 创建开发文档

### 5. 功能测试和联调（预计1.5小时）

#### 5.1 后端接口测试
- [ ] 创建测试类`InventoryCheckServiceTest`
  - 盘点单CRUD功能测试
  - 盘点单号生成测试
  - 盘点明细生成测试
  - 盘点开始/完成/作废测试
  - 盘点统计计算测试
- [ ] 创建测试类`InventoryCheckDetailServiceTest`
  - 盘点明细CRUD功能测试
  - 差异计算测试
  - 盘点明细确认测试
  - 库存调整测试
  - 批量操作测试
- [ ] 创建测试类`InventoryCheckControllerTest`
  - 盘点单接口测试
  - 盘点明细接口测试
  - 盘点统计接口测试
- [ ] 执行测试并验证结果
- [ ] 生成测试覆盖率报告
- [ ] 创建开发文档

#### 5.2 前端功能测试
- [ ] 盘点单列表页面功能测试
  - 盘点单列表显示测试
  - 搜索功能测试
  - 筛选功能测试
  - 创建盘点单功能测试
  - 开始/完成/作废功能测试
- [ ] 创建盘点单页面功能测试
  - 盘点单基本信息表单测试
  - 盘点范围配置测试
  - 表单验证测试
  - 创建盘点单逻辑测试
- [ ] 盘点明细录入页面功能测试
  - 盘点明细列表显示测试
  - 实际盘点数量输入测试
  - 差异自动计算测试
  - 确认盘点明细功能测试
  - 调整库存功能测试
  - 批量导入功能测试
  - 扫码盘点功能测试（如实现）
- [ ] 创建开发文档

#### 5.3 前后端联调测试
- [ ] 盘点流程完整测试
  - 创建盘点单
  - 生成盘点明细
  - 开始盘点
  - 录入实际盘点数量
  - 计算差异
  - 确认盘点明细
  - 调整库存
  - 完成盘点
  - 查看盘点报告
- [ ] 盘点类型测试
  - 全盘测试
  - 抽盘测试
  - 专项盘测试
- [ ] 盘点状态测试
  - 进行中状态测试
  - 已完成状态测试
  - 已作废状态测试
- [ ] 差异处理测试
  - 盘盈处理测试
  - 盘亏处理测试
  - 无差异处理测试
- [ ] 库存调整测试
  - 库存调整准确性测试
  - 库存调整事务测试
- [ ] 并发操作测试
  - 并发盘点测试
  - 并发库存调整测试
- [ ] 异常处理测试
  - 盘点单状态异常测试
  - 库存调整异常测试
  - 网络异常测试
- [ ] 创建开发文档

## 验收标准
- [ ] 盘点单创建功能正常
- [ ] 盘点明细生成功能正常
- [ ] 盘点明细录入功能完整
- [ ] 差异计算准确无误
- [ ] 库存调整准确无误
- [ ] 盘点统计信息正确
- [ ] 盘点报告生成正常
- [ ] 所有接口都有相应权限控制
- [ ] 前后端联调测试通过
- [ ] 测试覆盖率 > 80%

## 技术要点
- 盘点单号自动生成（CHK + yyyyMMdd + 6位流水号）
- Redis分布式锁保证单号唯一性
- 盘点范围配置（全盘/抽盘/专项盘）
- 盘点明细批量生成
- 差异数量和金额自动计算
- 库存调整事务控制
- 盘点统计信息计算
- Vue 3 Composition API
- Element Plus组件库
- TypeScript类型安全
- 前端表单验证
- 前端状态管理
- Excel导入导出

## 注意事项
- 盘点单号必须唯一
- 盘点明细生成时需要从库存表获取系统库存数量
- 差异数量和金额需要自动计算
- 库存调整必须在确认盘点明细后进行
- 库存调整需要事务控制
- 盘点完成后不能再修改盘点明细
- 盘点作废后不能进行任何操作
- 前端需要实时计算差异数量和金额
- 前端需要提供友好的错误提示
- 批量导入需要验证数据格式

## 预计完成时间
18:00 - 库存盘点模块开发完成并测试通过

## 风险评估
- 盘点单号生成可能存在并发问题
- 盘点明细生成逻辑复杂，容易出错
- 差异计算逻辑需要准确
- 库存调整逻辑需要准确
- 并发盘点可能导致数据不一致
- 前端表单验证需要完善
- 异常处理需要全面

## 依赖检查
- 确保盘点单表结构正确
- 确保盘点明细表结构正确
- 确保库存表结构正确
- 确保耗材表结构正确
- 确保Redis配置正确
- 确保前端路由配置正确
- 确保前端API调用正确

## 开发流程规范
1. **开发前准备**
   - 阅读相关文档和代码
   - 理解业务逻辑和技术要点
   - 准备测试数据

2. **开发过程**
   - 按照任务清单逐步开发
   - 每完成一个功能立即测试
   - 遇到问题及时记录和解决
   - 代码提交前务必测试通过

3. **测试要求**
   - 本地测试：所有功能在本地进行完整测试
   - 自动化测试：编写自动化测试脚本
   - 推送前验证：确保本地测试全部通过
   - 部署测试：代码推送后触发CI流程

4. **文档要求**
   - 开发记录：记录实现思路、关键代码变更、依赖调整
   - 测试记录：记录测试工具、测试方法、测试结果
   - 文档更新：同步更新技术文档、API文档

5. **质量要求**
   - 代码审查：所有合并请求需经过审查
   - 测试覆盖率：单元测试覆盖率 > 80%
   - 性能要求：接口响应时间 < 2秒

## 相关文档
- docs/common/plan.md - 系统整体规划
- docs/day8/day8-plan.md - 库存管理基础功能
- docs/day10/day10-plan.md - 入库管理模块开发
- docs/day11/day11-plan.md - 出库管理模块开发（上）
- docs/day12/day12-plan.md - 出库管理模块开发（下）
