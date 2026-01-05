# Docker本地测试配置指南

## 概述

本指南详细说明如何在Windows环境下配置Docker，以便运行角色权限管理模块的测试。

## 一、Docker安装

### 1.1 系统要求

**Windows系统要求**:
- Windows 10 64-bit: Pro, Enterprise, or Education (Build 16299 or later)
- Windows 11 64-bit: Home or Pro version 21H2 or greater
- 启用Hyper-V和容器功能
- 至少4GB RAM（推荐8GB）
- BIOS级虚拟化支持

### 1.2 安装Docker Desktop

#### 步骤1: 下载Docker Desktop

1. 访问Docker官网: https://www.docker.com/products/docker-desktop/
2. 点击"Download for Windows"
3. 下载`Docker Desktop Installer.exe`

#### 步骤2: 安装Docker Desktop

1. 双击运行`Docker Desktop Installer.exe`
2. 在安装向导中，确保勾选以下选项：
   - ✅ Use WSL 2 instead of Hyper-V (推荐)
   - ✅ Add shortcut to desktop
3. 点击"OK"开始安装
4. 等待安装完成（可能需要几分钟）
5. 安装完成后，点击"Close and restart"

#### 步骤3: 启用WSL 2（推荐）

如果安装时选择了WSL 2，需要：

1. 以管理员身份打开PowerShell
2. 运行以下命令：
```powershell
wsl --install
```
3. 重启计算机
4. 重启后，WSL 2会自动安装Ubuntu

#### 步骤4: 启动Docker Desktop

1. 从开始菜单启动"Docker Desktop"
2. 首次启动时，接受服务协议
3. Docker会在系统托盘显示鲸鱼图标
4. 等待Docker完全启动（图标变为稳定状态）

### 1.3 验证Docker安装

打开PowerShell或命令提示符，运行：

```bash
docker --version
```

预期输出：
```
Docker version 24.0.7, build afdd53b
```

运行测试容器：
```bash
docker run hello-world
```

预期输出：
```
Hello from Docker!
This message shows that your installation appears to be working correctly.
```

## 二、Docker配置

### 2.1 配置Docker资源

1. 右键点击系统托盘的Docker图标
2. 选择"Settings"
3. 在"Resources"选项卡中配置：
   - **Memory**: 至少4GB（推荐8GB）
   - **CPUs**: 至少2核（推荐4核）
   - **Disk**: 至少20GB
4. 点击"Apply & Restart"

### 2.2 配置Docker镜像加速（可选）

如果在中国大陆，建议配置镜像加速：

1. 打开Docker Desktop设置
2. 选择"Docker Engine"
3. 在JSON配置中添加：
```json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://hub-mirror.c.163.com"
  ]
}
```
4. 点击"Apply & Restart"

## 三、本地MySQL配置（替代方案）

如果无法安装Docker，可以使用本地MySQL数据库进行测试。

### 3.1 安装MySQL

#### 方式1: 使用已安装的MySQL

如果系统已安装MySQL 8.0+，可以直接使用。

#### 方式2: 下载安装MySQL

1. 访问MySQL官网: https://dev.mysql.com/downloads/mysql/
2. 下载MySQL 8.0+ Community Server
3. 运行安装程序
4. 选择"Developer Default"
5. 设置root密码（记住这个密码）
6. 完成安装

### 3.2 创建测试数据库

1. 打开MySQL命令行或使用Navicat等工具
2. 连接到MySQL
3. 创建测试数据库：
```sql
CREATE DATABASE material_system_test CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3.3 修改测试配置

#### 方式1: 修改application-test.yml

编辑`backend/src/test/resources/application-test.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/material_system_test?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password  # 替换为你的MySQL密码
    driver-class-name: com.mysql.cj.jdbc.Driver
```

#### 方式2: 修改AbstractMySQLTest.java

编辑`backend/src/test/java/com/material/system/AbstractMySQLTest.java`：

```java
@SpringBootTest
@ActiveProfiles("test")
public abstract class AbstractMySQLTest {
    // 移除Testcontainers相关代码
    // 直接使用本地MySQL
}
```

### 3.4 初始化测试数据

1. 连接到测试数据库
2. 执行以下SQL脚本：
   - `backend/src/main/resources/db/schema.sql`
   - `backend/src/main/resources/db/init.sql`
   - `backend/src/main/resources/db/role_permission_schema.sql`
   - `backend/src/main/resources/db/init_role_permission.sql`

## 四、运行测试

### 4.1 使用Docker运行测试

确保Docker正在运行，然后：

```bash
cd backend
mvn test
```

Testcontainers会自动：
1. 拉取MySQL Docker镜像（首次运行）
2. 启动MySQL容器
3. 初始化数据库
4. 运行测试
5. 测试完成后自动清理容器

### 4.2 使用本地MySQL运行测试

```bash
cd backend
mvn test -Dspring.profiles.active=test
```

### 4.3 运行特定测试

```bash
# 运行角色服务测试
mvn test -Dtest=SysRoleServiceTest

