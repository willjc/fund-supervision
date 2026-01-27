# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

这是一个基于若依(RuoYi) v3.9.0框架的**养老机构预收费资金监管平台**，采用Spring Boot + Vue架构。

### 项目定位
- **系统目标**: 养老机构预收费资金全流程监管，保障老年人权益
- **架构模式**: 四端协同（民政监管端 + 养老机构端 + 小程序端 + 数据统计平台）
- **开发策略**: 单模块开发（所有后端代码放在ruoyi-admin模块），功能导向，快速迭代

## 数据库操作规范 (极其重要! 必须遵守!)

**🔴 强制要求：所有数据库查询和操作必须使用 MCP MySQL 工具**

- ✅ **必须使用**: `mcp__mysql__*` 系列工具进行数据库操作
- ❌ **严禁使用**: Bash 执行 `mysql` 命令行工具
- ❌ **严禁使用**: 其他任何直接连接数据库的方式

### MCP 工具使用步骤

1. **连接数据库**: 使用 `mcp__mysql__connect_db`
2. **查询数据**: 使用 `mcp__mysql__query` (SELECT)
3. **修改数据**: 使用 `mcp__mysql__execute` (INSERT/UPDATE/DELETE)
4. **查看表结构**: 使用 `mcp__mysql__describe_table`
5. **列出所有表**: 使用 `mcp__mysql__list_tables`


## 数据库配置

**开发环境数据库连接信息**：
- **数据库名**: newzijin
- **主机**: localhost
- **端口**: 3306
- **用户名**: root
- **密码**: 123456789w
- **连接URL**: jdbc:mysql://localhost:3306/newzijin?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8

**说明**：
- 使用 MySQL MCP 工具时，直接连接到此数据库
- 配置文件位置：`/Users/wangwen/Desktop/supervison/fund-supervision/ruoyi-admin/src/main/resources/application-druid.yml`

## 核心开发原则 (必须遵守)

### 1. 测试规则
**重要**: 永远不要执行任何测试命令 (包括前端和后端)。所有测试由用户负责。
- ❌ 不要运行: `npm run test`, `mvn test`, `npm run dev` 等任何测试/运行命令
- ✅ 只负责: 代码编写、修改、文档更新


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

### 测试原则
- **永远不要测试** - 代码修改完成后由用户进行测试
- **永远不要启动后端或测试任何内容** - 绝对不要启动任何服务、运行构建或进行功能测试
- 修复问题时只需分析和修改代码，不需要启动服务验证
- 用户负责测试所有修改的功能和修复的问题
- 不要运行任何mvn、npm、java、node等命令来启动或测试服务

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

### 权限管理设计原则 ⭐⭐⭐ 重要

**核心原则：功能页面必须包含该功能所需的所有权限按钮**

#### 设计目标
- 方便在系统管理-角色管理中集中勾选权限
- 避免权限分散在多个菜单下，导致遗漏
- 机构管理员只需在养老机构导航下配置权限，不需要看到其他主导航菜单

#### 实施规范

**1. 权限按钮配置规则**

每个功能页面（menu_type='C'）下必须包含该页面所需的所有权限按钮（menu_type='F'），包括：
- 基础权限：查询(query)、新增(add)、修改(edit)、删除(remove)、导出(export)
- 业务权限：支付(pay)、取消(cancel)、审核(approve)等业务操作
- **关联数据权限**：如果页面需要查询其他表的数据，也要添加对应的权限按钮

**示例：订单列表页面（menu_id=2041）的权限配置**
```
订单列表 (order:info:list) - 菜单页面
├── 订单列表权限 (order:info:list) - 列表查询
├── 订单列表查询 (order:info:query) - 详情查询
├── 订单列表新增 (order:info:add)
├── 订单列表修改 (order:info:edit)
├── 订单列表删除 (order:info:remove)
├── 订单列表导出 (order:info:export)
├── 订单支付 (order:info:pay)
├── 订单取消 (order:info:cancel)
├── 生成订单 (order:info:generate)
├── 订单明细查询 (order:item:query) ⭐ 关联数据权限
└── 支付记录查询 (payment:record:list) ⭐ 关联数据权限
```

**2. 新功能开发检查清单**

开发新功能时，必须完成以下步骤：

- [ ] 创建菜单页面（menu_type='C'）
- [ ] 为该页面添加所有需要的权限按钮（menu_type='F'）
  - [ ] 基础CRUD权限
  - [ ] 业务操作权限
  - [ ] 关联数据查询权限（分析页面会调用哪些API）
