#!/bin/bash
# H5项目创建脚本

echo "开始创建ruoyi-h5项目..."

# 创建项目目录
mkdir -p ruoyi-h5/src/{api,assets/{images,styles},components,config,mixins,router,store/modules,utils,views}

# 创建views子目录
mkdir -p ruoyi-h5/src/views/{home,institution,order,user,account,todo,elder,notice,auth}

# 创建components子目录
mkdir -p ruoyi-h5/src/components/{TabBar,InstitutionCard,OrderCard,Empty}

echo "目录结构创建完成!"
echo "请手动执行: vue create ruoyi-h5 并选择Vue 2.x"
