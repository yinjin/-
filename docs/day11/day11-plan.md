# 第十一天工作计划：出库管理模块开发（上）

## 日期：2026年1月16日

## 总体目标
完成出库管理模块上半部分的开发，实现出库单的创建、领用申请、审批等核心功能，建立完整的出库申请和审批流程。

## 详细工作任务

### 1. 后端出库管理模块开发（预计5小时）

#### 1.1 出库单实体类设计（预计1小时）
- [ ] 创建出库单实体类 `OutboundOrder`
  - 出库单基本信息：id、orderNo（出库单号）、outboundDate（出库日期）、applicantId（申请人ID）、applicantDeptId（申请人部门ID）
  - 出库单审批字段：handlerId（处理人ID）、approverId（审批人ID）、approvalTime（审批时间）、approvalOpinion（审批意见）
  - 出库单用途字段：usageType（用途类型：教学/科研/竞赛/其他）、usageDetail（用途详情JSON格式）
  - 出库单归还字段：expectedReturnDate（预计归还日期）、actualReturnDate（实际归还日期）
  - 出库单状态字段：status（待审批/已批准/已拒绝/待出库/已出库/已归还/已损坏/已丢失）
  - 出库单统计字段：totalQuantity（总数量）、totalAmount（总金额）
  - 出库单其他字段：remark（备注）、createTime、updateTime
  - 使用Lombok注解简化代码
  - 配置JPA注解和表映射
- [ ] 创建出库明细实体类 `OutboundDetail`
  - 出库明细基本信息：id、outboundOrderId（出库单ID）、materialId（耗材ID）、quantity（数量）、unitPrice（单价）、totalPrice（总价）
  - 出库明细归还字段：returnQuantity（已归还数量）、returnStatus（归还状态：未归还/部分归还/已归还/已损坏/已丢失）
  - 出库明细其他字段：createTime、updateTime
  - 配置与出库单的关联关系（@ManyToOne）
  - 配置与耗材的关联关系（@ManyToOne）
- [ ] 创建归还记录实体类 `ReturnRecord`
  - 归还记录基本信息：id、outboundDetailId（出库明细ID）、returnDate（归还日期）、returnQuantity（归还数量）
  - 归还记录状态字段：returnStatus（归还状态：完好/损坏/丢失）、damageDescription（损坏描述）
  - 归还记录其他字段：handlerId（处理人ID）、remark（备注）、createTime
  - 配置与出库明细的关联关系（@ManyToOne）
- [ ] 创建出库单DTO类
  - `OutboundOrderCreateDTO`：创建出库单请求
  - `OutboundOrderUpdateDTO`：更新出库单请求
  - `OutboundOrderQueryDTO`：查询出库单请求
  - `OutboundDetailCreateDTO`：创建出库明细请求
  - `OutboundDetailUpdateDTO`：更新出库明细请求
  - `ReturnRecordCreateDTO`：创建归还记录请求
- [ ] 创建出库单VO类
  - `OutboundOrderVO`：出库单信息响应
  - `OutboundDetailVO`：出库明细信息响应
  - `ReturnRecordVO`：归还记录信息响应
  - `OutboundOrderDetailVO`：出库单详情响应（包含明细列表）
- [ ] 配置实体类验证注解
  - 出库单号格式验证
  - 出库日期验证
  - 用途类型验证
  - 数量和金额验证
- [ ] 创建OutboundStatus枚举类
  - 定义出库单状态枚举（PENDING_APPROVAL、APPROVED、REJECTED、PENDING_OUTBOUND、OUTBOUND、RETURNED、DAMAGED、LOST）
  - 提供状态转换方法
- [ ] 创建UsageType枚举类
  - 定义用途类型枚举（TEACHING、RESEARCH、COMPETITION、OTHER）
  - 提供类型描述方法
- [ ] 创建ReturnStatus枚举类
  - 定义归还状态枚举（NOT_RETURNED、PARTIAL_RETURNED、RETURNED、DAMAGED、LOST）
  - 提供状态转换方法
- [ ] 编译测试通过
- [ ] 创建开发教程文档

#### 1.2 出库单数据访问层（预计1小时）
- [ ] 创建出库单Mapper接口 `OutboundOrderMapper`
  - 继承BaseMapper获得基础CRUD方法
  - 自定义查询方法：根据单号查询、根据状态查询、分页查询、根据申请人查询、根据用途类型查询
  - 配置MyBatis-Plus注解
