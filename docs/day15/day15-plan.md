# 第十五天工作计划：统计报表模块开发

## 日期：2026年1月20日

## 总体目标
完成统计报表模块的开发，实现入库统计、出库统计、库存统计、综合报表等功能，建立完整的统计报表系统，为管理决策提供数据支持。

## 详细工作任务

### 1. 后端统计报表接口开发（预计5小时）

#### 1.1 入库统计接口开发（预计1.5小时）
- [ ] 创建入库统计Service接口 `IInboundStatisticsService`
  - `getInboundStatisticsByDate(StatisticsQueryDTO dto)` - 按时间统计入库
  - `getInboundStatisticsByCategory(StatisticsQueryDTO dto)` - 按分类统计入库
  - `getInboundStatisticsBySupplier(StatisticsQueryDTO dto)` - 按供应商统计入库
  - `getInboundStatisticsByHandler(StatisticsQueryDTO dto)` - 按经办人统计入库
  - `getInboundTrendData(StatisticsQueryDTO dto)` - 入库趋势分析
  - `getInboundComparisonData(StatisticsQueryDTO dto)` - 入库对比分析（同比/环比）

- [ ] 创建入库统计Service实现类 `InboundStatisticsServiceImpl`
  - 实现按时间统计逻辑（日/周/月/年）
  - 实现按分类统计逻辑（树形结构）
  - 实现按供应商统计逻辑
  - 实现按经办人统计逻辑
  - 实现趋势分析逻辑（近7天/30天/90天/1年）
  - 实现对比分析逻辑（同比/环比）
  - 添加业务异常处理
  - 添加详细日志记录

- [ ] 创建入库统计Controller `InboundStatisticsController`
  - `GET /api/statistics/inbound/by-date` - 按时间统计入库
  - `GET /api/statistics/inbound/by-category` - 按分类统计入库
  - `GET /api/statistics/inbound/by-supplier` - 按供应商统计入库
  - `GET /api/statistics/inbound/by-handler` - 按经办人统计入库
  - `GET /api/statistics/inbound/trend` - 入库趋势分析
  - `GET /api/statistics/inbound/comparison` - 入库对比分析

- [ ] 创建入库统计DTO类
  - `StatisticsQueryDTO` - 统计查询参数（日期范围、分类ID、供应商ID等）
  - `InboundStatisticsVO` - 入库统计结果（数量、金额、占比等）
  - `TrendDataVO` - 趋势数据（日期、数量、金额）
  - `ComparisonDataVO` - 对比数据（本期、上期、增长率）

#### 1.2 出库统计接口开发（预计1.5小时）
- [ ] 创建出库统计Service接口 `IOutboundStatisticsService`
  - `getOutboundStatisticsByDate(StatisticsQueryDTO dto)` - 按时间统计出库
  - `getOutboundStatisticsByUsageType(StatisticsQueryDTO dto)` - 按用途分类统计出库
  - `getOutboundStatisticsByCourse(StatisticsQueryDTO dto)` - 按课程统计出库（教学用）
  - `getOutboundStatisticsByProject(StatisticsQueryDTO dto)` - 按项目统计出库（科研用）
  - `getOutboundStatisticsByCompetition(StatisticsQueryDTO dto)` - 按竞赛统计出库（竞赛用）
  - `getOutboundStatisticsByApplicant(StatisticsQueryDTO dto)` - 按领用人统计出库
  - `getOutboundStatisticsByDepartment(StatisticsQueryDTO dto)` - 按部门统计出库
  - `getOutboundTrendData(StatisticsQueryDTO dto)` - 出库趋势分析
  - `getOutboundComparisonData(StatisticsQueryDTO dto)` - 出库对比分析

