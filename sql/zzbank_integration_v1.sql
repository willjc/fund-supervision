-- 郑州银行对接第一阶段：机构商户号与监管账户绑定
-- 执行前请先备份数据库；本脚本只建表，不写入业务数据。

CREATE TABLE IF NOT EXISTS `bank_merchant_config` (
  `config_id` bigint NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `institution_id` bigint NOT NULL COMMENT '养老机构ID',
  `bank_code` varchar(32) NOT NULL DEFAULT 'ZZBANK' COMMENT '银行编码',
  `bank_name` varchar(100) NOT NULL DEFAULT '郑州银行' COMMENT '银行名称',
  `mer_id` varchar(64) NOT NULL COMMENT '银行商户号',
  `merchant_name` varchar(200) NOT NULL COMMENT '银行商户名称',
  `settlement_account_no` varchar(64) NOT NULL COMMENT '收单结算监管账户',
  `settlement_account_name` varchar(200) NOT NULL COMMENT '监管账户名称',
  `basic_account_no` varchar(64) DEFAULT NULL COMMENT '机构基本账户',
  `channel_type` varchar(32) NOT NULL DEFAULT 'H5' COMMENT '接入渠道',
  `environment` varchar(16) NOT NULL DEFAULT 'sandbox' COMMENT '环境:sandbox/prod',
  `verify_status` char(1) NOT NULL DEFAULT '0' COMMENT '银行验证状态:0待验证,1已验证,2失败',
  `verify_message` varchar(500) DEFAULT NULL COMMENT '最近银行验证结果说明',
  `last_verified_time` datetime DEFAULT NULL COMMENT '最近银行验证时间',
  `is_default` char(1) NOT NULL DEFAULT '0' COMMENT '是否机构默认商户号:0否,1是',
  `default_institution_id` bigint GENERATED ALWAYS AS (
    CASE WHEN `is_default` = '1' THEN `institution_id` ELSE NULL END
  ) STORED COMMENT '默认商户唯一约束辅助列',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '启用状态:0停用,1启用',
  `create_by` varchar(64) DEFAULT '',
  `create_time` datetime DEFAULT NULL,
  `update_by` varchar(64) DEFAULT '',
  `update_time` datetime DEFAULT NULL,
  `remark` varchar(500) DEFAULT NULL,
  PRIMARY KEY (`config_id`),
  UNIQUE KEY `uk_bank_mer_id` (`bank_code`, `mer_id`),
  UNIQUE KEY `uk_default_merchant_per_institution` (`default_institution_id`),
  KEY `idx_merchant_institution` (`institution_id`),
  KEY `idx_merchant_status` (`status`, `verify_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='银行商户号与机构监管账户绑定';

CREATE TABLE IF NOT EXISTS `bank_transaction` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT COMMENT '银行交易ID',
  `request_no` varchar(64) NOT NULL COMMENT '平台请求流水号',
  `business_type` varchar(32) NOT NULL COMMENT '业务类型:PAY/REFUND/TRANSFER/VERIFY',
  `business_id` bigint NOT NULL COMMENT '业务主键',
  `institution_id` bigint NOT NULL COMMENT '养老机构ID',
  `mer_id` varchar(64) NOT NULL COMMENT '银行商户号',
  `bank_code` varchar(32) NOT NULL DEFAULT 'ZZBANK',
  `channel_type` varchar(32) DEFAULT NULL,
  `amount` decimal(15,2) NOT NULL DEFAULT '0.00',
  `status` varchar(16) NOT NULL COMMENT 'PENDING/SUCCESS/FAILED/UNKNOWN',
  `bank_serial_no` varchar(128) DEFAULT NULL,
  `pay_url` varchar(1000) DEFAULT NULL,
  `response_code` varchar(64) DEFAULT NULL,
  `response_message` varchar(500) DEFAULT NULL,
  `complete_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_bank_request_no` (`request_no`),
  UNIQUE KEY `uk_bank_business` (`business_type`, `business_id`),
  KEY `idx_bank_transaction_institution` (`institution_id`, `create_time`),
  KEY `idx_bank_transaction_status` (`status`, `create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='银行交易幂等与审计记录';
