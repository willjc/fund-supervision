-- 创建余额预警记录表
USE newzijin;

DROP TABLE IF EXISTS `balance_warning`;
CREATE TABLE `balance_warning` (
  `warning_id` bigint NOT NULL AUTO_INCREMENT COMMENT '预警ID',
  `warning_no` varchar(50) NOT NULL COMMENT '预警编号',
  `account_id` bigint NOT NULL COMMENT '账户ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `warning_type` char(1) DEFAULT '1' COMMENT '预警等级 1-提示 2-警告 3-严重',
  `current_balance` decimal(15,2) DEFAULT 0.00 COMMENT '当前余额',
  `available_months` int DEFAULT 0 COMMENT '可用月数',
  `monthly_fee` decimal(10,2) DEFAULT 0.00 COMMENT '月费标准',
  `warning_reason` varchar(500) DEFAULT NULL COMMENT '预警原因',
  `suggestion` varchar(500) DEFAULT NULL COMMENT '建议措施',
  `warning_status` char(1) DEFAULT '0' COMMENT '预警状态 0-未处理 1-已处理 2-已忽略',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handler` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `is_notified` char(1) DEFAULT '0' COMMENT '是否已通知 0-未通知 1-已通知',
  `notify_time` datetime DEFAULT NULL COMMENT '通知时间',
  `notify_method` varchar(100) DEFAULT NULL COMMENT '通知方式',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`warning_id`),
  UNIQUE KEY `uk_warning_no` (`warning_no`),
  KEY `idx_warning_account` (`account_id`),
  KEY `idx_warning_elder` (`elder_id`),
  KEY `idx_warning_institution` (`institution_id`),
  KEY `idx_warning_type` (`warning_type`),
  KEY `idx_warning_status` (`warning_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额预警记录表';