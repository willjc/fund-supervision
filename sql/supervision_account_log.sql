-- 监管账户流水表
CREATE TABLE `supervision_account_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '交易流水号',
  `transaction_type` varchar(20) NOT NULL COMMENT '交易类型(收入/支出)',
  `amount` decimal(15,2) NOT NULL COMMENT '交易金额',
  `balance_before` decimal(15,2) DEFAULT NULL COMMENT '交易前余额',
  `balance_after` decimal(15,2) DEFAULT NULL COMMENT '交易后余额',
  `transaction_time` datetime NOT NULL COMMENT '交易时间',
  `business_type` varchar(50) DEFAULT NULL COMMENT '业务类型(用户支付/押金划拨/退款等)',
  `related_order_id` bigint(20) DEFAULT NULL COMMENT '关联订单ID',
  `related_transfer_id` bigint(20) DEFAULT NULL COMMENT '关联划拨ID',
  `business_desc` varchar(500) DEFAULT NULL COMMENT '业务描述',
  `counterparty` varchar(100) DEFAULT NULL COMMENT '交易对手',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_transaction_time` (`transaction_time`),
  KEY `idx_business_type` (`business_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='监管账户流水表';