- [ ] 创建出库明细Mapper接口 `OutboundDetailMapper`
  - 继承BaseMapper获得基础CRUD方法
  - 自定义查询方法：根据出库单ID查询明细、根据耗材ID查询、分页查询
  - 配置MyBatis-Plus注解
- [ ] 创建归还记录Mapper接口 `ReturnRecordMapper`
  - 继承BaseMapper获得基础CRUD方法
  - 自定义查询方法：根据出库明细ID查询、根据归还日期查询、分页查询
  - 配置MyBatis-Plus注解
- [ ] 配置数据访问层异常处理
  - 创建出库单不存在异常
  - 创建出库单状态异常
  - 创建库存不足异常
  - 创建归还数量异常
- [ ] 编译测试通过
- [ ] 创建开发教程文档

#### 1.3 出库单业务逻辑层（预计2小时）
- [ ] 创建出库单Service接口 `IOutboundOrderService`
  - 定义出库单创建、审批、查询等业务方法接口
- [ ] 创建出库单Service实现类 `OutboundOrderServiceImpl`
  - 出库单创建逻辑（单号自动生成、状态初始化、明细添加、用途信息验证）
  - 出库单查询逻辑（分页、搜索、筛选、详情查询）
  - 出库单审批逻辑（状态更新、库存检查、审批记录）
  - 出库单拒绝逻辑（状态更新、拒绝原因记录）
  - 出库单删除逻辑（状态检查、关联检查）
  - 出库单统计逻辑（按时间、用途、申请人统计）
- [ ] 创建出库明细Service接口 `IOutboundDetailService`
  - 定义出库明细CRUD方法接口
- [ ] 创建出库明细Service实现类 `OutboundDetailServiceImpl`
  - 出库明细CRUD基本功能
  - 出库明细批量添加功能
  - 出库明细查询功能
  - 出库明细归还数量更新功能
- [ ] 创建归还记录Service接口 `IReturnRecordService`
  - 定义归还记录CRUD方法接口
- [ ] 创建归还记录Service实现类 `ReturnRecordServiceImpl`
  - 归还记录CRUD基本功能
  - 归还登记逻辑（归还状态更新、库存回增）
  - 归还记录查询功能
- [ ] 出库单号自动生成
  - 实现单号生成规则（OUT + yyyyMMdd + 6位流水号）
  - 使用Redis分布式锁保证单号唯一性
- [ ] 用途信息验证逻辑
  - 教学用途：验证课程名称、班级、授课教师、学期信息
  - 科研用途：验证科研项目名称、负责人、项目编号、项目周期信息
  - 竞赛用途：验证竞赛名称、参赛团队、指导教师、竞赛时间信息
  - 其他用途：验证具体用途说明
- [ ] 出库审批流程实现
  - 审批通过后检查库存是否充足
  - 审批通过后更新出库单状态为"待出库"
  - 使用事务控制保证数据一致性
  - 记录审批日志
- [ ] 归还登记逻辑
  - 归还完好时更新库存数量
  - 归还损坏或丢失时不更新库存
  - 更新出库明细的归还数量和归还状态
  - 更新出库单的实际归还日期
- [ ] 编译测试通过
- [ ] 创建开发教程文档

#### 1.4 出库单控制层（预计1小时）
- [ ] 创建出库单Controller `OutboundController`
  - 创建出库单接口 `POST /api/outbound`
  - 更新出库单接口 `PUT /api/outbound/{id}`
  - 删除出库单接口 `DELETE /api/outbound/{id}`
  - 获取出库单详情接口 `GET /api/outbound/{id}`
  - 分页查询出库单列表接口 `GET /api/outbound/list`
  - 审批出库单接口 `PUT /api/outbound/{id}/approve`
  - 拒绝出库单接口 `PUT /api/outbound/{id}/reject`
  - 出库单统计接口 `GET /api/outbound/statistics`
  - 导出出库单接口 `GET /api/outbound/export`
  - 待审批出库单列表接口 `GET /api/outbound/pending-approval`
- [ ] 创建出库明细Controller `OutboundDetailController`
  - 创建出库明细接口 `POST /api/outbound-detail`
  - 更新出库明细接口 `PUT /api/outbound-detail/{id}`
  - 删除出库明细接口 `DELETE /api/outbound-detail/{id}`
  - 获取出库明细列表接口 `GET /api/outbound-detail/list`