- [ ] 在控制器方法上添加 `@PreAuthorize` 注解
- [ ] 在前端页面按钮上添加 `v-hasPermi` 指令
- [ ] 为默认角色分配必要的权限（如机构管理员role_id=100）

**3. 如何识别关联数据权限**

分析前端页面代码，查看：
- API调用：`src/api/` 下的接口文件
- 并发请求：`Promise.all([...])` 中的多个接口
- 组件引用：子组件可能调用的接口
- 对话框打开：详情对话框、编辑对话框中的数据加载

**4. 权限命名规范**

- 格式：`模块:功能:操作`
- 示例：
  - `order:info:list` - 订单主表列表
  - `order:info:query` - 订单主表查询
  - `order:item:query` - 订单明细查询
  - `payment:record:list` - 支付记录列表

**5. 数据库操作**

```sql
-- 添加权限按钮（menu_type='F'）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, menu_type, visible, status, perms, create_by, create_time, update_by, update_time, remark)
VALUES (下一个ID, '权限名称', 父菜单ID, 排序号, 'F', '0', '0', '权限标识', 'admin', NOW(), 'admin', NOW(), '权限说明');

-- 为角色分配权限
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (角色ID, 权限菜单ID);
```

#### 常见问题

**Q: 为什么订单详情提示两次权限错误？**
A: 订单详情对话框并发调用了三个接口（订单主信息、订单明细、支付记录），缺少订单明细和支付记录的查询权限。

**Q: 是否需要给机构管理员分配主导航"订单管理"的权限？**
A: 不需要。机构管理员只在"养老机构"导航下操作，订单列表已经放在养老机构导航下，所有权限也应该配置在该菜单下。

**Q: 如何避免权限遗漏？**
A: 在开发完成后，以该角色登录测试，确保所有功能按钮都能正常显示和操作。如遇权限错误，立即添加对应的权限按钮。

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
7. **✅ 订单支付功能** - H5端订单支付流程，自动同步账户余额
8. **✅ H5小程序端** - 完整的用户端功能（详见H5端功能说明）

### 待完善功能
1. **预警监控** ⭐⭐ - 风险控制功能
2. **数据统计分析** ⭐⭐ - 监管决策支持
3. **真实支付网关对接** ⭐ - 当前使用模拟支付

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
- `order_info` - 订单信息表 ✅

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

### 数据库操作规范
**重要原则**: 执行任何数据库操作前，优先使用MCP工具连接数据库

#### MCP工具使用规范
1. **查询操作**：
   - 使用 `mcp__mysql__query` 工具执行所有SELECT查询
   - 支持复杂查询、JOIN、聚合函数等
   - 示例：查看数据、分析问题、验证结果

2. **MCP限制说明**：
   - MCP的 `mcp__mysql__query` 工具只能执行SELECT查询
   - 不能执行INSERT、UPDATE、DELETE、ALTER等修改数据的语句
   - 这是出于安全考虑的设计限制

#### 命令行工具使用时机
当MCP无法实现时，使用以下命令行方式：

```bash
# 连接数据库
mysql -u root -p123456 -e "USE newzijin; [SQL语句]"

# 或交互式执行
mysql -u root -p123456 newzijin
```

3. **适用场景**：
   - 数据初始化（INSERT批量数据）
   - 表结构修改（ALTER TABLE）
   - 执行SQL脚本文件（mysql < file.sql）
   - 批量更新或删除操作

### 常用数据库命令
- **查看表结构**: `DESC table_name;`
- **查看数据**: `SELECT * FROM table_name LIMIT 10;`
- **表结构说明**: elder_info表已添加password字段用于老人登录密码

### Druid监控
- **访问地址**: http://localhost:8080/druid
- **用户名**: yl
- **密码**: 123456

**注意**: 所有数据库相关的操作都应遵循上述规范，优先使用MCP工具。

## 常见问题
- 确保MySQL和Redis服务已启动
- 前端开发需先执行 `npm install` 安装依赖
- 数据库连接失败请检查 `application-druid.yml` 配置
- 跨域问题由前端 `vue.config.js` 中的代理配置解决
- 养老机构业务功能需要在系统管理中配置相应的菜单和权限

## H5端功能说明

### H5小程序端架构
- **技术栈**: Vue 3 + Vite + Vant UI
- **端口**: 3000
- **目录**: `ruoyi-h5/`
- **API接口**: 统一使用 `/h5` 前缀

### 核心功能模块

#### 1. 订单管理 (`/order`)
**文件位置**:
- 前端: `ruoyi-h5/src/views/order/index.vue` (订单列表)
- 前端: `ruoyi-h5/src/views/order/detail.vue` (订单详情)
- 后端: `H5OrderController.java`

