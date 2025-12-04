# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于若依(RuoYi) v3.9.0框架的**养老机构预收费资金监管平台**，采用Spring Boot + Vue架构。

### 项目定位
- **系统目标**: 养老机构预收费资金全流程监管，保障老年人权益
- **架构模式**: 四端协同（民政监管端 + 养老机构端 + 小程序端 + 数据统计平台）
- **开发策略**: 单模块开发（所有后端代码放在ruoyi-admin模块），功能导向，快速迭代

## 数据库配置

**开发环境数据库连接信息**：
- **数据库名**: newzijin
- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: 123456
- **连接URL**: jdbc:mysql://localhost:3306/newzijin?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8

**说明**：
- 使用 MySQL MCP 工具时，直接连接到此数据库
- 配置文件位置：`ruoyi-admin/src/main/resources/application-druid.yml`

## 常用开发命令

### 后端启动
```bash
# 运行主应用类 (位于 ruoyi-admin/src/main/java/com/ruoyi/RuoYiApplication.java)
mvn spring-boot:run -pl ruoyi-admin
```

### 前端开发
```bash
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run dev        # 开发环境 (端口: 80)
npm run build:prod # 生产环境构建
npm run build:stage # 测试环境构建

# H5小程序开发
cd ruoyi-h5
npm install
npm run dev        # 开发环境 (端口: 3000)
npm run build      # 生产环境构建
```

### 数据库初始化
```bash
# 导入数据库脚本 (MySQL)
mysql -u root -p < sql/ry_20250522.sql
mysql -u root -p < sql/quartz.sql
```

## 项目架构

### 后端架构（若依基础框架）
- **ruoyi-admin**: 主应用模块，包含启动类和控制器
- **ruoyi-framework**: 核心框架模块，包含安全、配置、通用功能
- **ruoyi-system**: 系统管理模块，包含用户、角色、菜单等业务
- **ruoyi-common**: 通用工具模块，包含注解、常量、工具类
- **ruoyi-generator**: 代码生成模块
- **ruoyi-quartz**: 定时任务模块
- **ruoyi-h5**: H5小程序接口模块（独立模块）

### 当前系统状态
#### 数据库层面（已导入19张基础表）
**用户权限管理表**:
- `sys_user` - 用户信息表（默认账号：admin/admin123, ry/ry123）
- `sys_role` - 角色表
- `sys_menu` - 菜单权限表
- `sys_dept` - 部门表（若依科技公司架构）
- `sys_post` - 岗位表
- 用户角色、角色菜单、角色部门、用户岗位关联表

**系统管理表**:
- `sys_config` - 系统配置表
- `sys_dict_type` / `sys_dict_data` - 数据字典表
- `sys_notice` - 通知公告表
- `sys_oper_log` - 操作日志表
- `sys_logininfor` - 登录日志表

**工具表**:
- `sys_job` / `sys_job_log` - 定时任务表
- `gen_table` / `gen_table_column` - 代码生成表

#### 后端代码层面（完整若依框架）
**控制器层**（`ruoyi-admin/src/main/java/com/ruoyi/web/controller/`）:
- `system/` - 11个系统管理控制器（用户、角色、菜单、部门、岗位、字典、配置、通知等）
- `monitor/` - 系统监控控制器（在线用户、定时任务、数据监控等）
- `common/` - 通用控制器（文件上传、代码生成等）
- `tool/` - 工具控制器（构建页面、Swagger文档等）
- `pension/` - 养老机构业务控制器（已实现床位管理、入住管理、资金监管等）
- `h5/` - H5小程序接口控制器

#### 前端页面层面（完整若依界面）
**系统管理页面**（`ruoyi-ui/src/views/system/`）:
- 用户管理、角色管理、菜单管理、部门管理
- 岗位管理、字典管理、参数设置、通知公告

**监控工具页面**:
- 系统监控、服务监控、缓存监控
- 在线用户、定时任务、代码生成、系统接口

