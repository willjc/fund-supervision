# 养老机构监管平台 - H5端

> 基于Vue3 + Vant4的移动端应用

## 项目介绍

本项目是养老机构预收费资金监管平台的H5移动端,为家属和用户提供便捷的移动端访问入口。

### 主要功能

- 用户中心: 登录、个人信息、退出登录
- 老人信息: 查看、管理关联的老人信息
- 费用查询: 查看老人费用明细和缴费记录
- 押金管理: 查看押金余额、审批押金使用申请
- 机构查询: 浏览养老机构列表和详情
- 通知公告: 接收和查看系统通知
- 待办事项: 管理需要处理的事项

## 技术栈

- 框架: Vue 3.2
- UI组件: Vant 4.9
- 路由: Vue Router 4.6
- 状态管理: Pinia 3.0
- HTTP客户端: Axios 1.13
- 日期处理: Day.js 1.11
- 移动端适配: postcss-pxtorem + amfe-flexible

## 快速开始

### 开发环境运行
```bash
npm install
npm run serve
```
访问: http://localhost:8081

### 生产环境构建
```bash
npm run build
```
或使用部署脚本:
- Windows: 双击 deploy.bat
- Linux/Mac: chmod +x deploy.sh && ./deploy.sh

## 部署说明

### 快速部署(3步)

1. 构建项目
```bash
npm run build
```

2. 上传到服务器
```bash
scp -r dist/* root@服务器IP:/var/www/h5/
```

3. 配置Nginx (参考 nginx.conf.example)

### 详细文档

- 完整部署指南: ../H5部署指南.md
- 快速参考: 部署速查卡.md
- API接口文档: ../H5接口.md

## 环境配置

- 开发环境: .env.development (API地址: /api)
- 生产环境: .env.production (API地址: http://jg.dayushaiwang.com)

## 常见问题

1. 页面刷新404: Nginx需要配置 try_files $uri $uri/ /index.html;
2. API跨域: 后端配置CORS或使用Nginx反向代理
3. 移动端适配: 使用 .norem class 避免rem转换

## 更新日志

### v0.1.0 (2025-01-16)
- 完成用户登录/登出功能
- 完成用户中心页面
- 完成老人信息展示
- 集成押金管理API
- 配置生产环境部署
