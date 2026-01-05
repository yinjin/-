# 项目初始化开发教材：后端与前端项目搭建详解

## 前言

本文档详细记录了高职人工智能学院实训耗材管理系统第一天工作中"后端项目初始化"和"前端项目初始化"两个阶段的具体实施过程。作为开发教材，本文档不仅说明了每个步骤的做法，还深入剖析了背后的技术原理和设计目的，帮助开发者理解现代Web应用开发的最佳实践。

## 1. 后端项目初始化详解

### 1.1 项目结构创建

#### 做法步骤：
1. **使用Maven创建Spring Boot项目**
   ```bash
   mvn archetype:generate \
     -DgroupId=com.haocai \
     -DartifactId=haocai-management-backend \
     -DarchetypeArtifactId=maven-archetype-quickstart \
     -DinteractiveMode=false
   ```

2. **配置项目坐标**
   - `groupId`: com.haocai （组织标识符）
   - `artifactId`: haocai-management-backend （项目标识符）
   - `version`: 0.0.1-SNAPSHOT （版本号）
   - `packaging`: jar （打包方式）

#### 技术原理与目的：
- **Maven坐标系统**：Maven使用groupId、artifactId、version三元组唯一标识一个项目，确保在Maven仓库中的唯一性
- **包命名规范**：采用反向域名命名法（com.haocai.management），避免包名冲突，提高代码可维护性
- **版本管理**：使用SNAPSHOT版本表示开发中的不稳定版本，便于持续集成

### 1.2 Maven依赖配置

#### 核心依赖详解：

**Spring Boot Starters：**
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```
- **目的**：提供Web开发的核心功能，包括Spring MVC、嵌入式Tomcat服务器、JSON处理等
- **优势**：自动配置，减少手动配置工作

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
```
- **目的**：集成Spring Data JPA，提供ORM功能
- **优势**：简化数据库操作，支持多种数据库

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```
- **目的**：提供安全认证和授权功能
- **优势**：开箱即用的安全配置，支持多种认证方式

**数据库相关：**
```xml
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <version>8.0.33</version>
</dependency>
```
- **目的**：MySQL数据库驱动
- **版本选择**：8.0.33兼容Spring Boot 3.x，支持最新的MySQL特性

**JWT认证：**
```xml
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```
- **目的**：提供JWT令牌生成和验证功能
- **安全性**：无状态认证，减少服务器压力

**工具库：**
```xml
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>
```
- **目的**：自动生成getter/setter、toString等方法
- **优势**：减少样板代码，提高开发效率

#### 版本管理策略：
```xml
<properties>
    <java.version>17</java.version>
    <mybatis-plus.version>3.5.5</mybatis-plus.version>
    <mysql.version>8.0.33</mysql.version>
    <redis.version>7.2.4</redis.version>
</properties>
```
- **目的**：集中管理版本号，便于统一升级
- **优势**：避免版本冲突，提高可维护性

### 1.3 目录结构设计

#### 标准Maven目录结构：
```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/haocai/management/
│   │   │   ├── config/          # 配置类
│   │   │   ├── controller/      # 控制器层
│   │   │   ├── service/         # 业务逻辑层
│   │   │   ├── mapper/          # 数据访问层
│   │   │   ├── entity/          # 实体类
│   │   │   ├── dto/             # 数据传输对象
│   │   │   └── utils/           # 工具类
│   │   └── resources/           # 配置文件
│   └── test/                    # 测试代码
```

#### 各层职责说明：
- **Controller层**：处理HTTP请求，参数校验，返回响应
- **Service层**：业务逻辑处理，事务管理
- **Mapper层**：数据库操作接口（MyBatis-Plus）
- **Entity层**：数据库表映射对象
- **DTO层**：数据传输对象，避免暴露实体细节
- **Config层**：配置类，安全配置、跨域配置等
- **Utils层**：工具类，JWT工具、密码加密等

### 1.4 配置文件详解

#### application.yml配置：
```yaml
server:
  port: 8081  # 服务器端口，避免与前端端口冲突

spring:
  application:
    name: haocai-management-backend  # 应用名称

  datasource:  # 数据源配置
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: jdbc:mysql://localhost:3306/haocai_management?useSSL=false&serverTimezone=Asia/Shanghai&allowPublicKeyRetrieval=true
    username: root
    password: root

  jpa:
    hibernate:
      ddl-auto: update  # 自动更新表结构
    show-sql: true     # 显示SQL语句（开发环境）