- [ ] 创建归还记录Controller `ReturnRecordController`
  - 创建归还记录接口 `POST /api/return`
  - 更新归还记录接口 `PUT /api/return/{id}`
  - 获取归还记录列表接口 `GET /api/return/list`
  - 待归还列表接口 `GET /api/return/pending`
  - 逾期未归还列表接口 `GET /api/return/overdue`
- [ ] 配置接口权限注解
  - 创建出库单权限：outbound:create、outbound:update、outbound:delete、outbound:query、outbound:approve、outbound:out
  - 审批接口需要审批权限
  - 其他接口需要相应权限
- [ ] 集成Swagger API文档
- [ ] 参数验证和异常处理
- [ ] 编译测试通过

### 2. 数据库表结构完善（预计1小时）

#### 2.1 出库相关表结构
- [ ] 检查并完善出库单表 `outbound_order`
  - 确认所有字段定义正确
  - 检查索引配置（order_no、status、applicant_id、usage_type、expected_return_date）
  - 验证外键约束（applicant_id、applicant_dept_id、handler_id、approver_id）
- [ ] 检查并完善出库明细表 `outbound_detail`
  - 确认所有字段定义正确
  - 检查索引配置（outbound_order_id、material_id、return_status）
  - 验证外键约束（outbound_order_id、material_id）
- [ ] 检查并完善归还记录表 `return_record`
  - 确认所有字段定义正确
  - 检查索引配置（outbound_detail_id、return_date、return_status）
  - 验证外键约束（outbound_detail_id、handler_id）
- [ ] 执行数据库脚本更新
  - 更新init.sql脚本
  - 执行数据库迁移
- [ ] 创建开发文档

### 3. 前端出库管理页面开发（预计3小时）

#### 3.1 出库单列表页面开发（预计1小时）
- [ ] 创建出库单列表页面 `OutboundList.vue`
  - 出库单表格展示（支持分页）
  - 状态筛选（待审批、已批准、已拒绝、待出库、已出库、已归还）
  - 用途分类筛选（教学/科研/竞赛/其他）
  - 搜索功能（按单号、申请人、课程/项目名称搜索）
  - 查看详情功能
  - 审批功能（待审批出库单）
  - 拒绝功能（待审批出库单）
  - 删除功能（未审批出库单）
  - 导出功能
- [ ] 配置出库单列表路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现出库单列表API调用
  - 获取出库单列表
  - 审批出库单
  - 拒绝出库单
  - 删除出库单
  - 导出出库单

#### 3.2 领用申请页面开发（预计1.5小时）
- [ ] 创建领用申请页面 `OutboundApply.vue`
  - 申请人信息自动填充
  - 耗材选择和数量填写（库存提示）
  - 用途分类选择（教学/科研/竞赛/其他）
  - 动态用途详情表单（根据用途类型显示不同字段）
    - 教学用：课程名称、班级、授课教师、学期
    - 科研用：科研项目名称、负责人、项目编号、项目周期
    - 竞赛用：竞赛名称、参赛团队、指导教师、竞赛时间
    - 其他：具体用途说明
  - 预计归还日期（必填/可选根据耗材类型）
  - 附件上传（支持多种格式）
  - 表单验证（完整性、合法性）
  - 提交申请
- [ ] 配置领用申请路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现领用申请API调用
  - 创建出库单
  - 添加出库明细
  - 提交申请

#### 3.3 出库审批页面开发（预计0.5小时）
- [ ] 创建出库审批页面 `OutboundApprove.vue`
  - 待审批出库单列表
  - 审批详情查看（包括附件预览）
  - 审批操作（批准/拒绝）
  - 审批意见填写
  - 审批记录查看
- [ ] 配置出库审批路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现出库审批API调用
  - 获取待审批出库单列表
  - 审批出库单
  - 拒绝出库单

### 4. 功能测试和联调（预计1.5小时）

#### 4.1 后端接口测试
- [ ] 创建测试类OutboundOrderServiceTest
  - 出库单创建测试
  - 出库单审批测试
  - 出库单拒绝测试
  - 出库单查询测试
  - 出库单统计测试
  - 用途信息验证测试
  - 事务回滚测试
- [ ] 创建测试类OutboundControllerTest
  - 创建出库单接口测试
  - 审批出库单接口测试
  - 拒绝出库单接口测试
  - 查询出库单接口测试
  - 导出出库单接口测试
- [ ] 执行测试并验证结果
- [ ] 生成测试覆盖率报告

