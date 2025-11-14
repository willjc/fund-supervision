# 养老机构监管平台 H5 移动端

基于 Vue 3 + Vant 4 的移动端家属应用

## 技术栈

- **Vue 3.4** - 渐进式 JavaScript 框架
- **Vant 4** - 移动端组件库
- **Vue Router 4** - 官方路由管理器
- **Pinia** - 新一代状态管理
- **Axios** - HTTP 客户端
- **postcss-pxtorem** - 自动转换 px 到 rem
- **amfe-flexible** - 移动端适配方案

## 功能模块

### 1. 首页
- 搜索养老机构
- 轮播图展示
- 快捷入口(押金管理、养老机构、我的订单、客服咨询)
- 推荐机构列表

### 2. 养老机构
- 机构列表(支持搜索、筛选、排序)
- 机构详情(信息、联系方式、服务项目、设施环境)
- 申请入住

### 3. 押金管理(核心功能)
- **押金余额**: 查看账户总余额和老人账户列表
- **申请列表**: 查看押金使用申请(待审批、已通过、已拒绝)
- **申请详情**: 查看详细信息、附件材料、审批流程
- **押金审批**: 家属审批押金使用申请(同意/拒绝)

### 4. 我的
- 用户信息展示
- 快捷功能(押金管理、我的订单)
- 常用工具(设置、帮助与反馈、关于我们)
- 退出登录

### 5. 用户认证
- 手机号密码登录
- 验证码登录(预留)
- 用户信息管理

## 项目结构

```
ruoyi-h5/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API 接口
│   │   ├── user.js       # 用户相关接口
│   │   ├── institution.js # 养老机构接口
│   │   └── deposit.js    # 押金管理接口
│   ├── assets/            # 资源文件
│   ├── components/        # 通用组件
│   │   ├── TabBar.vue    # 底部导航栏
│   │   └── InstitutionCard.vue # 机构卡片
│   ├── router/            # 路由配置
│   │   └── index.js
│   ├── store/             # 状态管理
│   │   ├── index.js
│   │   └── modules/
│   │       ├── user.js   # 用户状态
│   │       └── app.js    # 应用状态
│   ├── utils/             # 工具函数
│   │   ├── request.js    # axios 封装
│   │   ├── auth.js       # 认证相关
│   │   ├── format.js     # 格式化工具
│   │   └── validate.js   # 验证工具
│   ├── views/             # 页面组件
│   │   ├── home/         # 首页
│   │   ├── institution/  # 养老机构
│   │   ├── deposit/      # 押金管理
│   │   └── user/         # 用户中心
│   ├── App.vue
│   └── main.js
├── .env.development       # 开发环境变量
├── .env.production        # 生产环境变量
├── vue.config.js          # Vue CLI 配置
└── package.json
```

## 开发指南

### 安装依赖
```bash
npm install
```

### 启动开发服务器
```bash
npm run serve
```
访问 http://localhost:8081

### 生产环境构建
```bash
npm run build
```

### 代码检查
```bash
npm run lint
```

## 核心功能说明

### 押金审批流程

1. **机构提交申请**: 养老机构工作人员在 PC 端提交押金使用申请
2. **家属审批**: 家属在 H5 端收到通知,查看申请详情并审批(同意/拒绝)
3. **监管审批**: 家属同意后,民政监管部门进行二次审批
4. **拨付完成**: 监管审批通过后,系统自动拨付押金

### 状态流转

```
待家属审批(pending_family)
  → 家属已同意(family_approved)
  → 待监管审批(pending_supervision)
  → 已通过(approved)
  → 拨付完成(actualAmount > 0)

或

待家属审批(pending_family)
  → 已拒绝(rejected)
```

## API 接口说明

### 基础配置

- **开发环境**: `/api` (通过 vue.config.js 代理到 http://localhost:8080)
- **生产环境**: 需要在 `.env.production` 中配置实际服务器地址

### 接口规范

所有接口返回格式:
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {},
  "rows": [],
  "total": 0
}
```

### 主要接口

#### 用户相关
- `POST /h5/user/login` - 用户登录
- `GET /h5/user/info` - 获取用户信息
- `POST /h5/user/logout` - 退出登录

#### 养老机构
- `GET /h5/institution/list` - 获取机构列表
- `GET /h5/institution/{id}` - 获取机构详情

#### 押金管理
- `GET /h5/deposit/account/list` - 获取账户列表
- `GET /h5/deposit/apply/list` - 获取申请列表
- `GET /h5/deposit/apply/{id}` - 获取申请详情
- `POST /h5/deposit/apply/family/approve` - 提交家属审批

## 移动端适配

项目使用 `postcss-pxtorem` + `amfe-flexible` 实现移动端适配:

- 设计稿基准: 375px
- rootValue: 37.5
- 自动将 px 转换为 rem
- 动态设置根元素字体大小

### 使用方式

直接使用 px 单位,会自动转换为 rem:
```css
.box {
  width: 100px;  /* 自动转换为 2.666rem */
  height: 100px;
}
```

如果不希望转换,使用 `.norem` 类名:
```css
.norem-box {
  width: 100px;  /* 不会转换 */
}
```

## 注意事项

1. **路由守卫**: 需要登录的页面设置 `meta: { requireAuth: true }`
2. **Token 管理**: Token 存储在 Cookie 中,自动在请求头中携带
3. **状态持久化**: 用户信息存储在 localStorage,刷新页面不会丢失
4. **TabBar 显示**: 只在首页、养老机构、我的页面显示底部导航
5. **组件按需引入**: Vant 组件通过 `unplugin-vue-components` 自动按需引入

## 后续开发建议

1. **实时通知**: 接入 WebSocket 或轮询,实现审批通知推送
2. **文件预览**: 实现附件材料的在线预览功能
3. **电子签名**: 审批环节添加电子签名功能
4. **数据统计**: 添加押金使用统计图表
5. **消息中心**: 添加消息通知中心
6. **客服功能**: 接入在线客服系统