**养老机构管理页面**（`ruoyi-ui/src/views/pension/`）:
- 账户资金管理、公告管理、余额预警
- 银行支付和监管接口、床位列表
- 押金申请和管理、仪表盘
- 入住管理、机构管理等核心业务功能

### 养老机构业务架构

**业务代码位置**（已开发）:
```
ruoyi-admin/src/main/java/com/ruoyi/
├── controller/pension/    # 养老机构控制器（已实现）
├── service/pension/       # 业务逻辑层（已实现）
├── domain/pension/        # 实体类（已实现）
└── mapper/pension/        # 数据访问层（已实现）

ruoyi-ui/src/views/pension/   # 养老机构页面（已实现）
ruoyi-h5/                    # H5小程序端（独立模块）
```

### 前端结构
- **src/api**: API接口定义
- **src/views**: 页面组件
- **src/components**: 通用组件
- **src/router**: 路由配置
- **src/store**: Vuex状态管理
- **src/utils**: 工具函数

### 重要配置文件
- `ruoyi-admin/src/main/resources/application.yml`: 主配置文件
- `ruoyi-admin/src/main/resources/application-druid.yml`: 数据库配置
- `ruoyi-ui/vue.config.js`: 前端构建配置
- `ruoyi-ui/src/settings.js`: 前端环境配置

## 开发环境配置

### 数据库配置
- 数据库: MySQL (localhost:3306/newzijin)
- 用户名: root
- 密码: 123456 (在 application-druid.yml 中配置)
- Druid监控: http://localhost:8080/druid (用户名: yl, 密码: 123456)

### Redis配置
- 地址: localhost:6379
- 数据库: 0
- 无密码

### 端口配置
- 后端: 8080
- 前端: 80
- H5前端: 3000 (ruoyi-h5模块)

## 默认账号
- 用户名: admin
- 密码: admin123

## 开发注意事项

### 后端开发
- 控制器位于 `ruoyi-admin/src/main/java/com/ruoyi/web/controller/` 下按模块分组
- 实体类使用 `@Data` 注解，包含主键 `@TableId`
- 服务层使用 `@Service` 注解，自动注入 `@Autowired`
- MyBatis 映射文件位于 `resources/mapper/` 目录

### 前端开发
- API调用统一使用 `src/api/` 下的接口定义
- 页面组件以 `.vue` 文件存放在 `src/views/` 对应模块目录
- 使用 Element UI 组件库，遵循设计规范
- 路由配置在 `src/router/index.js` 中

### 权限系统
- 基于角色的权限控制 (RBAC)
- 使用JWT进行身份验证
- 菜单权限在系统管理中配置
- 按钮权限使用 `@PreAuthorize` 注解控制

## 构建和部署

### 后端构建
```bash
mvn clean package
# 或使用提供的脚本
./ry.sh start    # 启动服务
./ry.sh stop     # 停止服务
./ry.sh restart  # 重启服务
./ry.sh status   # 查看状态
```
生成的JAR文件位于 `ruoyi-admin/target/` 目录

### 前端构建
```bash
cd ruoyi-ui
npm run build:prod    # 生产环境构建
npm run build:stage   # 测试环境构建

# H5构建
cd ruoyi-h5
npm run build
```
生成的静态文件位于 `ruoyi-ui/dist/` 目录

## 监控和管理
- 应用启动后访问 http://localhost:8080
- Swagger文档: http://localhost:8080/swagger-ui/
- Druid监控: http://localhost:8080/druid/ (用户名: yl, 密码: 123456)
- H5小程序: http://localhost:3000

## 养老机构业务功能规划

