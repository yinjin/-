# 开发环境搭建指南

## 环境要求

### 基础环境
- **操作系统**：Windows 10/11 或 macOS 12+ 或 Ubuntu 20.04+
- **内存**：至少8GB RAM
- **磁盘空间**：至少20GB可用空间

### 开发工具
- **JDK**：17 (推荐使用Eclipse Temurin)
- **Node.js**：20 LTS
- **MySQL**：8.0.35
- **Redis**：7.2.4
- **Maven**：3.9.5
- **Git**：2.42+

### IDE推荐
- **后端**：IntelliJ IDEA 2023.3+
- **前端**：VS Code 1.85+ (推荐安装Vue.js扩展)
- **数据库**：MySQL Workbench 或 DBeaver

## 环境安装步骤

### 1. JDK 17 安装

#### Windows
1. 下载Eclipse Temurin JDK 17：https://adoptium.net/temurin/releases/
2. 运行安装程序，选择默认设置
3. 配置环境变量：
   - 新建系统变量 `JAVA_HOME`：`C:\Program Files\Eclipse Adoptium\jdk-17.x.x.x-hotspot`
   - 在 `Path` 变量中添加：`%JAVA_HOME%\bin`

#### macOS
```bash
brew install openjdk@17
```

#### Linux (Ubuntu)
```bash
sudo apt update
sudo apt install openjdk-17-jdk
```

验证安装：
```bash
java -version
# 应显示 Java 17.x.x
```

### 2. Node.js 20 安装

#### Windows/macOS
1. 下载LTS版本：https://nodejs.org/
2. 运行安装程序，选择默认设置
3. 安装完成后会自动配置环境变量

#### Linux (Ubuntu)
```bash
curl -fsSL https://deb.nodesource.com/setup_20.x | sudo -E bash -
sudo apt-get install -y nodejs
```

验证安装：
```bash
node --version
# 应显示 v20.x.x
npm --version
# 应显示 10.x.x
```

### 3. MySQL 8.0 安装

#### Windows
1. 下载MySQL Installer：https://dev.mysql.com/downloads/installer/
2. 运行安装程序，选择"Developer Default"配置
3. 设置root密码为：`root`
4. 保持默认端口3306

#### macOS
```bash
brew install mysql@8.0
brew services start mysql@8.0
mysql_secure_installation
```

#### Linux (Ubuntu)
```bash
sudo apt install mysql-server-8.0
sudo mysql_secure_installation
```

验证安装：
```bash
mysql --version
# 应显示 8.0.x
```

### 4. Redis 7.2 安装

#### Windows
1. 下载Redis：https://redis.io/download
2. 解压到 `C:\Redis`
3. 以管理员身份运行：
```cmd
redis-server --service-install
redis-server --service-start
```

#### macOS
```bash
brew install redis
brew services start redis
```

#### Linux (Ubuntu)
```bash
sudo apt install redis-server
sudo systemctl start redis-server
```

验证安装：
```bash
redis-cli ping
# 应返回 PONG
```

### 5. Maven 3.9 安装

#### Windows/macOS
1. 下载Maven：https://maven.apache.org/download.cgi
2. 解压到合适目录
3. 配置环境变量：
   - `MAVEN_HOME`：解压目录
   - `Path`：`%MAVEN_HOME%\bin`

#### Linux (Ubuntu)
```bash
sudo apt install maven
```

验证安装：
```bash
mvn -version
# 应显示 Apache Maven 3.9.x
```

### 6. Git 安装

#### Windows
下载并安装：https://git-scm.com/download/win

#### macOS
```bash
brew install git
```

#### Linux (Ubuntu)
```bash
sudo apt install git
```

验证安装：
```bash
git --version
# 应显示 2.42.x
```

## 项目初始化

### 1. 克隆项目
```bash
git clone <repository-url>
cd cangku
```

### 2. 后端项目初始化
```bash
cd backend
mvn clean install
```

### 3. 前端项目初始化
```bash
cd ../frontend
npm install
```

### 4. 数据库初始化
1. 连接到MySQL：
```bash
mysql -u root -p
# 输入密码：root
```

2. 创建数据库：
```sql
CREATE DATABASE haocai_management CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

3. 执行初始化脚本：
```bash
mysql -u root -p haocai_management < ../backend/src/main/resources/init.sql
```

### 5. Redis配置
Redis默认配置即可，无需额外配置。

## 启动项目

### 后端启动
```bash
cd backend
mvn spring-boot:run
```
访问：http://localhost:8081

### 前端启动
```bash
cd frontend
npm run dev
```
访问：http://localhost:5173

## 常见问题

### JDK版本问题
如果遇到版本不匹配，检查 `JAVA_HOME` 环境变量是否正确设置。

### Node.js版本问题
如果遇到版本问题，可以使用nvm管理多个Node.js版本：
```bash
npm install -g nvm
nvm install 20
nvm use 20
```

### MySQL连接问题
1. 检查MySQL服务是否启动
2. 确认用户名密码正确
3. 检查防火墙设置

### Maven下载慢
配置阿里云镜像，在 `~/.m2/settings.xml` 中添加：
```xml
<mirrors>
  <mirror>
    <id>aliyunmaven</id>
    <mirrorOf>*</mirrorOf>
    <name>阿里云公共仓库</name>
    <url>https://maven.aliyun.com/repository/public</url>
  </mirror>
</mirrors>
```

### 端口冲突
如果8081或5173端口被占用，可以修改配置文件：
- 后端：`application.yml` 中的 `server.port`
- 前端：`vite.config.ts` 中的 `server.port`

## 开发工具配置

### IntelliJ IDEA
1. 导入backend项目
2. 配置JDK 17
3. 安装Lombok插件
4. 配置Maven设置

### VS Code
推荐安装以下扩展：
- Vue Language Features (Volar)
- TypeScript Vue Plugin (Volar)
- Prettier - Code formatter
- ESLint

## 团队协作

### Git工作流
1. 从develop分支创建feature分支
2. 提交代码时遵循提交规范
3. 发起Pull Request进行代码审查
4. 合并到develop分支后删除feature分支

### 代码规范
- 后端：遵循阿里巴巴Java开发规范
- 前端：使用ESLint + Prettier
- 提交前必须通过代码检查

## 技术支持

如遇到问题，请：
1. 查看项目文档
2. 搜索相关Issue
3. 联系开发团队

---

*最后更新：2024年*