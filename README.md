# 高职人工智能学院实训耗材管理系统

## 项目简介

高职人工智能学院实训耗材管理系统是一个基于Spring Boot + Vue 3的企业级耗材管理解决方案，旨在实现耗材从采购入库到使用归还的全生命周期数字化管理。

## 主要功能

- **基础管理**：用户管理、角色权限、部门管理、耗材分类
- **入库管理**：入库单创建、审核、库存更新
- **出库管理**：领用申请、审批、出库、归还
- **库存管理**：实时库存查询、预警、盘点
- **数据大屏**：可视化数据展示、统计报表
- **系统安全**：JWT认证、RBAC权限控制

## 技术栈

### 后端技术栈
- **框架**：Spring Boot 3.2.2
- **ORM**：MyBatis-Plus 3.5.5 + Spring Data JPA
- **数据库**：MySQL 8.0.35
- **缓存**：Redis 7.2.4
- **安全**：Spring Security 6.2 + JWT
- **文档**：SpringDoc OpenAPI 2.3.0

### 前端技术栈
- **框架**：Vue 3.4.15 + TypeScript 5.3.3
- **构建工具**：Vite 5.0.12
- **UI组件**：Element Plus 2.5.4
- **状态管理**：Pinia 2.1.7
- **路由**：Vue Router 4.2.5
- **HTTP客户端**：Axios 1.6.5
- **图表**：ECharts 5.5.0

### 开发工具
- **JDK**：17
- **Node.js**：20 LTS
- **构建工具**：Maven 3.9+
- **IDE**：IntelliJ IDEA 2023.3+ / VS Code 1.85+

## 快速开始

### 环境要求
- JDK 17+
- Node.js 20+
- MySQL 8.0+
- Redis 7.0+
- Maven 3.9+

### 后端启动
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 前端启动
```bash
cd frontend
npm install
npm run dev
```

### 数据库初始化
1. 创建数据库：`haocai_management`
2. 执行 `backend/src/main/resources/init.sql`

## 项目结构

```
cangku/
├── backend/                 # 后端项目
│   ├── src/main/java/com/haocai/management/
│   │   ├── config/         # 配置类
│   │   ├── controller/     # 控制器
│   │   ├── service/        # 业务逻辑
│   │   ├── mapper/         # 数据访问
│   │   ├── entity/         # 实体类
│   │   ├── dto/            # 数据传输对象
│   │   └── utils/          # 工具类
│   └── src/main/resources/  # 配置文件
├── frontend/                # 前端项目
│   ├── src/
│   │   ├── views/          # 页面组件
│   │   ├── components/     # 公共组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── api/            # API接口
│   │   ├── types/          # TypeScript类型
│   │   └── utils/          # 工具函数
│   └── public/             # 静态资源
├── plan.md                  # 项目计划文档
├── day1-plan.md            # 第一天工作计划
└── README.md               # 项目说明
```

## 开发规范

### 代码规范
- 遵循阿里巴巴Java开发规范
- 使用ESLint + Prettier进行代码格式化
- 提交前必须通过代码检查

### Git提交规范
- feat: 新功能
- fix: 修复bug
- docs: 文档更新
- style: 代码格式调整
- refactor: 代码重构
- test: 测试相关
- chore: 构建过程或工具配置更新

### 分支管理
- `main`: 主分支，生产环境代码
- `develop`: 开发分支
- `feature/*`: 功能分支
- `hotfix/*`: 热修复分支

## 部署说明

### 开发环境
- 后端端口：8081
- 前端端口：5173
- 数据库：localhost:3306/haocai_management

### 生产环境
- 使用Docker容器化部署
- Nginx反向代理
- HTTPS证书配置

## 团队成员

- 项目经理：张三
- 后端开发：李四、王五
- 前端开发：赵六、孙七
- 测试工程师：周八

## 许可证

本项目采用 MIT 许可证。

## 联系方式

如有问题，请联系开发团队或提交Issue。