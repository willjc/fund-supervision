-- 订单支付管理相关数据库表
-- 创建时间: 2025-10-28
-- 说明: 养老机构订单支付功能相关表结构

-- 1. 订单主表
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `order_type` char(1) NOT NULL COMMENT '订单类型(1床位费 2护理费 3餐饮费 4医疗费 5其他费用)',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `check_in_id` bigint(20) DEFAULT NULL COMMENT '入住申请ID',
  `bed_id` bigint(20) DEFAULT NULL COMMENT '床位ID',
  `order_amount` decimal(10,2) NOT NULL COMMENT '订单总金额(元)',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已付金额(元)',
  `order_status` char(1) DEFAULT '0' COMMENT '订单状态(0待支付 1已支付 2已取消 3已退款)',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `order_date` date NOT NULL COMMENT '订单日期',
  `service_start_date` date DEFAULT NULL COMMENT '服务开始日期',
  `service_end_date` date DEFAULT NULL COMMENT '服务结束日期',
  `billing_cycle` varchar(20) DEFAULT NULL COMMENT '计费周期(月度、季度、年度)',
  `due_date` date DEFAULT NULL COMMENT '到期日期',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '���新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_check_in_id` (`check_in_id`),
  KEY `idx_order_date` (`order_date`),
  KEY `idx_order_status` (`order_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 2. 订单明细表
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `item_name` varchar(100) NOT NULL COMMENT '项目名称',
  `item_type` varchar(50) DEFAULT NULL COMMENT '项目类型',
  `item_description` varchar(500) DEFAULT NULL COMMENT '项目描述',
  `unit_price` decimal(10,2) NOT NULL COMMENT '单价(元)',
  `quantity` int(11) NOT NULL DEFAULT '1' COMMENT '数量',
  `total_amount` decimal(10,2) NOT NULL COMMENT '小计金额(元)',
  `service_period` varchar(50) DEFAULT NULL COMMENT '服务周期',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`item_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';

-- 3. 支付记录表
DROP TABLE IF EXISTS `payment_record`;
CREATE TABLE `payment_record` (
  `payment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '支付记录ID',
  `payment_no` varchar(50) NOT NULL COMMENT '支付流水号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `payment_amount` decimal(10,2) NOT NULL COMMENT '支付金额(元)',
  `payment_method` varchar(50) NOT NULL COMMENT '支付方式(微信 支付宝 银行卡转账 现金)',
  `payment_status` char(1) DEFAULT '0' COMMENT '支付状态(0处理中 1成功 2���败)',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `transaction_id` varchar(100) DEFAULT NULL COMMENT '第三方交易号',
  `gateway_response` text COMMENT '支付网关响应信息',
  `operator` varchar(50) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`payment_id`),
  UNIQUE KEY `uk_payment_no` (`payment_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_payment_time` (`payment_time`),
  KEY `idx_payment_status` (`payment_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='支付记录表';

-- 4. 退款记录表
DROP TABLE IF EXISTS `refund_record`;
CREATE TABLE `refund_record` (
  `refund_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '退款记录ID',
  `refund_no` varchar(50) NOT NULL COMMENT '退款单号',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `payment_id` bigint(20) NOT NULL COMMENT '支付记录ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `refund_amount` decimal(10,2) NOT NULL COMMENT '退款金额(元)',
  `refund_reason` varchar(500) DEFAULT NULL COMMENT '退款原因',
  `refund_status` char(1) DEFAULT '0' COMMENT '退款状态(0申请中 1已退款 2已拒绝)',
  `refund_method` varchar(50) DEFAULT NULL COMMENT '退款方式',
  `refund_time` datetime DEFAULT NULL COMMENT '退款时间',
  `approver` varchar(50) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`refund_id`),
  UNIQUE KEY `uk_refund_no` (`refund_no`),
  KEY `idx_order_id` (`order_id`),
  KEY `idx_payment_id` (`payment_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_refund_status` (`refund_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='退款记录表';

-- 添加字典类型
INSERT IGNORE INTO `sys_dict_type` VALUES (300, '订单类型', 'order_type', '0', 'admin', NOW(), 'ry', NOW(), '订单类型字典类型');
INSERT IGNORE INTO `sys_dict_type` VALUES (301, '订单状态', 'order_status', '0', 'admin', NOW(), 'ry', NOW(), '订单状态字典类型');
INSERT IGNORE INTO `sys_dict_type` VALUES (302, '支付方式', 'payment_method', '0', 'admin', NOW(), 'ry', NOW(), '支付方式字典类型');
INSERT IGNORE INTO `sys_dict_type` VALUES (303, '支付状态', 'payment_status', '0', 'admin', NOW(), 'ry', NOW(), '支付状态字典类型');
INSERT IGNORE INTO `sys_dict_type` VALUES (304, '退款状态', 'refund_status', '0', 'admin', NOW(), 'ry', NOW(), '退款状态字典类型');
INSERT IGNORE INTO `sys_dict_type` VALUES (305, '计费周期', 'billing_cycle', '0', 'admin', NOW(), 'ry', NOW(), '计费周期字典类型');

-- 添加字典数据
INSERT IGNORE INTO `sys_dict_data` (dict_code, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES
-- 订单类型字典
(300, '床位费', '1', 'order_type', '', 'primary', 'N', '0', 'admin', NOW(), '床位使用费用'),
(301, '护理费', '2', 'order_type', '', 'success', 'N', '0', 'admin', NOW(), '护理服务费用'),
(302, '餐饮费', '3', 'order_type', '', 'warning', 'N', '0', 'admin', NOW(), '餐饮服务费用'),
(303, '医疗费', '4', 'order_type', '', 'danger', 'N', '0', 'admin', NOW(), '医疗服务费用'),
(304, '其他费用', '5', 'order_type', '', 'info', 'N', '0', 'admin', NOW(), '其他杂项费用'),

-- 订单状态字典
(305, '待支付', '0', 'order_status', '', 'warning', 'N', '0', 'admin', NOW(), '订单待支付'),
(306, '已支付', '1', 'order_status', '', 'success', 'N', '0', 'admin', NOW(), '订单已支付'),
(307, '已取消', '2', 'order_status', '', 'info', 'N', '0', 'admin', NOW(), '订单已取消'),
(308, '已退款', '3', 'order_status', '', 'danger', 'N', '0', 'admin', NOW(), '订单已退款'),

-- 支付方式字典
(309, '微信支付', 'wechat', 'payment_method', '', 'success', 'N', '0', 'admin', NOW(), '微信扫码支付'),
(310, '支付宝', 'alipay', 'payment_method', '', 'primary', 'N', '0', 'admin', NOW(), '支付宝支付'),
(311, '银行卡转账', 'bank', 'payment_method', '', 'info', 'N', '0', 'admin', NOW(), '银行转账支付'),
(312, '现金', 'cash', 'payment_method', '', 'warning', 'N', '0', 'admin', NOW(), '现金支付'),

-- 支付状态字典
(313, '处理中', '0', 'payment_status', '', 'warning', 'N', '0', 'admin', NOW(), '支付处理中'),
(314, '支付成功', '1', 'payment_status', '', 'success', 'N', '0', 'admin', NOW(), '支付成功'),
(315, '支付失败', '2', 'payment_status', '', 'danger', 'N', '0', 'admin', NOW(), '支付失败'),

-- 退款状态字典
(316, '申请中', '0', 'refund_status', '', 'warning', 'N', '0', 'admin', NOW(), '退款申请中'),
(317, '已退款', '1', 'refund_status', '', 'success', 'N', '0', 'admin', NOW(), '退款已完成'),
(318, '已拒绝', '2', 'refund_status', '', 'danger', 'N', '0', 'admin', NOW(), '退款申请被拒绝'),

-- 计费周期字典
(319, '月度', 'monthly', 'billing_cycle', '', 'primary', 'N', '0', 'admin', NOW(), '按月计费'),
(320, '季度', 'quarterly', 'billing_cycle', '', 'success', 'N', '0', 'admin', NOW(), '按季度计费'),
(321, '年度', 'yearly', 'billing_cycle', '', 'warning', 'N', '0', 'admin', NOW(), '按年计费'),
(322, '一次性', 'once', 'billing_cycle', '', 'info', 'N', '0', 'admin', NOW(), '一次性费用');

-- 初始化一些测试订单数据
INSERT IGNORE INTO `order_info` (`order_no`, `order_type`, `elder_id`, `institution_id`, `check_in_id`, `bed_id`, `order_amount`, `order_status`, `order_date`, `service_start_date`, `service_end_date`, `billing_cycle`, `due_date`, `create_by`, `create_time`) VALUES
('ORD20251028001', '1', 1, 1, 1, 1, 2000.00, '1', '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', 'admin', '2025-10-28 16:00:00'),
('ORD20251028002', '2', 1, 1, 1, NULL, 1500.00, '0', '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', 'admin', '2025-10-28 16:00:00'),
('ORD20251028003', '3', 1, 1, 1, NULL, 800.00, '0', '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', 'admin', '2025-10-28 16:00:00');

-- 添加对应的订单明细
INSERT IGNORE INTO `order_item` (`order_id`, `item_name`, `item_type`, `item_description`, `unit_price`, `quantity`, `total_amount`, `service_period`, `create_by`, `create_time`) VALUES
(1, '101房间01号床位使用费', '床位费', '101房间01号床位，月度使用费', 2000.00, 1, 2000.00, '2025-10', 'admin', '2025-10-28 16:00:00'),
(2, '半护理服务费', '护理费', '月度半护理服务，包含基础护理和健康管理', 1500.00, 1, 1500.00, '2025-10', 'admin', '2025-10-28 16:00:00'),
(3, '月度餐饮服务费', '餐饮费', '一日三餐营养餐食服务', 800.00, 1, 800.00, '2025-10', 'admin', '2025-10-28 16:00:00');

-- 添加一条支付记录
INSERT IGNORE INTO `payment_record` (`payment_no`, `order_id`, `elder_id`, `institution_id`, `payment_amount`, `payment_method`, `payment_status`, `payment_time`, `transaction_id`, `operator`, `create_by`, `create_time`) VALUES
('PAY20251028001', 1, 1, 1, 2000.00, 'bank', '1', '2025-10-28 16:30:00', 'BANK20251028001', 'admin', 'admin', '2025-10-28 16:30:00');