# 运行权限服务测试
mvn test -Dtest=SysPermissionServiceTest

# 运行角色控制器测试
mvn test -Dtest=SysRoleControllerTest

# 运行权限控制器测试
mvn test -Dtest=SysPermissionControllerTest
```

## 五、常见问题

### 5.1 Docker相关问题

#### 问题1: Docker Desktop无法启动

**解决方案**:
1. 检查Hyper-V是否启用
2. 以管理员身份运行PowerShell，执行：
```powershell
Enable-WindowsOptionalFeature -Online -FeatureName Microsoft-Hyper-V -All
```
3. 重启计算机

#### 问题2: Docker命令提示"command not found"

**解决方案**:
1. 确保Docker Desktop正在运行
2. 重启命令提示符
3. 检查PATH环境变量是否包含Docker路径

#### 问题3: Testcontainers无法连接到Docker

**解决方案**:
1. 确保Docker Desktop正在运行
2. 检查Docker Desktop设置中的"Expose daemon on tcp://localhost:2375 without TLS"是否启用
3. 重启Docker Desktop

### 5.2 MySQL相关问题

#### 问题1: 连接MySQL失败

**解决方案**:
1. 检查MySQL服务是否启动
2. 检查用户名和密码是否正确
3. 检查数据库名称是否正确
4. 检查防火墙是否阻止连接

#### 问题2: 测试数据初始化失败

**解决方案**:
1. 确保所有SQL脚本都已执行
2. 检查SQL脚本是否有语法错误
3. 手动执行SQL脚本验证

### 5.3 Maven相关问题

#### 问题1: 依赖下载失败

**解决方案**:
1. 检查网络连接
2. 配置Maven镜像（阿里云）：
   编辑`backend/maven-settings.xml`：
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

#### 问题2: 测试编译失败

**解决方案**:
1. 清理Maven缓存：
```bash
mvn clean
```
2. 重新编译：
```bash
mvn compile test-compile
```
3. 运行测试：
```bash
mvn test
```

## 六、性能优化

### 6.1 Docker性能优化

1. 增加Docker Desktop分配的内存和CPU
2. 使用SSD存储Docker镜像
3. 定期清理Docker缓存：
```bash
docker system prune -a
```

### 6.2 测试性能优化

1. 并行运行测试：
```bash
mvn test -Dparallel=methods -Dparallel.threadCount=4
```

2. 跳过慢速测试：
```bash
mvn test -DskipSlowTests=true
```

## 七、持续集成配置

### 7.1 GitHub Actions配置

创建`.github/workflows/test.yml`：

```yaml
name: Run Tests

on: [push, pull_request]

jobs:
  test:
    runs-on: ubuntu-latest
    
    services:
      mysql:
        image: mysql:8.0
        env:
          MYSQL_ROOT_PASSWORD: root
          MYSQL_DATABASE: material_system_test
        ports:
          - 3306:3306
        options: --health-cmd="mysqladmin ping" --health-interval=10s --health-timeout=5s --health-retries=3
    
    steps:
      - uses: actions/checkout@v2
      - name: Set up JDK 17
        uses: actions/setup-java@v2
        with:
          java-version: '17'
          distribution: 'temurin'
      - name: Run tests
        run: |
          cd backend
          mvn test
```

## 八、总结

### 推荐方案

**方案1: 使用Docker（推荐）**
- ✅ 环境隔离，不影响本地环境
- ✅ 自动管理数据库生命周期
- ✅ 测试结果一致性好
- ✅ 适合团队协作

**方案2: 使用本地MySQL**
- ✅ 无需安装Docker
- ✅ 测试速度更快
- ❌ 需要手动管理数据库
- ❌ 可能影响本地开发环境

### 快速开始

1. **安装Docker Desktop**（5分钟）
2. **启动Docker**（1分钟）
3. **运行测试**（2分钟）

```bash
cd backend
mvn test
```

### 获取帮助

- Docker官方文档: https://docs.docker.com/
- Testcontainers文档: https://www.testcontainers.org/
- MySQL文档: https://dev.mysql.com/doc/
- Maven文档: https://maven.apache.org/guides/

---

**最后更新**: 2026年1月4日
**维护者**: 开发团队