**功能特性**:
- ✅ 订单列表展示（基于当前登录用户查询，非老人关联）
- ✅ 订单状态筛选（全部/待付款/已支付/已取消/退款）
- ✅ 订单详情查看（机构信息、费用明细、服务周期）
- ✅ 订单支付功能（模拟支付成功，自动同步数据）
- ✅ 订单取消功能

**关键接口**:
```
GET  /h5/order/list         # 获取订单列表
GET  /h5/order/{orderId}    # 获取订单详情
POST /h5/payment/process/{orderId}  # 处理支付
```

**支付流程**:
1. 用户点击"立即支付"按钮
2. 显示确认对话框（订单号+金额）
3. 调用 `/h5/payment/process/{orderId}` 接口
4. 后端模拟1秒支付延迟
5. 更新订单状态为"已支付"（orderStatus = "1"）
6. 检查老人是否有账户，无则自动创建
7. 根据订单金额更新账户余额：
   - 入驻订单（type=1）: 40%押金 + 10%会员费 + 50%服务费
   - 续费订单（type=2）: 90%服务费 + 10%其他
   - 其他订单: 80%服务费 + 20%其他
8. 返回支付成功结果，前端刷新订单列表

#### 2. 费用管理 (`/user/expense`)
**文件位置**:
- 前端: `ruoyi-h5/src/views/user/expense/index.vue`
- API: `ruoyi-h5/src/api/expense.js`
- 后端: `H5OrderController.java`

**功能特性**:
- ✅ 老人列表选择（仅显示当前用户关联的老人）
- ✅ 账户余额展示（总余额、押金余额、预存金额）
- ✅ 费用明细查询（押金使用、服务费扣款、其他费用）
- ✅ 分类查看（全部/押金/服务费/其他）
- ✅ 下拉加载更多

**关键接口**:
```
GET /h5/elder/list          # 获取老人列表
GET /h5/account/{elderId}   # 获取账户余额
GET /h5/expense/list        # 获取费用明细
```

**数据来源**:
- 老人列表: `elder_family` 表（关联current user）+ `elder_info` 表
- 账户余额: `account_info` 表
- 押金记录: `deposit_apply` 表（已批准的申请）
- 服务费记录: `order_info` 表（已支付订单）

**特殊处理**:
- 未创建账户的老人显示默认值（余额为0），提示"请先办理入住手续"
- 只显示未出院的老人（status != "2"）

#### 3. 机构展示
- ✅ 机构列表展示
- ✅ 机构详情查看
- ✅ 床位价格查询
- ✅ 在线预约

### 数据权限控制

**用户身份验证**:
- 所有H5接口都需要用户登录（通过SecurityUtils.getUserId()获取）
- 未登录用户返回null，提示"用户未登录或身份验证失败"

**数据访问权限**:
- 订单列表: 只能查看自己创建的订单（creatorUserId = currentUserId）
- 老人信息: 只能查看elder_family表中关联的老人
- 账户信息: 验证用户是否为该老人的家属
- 费用明细: 验证用户是否有权限访问该老人信息

### 技术要点

**前端技术栈**:
- Vue 3 Composition API (`<script setup>`)
- Vant UI 组件库（showToast, showConfirmDialog, van-list等）
- Axios 请求封装
- Pinia 状态管理（userStore）

**后端技术栈**:
- Spring Boot REST API
- MyBatis 数据访问
- BigDecimal 精确金额计算
- 分页查询支持

**关键代码位置**:
```
ruoyi-h5/
├── src/
│   ├── api/
│   │   ├── order.js        # 订单API
│   │   └── expense.js      # 费用API
│   ├── views/
│   │   ├── order/
│   │   │   ├── index.vue   # 订单列表
│   │   │   └── detail.vue  # 订单详情
│   │   └── user/
│   │       └── expense/
│   │           └── index.vue  # 费用管理
│   └── store/
│       └── modules/
│           └── user.js      # 用户状态管理

ruoyi-admin/
└── src/main/java/com/ruoyi/web/controller/h5/
    └── H5OrderController.java  # H5端控制器（566-690行：支付功能）
```

## 监管账户划拨流水记录最佳实践 ⭐ 重要

当系统执行资金划拨操作（如押金使用、服务费扣除等）时，必须同时生成监管账户流水记录，以便在管理端监管账户流水页面能够追踪所有资金变动。

### 核心原则

**任何资金从监管账户划拨出去的操作，都必须调用 `recordTransferOut` 生成流水记录。**

### 什么时候需要生成划拨流水

以下场景需要生成监管账户支出流水：

