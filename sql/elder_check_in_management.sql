-- 老人入住管理相关数据库表
-- 创建时间: 2025-10-28
-- 说明: 养老机构老人入住管理功能相关表结构

-- 1. 老人基础信息表
DROP TABLE IF EXISTS `elder_info`;
CREATE TABLE `elder_info` (
  `elder_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '老人ID',
  `elder_name` varchar(50) NOT NULL COMMENT '老人姓名',
  `gender` char(1) NOT NULL COMMENT '性别(1男 2女)',
  `id_card` varchar(18) NOT NULL COMMENT '身份证号',
  `birth_date` date NOT NULL COMMENT '出生日期',
  `age` int(3) NOT NULL COMMENT '年龄',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `address` varchar(500) DEFAULT NULL COMMENT '家庭住址',
  `emergency_contact` varchar(50) DEFAULT NULL COMMENT '紧急联系人',
  `emergency_phone` varchar(20) DEFAULT NULL COMMENT '紧急联系电话',
  `health_status` varchar(500) DEFAULT NULL COMMENT '健康状况描述',
  `care_level` char(1) DEFAULT '1' COMMENT '护理等级(1自理 2半护理 3全护理)',
  `special_needs` varchar(1000) DEFAULT NULL COMMENT '特殊需求说明',
  `photo_path` varchar(500) DEFAULT NULL COMMENT '照片路径',
  `status` char(1) DEFAULT '1' COMMENT '状态(0未入住 1已入住 2已退住)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`elder_id`),
  UNIQUE KEY `uk_id_card` (`id_card`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人基础信息表';

-- 2. 床位信息表
DROP TABLE IF EXISTS `bed_info`;
CREATE TABLE `bed_info` (
  `bed_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `room_number` varchar(20) NOT NULL COMMENT '房间号',
  `bed_number` varchar(10) NOT NULL COMMENT '床位号',
  `bed_type` char(1) DEFAULT '1' COMMENT '床位类型(1普通 2豪华 3医疗)',
  `bed_status` char(1) DEFAULT '1' COMMENT '床位状态(0空置 1占用 2维修)',
  `price` decimal(10,2) DEFAULT NULL COMMENT '床位价格(元/月)',
  `floor_number` int(3) DEFAULT NULL COMMENT '楼层',
  `room_area` decimal(8,2) DEFAULT NULL COMMENT '房间面积(平方米)',
  `has_bathroom` char(1) DEFAULT '0' COMMENT '是否有独立卫生间(0否 1是)',
  `has_balcony` char(1) DEFAULT '0' COMMENT '是否有阳台(0否 1是)',
  `facilities` varchar(1000) DEFAULT NULL COMMENT '设施配置',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`bed_id`),
  UNIQUE KEY `uk_institution_room_bed` (`institution_id`, `room_number`, `bed_number`),
  KEY `idx_institution_id` (`institution_id`),
  CONSTRAINT `fk_bed_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位信息表';

-- 3. 老人入住申请表
DROP TABLE IF EXISTS `elder_check_in`;
CREATE TABLE `elder_check_in` (
  `check_in_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '入住申请ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `bed_id` bigint(20) DEFAULT NULL COMMENT '分配的床位ID',
  `apply_date` datetime NOT NULL COMMENT '申请日期',
  `expected_check_in_date` date NOT NULL COMMENT '期望入住日期',
  `actual_check_in_date` date DEFAULT NULL COMMENT '实际入住日期',
  `check_in_status` char(1) DEFAULT '0' COMMENT '入住状态(0申请中 1已入住 2已拒绝 3已取消)',
  `care_level` char(1) NOT NULL COMMENT '护理等级(1自理 2半护理 3全护理)',
  `monthly_fee` decimal(10,2) DEFAULT NULL COMMENT '月费用(元)',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '押金金额(元)',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `interview_date` datetime DEFAULT NULL COMMENT '面谈时间',
  `interview_result` varchar(500) DEFAULT NULL COMMENT '面谈结果',
  `approval_user` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approval_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`check_in_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_bed_id` (`bed_id`),
  CONSTRAINT `fk_checkin_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_checkin_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_checkin_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed_info` (`bed_id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='老人入住申请表';

-- 4. 床位分配记录表
DROP TABLE IF EXISTS `bed_allocation`;
CREATE TABLE `bed_allocation` (
  `allocation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '分配记录ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `bed_id` bigint(20) NOT NULL COMMENT '床位ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `check_in_date` date NOT NULL COMMENT '入住日期',
  `check_out_date` date DEFAULT NULL COMMENT '退住日期',
  `allocation_status` char(1) DEFAULT '1' COMMENT '分配状态(1在住 2已退住)',
  `monthly_fee` decimal(10,2) DEFAULT NULL COMMENT '月费用(元)',
  `deposit_status` char(1) DEFAULT '0' COMMENT '押金状态(0未支付 1已支付 2已退还)',
  `deposit_amount` decimal(10,2) DEFAULT NULL COMMENT '押金金额(元)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`allocation_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_bed_id` (`bed_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_check_in_date` (`check_in_date`),
  CONSTRAINT `fk_allocation_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_allocation_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed_info` (`bed_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_allocation_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='床位分配记录表';

-- 5. 添加字典类型和数据
-- 护理等级字典
INSERT INTO `sys_dict_type` VALUES (200, '护理等级', 'elder_care_level', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '老人护理等级字典类型');
INSERT INTO `sys_dict_data` (dict_code, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES (200, '自理', '1', 'elder_care_level', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:30:00', '生活完全自理');
INSERT INTO `sys_dict_data` (dict_code, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES (201, '半护理', '2', 'elder_care_level', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:30:00', '需要部分护理');
INSERT INTO `sys_dict_data` (dict_code, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark) VALUES (202, '全护理', '3', 'elder_care_level', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '需要全面护理');

-- 老人状态字典
INSERT INTO `sys_dict_type` VALUES (201, '老人状态', 'elder_status', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '老人状态字典类型');
INSERT INTO `sys_dict_data` VALUES (203, 1, '未入住', '0', 'elder_status', '', 'info', 'N', '0', 'admin', '2025-10-28 10:30:00', '尚未入住');
INSERT INTO `sys_dict_data` VALUES (204, 2, '已入住', '1', 'elder_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:30:00', '已入住');
INSERT INTO `sys_dict_data` VALUES (205, 3, '已退住', '2', 'elder_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '已退住');

-- 床位类型字典
INSERT INTO `sys_dict_type` VALUES (202, '床位类型', 'bed_type', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '床位类型字典类型');
INSERT INTO `sys_dict_data` VALUES (206, 1, '普通床位', '1', 'bed_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:30:00', '标准普通床位');
INSERT INTO `sys_dict_data` VALUES (207, 2, '豪华床位', '2', 'bed_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:30:00', '豪华舒适床位');
INSERT INTO `sys_dict_data` VALUES (208, 3, '医疗床位', '3', 'bed_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '医疗护理床位');

-- 床位状态字典
INSERT INTO `sys_dict_type` VALUES (203, '床位状态', 'bed_status', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '床位状态字典类型');
INSERT INTO `sys_dict_data` VALUES (209, 1, '空置', '0', 'bed_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:30:00', '床位空置可用');
INSERT INTO `sys_dict_data` VALUES (210, 2, '占用', '1', 'bed_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '床位已被占用');
INSERT INTO `sys_dict_data` VALUES (211, 3, '维修', '2', 'bed_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:30:00', '床位维修中');

-- 入住申请状态字典
INSERT INTO `sys_dict_type` VALUES (204, '入住申请状态', 'check_in_status', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '入住申请状态字典类型');
INSERT INTO `sys_dict_data` VALUES (212, 1, '申请中', '0', 'check_in_status', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:30:00', '入住申请处理中');
INSERT INTO `sys_dict_data` VALUES (213, 2, '已入住', '1', 'check_in_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:30:00', '已成功入住');
INSERT INTO `sys_dict_data` VALUES (214, 3, '已拒绝', '2', 'check_in_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '申请被拒绝');
INSERT INTO `sys_dict_data` VALUES (215, 4, '已取消', '3', 'check_in_status', '', 'info', 'N', '0', 'admin', '2025-10-28 10:30:00', '申请已取消');

-- 分配状态字典
INSERT INTO `sys_dict_type` VALUES (205, '分配状态', 'allocation_status', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '床位分配状态字典类型');
INSERT INTO `sys_dict_data` VALUES (216, 1, '在住', '1', 'allocation_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:30:00', '老人在住');
INSERT INTO `sys_dict_data` VALUES (217, 2, '已退住', '2', 'allocation_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '老人已退住');

-- 押金状态字典
INSERT INTO `sys_dict_type` VALUES (206, '押金状态', 'deposit_status', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '押金状态字典类型');
INSERT INTO `sys_dict_data` VALUES (218, 1, '未支付', '0', 'deposit_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:30:00', '押金未支付');
INSERT INTO `sys_dict_data` VALUES (219, 2, '已支付', '1', 'deposit_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:30:00', '押金已支付');
INSERT INTO `sys_dict_data` VALUES (220, 3, '已退还', '2', 'deposit_status', '', 'info', 'N', '0', 'admin', '2025-10-28 10:30:00', '押金已退还');

-- 初始化一些床位数据(假设机构ID为1)
INSERT INTO `bed_info` (`institution_id`, `room_number`, `bed_number`, `bed_type`, `bed_status`, `price`, `floor_number`, `room_area`, `has_bathroom`, `has_balcony`, `facilities`, `create_by`, `create_time`) VALUES
(1, '101', '01', '1', '0', 2000.00, 1, 20.00, '1', '0', '空调、电视、热水器', 'admin', '2025-10-28 10:30:00'),
(1, '101', '02', '1', '0', 2000.00, 1, 20.00, '1', '0', '空调、电视、热水器', 'admin', '2025-10-28 10:30:00'),
(1, '102', '01', '2', '0', 3500.00, 1, 25.00, '1', '1', '空调、电视、热水器、阳台、独立卫浴', 'admin', '2025-10-28 10:30:00'),
(1, '102', '02', '2', '0', 3500.00, 1, 25.00, '1', '1', '空调、电视、热水器、阳台、独立卫浴', 'admin', '2025-10-28 10:30:00'),
(1, '201', '01', '3', '0', 5000.00, 2, 30.00, '1', '1', '医疗床、呼吸机、心电监护、独立卫浴', 'admin', '2025-10-28 10:30:00'),
(1, '201', '02', '3', '1', 5000.00, 2, 30.00, '1', '1', '医疗床、呼吸机、心电监护、独立卫浴', 'admin', '2025-10-28 10:30:00');