-- 养老机构业务表创建脚本
-- 数据库字符集: utf8mb4
-- 创建时间: 2025-10-29

USE newzijin;

-- 1. 养老机构信息表
DROP TABLE IF EXISTS `pension_institution`;
CREATE TABLE `pension_institution` (
  `institution_id` bigint NOT NULL AUTO_INCREMENT COMMENT '机构ID',
  `institution_code` varchar(50) NOT NULL COMMENT '机构编码',
  `institution_name` varchar(200) NOT NULL COMMENT '机构名称',
  `institution_type` char(1) DEFAULT '1' COMMENT '机构类型 1-养老院 2-护理院 3-日间照料中心',
  `license_no` varchar(100) DEFAULT NULL COMMENT '营业执照号',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法人代表',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `province` varchar(50) DEFAULT NULL COMMENT '省份',
  `city` varchar(50) DEFAULT NULL COMMENT '城市',
  `district` varchar(50) DEFAULT NULL COMMENT '区县',
  `address` varchar(500) DEFAULT NULL COMMENT '详细地址',
  `bed_count` int DEFAULT 0 COMMENT '床位数',
  `current_elders` int DEFAULT 0 COMMENT '当前老人数',
  `status` char(1) DEFAULT '0' COMMENT '机构状态 0-待审核 1-正常 2-停业',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`institution_id`),
  UNIQUE KEY `uk_institution_code` (`institution_code`),
  KEY `idx_institution_name` (`institution_name`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养老机构信息表';

-- 2. 机构附件材料表
DROP TABLE IF EXISTS `pension_institution_attach`;
CREATE TABLE `pension_institution_attach` (
  `attach_id` bigint NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `attach_type` char(1) DEFAULT '1' COMMENT '附件类型 1-营业执照 2-法人身份证 3-场所证明 4-其他',
  `attach_name` varchar(200) DEFAULT NULL COMMENT '附件名称',
  `file_path` varchar(500) DEFAULT NULL COMMENT '文件路径',
  `file_size` bigint DEFAULT 0 COMMENT '文件大小(字节)',
  `upload_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`attach_id`),
  KEY `fk_institution_attach` (`institution_id`),
  CONSTRAINT `fk_institution_attach` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构附件材料表';

-- 3. 老人基础信息表
DROP TABLE IF EXISTS `elder_info`;
CREATE TABLE `elder_info` (
  `elder_id` bigint NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `elder_no` varchar(50) NOT NULL COMMENT '老人编号',
  `elder_name` varchar(100) NOT NULL COMMENT '老人姓名',
  `gender` char(1) DEFAULT '1' COMMENT '性别 1-男 2-女',
  `id_card` varchar(18) DEFAULT NULL COMMENT '身份证号',
  `birth_date` date DEFAULT NULL COMMENT '出生日期',
  `age` int DEFAULT NULL COMMENT '年龄',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `family_phone` varchar(20) DEFAULT NULL COMMENT '家属联系电话',
  `family_name` varchar(50) DEFAULT NULL COMMENT '家属姓名',
  `relation` varchar(20) DEFAULT NULL COMMENT '与老人关系',
  `address` varchar(500) DEFAULT NULL COMMENT '家庭住址',
  `health_status` char(1) DEFAULT '1' COMMENT '健康状况 1-良好 2-一般 3-较差 4-失能',
  `care_level` char(1) DEFAULT '1' COMMENT '护理等级 1-自理 2-半护理 3-全护理',
  `institution_id` bigint NOT NULL COMMENT '所在机构ID',
  `check_in_date` date DEFAULT NULL COMMENT '入住日期',
  `check_out_date` date DEFAULT NULL COMMENT '退住日期',
  `room_no` varchar(20) DEFAULT NULL COMMENT '房间号',
  `bed_no` varchar(20) DEFAULT NULL COMMENT '床位号',
  `status` char(1) DEFAULT '1' COMMENT '状态 1-在住 2-退住 3-请假',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`elder_id`),
  UNIQUE KEY `uk_elder_no` (`elder_no`),
  KEY `idx_elder_name` (`elder_name`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_id_card` (`id_card`),
  CONSTRAINT `fk_elder_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人基础信息表';

-- 4. 老人账户信息表
DROP TABLE IF EXISTS `account_info`;
CREATE TABLE `account_info` (
  `account_id` bigint NOT NULL AUTO_INCREMENT COMMENT '账户ID',
  `account_no` varchar(50) NOT NULL COMMENT '账户编号',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `service_balance` decimal(15,2) DEFAULT 0.00 COMMENT '服务费余额',
  `deposit_balance` decimal(15,2) DEFAULT 0.00 COMMENT '押金余额',
  `member_balance` decimal(15,2) DEFAULT 0.00 COMMENT '会员费余额',
  `reserve_balance` decimal(15,2) DEFAULT 0.00 COMMENT '备用金余额',
  `total_balance` decimal(15,2) DEFAULT 0.00 COMMENT '总余额',
  `monthly_fee` decimal(10,2) DEFAULT 0.00 COMMENT '月费标准',
  `account_status` char(1) DEFAULT '1' COMMENT '账户状态 1-正常 0-冻结 2-销户',
  `open_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '开户时间',
  `close_date` datetime DEFAULT NULL COMMENT '销户时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_account_no` (`account_no`),
  KEY `fk_account_elder` (`elder_id`),
  KEY `fk_account_institution` (`institution_id`),
  KEY `idx_account_status` (`account_status`),
  CONSTRAINT `fk_account_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`),
  CONSTRAINT `fk_account_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人账户信息表';

-- 5. 订单主表
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `order_id` bigint NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `order_type` char(1) DEFAULT '1' COMMENT '订单类型 1-充值 2-消费 3-退费 4-划拨',
  `order_amount` decimal(15,2) DEFAULT 0.00 COMMENT '订单金额',
  `payment_method` char(1) DEFAULT '1' COMMENT '支付方式 1-现金 2-银行转账 3-微信 4-支付宝 5-Pos机',
  `payment_status` char(1) DEFAULT '0' COMMENT '支付状态 0-待支付 1-已支付 2-支付失败 3-已退款',
  `order_status` char(1) DEFAULT '0' COMMENT '订单状态 0-待处理 1-已完成 2-已取消 3-已退款',
  `order_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `complete_time` datetime DEFAULT NULL COMMENT '完成时间',
  `description` varchar(500) DEFAULT NULL COMMENT '订单描述',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`order_id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `fk_order_elder` (`elder_id`),
  KEY `fk_order_institution` (`institution_id`),
  KEY `idx_order_date` (`order_date`),
  KEY `idx_order_status` (`order_status`),
  CONSTRAINT `fk_order_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`),
  CONSTRAINT `fk_order_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单主表';

-- 6. 交易记录表
DROP TABLE IF EXISTS `transaction_record`;
CREATE TABLE `transaction_record` (
  `transaction_id` bigint NOT NULL AUTO_INCREMENT COMMENT '交易ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '交易编号',
  `order_id` bigint DEFAULT NULL COMMENT '订单ID',
  `account_id` bigint NOT NULL COMMENT '账户ID',
  `elder_id` bigint NOT NULL COMMENT '老人ID',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `transaction_type` char(1) DEFAULT '1' COMMENT '交易类型 1-充值 2-划拨 3-退费 4-消费',
  `transaction_amount` decimal(15,2) DEFAULT 0.00 COMMENT '交易金额',
  `account_type` char(1) DEFAULT '1' COMMENT '账户类型 1-服务费 2-押金 3-会员费 4-备用金',
  `before_balance` decimal(15,2) DEFAULT 0.00 COMMENT '交易前余额',
  `after_balance` decimal(15,2) DEFAULT 0.00 COMMENT '交易后余额',
  `transaction_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '交易时间',
  `description` varchar(500) DEFAULT NULL COMMENT '交易描述',
  `operator` varchar(64) DEFAULT NULL COMMENT '操作人',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `fk_transaction_order` (`order_id`),
  KEY `fk_transaction_account` (`account_id`),
  KEY `fk_transaction_elder` (`elder_id`),
  KEY `fk_transaction_institution` (`institution_id`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_transaction_type` (`transaction_type`),
  CONSTRAINT `fk_transaction_order` FOREIGN KEY (`order_id`) REFERENCES `order_info` (`order_id`),
  CONSTRAINT `fk_transaction_account` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`account_id`),
  CONSTRAINT `fk_transaction_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`),
  CONSTRAINT `fk_transaction_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='交易记录表';

-- 7. 资金划拨记录表
DROP TABLE IF EXISTS `fund_transfer`;
CREATE TABLE `fund_transfer` (
  `transfer_id` bigint NOT NULL AUTO_INCREMENT COMMENT '划拨ID',
  `transfer_no` varchar(50) NOT NULL COMMENT '划拨编号',
  `institution_id` bigint NOT NULL COMMENT '机构ID',
  `from_account_id` bigint NOT NULL COMMENT '转出账户ID',
  `to_account_id` bigint NOT NULL COMMENT '转入账户ID',
  `transfer_amount` decimal(15,2) DEFAULT 0.00 COMMENT '划拨金额',
  `transfer_type` char(1) DEFAULT '1' COMMENT '划拨类型 1-月度划拨 2-临时划拨 3-紧急划拨',
  `transfer_status` char(1) DEFAULT '0' COMMENT '划拨状态 0-待审批 1-已批准 2-已拒绝 3-已完成 4-已取消',
  `apply_date` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '申请时间',
  `approve_date` datetime DEFAULT NULL COMMENT '审批时间',
  `execute_date` datetime DEFAULT NULL COMMENT '执行时间',
  `approver` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批备注',
  `description` varchar(500) DEFAULT NULL COMMENT '划拨说明',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `fk_transfer_institution` (`institution_id`),
  KEY `fk_transfer_from_account` (`from_account_id`),
  KEY `fk_transfer_to_account` (`to_account_id`),
  KEY `idx_transfer_status` (`transfer_status`),
  KEY `idx_transfer_date` (`apply_date`),
  CONSTRAINT `fk_transfer_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`),
  CONSTRAINT `fk_transfer_from_account` FOREIGN KEY (`from_account_id`) REFERENCES `account_info` (`account_id`),
  CONSTRAINT `fk_transfer_to_account` FOREIGN KEY (`to_account_id`) REFERENCES `account_info` (`account_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨记录表';

-- 8. 余额预警记录表
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
  KEY `fk_warning_account` (`account_id`),
  KEY `fk_warning_elder` (`elder_id`),
  KEY `fk_warning_institution` (`institution_id`),
  KEY `idx_warning_type` (`warning_type`),
  KEY `idx_warning_status` (`warning_status`),
  KEY `idx_create_time` (`create_time`),
  CONSTRAINT `fk_warning_account` FOREIGN KEY (`account_id`) REFERENCES `account_info` (`account_id`),
  CONSTRAINT `fk_warning_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`),
  CONSTRAINT `fk_warning_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='余额预警记录表';