```

#### 配置要点：
- **端口选择**：8081避免与默认8080冲突
- **数据库URL参数**：
  - `useSSL=false`：禁用SSL（开发环境）
  - `serverTimezone=Asia/Shanghai`：设置时区
  - `allowPublicKeyRetrieval=true`：允许公钥检索
- **JPA配置**：自动创建/更新表结构，便于开发

#### JWT配置：
```yaml
jwt:
  secret: haocai-management-secret-key-2024  # JWT密钥
  expiration: 86400000  # Token过期时间（24小时）
```

### 1.5 主启动类实现

#### 代码实现：
```java
package com.haocai.management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HaocaiManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(HaocaiManagementApplication.class, args);
    }
}
```

#### 注解详解：
- `@SpringBootApplication`：复合注解，包含：
  - `@Configuration`：配置类
  - `@EnableAutoConfiguration`：启用自动配置
  - `@ComponentScan`：组件扫描

#### 启动流程：
1. 创建Spring应用上下文
2. 扫描并注册Bean
3. 启动嵌入式Web服务器
4. 初始化数据源、缓存等组件

### 1.6 测试接口实现

#### 代码实现：
```java
package com.haocai.management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping("/hello")
    public String hello() {
        return "Hello, Haocai Management System!";
    }
}
```

#### 设计要点：
- **RESTful设计**：使用RESTful API规范
- **路径规划**：`/api/test/hello`便于前端调用
- **注解使用**：
  - `@RestController`：返回JSON数据
  - `@RequestMapping`：定义基础路径
  - `@GetMapping`：处理GET请求

## 2. 前端项目初始化详解

### 2.1 Vue 3项目创建

#### 做法步骤：
```bash
npm create vue@latest frontend
# 选择以下配置：
# Project name: haocai-management-frontend
# Add TypeScript: Yes
# Add JSX Support: No
# Add Vue Router: Yes
# Add Pinia: Yes
# Add Vitest: No
# Add Cypress: No
# Add ESLint: Yes
```

#### 技术选型理由：
- **Vue 3**：最新的Vue版本，提供Composition API和更好的性能
- **TypeScript**：提供类型安全，提高代码质量
- **Vite**：快速的构建工具，支持热重载
- **Vue Router**：官方路由管理器
- **Pinia**：Vue 3官方状态管理库

### 2.2 核心依赖安装

#### UI框架 - Element Plus：
```json
"element-plus": "^2.5.4"
```
- **目的**：提供丰富的UI组件
- **优势**：Vue 3原生支持，设计美观，易于定制

#### HTTP客户端 - Axios：
```json
"axios": "^1.6.5"
```
- **目的**：处理HTTP请求
- **优势**：支持请求/响应拦截器，Promise API

#### 图表库 - ECharts：
```json
"echarts": "^5.5.0"
```
- **目的**：数据可视化展示
- **优势**：功能强大，支持多种图表类型

#### 工具库：
```json
"lodash-es": "^4.17.21",
"dayjs": "^1.11.10"
```
- **目的**：提供实用工具函数
- **优势**：lodash-es提供函数式编程支持，dayjs处理日期时间

### 2.3 项目结构设计

#### 目录结构：
```
frontend/
├── src/
│   ├── views/          # 页面组件（路由页面）
│   ├── components/     # 可复用组件
│   ├── router/         # 路由配置
│   ├── store/          # 状态管理
│   ├── api/            # API接口封装
│   ├── types/          # TypeScript类型定义
│   ├── utils/          # 工具函数
│   └── assets/         # 静态资源
├── public/             # 公共静态资源
└── dist/               # 构建输出目录
```

#### 结构设计原则：
- **关注点分离**：不同功能模块分离管理
- **可维护性**：清晰的文件组织便于维护
- **可扩展性**：模块化设计支持功能扩展

### 2.4 Vite配置详解

#### vite.config.ts配置：
```typescript
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