- [ ] 创建出库统计Service实现类 `OutboundStatisticsServiceImpl`
  - 实现按时间统计逻辑
  - 实现按用途分类统计逻辑（教学/科研/竞赛/其他）
  - 实现按课程统计逻辑（关联课程信息）
  - 实现按项目统计逻辑（关联项目信息）
  - 实现按竞赛统计逻辑（关联竞赛信息）
  - 实现按领用人统计逻辑
  - 实现按部门统计逻辑
  - 实现趋势分析逻辑
  - 实现对比分析逻辑
  - 添加业务异常处理
  - 添加详细日志记录

- [ ] 创建出库统计Controller `OutboundStatisticsController`
  - `GET /api/statistics/outbound/by-date` - 按时间统计出库
  - `GET /api/statistics/outbound/by-usage-type` - 按用途分类统计出库
  - `GET /api/statistics/outbound/by-course` - 按课程统计出库
  - `GET /api/statistics/outbound/by-project` - 按项目统计出库
  - `GET /api/statistics/outbound/by-competition` - 按竞赛统计出库
  - `GET /api/statistics/outbound/by-applicant` - 按领用人统计出库
  - `GET /api/statistics/outbound/by-department` - 按部门统计出库
  - `GET /api/statistics/outbound/trend` - 出库趋势分析
  - `GET /api/statistics/outbound/comparison` - 出库对比分析

- [ ] 创建出库统计DTO类
  - `OutboundStatisticsVO` - 出库统计结果
  - `UsageTypeStatisticsVO` - 用途分类统计结果
  - `CourseStatisticsVO` - 课程统计结果
  - `ProjectStatisticsVO` - 项目统计结果
  - `CompetitionStatisticsVO` - 竞赛统计结果

#### 1.3 库存统计接口开发（预计1小时）
- [ ] 创建库存统计Service接口 `IInventoryStatisticsService`
  - `getInventoryTurnoverRate(StatisticsQueryDTO dto)` - 库存周转率分析
  - `getInventoryValue(StatisticsQueryDTO dto)` - 库存价值分析
  - `getSlowMovingMaterials(StatisticsQueryDTO dto)` - 滞销耗材分析
  - `getInventoryStructure(StatisticsQueryDTO dto)` - 库存结构分析
  - `getInventoryWarningStatistics(StatisticsQueryDTO dto)` - 库存预警统计

- [ ] 创建库存统计Service实现类 `InventoryStatisticsServiceImpl`
  - 实现库存周转率计算逻辑（出库数量/平均库存）
  - 实现库存价值计算逻辑（库存数量 * 单价）
  - 实现滞销耗材分析逻辑（长期未出库）
  - 实现库存结构分析逻辑（按分类占比）
  - 实现库存预警统计逻辑（低库存、临期）
  - 添加业务异常处理
  - 添加详细日志记录

- [ ] 创建库存统计Controller `InventoryStatisticsController`
  - `GET /api/statistics/inventory/turnover-rate` - 库存周转率分析
  - `GET /api/statistics/inventory/value` - 库存价值分析
  - `GET /api/statistics/inventory/slow-moving` - 滞销耗材分析
  - `GET /api/statistics/inventory/structure` - 库存结构分析
  - `GET /api/statistics/inventory/warning` - 库存预警统计

- [ ] 创建库存统计DTO类
  - `InventoryTurnoverRateVO` - 库存周转率结果
  - `InventoryValueVO` - 库存价值结果
  - `SlowMovingMaterialVO` - 滞销耗材结果
  - `InventoryStructureVO` - 库存结构结果
  - `InventoryWarningVO` - 库存预警结果

#### 1.4 数据导出接口开发（预计1小时）
- [ ] 创建数据导出Service接口 `IDataExportService`
  - `exportInboundStatistics(StatisticsQueryDTO dto)` - 导出入库统计数据
  - `exportOutboundStatistics(StatisticsQueryDTO dto)` - 导出出库统计数据
  - `exportInventoryStatistics(StatisticsQueryDTO dto)` - 导出库存统计数据
  - `getExportProgress(String taskId)` - 获取导出进度
  - `cancelExport(String taskId)` - 取消导出任务

