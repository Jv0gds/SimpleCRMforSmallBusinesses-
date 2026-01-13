# Simple CRM for Small Businesses

一个面向小型企业的简单客户关系管理（CRM）系统。

## 📋 项目概述

本项目是一个基于 Spring Boot 的 CRM 系统，提供用户认证、客户管理、销售跟踪等核心功能。

## 🛠️ 技术栈

- **后端框架**: Spring Boot 3.2.1
- **持久层**: Spring Data JPA + Hibernate
- **数据库**: MySQL 8.0 / H2（开发环境）
- **构建工具**: Maven 3.6+
- **Java 版本**: JDK 17+

## 🚀 快速开始

### 前置条件

1. **安装 JDK 17 或更高版本**
   - 下载地址: https://adoptium.net/
   - 验证安装: `java -version`

2. **安装 Maven 3.6 或更高版本**（推荐）
   - 下载地址: https://maven.apache.org/download.cgi
   - 验证安装: `mvn -version`
   - 或使用项目提供的 Maven Wrapper（无需安装 Maven）

3. **安装 MySQL 8.0**（生产环境）
   - 下载地址: https://dev.mysql.com/downloads/mysql/
   - 或使用 H2 内存数据库（开发环境，无需安装）

### 方法一：使用 IDE（推荐）

#### 使用 IntelliJ IDEA

1. 打开 IntelliJ IDEA
2. File → Open → 选择项目根目录
3. IDE 会自动识别 Maven 项目并下载依赖
4. 等待依赖下载完成（右下角显示进度）
5. 右键点击 `SimpleCrmApplication.java` → Run

#### 使用 Visual Studio Code

1. 安装扩展:
   - Extension Pack for Java
   - Spring Boot Extension Pack
2. 打开项目文件夹
3. VSCode 会自动检测 Maven 项目
4. 在左侧 JAVA PROJECTS 面板中，展开项目
5. 右键点击项目 → Update Project Configuration/Reload Projects
6. 等待依赖下载完成
7. 打开 `SimpleCrmApplication.java`，点击 Run 按钮

### 方法二：使用命令行

#### 使用 Maven

```bash
# 下载依赖并编译
mvn clean compile

# 运行应用
mvn spring-boot:run

# 打包为 JAR 文件
mvn clean package

# 运行打包后的 JAR
java -jar target/simple-crm-1.0.0-SNAPSHOT.jar
```

#### 使用 Maven Wrapper（无需安装 Maven）

```bash
# Windows
.\mvnw.cmd clean compile
.\mvnw.cmd spring-boot:run

# Linux/Mac
./mvnw clean compile
./mvnw spring-boot:run
```

### 方法三：数据库配置

#### 配置 MySQL（生产环境）

1. 创建数据库:
```sql
CREATE DATABASE crm_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 修改 `src/main/resources/application.properties`:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/crm_db?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=你的用户名
spring.datasource.password=你的密码
```

3. 初始化数据库表（首次运行）:
```properties
spring.sql.init.mode=always
```

#### 使用 H2 数据库（开发环境，无需配置）

H2 数据库会在应用启动时自动创建，数据存储在内存中。适合开发和测试。

## 🔧 解决"报红"问题

如果您看到 Java 文件中的导入语句标红，请按以下步骤操作：

### 1. 确认已创建必要文件

确保项目包含以下文件：
- ✅ `pom.xml` - Maven 构建配置
- ✅ `src/main/resources/application.properties` - 应用配置
- ✅ `src/main/java/com/crm/SimpleCrmApplication.java` - 主启动类

### 2. 下载 Maven 依赖

**在 IntelliJ IDEA 中:**
- 右键点击 `pom.xml` → Maven → Reload Project

**在 VSCode 中:**
- 打开命令面板（Ctrl+Shift+P）
- 输入 "Java: Clean Java Language Server Workspace"
- 重启 VSCode

**使用命令行:**
```bash
mvn dependency:resolve
```

### 3. 刷新项目

- **IntelliJ IDEA**: File → Invalidate Caches / Restart
- **VSCode**: 重启 VSCode
- **Eclipse**: Project → Clean

### 4. 验证配置

检查 IDE 中的 Java 版本设置是否为 JDK 17。

## 📁 项目结构

```
SimpleCRMforSmallBusinesses/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── crm/
│   │   │           ├── SimpleCrmApplication.java    # 主启动类
│   │   │           ├── controller/                  # 控制器层
│   │   │           ├── service/                     # 业务逻辑层
│   │   │           ├── repository/                  # 数据访问层
│   │   │           │   └── UserRepository.java
│   │   │           └── model/                       # 实体模型层
│   │   │               └── User.java
│   │   └── resources/
│   │       ├── application.properties               # 应用配置
│   │       ├── db/
│   │       │   └── schema.sql                       # 数据库结构
│   │       └── static/                              # 静态资源
│   └── test/                                        # 测试代码
├── pom.xml                                          # Maven 配置
└── README.md                                        # 项目说明
```

## 🐛 常见问题

### Q1: 找不到 `jakarta.persistence` 包
**解决**: 确保已下载 Maven 依赖。在 IDE 中重新加载 Maven 项目。

### Q2: 端口 8080 已被占用
**解决**: 修改 `application.properties` 中的 `server.port=8081`

### Q3: 数据库连接失败
**解决**: 
1. 确认 MySQL 服务已启动
2. 检查数据库名称、用户名、密码是否正确
3. 尝试使用 H2 数据库进行测试

### Q4: Maven 下载依赖缓慢
**解决**: 配置国内镜像源（阿里云）。编辑 `~/.m2/settings.xml`:
```xml
<mirrors>
    <mirror>
        <id>aliyun</id>
        <mirrorOf>central</mirrorOf>
        <name>Aliyun Maven</name>
        <url>https://maven.aliyun.com/repository/public</url>
    </mirror>
</mirrors>
```

## 📝 开发指南

### 编码规范

- 使用 UTF-8 编码
- 遵循 Java 命名规范
- 添加必要的注释和文档
- 提交前运行测试

### Git 提交规范

```
feat: 新功能
fix: 修复 bug
docs: 文档更新
style: 代码格式调整
refactor: 重构代码
test: 测试相关
chore: 构建/工具链更改
```

## 📄 许可证

本项目采用 MIT 许可证。

## 🤝 贡献

欢迎提交 Issue 和 Pull Request！

## 📧 联系方式

如有问题，请联系项目维护者。
