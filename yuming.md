# 预警系统说明文档

## 一、概述

养老机构预收费资金监管平台的预警系统，用于自动检测和提示各类资金风险。

## 二、预警类型配置

| 类型编码 | 预警事项 | 检测规则 | 阈值单位 | 数据来源 |
|---------|---------|---------|----------|----------|
| 1 | 预收费用超额 | 老人预收费余额 ÷ 月费用 > 阈值 | 倍 | account_info, elder_check_in |
| 2 | 押金超额 | 老人押金余额 ÷ 床位费 > 阈值 | 倍 | account_info, bed_info |
| 3 | 入住人数超额 | 实际入住人数 ÷ 床位数 > 阈值 | 倍 | elder_check_in, bed_info |
| 4 | 预收总额超额 | 账户总额 ÷ 固定资产净额 > 阈值 | 倍 | account_info, pension_institution |
| 5 | 风险保证金超低 | 账户余额 < 固定资产 × 阈值% | 百分比 | account_info, pension_institution |
| 6 | 大额支出 | 单笔支付金额 > 阈值 | 元 | order_info |
| 7 | 交易对方风险 | 手动触发 | 开关 | - |

## 三、预警配置页面

**路径**: 民���监管端 → 预警管理 → 预警配置

**功能**:
- 卡片式展示7种预警规则
- 可配置每种规则的阈值参数
- 启用/禁用开关控制是否执行检测

**默认阈值**:
```
预收费用超额: 15倍
押金超额: 12倍
入住人数超额: 1倍
预收总额超额: 1倍
风险保证金超低: 80%
大额支出: 50000元
交易对方风险: 开关
```

## 四、预警列表页面

**路径**: 民政监管端 → 预警管理 → 预警列表

**显示字段**:
- 预警时间
- 机构名称
- 预警事项（带标签颜色）
- 预警内容
- 机构联系人
- 联系方式
- 状态（待处理/已处理）

**操作**:
- **详情**: 查看预警完整信息
- **处理**: 填写备注，点击确定后状态变更为已处理

## 五、定时任务配置

**任务信息**:
| 配置项 | 值 |
|-------|-----|
| 任务名称 | 预警检测任务 |
| 调用目标 | warningDetectTask.detectWarnings() |
| 执行表达式 | `0 */30 * * * ?` |
| 执行频率 | 每30分钟一次 |

**执行流程**:
```
1. 从 warning_rule_config 表获取所有启用的规则（enabled='1'）
2. 遍历每种规则，执行对应的检测逻辑
3. 判断是否超过配置的阈值
4. 如果超过且24小时内未重复，则生成预警记录
5. 记录到 supervision_warning 表
```

## 六、防重复机制

- 24小时内相同机构的相同预警内容不会重复生成
- 只检查未处理状态的预警（warning_status='0'）
- 处理后状态变为'1'，不再参与重复判断

## 七、相关数据表

| 表名 | 说明 |
|------|------|
| warning_rule_config | 预警规则配置表 |
| supervision_warning | 预警记录表 |
| account_info | 老人账户表 |
| elder_check_in | 入住管理表 |
| bed_info | 床位信息表 |
| pension_institution | 机构信息表 |
| order_info | 订单表 |
| sys_job | 定时任务配置表 |

## 八、代码文件位置

```
后端:
├── ruoyi-admin/src/main/java/com/ruoyi/
│   ├── domain/pension/SupervisionWarning.java      # 预警实体
│   ├── mapper/pension/SupervisionWarningMapper.java # Mapper接口
│   ├── service/pension/ISupervisionWarningService.java # Service接口
│   ├── controller/supervision/WarningController.java  # 预警控制器
│   └── task/WarningDetectTask.java                  # 定时任务

前端:
└── ruoyi-ui/src/
    ├── views/supervision/warning/index.vue          # 预警列表页
    ├── views/supervision/warning/config.vue         # 预警配置页
    └── api/supervision/warning.js                   # 预警API
```
