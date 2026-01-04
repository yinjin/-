# Material Management System - Backend

## 项目概述
这是物料管理系统的后端服务，基于 Spring Boot 3.2.0 开发。

## 技术栈
- Java 17+
- Spring Boot 3.2.0
- Spring Data JPA
- MySQL 8.0
- Maven

## 项目结构
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/material/system/
│   │   │   ├── MaterialSystemApplication.java  # 主应用类
│   │   │   └── controller/
│   │   │       └── HealthController.java       # 健康检查控制器
│   │   └── resources/
│   │       └── application.properties          # 应用配置
│   └── test/
└── pom.xml                                    # Maven 依赖配置
```

## 环境要求
- JDK 17 或更高版本
- Maven 3.6+
- MySQL 8.0+

## 配置说明

### 数据库配置
在 `application.properties` 中配置数据库连接：
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/material_system?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### JPA 配置
```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

### 服务器配置
```properties
server.port=8080
```

## 快速开始

### 1. 创建数据库
```sql
CREATE DATABASE material_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 2. 安装依赖
```bash
cd backend
mvn clean install
```

### 3. 启动服务
```bash
mvn spring-boot:run
```

服务将在 `http://localhost:8080` 启动

## API 接口

### 健康检查接口
- **URL**: `GET /api/health`
- **描述**: 检查后端服务健康状态
- **响应示例**:
```json
{
  "service": "Material System Backend",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": "2026-01-03T22:03:40.2456693"
}
```

### 欢迎接口
- **URL**: `GET /api/welcome`
- **描述**: 欢迎信息
- **响应示例**:
```json
{
  "message": "Welcome to Material Management System Backend API",
  "status": "Backend service is running successfully"
}
```

## 测试

### 使用 curl 测试
```bash
# 测试健康检查
curl http://localhost:8080/api/health

# 测试欢迎接口
curl http://localhost:8080/api/welcome
```

### 使用浏览器测试
直接在浏览器中访问：
- http://localhost:8080/api/health
- http://localhost:8080/api/welcome

## 开发状态

### 阶段一：项目初始化和环境搭建 ✅
- [x] 创建 Spring Boot 项目结构
- [x] 配置 Maven 依赖
- [x] 配置数据库连接
- [x] 实现健康检查接口
- [x] 测试服务启动
- [x] 验证 API 接口

## 注意事项
1. 确保 MySQL 服务已启动
2. 确保数据库 `material_system` 已创建
3. 首次启动时，Hibernate 会自动创建表结构（ddl-auto=update）
4. 默认端口为 8080，如需修改请在 `application.properties` 中配置

## 下一步开发
- 实现用户认证模块
- 实现物料管理模块
- 实现库存管理模块
- 实现出入库管理模块

## 数据库迁移（Flyway）

项目已集成 Flyway 作为数据库迁移工具，迁移脚本位于 `src/main/resources/db/migration`，例如 `V1__init.sql`。

本地运行迁移（使用 MySQL）：

```bash
# 使用 Flyway Maven 插件执行迁移（替换 JDBC URL/用户名/密码）
mvn -f backend/pom.xml -Dflyway.url=jdbc:mysql://localhost:3306/material_system -Dflyway.user=root -Dflyway.password=root flyway:migrate
```

注意：项目启动时（Spring Boot）会自动运行 Flyway（如果 Flyway 在 classpath 且配置存在），测试 profile 使用内存 H2 数据库，集成测试会在启动过程中应用迁移脚本。

## CI 已配置

已添加基础 GitHub Actions 工作流 `.github/workflows/ci.yml`，在 push/PR 时运行后端构建与测试（使用 `test` profile）。CI 会执行 Maven 验证阶段（包括运行测试）。

如需我把 Flyway 的生产迁移策略写入文档（例如：预发布环境先运行 `flyway:migrate`，生产环境由运维执行），我可以继续补充。