#### 4.2 前端功能测试
- [ ] 出库单列表页面功能测试
- [ ] 领用申请页面功能测试
- [ ] 出库审批页面功能测试
- [ ] 审批流程测试

#### 4.3 前后端联调测试
- [ ] 领用申请流程完整测试（四种用途类型）
- [ ] 出库审批流程测试
- [ ] 用途信息验证测试
- [ ] 异常处理测试

## 验收标准
- [ ] 出库单能正常创建
- [ ] 出库单能正常审批
- [ ] 用途信息验证正确
- [ ] 出库单能正常查询和筛选
- [ ] 出库单能正常导出
- [ ] 出库单号自动生成正确
- [ ] 所有接口都有相应权限控制
- [ ] 前后端联调测试通过
- [ ] 测试覆盖率 > 80%

## 技术要点
- 出库单号自动生成（OUT + yyyyMMdd + 6位流水号）
- Redis分布式锁保证单号唯一性
- 出库审批流程实现
- 用途信息验证逻辑
- 事务控制保证数据一致性
- MyBatis-Plus数据访问
- Vue 3 Composition API
- Element Plus组件库
- TypeScript类型安全

## 注意事项
- 出库单号必须唯一，使用分布式锁保证
- 审批通过后必须检查库存是否充足
- 删除出库单前要检查状态，已审批的不能删除
- 用途信息必须完整验证
- 前端需要处理审批状态的变化
- 数据库字段长度要合理设置
- 接口要做好参数验证和错误处理

## 预计完成时间
18:00 - 出库管理模块上半部分开发完成并测试通过

## 风险评估
- 出库单号生成算法不当可能导致重复
- 审批流程事务控制不当可能导致数据不一致
- 用途信息验证逻辑错误可能导致数据错误
- 前端状态管理不当可能导致用户体验问题
- 数据库设计不合理影响后续扩展

## 依赖检查
- 确保耗材管理模块已完成
- 确保库存管理模块已完成
- 确保用户管理模块已完成
- 确保部门管理模块已完成
- 确保权限控制模块已完成
- 确保Redis配置正确
- 确保数据库表结构与实体类匹配

## 开发流程规范

### 一、开发模式与流程
1. **采用持续集成（CI）开发模式**，确保代码持续集成、自动化构建与测试。
2. **功能驱动开发**：每个功能模块开发完成后，必须经过充分测试与验证，确保无误后方可进入下一功能开发。
3. **迭代推进**：以功能为单位进行迭代，保证每个模块稳定可用。

### 二、测试策略
1. **本地测试优先**：
   - 所有功能需在本地进行完整测试，包括单元测试、集成测试及界面交互测试。
   - 需使用与CI环境一致的测试工具和依赖版本。
2. **自动化测试**：
   - 针对关键路径与核心功能编写自动化测试脚本。
   - 每次提交前应在本地运行自动化测试套件。
3. **推送前验证**：
   - 确保本地测试全部通过后，才可将代码推送至GitHub。
4. **部署测试**：
   - 代码推送后触发CI流程，执行部署与线上环境测试，确保功能在目标环境中正常运行。

### 三、版本控制与部署
1. **分支管理**：采用功能分支策略，每个新功能在独立分支开发，通过Pull Request合并至主分支。
2. **CI/CD流水线**：
   - 利用GitHub Actions等工具实现自动化测试与部署。
   - 部署成功后需在测试环境进行验证。
3. **回滚机制**：确保发现严重问题时能快速回退至上一稳定版本。

### 四、过程记录与文档
1. **开发记录**：每个功能需记录：
   - 实现思路、关键代码变更、依赖调整。
   - 开发过程中遇到的问题与解决方案。
2. **测试记录**：
   - 测试工具、框架及版本。
   - 测试方法、用例设计、测试数据。
   - 测试结果（通过/失败）、截图或日志摘要。
   - 发现的缺陷及其修复状态。
3. **文档更新**：
   - 同步更新技术文档、API文档及用户手册。
   - 记录配置变更、环境依赖及部署步骤。

### 五、质量与协作要求
1. **代码审查**：所有合并请求需经过至少一名成员审查。
2. **持续反馈**：定期同步开发与测试进展，及时调整计划。
3. **责任到人**：每个功能由开发者主导测试并确保质量，测试记录归档至项目Wiki或指定文档库。

通过以上规范，旨在形成**开发→测试→记录→集成**的闭环流程，确保交付质量，并为后续维护、BUG修复与优化提供完整依据。