export default defineConfig({
  plugins: [
    vue(),
    AutoImport({
      resolvers: [ElementPlusResolver()],
      imports: ['vue', 'vue-router', 'pinia'],
      dts: 'src/auto-imports.d.ts',
    }),
    Components({
      resolvers: [ElementPlusResolver()],
      dts: 'src/components.d.ts',
    }),
  ],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
```

#### 配置要点：
- **路径别名**：`@`指向src目录，简化导入路径
- **自动导入**：自动导入Vue API和Element Plus组件
- **代理配置**：解决跨域问题，转发API请求到后端

### 2.5 应用入口配置

#### main.ts实现：
```typescript
import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import router from './router'
import App from './App.vue'

const app = createApp(App)

app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
```

#### 插件注册顺序：
1. **Pinia**：状态管理，必须在其他插件之前注册
2. **Router**：路由管理
3. **ElementPlus**：UI框架
4. **mount**：最后挂载应用

### 2.6 路由系统设计

#### 路由配置：
```typescript
import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('@/views/HomeView.vue')
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('@/views/AboutView.vue')
    }
  ]
})
```

#### 设计要点：
- **懒加载**：使用动态导入提高首屏加载速度
- **历史模式**：使用HTML5 History模式，支持浏览器前进后退
- **路径别名**：使用@简化组件路径

### 2.7 状态管理配置

#### Pinia Store实现：
```typescript
import { defineStore } from 'pinia'

export const useMainStore = defineStore('main', {
  state: () => ({
    counter: 0,
  }),
  getters: {
    doubleCount: (state) => state.counter * 2,
  },
  actions: {
    increment() {
      this.counter++
    },
  },
})
```

#### 设计要点：
- **组合式API**：使用Vue 3的Composition API风格
- **类型安全**：TypeScript提供类型检查
- **模块化**：每个store管理特定业务状态

### 2.8 API接口封装

#### Axios配置：
```typescript
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 10000,
})

// 请求拦截器
request.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  (response) => response.data,
  (error) => Promise.reject(error)
)

export default request
```

#### 设计要点：
- **统一配置**：集中管理API基础配置
- **自动认证**：请求拦截器自动添加token
- **统一响应**：响应拦截器统一处理数据格式
- **错误处理**：统一的错误处理机制

## 3. 技术选型总结

### 后端技术栈优势：
- **Spring Boot 3.x**：最新的Spring生态，支持Java 17
- **JPA + MyBatis-Plus**：双重数据访问方案，灵活性强
- **JWT认证**：无状态认证，易于扩展
- **多环境配置**：支持开发/测试/生产环境切换

### 前端技术栈优势：
- **Vue 3 + TypeScript**：类型安全，开发体验佳
- **Vite构建**：快速热重载，提升开发效率
- **Element Plus**：美观易用的UI组件库
- **Pinia状态管理**：轻量级，易于学习

## 4. 开发规范与最佳实践

### 代码规范：
- **包结构清晰**：按功能模块组织代码
- **命名规范**：驼峰命名，类名首字母大写
- **注释完整**：重要方法和类要有文档注释

### 配置管理：
- **环境分离**：不同环境使用不同配置文件
- **敏感信息**：密码等敏感信息不应该硬编码
- **版本控制**：依赖版本统一管理

### 安全考虑：
- **JWT过期时间**：合理设置token过期时间
- **密码加密**：用户密码必须加密存储
- **跨域配置**：开发环境允许跨域，生产环境配置CORS

## 5. 常见问题与解决方案

### 后端问题：
1. **端口冲突**：修改application.yml中的server.port
2. **数据库连接失败**：检查MySQL服务状态和连接参数
3. **依赖冲突**：查看Maven依赖树，排除冲突版本

### 前端问题：
1. **Node版本不匹配**：使用nvm管理Node.js版本
2. **依赖安装失败**：清除node_modules，重新npm install
3. **热重载失效**：重启Vite开发服务器

## 6. 学习建议

1. **理解原理**：不仅仅是会用，更要理解为什么这么设计
2. **多实践**：跟着教程动手实现，加深理解
3. **阅读源码**：查看框架源码，学习优秀的设计模式
4. **持续学习**：技术日新月异，要保持学习的态度

---

本文档记录了项目初始化的完整过程，希望能为开发者提供有价值的参考。在实际开发中，要根据项目需求和团队情况灵活调整技术选型和架构设计。