/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : newzijin

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2025-10-31 01:54:53
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for account_balance_log
-- ----------------------------
DROP TABLE IF EXISTS `account_balance_log`;
CREATE TABLE `account_balance_log` (
  `log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `account_id` bigint(20) NOT NULL COMMENT '璐︽埛ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `transaction_id` bigint(20) NOT NULL COMMENT '浜ゆ槗ID',
  `balance_type` char(1) NOT NULL COMMENT '浣欓?绫诲瀷(1鎬讳綑棰?2鏈嶅姟璐?3鎶奸噾 4浼氬憳璐?',
  `amount_before` decimal(12,2) NOT NULL COMMENT '鍙樺姩鍓嶄綑棰',
  `amount_change` decimal(12,2) NOT NULL COMMENT '鍙樺姩閲戦?',
  `amount_after` decimal(12,2) NOT NULL COMMENT '鍙樺姩鍚庝綑棰',
  `change_type` char(1) NOT NULL COMMENT '鍙樺姩绫诲瀷(1澧炲姞 2鍑忓皯)',
  `business_type` char(1) NOT NULL COMMENT '涓氬姟绫诲瀷',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`log_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_transaction_id` (`transaction_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='璐︽埛浣欓?鍙樺姩璁板綍琛';

-- ----------------------------
-- Records of account_balance_log
-- ----------------------------

-- ----------------------------
-- Table structure for account_info
-- ----------------------------
DROP TABLE IF EXISTS `account_info`;
CREATE TABLE `account_info` (
  `account_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璐︽埛ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `account_no` varchar(50) NOT NULL COMMENT '璐︽埛缂栧彿',
  `account_name` varchar(100) NOT NULL COMMENT '璐︽埛鍚嶇О',
  `account_status` char(1) DEFAULT '1' COMMENT '璐︽埛鐘舵?(0鍐荤粨 1姝ｅ父 2閿?埛)',
  `total_balance` decimal(12,2) DEFAULT '0.00' COMMENT '鎬讳綑棰',
  `service_balance` decimal(12,2) DEFAULT '0.00' COMMENT '鏈嶅姟璐逛綑棰',
  `deposit_balance` decimal(12,2) DEFAULT '0.00' COMMENT '鎶奸噾浣欓?',
  `member_balance` decimal(12,2) DEFAULT '0.00' COMMENT '浼氬憳璐逛綑棰',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`account_id`),
  UNIQUE KEY `uk_account_no` (`account_no`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='鑰佷汉璐︽埛淇℃伅琛';

-- ----------------------------
-- Records of account_info
-- ----------------------------
INSERT INTO `account_info` VALUES ('1', '1', '1', 'ACC001', 'Zhang Account', '1', '25000.00', '15000.00', '5000.00', '5000.00', '', '2025-10-29 03:25:06', '', null, 'Zhang Daye Account');
INSERT INTO `account_info` VALUES ('2', '2', '1', 'ACC002', 'Li Account', '1', '16500.00', '8000.00', '5000.00', '3500.00', '', '2025-10-29 03:25:06', '', null, 'Li Nainai Account');
INSERT INTO `account_info` VALUES ('3', '3', '1', 'ACC003', 'Wang Account', '1', '20500.00', '12000.00', '5000.00', '3500.00', '', '2025-10-29 03:25:06', '', null, 'Wang Yeye Account');

-- ----------------------------
-- Table structure for balance_warning
-- ----------------------------
DROP TABLE IF EXISTS `balance_warning`;
CREATE TABLE `balance_warning` (
  `warning_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '棰勮?ID',
  `warning_no` varchar(50) NOT NULL COMMENT '棰勮?缂栧彿',
  `account_id` bigint(20) NOT NULL COMMENT '璐︽埛ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `warning_type` char(1) DEFAULT '1' COMMENT '棰勮?绛夌骇 1-鎻愮ず 2-璀﹀憡 3-涓ラ噸',
  `current_balance` decimal(15,2) DEFAULT '0.00' COMMENT '褰撳墠浣欓?',
  `available_months` int(11) DEFAULT '0' COMMENT '鍙?敤鏈堟暟',
  `monthly_fee` decimal(10,2) DEFAULT '0.00' COMMENT '鏈堣垂鏍囧噯',
  `warning_reason` varchar(500) DEFAULT NULL COMMENT '棰勮?鍘熷洜',
  `suggestion` varchar(500) DEFAULT NULL COMMENT '寤鸿?鎺?柦',
  `warning_status` char(1) DEFAULT '0' COMMENT '棰勮?鐘舵? 0-鏈??鐞?1-宸插?鐞?2-宸插拷鐣',
  `handle_time` datetime DEFAULT NULL COMMENT '澶勭悊鏃堕棿',
  `handler` varchar(64) DEFAULT NULL COMMENT '澶勭悊浜',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '澶勭悊澶囨敞',
  `is_notified` char(1) DEFAULT '0' COMMENT '鏄?惁宸查?鐭?0-鏈??鐭?1-宸查?鐭',
  `notify_time` datetime DEFAULT NULL COMMENT '閫氱煡鏃堕棿',
  `notify_method` varchar(100) DEFAULT NULL COMMENT '閫氱煡鏂瑰紡',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`warning_id`),
  UNIQUE KEY `uk_warning_no` (`warning_no`),
  KEY `idx_warning_account` (`account_id`),
  KEY `idx_warning_elder` (`elder_id`),
  KEY `idx_warning_institution` (`institution_id`),
  KEY `idx_warning_type` (`warning_type`),
  KEY `idx_warning_status` (`warning_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='浣欓?棰勮?璁板綍琛';

-- ----------------------------
-- Records of balance_warning
-- ----------------------------
INSERT INTO `balance_warning` VALUES ('1', 'WAR001', '2', '2', '1', '3', '13700.00', '4', '2800.00', 'Low balance warning', 'Recharge needed soon', '0', null, null, null, '0', null, null, 'Critical Warning', '', '2025-10-29 03:25:58', '', '2025-10-29 03:25:58');
INSERT INTO `balance_warning` VALUES ('2', 'WAR002', '1', '1', '1', '2', '22000.00', '7', '3000.00', 'Medium balance', 'Monitor balance trend', '0', null, null, null, '0', null, null, 'Warning Level', '', '2025-10-29 03:25:58', '', '2025-10-29 03:25:58');
INSERT INTO `balance_warning` VALUES ('3', 'WAR003', '3', '3', '1', '1', '17000.00', '4', '3500.00', 'Normal balance', 'Watch balance changes', '0', null, null, null, '0', null, null, 'Info Level', '', '2025-10-29 03:25:58', '', '2025-10-29 03:25:58');

-- ----------------------------
-- Table structure for bed_allocation
-- ----------------------------
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
  CONSTRAINT `fk_allocation_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed_info` (`bed_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_allocation_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_allocation_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='床位分配记录表';

-- ----------------------------
-- Records of bed_allocation
-- ----------------------------

-- ----------------------------
-- Table structure for bed_info
-- ----------------------------
DROP TABLE IF EXISTS `bed_info`;
CREATE TABLE `bed_info` (
  `bed_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '床位ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `room_number` varchar(20) NOT NULL COMMENT '房间号',
  `bed_number` varchar(10) NOT NULL COMMENT '床位号',
  `bed_type` char(1) DEFAULT '1' COMMENT '床位类型(1普通 2豪华 3医疗)',
  `bed_status` char(1) DEFAULT '1' COMMENT '床位状态(0空置 1占用 2维修)',
  `price` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '价格',
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
  UNIQUE KEY `uk_institution_room_bed` (`institution_id`,`room_number`,`bed_number`),
  KEY `idx_institution_id` (`institution_id`),
  CONSTRAINT `fk_bed_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='床位信息表';

-- ----------------------------
-- Records of bed_info
-- ----------------------------
INSERT INTO `bed_info` VALUES ('1', '1', '101', '01', '1', '0', '2000.00', '1', '20.00', '1', '0', '空调、电视、热水器', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('2', '1', '101', '02', '1', '0', '2000.00', '1', '20.00', '1', '0', '空调、电视、热水器', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('3', '1', '102', '01', '2', '0', '3500.00', '1', '25.00', '1', '1', '空调、电视、热水器、阳台、独立卫浴', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('4', '1', '102', '02', '2', '0', '3500.00', '1', '25.00', '1', '1', '空调、电视、热水器、阳台、独立卫浴', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('5', '1', '201', '01', '3', '0', '5000.00', '2', '30.00', '1', '1', '医疗床、呼吸机、心电监护、独立卫浴', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('6', '1', '201', '02', '3', '1', '5000.00', '2', '30.00', '1', '1', '医疗床、呼吸机、心电监护、独立卫浴', 'admin', '2025-10-28 18:35:38', '', null, null);
INSERT INTO `bed_info` VALUES ('7', '3', '101', '10', '1', '1', '0.00', '1', '50.00', 'Y', 'Y', '电视机', '', '2025-10-28 23:27:05', '', null, null);
INSERT INTO `bed_info` VALUES ('8', '3', '202', '103', '1', '1', '5000.00', '2', '100.00', 'Y', 'Y', null, '', '2025-10-29 00:10:14', '', null, null);
INSERT INTO `bed_info` VALUES ('9', '3', '302', '8', '1', '0', '1000.00', null, null, 'Y', 'Y', null, '', '2025-10-29 00:25:38', '', null, null);

-- ----------------------------
-- Table structure for bed_type
-- ----------------------------
DROP TABLE IF EXISTS `bed_type`;
CREATE TABLE `bed_type` (
  `bed_type_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '搴婁綅绫诲瀷ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `bed_type_name` varchar(100) NOT NULL COMMENT '搴婁綅绫诲瀷鍚嶇О',
  `bed_fee` decimal(10,2) NOT NULL COMMENT '搴婁綅璐圭敤(鍏?鏈?',
  `bed_count` int(11) DEFAULT NULL COMMENT '搴婁綅鏁',
  `available_count` int(11) DEFAULT NULL COMMENT '鍙?敤搴婁綅鏁',
  `description` varchar(500) DEFAULT NULL COMMENT '鎻忚堪',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`bed_type_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='搴婁綅绫诲瀷琛';

-- ----------------------------
-- Records of bed_type
-- ----------------------------

-- ----------------------------
-- Table structure for deposit_apply
-- ----------------------------
DROP TABLE IF EXISTS `deposit_apply`;
CREATE TABLE `deposit_apply` (
  `apply_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '申请ID',
  `apply_no` varchar(50) NOT NULL COMMENT '申请单号',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `account_id` bigint(20) NOT NULL COMMENT '账户ID',
  `apply_amount` decimal(10,2) NOT NULL COMMENT '申请金额',
  `apply_reason` varchar(500) NOT NULL COMMENT '申请原因',
  `apply_type` varchar(50) DEFAULT NULL COMMENT '申请类型',
  `urgency_level` char(1) DEFAULT '2' COMMENT '紧急程度(1-紧急 2-普通 3-一般)',
  `apply_status` char(1) DEFAULT '0' COMMENT '申请状态(0-待审批 1-已批准 2-已拒绝 3-已撤销)',
  `approver` varchar(50) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `actual_amount` decimal(10,2) DEFAULT NULL COMMENT '实际使用金额',
  `use_time` datetime DEFAULT NULL COMMENT '使用时间',
  `create_by` varchar(64) DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`apply_id`),
  UNIQUE KEY `uk_apply_no` (`apply_no`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_apply_status` (`apply_status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='押金使用申请表';

-- ----------------------------
-- Records of deposit_apply
-- ----------------------------
INSERT INTO `deposit_apply` VALUES ('1', 'DEP20251029001', '1', '1', '1', '3000.00', '老人突发疾病需要紧急医疗救治', '医疗费用', '1', '0', null, null, null, null, null, 'admin', '2025-10-29 11:22:01', 'admin', '2025-10-29 11:22:01', '紧急医疗费用申请');
INSERT INTO `deposit_apply` VALUES ('2', 'DEP20251029002', '2', '1', '2', '1500.00', '购买轮椅等康复器材', '康复护理', '2', '0', null, null, null, null, null, 'admin', '2025-10-29 11:22:01', 'admin', '2025-10-29 11:22:01', '康复器材采购申请');
INSERT INTO `deposit_apply` VALUES ('3', 'DEP20251029003', '3', '1', '3', '800.00', '冬季衣物和生活用品采购', '生活用品', '3', '1', 'admin', '2025-10-29 12:22:01', '申请合理，批准使用', '800.00', '2025-10-29 13:22:01', 'admin', '2025-10-29 11:22:01', 'admin', '2025-10-29 12:22:01', '生活用品申请已批准并使用');

-- ----------------------------
-- Table structure for elder_check_in
-- ----------------------------
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
  `monthly_fee` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '月费用',
  `deposit_amount` decimal(10,2) NOT NULL DEFAULT '0.00' COMMENT '押金金额',
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
  CONSTRAINT `fk_checkin_bed` FOREIGN KEY (`bed_id`) REFERENCES `bed_info` (`bed_id`) ON DELETE SET NULL,
  CONSTRAINT `fk_checkin_elder` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_checkin_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='老人入住申请表';

-- ----------------------------
-- Records of elder_check_in
-- ----------------------------

-- ----------------------------
-- Table structure for elder_info
-- ----------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='老人基础信息表';

-- ----------------------------
-- Records of elder_info
-- ----------------------------
INSERT INTO `elder_info` VALUES ('1', 'Zhang Daye', '1', '110101192001011234', '1920-01-01', '104', '13900139001', 'Beijing', 'Zhang Son', '13900139001', 'Good', '3', null, null, '1', '', null, '', null, 'Elder Zhang');
INSERT INTO `elder_info` VALUES ('2', 'Li Nainai', '2', '110101192502022345', '1925-02-02', '99', '13900139002', 'Shanghai', 'Li Daughter', '13900139002', 'Fair', '2', null, null, '1', '', null, '', null, 'Elder Li');
INSERT INTO `elder_info` VALUES ('3', 'Wang Yeye', '1', '110101193003033456', '1930-03-03', '94', '13900139003', 'Guangzhou', 'Wang Grandson', '13900139003', 'Poor', '3', null, null, '1', '', null, '', null, 'Elder Wang');

-- ----------------------------
-- Table structure for fund_transfer
-- ----------------------------
DROP TABLE IF EXISTS `fund_transfer`;
CREATE TABLE `fund_transfer` (
  `transfer_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍒掓嫧ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `transfer_no` varchar(50) NOT NULL COMMENT '鍒掓嫧鍗曞彿',
  `transfer_type` char(1) NOT NULL COMMENT '鍒掓嫧绫诲瀷(1鑷?姩鍒掓嫧 2鎵嬪姩鍒掓嫧 3鐗规畩鐢宠?)',
  `transfer_amount` decimal(15,2) NOT NULL COMMENT '鍒掓嫧閲戦?',
  `transfer_date` date NOT NULL COMMENT '鍒掓嫧鏃ユ湡',
  `transfer_period` varchar(20) DEFAULT NULL COMMENT '鍒掓嫧鏈熼棿(濡?025-12)',
  `elder_count` int(11) DEFAULT '0' COMMENT '娑夊強鑰佷汉鏁伴噺',
  `transfer_status` char(1) DEFAULT '0' COMMENT '鍒掓嫧鐘舵?(0寰呭?鐞?1鎴愬姛 2澶辫触)',
  `bank_order_no` varchar(100) DEFAULT NULL COMMENT '閾惰?璁㈠崟鍙',
  `failure_reason` varchar(500) DEFAULT NULL COMMENT '澶辫触鍘熷洜',
  `approve_user` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `execute_user` varchar(64) DEFAULT NULL COMMENT '鎵ц?浜',
  `execute_time` datetime DEFAULT NULL COMMENT '鎵ц?鏃堕棿',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`transfer_id`),
  UNIQUE KEY `uk_transfer_no` (`transfer_no`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_transfer_date` (`transfer_date`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='璧勯噾鍒掓嫧璁板綍琛';

-- ----------------------------
-- Records of fund_transfer
-- ----------------------------
INSERT INTO `fund_transfer` VALUES ('1', '1', 'TRF1761681617639597', '1', '9000.00', '2025-10-29', '2025-01', '3', '0', null, null, null, null, null, null, 'system', '2025-10-29 04:00:18', '', null, '系统自动生成的月度划拨');

-- ----------------------------
-- Table structure for fund_transfer_detail
-- ----------------------------
DROP TABLE IF EXISTS `fund_transfer_detail`;
CREATE TABLE `fund_transfer_detail` (
  `detail_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鏄庣粏ID',
  `transfer_id` bigint(20) NOT NULL COMMENT '鍒掓嫧ID',
  `account_id` bigint(20) NOT NULL COMMENT '璐︽埛ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `transfer_amount` decimal(12,2) NOT NULL COMMENT '鍒掓嫧閲戦?',
  `service_fee` decimal(12,2) DEFAULT '0.00' COMMENT '鏈嶅姟璐',
  `care_fee` decimal(12,2) DEFAULT '0.00' COMMENT '鎶ょ悊璐',
  `food_fee` decimal(12,2) DEFAULT '0.00' COMMENT '浼欓?璐',
  `other_fee` decimal(12,2) DEFAULT '0.00' COMMENT '鍏朵粬璐圭敤',
  `billing_month` varchar(7) NOT NULL COMMENT '缁撶畻鏈堜唤(YYYY-MM)',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`detail_id`),
  KEY `idx_transfer_id` (`transfer_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_billing_month` (`billing_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='璧勯噾鍒掓嫧鏄庣粏琛';

-- ----------------------------
-- Records of fund_transfer_detail
-- ----------------------------

-- ----------------------------
-- Table structure for fund_transfer_rule
-- ----------------------------
DROP TABLE IF EXISTS `fund_transfer_rule`;
CREATE TABLE `fund_transfer_rule` (
  `rule_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '瑙勫垯ID',
  `rule_name` varchar(100) NOT NULL COMMENT '瑙勫垯鍚嶇О',
  `transfer_cycle` char(1) NOT NULL COMMENT '鍒掓嫧鍛ㄦ湡(1姣忔湀 2姣忓?搴?3姣忓崐骞?',
  `transfer_day` int(2) NOT NULL COMMENT '鍒掓嫧鏃?濡傛瘡鏈?鏃?',
  `advance_days` int(2) DEFAULT '0' COMMENT '鎻愬墠澶╂暟',
  `min_balance_ratio` decimal(5,2) DEFAULT '0.20' COMMENT '鏈?綆浣欓?姣斾緥',
  `auto_approve` char(1) DEFAULT '1' COMMENT '鑷?姩瀹℃壒(0鍚?1鏄?',
  `status` char(1) DEFAULT '1' COMMENT '鐘舵?(0绂佺敤 1鍚?敤)',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`rule_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='璧勯噾鍒掓嫧瑙勫垯閰嶇疆琛';

-- ----------------------------
-- Records of fund_transfer_rule
-- ----------------------------
INSERT INTO `fund_transfer_rule` VALUES ('1', 'Default Monthly Transfer Rule', '1', '1', '0', '0.20', '1', '1', 'system', '2025-10-29 00:37:11', '', null, 'Monthly transfer on day 1, keep 20% as risk deposit');

-- ----------------------------
-- Table structure for gen_table
-- ----------------------------
DROP TABLE IF EXISTS `gen_table`;
CREATE TABLE `gen_table` (
  `table_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表名称',
  `table_comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '表描述',
  `sub_table_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '关联子表的表名',
  `sub_table_fk_name` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '子表关联的外键名',
  `class_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '实体类名称',
  `tpl_category` varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'crud' COMMENT '使用的模板（crud单表操作 tree树表操作）',
  `tpl_web_type` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '前端模板类型（element-ui模版 element-plus模版）',
  `package_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成包路径',
  `module_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成模块名',
  `business_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成业务名',
  `function_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能名',
  `function_author` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '生成功能作者',
  `gen_type` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '生成代码方式（0zip压缩包 1自定义路径）',
  `gen_path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '/' COMMENT '生成路径（不填默认项目路径）',
  `options` varchar(1000) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '其它生成选项',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`table_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表';

-- ----------------------------
-- Records of gen_table
-- ----------------------------

-- ----------------------------
-- Table structure for gen_table_column
-- ----------------------------
DROP TABLE IF EXISTS `gen_table_column`;
CREATE TABLE `gen_table_column` (
  `column_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '编号',
  `table_id` bigint(20) DEFAULT NULL COMMENT '归属表编号',
  `column_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列名称',
  `column_comment` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列描述',
  `column_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '列类型',
  `java_type` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA类型',
  `java_field` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'JAVA字段名',
  `is_pk` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否主键（1是）',
  `is_increment` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否自增（1是）',
  `is_required` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否必填（1是）',
  `is_insert` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否为插入字段（1是）',
  `is_edit` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否编辑字段（1是）',
  `is_list` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否列表字段（1是）',
  `is_query` char(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否查询字段（1是）',
  `query_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT 'EQ' COMMENT '查询方式（等于、不等于、大于、小于、范围）',
  `html_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '显示类型（文本框、文本域、下拉框、复选框、单选框、日期控件）',
  `dict_type` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `sort` int(11) DEFAULT NULL COMMENT '排序',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`column_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='代码生成业务表字段';

-- ----------------------------
-- Records of gen_table_column
-- ----------------------------

-- ----------------------------
-- Table structure for order_info
-- ----------------------------
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `order_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` varchar(50) NOT NULL COMMENT '订单编号',
  `order_type` char(1) NOT NULL COMMENT '订单类型(1床位费 2护理费 3餐饮费 4医疗费 5其他费用)',
  `elder_id` bigint(20) DEFAULT NULL COMMENT '老人ID',
  `institution_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单主表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('1', 'ORD20251028001', '1', '1', '1', '1', '1', '2000.00', '0.00', '1', null, null, '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', null, 'admin', '2025-10-28 16:00:00', '', null);
INSERT INTO `order_info` VALUES ('2', 'ORD20251028002', '2', '1', '1', '1', null, '1500.00', '0.00', '0', null, null, '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', null, 'admin', '2025-10-28 16:00:00', '', null);
INSERT INTO `order_info` VALUES ('3', 'ORD20251028003', '3', '1', '1', '1', null, '800.00', '0.00', '0', null, null, '2025-10-28', '2025-10-28', '2025-11-27', 'monthly', '2025-10-27', null, 'admin', '2025-10-28 16:00:00', '', null);
INSERT INTO `order_info` VALUES ('4', 'ORDBED20251029001140', '1', '1', '3', '2', '8', '6000.00', '0.00', '0', null, null, '2025-10-29', null, null, 'monthly', null, null, 'system', null, '', null);
INSERT INTO `order_info` VALUES ('5', 'ORDCARE20251029001140', '2', '1', '3', '2', null, '1500.00', '0.00', '0', null, null, '2025-10-29', null, null, 'monthly', null, null, 'system', null, '', null);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `item_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '明细ID',
  `order_id` bigint(20) NOT NULL COMMENT '订单ID',
  `order_no` varchar(50) DEFAULT NULL COMMENT '订单号',
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
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`item_id`),
  KEY `idx_order_id` (`order_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='订单明细表';

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES ('1', '1', null, '101房间01号床位使用费', '床位费', '101房间01号床位，月度使用费', '2000.00', '1', '2000.00', '2025-10', 'admin', '2025-10-28 16:00:00', '', null, null);
INSERT INTO `order_item` VALUES ('2', '2', null, '半护理服务费', '护理费', '月度半护理服务，包含基础护理和健康管理', '1500.00', '1', '1500.00', '2025-10', 'admin', '2025-10-28 16:00:00', '', null, null);
INSERT INTO `order_item` VALUES ('3', '3', null, '月度餐饮服务费', '餐饮费', '一日三餐营养餐食服务', '800.00', '1', '800.00', '2025-10', 'admin', '2025-10-28 16:00:00', '', null, null);

-- ----------------------------
-- Table structure for payment_record
-- ----------------------------
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='支付记录表';

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES ('1', 'PAY20251028001', '1', '1', '1', '2000.00', 'bank', '1', '2025-10-28 16:30:00', 'BANK20251028001', null, 'admin', null, 'admin', '2025-10-28 16:30:00', '', null);

-- ----------------------------
-- Table structure for pension_institution
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution`;
CREATE TABLE `pension_institution` (
  `institution_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鏈烘瀯ID',
  `institution_name` varchar(200) NOT NULL COMMENT '鏈烘瀯鍚嶇О',
  `credit_code` varchar(50) NOT NULL COMMENT '缁熶竴淇＄敤浠ｇ爜',
  `registered_capital` decimal(15,2) DEFAULT NULL COMMENT '娉ㄥ唽璧勯噾(涓囧厓)',
  `registered_address` varchar(500) DEFAULT NULL COMMENT '娉ㄥ唽鍦板潃',
  `actual_address` varchar(500) DEFAULT NULL COMMENT '瀹為檯缁忚惀鍦板潃',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '娉曞畾浠ｈ〃浜',
  `contact_person` varchar(50) NOT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) NOT NULL COMMENT '鑱旂郴鐢佃瘽',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '鑱旂郴閭??',
  `business_scope` varchar(1000) DEFAULT NULL COMMENT '缁忚惀鑼冨洿',
  `institution_type` char(1) DEFAULT '1' COMMENT '鏈烘瀯绫诲瀷(1姘戝姙 2鍏?姙 3鍏?缓姘戣惀)',
  `bed_count` int(11) DEFAULT NULL COMMENT '搴婁綅鏁',
  `established_date` date DEFAULT NULL COMMENT '鎴愮珛鏃ユ湡',
  `record_number` varchar(100) DEFAULT NULL COMMENT '澶囨?鍙',
  `fixed_assets` decimal(15,2) DEFAULT NULL COMMENT '鍥哄畾璧勪骇鍑??(涓囧厓)',
  `bank_account` varchar(100) DEFAULT NULL COMMENT '鍩烘湰缁撶畻璐︽埛',
  `supervise_account` varchar(100) DEFAULT NULL COMMENT '鐩戠?璐︽埛',
  `status` char(1) DEFAULT '0' COMMENT '鐘舵?(0寰呭?鎵?1宸插叆椹?2宸查┏鍥?3瑙ｉ櫎鐩戠?)',
  `apply_time` datetime DEFAULT NULL COMMENT '鐢宠?鏃堕棿',
  `approve_time` datetime DEFAULT NULL COMMENT '瀹℃壒鏃堕棿',
  `approve_user` varchar(64) DEFAULT NULL COMMENT '瀹℃壒浜',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '瀹℃壒鎰忚?',
  `blacklist_flag` char(1) DEFAULT '0' COMMENT '榛戝悕鍗曟爣蹇?0姝ｅ父 1榛戝悕鍗?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  `approval_by` varchar(64) DEFAULT NULL COMMENT '审批人',
  `approval_time` datetime DEFAULT NULL COMMENT '审批时间',
  PRIMARY KEY (`institution_id`),
  UNIQUE KEY `uk_credit_code` (`credit_code`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='鍏昏?鏈烘瀯淇℃伅琛';

-- ----------------------------
-- Records of pension_institution
-- ----------------------------
INSERT INTO `pension_institution` VALUES ('1', 'Sunshine Nursing Home', '91440300123456789A', '500.00', 'Shenzhen Nanshan Science Park', 'Nanshan Science Park South Area', 'Zhang San', 'Li Si', '13800138000', 'lisi@sunshine.com', 'Elderly Care Service', '1', '200', '2020-01-01', '20200101', '1000.00', '6228480123456789', '6228480987654321', '0', '2025-10-28 03:30:03', null, null, null, '0', 'admin', '2025-10-28 03:30:03', '', null, 'Test Institution', null, null);
INSERT INTO `pension_institution` VALUES ('2', '测试服务机构', '12123123123', '15.00', '阿斯蒂芬', '阿斯蒂芬', null, '阿斯蒂芬', '18539279011', null, null, '3', '0', null, null, '0.00', null, null, '0', '2025-10-28 16:59:21', null, null, null, '0', '', '2025-10-28 16:59:21', '', '2025-10-28 17:01:00', null, null, null);
INSERT INTO `pension_institution` VALUES ('3', '123123', '123123', '0.00', '测试地址', null, null, '123', '18539279011', null, null, '3', '0', null, null, '0.00', null, null, '1', '2025-10-28 17:50:42', '2025-10-28 18:05:20', 'admin', '0', '0', '', '2025-10-28 17:50:42', '', null, null, null, null);

-- ----------------------------
-- Table structure for pension_institution_attach
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution_attach`;
CREATE TABLE `pension_institution_attach` (
  `attach_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '闄勪欢ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `attach_type` char(1) DEFAULT NULL COMMENT '闄勪欢绫诲瀷(1钀ヤ笟鎵х収 2绀句細绂忓埄鏈烘瀯璁剧疆鎵瑰噯璇佷功 3涓夋柟鐩戠?鍗忚?)',
  `attach_name` varchar(200) DEFAULT NULL COMMENT '闄勪欢鍚嶇О',
  `attach_path` varchar(500) DEFAULT NULL COMMENT '闄勪欢璺?緞',
  `file_size` bigint(20) DEFAULT NULL COMMENT '鏂囦欢澶у皬(瀛楄妭)',
  `file_type` varchar(50) DEFAULT NULL COMMENT '鏂囦欢绫诲瀷',
  `create_by` varchar(64) DEFAULT '' COMMENT '涓婁紶鑰',
  `create_time` datetime DEFAULT NULL COMMENT '涓婁紶鏃堕棿',
  PRIMARY KEY (`attach_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='鏈烘瀯闄勪欢鏉愭枡琛';

-- ----------------------------
-- Records of pension_institution_attach
-- ----------------------------

-- ----------------------------
-- Table structure for pension_institution_public
-- ----------------------------
DROP TABLE IF EXISTS `pension_institution_public`;
CREATE TABLE `pension_institution_public` (
  `public_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '鍏?ずID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `institution_intro` text COMMENT '鏈烘瀯绠?粙',
  `service_scope` varchar(1000) DEFAULT NULL COMMENT '鏈嶅姟鑼冨洿',
  `service_features` varchar(1000) DEFAULT NULL COMMENT '鐗硅壊鏈嶅姟',
  `land_area` decimal(10,2) DEFAULT NULL COMMENT '鍗犲湴闈㈢Н(骞虫柟绫?',
  `building_area` decimal(10,2) DEFAULT NULL COMMENT '寤虹瓚闈㈢Н(骞虫柟绫?',
  `environment_imgs` text COMMENT '鐜??鍥剧墖(澶氬紶锛岀敤閫楀彿鍒嗛殧)',
  `rating` char(1) DEFAULT '3' COMMENT '鏈烘瀯璇勭骇(1鈽?2鈽呪槄 3鈽呪槄鈽?4鈽呪槄鈽呪槄 5鈽呪槄鈽呪槄鈽?',
  `fee_range_min` decimal(10,2) DEFAULT NULL COMMENT '鏀惰垂鍖洪棿-鏈?綆(鍏?鏈?',
  `fee_range_max` decimal(10,2) DEFAULT NULL COMMENT '鏀惰垂鍖洪棿-鏈?珮(鍏?鏈?',
  `accept_elder_type` varchar(200) DEFAULT NULL COMMENT '鏀朵綇鑰佷汉绫诲瀷',
  `is_published` char(1) DEFAULT '0' COMMENT '鏄?惁鍏?ず(0鍚?1鏄?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`public_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='鏈烘瀯鍏?ず淇℃伅琛';

-- ----------------------------
-- Records of pension_institution_public
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_blob_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_blob_triggers`;
CREATE TABLE `qrtz_blob_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `blob_data` blob COMMENT '存放持久化Trigger对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_blob_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Blob类型的触发器表';

-- ----------------------------
-- Records of qrtz_blob_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_calendars
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_calendars`;
CREATE TABLE `qrtz_calendars` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `calendar_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '日历名称',
  `calendar` blob NOT NULL COMMENT '存放持久化calendar对象',
  PRIMARY KEY (`sched_name`,`calendar_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='日历信息表';

-- ----------------------------
-- Records of qrtz_calendars
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_cron_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_cron_triggers`;
CREATE TABLE `qrtz_cron_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `cron_expression` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'cron表达式',
  `time_zone_id` varchar(80) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '时区',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_cron_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='Cron类型的触发器表';

-- ----------------------------
-- Records of qrtz_cron_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_fired_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_fired_triggers`;
CREATE TABLE `qrtz_fired_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `entry_id` varchar(95) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例id',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `instance_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度器实例名',
  `fired_time` bigint(13) NOT NULL COMMENT '触发的时间',
  `sched_time` bigint(13) NOT NULL COMMENT '定时器制定的时间',
  `priority` int(11) NOT NULL COMMENT '优先级',
  `state` varchar(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务名称',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '任务组名',
  `is_nonconcurrent` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否并发',
  `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '是否接受恢复执行',
  PRIMARY KEY (`sched_name`,`entry_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='已触发的触发器表';

-- ----------------------------
-- Records of qrtz_fired_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_job_details
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_job_details`;
CREATE TABLE `qrtz_job_details` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `description` varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `job_class_name` varchar(250) COLLATE utf8mb4_general_ci NOT NULL COMMENT '执行任务类名称',
  `is_durable` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否持久化',
  `is_nonconcurrent` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否并发',
  `is_update_data` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否更新数据',
  `requests_recovery` varchar(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '是否接受恢复执行',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`job_name`,`job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='任务详细信息表';

-- ----------------------------
-- Records of qrtz_job_details
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_locks
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_locks`;
CREATE TABLE `qrtz_locks` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `lock_name` varchar(40) COLLATE utf8mb4_general_ci NOT NULL COMMENT '悲观锁名称',
  PRIMARY KEY (`sched_name`,`lock_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='存储的悲观锁信息表';

-- ----------------------------
-- Records of qrtz_locks
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_paused_trigger_grps
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_paused_trigger_grps`;
CREATE TABLE `qrtz_paused_trigger_grps` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  PRIMARY KEY (`sched_name`,`trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='暂停的触发器表';

-- ----------------------------
-- Records of qrtz_paused_trigger_grps
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_scheduler_state
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_scheduler_state`;
CREATE TABLE `qrtz_scheduler_state` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `instance_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '实例名称',
  `last_checkin_time` bigint(13) NOT NULL COMMENT '上次检查时间',
  `checkin_interval` bigint(13) NOT NULL COMMENT '检查间隔时间',
  PRIMARY KEY (`sched_name`,`instance_name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='调度器状态表';

-- ----------------------------
-- Records of qrtz_scheduler_state
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simple_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simple_triggers`;
CREATE TABLE `qrtz_simple_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `repeat_count` bigint(7) NOT NULL COMMENT '重复的次数统计',
  `repeat_interval` bigint(12) NOT NULL COMMENT '重复的间隔时间',
  `times_triggered` bigint(10) NOT NULL COMMENT '已经触发的次数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simple_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='简单触发器的信息表';

-- ----------------------------
-- Records of qrtz_simple_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_simprop_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_simprop_triggers`;
CREATE TABLE `qrtz_simprop_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_name的外键',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_triggers表trigger_group的外键',
  `str_prop_1` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第一个参数',
  `str_prop_2` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第二个参数',
  `str_prop_3` varchar(512) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'String类型的trigger的第三个参数',
  `int_prop_1` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第一个参数',
  `int_prop_2` int(11) DEFAULT NULL COMMENT 'int类型的trigger的第二个参数',
  `long_prop_1` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第一个参数',
  `long_prop_2` bigint(20) DEFAULT NULL COMMENT 'long类型的trigger的第二个参数',
  `dec_prop_1` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第一个参数',
  `dec_prop_2` decimal(13,4) DEFAULT NULL COMMENT 'decimal类型的trigger的第二个参数',
  `bool_prop_1` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第一个参数',
  `bool_prop_2` varchar(1) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT 'Boolean类型的trigger的第二个参数',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  CONSTRAINT `qrtz_simprop_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `trigger_name`, `trigger_group`) REFERENCES `qrtz_triggers` (`sched_name`, `trigger_name`, `trigger_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='同步机制的行锁表';

-- ----------------------------
-- Records of qrtz_simprop_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for qrtz_triggers
-- ----------------------------
DROP TABLE IF EXISTS `qrtz_triggers`;
CREATE TABLE `qrtz_triggers` (
  `sched_name` varchar(120) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调度名称',
  `trigger_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的名字',
  `trigger_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器所属组的名字',
  `job_name` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_name的外键',
  `job_group` varchar(200) COLLATE utf8mb4_general_ci NOT NULL COMMENT 'qrtz_job_details表job_group的外键',
  `description` varchar(250) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '相关介绍',
  `next_fire_time` bigint(13) DEFAULT NULL COMMENT '上一次触发时间（毫秒）',
  `prev_fire_time` bigint(13) DEFAULT NULL COMMENT '下一次触发时间（默认为-1表示不触发）',
  `priority` int(11) DEFAULT NULL COMMENT '优先级',
  `trigger_state` varchar(16) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器状态',
  `trigger_type` varchar(8) COLLATE utf8mb4_general_ci NOT NULL COMMENT '触发器的类型',
  `start_time` bigint(13) NOT NULL COMMENT '开始时间',
  `end_time` bigint(13) DEFAULT NULL COMMENT '结束时间',
  `calendar_name` varchar(200) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日程表名称',
  `misfire_instr` smallint(2) DEFAULT NULL COMMENT '补偿执行的策略',
  `job_data` blob COMMENT '存放持久化job对象',
  PRIMARY KEY (`sched_name`,`trigger_name`,`trigger_group`),
  KEY `sched_name` (`sched_name`,`job_name`,`job_group`),
  CONSTRAINT `qrtz_triggers_ibfk_1` FOREIGN KEY (`sched_name`, `job_name`, `job_group`) REFERENCES `qrtz_job_details` (`sched_name`, `job_name`, `job_group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='触发器详细信息表';

-- ----------------------------
-- Records of qrtz_triggers
-- ----------------------------

-- ----------------------------
-- Table structure for refund_record
-- ----------------------------
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='退款记录表';

-- ----------------------------
-- Records of refund_record
-- ----------------------------

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config` (
  `config_id` int(5) NOT NULL AUTO_INCREMENT COMMENT '参数主键',
  `config_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数名称',
  `config_key` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键名',
  `config_value` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '参数键值',
  `config_type` char(1) COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '系统内置（Y是 N否）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`config_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='参数配置表';

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('1', '主框架页-默认皮肤样式名称', 'sys.index.skinName', 'skin-blue', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '蓝色 skin-blue、绿色 skin-green、紫色 skin-purple、红色 skin-red、黄色 skin-yellow');
INSERT INTO `sys_config` VALUES ('2', '用户管理-账号初始密码', 'sys.user.initPassword', '123456', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '初始化密码 123456');
INSERT INTO `sys_config` VALUES ('3', '主框架页-侧边栏主题', 'sys.index.sideTheme', 'theme-dark', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '深色主题theme-dark，浅色主题theme-light');
INSERT INTO `sys_config` VALUES ('4', '账号自助-验证码开关', 'sys.account.captchaEnabled', 'true', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '是否开启验证码功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES ('5', '账号自助-是否开启用户注册功能', 'sys.account.registerUser', 'false', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '是否开启注册用户功能（true开启，false关闭）');
INSERT INTO `sys_config` VALUES ('6', '用户登录-黑名单列表', 'sys.login.blackIPList', '', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '设置登录IP黑名单限制，多个匹配项以;分隔，支持匹配（*通配、网段）');
INSERT INTO `sys_config` VALUES ('7', '用户管理-初始密码修改策略', 'sys.account.initPasswordModify', '1', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '0：初始密码修改策略关闭，没有任何提示，1：提醒用户，如果未修改初始密码，则在登录时就会提醒修改密码对话框');
INSERT INTO `sys_config` VALUES ('8', '用户管理-账号密码更新周期', 'sys.account.passwordValidateDays', '0', 'Y', 'admin', '2025-10-28 02:47:08', '', null, '密码更新周期（填写数字，数据初始化值为0不限制，若修改必须为大于0小于365的正整数），如果超过这个周期登录系统时，则在登录时就会提醒修改密码对话框');

-- ----------------------------
-- Table structure for sys_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_dept`;
CREATE TABLE `sys_dept` (
  `dept_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '部门id',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父部门id',
  `ancestors` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '祖级列表',
  `dept_name` varchar(30) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `leader` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '负责人',
  `phone` varchar(11) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '联系电话',
  `email` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '邮箱',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '部门状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`dept_id`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='部门表';

-- ----------------------------
-- Records of sys_dept
-- ----------------------------
INSERT INTO `sys_dept` VALUES ('100', '0', '0', '若依科技', '0', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('101', '100', '0,100', '深圳总公司', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('102', '100', '0,100', '长沙分公司', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('103', '101', '0,100,101', '研发部门', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('104', '101', '0,100,101', '市场部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('105', '101', '0,100,101', '测试部门', '3', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('106', '101', '0,100,101', '财务部门', '4', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('107', '101', '0,100,101', '运维部门', '5', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('108', '102', '0,100,102', '市场部门', '1', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);
INSERT INTO `sys_dept` VALUES ('109', '102', '0,100,102', '财务部门', '2', '若依', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', '', null);

-- ----------------------------
-- Table structure for sys_dict_data
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_data`;
CREATE TABLE `sys_dict_data` (
  `dict_code` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典编码',
  `dict_sort` int(4) DEFAULT '0' COMMENT '字典排序',
  `dict_label` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典标签',
  `dict_value` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典键值',
  `dict_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `css_class` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '样式属性（其他样式扩展）',
  `list_class` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '表格回显样式',
  `is_default` char(1) COLLATE utf8mb4_general_ci DEFAULT 'N' COMMENT '是否默认（Y是 N否）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_code`)
) ENGINE=InnoDB AUTO_INCREMENT=1024 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

-- ----------------------------
-- Records of sys_dict_data
-- ----------------------------
INSERT INTO `sys_dict_data` VALUES ('1', '1', '男', '0', 'sys_user_sex', '', '', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '性别男');
INSERT INTO `sys_dict_data` VALUES ('2', '2', '女', '1', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '性别女');
INSERT INTO `sys_dict_data` VALUES ('3', '3', '未知', '2', 'sys_user_sex', '', '', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '性别未知');
INSERT INTO `sys_dict_data` VALUES ('4', '1', '显示', '0', 'sys_show_hide', '', 'primary', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '显示菜单');
INSERT INTO `sys_dict_data` VALUES ('5', '2', '隐藏', '1', 'sys_show_hide', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '隐藏菜单');
INSERT INTO `sys_dict_data` VALUES ('6', '1', '正常', '0', 'sys_normal_disable', '', 'primary', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('7', '2', '停用', '1', 'sys_normal_disable', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('8', '1', '正常', '0', 'sys_job_status', '', 'primary', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('9', '2', '暂停', '1', 'sys_job_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('10', '1', '默认', 'DEFAULT', 'sys_job_group', '', '', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '默认分组');
INSERT INTO `sys_dict_data` VALUES ('11', '2', '系统', 'SYSTEM', 'sys_job_group', '', '', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '系统分组');
INSERT INTO `sys_dict_data` VALUES ('12', '1', '是', 'Y', 'sys_yes_no', '', 'primary', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '系统默认是');
INSERT INTO `sys_dict_data` VALUES ('13', '2', '否', 'N', 'sys_yes_no', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '系统默认否');
INSERT INTO `sys_dict_data` VALUES ('14', '1', '通知', '1', 'sys_notice_type', '', 'warning', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '通知');
INSERT INTO `sys_dict_data` VALUES ('15', '2', '公告', '2', 'sys_notice_type', '', 'success', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '公告');
INSERT INTO `sys_dict_data` VALUES ('16', '1', '正常', '0', 'sys_notice_status', '', 'primary', 'Y', '0', 'admin', '2025-10-28 02:47:08', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('17', '2', '关闭', '1', 'sys_notice_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '关闭状态');
INSERT INTO `sys_dict_data` VALUES ('18', '99', '其他', '0', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '其他操作');
INSERT INTO `sys_dict_data` VALUES ('19', '1', '新增', '1', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '新增操作');
INSERT INTO `sys_dict_data` VALUES ('20', '2', '修改', '2', 'sys_oper_type', '', 'info', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '修改操作');
INSERT INTO `sys_dict_data` VALUES ('21', '3', '删除', '3', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '删除操作');
INSERT INTO `sys_dict_data` VALUES ('22', '4', '授权', '4', 'sys_oper_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '授权操作');
INSERT INTO `sys_dict_data` VALUES ('23', '5', '导出', '5', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '导出操作');
INSERT INTO `sys_dict_data` VALUES ('24', '6', '导入', '6', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '导入操作');
INSERT INTO `sys_dict_data` VALUES ('25', '7', '强退', '7', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '强退操作');
INSERT INTO `sys_dict_data` VALUES ('26', '8', '生成代码', '8', 'sys_oper_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '生成操作');
INSERT INTO `sys_dict_data` VALUES ('27', '9', '清空数据', '9', 'sys_oper_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '清空操作');
INSERT INTO `sys_dict_data` VALUES ('28', '1', '成功', '0', 'sys_common_status', '', 'primary', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('29', '2', '失败', '1', 'sys_common_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 02:47:08', '', null, '停用状态');
INSERT INTO `sys_dict_data` VALUES ('103', '1', '民办机构', '1', 'pension_institution_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '民办养老机构');
INSERT INTO `sys_dict_data` VALUES ('104', '2', '公办机构', '2', 'pension_institution_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '公办养老机构');
INSERT INTO `sys_dict_data` VALUES ('105', '3', '公建民营', '3', 'pension_institution_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '公建民营机构');
INSERT INTO `sys_dict_data` VALUES ('106', '1', '待审批', '0', 'pension_institution_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构入驻申请待审批');
INSERT INTO `sys_dict_data` VALUES ('107', '2', '已入驻', '1', 'pension_institution_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构已正式入驻');
INSERT INTO `sys_dict_data` VALUES ('108', '3', '已驳回', '2', 'pension_institution_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构入驻申请被驳回');
INSERT INTO `sys_dict_data` VALUES ('109', '1', '营业执照', '1', 'pension_attach_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构营业执照附件');
INSERT INTO `sys_dict_data` VALUES ('110', '2', '批准证书', '2', 'pension_attach_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '社会福利机构设置批准证书');
INSERT INTO `sys_dict_data` VALUES ('111', '3', '三方协议', '3', 'pension_attach_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构+银行+民政三方监管协议');
INSERT INTO `sys_dict_data` VALUES ('200', '0', '自理', '1', 'elder_care_level', '', 'primary', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '生活完全自理');
INSERT INTO `sys_dict_data` VALUES ('201', '0', '半护理', '2', 'elder_care_level', '', 'warning', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '需要部分护理');
INSERT INTO `sys_dict_data` VALUES ('202', '0', '全护理', '3', 'elder_care_level', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '需要全面护理');
INSERT INTO `sys_dict_data` VALUES ('203', '0', '未入住', '0', 'elder_status', '', 'info', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '尚未入住');
INSERT INTO `sys_dict_data` VALUES ('204', '0', '已入住', '1', 'elder_status', '', 'success', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '已入住');
INSERT INTO `sys_dict_data` VALUES ('205', '0', '已退住', '2', 'elder_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '已退住');
INSERT INTO `sys_dict_data` VALUES ('206', '0', '普通床位', '1', 'bed_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '标准普通床位');
INSERT INTO `sys_dict_data` VALUES ('207', '0', '豪华床位', '2', 'bed_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '豪华舒适床位');
INSERT INTO `sys_dict_data` VALUES ('208', '0', '医疗床位', '3', 'bed_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '医疗护理床位');
INSERT INTO `sys_dict_data` VALUES ('209', '0', '空置', '0', 'bed_status', '', 'success', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '床位空置可用');
INSERT INTO `sys_dict_data` VALUES ('210', '0', '占用', '1', 'bed_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '床位已被占用');
INSERT INTO `sys_dict_data` VALUES ('211', '0', '维修', '2', 'bed_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '床位维修中');
INSERT INTO `sys_dict_data` VALUES ('212', '0', '申请中', '0', 'check_in_status', '', 'primary', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '入住申请处理中');
INSERT INTO `sys_dict_data` VALUES ('213', '0', '已入住', '1', 'check_in_status', '', 'success', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '已成功入住');
INSERT INTO `sys_dict_data` VALUES ('214', '0', '已拒绝', '2', 'check_in_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '申请被拒绝');
INSERT INTO `sys_dict_data` VALUES ('215', '0', '已取消', '3', 'check_in_status', '', 'info', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '申请已取消');
INSERT INTO `sys_dict_data` VALUES ('216', '0', '在住', '1', 'allocation_status', '', 'success', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '老人在住');
INSERT INTO `sys_dict_data` VALUES ('217', '0', '已退住', '2', 'allocation_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '老人已退住');
INSERT INTO `sys_dict_data` VALUES ('218', '0', '未支付', '0', 'deposit_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '押金未支付');
INSERT INTO `sys_dict_data` VALUES ('219', '0', '已支付', '1', 'deposit_status', '', 'success', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '押金已支付');
INSERT INTO `sys_dict_data` VALUES ('220', '0', '已退还', '2', 'deposit_status', '', 'info', 'N', '0', 'admin', '2025-10-28 18:35:38', '', null, '押金已退还');
INSERT INTO `sys_dict_data` VALUES ('221', '0', '男', '1', 'elder_gender', '', 'primary', 'N', '0', 'admin', '2025-10-28 19:03:57', '', null, '男性');
INSERT INTO `sys_dict_data` VALUES ('222', '0', '女', '2', 'elder_gender', '', 'danger', 'N', '0', 'admin', '2025-10-28 19:03:57', '', null, '女性');
INSERT INTO `sys_dict_data` VALUES ('300', '0', '床位费', '1', 'order_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '床位使用费用');
INSERT INTO `sys_dict_data` VALUES ('301', '0', '护理费', '2', 'order_type', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '护理服务费用');
INSERT INTO `sys_dict_data` VALUES ('302', '0', '餐饮费', '3', 'order_type', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '餐饮服务费用');
INSERT INTO `sys_dict_data` VALUES ('303', '0', '医疗费', '4', 'order_type', '', 'danger', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '医疗服务费用');
INSERT INTO `sys_dict_data` VALUES ('304', '0', '其他费用', '5', 'order_type', '', 'info', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '其他杂项费用');
INSERT INTO `sys_dict_data` VALUES ('305', '0', '待支付', '0', 'order_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '订单待支付');
INSERT INTO `sys_dict_data` VALUES ('306', '0', '已支付', '1', 'order_status', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '订单已支付');
INSERT INTO `sys_dict_data` VALUES ('307', '0', '已取消', '2', 'order_status', '', 'info', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '订单已取消');
INSERT INTO `sys_dict_data` VALUES ('308', '0', '已退款', '3', 'order_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '订单已退款');
INSERT INTO `sys_dict_data` VALUES ('309', '0', '微信支付', 'wechat', 'payment_method', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '微信扫码支付');
INSERT INTO `sys_dict_data` VALUES ('310', '0', '支付宝', 'alipay', 'payment_method', '', 'primary', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '支付宝支付');
INSERT INTO `sys_dict_data` VALUES ('311', '0', '银行卡转账', 'bank', 'payment_method', '', 'info', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '银行转账支付');
INSERT INTO `sys_dict_data` VALUES ('312', '0', '现金', 'cash', 'payment_method', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '现金支付');
INSERT INTO `sys_dict_data` VALUES ('313', '0', '处理中', '0', 'payment_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '支付处理中');
INSERT INTO `sys_dict_data` VALUES ('314', '0', '支付成功', '1', 'payment_status', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '支付成功');
INSERT INTO `sys_dict_data` VALUES ('315', '0', '支付失败', '2', 'payment_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '支付失败');
INSERT INTO `sys_dict_data` VALUES ('316', '0', '申请中', '0', 'refund_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '退款申请中');
INSERT INTO `sys_dict_data` VALUES ('317', '0', '已退款', '1', 'refund_status', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '退款已完成');
INSERT INTO `sys_dict_data` VALUES ('318', '0', '已拒绝', '2', 'refund_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '退款申请被拒绝');
INSERT INTO `sys_dict_data` VALUES ('319', '0', '月度', 'monthly', 'billing_cycle', '', 'primary', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '按月计费');
INSERT INTO `sys_dict_data` VALUES ('320', '0', '季度', 'quarterly', 'billing_cycle', '', 'success', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '按季度计费');
INSERT INTO `sys_dict_data` VALUES ('321', '0', '年度', 'yearly', 'billing_cycle', '', 'warning', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '按年计费');
INSERT INTO `sys_dict_data` VALUES ('322', '0', '一次性', 'once', 'billing_cycle', '', 'info', 'N', '0', 'admin', '2025-10-28 20:01:58', '', null, '一次性费用');
INSERT INTO `sys_dict_data` VALUES ('323', '1', '正常', '1', 'account_status', '', 'primary', 'N', '0', 'admin', '2025-10-29 00:51:41', '', null, '正常状态');
INSERT INTO `sys_dict_data` VALUES ('324', '2', '冻结', '0', 'account_status', '', 'danger', 'N', '0', 'admin', '2025-10-29 00:51:41', '', null, '冻结状态');
INSERT INTO `sys_dict_data` VALUES ('325', '3', '销户', '2', 'account_status', '', 'info', 'N', '0', 'admin', '2025-10-29 00:51:41', '', null, '销户状态');
INSERT INTO `sys_dict_data` VALUES ('326', '1', '自动划拨', '1', 'transfer_type', '', 'primary', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '系统自动执行');
INSERT INTO `sys_dict_data` VALUES ('327', '2', '手动划拨', '2', 'transfer_type', '', 'warning', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '人工手动执行');
INSERT INTO `sys_dict_data` VALUES ('328', '3', '特殊申请', '3', 'transfer_type', '', 'info', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '特殊业务申请');
INSERT INTO `sys_dict_data` VALUES ('329', '1', '待处理', '0', 'transfer_status', '', 'warning', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '等待处理');
INSERT INTO `sys_dict_data` VALUES ('330', '2', '成功', '1', 'transfer_status', '', 'success', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '执行成功');
INSERT INTO `sys_dict_data` VALUES ('331', '3', '失败', '2', 'transfer_status', '', 'danger', 'N', '0', 'admin', '2025-10-29 00:53:04', '', null, '执行失败');
INSERT INTO `sys_dict_data` VALUES ('1000', '1', '医疗费用', '1', 'deposit_apply_type', '', 'danger', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '紧急医疗费用');
INSERT INTO `sys_dict_data` VALUES ('1001', '2', '生活用品', '2', 'deposit_apply_type', '', 'primary', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '日常生活用品');
INSERT INTO `sys_dict_data` VALUES ('1002', '3', '康复护理', '3', 'deposit_apply_type', '', 'warning', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '康复护理费用');
INSERT INTO `sys_dict_data` VALUES ('1003', '4', '其他费用', '4', 'deposit_apply_type', '', 'info', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '其他特殊费用');
INSERT INTO `sys_dict_data` VALUES ('1010', '1', '紧急', '1', 'urgency_level', '', 'danger', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '紧急情况需立即处理');
INSERT INTO `sys_dict_data` VALUES ('1011', '2', '普通', '2', 'urgency_level', '', 'primary', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '普通情况按流程处理');
INSERT INTO `sys_dict_data` VALUES ('1012', '3', '一般', '3', 'urgency_level', '', 'info', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '一般情况可延后处理');
INSERT INTO `sys_dict_data` VALUES ('1020', '1', '待审批', '0', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '等待审批处理');
INSERT INTO `sys_dict_data` VALUES ('1021', '2', '已批准', '1', 'deposit_apply_status', '', 'success', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '审批通过可以使用');
INSERT INTO `sys_dict_data` VALUES ('1022', '3', '已拒绝', '2', 'deposit_apply_status', '', 'danger', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '审批拒绝不能使用');
INSERT INTO `sys_dict_data` VALUES ('1023', '4', '已撤销', '3', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', '2025-10-29 11:22:01', '', null, '申请人已撤销申请');

-- ----------------------------
-- Table structure for sys_dict_type
-- ----------------------------
DROP TABLE IF EXISTS `sys_dict_type`;
CREATE TABLE `sys_dict_type` (
  `dict_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '字典主键',
  `dict_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典名称',
  `dict_type` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '字典类型',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`dict_id`),
  UNIQUE KEY `dict_type` (`dict_type`)
) ENGINE=InnoDB AUTO_INCREMENT=309 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

-- ----------------------------
-- Records of sys_dict_type
-- ----------------------------
INSERT INTO `sys_dict_type` VALUES ('1', '用户性别', 'sys_user_sex', '0', 'admin', '2025-10-28 02:47:08', '', null, '用户性别列表');
INSERT INTO `sys_dict_type` VALUES ('2', '菜单状态', 'sys_show_hide', '0', 'admin', '2025-10-28 02:47:08', '', null, '菜单状态列表');
INSERT INTO `sys_dict_type` VALUES ('3', '系统开关', 'sys_normal_disable', '0', 'admin', '2025-10-28 02:47:08', '', null, '系统开关列表');
INSERT INTO `sys_dict_type` VALUES ('4', '任务状态', 'sys_job_status', '0', 'admin', '2025-10-28 02:47:08', '', null, '任务状态列表');
INSERT INTO `sys_dict_type` VALUES ('5', '任务分组', 'sys_job_group', '0', 'admin', '2025-10-28 02:47:08', '', null, '任务分组列表');
INSERT INTO `sys_dict_type` VALUES ('6', '系统是否', 'sys_yes_no', '0', 'admin', '2025-10-28 02:47:08', '', null, '系统是否列表');
INSERT INTO `sys_dict_type` VALUES ('7', '通知类型', 'sys_notice_type', '0', 'admin', '2025-10-28 02:47:08', '', null, '通知类型列表');
INSERT INTO `sys_dict_type` VALUES ('8', '通知状态', 'sys_notice_status', '0', 'admin', '2025-10-28 02:47:08', '', null, '通知状态列表');
INSERT INTO `sys_dict_type` VALUES ('9', '操作类型', 'sys_oper_type', '0', 'admin', '2025-10-28 02:47:08', '', null, '操作类型列表');
INSERT INTO `sys_dict_type` VALUES ('10', '系统状态', 'sys_common_status', '0', 'admin', '2025-10-28 02:47:08', '', null, '登录状态列表');
INSERT INTO `sys_dict_type` VALUES ('100', '押金申请类型', 'deposit_apply_type', '0', 'admin', '2025-10-29 11:22:01', '', null, '押金使用申请类型列表');
INSERT INTO `sys_dict_type` VALUES ('101', '紧急程度', 'urgency_level', '0', 'admin', '2025-10-29 11:22:01', '', null, '紧急程度分类列表');
INSERT INTO `sys_dict_type` VALUES ('104', '机构状态', 'pension_institution_status', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构状态列表');
INSERT INTO `sys_dict_type` VALUES ('200', '护理等级', 'elder_care_level', '0', 'admin', '2025-10-28 10:30:00', 'ry', '2025-10-28 10:30:00', '老人护理等级字典类型');
INSERT INTO `sys_dict_type` VALUES ('201', '老人状态', 'elder_status', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '老人状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('202', '床位类型', 'bed_type', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '床位类型字典类型');
INSERT INTO `sys_dict_type` VALUES ('203', '床位状态', 'bed_status', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '床位状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('204', '入住申请状态', 'check_in_status', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '入住申请状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('205', '分配状态', 'allocation_status', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '床位分配状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('206', '押金状态', 'deposit_status', '0', 'admin', '2025-10-28 18:35:38', 'ry', '2025-10-28 18:35:38', '押金状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('207', '性别', 'elder_gender', '0', 'admin', '2025-10-28 19:02:45', 'ry', '2025-10-28 19:02:45', '性别字典类型');
INSERT INTO `sys_dict_type` VALUES ('300', '订单类型', 'order_type', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '订单类型字典类型');
INSERT INTO `sys_dict_type` VALUES ('301', '订单状态', 'order_status', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '订单状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('302', '支付方式', 'payment_method', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '支付方式字典类型');
INSERT INTO `sys_dict_type` VALUES ('303', '支付状态', 'payment_status', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '支付状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('304', '退款状态', 'refund_status', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '退款状态字典类型');
INSERT INTO `sys_dict_type` VALUES ('305', '计费周期', 'billing_cycle', '0', 'admin', '2025-10-28 20:01:58', 'ry', '2025-10-28 20:01:58', '计费周期字典类型');
INSERT INTO `sys_dict_type` VALUES ('306', '账户状态', 'account_status', '0', 'admin', '2025-10-29 00:51:41', '', null, '老人账户状态列表');
INSERT INTO `sys_dict_type` VALUES ('307', '划拨类型', 'transfer_type', '0', 'admin', '2025-10-29 00:53:04', '', null, '资金划拨类型列表');
INSERT INTO `sys_dict_type` VALUES ('308', '划拨状态', 'transfer_status', '0', 'admin', '2025-10-29 00:53:04', '', null, '资金划拨状态列表');

-- ----------------------------
-- Table structure for sys_job
-- ----------------------------
DROP TABLE IF EXISTS `sys_job`;
CREATE TABLE `sys_job` (
  `job_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务ID',
  `job_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT '' COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'DEFAULT' COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `cron_expression` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT 'cron执行表达式',
  `misfire_policy` varchar(20) COLLATE utf8mb4_general_ci DEFAULT '3' COMMENT '计划执行错误策略（1立即执行 2执行一次 3放弃执行）',
  `concurrent` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '是否并发执行（0允许 1禁止）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态（0正常 1暂停）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注信息',
  PRIMARY KEY (`job_id`,`job_name`,`job_group`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度表';

-- ----------------------------
-- Records of sys_job
-- ----------------------------
INSERT INTO `sys_job` VALUES ('1', '系统默认（无参）', 'DEFAULT', 'ryTask.ryNoParams', '0/10 * * * * ?', '3', '1', '1', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_job` VALUES ('2', '系统默认（有参）', 'DEFAULT', 'ryTask.ryParams(\'ry\')', '0/15 * * * * ?', '3', '1', '1', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_job` VALUES ('3', '系统默认（多参）', 'DEFAULT', 'ryTask.ryMultipleParams(\'ry\', true, 2000L, 316.50D, 100)', '0/20 * * * * ?', '3', '1', '1', 'admin', '2025-10-28 02:47:08', '', null, '');

-- ----------------------------
-- Table structure for sys_job_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_job_log`;
CREATE TABLE `sys_job_log` (
  `job_log_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
  `job_name` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务名称',
  `job_group` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '任务组名',
  `invoke_target` varchar(500) COLLATE utf8mb4_general_ci NOT NULL COMMENT '调用目标字符串',
  `job_message` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '日志信息',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '执行状态（0正常 1失败）',
  `exception_info` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '异常信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`job_log_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='定时任务调度日志表';

-- ----------------------------
-- Records of sys_job_log
-- ----------------------------

-- ----------------------------
-- Table structure for sys_logininfor
-- ----------------------------
DROP TABLE IF EXISTS `sys_logininfor`;
CREATE TABLE `sys_logininfor` (
  `info_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '访问ID',
  `user_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户账号',
  `ipaddr` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录IP地址',
  `login_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '登录地点',
  `browser` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '浏览器类型',
  `os` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作系统',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '登录状态（0成功 1失败）',
  `msg` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '提示消息',
  `login_time` datetime DEFAULT NULL COMMENT '访问时间',
  PRIMARY KEY (`info_id`),
  KEY `idx_sys_logininfor_s` (`status`),
  KEY `idx_sys_logininfor_lt` (`login_time`)
) ENGINE=InnoDB AUTO_INCREMENT=112 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';

-- ----------------------------
-- Records of sys_logininfor
-- ----------------------------
INSERT INTO `sys_logininfor` VALUES ('100', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-28 16:52:47');
INSERT INTO `sys_logininfor` VALUES ('101', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-10-28 17:49:45');
INSERT INTO `sys_logininfor` VALUES ('102', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-28 17:49:49');
INSERT INTO `sys_logininfor` VALUES ('103', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-10-28 22:30:01');
INSERT INTO `sys_logininfor` VALUES ('104', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-28 22:30:03');
INSERT INTO `sys_logininfor` VALUES ('105', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-10-28 22:43:40');
INSERT INTO `sys_logininfor` VALUES ('106', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-28 22:43:44');
INSERT INTO `sys_logininfor` VALUES ('107', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-29 02:26:46');
INSERT INTO `sys_logininfor` VALUES ('108', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-29 11:44:49');
INSERT INTO `sys_logininfor` VALUES ('109', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-29 21:16:12');
INSERT INTO `sys_logininfor` VALUES ('110', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-29 22:24:41');
INSERT INTO `sys_logininfor` VALUES ('111', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-30 02:48:36');

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu` (
  `menu_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
  `menu_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜单名称',
  `parent_id` bigint(20) DEFAULT '0' COMMENT '父菜单ID',
  `order_num` int(4) DEFAULT '0' COMMENT '显示顺序',
  `path` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由地址',
  `component` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '组件路径',
  `query` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '路由参数',
  `route_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '路由名称',
  `is_frame` int(1) DEFAULT '1' COMMENT '是否为外链（0是 1否）',
  `is_cache` int(1) DEFAULT '0' COMMENT '是否缓存（0缓存 1不缓存）',
  `menu_type` char(1) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
  `visible` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0显示 1隐藏）',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '菜单状态（0正常 1停用）',
  `perms` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '#' COMMENT '菜单图标',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '备注',
  PRIMARY KEY (`menu_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4004 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '系统管理', '0', '1', 'system', null, '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2025-10-28 02:47:08', '', null, '系统管理目录');
INSERT INTO `sys_menu` VALUES ('2', '系统监控', '0', '2', 'monitor', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2025-10-28 02:47:08', '', null, '系统监控目录');
INSERT INTO `sys_menu` VALUES ('3', '系统工具', '0', '3', 'tool', null, '', '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2025-10-28 02:47:08', '', null, '系统工具目录');
INSERT INTO `sys_menu` VALUES ('4', '若依官网', '0', '4', 'http://ruoyi.vip', null, '', '', '0', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-10-28 02:47:08', '', null, '若依官网地址');
INSERT INTO `sys_menu` VALUES ('100', '用户管理', '1', '1', 'user', 'system/user/index', '', '', '1', '0', 'C', '0', '0', 'system:user:list', 'user', 'admin', '2025-10-28 02:47:08', '', null, '用户管理菜单');
INSERT INTO `sys_menu` VALUES ('101', '角色管理', '1', '2', 'role', 'system/role/index', '', '', '1', '0', 'C', '0', '0', 'system:role:list', 'peoples', 'admin', '2025-10-28 02:47:08', '', null, '角色管理菜单');
INSERT INTO `sys_menu` VALUES ('102', '菜单管理', '1', '3', 'menu', 'system/menu/index', '', '', '1', '0', 'C', '0', '0', 'system:menu:list', 'tree-table', 'admin', '2025-10-28 02:47:08', '', null, '菜单管理菜单');
INSERT INTO `sys_menu` VALUES ('103', '部门管理', '1', '4', 'dept', 'system/dept/index', '', '', '1', '0', 'C', '0', '0', 'system:dept:list', 'tree', 'admin', '2025-10-28 02:47:08', '', null, '部门管理菜单');
INSERT INTO `sys_menu` VALUES ('104', '岗位管理', '1', '5', 'post', 'system/post/index', '', '', '1', '0', 'C', '0', '0', 'system:post:list', 'post', 'admin', '2025-10-28 02:47:08', '', null, '岗位管理菜单');
INSERT INTO `sys_menu` VALUES ('105', '字典管理', '1', '6', 'dict', 'system/dict/index', '', '', '1', '0', 'C', '0', '0', 'system:dict:list', 'dict', 'admin', '2025-10-28 02:47:08', '', null, '字典管理菜单');
INSERT INTO `sys_menu` VALUES ('106', '参数设置', '1', '7', 'config', 'system/config/index', '', '', '1', '0', 'C', '0', '0', 'system:config:list', 'edit', 'admin', '2025-10-28 02:47:08', '', null, '参数设置菜单');
INSERT INTO `sys_menu` VALUES ('107', '通知公告', '1', '8', 'notice', 'system/notice/index', '', '', '1', '0', 'C', '0', '0', 'system:notice:list', 'message', 'admin', '2025-10-28 02:47:08', '', null, '通知公告菜单');
INSERT INTO `sys_menu` VALUES ('108', '日志管理', '1', '9', 'log', '', '', '', '1', '0', 'M', '0', '0', '', 'log', 'admin', '2025-10-28 02:47:08', '', null, '日志管理菜单');
INSERT INTO `sys_menu` VALUES ('109', '在线用户', '2', '1', 'online', 'monitor/online/index', '', '', '1', '0', 'C', '0', '0', 'monitor:online:list', 'online', 'admin', '2025-10-28 02:47:08', '', null, '在线用户菜单');
INSERT INTO `sys_menu` VALUES ('110', '定时任务', '2', '2', 'job', 'monitor/job/index', '', '', '1', '0', 'C', '0', '0', 'monitor:job:list', 'job', 'admin', '2025-10-28 02:47:08', '', null, '定时任务菜单');
INSERT INTO `sys_menu` VALUES ('111', '数据监控', '2', '3', 'druid', 'monitor/druid/index', '', '', '1', '0', 'C', '0', '0', 'monitor:druid:list', 'druid', 'admin', '2025-10-28 02:47:08', '', null, '数据监控菜单');
INSERT INTO `sys_menu` VALUES ('112', '服务监控', '2', '4', 'server', 'monitor/server/index', '', '', '1', '0', 'C', '0', '0', 'monitor:server:list', 'server', 'admin', '2025-10-28 02:47:08', '', null, '服务监控菜单');
INSERT INTO `sys_menu` VALUES ('113', '缓存监控', '2', '5', 'cache', 'monitor/cache/index', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis', 'admin', '2025-10-28 02:47:08', '', null, '缓存监控菜单');
INSERT INTO `sys_menu` VALUES ('114', '缓存列表', '2', '6', 'cacheList', 'monitor/cache/list', '', '', '1', '0', 'C', '0', '0', 'monitor:cache:list', 'redis-list', 'admin', '2025-10-28 02:47:08', '', null, '缓存列表菜单');
INSERT INTO `sys_menu` VALUES ('115', '表单构建', '3', '1', 'build', 'tool/build/index', '', '', '1', '0', 'C', '0', '0', 'tool:build:list', 'build', 'admin', '2025-10-28 02:47:08', '', null, '表单构建菜单');
INSERT INTO `sys_menu` VALUES ('116', '代码生成', '3', '2', 'gen', 'tool/gen/index', '', '', '1', '0', 'C', '0', '0', 'tool:gen:list', 'code', 'admin', '2025-10-28 02:47:08', '', null, '代码生成菜单');
INSERT INTO `sys_menu` VALUES ('117', '系统接口', '3', '3', 'swagger', 'tool/swagger/index', '', '', '1', '0', 'C', '0', '0', 'tool:swagger:list', 'swagger', 'admin', '2025-10-28 02:47:08', '', null, '系统接口菜单');
INSERT INTO `sys_menu` VALUES ('500', '操作日志', '108', '1', 'operlog', 'monitor/operlog/index', '', '', '1', '0', 'C', '0', '0', 'monitor:operlog:list', 'form', 'admin', '2025-10-28 02:47:08', '', null, '操作日志菜单');
INSERT INTO `sys_menu` VALUES ('501', '登录日志', '108', '2', 'logininfor', 'monitor/logininfor/index', '', '', '1', '0', 'C', '0', '0', 'monitor:logininfor:list', 'logininfor', 'admin', '2025-10-28 02:47:08', '', null, '登录日志菜单');
INSERT INTO `sys_menu` VALUES ('1000', '用户查询', '100', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1001', '用户新增', '100', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1002', '用户修改', '100', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1003', '用户删除', '100', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1004', '用户导出', '100', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1005', '用户导入', '100', '6', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:import', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1006', '重置密码', '100', '7', '', '', '', '', '1', '0', 'F', '0', '0', 'system:user:resetPwd', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1007', '角色查询', '101', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1008', '角色新增', '101', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1009', '角色修改', '101', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1010', '角色删除', '101', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1011', '角色导出', '101', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:role:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1012', '菜单查询', '102', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1013', '菜单新增', '102', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1014', '菜单修改', '102', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1015', '菜单删除', '102', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:menu:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1016', '部门查询', '103', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1017', '部门新增', '103', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1018', '部门修改', '103', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1019', '部门删除', '103', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:dept:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1020', '岗位查询', '104', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1021', '岗位新增', '104', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1022', '岗位修改', '104', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1023', '岗位删除', '104', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1024', '岗位导出', '104', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'system:post:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1025', '字典查询', '105', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1026', '字典新增', '105', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1027', '字典修改', '105', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1028', '字典删除', '105', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1029', '字典导出', '105', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:dict:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1030', '参数查询', '106', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1031', '参数新增', '106', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1032', '参数修改', '106', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1033', '参数删除', '106', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1034', '参数导出', '106', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:config:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1035', '公告查询', '107', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1036', '公告新增', '107', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1037', '公告修改', '107', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1038', '公告删除', '107', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'system:notice:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1039', '操作查询', '500', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1040', '操作删除', '500', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1041', '日志导出', '500', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:operlog:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1042', '登录查询', '501', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1043', '登录删除', '501', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1044', '日志导出', '501', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1045', '账户解锁', '501', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:logininfor:unlock', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1046', '在线查询', '109', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1047', '批量强退', '109', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:batchLogout', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1048', '单条强退', '109', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:online:forceLogout', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1049', '任务查询', '110', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1050', '任务新增', '110', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:add', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1051', '任务修改', '110', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1052', '任务删除', '110', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1053', '状态修改', '110', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:changeStatus', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1054', '任务导出', '110', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'monitor:job:export', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1055', '生成查询', '116', '1', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:query', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1056', '生成修改', '116', '2', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:edit', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1057', '生成删除', '116', '3', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:remove', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1058', '导入代码', '116', '4', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:import', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1059', '预览代码', '116', '5', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:preview', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('1060', '生成代码', '116', '6', '#', '', '', '', '1', '0', 'F', '0', '0', 'tool:gen:code', '#', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_menu` VALUES ('2000', '养老机构管理', '0', '1', 'pension', null, null, null, '1', '0', 'M', '0', '0', '', 'tree', 'admin', '2025-10-28 10:00:00', '', null, 'Pension institution management directory');
INSERT INTO `sys_menu` VALUES ('2001', '机构入驻申请', '2000', '1', 'institution', 'pension/institution/index', null, null, '1', '0', 'C', '0', '0', 'pension:institution:list', 'build', 'admin', '2025-10-28 10:00:00', '', null, 'Pension institution application menu');
INSERT INTO `sys_menu` VALUES ('2002', '机构入驻申请查询', '2001', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'pension:institution:query', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2003', '机构入驻申请新增', '2001', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'pension:institution:add', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2004', '机构入驻申请修改', '2001', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'pension:institution:edit', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2005', '机构入驻申请删除', '2001', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'pension:institution:remove', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2006', '机构入驻申请导出', '2001', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'pension:institution:export', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2100', '老人管理', '0', '9', 'elder', null, null, null, '1', '0', 'M', '0', '0', '', 'peoples', 'admin', '2025-10-28 16:00:00', '', null, '老人管理功能模块');
INSERT INTO `sys_menu` VALUES ('2101', '老人信息', '2100', '1', 'info', 'elder/info/index', null, null, '1', '0', 'C', '0', '0', 'elder:info:list', 'user', 'admin', '2025-10-28 16:00:00', '', null, '老人信息管理菜单');
INSERT INTO `sys_menu` VALUES ('2102', '床位管理', '2100', '2', 'bed', 'elder/bed/index', null, null, '1', '0', 'C', '0', '0', 'elder:bed:list', 'tree', 'admin', '2025-10-28 16:00:00', '', null, '床位管理菜单');
INSERT INTO `sys_menu` VALUES ('2103', '入住申请', '2100', '3', 'checkin', 'elder/checkin/index', null, null, '1', '0', 'C', '0', '0', 'elder:checkin:list', 'component', 'admin', '2025-10-28 16:00:00', '', null, '入住申请管理菜单');
INSERT INTO `sys_menu` VALUES ('2111', '老人信息查询', '2101', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2112', '老人信息新增', '2101', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2113', '老人信息修改', '2101', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2114', '老人信息删除', '2101', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2115', '老人信息导出', '2101', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2121', '床位查询', '2102', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2122', '床位新增', '2102', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2123', '床位修改', '2102', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2124', '床位删除', '2102', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2125', '床位导出', '2102', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2131', '入住申请查询', '2103', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2132', '入住申请新增', '2103', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2133', '入住申请修改', '2103', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2134', '入住申请删除', '2103', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2135', '入住申请审批', '2103', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:approve', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2136', '入住申请导出', '2103', '6', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:checkin:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2137', '订单管理', '0', '5', 'order', null, null, '', '1', '0', 'M', '0', '0', null, 'shopping', 'admin', '2025-10-28 22:20:15', '', null, '订单管理目录');
INSERT INTO `sys_menu` VALUES ('2138', '订单列表', '2137', '1', 'orderInfo', 'order/orderInfo/index', null, '', '1', '0', 'C', '0', '0', 'order:info:list', 'list', 'admin', '2025-10-28 22:20:15', '', null, '订单管理菜单');
INSERT INTO `sys_menu` VALUES ('2139', '订单查询', '2138', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:query', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2140', '订单新增', '2138', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:add', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2141', '订单修改', '2138', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:edit', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2142', '订单删除', '2138', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:remove', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2143', '订单导出', '2138', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:export', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2144', '订单支付', '2138', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:pay', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2145', '订单取消', '2138', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:cancel', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2146', '支付记录', '2137', '2', 'paymentRecord', 'order/paymentRecord/index', null, '', '1', '0', 'C', '0', '0', 'payment:record:list', 'money', 'admin', '2025-10-28 22:20:15', '', null, '支付记录菜单');
INSERT INTO `sys_menu` VALUES ('2147', '支付查询', '2146', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:query', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2148', '支付新增', '2146', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:add', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2149', '支付修改', '2146', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:edit', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2150', '支付删除', '2146', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:remove', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2151', '支付导出', '2146', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:export', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2152', '支付状态', '2146', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:status', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2153', '支付统计', '2146', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'payment:record:statistics', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2154', '订单明细', '2137', '3', 'orderItem', 'order/orderItem/index', null, '', '1', '0', 'C', '0', '0', 'order:item:list', 'form', 'admin', '2025-10-28 22:20:15', '', null, '订单明细菜单');
INSERT INTO `sys_menu` VALUES ('2155', '明细查询', '2154', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:item:query', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2156', '明细新增', '2154', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:item:add', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2157', '明细修改', '2154', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:item:edit', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2158', '明细删除', '2154', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:item:remove', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2159', '明细导出', '2154', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:item:export', '#', 'admin', '2025-10-28 22:20:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('2161', '账户资金管理', '0', '5', 'account', null, null, '', '1', '0', 'M', '0', '0', '', 'money', 'admin', '2025-10-29 01:35:22', '', null, '账户资金管理目录');
INSERT INTO `sys_menu` VALUES ('2162', '老人账户管理', '2161', '1', 'accountInfo', 'pension/account/index', null, '', '1', '0', 'C', '0', '0', 'pension:account:list', 'user', 'admin', '2025-10-29 01:35:22', '', null, '老人账户管理菜单');
INSERT INTO `sys_menu` VALUES ('2163', '老人账户查询', '2162', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:query', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2164', '老人账户新增', '2162', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:add', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2165', '老人账户修改', '2162', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:edit', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2166', '老人账户删除', '2162', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:remove', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2167', '老人账户导出', '2162', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:export', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2168', '账户冻结', '2162', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:freeze', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2169', '账户解冻', '2162', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:account:unfreeze', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2170', '资金划拨管理', '2161', '2', 'fundTransfer', 'pension/fundTransfer/index', null, '', '1', '0', 'C', '0', '0', 'pension:fundTransfer:list', 'money', 'admin', '2025-10-29 01:35:22', '', null, '资金划拨管理菜单');
INSERT INTO `sys_menu` VALUES ('2171', '资金划拨查询', '2170', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:fundTransfer:query', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2172', '生成划拨单', '2170', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:fundTransfer:generate', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2173', '审批划拨单', '2170', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:fundTransfer:approve', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2174', '执行划拨', '2170', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:fundTransfer:execute', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2175', '资金划拨导出', '2170', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:fundTransfer:export', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2176', '交易记录查询', '2161', '3', 'transactionRecord', 'pension/transactionRecord/index', null, '', '1', '0', 'C', '0', '0', 'pension:transaction:list', 'list', 'admin', '2025-10-29 01:35:22', '', null, '交易记录查询菜单');
INSERT INTO `sys_menu` VALUES ('2177', '交易记录查询权限', '2176', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:transaction:query', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2178', '交易记录导出', '2176', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:transaction:export', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('2179', '余额预警', '2161', '4', 'balanceWarning', 'pension/balanceWarning/index', null, '', '1', '0', 'C', '0', '0', 'pension:warning:list', 'bell', 'admin', '2025-10-29 01:35:22', '', null, '余额预警菜单');
INSERT INTO `sys_menu` VALUES ('2180', '余额预警查询', '2179', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:warning:query', '#', 'admin', '2025-10-29 01:35:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('3000', '民政监管', '0', '1', 'supervision', null, '', '', '1', '0', 'M', '0', '0', '', 'guide', 'admin', '2025-10-29 04:49:09', '', null, '民政监管功能');
INSERT INTO `sys_menu` VALUES ('3001', '监管首页', '3000', '1', 'dashboard', 'supervision/dashboard/index', '', '', '1', '0', 'C', '0', '0', 'supervision:dashboard:view', 'dashboard', 'admin', '2025-10-29 04:49:09', '', null, '监管首页');
INSERT INTO `sys_menu` VALUES ('3010', '机构审批', '3000', '2', 'institution', null, '', '', '1', '0', 'M', '0', '0', '', 'peoples', 'admin', '2025-10-29 04:49:09', '', null, '机构审批管理');
INSERT INTO `sys_menu` VALUES ('3011', '入驻审批', '3010', '1', 'approval', 'supervision/institution/approval', '', '', '1', '0', 'C', '0', '0', 'supervision:institution:approval', 'form', 'admin', '2025-10-29 04:49:09', '', null, '机构入驻审批');
INSERT INTO `sys_menu` VALUES ('3012', '审批通过', '3011', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:institution:approve', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3013', '审批拒绝', '3011', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:institution:reject', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3014', '查看详情', '3011', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:institution:query', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3020', '资金审批', '3000', '3', 'fund', null, '', '', '1', '0', 'M', '0', '0', '', 'money', 'admin', '2025-10-29 04:49:09', '', null, '资金审批管理');
INSERT INTO `sys_menu` VALUES ('3021', '划拨审批', '3020', '1', 'transfer', 'supervision/fundTransfer/approval', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:transfer', 'shopping', 'admin', '2025-10-29 04:49:09', '', null, '资金划拨审批');
INSERT INTO `sys_menu` VALUES ('3022', '退款审批', '3020', '2', 'refund', 'supervision/refund/approval', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:refund', 'guide', 'admin', '2025-10-29 04:49:09', '', null, '退款审批');
INSERT INTO `sys_menu` VALUES ('3023', '押金审批', '3020', '3', 'deposit', 'supervision/deposit/approval', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:deposit', 'list', 'admin', '2025-10-29 04:49:09', '', null, '押金使用审批');
INSERT INTO `sys_menu` VALUES ('3024', '审批通过', '3021', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:fund:approve', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3025', '审批拒绝', '3021', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:fund:reject', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3030', '预警管理', '3000', '4', 'warning', 'pension/balanceWarning/index', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:view', 'bug', 'admin', '2025-10-29 04:49:09', '', null, '预警管理');
INSERT INTO `sys_menu` VALUES ('3031', '预警处理', '3030', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:warning:handle', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('3032', '预警忽略', '3030', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:warning:ignore', '#', 'admin', '2025-10-29 04:49:09', '', null, '');
INSERT INTO `sys_menu` VALUES ('4000', '数据大屏', '0', '8', 'bigscreen', null, null, '', '1', '0', 'M', '0', '0', null, 'chart', 'admin', '2025-10-29 22:21:46', '', null, '数据大屏菜单');
INSERT INTO `sys_menu` VALUES ('4001', '机构分布大屏', '4000', '1', 'http://localhost:8080/screen/institution-distribution.html', 'bigscreen/institution-distribution', null, '', '0', '0', 'C', '0', '0', 'bigscreen:institution:view', 'chart', 'admin', '2025-10-29 22:21:46', 'admin', '2025-10-30 01:15:20', '养老机构分布大屏');
INSERT INTO `sys_menu` VALUES ('4002', '资金监管大屏', '4000', '2', 'http://localhost:8080/screen/fund-supervision.html', 'bigscreen/fund-supervision', null, '', '0', '0', 'C', '0', '0', 'bigscreen:fund:view', 'money', 'admin', '2025-10-30 00:41:01', 'admin', '2025-10-30 01:15:59', '');
INSERT INTO `sys_menu` VALUES ('4003', '预警监控大屏', '4000', '3', 'http://localhost:8080/screen/warning-monitor.html', 'bigscreen/warning-monitor', null, '', '0', '0', 'C', '0', '0', 'bigscreen:warning:view', 'user', 'admin', '2025-10-30 01:20:52', 'admin', '2025-10-30 01:48:48', '');

-- ----------------------------
-- Table structure for sys_notice
-- ----------------------------
DROP TABLE IF EXISTS `sys_notice`;
CREATE TABLE `sys_notice` (
  `notice_id` int(4) NOT NULL AUTO_INCREMENT COMMENT '公告ID',
  `notice_title` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告标题',
  `notice_type` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '公告类型（1通知 2公告）',
  `notice_content` longblob COMMENT '公告内容',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '公告状态（0正常 1关闭）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`notice_id`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1', '温馨提醒：2018-07-01 若依新版本发布啦', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-10-28 02:47:08', '', null, '管理员');
INSERT INTO `sys_notice` VALUES ('2', '维护通知：2018-07-01 若依系统凌晨维护', '1', 0xE7BBB4E68AA4E58685E5AEB9, '0', 'admin', '2025-10-28 02:47:08', '', null, '管理员');

-- ----------------------------
-- Table structure for sys_oper_log
-- ----------------------------
DROP TABLE IF EXISTS `sys_oper_log`;
CREATE TABLE `sys_oper_log` (
  `oper_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `title` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '模块标题',
  `business_type` int(2) DEFAULT '0' COMMENT '业务类型（0其它 1新增 2修改 3删除）',
  `method` varchar(200) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '方法名称',
  `request_method` varchar(10) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求方式',
  `operator_type` int(1) DEFAULT '0' COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
  `oper_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作人员',
  `dept_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '部门名称',
  `oper_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求URL',
  `oper_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '主机地址',
  `oper_location` varchar(255) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '操作地点',
  `oper_param` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '请求参数',
  `json_result` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '返回参数',
  `status` int(1) DEFAULT '0' COMMENT '操作状态（0正常 1异常）',
  `error_msg` varchar(2000) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '错误消息',
  `oper_time` datetime DEFAULT NULL COMMENT '操作时间',
  `cost_time` bigint(20) DEFAULT '0' COMMENT '消耗时间',
  PRIMARY KEY (`oper_id`),
  KEY `idx_sys_oper_log_bt` (`business_type`),
  KEY `idx_sys_oper_log_s` (`status`),
  KEY `idx_sys_oper_log_ot` (`oper_time`)
) ENGINE=InnoDB AUTO_INCREMENT=132 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';

-- ----------------------------
-- Records of sys_oper_log
-- ----------------------------
INSERT INTO `sys_oper_log` VALUES ('100', '养老机构信息', '1', 'com.ruoyi.web.controller.PensionInstitutionController.add()', 'POST', '1', 'admin', '研发部门', '/pension/institution', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-10-28 16:59:21\",\"bedCount\":0,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-10-28 16:59:21\",\"creditCode\":\"12123123123\",\"fixedAssets\":0.0,\"institutionId\":2,\"institutionName\":\"测试服务机构\",\"institutionType\":\"2\",\"params\":{},\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":15.0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 16:59:21', '17');
INSERT INTO `sys_oper_log` VALUES ('101', '养老机构信息', '2', 'com.ruoyi.web.controller.PensionInstitutionController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/institution', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-10-28 16:59:21\",\"bedCount\":0,\"blacklistFlag\":\"0\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-10-28 16:59:21\",\"creditCode\":\"12123123123\",\"fixedAssets\":0.0,\"institutionId\":2,\"institutionName\":\"测试服务机构\",\"institutionType\":\"3\",\"params\":{},\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":15.0,\"status\":\"0\",\"updateBy\":\"\",\"updateTime\":\"2025-10-28 17:00:59\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 17:00:59', '19');
INSERT INTO `sys_oper_log` VALUES ('102', '养老机构信息', '1', 'com.ruoyi.web.controller.PensionInstitutionController.add()', 'POST', '1', 'admin', '研发部门', '/pension/institution', '127.0.0.1', '内网IP', '{\"applyTime\":\"2025-10-28 17:50:42\",\"bedCount\":0,\"contactPerson\":\"123\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-10-28 17:50:42\",\"creditCode\":\"123123\",\"fixedAssets\":0.0,\"institutionId\":3,\"institutionName\":\"123123\",\"institutionType\":\"3\",\"params\":{},\"registeredAddress\":\"测试地址\",\"registeredCapital\":0.0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 17:50:42', '31');
INSERT INTO `sys_oper_log` VALUES ('103', '审批机构入驻申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/approve', '127.0.0.1', '内网IP', '{\"approveRemark\":\"0\",\"approveTime\":\"2025-10-28 18:05:19\",\"approveUser\":\"admin\",\"fixedAssets\":null,\"institutionId\":3,\"institutionName\":\"123123\",\"params\":{},\"registeredCapital\":null,\"status\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 18:05:19', '4');
INSERT INTO `sys_oper_log` VALUES ('104', '订单主表', '1', 'com.ruoyi.web.controller.OrderInfoController.add()', 'POST', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-10-28 22:39:59\",\"orderAmount\":3,\"orderStatus\":\"0\",\"orderType\":\"1\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"wechat\",\"unpaidAmount\":3}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_type,                                                                 order_amount,             paid_amount,             order_status,             payment_method,                                                                                                        create_time )           values ( ?,                                                                 ?,             ?,             ?,             ?,                                                                                                        ? )\r\n### Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\n; Field \'order_no\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_no\' doesn\'t have a default value', '2025-10-28 22:39:59', '22');
INSERT INTO `sys_oper_log` VALUES ('105', '订单主表', '1', 'com.ruoyi.web.controller.OrderInfoController.add()', 'POST', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-10-28 22:41:01\",\"orderAmount\":4,\"orderStatus\":\"0\",\"orderType\":\"1\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"wechat\",\"unpaidAmount\":4}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_type,                                                                 order_amount,             paid_amount,             order_status,             payment_method,                                                                                                        create_time )           values ( ?,                                                                 ?,             ?,             ?,             ?,                                                                                                        ? )\r\n### Cause: java.sql.SQLException: Field \'order_no\' doesn\'t have a default value\n; Field \'order_no\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_no\' doesn\'t have a default value', '2025-10-28 22:41:01', '109');
INSERT INTO `sys_oper_log` VALUES ('106', '订单主表', '1', 'com.ruoyi.web.controller.OrderInfoController.add()', 'POST', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-10-28 22:43:56\",\"orderAmount\":4,\"orderNo\":\"ORD17616626368936569\",\"orderStatus\":\"1\",\"orderType\":\"1\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"wechat\",\"unpaidAmount\":4}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,                                                                 order_amount,             paid_amount,             order_status,             payment_method,                                                                                                        create_time )           values ( ?,             ?,                                                                 ?,             ?,             ?,             ?,                                                                                                        ? )\r\n### Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\n; Field \'elder_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value', '2025-10-28 22:43:56', '42');
INSERT INTO `sys_oper_log` VALUES ('107', '订单主表', '1', 'com.ruoyi.web.controller.OrderInfoController.add()', 'POST', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-10-28 22:46:40\",\"orderAmount\":3,\"orderNo\":\"ORD17616628009325067\",\"orderStatus\":\"1\",\"orderType\":\"2\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"wechat\",\"unpaidAmount\":3}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_date\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,                                                                 order_amount,             paid_amount,             order_status,             payment_method,                                                                                                        create_time )           values ( ?,             ?,                                                                 ?,             ?,             ?,             ?,                                                                                                        ? )\r\n### Cause: java.sql.SQLException: Field \'order_date\' doesn\'t have a default value\n; Field \'order_date\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_date\' doesn\'t have a default value', '2025-10-28 22:46:40', '6');
INSERT INTO `sys_oper_log` VALUES ('108', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":7,\"bedNumber\":\"10\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-10-28 23:27:05\",\"facilities\":\"电视机\",\"floorNumber\":1,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":3,\"params\":{},\"roomArea\":50,\"roomNumber\":\"101\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 23:27:05', '105');
INSERT INTO `sys_oper_log` VALUES ('109', '老人入住申请', '1', 'com.ruoyi.web.controller.ElderCheckInController.add()', 'POST', '1', 'admin', '研发部门', '/elder/checkin', '127.0.0.1', '内网IP', '{\"applyDate\":\"2025-10-28 23:27:43\",\"bedId\":7,\"careLevel\":\"1\",\"checkInId\":1,\"checkInStatus\":\"0\",\"elderId\":1,\"expectedCheckInDate\":\"2025-10-15\",\"institutionId\":3,\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-28 23:27:43', '32');
INSERT INTO `sys_oper_log` VALUES ('110', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'bedStatus\' not found. Available parameters are [arg1, arg0, param1, param2]', '2025-10-28 23:49:09', '17');
INSERT INTO `sys_oper_log` VALUES ('111', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'bedStatus\' not found. Available parameters are [arg1, arg0, param1, param2]', '2025-10-28 23:49:34', '20');
INSERT INTO `sys_oper_log` VALUES ('112', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-28 23:58:44', '46');
INSERT INTO `sys_oper_log` VALUES ('113', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-28 23:58:50', '14');
INSERT INTO `sys_oper_log` VALUES ('114', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-28 23:59:02', '16');
INSERT INTO `sys_oper_log` VALUES ('115', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-28 23:59:19', '11');
INSERT INTO `sys_oper_log` VALUES ('116', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"3\\n\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-29 00:00:18', '20');
INSERT INTO `sys_oper_log` VALUES ('117', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', null, '1', 'nested exception is org.apache.ibatis.binding.BindingException: Parameter \'checkInStatus\' not found. Available parameters are [arg3, arg2, arg1, arg0, param3, param4, param1, param2]', '2025-10-29 00:00:55', '16');
INSERT INTO `sys_oper_log` VALUES ('118', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":1,\"checkInStatus\":\"1\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:05:51', '41');
INSERT INTO `sys_oper_log` VALUES ('119', '生成订单', '1', 'com.ruoyi.web.controller.OrderInfoController.generateOrders()', 'POST', '1', 'admin', '研发部门', '/order/info/generate/1', '127.0.0.1', '内网IP', '1', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,             elder_id,             institution_id,             check_in_id,             bed_id,                                                                              order_date,                                       billing_cycle,                          create_by )           values ( ?,             ?,             ?,             ?,             ?,             ?,                                                                              ?,                                       ?,                          ? )\r\n### Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\n; Field \'order_amount\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value', '2025-10-29 00:05:58', '59');
INSERT INTO `sys_oper_log` VALUES ('120', '生成订单', '1', 'com.ruoyi.web.controller.OrderInfoController.generateOrders()', 'POST', '1', 'admin', '研发部门', '/order/info/generate/1', '127.0.0.1', '内网IP', '1', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,             elder_id,             institution_id,             check_in_id,             bed_id,                                                                              order_date,                                       billing_cycle,                          create_by )           values ( ?,             ?,             ?,             ?,             ?,             ?,                                                                              ?,                                       ?,                          ? )\r\n### Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\n; Field \'order_amount\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value', '2025-10-29 00:06:35', '7');
INSERT INTO `sys_oper_log` VALUES ('121', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":8,\"bedNumber\":\"103\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-10-29 00:10:14\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":3,\"params\":{},\"price\":5000,\"roomArea\":100,\"roomNumber\":\"202\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:10:14', '50');
INSERT INTO `sys_oper_log` VALUES ('122', '老人入住申请', '1', 'com.ruoyi.web.controller.ElderCheckInController.add()', 'POST', '1', 'admin', '研发部门', '/elder/checkin', '127.0.0.1', '内网IP', '{\"applyDate\":\"2025-10-29 00:11:17\",\"bedId\":8,\"careLevel\":\"3\",\"checkInId\":2,\"checkInStatus\":\"0\",\"depositAmount\":10000,\"elderId\":1,\"expectedCheckInDate\":\"2025-10-15\",\"institutionId\":3,\"interviewDate\":\"2025-10-30 00:00:00\",\"monthlyFee\":6000,\"params\":{},\"paymentMethod\":\"现场\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:11:17', '26');
INSERT INTO `sys_oper_log` VALUES ('123', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"0\",\"checkInId\":2,\"checkInStatus\":\"1\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:11:33', '29');
INSERT INTO `sys_oper_log` VALUES ('124', '生成订单', '1', 'com.ruoyi.web.controller.OrderInfoController.generateOrders()', 'POST', '1', 'admin', '研发部门', '/order/info/generate/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:11:40', '34');
INSERT INTO `sys_oper_log` VALUES ('125', '老人入住申请', '1', 'com.ruoyi.web.controller.ElderCheckInController.add()', 'POST', '1', 'admin', '研发部门', '/elder/checkin', '127.0.0.1', '内网IP', '{\"applyDate\":\"2025-10-29 00:24:11\",\"careLevel\":\"1\",\"checkInId\":3,\"checkInStatus\":\"0\",\"depositAmount\":15000,\"elderId\":1,\"expectedCheckInDate\":\"2025-10-29\",\"institutionId\":3,\"monthlyFee\":10000,\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:24:12', '42');
INSERT INTO `sys_oper_log` VALUES ('126', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":9,\"bedNumber\":\"8\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-10-29 00:25:37\",\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":3,\"params\":{},\"price\":1000,\"roomNumber\":\"302\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:25:37', '13');
INSERT INTO `sys_oper_log` VALUES ('127', '老人入住申请', '2', 'com.ruoyi.web.controller.ElderCheckInController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin', '127.0.0.1', '内网IP', '{\"applyDate\":\"2025-10-29 00:24:12\",\"bedId\":9,\"careLevel\":\"1\",\"checkInId\":3,\"checkInStatus\":\"0\",\"createBy\":\"\",\"depositAmount\":15000,\"elderId\":1,\"elderName\":\"张三\",\"expectedCheckInDate\":\"2025-10-29\",\"institutionId\":3,\"institutionName\":\"123123\",\"monthlyFee\":10000,\"params\":{},\"updateBy\":\"\",\"updateTime\":\"2025-10-29 00:25:46\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:25:46', '38');
INSERT INTO `sys_oper_log` VALUES ('128', '老人入住申请审批', '2', 'com.ruoyi.web.controller.ElderCheckInController.approve()', 'PUT', '1', 'admin', '研发部门', '/elder/checkin/approve', '127.0.0.1', '内网IP', '{\"approvalRemark\":\"00\",\"checkInId\":3,\"checkInStatus\":\"2\",\"params\":{}}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-29 00:25:54', '40');
INSERT INTO `sys_oper_log` VALUES ('129', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"bigscreen/institution-distribution\",\"createTime\":\"2025-10-29 22:21:46\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4001,\"menuName\":\"机构分布大屏\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":4000,\"path\":\"http://localhost:8080/screen/institution-distribution.html\",\"perms\":\"bigscreen:institution:view\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-30 01:15:20', '26');
INSERT INTO `sys_oper_log` VALUES ('130', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"bigscreen/fund-supervision\",\"createTime\":\"2025-10-30 00:41:01\",\"icon\":\"money\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4002,\"menuName\":\"资金监管大屏\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":4000,\"path\":\"http://localhost:8080/screen/fund-supervision.html\",\"perms\":\"bigscreen:fund:view\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-30 01:15:59', '23');
INSERT INTO `sys_oper_log` VALUES ('131', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"bigscreen/warning-monitor\",\"createTime\":\"2025-10-30 01:20:52\",\"icon\":\"user\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4003,\"menuName\":\"预警监控大屏\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":4000,\"path\":\"http://localhost:8080/screen/warning-monitor.html\",\"perms\":\"bigscreen:warning:view\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-10-30 01:48:48', '51');

-- ----------------------------
-- Table structure for sys_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_post`;
CREATE TABLE `sys_post` (
  `post_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
  `post_code` varchar(64) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位编码',
  `post_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '岗位名称',
  `post_sort` int(4) NOT NULL COMMENT '显示顺序',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '状态（0正常 1停用）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`post_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('1', 'ceo', '董事长', '1', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('2', 'se', '项目经理', '2', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('3', 'hr', '人力资源', '3', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('4', 'user', '普通员工', '4', '0', 'admin', '2025-10-28 02:47:08', '', null, '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role` (
  `role_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色名称',
  `role_key` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色权限字符串',
  `role_sort` int(4) NOT NULL COMMENT '显示顺序',
  `data_scope` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
  `menu_check_strictly` tinyint(1) DEFAULT '1' COMMENT '菜单树选择项是否关联显示',
  `dept_check_strictly` tinyint(1) DEFAULT '1' COMMENT '部门树选择项是否关联显示',
  `status` char(1) COLLATE utf8mb4_general_ci NOT NULL COMMENT '角色状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'admin', '1', '1', '1', '1', '0', '0', 'admin', '2025-10-28 02:47:08', '', null, '超级管理员');
INSERT INTO `sys_role` VALUES ('2', '普通角色', 'common', '2', '2', '1', '1', '0', '0', 'admin', '2025-10-28 02:47:08', '', null, '普通角色');
INSERT INTO `sys_role` VALUES ('3', '民政监管员', 'supervision', '3', '2', '1', '1', '0', '0', 'admin', '2025-10-29 04:49:09', '', null, '民政监管部门角色');

-- ----------------------------
-- Table structure for sys_role_dept
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_dept`;
CREATE TABLE `sys_role_dept` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `dept_id` bigint(20) NOT NULL COMMENT '部门ID',
  PRIMARY KEY (`role_id`,`dept_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和部门关联表';

-- ----------------------------
-- Records of sys_role_dept
-- ----------------------------
INSERT INTO `sys_role_dept` VALUES ('2', '100');
INSERT INTO `sys_role_dept` VALUES ('2', '101');
INSERT INTO `sys_role_dept` VALUES ('2', '105');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu` (
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  `menu_id` bigint(20) NOT NULL COMMENT '菜单ID',
  PRIMARY KEY (`role_id`,`menu_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色和菜单关联表';

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1', '2000');
INSERT INTO `sys_role_menu` VALUES ('1', '2001');
INSERT INTO `sys_role_menu` VALUES ('1', '2002');
INSERT INTO `sys_role_menu` VALUES ('1', '2003');
INSERT INTO `sys_role_menu` VALUES ('1', '2004');
INSERT INTO `sys_role_menu` VALUES ('1', '2005');
INSERT INTO `sys_role_menu` VALUES ('1', '2006');
INSERT INTO `sys_role_menu` VALUES ('1', '2100');
INSERT INTO `sys_role_menu` VALUES ('1', '2101');
INSERT INTO `sys_role_menu` VALUES ('1', '2102');
INSERT INTO `sys_role_menu` VALUES ('1', '2103');
INSERT INTO `sys_role_menu` VALUES ('1', '2111');
INSERT INTO `sys_role_menu` VALUES ('1', '2112');
INSERT INTO `sys_role_menu` VALUES ('1', '2113');
INSERT INTO `sys_role_menu` VALUES ('1', '2114');
INSERT INTO `sys_role_menu` VALUES ('1', '2115');
INSERT INTO `sys_role_menu` VALUES ('1', '2121');
INSERT INTO `sys_role_menu` VALUES ('1', '2122');
INSERT INTO `sys_role_menu` VALUES ('1', '2123');
INSERT INTO `sys_role_menu` VALUES ('1', '2124');
INSERT INTO `sys_role_menu` VALUES ('1', '2125');
INSERT INTO `sys_role_menu` VALUES ('1', '2131');
INSERT INTO `sys_role_menu` VALUES ('1', '2132');
INSERT INTO `sys_role_menu` VALUES ('1', '2133');
INSERT INTO `sys_role_menu` VALUES ('1', '2134');
INSERT INTO `sys_role_menu` VALUES ('1', '2135');
INSERT INTO `sys_role_menu` VALUES ('1', '2136');
INSERT INTO `sys_role_menu` VALUES ('1', '2137');
INSERT INTO `sys_role_menu` VALUES ('1', '2138');
INSERT INTO `sys_role_menu` VALUES ('1', '2139');
INSERT INTO `sys_role_menu` VALUES ('1', '2140');
INSERT INTO `sys_role_menu` VALUES ('1', '2141');
INSERT INTO `sys_role_menu` VALUES ('1', '2142');
INSERT INTO `sys_role_menu` VALUES ('1', '2143');
INSERT INTO `sys_role_menu` VALUES ('1', '2144');
INSERT INTO `sys_role_menu` VALUES ('1', '2145');
INSERT INTO `sys_role_menu` VALUES ('1', '2146');
INSERT INTO `sys_role_menu` VALUES ('1', '2147');
INSERT INTO `sys_role_menu` VALUES ('1', '2148');
INSERT INTO `sys_role_menu` VALUES ('1', '2149');
INSERT INTO `sys_role_menu` VALUES ('1', '2150');
INSERT INTO `sys_role_menu` VALUES ('1', '2151');
INSERT INTO `sys_role_menu` VALUES ('1', '2152');
INSERT INTO `sys_role_menu` VALUES ('1', '2153');
INSERT INTO `sys_role_menu` VALUES ('1', '2154');
INSERT INTO `sys_role_menu` VALUES ('1', '2155');
INSERT INTO `sys_role_menu` VALUES ('1', '2156');
INSERT INTO `sys_role_menu` VALUES ('1', '2157');
INSERT INTO `sys_role_menu` VALUES ('1', '2158');
INSERT INTO `sys_role_menu` VALUES ('1', '2159');
INSERT INTO `sys_role_menu` VALUES ('2', '1');
INSERT INTO `sys_role_menu` VALUES ('2', '2');
INSERT INTO `sys_role_menu` VALUES ('2', '3');
INSERT INTO `sys_role_menu` VALUES ('2', '4');
INSERT INTO `sys_role_menu` VALUES ('2', '100');
INSERT INTO `sys_role_menu` VALUES ('2', '101');
INSERT INTO `sys_role_menu` VALUES ('2', '102');
INSERT INTO `sys_role_menu` VALUES ('2', '103');
INSERT INTO `sys_role_menu` VALUES ('2', '104');
INSERT INTO `sys_role_menu` VALUES ('2', '105');
INSERT INTO `sys_role_menu` VALUES ('2', '106');
INSERT INTO `sys_role_menu` VALUES ('2', '107');
INSERT INTO `sys_role_menu` VALUES ('2', '108');
INSERT INTO `sys_role_menu` VALUES ('2', '109');
INSERT INTO `sys_role_menu` VALUES ('2', '110');
INSERT INTO `sys_role_menu` VALUES ('2', '111');
INSERT INTO `sys_role_menu` VALUES ('2', '112');
INSERT INTO `sys_role_menu` VALUES ('2', '113');
INSERT INTO `sys_role_menu` VALUES ('2', '114');
INSERT INTO `sys_role_menu` VALUES ('2', '115');
INSERT INTO `sys_role_menu` VALUES ('2', '116');
INSERT INTO `sys_role_menu` VALUES ('2', '117');
INSERT INTO `sys_role_menu` VALUES ('2', '500');
INSERT INTO `sys_role_menu` VALUES ('2', '501');
INSERT INTO `sys_role_menu` VALUES ('2', '1000');
INSERT INTO `sys_role_menu` VALUES ('2', '1001');
INSERT INTO `sys_role_menu` VALUES ('2', '1002');
INSERT INTO `sys_role_menu` VALUES ('2', '1003');
INSERT INTO `sys_role_menu` VALUES ('2', '1004');
INSERT INTO `sys_role_menu` VALUES ('2', '1005');
INSERT INTO `sys_role_menu` VALUES ('2', '1006');
INSERT INTO `sys_role_menu` VALUES ('2', '1007');
INSERT INTO `sys_role_menu` VALUES ('2', '1008');
INSERT INTO `sys_role_menu` VALUES ('2', '1009');
INSERT INTO `sys_role_menu` VALUES ('2', '1010');
INSERT INTO `sys_role_menu` VALUES ('2', '1011');
INSERT INTO `sys_role_menu` VALUES ('2', '1012');
INSERT INTO `sys_role_menu` VALUES ('2', '1013');
INSERT INTO `sys_role_menu` VALUES ('2', '1014');
INSERT INTO `sys_role_menu` VALUES ('2', '1015');
INSERT INTO `sys_role_menu` VALUES ('2', '1016');
INSERT INTO `sys_role_menu` VALUES ('2', '1017');
INSERT INTO `sys_role_menu` VALUES ('2', '1018');
INSERT INTO `sys_role_menu` VALUES ('2', '1019');
INSERT INTO `sys_role_menu` VALUES ('2', '1020');
INSERT INTO `sys_role_menu` VALUES ('2', '1021');
INSERT INTO `sys_role_menu` VALUES ('2', '1022');
INSERT INTO `sys_role_menu` VALUES ('2', '1023');
INSERT INTO `sys_role_menu` VALUES ('2', '1024');
INSERT INTO `sys_role_menu` VALUES ('2', '1025');
INSERT INTO `sys_role_menu` VALUES ('2', '1026');
INSERT INTO `sys_role_menu` VALUES ('2', '1027');
INSERT INTO `sys_role_menu` VALUES ('2', '1028');
INSERT INTO `sys_role_menu` VALUES ('2', '1029');
INSERT INTO `sys_role_menu` VALUES ('2', '1030');
INSERT INTO `sys_role_menu` VALUES ('2', '1031');
INSERT INTO `sys_role_menu` VALUES ('2', '1032');
INSERT INTO `sys_role_menu` VALUES ('2', '1033');
INSERT INTO `sys_role_menu` VALUES ('2', '1034');
INSERT INTO `sys_role_menu` VALUES ('2', '1035');
INSERT INTO `sys_role_menu` VALUES ('2', '1036');
INSERT INTO `sys_role_menu` VALUES ('2', '1037');
INSERT INTO `sys_role_menu` VALUES ('2', '1038');
INSERT INTO `sys_role_menu` VALUES ('2', '1039');
INSERT INTO `sys_role_menu` VALUES ('2', '1040');
INSERT INTO `sys_role_menu` VALUES ('2', '1041');
INSERT INTO `sys_role_menu` VALUES ('2', '1042');
INSERT INTO `sys_role_menu` VALUES ('2', '1043');
INSERT INTO `sys_role_menu` VALUES ('2', '1044');
INSERT INTO `sys_role_menu` VALUES ('2', '1045');
INSERT INTO `sys_role_menu` VALUES ('2', '1046');
INSERT INTO `sys_role_menu` VALUES ('2', '1047');
INSERT INTO `sys_role_menu` VALUES ('2', '1048');
INSERT INTO `sys_role_menu` VALUES ('2', '1049');
INSERT INTO `sys_role_menu` VALUES ('2', '1050');
INSERT INTO `sys_role_menu` VALUES ('2', '1051');
INSERT INTO `sys_role_menu` VALUES ('2', '1052');
INSERT INTO `sys_role_menu` VALUES ('2', '1053');
INSERT INTO `sys_role_menu` VALUES ('2', '1054');
INSERT INTO `sys_role_menu` VALUES ('2', '1055');
INSERT INTO `sys_role_menu` VALUES ('2', '1056');
INSERT INTO `sys_role_menu` VALUES ('2', '1057');
INSERT INTO `sys_role_menu` VALUES ('2', '1058');
INSERT INTO `sys_role_menu` VALUES ('2', '1059');
INSERT INTO `sys_role_menu` VALUES ('2', '1060');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `user_type` varchar(2) COLLATE utf8mb4_general_ci DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `email` varchar(50) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '用户邮箱',
  `phonenumber` varchar(11) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '手机号码',
  `sex` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '用户性别（0男 1女 2未知）',
  `avatar` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '头像地址',
  `password` varchar(100) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '密码',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '账号状态（0正常 1停用）',
  `del_flag` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '删除标志（0代表存在 2代表删除）',
  `login_ip` varchar(128) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '最后登录IP',
  `login_date` datetime DEFAULT NULL COMMENT '最后登录时间',
  `pwd_update_date` datetime DEFAULT NULL COMMENT '密码最后更新时间',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '103', 'admin', '若依', '00', 'ry@163.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-10-30 02:48:37', '2025-10-28 02:47:08', 'admin', '2025-10-28 02:47:08', '', null, '管理员');
INSERT INTO `sys_user` VALUES ('2', '105', 'ry', '若依', '00', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2025-10-28 02:47:08', '2025-10-28 02:47:08', 'admin', '2025-10-28 02:47:08', '', null, '测试员');
INSERT INTO `sys_user` VALUES ('100', '103', 'supervision', '民政监管员', '00', 'supervision@qq.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TP57rDdaTpe', '0', '0', '127.0.0.1', '2025-10-29 04:49:09', null, 'admin', '2025-10-29 04:49:09', '', null, '民政监管测试账号，密码：admin123');

-- ----------------------------
-- Table structure for sys_user_post
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_post`;
CREATE TABLE `sys_user_post` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `post_id` bigint(20) NOT NULL COMMENT '岗位ID',
  PRIMARY KEY (`user_id`,`post_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户与岗位关联表';

-- ----------------------------
-- Records of sys_user_post
-- ----------------------------
INSERT INTO `sys_user_post` VALUES ('1', '1');
INSERT INTO `sys_user_post` VALUES ('2', '2');

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role` (
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `role_id` bigint(20) NOT NULL COMMENT '角色ID',
  PRIMARY KEY (`user_id`,`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户和角色关联表';

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1', '1');
INSERT INTO `sys_user_role` VALUES ('2', '2');
INSERT INTO `sys_user_role` VALUES ('100', '3');

-- ----------------------------
-- Table structure for transaction_record
-- ----------------------------
DROP TABLE IF EXISTS `transaction_record`;
CREATE TABLE `transaction_record` (
  `transaction_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '浜ゆ槗ID',
  `account_id` bigint(20) NOT NULL COMMENT '璐︽埛ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `transaction_no` varchar(50) NOT NULL COMMENT '浜ゆ槗娴佹按鍙',
  `transaction_type` char(1) NOT NULL COMMENT '浜ゆ槗绫诲瀷(1鍏ヨ处 2鍑鸿处)',
  `business_type` char(1) NOT NULL COMMENT '涓氬姟绫诲瀷(1缂磋垂 2鏈堝害鎵ｈ垂 3鎶奸噾浣跨敤 4閫?垂)',
  `amount` decimal(12,2) NOT NULL COMMENT '浜ゆ槗閲戦?',
  `balance_before` decimal(12,2) NOT NULL COMMENT '浜ゆ槗鍓嶄綑棰',
  `balance_after` decimal(12,2) NOT NULL COMMENT '浜ゆ槗鍚庝綑棰',
  `service_balance` decimal(12,2) DEFAULT '0.00' COMMENT '鏈嶅姟璐逛綑棰',
  `deposit_balance` decimal(12,2) DEFAULT '0.00' COMMENT '鎶奸噾浣欓?',
  `member_balance` decimal(12,2) DEFAULT '0.00' COMMENT '浼氬憳璐逛綑棰',
  `transaction_date` datetime NOT NULL COMMENT '浜ゆ槗鏃堕棿',
  `related_order_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈璁㈠崟ID',
  `related_transfer_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈鍒掓嫧ID',
  `counterparty` varchar(100) DEFAULT NULL COMMENT '浜ゆ槗瀵规墜',
  `business_desc` varchar(500) NOT NULL COMMENT '涓氬姟鎻忚堪',
  `operator` varchar(64) DEFAULT NULL COMMENT '鎿嶄綔浜',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`transaction_id`),
  UNIQUE KEY `uk_transaction_no` (`transaction_no`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_transaction_date` (`transaction_date`),
  KEY `idx_business_type` (`business_type`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='浜ゆ槗娴佹按璁板綍琛';

-- ----------------------------
-- Records of transaction_record
-- ----------------------------
INSERT INTO `transaction_record` VALUES ('1', '1', '1', '1', 'TRX001', '1', '1', '5000.00', '20000.00', '25000.00', '0.00', '0.00', '0.00', '2024-01-15 10:00:00', null, null, null, 'Family Recharge', 'Zhang Son', null, 'Recharge 5000');
INSERT INTO `transaction_record` VALUES ('2', '2', '2', '1', 'TRX002', '4', '1', '2800.00', '16500.00', '13700.00', '0.00', '0.00', '0.00', '2024-02-05 10:15:00', null, null, null, 'Monthly Fee Deduction', 'System Auto', null, 'Feb Fee 2800');
INSERT INTO `transaction_record` VALUES ('3', '3', '3', '1', 'TRX003', '1', '1', '8000.00', '12500.00', '20500.00', '0.00', '0.00', '0.00', '2024-01-25 16:45:00', null, null, null, 'Family Recharge', 'Wang Grandson', null, 'Recharge 8000');
