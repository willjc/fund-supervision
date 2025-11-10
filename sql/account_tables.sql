-- 账户资金管理相关数据库表
USE newzijin;

-- 1. 老人账户信息表
CREATE TABLE `account_info` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `account_no` varchar(50) NOT NULL COMMENT '账户编号',
  `account_name` varchar(100) NOT NULL COMMENT '账户名称',
  `account_status` char(1) DEFAULT '1' COMMENT '账户状态(0冻结 1正常 2销户)',
  `total_balance` decimal(12,2) DEFAULT 0.00 COMMENT '总余额',
  `service_balance` decimal(12,2) DEFAULT 0.00 COMMENT '服务费余额',
  `deposit_balance` decimal(12,2) DEFAULT 0.00 COMMENT '押金余额',
  `member_balance` decimal(12,2) DEFAULT 0.00 COMMENT '会员费余额',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_account_no` (`account_no`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人账户信息表';

-- 2. 资金划拨记录表
CREATE TABLE `fund_transfer` (
  `transfer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '划拨ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `transfer_no` varchar(50) NOT NULL COMMENT '划拨单号',
  `transfer_type` char(1) NOT NULL COMMENT '划拨类型(1自动划拨 2手动划拨 3特殊申请)',
  `transfer_amount` decimal(15,2) NOT NULL COMMENT '划拨金额',
  `transfer_date` date NOT NULL COMMENT '划拨日期',
  `transfer_period` varchar(20) DEFAULT NULL COMMENT '划拨期间(如2025-12)',
  `elder_count` int(11) DEFAULT 0 COMMENT '涉及老人数量',
  `transfer_status` char(1) DEFAULT '0' COMMENT '划拨状态(0待处理 1成功 2失败)',
  `bank_order_no` varchar(100) DEFAULT NULL COMMENT '银行订单号',
  `failure_reason` varchar(500) DEFAULT NULL COMMENT '失败原因',
  `approve_user` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `execute_user` varchar(64) DEFAULT NULL COMMENT '执行人',
  `execute_time` datetime DEFAULT NULL COMMENT '执行时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_transfer_date` (`transfer_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨记录表';

-- 3. 交易流水记录表
CREATE TABLE `transaction_record` (
  `transaction_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '交易流水号',
  `transaction_type` char(1) NOT NULL COMMENT '交易类型(1入账 2出账)',
  `business_type` char(1) NOT NULL COMMENT '业务类型(1缴费 2月度扣费 3押金使用 4退费)',
  `amount` decimal(12,2) NOT NULL COMMENT '交易金额',
  `balance_before` decimal(12,2) NOT NULL COMMENT '交易前余额',
  `balance_after` decimal(12,2) NOT NULL COMMENT '交易后余额',
  `service_balance` decimal(12,2) DEFAULT 0.00 COMMENT '服务费余额',
  `deposit_balance` decimal(12,2) DEFAULT 0.00 COMMENT '押金余额',
  `member_balance` decimal(12,2) DEFAULT 0.00 COMMENT '会员费余额',
  `transaction_date` datetime NOT NULL COMMENT '交易时间',
  `related_order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `related_transfer_id` bigint(20) DEFAULT NULL COMMENT '关联划拨ID',
  `counterparty` varchar(100) DEFAULT NULL COMMENT '交易对手',
  `business_desc` varchar(500) NOT NULL COMMENT '业务描述',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_business_type` (`business_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易流水记录表';

-- 4. 资金划拨明细表
CREATE TABLE `fund_transfer_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `transfer_id` bigint(20) NOT NULL COMMENT '划拨ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `transfer_amount` decimal(12,2) NOT NULL COMMENT '划拨金额',
  `service_fee` decimal(12,2) DEFAULT 0.00 COMMENT '服务费',
  `care_fee` decimal(12,2) DEFAULT 0.00 COMMENT '护理费',
  `food_fee` decimal(12,2) DEFAULT 0.00 COMMENT '伙食费',
  `other_fee` decimal(12,2) DEFAULT 0.00 COMMENT '其他费用',
  `billing_month` varchar(7) NOT NULL COMMENT '结算月份(YYYY-MM)',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`detail_id`),
  KEY `idx_transfer_id` (`transfer_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_billing_month` (`billing_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨明细表';

-- 5. 账户余额变动记录表
CREATE TABLE `account_balance_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `transaction_id` bigint(20) NOT NULL COMMENT '交易ID',
  `balance_type` char(1) NOT NULL COMMENT '余额类型(1总余额 2服务费 3押金 4会员费)',
  `amount_before` decimal(12,2) NOT NULL COMMENT '变动前余额',
  `amount_change` decimal(12,2) NOT NULL COMMENT '变动金额',
  `amount_after` decimal(12,2) NOT NULL COMMENT '变动后余额',
  `change_type` char(1) NOT NULL COMMENT '变动类型(1增加 2减少)',
  `business_type` char(1) NOT NULL COMMENT '业务类型',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='账户余额变动记录表';

-- 6. 资金划拨规则配置表
CREATE TABLE `fund_transfer_rule` (
  `rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '规则ID',
  `rule_name` varchar(100) NOT NULL COMMENT '规则名称',
  `transfer_cycle` char(1) NOT NULL COMMENT '划拨周期(1每月 2每季度 3每半年)',
  `transfer_day` int(2) NOT NULL COMMENT '划拨日(如每月1日)',
  `advance_days` int(2) DEFAULT 0 COMMENT '提前天数',
  `min_balance_ratio` decimal(5,2) DEFAULT 0.20 COMMENT '最低余额比例',
  `auto_approve` char(1) DEFAULT '1' COMMENT '自动审批(0否 1是)',
  `status` char(1) DEFAULT '1' COMMENT '状态(0禁用 1启用)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨规则配置表';

-- 插入默认划拨规则
INSERT INTO `fund_transfer_rule` (`rule_name`, `transfer_cycle`, `transfer_day`, `advance_days`, `min_balance_ratio`, `auto_approve`, `status`, `create_by`, `create_time`, `remark`) VALUES
('Default Monthly Transfer Rule', '1', '1', '0', '0.20', '1', '1', 'system', NOW(), 'Monthly transfer on day 1, keep 20% as risk deposit');