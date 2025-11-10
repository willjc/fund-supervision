-- ----------------------------
-- 养老机构信息表
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution`;
CREATE TABLE `pension_institution` (
  `institution_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '机构ID',
  `institution_name` varchar(200) NOT NULL COMMENT '机构名称',
  `credit_code` varchar(50) NOT NULL COMMENT '统一信用代码',
  `registered_capital` decimal(15,2) DEFAULT NULL COMMENT '注册资金(万元)',
  `registered_address` varchar(500) DEFAULT NULL COMMENT '注册地址',
  `actual_address` varchar(500) DEFAULT NULL COMMENT '实际经营地址',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法定代表人',
  `contact_person` varchar(50) NOT NULL COMMENT '联系人',
  `contact_phone` varchar(20) NOT NULL COMMENT '联系电话',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '联系邮箱',
  `business_scope` varchar(1000) DEFAULT NULL COMMENT '经营范围',
  `institution_type` char(1) DEFAULT '1' COMMENT '机构类型(1民办 2公办 3公建民营)',
  `bed_count` int(11) DEFAULT NULL COMMENT '床位数',
  `established_date` date DEFAULT NULL COMMENT '成立日期',
  `record_number` varchar(100) DEFAULT NULL COMMENT '备案号',
  `fixed_assets` decimal(15,2) DEFAULT NULL COMMENT '固定资产净额(万元)',
  `bank_account` varchar(100) DEFAULT NULL COMMENT '基本结算账户',
  `supervise_account` varchar(100) DEFAULT NULL COMMENT '监管账户',
  `status` char(1) DEFAULT '0' COMMENT '状态(0待审批 1已入驻 2已驳回 3解除监管)',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_user` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `blacklist_flag` char(1) DEFAULT '0' COMMENT '黑名单标志(0正常 1黑名单)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`institution_id`),
  UNIQUE KEY `uk_credit_code` (`credit_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='养老机构信息表';

-- ----------------------------
-- 机构附件材料表
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution_attach`;
CREATE TABLE `pension_institution_attach` (
  `attach_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `attach_type` char(1) DEFAULT NULL COMMENT '附件类型(1营业执照 2社会福利机构设置批准证书 3三方监管协议)',
  `attach_name` varchar(200) DEFAULT NULL COMMENT '附件名称',
  `attach_path` varchar(500) DEFAULT NULL COMMENT '附件路径',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小(字节)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '文件类型',
  `create_by` varchar(64) DEFAULT '' COMMENT '上传者',
  `create_time` datetime DEFAULT NULL COMMENT '上传时间',
  PRIMARY KEY (`attach_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构附件材料表';

-- ----------------------------
-- 机构公示信息表
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution_public`;
CREATE TABLE `pension_institution_public` (
  `public_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '公示ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `institution_intro` text COMMENT '机构简介',
  `service_scope` varchar(1000) DEFAULT NULL COMMENT '服务范围',
  `service_features` varchar(1000) DEFAULT NULL COMMENT '特色服务',
  `land_area` decimal(10,2) DEFAULT NULL COMMENT '占地面积(平方米)',
  `building_area` decimal(10,2) DEFAULT NULL COMMENT '建筑面积(平方米)',
  `environment_imgs` text COMMENT '环境图片(多张，用逗号分隔)',
  `rating` char(1) DEFAULT '3' COMMENT '机构评级(1★ 2★★ 3★★★ 4★★★★ 5★★★★★)',
  `fee_range_min` decimal(10,2) DEFAULT NULL COMMENT '收费区间-最低(元/月)',
  `fee_range_max` decimal(10,2) DEFAULT NULL COMMENT '收费区间-最高(元/月)',
  `accept_elder_type` varchar(200) DEFAULT NULL COMMENT '收住老人类型',
  `is_published` char(1) DEFAULT '0' COMMENT '是否公示(0否 1是)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`public_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构公示信息表';

-- ----------------------------
-- 床位类型表
-- ----------------------------
DROP TABLE IF EXISTS `bed_type`;
CREATE TABLE `bed_type` (
  `bed_type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '床位类型ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `bed_type_name` varchar(100) NOT NULL COMMENT '床位类型名称',
  `bed_fee` decimal(10,2) NOT NULL COMMENT '床位费用(元/月)',
  `bed_count` int(11) DEFAULT NULL COMMENT '床位数',
  `available_count` int(11) DEFAULT NULL COMMENT '可用床位数',
  `description` varchar(500) DEFAULT NULL COMMENT '描述',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`bed_type_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位类型表';

-- ----------------------------
-- 初始化测试数据
-- ----------------------------
-- 插入测试机构数据
INSERT INTO `pension_institution` VALUES
(1, 'Sunshine Nursing Home', '91440300123456789A', 500.00, 'Shenzhen Nanshan Science Park', 'Nanshan Science Park South Area', 'Zhang San', 'Li Si', '13800138000', 'lisi@sunshine.com', 'Elderly Care Service', '1', 200, '2020-01-01', '20200101', 1000.00, '6228480123456789', '6228480987654321', '0', NOW(), NULL, NULL, NULL, '0', 'admin', NOW(), '', NULL, 'Test Institution');