| 场景 | 示例 | 划拨去向 |
|------|------|----------|
| 押金使用申请审批通过 | 家属申请购买物品，监管部门审批通过 | 基本账户 |
| 首月服务费扣除 | 新入住订单支付后自动扣除首月服务费 | 基本账户 |
| 月度服务费划拨 | 定时任务每月自动划拨服务费给机构 | 基本账户 |
| 退款划拨 | 老人退款审批通过 | 基本账户 |

### 代码实现模板

```java
// 1. 注入服务
@Autowired
private ISupervisionAccountLogService supervisionAccountLogService;

// 2. 在资金划拨成功后调用 recordTransferOut
try {
    supervisionAccountLogService.recordTransferOut(
        institutionId,           // 机构ID
        relatedId,               // 关联ID（订单ID、申请ID等）
        amount,                  // 划拨金额
        businessDesc,            // 业务描述，如"首月服务费划拨-订单号"
        "基本账户"                // 划拨去向
    );
    logger.info("生成划拨流水成功：" + amount + "元");
} catch (Exception e) {
    logger.error("记录划拨流水失败", e);
    // 划拨流水记录失败不应影响主流程
}
```

### 完整示例（首月服务费划拨）

```java
// 文件：H5OrderController.java
// 位置：updateAccountBalanceFromOrder 方法中

if ("1".equals(order.getOrderType())) {
    // 计算首月服务费
    BigDecimal firstMonthServiceFee = calculateFirstMonthServiceFee(order.getOrderId());

    if (firstMonthServiceFee.compareTo(BigDecimal.ZERO) > 0) {
        // 检查余额是否足够
        if (account.getServiceBalance().compareTo(firstMonthServiceFee) >= 0) {
            // 更新账户余额（扣除）
            account.setServiceBalance(account.getServiceBalance().subtract(firstMonthServiceFee));
            account.setTotalBalance(account.getTotalBalance().subtract(firstMonthServiceFee));
            accountInfoService.updateAccountInfo(account);

            // 生成监管账户划拨流水
            supervisionAccountLogService.recordTransferOut(
                order.getInstitutionId(),
                order.getOrderId(),
                firstMonthServiceFee,
                "首月服务费划拨-" + order.getOrderNo(),
                "基本账户"
            );

            logger.info("扣除首月服务费：" + firstMonthServiceFee + "元，订单号：" + order.getOrderNo());
        }
    }
}
```

### 参考实现

- **押金使用划拨**: `DepositApplyServiceImpl.java` 第268-275行
- **首月服务费划拨**: `H5OrderController.java` 第1056-1069行
- **监管账户流水服务**: `SupervisionAccountLogServiceImpl.java` 第132-165行
- **流水查询接口**: `BankReconciliationController.java` 第113-158行

### 流水记录字段说明

| 字段 | 说明 | 示例值 |
|------|------|--------|
| transactionType | 交易类型 | "支出" |
| businessType | 业务类型 | "押金划拨" |
| amount | 划拨金额 | 3000.00 |
| businessDesc | 业务描述 | "首月服务费划拨-ORD202501270001" |
| counterparty | 交易对手 | "基本账户" |
| relatedOrderId | 关联订单ID | 123 |
| relatedTransferId | 关联划拨ID | 456 |

## 相关文档
- **功能描述**: `功能描述.md` - 完整的功能需求文档
- **业务流程**: `业务流程详解.md` - 详细的业务流程说明
- **开发列表**: `开发列表.md` - 功能开发计划和指导
- **修改记录**: `xiugai.md` - 项目历史变更记录（已停止更新，改为更新CLAUDE.md）## 2025-12-20 修复评价提交数据库字段错误

### 问题描述
H5端评价提交时出现数据库字段错误：
- 错误信息：Unknown column 'create_by' in 'field list'
- 原因：institution_review表没有create_by字段，但代码试图插入该字段

### 修复内容

#### 1. 修改H5ReviewController.java
**文件位置**：ruoyi-admin/src/main/java/com/ruoyi/web/controller/h5/H5ReviewController.java

**修改内容**：
- 移除对create_by字段的设置
- 添加IOrderInfoService依赖注入
- 在评价提交时从订单获取institution_id和elder_id

**具体修改**：


#### 2. 修改InstitutionReviewMapper.xml
**文件位置**：ruoyi-admin/src/main/resources/mapper/pension/InstitutionReviewMapper.xml

**修改内容**：
- 移除insert语句中create_by字段的引用（第138行和第155行）

**具体修改**：


#### 3. 添加必要的import
**文件位置**：H5ReviewController.java

**添加import**：


### 测试验证
修复后的评价功能应该能够：
- 正确提交评价数据到数据库
- 自动从订单中获取机构和老人信息
- 避免数据库字段错误