- [ ] 创建数据导出Service实现类 `DataExportServiceImpl`
  - 使用EasyExcel实现Excel导出
  - 实现大数据量分批次导出（避免内存溢出）
  - 实现异步导出（使用@Async）
  - 实现导出进度跟踪（Redis存储）
  - 实现导出任务管理（创建、取消、查询）
  - 添加业务异常处理
  - 添加详细日志记录

- [ ] 创建数据导出Controller `DataExportController`
  - `POST /api/statistics/export/inbound` - 导出入库统计数据
  - `POST /api/statistics/export/outbound` - 导出出库统计数据
  - `POST /api/statistics/export/inventory` - 导出库存统计数据
  - `GET /api/statistics/export/progress/{taskId}` - 获取导出进度
  - `DELETE /api/statistics/export/cancel/{taskId}` - 取消导出任务
  - `GET /api/statistics/export/download/{taskId}` - 下载导出文件

- [ ] 创建数据导出DTO类
  - `ExportTaskVO` - 导出任务信息
  - `ExportProgressVO` - 导出进度信息

### 2. 数据库表结构完善（预计1小时）

#### 2.1 统计报表相关表结构
- [ ] 检查并完善统计相关表
  - 确认入库单表（inbound_order）字段完整 ✅
  - 确认出库单表（outbound_order）字段完整 ✅
  - 确认库存表（material_inventory）字段完整 ✅
  - 确认耗材信息表（material_info）字段完整 ✅
  - 检查索引配置（日期、分类、供应商等字段）
  - 验证外键约束

#### 2.2 添加导出任务表
- [ ] 创建导出任务表 `export_task`
  - 任务ID、任务类型、任务状态、创建时间、完成时间
  - 导出参数（JSON格式）
  - 导出文件路径
  - 导出进度、总记录数、已处理记录数
  - 创建人ID、创建人姓名
- [ ] 执行数据库脚本更新
  - 更新init.sql脚本
  - 执行数据库迁移
- [ ] 创建开发文档

### 3. 前端统计报表页面开发（预计4小时）

#### 3.1 入库统计页面开发（预计1小时）
- [ ] 创建入库统计页面 `InboundStatistics.vue`
  - 统计条件筛选（日期范围、分类、供应商、经办人）
  - 统计结果展示（卡片、表格）
  - 图表展示（柱状图、折线图、饼图）
  - 导出Excel按钮
  - 数据刷新按钮
- [ ] 配置入库统计路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现入库统计API调用
  - 按时间统计
  - 按分类统计
  - 按供应商统计
  - 按经办人统计
  - 趋势分析
  - 对比分析
  - 数据导出

#### 3.2 出库统计页面开发（预计1.5小时）
- [ ] 创建出库统计页面 `OutboundStatistics.vue`
  - 统计条件筛选（日期范围、用途类型、课程、项目、竞赛）
  - 统计结果展示（按用途分类）
  - 图表展示（堆叠柱状图、扇形图、雷达图）
  - 导出Excel按钮
  - 数据刷新按钮
- [ ] 配置出库统计路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现出库统计API调用
  - 按时间统计
  - 按用途分类统计
  - 按课程统计
  - 按项目统计
  - 按竞赛统计
  - 按领用人统计
  - 按部门统计
  - 趋势分析
  - 对比分析
  - 数据导出

#### 3.3 库存统计页面开发（预计1小时）
- [ ] 创建库存统计页面 `InventoryStatistics.vue`
  - 库存周转率展示（表格、趋势图）
  - 库存价值分析（卡片、饼图）
  - 滞销耗材列表（表格、可操作）
  - 库存结构分析（树形图、饼图）
  - 库存预警统计（列表、图表）
  - 导出Excel按钮
  - 数据刷新按钮
- [ ] 配置库存统计路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现库存统计API调用
  - 库存周转率分析
  - 库存价值分析
  - 滞销耗材分析
  - 库存结构分析
  - 库存预警统计
  - 数据导出

