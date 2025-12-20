-- 费用记录表
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '记录ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `account_id` bigint(20) DEFAULT NULL COMMENT '账户ID',
  `expense_type` varchar(20) NOT NULL COMMENT '费用类型(deposit:押金,service:服务费,member:会员费,other:其他)',
  `transaction_type` varchar(20) NOT NULL COMMENT '交易类型(income:收入,expense:支出)',
  `amount` decimal(10,2) NOT NULL COMMENT '金额',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `related_id` bigint(20) DEFAULT NULL COMMENT '关联ID(如订单ID、押金申请ID等)',
  `related_type` varchar(50) DEFAULT NULL COMMENT '关联类型',
  `balance_before` decimal(10,2) DEFAULT NULL COMMENT '交易前余额',
  `balance_after` decimal(10,2) DEFAULT NULL COMMENT '交易后余额',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`record_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_expense_type` (`expense_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='费用记录表';