### 已实现核心功能
1. **✅ 机构信息管理** - 养老机构基础信息管理、附件上传、审核流程
2. **✅ 老人入住管理** - 老人信息登记、家庭成员管理、入住流程
3. **✅ 床位管理** - 床位信息管理、床位分配、状态跟踪
4. **✅ 账户资金管理** - 老人账户管理、资金流水、余额预警
5. **✅ 押金管理** - 押金申请、审核、使用记录
6. **✅ 银行接口** - 支付接口、监管银行对接
7. **✅ H5小程序端** - 机构展示、预约参观、投诉建议等便民服务

### 待完善功能
1. **订单支付功能** ⭐⭐⭐ - 资金流转关键环节
2. **预警监控** ⭐⭐ - 风险控制功能
3. **数据统计分析** ⭐⭐ - 监管决策支持

### 核心业务表（已实现）
- `pension_institution` - 养老机构信息表 ✅
- `pension_institution_attach` - 机构附件材料表 ✅
- `elder_info` - 老人基础信息表 ✅
- `elder_family` - 老人家庭成员表 ✅
- `elder_check_in` - 入住管理表 ✅
- `bed_info` - 床位信息表 ✅
- `account_info` - 老人账户信息表 ✅
- `deposit_apply` - 押金申请表 ✅
- `balance_warning` - 余额预警表 ✅

## 开发注意事项

### 开发策略
- **单模块开发**: 所有养老机构业务代码放在 `ruoyi-admin` 模块下
- **功能导向**: 实现一个功能，测试一个功能
- **代码简洁**: 避免过度抽象，注重可读性
- **快速迭代**: 复用若依现有组件，专注业务逻辑

### 后端开发
- 控制器位于 `ruoyi-admin/src/main/java/com/ruoyi/web/controller/pension/` 下
- 实体类使用 `@Data` 注解，继承 `BaseEntity`
- 服务层使用 `@Service` 注解，自动注入 `@Autowired`
- MyBatis 映射文件位于 `ruoyi-admin/src/main/resources/mapper/pension/` 目录
- 复用若依的权限验证、日志记录、数据字典等功能

### 前端开发
- API调用统一使用 `src/api/pension/` 下的接口定义
- 页面组件以 `.vue` 文件存放在 `src/views/pension/` 对应模块目录
- 使用 Element UI 组件库，遵循若依设计规范
- 复用若依的通用组件：文件上传、数据表格、表单验证等
- 路由配置在 `src/router/index.js` 中，添加养老机构相关路由

### 权限系统
- 基于若依现有的RBAC权限控制
- 在系统管理中配置养老机构相关菜单和权限
- 使用 `@PreAuthorize` 注解控制接口权限
- 按钮权限使用若依�� `v-hasPermi` 指令控制

## 数据库配置

### 数据库连接信息
- **配置文件**: `ruoyi-admin/src/main/resources/application-druid.yml`
- **数据库地址**: localhost:3306
- **数据库名**: newzijin
- **用户名**: root
- **密码**: 123456
- **字符集**: utf8mb4

### 数据库管理
- **连接数据库**: `mysql -h localhost -u root -p newzijin`
- **查看表结构**: `DESC table_name;`
- **查看数据**: `SELECT * FROM table_name LIMIT 10;`
- **表结构说明**: elder_info表已添加password字段用于老人登录密码

### Druid监控
- **访问地址**: http://localhost:8080/druid
- **用户名**: yl
- **密码**: 123456

**注意**: 所有数据库相关的查询和操作都应该使用上述配置信息进行连接。

## 常见问题
- 确保MySQL和Redis服务已启动
- 前端开发需先执行 `npm install` 安装依赖
- 数据库连接失败请检查 `application-druid.yml` 配置
- 跨域问题由前端 `vue.config.js` 中的代理配置解决
- 养老机构业务功能需要在系统管理中配置相应的菜单和权限

## 相关文档
- **功能描述**: `功能描述.md` - 完整的功能需求文档
- **业务流程**: `业务流程详解.md` - 详细的业务流程说明
- **开发列表**: `开发列表.md` - 功能开发计划和指导
- **修改记录**: `xiugai.md` - 项目变更和开发记录