#### 3.4 综合报表页面开发（预计0.5小时）
- [ ] 创建综合报表页面 `ComprehensiveReport.vue`
  - 自定义报表生成器（拖拽式）
  - 报表模板管理
  - 报表定时生成配置
  - 报表推送设置（邮件、消息）
  - 报表历史记录
- [ ] 配置综合报表路由
  - 添加到路由配置中
  - 配置页面权限
- [ ] 实现综合报表API调用
  - 创建自定义报表
  - 保存报表模板
  - 配置定时任务
  - 查询报表历史

### 4. 功能测试和联调（预计1.5小时）

#### 4.1 后端接口测试
- [ ] 创建测试类InboundStatisticsServiceTest
  - 按时间统计测试
  - 按分类统计测试
  - 按供应商统计测试
  - 趋势分析测试
  - 对比分析测试
- [ ] 创建测试类OutboundStatisticsServiceTest
  - 按时间统计测试
  - 按用途分类统计测试
  - 按课程统计测试
  - 按项目统计测试
  - 按竞赛统计测试
- [ ] 创建测试类InventoryStatisticsServiceTest
  - 库存周转率测试
  - 库存价值测试
  - 滞销耗材测试
  - 库存结构测试
- [ ] 创建测试类DataExportServiceTest
  - 导出功能测试
  - 异步导出测试
  - 大数据量导出测试
  - 导出进度测试
- [ ] 执行测试并验证结果
- [ ] 生成测试覆盖率报告

#### 4.2 前端功能测试
- [ ] 入库统计页面功能测试
- [ ] 出库统计页面功能测试
- [ ] 库存统计页面功能测试
- [ ] 综合报表页面功能测试
- [ ] 图表展示测试
- [ ] 数据导出测试

#### 4.3 前后端联调测试
- [ ] 统计数据准确性测试（与数据库对比）
- [ ] 统计条件筛选测试（多条件组合）
- [ ] 数据导出功能测试（不同格式、不同数据量）
- [ ] 图表展示测试（数据正确、样式美观）
- [ ] 大数据量性能测试（万级数据）
- [ ] 并发导出测试（多个用户同时导出）
- [ ] 接口性能测试（响应时间<2秒）
- [ ] 前端页面功能测试
- [ ] 前后端联调测试

## 验收标准
- [ ] 统计数据准确（数据一致性）
- [ ] 筛选功能正常（多条件组合查询）
- [ ] 导出功能完整（格式正确、数据完整）
- [ ] 图表展示正确（数据可视化清晰）
- [ ] 大数据量导出性能达标（<30秒）
- [ ] 所有测试用例通过
- [ ] 接口响应时间 < 2秒
- [ ] 前端页面加载流畅

## 技术要点
- EasyExcel数据导出
- 异步任务处理（@Async）
- Redis缓存优化
- SQL性能优化（索引、查询优化）
- ECharts图表可视化
- 大数据量分批次处理
- 导出进度跟踪
- 定时任务调度
- 邮件推送
- 消息推送

## 注意事项
- 统计查询要优化SQL性能，避免慢查询
- 大数据量导出要分批次处理，避免内存溢出
- 导出任务要异步处理，避免阻塞主线程
- 图表数据要合理聚合，避免前端渲染压力
- 统计数据要缓存，避免重复计算
- 导出文件要定期清理，避免磁盘空间不足
- 统计条件要灵活，支持多维度组合查询
- 图表要美观，符合用户习惯

## 预计完成时间
18:00 - 统计报表模块开发完成并测试通过

## 风险评估
- 统计查询性能可能不达标
- 大数据量导出可能导致内存溢出
- 图表渲染可能影响前端性能
- 统计数据准确性可能存在问题
- 导出文件格式可能不符合用户需求

## 依赖检查
- 确保EasyExcel依赖正确配置
- 确保ECharts依赖正确配置
- 确保异步任务配置正确
- 确保Redis缓存配置正确
- 确保数据库索引配置正确
- 确保定时任务配置正确

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
