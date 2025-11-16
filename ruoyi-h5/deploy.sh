#!/bin/bash

echo "================================"
echo "  H5端生产环境构建脚本"
echo "================================"
echo ""

# 检查npm是否安装
echo "[1/3] 检查环境..."
if ! command -v npm &> /dev/null; then
    echo "错误: 未找到npm命令,请先安装Node.js"
    exit 1
fi

# 构建项目
echo "[2/3] 开始构建..."
npm run build

if [ $? -ne 0 ]; then
    echo ""
    echo "构建失败! 请检查错误信息"
    exit 1
fi

echo ""
echo "[3/3] 构建完成!"
echo ""
echo "================================"
echo "  构建成功!"
echo "================================"
echo ""
echo "输出目录: dist/"
echo "包含文件:"
echo "  - index.html"
echo "  - static/css/"
echo "  - static/js/"
echo "  - static/img/"
echo ""
echo "后端API地址: http://jg.dayushaiwang.com"
echo ""
echo "下一步操作:"
echo "1. 将 dist/ 目录上传到服务器"
echo "2. 配置Nginx指向该目录"
echo "3. 访问H5页面测试"
echo ""
echo "详细部署步骤请参考: ../H5部署指南.md"
echo "================================"
