/*
Navicat MySQL Data Transfer

Source Server         : localhost
Source Server Version : 80015
Source Host           : localhost:3306
Source Database       : newzijin

Target Server Type    : MYSQL
Target Server Version : 80015
File Encoding         : 65001

Date: 2026-01-25 23:49:44
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璐︽埛浣欓?鍙樺姩璁板綍琛';

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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鑰佷汉璐︽埛淇℃伅琛';

-- ----------------------------
-- Records of account_info
-- ----------------------------
INSERT INTO `account_info` VALUES ('26', '41', '30', 'ACC1769353226687066', '账户-41', '1', '0.00', '0.00', '0.00', '0.00', '', '2026-01-25 23:00:27', '', null, '老人入住时自动创建');
INSERT INTO `account_info` VALUES ('27', '42', '30', 'ACC1769354296361306', '账户-42', '1', '12100.00', '9100.00', '2000.00', '1000.00', '', '2026-01-25 23:18:16', '', '2026-01-25 23:34:24', '老人入住时自动创建 | 支付订单ORD1769354296366增加余额11400.00元 | 支付订单ORD1769354699641增加余额1400.00元');

-- ----------------------------
-- Table structure for area_street
-- ----------------------------
DROP TABLE IF EXISTS `area_street`;
CREATE TABLE `area_street` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `area_code` varchar(12) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区县代码',
  `area_name` varchar(50) COLLATE utf8mb4_general_ci NOT NULL COMMENT '区县名称',
  `street_code` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '街道代码(可选)',
  `street_name` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '街道名称',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态(0正常 1停用)',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_area_street` (`area_code`,`street_name`),
  KEY `idx_area_code` (`area_code`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=200 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='区域街道信息表';

-- ----------------------------
-- Records of area_street
-- ----------------------------
INSERT INTO `area_street` VALUES ('1', '410102', '中原区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('2', '410102', '中原区', '41010285', '航海西路街道办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('3', '410102', '中原区', '41010210', '三官庙街道办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('4', '410102', '中原区', '41010220', '林山寨街道办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('5', '410102', '中原区', '41010286', '汝河路街道办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('6', '410102', '中原区', '41010247', '秦岭路街道办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('7', '410102', '中原区', '41010289', '莲湖街道办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('8', '410102', '中原区', '41010251', '柳湖街道办事处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('9', '410102', '中原区', '41010203', '棉纺路街道办事处', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('10', '410102', '中原区', '41010216', '建设路街道办事处', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('11', '410102', '中原区', '41010229', '桐柏路街道办事处', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('12', '410102', '中原区', '41010298', '西流湖街道办事处', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('13', '410102', '中原区', '41010263', '须水街道办事处', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('14', '410102', '中原区', '41010254', '中原西路街道办事处', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('15', '410102', '中原区', '41010293', '绿东村街道办事处', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('16', '410105', '金水区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('17', '410105', '金水区', '41010522', '兴达路街道办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('18', '410105', '金水区', '41010532', '杨金路街道办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('19', '410105', '金水区', '41010507', '北林路街道办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('20', '410105', '金水区', '41010583', '丰庆路街道办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('21', '410105', '金水区', '41010512', '国基路街道办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('22', '410105', '金水区', '41010550', '东风路街道办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('23', '410105', '金水区', '41010550', '花园路街道办事处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('24', '410105', '金水区', '41010543', '文化路街道办事处', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('25', '410105', '金水区', '41010591', '丰产路街道办事处', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('26', '410105', '金水区', '41010550', '南阳新村街道办事处', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('27', '410105', '金水区', '41010570', '南阳路街道办事处', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('28', '410105', '金水区', '41010538', '大石桥街道办事处', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('29', '410105', '金水区', '41010539', '经八路街道办事处', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('30', '410105', '金水区', '41010553', '人民路街道办事处', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('31', '410105', '金水区', '41010522', '杜岭街道办事处', '15', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('32', '410105', '金水区', '41010559', '未来路街道办事处', '16', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('33', '410105', '金水区', '41010569', '凤凰台街道办事处', '17', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('34', '410103', '二七区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('35', '410103', '二七区', '41010327', '大学路办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('36', '410103', '二七区', '41010323', '福华街办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('37', '410103', '二七区', '41010342', '淮河路街道办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('38', '410103', '二七区', '41010324', '建中街街道办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('39', '410103', '二七区', '41010381', '京广路办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('40', '410103', '二七区', '41010370', '蜜蜂张办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('41', '410103', '二七区', '41010328', '铭功路办事处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('42', '410103', '二七区', '41010311', '人和路办事处', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('43', '410103', '二七区', '41010338', '嵩山路办事处', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('44', '410103', '二七区', '41010366', '五里堡街道办事处', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('45', '410103', '二七区', '41010335', '一马路办事处', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('46', '410103', '二七区', '41010306', '长江路办事处', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('47', '410103', '二七区', '41010365', '侯寨街道办事处', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('48', '410103', '二七区', '41010363', '金水源办事处', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('49', '410103', '二七区', '41010334', '德化街办事处', '15', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('50', '410166', '郑东新区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('51', '410166', '郑东新区', '41016658', '祭城路街道办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('52', '410166', '郑东新区', '41016652', '龙子湖街道办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('53', '410166', '郑东新区', '41016692', '如意湖办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('54', '410166', '郑东新区', '41016685', '博学路办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('55', '410166', '郑东新区', '41016673', '商都路办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('56', '410166', '郑东新区', '41016631', '龙源路办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('57', '410166', '郑东新区', '41016600', '龙湖办事处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('58', '410166', '郑东新区', '41016686', '金光路办事处', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('59', '410166', '郑东新区', '41016687', '豫兴路办事处', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('60', '410166', '郑东新区', '41016682', '杨桥办事处', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('61', '410166', '郑东新区', '41016631', '白沙镇人民政府', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('62', '410166', '郑东新区', '41016689', '圃田乡人民政府', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('63', '410171', '高新区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('64', '410171', '高新区', '41017123', '石佛办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('65', '410171', '高新区', '41017162', '沟赵办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('66', '410171', '高新区', '41017115', '枫杨办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('67', '410171', '高新区', '41017156', '梧桐办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('68', '410171', '高新区', '41017166', '双桥办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('69', '410108', '惠济区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('70', '410108', '惠济区', '41010840', '大河路街道办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('71', '410108', '惠济区', '41010805', '刘寨街道办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('72', '410108', '惠济区', '41010823', '长兴路街道办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('73', '410108', '惠济区', '41010824', '迎宾路街道办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('74', '410108', '惠济区', '41010836', '江山路街道办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('75', '410108', '惠济区', '41010855', '新城路街道办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('76', '410108', '惠济区', '41010893', '古荥镇人民政府', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('77', '410108', '惠济区', '41010861', '花园口镇人民政府', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('78', '410104', '管城区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('79', '410104', '管城区', '41010441', '西大街办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('80', '410104', '管城区', '41010435', '北下街办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('81', '410104', '管城区', '41010498', '东大街办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('82', '410104', '管城区', '41010421', '南关办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('83', '410104', '管城区', '41010487', '城东路办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('84', '410104', '管城区', '41010464', '二里岗办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('85', '410104', '管城区', '41010454', '陇海办事处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('86', '410104', '管城区', '41010490', '紫南办事处', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('87', '410104', '管城区', '41010472', '航东办事处', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('88', '410104', '管城区', '41010419', '南曹办事处', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('89', '410104', '管城区', '41010472', '十八里河办事处', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('90', '410104', '管城区', '41010439', '金岱办事处', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('91', '410173', '经开区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('92', '410173', '经开区', '41017342', '明湖办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('93', '410173', '经开区', '41017358', '潮河办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('94', '410173', '经开区', '41017395', '京航办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('95', '410173', '经开区', '41017332', '前程办事处', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('96', '410173', '经开区', '41017381', '九龙办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('97', '410106', '上街区', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('98', '410106', '上街区', '41010664', '中心路街道办事处', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('99', '410106', '上街区', '41010678', '济源路街道办事处', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('100', '410106', '上街区', '41010689', '工业路街道办事处', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('101', '410106', '上街区', '41010679', '峡窝镇人民政府', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('102', '410106', '上街区', '41010615', '矿山街道办事处', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('103', '410106', '上街区', '41010622', '新安路街道办事处', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('104', '410122', '中牟县', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('105', '410122', '中牟县', '41012270', '大孟街道', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('106', '410122', '中牟县', '41012243', '青年路街道', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('107', '410122', '中牟县', '41012287', '东风路街道', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('108', '410122', '中牟县', '41012223', '广惠街街道', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('109', '410122', '中牟县', '41012204', '万洪路区域发展中心', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('110', '410122', '中牟县', '41012234', '韩寺镇', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('111', '410122', '中牟县', '41012252', '官渡镇', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('112', '410122', '中牟县', '41012277', '狼城岗镇', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('113', '410122', '中牟县', '41012271', '雁鸣湖镇', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('114', '410122', '中牟县', '41012236', '万滩镇', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('115', '410122', '中牟县', '41012224', '刘集镇', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('116', '410122', '中牟县', '41012266', '郑庵镇', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('117', '410122', '中牟县', '41012259', '刁家乡', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('118', '410122', '中牟县', '41012253', '姚家镇', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('119', '410181', '巩义市', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('120', '410181', '巩义市', '41018175', '新华路街道', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('121', '410181', '巩义市', '41018138', '杜甫路街道', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('122', '410181', '巩义市', '41018104', '永安路街道', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('123', '410181', '巩义市', '41018183', '孝义街道', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('124', '410181', '巩义市', '41018171', '紫荆路街道', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('125', '410181', '巩义市', '41018148', '米河镇', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('126', '410181', '巩义市', '41018194', '新中镇', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('127', '410181', '巩义市', '41018116', '小关镇', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('128', '410181', '巩义市', '41018146', '竹林镇', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('129', '410181', '巩义市', '41018131', '大峪沟镇', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('130', '410181', '巩义市', '41018129', '河洛镇', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('131', '410181', '巩义市', '41018136', '站街镇', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('132', '410181', '巩义市', '41018175', '康店镇', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('133', '410181', '巩义市', '41018189', '北山口镇', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('134', '410181', '巩义市', '41018129', '西村镇', '15', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('135', '410181', '巩义市', '41018129', '芝田镇', '16', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('136', '410181', '巩义市', '41018151', '回郭镇', '17', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('137', '410181', '巩义市', '41018149', '鲁庄镇', '18', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('138', '410181', '巩义市', '41018111', '夹津口镇', '19', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('139', '410181', '巩义市', '41018149', '涉村镇', '20', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('140', '410182', '荥阳市', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('141', '410182', '荥阳市', '41018257', '京城路街道', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('142', '410182', '荥阳市', '41018282', '索河街道', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('143', '410182', '荥阳市', '41018204', '豫龙镇政府', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('144', '410182', '荥阳市', '41018216', '贾峪镇政府', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('145', '410182', '荥阳市', '41018264', '广武镇政府', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('146', '410182', '荥阳市', '41018274', '乔楼镇政府', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('147', '410182', '荥阳市', '41018252', '科学城筹备处管理处', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('148', '410182', '荥阳市', '41018205', '崔庙镇', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('149', '410183', '新密市', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('150', '410183', '新密市', '41018309', '米村镇', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('151', '410183', '新密市', '41018318', '袁庄乡', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('152', '410183', '新密市', '41018312', '牛店镇', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('153', '410183', '新密市', '41018312', '平陌镇', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('154', '410183', '新密市', '41018308', '超化镇', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('155', '410183', '新密市', '41018302', '大隗镇', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('156', '410183', '新密市', '41018345', '苟堂镇', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('157', '410183', '新密市', '41018314', '刘寨镇', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('158', '410183', '新密市', '41018329', '曲梁镇', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('159', '410183', '新密市', '41018367', '白寨镇', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('160', '410183', '新密市', '41018326', '岳村镇', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('161', '410183', '新密市', '41018398', '来集镇', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('162', '410183', '新密市', '41018399', '城关镇', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('163', '410183', '新密市', '41018390', '新密西大街办事处', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('164', '410183', '新密市', '41018397', '青屏街办事处', '15', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('165', '410183', '新密市', '41018395', '新华路办事处', '16', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('166', '410183', '新密市', '41018385', '矿区办事处', '17', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('167', '410183', '新密市', '41018321', '伏羲山管委会', '18', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('168', '410184', '新郑市', null, null, '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('169', '410184', '新郑市', '41018475', '新建路街道', '1', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('170', '410184', '新郑市', '41018475', '新华路街道', '2', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('171', '410184', '新郑市', '41018486', '新村镇', '3', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('172', '410184', '新郑市', '41018473', '城关乡', '4', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('173', '410184', '新郑市', '41018451', '具茨山国家级森林公园管理委员会', '5', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('174', '410184', '新郑市', '41018492', '和庄镇', '6', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('175', '410184', '新郑市', '41018465', '辛店镇', '7', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('176', '410184', '新郑市', '41018428', '薛店镇', '8', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('177', '410184', '新郑市', '41018411', '龙湖镇', '9', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('178', '410184', '新郑市', '41018416', '孟庄镇', '10', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('179', '410184', '新郑市', '41018468', '郭店镇', '11', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('180', '410184', '新郑市', '41018462', '观音寺镇', '12', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('181', '410184', '新郑市', '41018457', '梨河镇', '13', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('182', '410184', '新郑市', '41018450', '新烟街道', '14', '0', 'system', '2025-12-04 19:38:12', 'system', '2025-12-04 19:38:12', null);
INSERT INTO `area_street` VALUES ('183', '410184', '新郑市', '41018464', '新郑新区管理委员会', '15', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('184', '410185', '登封市', null, null, '1', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('185', '410185', '登封市', '41018555', '嵩阳街道办事处', '1', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('186', '410185', '登封市', '41018503', '中岳街道办事处', '2', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('187', '410185', '登封市', '41018595', '少林街道办事处', '3', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('188', '410185', '登封市', '41018510', '卢店街道办事处', '4', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('189', '410173', '航空港区', null, null, '1', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('190', '410173', '航空港区', '41017303', '银河办事处', '1', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('191', '410173', '航空港区', '41017317', '新港办事处', '2', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('192', '410173', '航空港区', '41017309', '明港办事处', '3', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('193', '410173', '航空港区', '41017316', '龙王办事处', '4', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('194', '410173', '航空港区', '41017334', '郑港办事处', '5', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('195', '410173', '航空港区', '41017340', '滨河办事处', '6', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('196', '410173', '航空港区', '41017354', '三官庙办事处', '7', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('197', '410173', '航空港区', '41017372', '岗李乡', '8', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('198', '410173', '航空港区', '41017384', '龙港办事处', '9', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);
INSERT INTO `area_street` VALUES ('199', '410173', '航空港区', '41017352', '张庄办事处', '10', '0', 'system', '2025-12-04 19:38:13', 'system', '2025-12-04 19:38:13', null);

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浣欓?棰勮?璁板綍琛';

-- ----------------------------
-- Records of balance_warning
-- ----------------------------

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
  `due_date` date DEFAULT NULL COMMENT '到期日期',
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
) ENGINE=InnoDB AUTO_INCREMENT=50 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位分配记录表';

-- ----------------------------
-- Records of bed_allocation
-- ----------------------------
INSERT INTO `bed_allocation` VALUES ('48', '41', '58', '30', '2026-01-25', '2027-01-25', null, '0', '600.00', '0', '500.00', 'jg696562', '2026-01-25 23:00:27', '', null, '');
INSERT INTO `bed_allocation` VALUES ('49', '42', '59', '30', '2026-01-25', '2027-01-25', null, '0', '700.00', '0', '2000.00', 'jg696562', '2026-01-25 23:18:16', '', null, '');

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
  `self_care_price` decimal(10,2) DEFAULT NULL COMMENT '自理护理价格(元/月)',
  `half_care_price` decimal(10,2) DEFAULT NULL COMMENT '半护理价格(元/月)',
  `full_care_price` decimal(10,2) DEFAULT NULL COMMENT '全护理价格(元/月)',
  `member_fee` decimal(10,2) DEFAULT NULL COMMENT '会员费(一次性)',
  `deposit_fee` decimal(10,2) DEFAULT NULL COMMENT '押金(一次性)',
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
) ENGINE=InnoDB AUTO_INCREMENT=60 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='床位信息表';

-- ----------------------------
-- Records of bed_info
-- ----------------------------
INSERT INTO `bed_info` VALUES ('58', '30', '101', '01', '1', '1', '500.00', '200.00', '500.00', '1000.00', '500.00', '500.00', '1', '60.00', 'Y', 'N', '生活照料、医疗护理、康复训练', '', '2026-01-25 22:43:47', '', null, '中医理疗、文化娱乐、营养膳食');
INSERT INTO `bed_info` VALUES ('59', '30', '102', '23', '1', '1', '500.00', '600.00', '1200.00', '2400.00', '1000.00', '2000.00', '1', '60.00', 'Y', 'Y', null, '', '2026-01-25 23:04:14', '', null, null);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='搴婁綅绫诲瀷琛';

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
  `account_id` bigint(20) DEFAULT NULL COMMENT '账户ID',
  `apply_amount` decimal(10,2) NOT NULL COMMENT '申请金额',
  `apply_reason` varchar(500) NOT NULL COMMENT '申请原因',
  `apply_type` varchar(50) DEFAULT NULL COMMENT '申请类型',
  `urgency_level` varchar(20) DEFAULT '一般' COMMENT '紧急程度(一般/紧急/非常紧急)',
  `purpose` varchar(200) DEFAULT NULL COMMENT '使用事由',
  `description` text COMMENT '详细说明',
  `expected_use_date` date DEFAULT NULL COMMENT '期望使用日期',
  `attachments` text COMMENT '申请材料附件(JSON格式存储文件路径)',
  `apply_status` varchar(30) DEFAULT 'draft' COMMENT '申请状态(draft-草稿, pending_family-待家属审批, family_approved-家属已审批, pending_supervision-待监管审批, approved-已通过, rejected-已驳回, withdrawn-已撤回)',
  `family_confirm_name` varchar(50) DEFAULT NULL COMMENT '家属确认人姓名',
  `family_relation` varchar(20) DEFAULT NULL COMMENT '家属与老人关系',
  `family_phone` varchar(20) DEFAULT NULL COMMENT '家属联系电话',
  `family_approve_time` datetime DEFAULT NULL COMMENT '家属审批时间',
  `family_approve_opinion` varchar(500) DEFAULT NULL COMMENT '家属审批意见',
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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='押金使用申请表';

-- ----------------------------
-- Records of deposit_apply
-- ----------------------------

-- ----------------------------
-- Table structure for elder_attachment
-- ----------------------------
DROP TABLE IF EXISTS `elder_attachment`;
CREATE TABLE `elder_attachment` (
  `attachment_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '附件ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID',
  `attachment_type` char(1) NOT NULL COMMENT '附件类型(1身份证正面 2身份证反面)',
  `file_path` varchar(500) NOT NULL COMMENT '文件路径',
  `file_name` varchar(200) DEFAULT NULL COMMENT '文件名',
  `file_size` bigint(20) DEFAULT NULL COMMENT '文件大小',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`attachment_id`),
  KEY `idx_elder_id` (`elder_id`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='老人附件表';

-- ----------------------------
-- Records of elder_attachment
-- ----------------------------

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='老人入住申请表';

-- ----------------------------
-- Records of elder_check_in
-- ----------------------------

-- ----------------------------
-- Table structure for elder_family
-- ----------------------------
DROP TABLE IF EXISTS `elder_family`;
CREATE TABLE `elder_family` (
  `family_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '家属关系ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID(关联sys_user表)',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID(关联elder_info表)',
  `relation_type` char(1) NOT NULL DEFAULT '1' COMMENT '关系类型(1:子女 2:配偶 3:兄弟姐妹 4:其他亲属 5:朋友)',
  `relation_name` varchar(50) DEFAULT NULL COMMENT '关系描述(如:儿子、女儿、配偶等)',
  `is_default` char(1) DEFAULT '0' COMMENT '是否默认老人(0:否 1:是)',
  `is_main_contact` char(1) DEFAULT '0' COMMENT '是否主要联系人(0:否 1:是)',
  `status` char(1) DEFAULT '0' COMMENT '关联状态(0:正常 1:已解除)',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`family_id`),
  UNIQUE KEY `uk_user_elder` (`user_id`,`elder_id`) COMMENT '一个用户对一个老人只能有一条关联记录',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_elder_id` (`elder_id`) COMMENT '老人ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户-老人关联表';

-- ----------------------------
-- Records of elder_family
-- ----------------------------
INSERT INTO `elder_family` VALUES ('74', '120', '41', '1', '儿子', '1', '0', '0', '2026-01-25 23:01:01', '2026-01-25 23:01:00', null);
INSERT INTO `elder_family` VALUES ('75', '120', '42', '1', '女儿', '0', '0', '0', '2026-01-25 23:19:11', '2026-01-25 23:19:11', null);

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
  `password` varchar(100) DEFAULT NULL COMMENT '登录密码(MD5加密)',
  `medical_history` varchar(1000) DEFAULT NULL COMMENT '既往病史',
  `status` char(1) DEFAULT '1' COMMENT '状态(0未入住 1已入住 2已退住)',
  `source_type` char(1) DEFAULT '1' COMMENT '来源类型(1后台管理 2H5用户)',
  `submit_user_id` bigint(20) DEFAULT NULL COMMENT '提交用户ID(关联sys_user表)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`elder_id`),
  UNIQUE KEY `uk_id_card` (`id_card`),
  KEY `idx_submit_user_id` (`submit_user_id`),
  KEY `idx_source_type` (`source_type`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='老人基础信息表';

-- ----------------------------
-- Records of elder_info
-- ----------------------------
INSERT INTO `elder_info` VALUES ('41', '陈菲', '2', '412829196002202356', '1960-02-20', '65', '13656456325', '郑州市金水路65号', '详细', '15645156456', '健康', '1', null, null, null, null, '0', '1', null, 'jg696562', '2026-01-25 23:00:27', '', null, null);
INSERT INTO `elder_info` VALUES ('42', '明明', '2', '412829195202202352', '1952-02-20', '73', '13656235623', '郑州市黄河路', '李玲玲', '13652325412', '健康', '1', '无', null, null, null, '0', '1', null, 'jg696562', '2026-01-25 23:18:16', '', null, null);

-- ----------------------------
-- Table structure for elder_photo
-- ----------------------------
DROP TABLE IF EXISTS `elder_photo`;
CREATE TABLE `elder_photo` (
  `photo_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '照片ID',
  `elder_id` bigint(20) NOT NULL COMMENT '老人ID(关联elder_info表)',
  `photo_type` char(1) NOT NULL COMMENT '照片类型(1老人照片 2身份证正面 3身份证反面)',
  `photo_path` varchar(500) NOT NULL COMMENT '照片存储路径',
  `photo_name` varchar(200) DEFAULT NULL COMMENT '照片原始名称',
  `photo_size` bigint(20) DEFAULT NULL COMMENT '照片大小(字节)',
  `photo_url` varchar(500) DEFAULT NULL COMMENT '照片访问URL',
  `sort_order` int(3) DEFAULT '0' COMMENT '照片排序序号',
  `status` char(1) DEFAULT '1' COMMENT '状态(0禁用 1正常)',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`photo_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_photo_type` (`photo_type`),
  KEY `idx_elder_type` (`elder_id`,`photo_type`),
  CONSTRAINT `fk_elder_photo_elder_info` FOREIGN KEY (`elder_id`) REFERENCES `elder_info` (`elder_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='老人照片表';

-- ----------------------------
-- Records of elder_photo
-- ----------------------------

-- ----------------------------
-- Table structure for expense_record
-- ----------------------------
DROP TABLE IF EXISTS `expense_record`;
CREATE TABLE `expense_record` (
  `record_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璁板綍ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `account_id` bigint(20) DEFAULT NULL COMMENT '璐︽埛ID',
  `expense_type` varchar(20) NOT NULL COMMENT '璐圭敤绫诲瀷(deposit:鎶奸噾,service:鏈嶅姟璐?member:浼氬憳璐?other:鍏朵粬)',
  `transaction_type` varchar(20) NOT NULL COMMENT '浜ゆ槗绫诲瀷(income:鏀跺叆,expense:鏀?嚭)',
  `amount` decimal(10,2) NOT NULL COMMENT '閲戦?',
  `description` varchar(500) DEFAULT NULL COMMENT '鎻忚堪',
  `related_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈ID(濡傝?鍗旾D銆佹娂閲戠敵璇稩D绛?',
  `related_type` varchar(50) DEFAULT NULL COMMENT '鍏宠仈绫诲瀷',
  `balance_before` decimal(10,2) DEFAULT NULL COMMENT '浜ゆ槗鍓嶄綑棰',
  `balance_after` decimal(10,2) DEFAULT NULL COMMENT '浜ゆ槗鍚庝綑棰',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `remark` varchar(500) DEFAULT NULL COMMENT '澶囨敞',
  PRIMARY KEY (`record_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_account_id` (`account_id`),
  KEY `idx_expense_type` (`expense_type`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=75 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璐圭敤璁板綍琛';

-- ----------------------------
-- Records of expense_record
-- ----------------------------
INSERT INTO `expense_record` VALUES ('70', '42', '27', 'deposit', 'income', '2000.00', '订单支付：入驻订单-押金缴纳', '64', 'order_info', '0.00', '11400.00', 'system', '2026-01-25 23:20:20', '', null, null);
INSERT INTO `expense_record` VALUES ('71', '42', '27', 'service', 'income', '8400.00', '订单支付：入驻订单-服务费预存', '64', 'order_info', '0.00', '11400.00', 'system', '2026-01-25 23:20:20', '', null, null);
INSERT INTO `expense_record` VALUES ('72', '42', '27', 'member', 'income', '1000.00', '订单支付：入驻订单-会员费缴纳', '64', 'order_info', '0.00', '11400.00', 'system', '2026-01-25 23:20:20', '', null, null);
INSERT INTO `expense_record` VALUES ('73', '42', '27', 'service', 'expense', '700.00', '订单支付：入驻订单-首月服务费扣除', '64', 'order_info', '11400.00', '10700.00', 'system', '2026-01-25 23:20:20', '', null, null);
INSERT INTO `expense_record` VALUES ('74', '42', '27', 'service', 'income', '1400.00', '订单支付：续费订单-服务费预存', '65', 'order_info', '10700.00', '12100.00', 'system', '2026-01-25 23:34:24', '', null, null);

-- ----------------------------
-- Table structure for facility_icon_config
-- ----------------------------
DROP TABLE IF EXISTS `facility_icon_config`;
CREATE TABLE `facility_icon_config` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '配置ID',
  `facility_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '设施名称',
  `icon_name` varchar(100) COLLATE utf8mb4_general_ci NOT NULL COMMENT '图标名称',
  `facility_type` varchar(20) COLLATE utf8mb4_general_ci NOT NULL COMMENT '设施类型：life-生活设施，medical-医疗设施',
  `sort_order` int(11) DEFAULT '0' COMMENT '排序序号',
  `status` char(1) COLLATE utf8mb4_general_ci DEFAULT '0' COMMENT '状态：0-启用，1-停用',
  `create_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='设施图标配置表';

-- ----------------------------
-- Records of facility_icon_config
-- ----------------------------
INSERT INTO `facility_icon_config` VALUES ('1', '独立卫浴', 'enter', 'life', '1', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('2', '紧急呼叫', 'radio', 'life', '2', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('3', '洗衣服务', 'component', 'life', '3', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('4', '活动室', 'date', 'life', '4', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('5', '图书阅览室', 'education', 'life', '5', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('6', '电视/娱乐设备', 'language', 'life', '6', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('7', '空调设备', 'user', 'life', '7', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('8', '暖气设备', 'tool', 'life', '8', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('9', '无线网络', 'tool', 'life', '9', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('10', '储物柜', 'lock', 'life', '10', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('11', '衣柜', 'question', 'life', '11', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('12', '吧台/茶水间', 'redis', 'life', '12', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('13', '阳台/露台', 'textarea', 'life', '13', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('14', '呼叫系统', 'button', 'life', '14', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('15', '医疗室', 'rate', 'medical', '1', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('16', '康复室', 'select', 'medical', '2', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('17', '理疗室', 'tool', 'medical', '3', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('18', '健康监测', 'chart', 'medical', '4', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('19', '药房', 'shopping', 'medical', '5', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('20', '急救设备', 'time', 'medical', '6', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('21', '专业医生', 'phone', 'medical', '7', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('22', '护士站', 'user', 'medical', '8', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('23', '体检设备', 'monitor', 'medical', '9', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('24', '心电图机', 'chart', 'medical', '10', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('25', '血压监测', 'edit', 'medical', '11', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('26', '输液设备', 'tool', 'medical', '12', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('27', '氧气设备', 'server', 'medical', '13', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('28', '呼吸机', 'server', 'medical', '14', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('29', '除颤仪', 'tool', 'medical', '15', '0', null, null, null, null, null);
INSERT INTO `facility_icon_config` VALUES ('30', '测试设施', 'chart', 'life', '0', '0', null, null, null, null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勯噾鍒掓嫧璁板綍琛';

-- ----------------------------
-- Records of fund_transfer
-- ----------------------------
INSERT INTO `fund_transfer` VALUES ('1', '1', 'TRF1761681617639597', '1', '9000.00', '2025-10-29', '2025-01', '3', '0', null, null, null, null, null, null, 'system', '2025-10-29 04:00:18', '', null, '系统自动生成的月度划拨');
INSERT INTO `fund_transfer` VALUES ('2', '1', 'TRF1765867527350896', '1', '0.00', '2025-12-16', '2025-04', '0', '0', null, null, null, null, null, null, 'system', '2025-12-16 14:45:27', '', null, '系统自动生成的月度划拨');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勯噾鍒掓嫧鏄庣粏琛';

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='璧勯噾鍒掓嫧瑙勫垯閰嶇疆琛';

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
-- Table structure for institution_rating
-- ----------------------------
DROP TABLE IF EXISTS `institution_rating`;
CREATE TABLE `institution_rating` (
  `rating_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评级ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `institution_name` varchar(200) NOT NULL COMMENT '机构名称',
  `credit_code` varchar(50) NOT NULL COMMENT '统一信用代码',
  `rating_level` int(2) NOT NULL COMMENT '评级等级(1-5星)',
  `total_score` decimal(5,1) NOT NULL COMMENT '总分(0-100分)',
  `service_score` decimal(5,1) NOT NULL COMMENT '服务质量得分(0-25分)',
  `facility_score` decimal(5,1) NOT NULL COMMENT '设施环境得分(0-25分)',
  `management_score` decimal(5,1) NOT NULL COMMENT '管理水平得分(0-25分)',
  `safety_score` decimal(5,1) NOT NULL COMMENT '安全卫生得分(0-25分)',
  `rating_date` date NOT NULL COMMENT '评级日期',
  `expire_date` date NOT NULL COMMENT '有效期至',
  `validity_period` int(3) NOT NULL DEFAULT '12' COMMENT '有效期(月)',
  `rating_status` char(1) NOT NULL DEFAULT '1' COMMENT '评级状态(1-有效,0-已过期)',
  `rating_remark` text COMMENT '评级意见',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`rating_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_credit_code` (`credit_code`),
  KEY `idx_rating_status` (`rating_status`),
  CONSTRAINT `fk_rating_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构评级表';

-- ----------------------------
-- Records of institution_rating
-- ----------------------------
INSERT INTO `institution_rating` VALUES ('3', '30', '兴福养老集团郑州花园路园区', '91410100MA46852591', '5', '100.0', '25.0', '25.0', '25.0', '25.0', '2026-01-14', '2027-01-14', '12', '1', null, 'admin', '2026-01-25 22:30:22', '', null, null);

-- ----------------------------
-- Table structure for institution_review
-- ----------------------------
DROP TABLE IF EXISTS `institution_review`;
CREATE TABLE `institution_review` (
  `review_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '璇勪环ID',
  `order_id` varchar(50) NOT NULL COMMENT '鍏宠仈璁㈠崟鍙',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `elder_id` bigint(20) NOT NULL COMMENT '鑰佷汉ID',
  `user_id` bigint(20) NOT NULL COMMENT '璇勪环鐢ㄦ埛ID',
  `environment_rating` int(11) NOT NULL DEFAULT '5' COMMENT '鐜??璇勫垎(1-5鏄?',
  `service_rating` int(11) NOT NULL DEFAULT '5' COMMENT '鏈嶅姟璇勫垎(1-5鏄?',
  `price_rating` int(11) NOT NULL DEFAULT '5' COMMENT '浠锋牸璇勫垎(1-5鏄?',
  `average_rating` decimal(3,2) GENERATED ALWAYS AS ((((`environment_rating` + `service_rating`) + `price_rating`) / 3.0)) STORED COMMENT '骞冲潎璇勫垎',
  `content` text COMMENT '璇勪环鍐呭?',
  `images` json DEFAULT NULL COMMENT '璇勪环鍥剧墖(JSON鏁扮粍)',
  `status` tinyint(4) NOT NULL DEFAULT '0' COMMENT '鐘舵?: 0-寰呭?鏍? 1-宸查?杩? 2-宸叉嫆缁',
  `review_remark` varchar(500) DEFAULT NULL COMMENT '瀹℃牳澶囨敞',
  `review_time` datetime DEFAULT NULL COMMENT '瀹℃牳鏃堕棿',
  `review_by` varchar(50) DEFAULT NULL COMMENT '瀹℃牳浜',
  `feedback_id` bigint(20) DEFAULT NULL COMMENT '鍏宠仈鍙嶉?ID锛堢敤浜庡?鏍革級',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  `update_time` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '鏇存柊鏃堕棿',
  PRIMARY KEY (`review_id`),
  UNIQUE KEY `uk_order_id` (`order_id`) COMMENT '姣忎釜璁㈠崟鍙?兘璇勪环涓??',
  KEY `idx_order_id` (`order_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_elder_id` (`elder_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_status` (`status`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏈烘瀯璇勪环琛';

-- ----------------------------
-- Records of institution_review
-- ----------------------------

-- ----------------------------
-- Table structure for institution_visit_reservation
-- ----------------------------
DROP TABLE IF EXISTS `institution_visit_reservation`;
CREATE TABLE `institution_visit_reservation` (
  `reservation_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '预约ID',
  `reservation_no` varchar(50) NOT NULL COMMENT '预约编号',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `user_id` bigint(20) NOT NULL COMMENT '预约用户ID',
  `visitor_name` varchar(50) NOT NULL COMMENT '参观人姓名',
  `visitor_phone` varchar(20) NOT NULL COMMENT '参观人电话',
  `visit_date` date NOT NULL COMMENT '预计到访日期',
  `visit_time` varchar(10) NOT NULL COMMENT '预计到访时间',
  `visitor_count` int(3) NOT NULL DEFAULT '1' COMMENT '参观人数',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注说明',
  `status` char(1) NOT NULL DEFAULT '0' COMMENT '状态(0-待参观 1-已完成 2-已取消)',
  `handle_user` varchar(64) DEFAULT NULL COMMENT '处理人',
  `handle_time` datetime DEFAULT NULL COMMENT '处理时间',
  `handle_remark` varchar(500) DEFAULT NULL COMMENT '处理备注',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`reservation_id`),
  UNIQUE KEY `uk_reservation_no` (`reservation_no`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_visit_date` (`visit_date`),
  KEY `idx_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构预约参观表';

-- ----------------------------
-- Records of institution_visit_reservation
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
  `creator_user_id` bigint(20) DEFAULT NULL COMMENT '下单人ID',
  `institution_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
  `check_in_id` bigint(20) DEFAULT NULL COMMENT '入住申请ID',
  `bed_id` bigint(20) DEFAULT NULL COMMENT '床位ID',
  `order_amount` decimal(10,2) NOT NULL COMMENT '订单总金额(元)',
  `original_amount` decimal(10,2) DEFAULT NULL COMMENT '搴旀敹鎬昏?(浼樻儬鍓?',
  `discount_amount` decimal(10,2) DEFAULT '0.00' COMMENT '浼樻儬閲戦?',
  `paid_amount` decimal(10,2) DEFAULT '0.00' COMMENT '已付金额(元)',
  `order_status` char(1) DEFAULT '0' COMMENT '订单状态(0待支付 1已支付 2已取消 3已退款)',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `payment_time` datetime DEFAULT NULL COMMENT '支付时间',
  `order_date` date NOT NULL COMMENT '订单日期',
  `service_start_date` date DEFAULT NULL COMMENT '服务开始日期',
  `service_end_date` date DEFAULT NULL COMMENT '服务结束日期',
  `billing_cycle` varchar(20) DEFAULT NULL COMMENT '计费周期(月度、季度、年度)',
  `month_count` int(11) DEFAULT NULL COMMENT '鍏ラ┗鏈堟暟',
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
) ENGINE=InnoDB AUTO_INCREMENT=66 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单主表';

-- ----------------------------
-- Records of order_info
-- ----------------------------
INSERT INTO `order_info` VALUES ('64', 'ORD1769354296366', '1', '42', '119', '30', null, '59', '11400.00', '11400.00', '0.00', '11400.00', '1', '支付宝', '2026-01-25 23:20:20', '2026-01-25', '2026-01-25', '2027-01-25', '月度', '12', null, '', 'jg696562', '2026-01-25 23:18:16', '', '2026-01-25 23:20:20');
INSERT INTO `order_info` VALUES ('65', 'ORD1769354699641', '2', '42', '119', '30', null, null, '1400.00', '1400.00', '0.00', '1400.00', '1', '支付宝', '2026-01-25 23:34:24', '2026-01-25', '2027-01-25', '2027-03-25', '月度', '2', null, null, 'jg696562', '2026-01-25 23:25:00', '', '2026-01-25 23:34:24');

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
  `original_unit_price` decimal(10,2) DEFAULT NULL COMMENT '原始单价（审核修改前保存）',
  `is_price_modified` char(1) DEFAULT '0' COMMENT '价格是否被修改过 0-否 1-是',
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
) ENGINE=InnoDB AUTO_INCREMENT=211 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='订单明细表';

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES ('201', '63', 'ORD1769353226698', '床位费', 'bed_fee', '101-01床位费', '500.00', null, '0', '12', '6000.00', '月度', 'jg696562', '2026-01-25 23:00:27', '', null, null);
INSERT INTO `order_item` VALUES ('202', '63', 'ORD1769353226698', '护理费', 'care_fee', '自理护理费', '200.00', null, '0', '12', '2400.00', '月度', 'jg696562', '2026-01-25 23:00:27', '', null, null);
INSERT INTO `order_item` VALUES ('203', '63', 'ORD1769353226698', '押金', 'deposit', '入住押金', '500.00', null, '0', '1', '500.00', null, 'jg696562', '2026-01-25 23:00:27', '', null, null);
INSERT INTO `order_item` VALUES ('204', '63', 'ORD1769353226698', '会员费', 'member_fee', '会员卡充值', '500.00', null, '0', '1', '500.00', null, 'jg696562', '2026-01-25 23:00:27', '', null, null);
INSERT INTO `order_item` VALUES ('205', '64', 'ORD1769354296366', '床位费', 'bed_fee', '102-23床位费', '300.00', null, '0', '12', '3600.00', '月度', 'jg696562', '2026-01-25 23:18:16', '', null, null);
INSERT INTO `order_item` VALUES ('206', '64', 'ORD1769354296366', '护理费', 'care_fee', '自理护理费', '400.00', null, '0', '12', '4800.00', '月度', 'jg696562', '2026-01-25 23:18:16', '', null, null);
INSERT INTO `order_item` VALUES ('207', '64', 'ORD1769354296366', '押金', 'deposit', '入住押金', '2000.00', null, '0', '1', '2000.00', null, 'jg696562', '2026-01-25 23:18:16', '', null, null);
INSERT INTO `order_item` VALUES ('208', '64', 'ORD1769354296366', '会员费', 'member_fee', '会员卡充值', '1000.00', null, '0', '1', '1000.00', null, 'jg696562', '2026-01-25 23:18:16', '', null, null);
INSERT INTO `order_item` VALUES ('209', '65', 'ORD1769354699641', '床位费', 'bed_fee', '床位费2个月', '300.00', null, '0', '2', '600.00', '2个月', 'jg696562', '2026-01-25 23:25:00', '', null, null);
INSERT INTO `order_item` VALUES ('210', '65', 'ORD1769354699641', '护理费', 'care_fee', '护理费2个月', '400.00', null, '0', '2', '800.00', '2个月', 'jg696562', '2026-01-25 23:25:00', '', null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=23 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='支付记录表';

-- ----------------------------
-- Records of payment_record
-- ----------------------------
INSERT INTO `payment_record` VALUES ('21', 'PAY1769354418932162', '64', '42', '30', '11400.00', '支付宝', '0', '2026-01-25 23:20:20', 'PAY1769354418932162', null, '120', null, '', '2026-01-25 23:20:20', '', null);
INSERT INTO `payment_record` VALUES ('22', 'PAY1769355263081707', '65', '42', '30', '1400.00', '支付宝', '0', '2026-01-25 23:34:24', 'PAY1769355263081707', null, '120', null, '', '2026-01-25 23:34:24', '', null);

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
  `street` varchar(200) DEFAULT NULL COMMENT '所属街道/区域',
  `district_code` varchar(50) DEFAULT NULL COMMENT '区县代码(关联pension_district字典)',
  `area_code` varchar(12) DEFAULT NULL COMMENT '区县代码',
  `actual_address` varchar(500) DEFAULT NULL COMMENT '瀹為檯缁忚惀鍦板潃',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '娉曞畾浠ｈ〃浜',
  `contact_person` varchar(50) NOT NULL COMMENT '鑱旂郴浜',
  `contact_phone` varchar(20) NOT NULL COMMENT '鑱旂郴鐢佃瘽',
  `contact_email` varchar(100) DEFAULT NULL COMMENT '鑱旂郴閭??',
  `business_scope` varchar(1000) DEFAULT NULL COMMENT '缁忚惀鑼冨洿',
  `institution_type` varchar(50) DEFAULT '1' COMMENT '机构类型(nursing_home养老院, service_center养老服务中心, day_care日间照料中心, senior_apartment养老公寓, other其他)',
  `institution_nature` char(1) DEFAULT '1' COMMENT '机构性质: 1-民办 2-公办 3-公建民营',
  `bed_count` int(11) DEFAULT NULL COMMENT '搴婁綅鏁',
  `care_levels` varchar(100) DEFAULT NULL COMMENT '收住类型(多选): 1-自理,2-半护理,3-全护理,4-失能,5-失智',
  `medical_condition` char(1) DEFAULT NULL COMMENT '医疗条件: 1-内设医疗机构,2-与医疗机构合作,3-自营医疗机构,4-无医养结合',
  `rating_level` int(11) DEFAULT '3' COMMENT '星级评分: 1-5星',
  `price_range_min` decimal(10,2) DEFAULT '0.00' COMMENT '最低价格(元/月)',
  `price_range_max` decimal(10,2) DEFAULT '0.00' COMMENT '最高价格(元/月)',
  `free_trial` char(1) DEFAULT '0' COMMENT '是否支持免费试住: 0-否 1-是',
  `fee_range` varchar(100) DEFAULT NULL COMMENT '收费区间',
  `business_license` varchar(500) DEFAULT NULL COMMENT '营业执照文件路径',
  `approval_certificate` varchar(500) DEFAULT NULL COMMENT '批准证书文件路径',
  `supervision_agreement` varchar(500) DEFAULT NULL COMMENT '监管协议文件路径',
  `established_date` date DEFAULT NULL COMMENT '鎴愮珛鏃ユ湡',
  `organizer` varchar(200) DEFAULT NULL COMMENT '兴办机构',
  `responsible_name` varchar(50) DEFAULT NULL COMMENT '负责人姓名',
  `responsible_id_card` varchar(18) DEFAULT NULL COMMENT '负责人身份证号',
  `responsible_address` varchar(500) DEFAULT NULL COMMENT '负责人居住地',
  `responsible_phone` varchar(20) DEFAULT NULL COMMENT '负责人电话',
  `record_number` varchar(100) DEFAULT NULL COMMENT '澶囨?鍙',
  `fixed_assets` decimal(15,2) DEFAULT NULL COMMENT '鍥哄畾璧勪骇鍑??(涓囧厓)',
  `bank_account` varchar(100) DEFAULT NULL COMMENT '鍩烘湰缁撶畻璐︽埛',
  `supervise_account` varchar(100) DEFAULT NULL COMMENT '鐩戠?璐︽埛',
  `supervise_account_status` char(1) DEFAULT '0' COMMENT '监管账户状态:0-未开户,1-已开户',
  `status` varchar(10) DEFAULT NULL COMMENT '状态:4-草稿,0-待审批,1-已入驻,2-已驳回,5-维护中,6-维护待审批',
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
  `nursing_fee_min` decimal(10,2) DEFAULT NULL COMMENT '护理费最低价',
  `nursing_fee_max` decimal(10,2) DEFAULT NULL COMMENT '护理费最高价',
  `bed_fee_min` decimal(10,2) DEFAULT NULL COMMENT '床位费最低价',
  `bed_fee_max` decimal(10,2) DEFAULT NULL COMMENT '床位费最高价',
  `meal_fee_min` decimal(10,2) DEFAULT NULL COMMENT '膳食费最低价',
  `meal_fee_max` decimal(10,2) DEFAULT NULL COMMENT '膳食费最高价',
  `cover_images` text COMMENT '机构封面图片JSON格式存储多张图片URL',
  PRIMARY KEY (`institution_id`),
  KEY `idx_district_code` (`district_code`),
  KEY `idx_institution_nature` (`institution_nature`),
  KEY `idx_medical_condition` (`medical_condition`),
  KEY `idx_rating_level` (`rating_level`),
  KEY `idx_price_range` (`price_range_min`,`price_range_max`)
) ENGINE=InnoDB AUTO_INCREMENT=31 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鍏昏?鏈烘瀯淇℃伅琛';

-- ----------------------------
-- Records of pension_institution
-- ----------------------------
INSERT INTO `pension_institution` VALUES ('29', '郑州兴福养老集团', '91410100MA45815623', null, null, null, null, null, null, null, '程星', '15623696562', null, null, '1', '1', null, null, null, '3', '0.00', '0.00', '0', null, null, null, null, null, null, null, null, null, null, null, null, null, null, '0', null, null, null, null, null, '0', 'admin', '2026-01-25 19:16:59', '', null, null, null, null, null, null, null, null, null, null, null);
INSERT INTO `pension_institution` VALUES ('30', '兴福养老集团郑州花园路园区', '91410100MA46852591', '500.00', '郑州市花园路56号', '花园路街道办事处', null, '410105', '郑州市花园路56号', null, '陈飞雨', '15626963212', null, null, 'nursing_home', '1', '200', null, null, '3', '0.00', '0.00', '0', '1000-5000元/月', 'http://localhost:8080/profile/upload/2026/01/25/750+371_20260125192531A001.png', 'http://localhost:8080/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125192535A002.png', 'http://localhost:8080/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125192539A003.jpg', '2009-11-30', '兴福养老集团', '李兴福', '412829198905060056', '郑州市花园路566号', '15623965489', '562323', '1000.00', '12356321', '569865623', '0', '1', '2026-01-25 19:25:44', '2026-01-25 22:12:13', 'admin', null, '0', '', '2026-01-25 19:25:44', '', '2026-01-25 22:12:13', null, null, null, null, null, null, null, null, null, null);

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏈烘瀯闄勪欢鏉愭枡琛';

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
  `main_picture` text COMMENT '机构主图URL',
  `vr_image` varchar(500) DEFAULT NULL COMMENT 'VR全景图片',
  `rating` char(1) DEFAULT '3' COMMENT '鏈烘瀯璇勭骇(1鈽?2鈽呪槄 3鈽呪槄鈽?4鈽呪槄鈽呪槄 5鈽呪槄鈽呪槄鈽?',
  `fee_range_min` decimal(10,2) DEFAULT NULL COMMENT '鏀惰垂鍖洪棿-鏈?綆(鍏?鏈?',
  `fee_range_max` decimal(10,2) DEFAULT NULL COMMENT '鏀惰垂鍖洪棿-鏈?珮(鍏?鏈?',
  `accept_elder_type` varchar(200) DEFAULT NULL COMMENT '鏀朵綇鑰佷汉绫诲瀷',
  `is_published` char(1) DEFAULT '0' COMMENT '鏄?惁鍏?ず(0鍚?1鏄?',
  `create_by` varchar(64) DEFAULT '' COMMENT '鍒涘缓鑰',
  `create_time` datetime DEFAULT NULL COMMENT '鍒涘缓鏃堕棿',
  `update_by` varchar(64) DEFAULT '' COMMENT '鏇存柊鑰',
  `update_time` datetime DEFAULT NULL COMMENT '鏇存柊鏃堕棿',
  `nursing_fee_min` decimal(10,2) DEFAULT NULL COMMENT '护理费最低价',
  `nursing_fee_max` decimal(10,2) DEFAULT NULL COMMENT '护理费最高价',
  `bed_fee_min` decimal(10,2) DEFAULT NULL COMMENT '床位费最低价',
  `bed_fee_max` decimal(10,2) DEFAULT NULL COMMENT '床位费最高价',
  `meal_fee_min` decimal(10,2) DEFAULT NULL COMMENT '膳食费最低价',
  `meal_fee_max` decimal(10,2) DEFAULT NULL COMMENT '膳食费最高价',
  `room_facilities` text COMMENT '房间设施图片(JSON数组)',
  `basic_facilities` text COMMENT '基础设施图片(JSON数组)',
  `park_facilities` text COMMENT '园址设施图片(JSON数组)',
  `life_facilities` text COMMENT '生活设施选项(JSON数组)',
  `medical_facilities` text COMMENT '医疗设施选项(JSON数组)',
  `daily_services` text COMMENT '每日服务安排(JSON数组)',
  PRIMARY KEY (`public_id`),
  KEY `idx_institution_id` (`institution_id`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鏈烘瀯鍏?ず淇℃伅琛';

-- ----------------------------
-- Records of pension_institution_public
-- ----------------------------
INSERT INTO `pension_institution_public` VALUES ('6', '30', '这是新建的测试机构简介', '生活照料、医疗护理、康复训练', '中医理疗、文化娱乐、营养膳食', '1000.00', '800.00', '/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125222613A006.png,/profile/upload/2026/01/25/750+371_20260125222615A007.png', '/profile/upload/2026/01/25/ScreenShot_2026-01-25_021541_711_20260125222442A004.png', '/profile/upload/2026/01/25/QP61014284_20260125222453A005.jpg', '3', null, null, 'self_care,semi_disabled,disabled,dementia', '1', '', '2026-01-25 22:28:03', '', '2026-01-25 22:28:07', '500.00', '2500.00', '700.00', '2000.00', '1500.00', '4000.00', '/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125222617A008.jpg,/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125222620A009.png', '/profile/upload/2026/01/25/ScreenShot_2026-01-12_015115_648_20260125222632A010.png,/profile/upload/2026/01/25/1666946350486_5586_20260125222635A011.jpeg', '/profile/upload/2026/01/25/df079c167cae476cb21790c62b610481_20260125222641A012.png', '[\"测试设施\",\"独立卫浴\",\"紧急呼叫\",\"洗衣服务\",\"活动室\",\"图书阅览室\",\"电视/娱乐设备\",\"空调设备\",\"暖气设备\",\"无线网络\",\"呼叫系统\",\"阳台/露台\",\"吧台/茶水间\",\"衣柜\",\"储物柜\"]', '[\"医疗室\",\"专业医生\",\"护士站\",\"康复室\",\"理疗室\",\"体检设备\",\"健康监测\",\"心电图机\",\"除颤仪\",\"呼吸机\",\"氧气设备\",\"输液设备\"]', '[{\"time\":\"06:00\",\"content\":\"集体起床\"},{\"time\":\"07:28\",\"content\":\"集体用餐\"},{\"time\":\"08:27\",\"content\":\"遛弯跑操\"}]');

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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='退款记录表';

-- ----------------------------
-- Records of refund_record
-- ----------------------------

-- ----------------------------
-- Table structure for release_supervision
-- ----------------------------
DROP TABLE IF EXISTS `release_supervision`;
CREATE TABLE `release_supervision` (
  `release_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '解除监管申请ID',
  `apply_no` varchar(50) DEFAULT NULL COMMENT '申请编号',
  `institution_id` bigint(20) DEFAULT NULL COMMENT '机构ID',
  `institution_name` varchar(100) DEFAULT NULL COMMENT '机构名称',
  `credit_code` varchar(50) DEFAULT NULL COMMENT '统一信用代码',
  `legal_person` varchar(50) DEFAULT NULL COMMENT '法定代表人',
  `contact_person` varchar(50) DEFAULT NULL COMMENT '联系人',
  `contact_phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `release_reason` varchar(500) DEFAULT NULL COMMENT '解除原因',
  `supervision_balance` decimal(15,2) DEFAULT NULL COMMENT '监管账户总余额',
  `service_fee_balance` decimal(15,2) DEFAULT NULL COMMENT '预收服务费余额',
  `deposit_balance` decimal(15,2) DEFAULT NULL COMMENT '预收押金余额',
  `member_fee_balance` decimal(15,2) DEFAULT NULL COMMENT '预收会员费余额',
  `supervision_bank` varchar(100) DEFAULT NULL COMMENT '监管开户银行',
  `supervision_account` varchar(50) DEFAULT NULL COMMENT '监管账号',
  `basic_bank` varchar(100) DEFAULT NULL COMMENT '基本账户银行',
  `basic_account` varchar(50) DEFAULT NULL COMMENT '基本账号',
  `apply_status` char(1) DEFAULT '0' COMMENT '申请状态（0待审批 1已批准 2已驳回）',
  `apply_time` datetime DEFAULT NULL COMMENT '申请时间',
  `approver` varchar(50) DEFAULT NULL COMMENT '审批人',
  `approve_time` datetime DEFAULT NULL COMMENT '审批时间',
  `approve_remark` varchar(500) DEFAULT NULL COMMENT '审批意见',
  `reject_reason` varchar(500) DEFAULT NULL COMMENT '驳回原因',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`release_id`),
  KEY `idx_institution_id` (`institution_id`),
  KEY `idx_apply_status` (`apply_status`),
  KEY `idx_apply_time` (`apply_time`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='机构解除监管申请表';

-- ----------------------------
-- Records of release_supervision
-- ----------------------------
INSERT INTO `release_supervision` VALUES ('1', 'SQ20250101001', '1', '康乐养老院', '91110000123456789A', '张三', '李四', '13800138000', '因经营策略调整，申请解除资金监管', '150000.00', '80000.00', '50000.00', '20000.00', '中国工商银行北京分行', '1109075123456', '中国建设银行北京分行', '1109076098765', '0', '2025-01-01 10:00:00', null, null, null, null, '2025-11-04 10:12:26', '2025-11-04 10:12:26');
INSERT INTO `release_supervision` VALUES ('2', 'SQ20250102002', '2', '夕阳红养老中心', '91110000987654321B', '王五', '赵六', '13900139000', '服务模式变更，申请解除监管', '280000.00', '150000.00', '100000.00', '30000.00', '中国农业银行上海分行', '1039085234567', '中国工商银行上海分行', '1009085987654', '0', '2025-01-02 14:30:00', null, null, null, null, '2025-11-04 10:12:26', '2025-11-04 10:12:26');
INSERT INTO `release_supervision` VALUES ('3', 'SQ20250103003', '3', '幸福晚年护理院', '91110000567891234C', '陈七', '周八', '13700137000', '转型为公益机构，申请解除监管', '120000.00', '60000.00', '40000.00', '20000.00', '中国银行深圳分行', '1059176543210', '招商银行深圳分行', '7559176123456', '1', '2025-01-03 09:15:00', null, null, null, null, '2025-11-04 10:12:26', '2025-11-04 10:12:26');
INSERT INTO `release_supervision` VALUES ('4', 'SQ20250104004', '4', '金色年华养老公寓', '91110000112233445D', '刘九', '吴十', '13600136000', '合并重组，申请解除监管', '350000.00', '200000.00', '120000.00', '30000.00', '交通银行广州分行', '1106087123456', '中信银行广州分行', '7336087987654', '2', '2025-01-04 16:45:00', null, null, null, null, '2025-11-04 10:12:26', '2025-11-04 10:12:26');
INSERT INTO `release_supervision` VALUES ('5', 'SQ20250105005', '5', '安康养老服务有限公司', '91110000998877665E', '郑一', '孙二', '13500135000', '业务调整，申请解除监管', '95000.00', '45000.00', '35000.00', '15000.00', '中国民生银行成都分行', '1046876234567', '中国光大银行成都分行', '1046876789012', '0', '2025-01-05 11:20:00', null, null, null, null, '2025-11-04 10:12:26', '2025-11-04 10:12:26');

-- ----------------------------
-- Table structure for sys_banner
-- ----------------------------
DROP TABLE IF EXISTS `sys_banner`;
CREATE TABLE `sys_banner` (
  `banner_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '幻灯片ID',
  `title` varchar(100) DEFAULT NULL COMMENT '标题',
  `image_url` varchar(500) NOT NULL COMMENT '图片URL',
  `link_type` char(1) DEFAULT '1' COMMENT '链接类型：1-机构详情 2-外部URL',
  `link_value` varchar(200) DEFAULT NULL COMMENT '链接值（机构ID或URL）',
  `sort` int(4) DEFAULT '0' COMMENT '排序号',
  `status` char(1) DEFAULT '0' COMMENT '状态：0-正常 1-停用',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`banner_id`)
) ENGINE=InnoDB AUTO_INCREMENT=2103 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='幻灯片管理表';

-- ----------------------------
-- Records of sys_banner
-- ----------------------------
INSERT INTO `sys_banner` VALUES ('2100', '水电费', '/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125030323A002.png', '2', 'baidu.com', '0', '0', 'admin', '2025-12-24 12:19:55', 'admin', '2026-01-25 03:03:24', null);
INSERT INTO `sys_banner` VALUES ('2101', '阿斯蒂芬阿斯蒂芬', '/profile/upload/2026/01/25/ScreenShot_2026-01-25_021541_711_20260125030339A003.png', '2', 'baidu.com', '0', '0', 'admin', '2025-12-24 17:52:33', 'admin', '2026-01-25 03:03:40', null);
INSERT INTO `sys_banner` VALUES ('2102', 'ces', '/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125030349A004.jpg', '1', null, '0', '0', 'admin', '2026-01-25 03:03:52', '', null, null);

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
INSERT INTO `sys_config` VALUES ('4', '账号自助-验证码开关', 'sys.account.captchaEnabled', 'false', 'N', 'admin', '2025-10-28 02:47:08', 'admin', '2025-11-16 00:25:39', '是否开启验证码功能（true开启，false关闭）');
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
INSERT INTO `sys_dept` VALUES ('100', '0', '0', '测试管理员', '0', '测试管理员', '15888888888', 'ry@qq.com', '0', '0', 'admin', '2025-10-28 02:47:08', 'admin', '2025-11-10 02:20:57');
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
) ENGINE=InnoDB AUTO_INCREMENT=1154 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典数据表';

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
INSERT INTO `sys_dict_data` VALUES ('1010', '1', '一般', '一般', 'urgency_level', '', 'info', 'Y', '0', 'admin', '2025-11-13 02:41:48', '', null, '一般情况可延后处理');
INSERT INTO `sys_dict_data` VALUES ('1011', '2', '紧急', '紧急', 'urgency_level', '', 'warning', 'N', '0', 'admin', '2025-11-13 02:41:48', '', null, '紧急情况需优先处理');
INSERT INTO `sys_dict_data` VALUES ('1012', '3', '非常紧急', '非常紧急', 'urgency_level', '', 'danger', 'N', '0', 'admin', '2025-11-13 02:41:48', '', null, '非常紧急情况需立即处理');
INSERT INTO `sys_dict_data` VALUES ('1020', '1', '草稿', 'draft', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '申请草稿');
INSERT INTO `sys_dict_data` VALUES ('1021', '2', '待家属审批', 'pending_family', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '等待家属或老人确认');
INSERT INTO `sys_dict_data` VALUES ('1022', '3', '家属已审批', 'family_approved', 'deposit_apply_status', '', 'primary', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '家属已确认,待监管部门审批');
INSERT INTO `sys_dict_data` VALUES ('1023', '4', '待监管审批', 'pending_supervision', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '等待监管部门审批');
INSERT INTO `sys_dict_data` VALUES ('1024', '5', '已通过', 'approved', 'deposit_apply_status', '', 'success', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '监管部门审批通过');
INSERT INTO `sys_dict_data` VALUES ('1025', '6', '已驳回', 'rejected', 'deposit_apply_status', '', 'danger', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '审批被驳回');
INSERT INTO `sys_dict_data` VALUES ('1026', '7', '已撤回', 'withdrawn', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', '2025-11-13 02:42:00', '', null, '申请已撤回');
INSERT INTO `sys_dict_data` VALUES ('1027', '1', 'Children', '1', 'elder_relation_type', null, null, 'N', '0', 'admin', '2025-11-14 12:03:21', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1028', '2', 'Spouse', '2', 'elder_relation_type', null, null, 'N', '0', 'admin', '2025-11-14 12:03:21', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1029', '3', 'Sibling', '3', 'elder_relation_type', null, null, 'N', '0', 'admin', '2025-11-14 12:03:21', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1030', '4', 'Relative', '4', 'elder_relation_type', null, null, 'N', '0', 'admin', '2025-11-14 12:03:21', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1031', '5', 'Friend', '5', 'elder_relation_type', null, null, 'N', '0', 'admin', '2025-11-14 12:03:21', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1101', '1', '中原区', '410102', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1102', '2', '二七区', '410103', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1103', '3', '管城回族区', '410104', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1104', '4', '金水区', '410105', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1105', '5', '上街区', '410106', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1106', '6', '惠济区', '410108', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1107', '7', '中牟县', '410122', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1108', '8', '巩义市', '410181', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1109', '9', '荥阳市', '410182', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1110', '10', '新密市', '410183', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1111', '11', '新郑市', '410184', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1112', '12', '登封市', '410185', 'pension_district', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:38', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1113', '1', '民办', '1', 'pension_institution_nature', '', 'primary', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1114', '2', '公办', '2', 'pension_institution_nature', '', 'success', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1115', '3', '公建民营', '3', 'pension_institution_nature', '', 'info', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1116', '1', '自理', '1', 'pension_care_level', '', 'success', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1117', '2', '半护理', '2', 'pension_care_level', '', 'warning', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1118', '3', '全护理', '3', 'pension_care_level', '', 'danger', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1119', '4', '失能', '4', 'pension_care_level', '', 'danger', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1120', '5', '失智', '5', 'pension_care_level', '', 'danger', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1121', '1', '内设医疗机构', '1', 'pension_medical_condition', '', 'success', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1122', '2', '与医疗机构合作', '2', 'pension_medical_condition', '', 'primary', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1123', '3', '自营医疗机构', '3', 'pension_medical_condition', '', 'warning', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1124', '4', '无医养结合', '4', 'pension_medical_condition', '', 'info', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1125', '1', '一星级', '1', 'pension_rating_level', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1126', '2', '二星级', '2', 'pension_rating_level', '', 'default', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1127', '3', '三星级', '3', 'pension_rating_level', '', 'warning', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1128', '4', '四星级', '4', 'pension_rating_level', '', 'primary', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1129', '5', '五星级', '5', 'pension_rating_level', '', 'danger', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1130', '1', '全部机构', 'all', 'pension_bed_status', '', 'default', 'Y', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1131', '2', '有空位机构', 'available', 'pension_bed_status', '', 'success', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1132', '3', '免费入住机构', 'free', 'pension_bed_status', '', 'warning', 'N', '0', 'admin', '2025-11-20 00:32:26', '', null, null);
INSERT INTO `sys_dict_data` VALUES ('1133', '1', '老人照片', '1', 'elder_photo_type', '', 'primary', 'N', '1', 'admin', '2025-12-02 17:33:58', '', null, '老人生活照片');
INSERT INTO `sys_dict_data` VALUES ('1134', '2', '身份证正面', '2', 'elder_photo_type', '', 'success', 'N', '1', 'admin', '2025-12-02 17:33:58', '', null, '身份证正面照片');
INSERT INTO `sys_dict_data` VALUES ('1135', '3', '身份证反面', '3', 'elder_photo_type', '', 'warning', 'N', '1', 'admin', '2025-12-02 17:33:58', '', null, '身份证反面照片');
INSERT INTO `sys_dict_data` VALUES ('1136', '1', '后台管理', '1', 'elder_source_type', '', 'primary', 'Y', '1', 'admin', '2025-12-02 17:33:58', '', null, '后台管理系统录入');
INSERT INTO `sys_dict_data` VALUES ('1137', '2', 'H5用户', '2', 'elder_source_type', '', 'success', 'N', '1', 'admin', '2025-12-02 17:33:58', '', null, 'H5用户自助录入');
INSERT INTO `sys_dict_data` VALUES ('1138', '4', '解除监管', '3', 'pension_institution_status', '', 'info', 'N', '0', 'admin', '2025-12-04 19:50:40', 'admin', '2025-12-04 19:50:40', '机构解除监管');
INSERT INTO `sys_dict_data` VALUES ('1139', '5', '维护中', '5', 'pension_institution_status', '', 'primary', 'N', '0', 'admin', '2025-12-04 19:50:40', 'admin', '2025-12-04 19:50:40', '机构信息维护中');
INSERT INTO `sys_dict_data` VALUES ('1140', '6', '维护待审批', '6', 'pension_institution_status', '', 'warning', 'N', '0', 'admin', '2025-12-04 19:50:40', 'admin', '2025-12-04 19:50:40', '机构维护申请待审批');
INSERT INTO `sys_dict_data` VALUES ('1141', '7', '草稿', '4', 'pension_institution_status', '', 'info', 'N', '0', 'admin', '2025-12-04 19:50:40', 'admin', '2025-12-04 19:50:40', '机构申��草稿状态');
INSERT INTO `sys_dict_data` VALUES ('1142', '1', '生活设施', 'life', 'facility_type', '', 'primary', 'Y', '0', 'admin', '2025-12-07 19:55:02', '', null, '生活设施类型');
INSERT INTO `sys_dict_data` VALUES ('1143', '2', '医疗设施', 'medical', 'facility_type', '', 'success', 'N', '0', 'admin', '2025-12-07 19:55:02', '', null, '医疗设施类型');
INSERT INTO `sys_dict_data` VALUES ('1144', '1', '民办机构', '1', 'pension_institution_type', '', 'primary', 'N', '0', 'admin', '2025-12-20 09:09:46', '', null, '民办机构');
INSERT INTO `sys_dict_data` VALUES ('1145', '2', '公办机构', '2', 'pension_institution_type', '', 'success', 'N', '0', 'admin', '2025-12-20 09:09:46', '', null, '公办机构');
INSERT INTO `sys_dict_data` VALUES ('1146', '3', '公建民营', '3', 'pension_institution_type', '', 'info', 'N', '0', 'admin', '2025-12-20 09:09:46', '', null, '公建民营');
INSERT INTO `sys_dict_data` VALUES ('1147', '4', '养老院', 'nursing_home', 'pension_institution_type', '', 'warning', 'N', '0', 'admin', '2025-12-20 09:09:46', '', null, '养老院');
INSERT INTO `sys_dict_data` VALUES ('1148', '5', '服务中心', 'service_center', 'pension_institution_type', '', 'danger', 'N', '0', 'admin', '2025-12-20 09:09:46', '', null, '服务中心');
INSERT INTO `sys_dict_data` VALUES ('1149', '1', '待审核', '0', 'review_status', '', 'warning', 'N', '0', 'admin', '2025-12-22 23:19:45', '', null, '待审核状态');
INSERT INTO `sys_dict_data` VALUES ('1150', '2', '已通过', '1', 'review_status', '', 'success', 'N', '0', 'admin', '2025-12-22 23:19:45', '', null, '已通过状态');
INSERT INTO `sys_dict_data` VALUES ('1151', '3', '已拒绝', '2', 'review_status', '', 'danger', 'N', '0', 'admin', '2025-12-22 23:19:45', '', null, '已拒绝状态');
INSERT INTO `sys_dict_data` VALUES ('1152', '1', '待审核', '4', 'order_status', '', 'info', 'N', '0', 'admin', '2026-01-19 17:35:44', '', null, '订单待审核');
INSERT INTO `sys_dict_data` VALUES ('1153', '2', '审核通过', '5', 'order_status', '', 'primary', 'N', '0', 'admin', '2026-01-19 17:35:44', '', null, '订单审核通过待支付');

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
) ENGINE=InnoDB AUTO_INCREMENT=412 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='字典类型表';

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
INSERT INTO `sys_dict_type` VALUES ('110', '养老机构区域', 'pension_district', '0', 'admin', '2025-11-20 00:28:58', '', null, '郑州市各区县街道');
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
INSERT INTO `sys_dict_type` VALUES ('309', 'relation_type', 'elder_relation_type', '0', 'admin', '2025-11-14 12:02:33', '', null, '家属与老人的关系类型');
INSERT INTO `sys_dict_type` VALUES ('402', '机构性质', 'pension_institution_nature', '0', 'admin', '2025-11-20 00:32:26', '', null, '养老机构性质');
INSERT INTO `sys_dict_type` VALUES ('403', '收住类型', 'pension_care_level', '0', 'admin', '2025-11-20 00:32:26', '', null, '护理等级');
INSERT INTO `sys_dict_type` VALUES ('404', '医疗条件', 'pension_medical_condition', '0', 'admin', '2025-11-20 00:32:26', '', null, '医疗服务');
INSERT INTO `sys_dict_type` VALUES ('405', '机构星级', 'pension_rating_level', '0', 'admin', '2025-11-20 00:32:26', '', null, '星级评定');
INSERT INTO `sys_dict_type` VALUES ('406', '床位情况', 'pension_bed_status', '0', 'admin', '2025-11-20 00:32:26', '', null, '床位状况');
INSERT INTO `sys_dict_type` VALUES ('407', '老人照片类型', 'elder_photo_type', '1', 'admin', '2025-12-02 17:33:58', '', null, '老人照片类型字典');
INSERT INTO `sys_dict_type` VALUES ('408', '老人信息来源', 'elder_source_type', '1', 'admin', '2025-12-02 17:33:58', '', null, '老人信息来源类型字典');
INSERT INTO `sys_dict_type` VALUES ('409', '设施类型', 'facility_type', '0', 'admin', '2025-12-07 19:54:52', '', null, '生活设施和医疗设施类型');
INSERT INTO `sys_dict_type` VALUES ('410', '养老机构类型', 'pension_institution_type', '0', 'admin', '2025-12-20 09:09:37', '', null, '养老机构类型字典');
INSERT INTO `sys_dict_type` VALUES ('411', '评价审核状态', 'review_status', '0', 'admin', '2025-12-22 23:19:39', '', null, '评价审核状态列表');

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
) ENGINE=InnoDB AUTO_INCREMENT=536 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='系统访问记录';

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
INSERT INTO `sys_logininfor` VALUES ('112', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-10-31 03:44:34');
INSERT INTO `sys_logininfor` VALUES ('113', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-10-31 03:44:57');
INSERT INTO `sys_logininfor` VALUES ('114', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-03 15:03:36');
INSERT INTO `sys_logininfor` VALUES ('115', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-03 15:47:37');
INSERT INTO `sys_logininfor` VALUES ('116', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-03 17:38:40');
INSERT INTO `sys_logininfor` VALUES ('117', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-03 19:52:15');
INSERT INTO `sys_logininfor` VALUES ('118', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-03 22:12:28');
INSERT INTO `sys_logininfor` VALUES ('119', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-04 01:01:22');
INSERT INTO `sys_logininfor` VALUES ('120', 'admin', '127.0.0.1', '内网IP', 'Downloading Tool', 'Unknown', '1', '验证码已失效', '2025-11-04 01:10:46');
INSERT INTO `sys_logininfor` VALUES ('121', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-04 01:21:58');
INSERT INTO `sys_logininfor` VALUES ('122', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-04 09:44:30');
INSERT INTO `sys_logininfor` VALUES ('123', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-09 22:35:51');
INSERT INTO `sys_logininfor` VALUES ('124', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-09 23:13:49');
INSERT INTO `sys_logininfor` VALUES ('125', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-09 23:54:25');
INSERT INTO `sys_logininfor` VALUES ('126', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-09 23:54:27');
INSERT INTO `sys_logininfor` VALUES ('127', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 02:25:04');
INSERT INTO `sys_logininfor` VALUES ('128', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 02:25:19');
INSERT INTO `sys_logininfor` VALUES ('129', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 02:36:04');
INSERT INTO `sys_logininfor` VALUES ('130', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 02:36:09');
INSERT INTO `sys_logininfor` VALUES ('131', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 02:57:33');
INSERT INTO `sys_logininfor` VALUES ('132', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 02:57:45');
INSERT INTO `sys_logininfor` VALUES ('133', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:00:13');
INSERT INTO `sys_logininfor` VALUES ('134', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:00:16');
INSERT INTO `sys_logininfor` VALUES ('135', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:01:51');
INSERT INTO `sys_logininfor` VALUES ('136', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:01:59');
INSERT INTO `sys_logininfor` VALUES ('137', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:05:26');
INSERT INTO `sys_logininfor` VALUES ('138', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:05:29');
INSERT INTO `sys_logininfor` VALUES ('139', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:12:31');
INSERT INTO `sys_logininfor` VALUES ('140', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:12:49');
INSERT INTO `sys_logininfor` VALUES ('141', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:13:38');
INSERT INTO `sys_logininfor` VALUES ('142', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:13:41');
INSERT INTO `sys_logininfor` VALUES ('143', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 03:29:16');
INSERT INTO `sys_logininfor` VALUES ('144', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 03:29:24');
INSERT INTO `sys_logininfor` VALUES ('145', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:03:15');
INSERT INTO `sys_logininfor` VALUES ('146', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:26:01');
INSERT INTO `sys_logininfor` VALUES ('147', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:26:11');
INSERT INTO `sys_logininfor` VALUES ('148', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:26:20');
INSERT INTO `sys_logininfor` VALUES ('149', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:26:23');
INSERT INTO `sys_logininfor` VALUES ('150', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:30:00');
INSERT INTO `sys_logininfor` VALUES ('151', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码错误', '2025-11-10 11:30:12');
INSERT INTO `sys_logininfor` VALUES ('152', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:30:15');
INSERT INTO `sys_logininfor` VALUES ('153', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:30:26');
INSERT INTO `sys_logininfor` VALUES ('154', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:30:28');
INSERT INTO `sys_logininfor` VALUES ('155', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:31:07');
INSERT INTO `sys_logininfor` VALUES ('156', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:31:15');
INSERT INTO `sys_logininfor` VALUES ('157', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:31:20');
INSERT INTO `sys_logininfor` VALUES ('158', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:31:32');
INSERT INTO `sys_logininfor` VALUES ('159', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:36:02');
INSERT INTO `sys_logininfor` VALUES ('160', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:36:09');
INSERT INTO `sys_logininfor` VALUES ('161', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:36:18');
INSERT INTO `sys_logininfor` VALUES ('162', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:36:21');
INSERT INTO `sys_logininfor` VALUES ('163', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:38:39');
INSERT INTO `sys_logininfor` VALUES ('164', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:38:46');
INSERT INTO `sys_logininfor` VALUES ('165', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 11:39:10');
INSERT INTO `sys_logininfor` VALUES ('166', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 11:39:18');
INSERT INTO `sys_logininfor` VALUES ('167', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 15:04:35');
INSERT INTO `sys_logininfor` VALUES ('168', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 17:01:11');
INSERT INTO `sys_logininfor` VALUES ('169', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 22:27:56');
INSERT INTO `sys_logininfor` VALUES ('170', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 22:37:11');
INSERT INTO `sys_logininfor` VALUES ('171', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 22:37:25');
INSERT INTO `sys_logininfor` VALUES ('172', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 22:40:50');
INSERT INTO `sys_logininfor` VALUES ('173', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 22:40:54');
INSERT INTO `sys_logininfor` VALUES ('174', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:08:38');
INSERT INTO `sys_logininfor` VALUES ('175', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:08:46');
INSERT INTO `sys_logininfor` VALUES ('176', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:09:10');
INSERT INTO `sys_logininfor` VALUES ('177', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:09:15');
INSERT INTO `sys_logininfor` VALUES ('178', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:10:20');
INSERT INTO `sys_logininfor` VALUES ('179', 'jg555555', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:10:30');
INSERT INTO `sys_logininfor` VALUES ('180', 'jg555555', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:22:17');
INSERT INTO `sys_logininfor` VALUES ('181', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:22:20');
INSERT INTO `sys_logininfor` VALUES ('182', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:23:23');
INSERT INTO `sys_logininfor` VALUES ('183', 'jg656565', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:23:33');
INSERT INTO `sys_logininfor` VALUES ('184', 'jg656565', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-10 23:49:49');
INSERT INTO `sys_logininfor` VALUES ('185', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-10 23:49:52');
INSERT INTO `sys_logininfor` VALUES ('186', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:25:26');
INSERT INTO `sys_logininfor` VALUES ('187', 'jigoumanage', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2025-11-11 02:25:34');
INSERT INTO `sys_logininfor` VALUES ('188', 'jigoumanage', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2025-11-11 02:25:37');
INSERT INTO `sys_logininfor` VALUES ('189', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:25:46');
INSERT INTO `sys_logininfor` VALUES ('190', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:26:01');
INSERT INTO `sys_logininfor` VALUES ('191', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:26:09');
INSERT INTO `sys_logininfor` VALUES ('192', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:31:54');
INSERT INTO `sys_logininfor` VALUES ('193', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:32:00');
INSERT INTO `sys_logininfor` VALUES ('194', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:38:10');
INSERT INTO `sys_logininfor` VALUES ('195', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:38:13');
INSERT INTO `sys_logininfor` VALUES ('196', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:38:58');
INSERT INTO `sys_logininfor` VALUES ('197', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:39:06');
INSERT INTO `sys_logininfor` VALUES ('198', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:39:37');
INSERT INTO `sys_logininfor` VALUES ('199', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:39:49');
INSERT INTO `sys_logininfor` VALUES ('200', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:49:05');
INSERT INTO `sys_logininfor` VALUES ('201', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:49:22');
INSERT INTO `sys_logininfor` VALUES ('202', 'jg279011', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:49:39');
INSERT INTO `sys_logininfor` VALUES ('203', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码已失效', '2025-11-11 02:55:47');
INSERT INTO `sys_logininfor` VALUES ('204', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:55:50');
INSERT INTO `sys_logininfor` VALUES ('205', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:56:47');
INSERT INTO `sys_logininfor` VALUES ('206', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:56:53');
INSERT INTO `sys_logininfor` VALUES ('207', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 02:57:21');
INSERT INTO `sys_logininfor` VALUES ('208', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 02:57:25');
INSERT INTO `sys_logininfor` VALUES ('209', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 03:04:39');
INSERT INTO `sys_logininfor` VALUES ('210', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 03:04:52');
INSERT INTO `sys_logininfor` VALUES ('211', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-11 03:16:50');
INSERT INTO `sys_logininfor` VALUES ('212', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 03:16:53');
INSERT INTO `sys_logininfor` VALUES ('213', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 11:21:13');
INSERT INTO `sys_logininfor` VALUES ('214', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码已失效', '2025-11-11 14:42:56');
INSERT INTO `sys_logininfor` VALUES ('215', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 14:42:59');
INSERT INTO `sys_logininfor` VALUES ('216', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-11 21:23:25');
INSERT INTO `sys_logininfor` VALUES ('217', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 01:32:11');
INSERT INTO `sys_logininfor` VALUES ('218', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 02:24:01');
INSERT INTO `sys_logininfor` VALUES ('219', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 11:20:03');
INSERT INTO `sys_logininfor` VALUES ('220', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 12:16:34');
INSERT INTO `sys_logininfor` VALUES ('221', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 15:32:07');
INSERT INTO `sys_logininfor` VALUES ('222', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 20:40:15');
INSERT INTO `sys_logininfor` VALUES ('223', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-12 22:39:34');
INSERT INTO `sys_logininfor` VALUES ('224', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-13 02:37:55');
INSERT INTO `sys_logininfor` VALUES ('225', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-13 22:05:12');
INSERT INTO `sys_logininfor` VALUES ('226', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-14 11:44:36');
INSERT INTO `sys_logininfor` VALUES ('227', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码错误', '2025-11-14 13:15:47');
INSERT INTO `sys_logininfor` VALUES ('228', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '验证码错误', '2025-11-14 13:15:50');
INSERT INTO `sys_logininfor` VALUES ('229', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-14 13:15:52');
INSERT INTO `sys_logininfor` VALUES ('230', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-14 16:44:09');
INSERT INTO `sys_logininfor` VALUES ('231', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-16 00:18:36');
INSERT INTO `sys_logininfor` VALUES ('232', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-16 00:25:18');
INSERT INTO `sys_logininfor` VALUES ('233', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-16 00:25:23');
INSERT INTO `sys_logininfor` VALUES ('234', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-16 00:25:46');
INSERT INTO `sys_logininfor` VALUES ('235', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-16 00:25:48');
INSERT INTO `sys_logininfor` VALUES ('236', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '1', '用户不存在/密码错误', '2025-11-16 01:00:13');
INSERT INTO `sys_logininfor` VALUES ('237', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:00:21');
INSERT INTO `sys_logininfor` VALUES ('238', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-16 01:27:01');
INSERT INTO `sys_logininfor` VALUES ('239', 'admin', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:27:03');
INSERT INTO `sys_logininfor` VALUES ('240', 'admin', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-16 01:37:18');
INSERT INTO `sys_logininfor` VALUES ('241', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:37:22');
INSERT INTO `sys_logininfor` VALUES ('242', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-16 01:38:15');
INSERT INTO `sys_logininfor` VALUES ('243', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:38:19');
INSERT INTO `sys_logininfor` VALUES ('244', 'admin', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:41:51');
INSERT INTO `sys_logininfor` VALUES ('245', 'admin', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '退出成功', '2025-11-16 01:42:03');
INSERT INTO `sys_logininfor` VALUES ('246', 'jg666666', '192.168.31.217', '内网IP', 'Chrome 13', 'Mac OS X', '0', '登录成功', '2025-11-16 01:42:07');
INSERT INTO `sys_logininfor` VALUES ('247', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-11-17 01:16:28');
INSERT INTO `sys_logininfor` VALUES ('248', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-17 01:16:35');
INSERT INTO `sys_logininfor` VALUES ('249', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-11-20 01:41:49');
INSERT INTO `sys_logininfor` VALUES ('250', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-02 11:39:56');
INSERT INTO `sys_logininfor` VALUES ('251', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-02 11:39:58');
INSERT INTO `sys_logininfor` VALUES ('252', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-02 16:35:38');
INSERT INTO `sys_logininfor` VALUES ('253', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-02 19:40:47');
INSERT INTO `sys_logininfor` VALUES ('254', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-02 20:11:13');
INSERT INTO `sys_logininfor` VALUES ('255', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-03 00:23:26');
INSERT INTO `sys_logininfor` VALUES ('256', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-03 15:35:07');
INSERT INTO `sys_logininfor` VALUES ('257', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-03 19:09:44');
INSERT INTO `sys_logininfor` VALUES ('258', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 11:54:32');
INSERT INTO `sys_logininfor` VALUES ('259', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 13:35:07');
INSERT INTO `sys_logininfor` VALUES ('260', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 14:50:45');
INSERT INTO `sys_logininfor` VALUES ('261', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 16:33:11');
INSERT INTO `sys_logininfor` VALUES ('262', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 17:38:14');
INSERT INTO `sys_logininfor` VALUES ('263', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 21:39:52');
INSERT INTO `sys_logininfor` VALUES ('264', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-04 22:44:24');
INSERT INTO `sys_logininfor` VALUES ('265', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-05 00:48:59');
INSERT INTO `sys_logininfor` VALUES ('266', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-05 20:35:23');
INSERT INTO `sys_logininfor` VALUES ('267', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-06 20:25:36');
INSERT INTO `sys_logininfor` VALUES ('268', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-07 06:47:52');
INSERT INTO `sys_logininfor` VALUES ('269', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-09 00:59:12');
INSERT INTO `sys_logininfor` VALUES ('270', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-09 12:39:36');
INSERT INTO `sys_logininfor` VALUES ('271', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-10 17:45:07');
INSERT INTO `sys_logininfor` VALUES ('272', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-10 19:56:07');
INSERT INTO `sys_logininfor` VALUES ('273', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-10 22:35:38');
INSERT INTO `sys_logininfor` VALUES ('274', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-10 23:58:28');
INSERT INTO `sys_logininfor` VALUES ('275', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-12 11:27:39');
INSERT INTO `sys_logininfor` VALUES ('276', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-12 15:22:24');
INSERT INTO `sys_logininfor` VALUES ('277', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-13 17:35:05');
INSERT INTO `sys_logininfor` VALUES ('278', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 00:36:44');
INSERT INTO `sys_logininfor` VALUES ('279', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 00:37:46');
INSERT INTO `sys_logininfor` VALUES ('280', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 00:37:53');
INSERT INTO `sys_logininfor` VALUES ('281', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 01:10:56');
INSERT INTO `sys_logininfor` VALUES ('282', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 01:43:34');
INSERT INTO `sys_logininfor` VALUES ('283', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 01:50:50');
INSERT INTO `sys_logininfor` VALUES ('284', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 01:50:53');
INSERT INTO `sys_logininfor` VALUES ('285', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 01:51:25');
INSERT INTO `sys_logininfor` VALUES ('286', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 01:51:33');
INSERT INTO `sys_logininfor` VALUES ('287', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 02:19:23');
INSERT INTO `sys_logininfor` VALUES ('288', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 02:19:28');
INSERT INTO `sys_logininfor` VALUES ('289', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 02:19:35');
INSERT INTO `sys_logininfor` VALUES ('290', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 02:26:44');
INSERT INTO `sys_logininfor` VALUES ('291', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 02:26:46');
INSERT INTO `sys_logininfor` VALUES ('292', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 02:37:02');
INSERT INTO `sys_logininfor` VALUES ('293', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 02:37:08');
INSERT INTO `sys_logininfor` VALUES ('294', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 20:09:24');
INSERT INTO `sys_logininfor` VALUES ('295', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 20:15:19');
INSERT INTO `sys_logininfor` VALUES ('296', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 20:15:25');
INSERT INTO `sys_logininfor` VALUES ('297', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 20:17:35');
INSERT INTO `sys_logininfor` VALUES ('298', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 20:17:37');
INSERT INTO `sys_logininfor` VALUES ('299', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 20:23:13');
INSERT INTO `sys_logininfor` VALUES ('300', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 20:23:23');
INSERT INTO `sys_logininfor` VALUES ('301', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 21:35:44');
INSERT INTO `sys_logininfor` VALUES ('302', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 21:37:03');
INSERT INTO `sys_logininfor` VALUES ('303', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 21:42:03');
INSERT INTO `sys_logininfor` VALUES ('304', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 21:42:18');
INSERT INTO `sys_logininfor` VALUES ('305', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 21:42:24');
INSERT INTO `sys_logininfor` VALUES ('306', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 21:42:54');
INSERT INTO `sys_logininfor` VALUES ('307', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 21:42:56');
INSERT INTO `sys_logininfor` VALUES ('308', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 21:54:40');
INSERT INTO `sys_logininfor` VALUES ('309', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 21:54:43');
INSERT INTO `sys_logininfor` VALUES ('310', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-14 22:35:03');
INSERT INTO `sys_logininfor` VALUES ('311', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 22:35:13');
INSERT INTO `sys_logininfor` VALUES ('312', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-14 23:54:17');
INSERT INTO `sys_logininfor` VALUES ('313', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-15 00:01:58');
INSERT INTO `sys_logininfor` VALUES ('314', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 00:02:00');
INSERT INTO `sys_logininfor` VALUES ('315', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-15 00:02:10');
INSERT INTO `sys_logininfor` VALUES ('316', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 00:02:17');
INSERT INTO `sys_logininfor` VALUES ('317', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-15 01:46:21');
INSERT INTO `sys_logininfor` VALUES ('318', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 01:46:23');
INSERT INTO `sys_logininfor` VALUES ('319', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-15 17:09:43');
INSERT INTO `sys_logininfor` VALUES ('320', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-16 12:46:50');
INSERT INTO `sys_logininfor` VALUES ('321', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-16 14:41:57');
INSERT INTO `sys_logininfor` VALUES ('322', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-18 15:29:41');
INSERT INTO `sys_logininfor` VALUES ('323', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-18 22:38:07');
INSERT INTO `sys_logininfor` VALUES ('324', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-18 22:38:15');
INSERT INTO `sys_logininfor` VALUES ('325', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-19 00:33:59');
INSERT INTO `sys_logininfor` VALUES ('326', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-19 01:07:41');
INSERT INTO `sys_logininfor` VALUES ('327', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-19 01:07:51');
INSERT INTO `sys_logininfor` VALUES ('328', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-19 01:09:00');
INSERT INTO `sys_logininfor` VALUES ('329', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-19 01:09:02');
INSERT INTO `sys_logininfor` VALUES ('330', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-19 01:10:12');
INSERT INTO `sys_logininfor` VALUES ('331', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-20 08:35:41');
INSERT INTO `sys_logininfor` VALUES ('332', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-20 22:02:37');
INSERT INTO `sys_logininfor` VALUES ('333', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 18:37:39');
INSERT INTO `sys_logininfor` VALUES ('334', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:20:39');
INSERT INTO `sys_logininfor` VALUES ('335', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:29:05');
INSERT INTO `sys_logininfor` VALUES ('336', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:29:07');
INSERT INTO `sys_logininfor` VALUES ('337', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:29:56');
INSERT INTO `sys_logininfor` VALUES ('338', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:29:58');
INSERT INTO `sys_logininfor` VALUES ('339', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:32:34');
INSERT INTO `sys_logininfor` VALUES ('340', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:32:36');
INSERT INTO `sys_logininfor` VALUES ('341', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:36:47');
INSERT INTO `sys_logininfor` VALUES ('342', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:36:59');
INSERT INTO `sys_logininfor` VALUES ('343', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:37:36');
INSERT INTO `sys_logininfor` VALUES ('344', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:37:39');
INSERT INTO `sys_logininfor` VALUES ('345', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-22 23:42:52');
INSERT INTO `sys_logininfor` VALUES ('346', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-22 23:42:53');
INSERT INTO `sys_logininfor` VALUES ('347', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-23 01:55:24');
INSERT INTO `sys_logininfor` VALUES ('348', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-23 15:16:30');
INSERT INTO `sys_logininfor` VALUES ('349', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-24 11:23:35');
INSERT INTO `sys_logininfor` VALUES ('350', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-24 17:10:25');
INSERT INTO `sys_logininfor` VALUES ('351', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-24 22:18:05');
INSERT INTO `sys_logininfor` VALUES ('352', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-25 00:18:17');
INSERT INTO `sys_logininfor` VALUES ('353', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-25 01:33:51');
INSERT INTO `sys_logininfor` VALUES ('354', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-26 17:04:46');
INSERT INTO `sys_logininfor` VALUES ('355', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-26 21:47:49');
INSERT INTO `sys_logininfor` VALUES ('356', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-28 23:32:32');
INSERT INTO `sys_logininfor` VALUES ('357', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-29 11:51:27');
INSERT INTO `sys_logininfor` VALUES ('358', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-29 16:02:35');
INSERT INTO `sys_logininfor` VALUES ('359', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-29 16:02:41');
INSERT INTO `sys_logininfor` VALUES ('360', 'jigouuser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2025-12-29 16:10:52');
INSERT INTO `sys_logininfor` VALUES ('361', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-29 16:10:54');
INSERT INTO `sys_logininfor` VALUES ('362', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-29 18:18:07');
INSERT INTO `sys_logininfor` VALUES ('363', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2025-12-29 19:52:00');
INSERT INTO `sys_logininfor` VALUES ('364', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-06 10:52:44');
INSERT INTO `sys_logininfor` VALUES ('365', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 10:53:04');
INSERT INTO `sys_logininfor` VALUES ('366', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 10:53:06');
INSERT INTO `sys_logininfor` VALUES ('367', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 10:53:13');
INSERT INTO `sys_logininfor` VALUES ('368', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 10:53:17');
INSERT INTO `sys_logininfor` VALUES ('369', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 10:57:49');
INSERT INTO `sys_logininfor` VALUES ('370', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:50');
INSERT INTO `sys_logininfor` VALUES ('371', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:50');
INSERT INTO `sys_logininfor` VALUES ('372', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:51');
INSERT INTO `sys_logininfor` VALUES ('373', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:51');
INSERT INTO `sys_logininfor` VALUES ('374', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:51');
INSERT INTO `sys_logininfor` VALUES ('375', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:52');
INSERT INTO `sys_logininfor` VALUES ('376', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:52');
INSERT INTO `sys_logininfor` VALUES ('377', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:52');
INSERT INTO `sys_logininfor` VALUES ('378', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:57:52');
INSERT INTO `sys_logininfor` VALUES ('379', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 10:59:32');
INSERT INTO `sys_logininfor` VALUES ('380', 'admin', '192.168.31.146', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:00:05');
INSERT INTO `sys_logininfor` VALUES ('381', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:05:14');
INSERT INTO `sys_logininfor` VALUES ('382', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:05:26');
INSERT INTO `sys_logininfor` VALUES ('383', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 11:07:43');
INSERT INTO `sys_logininfor` VALUES ('384', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 11:07:49');
INSERT INTO `sys_logininfor` VALUES ('385', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 11:10:35');
INSERT INTO `sys_logininfor` VALUES ('386', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 11:10:38');
INSERT INTO `sys_logininfor` VALUES ('387', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-10 11:10:49');
INSERT INTO `sys_logininfor` VALUES ('388', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:11:05');
INSERT INTO `sys_logininfor` VALUES ('389', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:12:29');
INSERT INTO `sys_logininfor` VALUES ('390', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:12:36');
INSERT INTO `sys_logininfor` VALUES ('391', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '密码输入错误5次，帐户锁定10分钟', '2026-01-10 11:12:49');
INSERT INTO `sys_logininfor` VALUES ('392', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-10 11:14:21');
INSERT INTO `sys_logininfor` VALUES ('393', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 01:06:09');
INSERT INTO `sys_logininfor` VALUES ('394', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 01:54:53');
INSERT INTO `sys_logininfor` VALUES ('395', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 01:54:55');
INSERT INTO `sys_logininfor` VALUES ('396', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 03:35:29');
INSERT INTO `sys_logininfor` VALUES ('397', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-19 03:35:36');
INSERT INTO `sys_logininfor` VALUES ('398', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 03:35:45');
INSERT INTO `sys_logininfor` VALUES ('399', 'jguser', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 03:36:20');
INSERT INTO `sys_logininfor` VALUES ('400', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 03:36:23');
INSERT INTO `sys_logininfor` VALUES ('401', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 03:53:26');
INSERT INTO `sys_logininfor` VALUES ('402', 'jg6565651', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-19 03:53:38');
INSERT INTO `sys_logininfor` VALUES ('403', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 03:53:54');
INSERT INTO `sys_logininfor` VALUES ('404', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 03:55:48');
INSERT INTO `sys_logininfor` VALUES ('405', 'jg564512', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 03:55:54');
INSERT INTO `sys_logininfor` VALUES ('406', 'jg564512', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 03:57:30');
INSERT INTO `sys_logininfor` VALUES ('407', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 03:57:31');
INSERT INTO `sys_logininfor` VALUES ('408', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 04:00:12');
INSERT INTO `sys_logininfor` VALUES ('409', 'jg655625', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 04:00:20');
INSERT INTO `sys_logininfor` VALUES ('410', 'jg655625', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 04:10:50');
INSERT INTO `sys_logininfor` VALUES ('411', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 04:10:52');
INSERT INTO `sys_logininfor` VALUES ('412', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 04:12:58');
INSERT INTO `sys_logininfor` VALUES ('413', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 04:13:00');
INSERT INTO `sys_logininfor` VALUES ('414', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 04:13:05');
INSERT INTO `sys_logininfor` VALUES ('415', 'jg655625', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 04:13:11');
INSERT INTO `sys_logininfor` VALUES ('416', 'jg655625', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 04:16:27');
INSERT INTO `sys_logininfor` VALUES ('417', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 04:16:29');
INSERT INTO `sys_logininfor` VALUES ('418', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 10:56:26');
INSERT INTO `sys_logininfor` VALUES ('419', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:16:44');
INSERT INTO `sys_logininfor` VALUES ('420', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 15:40:39');
INSERT INTO `sys_logininfor` VALUES ('421', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:40:46');
INSERT INTO `sys_logininfor` VALUES ('422', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 15:44:44');
INSERT INTO `sys_logininfor` VALUES ('423', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:44:46');
INSERT INTO `sys_logininfor` VALUES ('424', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 15:45:40');
INSERT INTO `sys_logininfor` VALUES ('425', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:45:45');
INSERT INTO `sys_logininfor` VALUES ('426', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 15:52:20');
INSERT INTO `sys_logininfor` VALUES ('427', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:52:22');
INSERT INTO `sys_logininfor` VALUES ('428', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 15:52:53');
INSERT INTO `sys_logininfor` VALUES ('429', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 15:52:59');
INSERT INTO `sys_logininfor` VALUES ('430', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 16:00:41');
INSERT INTO `sys_logininfor` VALUES ('431', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 16:00:43');
INSERT INTO `sys_logininfor` VALUES ('432', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 16:09:07');
INSERT INTO `sys_logininfor` VALUES ('433', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 16:09:11');
INSERT INTO `sys_logininfor` VALUES ('434', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 16:16:00');
INSERT INTO `sys_logininfor` VALUES ('435', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 16:16:02');
INSERT INTO `sys_logininfor` VALUES ('436', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 17:14:02');
INSERT INTO `sys_logininfor` VALUES ('437', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 17:49:32');
INSERT INTO `sys_logininfor` VALUES ('438', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 17:58:08');
INSERT INTO `sys_logininfor` VALUES ('439', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 17:58:14');
INSERT INTO `sys_logininfor` VALUES ('440', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 17:59:46');
INSERT INTO `sys_logininfor` VALUES ('441', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 17:59:48');
INSERT INTO `sys_logininfor` VALUES ('442', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 18:06:10');
INSERT INTO `sys_logininfor` VALUES ('443', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 18:06:15');
INSERT INTO `sys_logininfor` VALUES ('444', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 18:46:16');
INSERT INTO `sys_logininfor` VALUES ('445', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 18:46:25');
INSERT INTO `sys_logininfor` VALUES ('446', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 18:46:30');
INSERT INTO `sys_logininfor` VALUES ('447', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 18:47:01');
INSERT INTO `sys_logininfor` VALUES ('448', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 18:47:03');
INSERT INTO `sys_logininfor` VALUES ('449', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 21:26:35');
INSERT INTO `sys_logininfor` VALUES ('450', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 22:34:04');
INSERT INTO `sys_logininfor` VALUES ('451', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 22:35:45');
INSERT INTO `sys_logininfor` VALUES ('452', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 22:35:51');
INSERT INTO `sys_logininfor` VALUES ('453', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 23:22:51');
INSERT INTO `sys_logininfor` VALUES ('454', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-19 23:25:54');
INSERT INTO `sys_logininfor` VALUES ('455', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-19 23:26:00');
INSERT INTO `sys_logininfor` VALUES ('456', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-20 01:25:17');
INSERT INTO `sys_logininfor` VALUES ('457', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-20 01:25:20');
INSERT INTO `sys_logininfor` VALUES ('458', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 18:56:57');
INSERT INTO `sys_logininfor` VALUES ('459', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-24 18:57:39');
INSERT INTO `sys_logininfor` VALUES ('460', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 19:01:29');
INSERT INTO `sys_logininfor` VALUES ('461', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 19:34:04');
INSERT INTO `sys_logininfor` VALUES ('462', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 22:22:01');
INSERT INTO `sys_logininfor` VALUES ('463', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 23:20:02');
INSERT INTO `sys_logininfor` VALUES ('464', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-24 23:33:21');
INSERT INTO `sys_logininfor` VALUES ('465', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-24 23:33:23');
INSERT INTO `sys_logininfor` VALUES ('466', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 00:34:14');
INSERT INTO `sys_logininfor` VALUES ('467', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 00:34:35');
INSERT INTO `sys_logininfor` VALUES ('468', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 00:34:43');
INSERT INTO `sys_logininfor` VALUES ('469', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 00:53:15');
INSERT INTO `sys_logininfor` VALUES ('470', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 00:53:21');
INSERT INTO `sys_logininfor` VALUES ('471', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 01:15:51');
INSERT INTO `sys_logininfor` VALUES ('472', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 01:15:53');
INSERT INTO `sys_logininfor` VALUES ('473', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 01:20:47');
INSERT INTO `sys_logininfor` VALUES ('474', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 01:20:54');
INSERT INTO `sys_logininfor` VALUES ('475', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 01:58:31');
INSERT INTO `sys_logininfor` VALUES ('476', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 01:58:33');
INSERT INTO `sys_logininfor` VALUES ('477', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 02:38:42');
INSERT INTO `sys_logininfor` VALUES ('478', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 02:38:44');
INSERT INTO `sys_logininfor` VALUES ('479', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 03:10:19');
INSERT INTO `sys_logininfor` VALUES ('480', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 03:10:25');
INSERT INTO `sys_logininfor` VALUES ('481', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 03:16:16');
INSERT INTO `sys_logininfor` VALUES ('482', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 03:16:18');
INSERT INTO `sys_logininfor` VALUES ('483', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 03:45:18');
INSERT INTO `sys_logininfor` VALUES ('484', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 03:45:20');
INSERT INTO `sys_logininfor` VALUES ('485', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 03:55:46');
INSERT INTO `sys_logininfor` VALUES ('486', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 03:55:47');
INSERT INTO `sys_logininfor` VALUES ('487', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 03:58:43');
INSERT INTO `sys_logininfor` VALUES ('488', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 03:58:45');
INSERT INTO `sys_logininfor` VALUES ('489', 'admin', '192.168.31.146', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 04:38:28');
INSERT INTO `sys_logininfor` VALUES ('490', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 11:55:58');
INSERT INTO `sys_logininfor` VALUES ('491', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 11:59:55');
INSERT INTO `sys_logininfor` VALUES ('492', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 12:00:00');
INSERT INTO `sys_logininfor` VALUES ('493', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 13:53:20');
INSERT INTO `sys_logininfor` VALUES ('494', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 13:53:28');
INSERT INTO `sys_logininfor` VALUES ('495', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 13:53:34');
INSERT INTO `sys_logininfor` VALUES ('496', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 17:42:42');
INSERT INTO `sys_logininfor` VALUES ('497', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 17:43:25');
INSERT INTO `sys_logininfor` VALUES ('498', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 17:43:27');
INSERT INTO `sys_logininfor` VALUES ('499', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 17:46:54');
INSERT INTO `sys_logininfor` VALUES ('500', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 17:47:00');
INSERT INTO `sys_logininfor` VALUES ('501', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 17:47:12');
INSERT INTO `sys_logininfor` VALUES ('502', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 17:47:14');
INSERT INTO `sys_logininfor` VALUES ('503', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 17:48:16');
INSERT INTO `sys_logininfor` VALUES ('504', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 17:48:22');
INSERT INTO `sys_logininfor` VALUES ('505', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 18:00:58');
INSERT INTO `sys_logininfor` VALUES ('506', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 18:01:00');
INSERT INTO `sys_logininfor` VALUES ('507', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 19:03:36');
INSERT INTO `sys_logininfor` VALUES ('508', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 19:03:41');
INSERT INTO `sys_logininfor` VALUES ('509', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 19:09:05');
INSERT INTO `sys_logininfor` VALUES ('510', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 19:09:07');
INSERT INTO `sys_logininfor` VALUES ('511', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 19:10:28');
INSERT INTO `sys_logininfor` VALUES ('512', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 19:10:30');
INSERT INTO `sys_logininfor` VALUES ('513', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 19:13:23');
INSERT INTO `sys_logininfor` VALUES ('514', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 19:13:29');
INSERT INTO `sys_logininfor` VALUES ('515', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 19:13:39');
INSERT INTO `sys_logininfor` VALUES ('516', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 19:13:40');
INSERT INTO `sys_logininfor` VALUES ('517', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-01-25 19:18:40');
INSERT INTO `sys_logininfor` VALUES ('518', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:11:45');
INSERT INTO `sys_logininfor` VALUES ('519', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-01-25 22:13:20');
INSERT INTO `sys_logininfor` VALUES ('520', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 22:21:56');
INSERT INTO `sys_logininfor` VALUES ('521', 'jg626566', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '1', '用户不存在/密码错误', '2026-01-25 22:22:03');
INSERT INTO `sys_logininfor` VALUES ('522', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:22:25');
INSERT INTO `sys_logininfor` VALUES ('523', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 22:29:04');
INSERT INTO `sys_logininfor` VALUES ('524', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:29:06');
INSERT INTO `sys_logininfor` VALUES ('525', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '退出成功', '2026-01-25 22:31:01');
INSERT INTO `sys_logininfor` VALUES ('526', 'admin', '127.0.0.1', '内网IP', 'Chrome 13', 'Windows 10', '0', '登录成功', '2026-01-25 22:31:03');
INSERT INTO `sys_logininfor` VALUES ('527', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 22:42:18');
INSERT INTO `sys_logininfor` VALUES ('528', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:42:26');
INSERT INTO `sys_logininfor` VALUES ('529', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 22:50:53');
INSERT INTO `sys_logininfor` VALUES ('530', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:50:59');
INSERT INTO `sys_logininfor` VALUES ('531', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 22:52:15');
INSERT INTO `sys_logininfor` VALUES ('532', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 23:33:11');
INSERT INTO `sys_logininfor` VALUES ('533', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 23:33:13');
INSERT INTO `sys_logininfor` VALUES ('534', 'admin', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '退出成功', '2026-01-25 23:34:33');
INSERT INTO `sys_logininfor` VALUES ('535', 'jg696562', '127.0.0.1', '内网IP', 'Chrome 14', 'Windows 10', '0', '登录成功', '2026-01-25 23:34:39');

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
) ENGINE=InnoDB AUTO_INCREMENT=5007 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='菜单权限表';

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('1', '系统管理', '0', '1', 'system', null, '', '', '1', '0', 'M', '0', '0', '', 'system', 'admin', '2025-10-28 02:47:08', '', null, '系统管理目录');
INSERT INTO `sys_menu` VALUES ('2', '系统监控', '0', '2', 'monitor', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2025-10-28 02:47:08', '', null, '系统监控目录');
INSERT INTO `sys_menu` VALUES ('3', '系统工具', '0', '3', 'tool', null, '', '', '1', '0', 'M', '0', '0', '', 'tool', 'admin', '2025-10-28 02:47:08', '', null, '系统工具目录');
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
INSERT INTO `sys_menu` VALUES ('2000', '养老机构', '0', '2', 'pension', null, null, null, '1', '0', 'M', '0', '0', '', 'peoples', 'admin', '2025-11-03 16:03:36', '', null, '养老机构管理');
INSERT INTO `sys_menu` VALUES ('2001', '首页', '2000', '1', 'dashboard', 'pension/dashboard/index', null, null, '1', '0', 'C', '0', '0', 'pension:dashboard:view', 'dashboard', 'admin', '2025-11-03 16:03:36', '', null, '养老机构首页');
INSERT INTO `sys_menu` VALUES ('2010', '机构管理', '2000', '2', 'institution', null, null, null, '1', '0', 'M', '0', '0', '', 'chart', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:11:43', '养老机构管理');
INSERT INTO `sys_menu` VALUES ('2011', '机构入驻申请', '2010', '1', 'apply', 'pension/institution/apply', null, null, '1', '0', 'C', '0', '0', 'pension:institution:apply', 'edit', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2012', '机构入驻列表', '2010', '2', 'list', 'pension/institution/institutionApplyList', null, null, '1', '0', 'C', '0', '0', 'pension:institution:list', 'list', 'admin', '2025-11-03 16:03:36', '', null, '展示已提交的入驻申请列表，包括名称、注册资金、所属街道、社会统一信用代码、机构备案号、机构联系人、成立日期、兴办机构等。可以维护相关信息、申请解除监管');
INSERT INTO `sys_menu` VALUES ('2013', '公示信息维护', '2010', '3', 'publicity', 'pension/institution/publicityManage', null, null, '1', '0', 'C', '0', '0', 'pension:publicity:list', 'documentation', 'admin', '2025-11-03 16:03:36', '', null, '养老机构入驻成功后，维护公示信息用于对外公示。包括养老机构名称、统一信用代码、机构备案号、地址、评级、占地面积、建筑面积、床位数、收住对象能力要求、收费标准、监管账户、机构简介、特色服务、机构图片等');
INSERT INTO `sys_menu` VALUES ('2014', 'query', '2012', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:institution:query', '#', 'admin', '2025-11-10 03:10:23', '', null, '');
INSERT INTO `sys_menu` VALUES ('2015', 'edit', '2012', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:institution:edit', '#', 'admin', '2025-11-10 03:10:23', '', null, '');
INSERT INTO `sys_menu` VALUES ('2016', 'add', '2012', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:institution:add', '#', 'admin', '2025-11-10 03:10:23', '', null, '');
INSERT INTO `sys_menu` VALUES ('2017', 'remove', '2012', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:institution:remove', '#', 'admin', '2025-11-10 03:10:23', '', null, '');
INSERT INTO `sys_menu` VALUES ('2018', 'export', '2012', '5', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:institution:export', '#', 'admin', '2025-11-10 03:10:23', '', null, '');
INSERT INTO `sys_menu` VALUES ('2020', '床位管理', '2000', '3', 'bed', null, null, null, '1', '0', 'M', '0', '0', '', 'people', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:12:01', '床位信息管理');
INSERT INTO `sys_menu` VALUES ('2021', '床位列表', '2020', '1', 'list', 'pension/bed/list', null, null, '1', '0', 'C', '0', '0', 'elder:bed:list', 'list', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2030', '入住管理', '2000', '4', 'elder', null, null, null, '1', '0', 'M', '0', '0', '', 'user', 'admin', '2025-11-03 16:03:36', '', null, '老人入住管理');
INSERT INTO `sys_menu` VALUES ('2031', '入住人列表', '2030', '1', 'list', 'pension/elder/list', null, null, '1', '0', 'C', '0', '0', 'elder:resident:list', 'list', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2032', '新增入住', '2030', '2', 'checkin', 'pension/elder/checkin', null, null, '1', '0', 'C', '0', '0', 'elder:checkin:list', 'edit', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-11 12:24:44', '');
INSERT INTO `sys_menu` VALUES ('2040', '订单管理', '2000', '5', 'order', null, null, null, '1', '0', 'M', '0', '0', '', 'job', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:12:15', '订单信息管理');
INSERT INTO `sys_menu` VALUES ('2041', '订单列表', '2040', '1', 'list', 'pension/order/orderInfo/index', null, null, '1', '0', 'C', '0', '0', 'order:info:list', 'list', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2050', '押金管理', '2000', '6', 'deposit', null, null, null, '1', '0', 'M', '0', '0', '', 'money', 'admin', '2025-11-03 16:03:36', '', null, '押金使用管理');
INSERT INTO `sys_menu` VALUES ('2051', '押金使用申请', '2050', '1', 'apply', 'pension/deposit/apply', null, null, '1', '0', 'C', '0', '0', 'pension:deposit:apply', 'edit', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2052', '押金使用列表', '2050', '2', 'list', 'pension/deposit/list', null, null, '1', '0', 'C', '0', '0', 'pension:deposit:list', 'list', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2060', '资金划拨', '2000', '7', 'fund', null, null, null, '1', '0', 'M', '0', '0', '', 'skill', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:12:31', '资金划拨管理');
INSERT INTO `sys_menu` VALUES ('2061', '资金划拨申请', '2060', '1', 'apply', 'pension/fund/transfer/index', null, null, '1', '0', 'C', '0', '0', 'pension:fund:apply', 'edit', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2062', '资金划拨记录', '2060', '2', 'record', 'pension/fund/record/index', null, null, '1', '0', 'C', '0', '0', 'pension:fund:record', 'list', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2070', '银行对账', '2000', '8', 'bank', null, null, null, '1', '0', 'M', '0', '0', '', 'edit', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:12:49', '银行流水对账');
INSERT INTO `sys_menu` VALUES ('2071', '监管账户流水', '2070', '1', 'supervision', 'pension/bank/supervision', null, null, '1', '0', 'C', '0', '0', 'pension:bank:supervision', 'document', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2072', '收单交易流水', '2070', '2', 'payment', 'pension/bank/payment', null, null, '1', '0', 'C', '0', '0', 'pension:bank:payment', 'document', 'admin', '2025-11-03 16:03:36', '', null, '');
INSERT INTO `sys_menu` VALUES ('2080', '公告查询', '2000', '9', 'announcement', 'pension/announcement/index', null, null, '1', '0', 'C', '0', '0', 'pension:announcement:view', 'button', 'admin', '2025-11-03 16:03:36', 'admin', '2025-11-03 16:13:00', '查看公告通知');
INSERT INTO `sys_menu` VALUES ('2090', '投诉建议', '2000', '10', 'feedback', 'pension/feedback/index', null, null, '1', '0', 'C', '0', '0', 'pension:feedback:view', 'message', 'admin', '2025-11-03 16:03:36', '', null, '投诉建议管理');
INSERT INTO `sys_menu` VALUES ('2100', '老人管理', '0', '9', 'elder', null, null, null, '1', '0', 'M', '0', '0', '', 'peoples', 'admin', '2025-10-28 16:00:00', '', null, '老人管理功能模块');
INSERT INTO `sys_menu` VALUES ('2101', '老人信息', '2100', '1', 'info', 'elder/info/index', null, null, '1', '0', 'C', '0', '0', 'elder:info:list', 'user', 'admin', '2025-10-28 16:00:00', '', null, '老人信息管理菜单');
INSERT INTO `sys_menu` VALUES ('2103', '入住申请', '2100', '3', 'checkin', 'elder/checkin/index', null, null, '1', '0', 'C', '0', '0', 'elder:checkin:list', 'component', 'admin', '2025-10-28 16:00:00', '', null, '入住申请管理菜单');
INSERT INTO `sys_menu` VALUES ('2111', '老人信息查询', '2101', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2112', '老人信息新增', '2101', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2113', '老人信息修改', '2101', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2114', '老人信息删除', '2101', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2115', '老人信息导出', '2101', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:info:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2121', '床位查询', '2021', '1', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2122', '床位新增', '2021', '2', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2123', '床位修改', '2021', '3', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2124', '床位删除', '2021', '4', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('2125', '床位导出', '2021', '5', '', '', null, null, '1', '0', 'F', '0', '0', 'elder:bed:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
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
INSERT INTO `sys_menu` VALUES ('2200', '幻灯片管理', '1', '10', 'banner', 'system/banner/index', null, '', '1', '0', 'C', '0', '0', 'system:banner:list', 'example', 'admin', '2025-12-24 12:12:22', 'admin', '2025-12-28 23:33:11', '幻灯片管理菜单');
INSERT INTO `sys_menu` VALUES ('2201', '幻灯片查询', '2200', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'system:banner:query', '#', 'admin', '2025-12-24 12:12:33', '', null, '查询幻灯片详情');
INSERT INTO `sys_menu` VALUES ('2202', '幻灯片新增', '2200', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'system:banner:add', '#', 'admin', '2025-12-24 12:12:33', '', null, '新增幻灯片');
INSERT INTO `sys_menu` VALUES ('2203', '幻灯片修改', '2200', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'system:banner:edit', '#', 'admin', '2025-12-24 12:12:33', '', null, '修改幻灯片');
INSERT INTO `sys_menu` VALUES ('2204', '幻灯片删除', '2200', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'system:banner:remove', '#', 'admin', '2025-12-24 12:12:33', '', null, '删除幻灯片');
INSERT INTO `sys_menu` VALUES ('3000', '民政监管', '0', '2', 'supervision', null, '', '', '1', '0', 'M', '0', '0', '', 'monitor', 'admin', '2025-11-03 16:51:30', '', null, '民政监管目录');
INSERT INTO `sys_menu` VALUES ('3100', '机构管理', '3000', '1', 'institution', null, '', '', '1', '0', 'M', '0', '0', '', 'build', 'admin', '2025-11-03 16:51:30', '', null, '机构管理目录');
INSERT INTO `sys_menu` VALUES ('3101', '批量导入', '3100', '1', 'batchImport', 'supervision/institution/batchImport', '', '', '1', '0', 'C', '0', '0', 'supervision:institution:batchImport', 'upload', 'admin', '2025-11-03 16:51:30', '', null, '机构批量导入');
INSERT INTO `sys_menu` VALUES ('3102', '机构查询', '3100', '4', 'queryList', 'supervision/institution/queryList', '', '', '1', '0', 'C', '0', '0', 'supervision:institution:query', 'search', 'admin', '2025-11-03 16:51:30', '', null, '机构信息查询功能，展示每个机构的当前信息，包括名称、预收服务费、预收押金、预收会员费、监管开户信息、入驻状态等');
INSERT INTO `sys_menu` VALUES ('3103', '机构解除监管审批', '3100', '5', 'releaseSupervision', 'supervision/institution/releaseSupervision', '', '', '1', '0', 'C', '0', '0', 'supervision:release:list', 'money', 'admin', '2025-11-03 16:51:30', '', null, '民政部门审批机构解除监管申请，批准后银行将监管资金划拨至机构基本账户');
INSERT INTO `sys_menu` VALUES ('3104', '机构评级', '3100', '6', 'ratingList', 'supervision/institution/ratingList', '', '', '1', '0', 'C', '0', '0', 'supervision:institution:rating', 'star', 'admin', '2025-11-03 16:51:30', '', null, '机构评级管理');
INSERT INTO `sys_menu` VALUES ('3105', '黑名单管理', '3100', '7', 'blacklistList', 'supervision/institution/blacklistList', '', '', '1', '0', 'C', '0', '0', 'supervision:institution:blacklist', 'bug', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:53:43', '黑名单管理');
INSERT INTO `sys_menu` VALUES ('3106', '机构入驻审批', '3100', '2', 'approval', 'supervision/institution/approval', null, '', '1', '0', 'C', '0', '0', 'supervision:institution:approval', 'form', 'admin', '2025-11-04 00:14:00', '', null, '');
INSERT INTO `sys_menu` VALUES ('3107', '机构信息管理', '3100', '3', 'infoManage', 'supervision/institution/infoManage', null, '', '1', '0', 'C', '1', '0', 'supervision:institution:info', 'list', 'admin', '2025-11-04 00:16:35', 'admin', '2025-11-04 00:20:58', '机构信息管理功能');
INSERT INTO `sys_menu` VALUES ('3200', '预警核验', '3000', '2', 'warning', null, '', '', '1', '0', 'M', '0', '0', '', 'bug', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:53:08', '预警核验目录');
INSERT INTO `sys_menu` VALUES ('3201', '预警列表', '3200', '1', 'index', 'supervision/warning/index', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:list', 'list', 'admin', '2025-11-03 16:51:30', '', null, '预警列表');
INSERT INTO `sys_menu` VALUES ('3202', '费用超额预警', '3200', '2', 'feeExcess', 'supervision/warning/feeExcess', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:feeExcess', 'money', 'admin', '2025-11-03 16:51:30', '', null, '费用超额预警');
INSERT INTO `sys_menu` VALUES ('3203', '押金超额预警', '3200', '3', 'depositExcess', 'supervision/warning/depositExcess', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:depositExcess', 'drag', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:53:55', '押金超额预警');
INSERT INTO `sys_menu` VALUES ('3204', '入驻超额预警', '3200', '4', 'checkinExcess', 'supervision/warning/checkinExcess', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:checkinExcess', 'peoples', 'admin', '2025-11-03 16:51:30', '', null, '入驻超额预警');
INSERT INTO `sys_menu` VALUES ('3205', '监管超额预警', '3200', '5', 'supervisionExcess', 'supervision/warning/supervisionExcess', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:supervisionExcess', 'druid', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:03', '监管超额预警');
INSERT INTO `sys_menu` VALUES ('3206', '风险保证金预警', '3200', '6', 'riskDepositExcess', 'supervision/warning/riskDepositExcess', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:riskDepositExcess', 'date', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:12', '风险保证金预警');
INSERT INTO `sys_menu` VALUES ('3207', '大额支付预警', '3200', '7', 'largePayment', 'supervision/warning/largePayment', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:largePayment', 'download', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:19', '大额支付预警');
INSERT INTO `sys_menu` VALUES ('3208', '突发风险预警', '3200', '8', 'emergencyRisk', 'supervision/warning/emergencyRisk', '', '', '1', '0', 'C', '0', '0', 'supervision:warning:emergencyRisk', 'bug', 'admin', '2025-11-03 16:51:30', '', null, '突发风险预警');
INSERT INTO `sys_menu` VALUES ('3300', '账户管理', '3000', '3', 'account', null, '', '', '1', '0', 'M', '0', '0', '', 'money', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:53:20', '账户管理目录');
INSERT INTO `sys_menu` VALUES ('3301', '机构账户查询', '3300', '1', 'institutionAccount', 'supervision/account/institutionAccount', '', '', '1', '0', 'C', '0', '0', 'supervision:account:institution', 'list', 'admin', '2025-11-03 16:51:30', '', null, '机构账户查询');
INSERT INTO `sys_menu` VALUES ('3302', '会员费管理', '3300', '2', 'memberFee', 'supervision/account/memberFee', '', '', '1', '0', 'C', '0', '0', 'supervision:account:memberFee', 'money', 'admin', '2025-11-03 16:51:30', '', null, '会员费管理');
INSERT INTO `sys_menu` VALUES ('3303', '监管账户维护', '3300', '3', 'supervisionAccount', 'supervision/account/supervisionAccount', '', '', '1', '0', 'C', '0', '0', 'supervision:account:supervision', 'dict', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:29', '监管账户维护');
INSERT INTO `sys_menu` VALUES ('3304', '订单管理', '3300', '4', 'orderList', 'supervision/account/orderList', '', '', '1', '0', 'C', '0', '0', 'supervision:account:order', 'list', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:38', '订单管理');
INSERT INTO `sys_menu` VALUES ('3305', '账户余额', '3300', '5', 'balanceList', 'supervision/account/balanceList', '', '', '1', '0', 'C', '0', '0', 'supervision:account:balance', 'question', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:45', '账户余额查询');
INSERT INTO `sys_menu` VALUES ('3400', '资金管理', '3000', '4', 'fund', null, '', '', '1', '0', 'M', '0', '0', '', 'money', 'admin', '2025-11-03 16:51:30', '', null, '资金管理目录');
INSERT INTO `sys_menu` VALUES ('3401', '资金记录', '3400', '1', 'recordList', 'supervision/fund/recordList', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:record', 'list', 'admin', '2025-11-03 16:51:30', '', null, '资金记录查看');
INSERT INTO `sys_menu` VALUES ('3402', '资金流动明细', '3400', '2', 'flowDetail', 'supervision/fund/flowDetail', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:flow', 'chart', 'admin', '2025-11-03 16:51:30', '', null, '资金流动明细');
INSERT INTO `sys_menu` VALUES ('3403', '分配规则', '3400', '3', 'allocationRule', 'supervision/fund/allocationRule', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:allocation', 'email', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:54:56', '分配规则配置');
INSERT INTO `sys_menu` VALUES ('3404', '资金统计', '3400', '4', 'statistics', 'supervision/fund/statistics', '', '', '1', '0', 'C', '0', '0', 'supervision:fund:statistics', 'chart', 'admin', '2025-11-03 16:51:30', '', null, '资金统计概览');
INSERT INTO `sys_menu` VALUES ('3500', '公告管理', '3000', '5', 'announcement', null, '', '', '1', '0', 'M', '0', '0', '', 'message', 'admin', '2025-11-03 16:51:30', '', null, '公告管理目录');
INSERT INTO `sys_menu` VALUES ('3501', '公告列表', '3500', '1', 'list', 'supervision/announcement/list', '', '', '1', '0', 'C', '0', '0', 'supervision:announcement:list', 'list', 'admin', '2025-11-03 16:51:30', '', null, '公告列表');
INSERT INTO `sys_menu` VALUES ('3502', '发布公告', '3500', '2', 'add', 'supervision/announcement/add', '', '', '1', '0', 'C', '0', '0', 'supervision:announcement:add', 'edit', 'admin', '2025-11-03 16:51:30', '', null, '发布公告');
INSERT INTO `sys_menu` VALUES ('3503', '公告模板', '3500', '3', 'template', 'supervision/announcement/template', '', '', '1', '0', 'C', '0', '0', 'supervision:announcement:template', 'button', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:55:07', '公告模板管理');
INSERT INTO `sys_menu` VALUES ('3504', '阅读统计', '3500', '4', 'statistics', 'supervision/announcement/statistics', '', '', '1', '0', 'C', '0', '0', 'supervision:announcement:statistics', 'chart', 'admin', '2025-11-03 16:51:30', '', null, '公告阅读统计');
INSERT INTO `sys_menu` VALUES ('3600', '反馈管理', '3000', '6', 'feedback', null, '', '', '1', '0', 'M', '1', '0', '', 'question', 'admin', '2025-11-03 16:51:30', 'admin', '2025-12-22 23:44:09', '反馈管理目录');
INSERT INTO `sys_menu` VALUES ('3601', '反馈列表', '3600', '1', 'list', 'supervision/feedback/list', '', '', '1', '0', 'C', '1', '0', 'supervision:feedback:list', 'list', 'admin', '2025-11-03 16:51:30', '', null, '反馈列表');
INSERT INTO `sys_menu` VALUES ('3602', '反馈统计', '3600', '2', 'statistics', 'supervision/feedback/statistics', '', '', '1', '0', 'C', '1', '0', 'supervision:feedback:statistics', 'chart', 'admin', '2025-11-03 16:51:30', '', null, '反馈统计');
INSERT INTO `sys_menu` VALUES ('3603', '热点反馈', '3600', '3', 'hot', 'supervision/feedback/hot', '', '', '1', '0', 'C', '1', '0', 'supervision:feedback:hot', 'star', 'admin', '2025-11-03 16:51:30', '', null, '热点反馈');
INSERT INTO `sys_menu` VALUES ('3604', '满意度评价', '3600', '4', 'satisfaction', 'supervision/feedback/satisfaction', '', '', '1', '0', 'C', '1', '0', 'supervision:feedback:satisfaction', 'education', 'admin', '2025-11-03 16:51:30', 'admin', '2025-11-03 17:55:19', '满意度评价');
INSERT INTO `sys_menu` VALUES ('4000', '数据统计', '0', '8', 'bigscreen', null, null, '', '1', '0', 'M', '0', '0', '', 'chart', 'admin', '2025-10-29 22:21:46', 'admin', '2026-01-19 01:20:56', '数据大屏菜单');
INSERT INTO `sys_menu` VALUES ('4003', '预警监控大屏', '4000', '3', 'http://localhost:8080/screen/warning-monitor.html', 'bigscreen/warning-monitor', null, '', '0', '0', 'C', '0', '0', 'bigscreen:warning:view', 'user', 'admin', '2025-10-30 01:20:52', 'admin', '2025-10-30 01:48:48', '');
INSERT INTO `sys_menu` VALUES ('4004', 'List', '3106', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'supervision:institution:list', '#', '', '2025-11-10 12:13:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('4005', 'Query', '3106', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'supervision:institution:query', '#', '', '2025-11-10 12:13:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('4006', 'Approve', '3106', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'supervision:institution:approve', '#', '', '2025-11-10 12:13:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('4007', 'Reject', '3106', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'supervision:institution:reject', '#', '', '2025-11-10 12:13:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('4008', 'Export', '3106', '5', '', null, null, '', '1', '0', 'F', '0', '0', 'supervision:institution:export', '#', '', '2025-11-10 12:13:24', '', null, '');
INSERT INTO `sys_menu` VALUES ('4009', '公示信息查询', '2013', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:publicity:query', '#', 'admin', '2025-11-11 02:37:21', '', null, '');
INSERT INTO `sys_menu` VALUES ('4010', '公示信息新增', '2013', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:publicity:add', '#', 'admin', '2025-11-11 02:37:50', '', null, '');
INSERT INTO `sys_menu` VALUES ('4012', '公示信息修改', '2013', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:publicity:edit', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4013', '公示信息删除', '2013', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:publicity:remove', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4014', '公示信息导出', '2013', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:publicity:export', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4015', '发布公示', '2013', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:publicity:publish', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4016', '取消公示', '2013', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:publicity:unpublish', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4017', '床位导入', '2021', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:bed:import', '#', 'admin', '2025-11-11 02:45:20', '', null, '');
INSERT INTO `sys_menu` VALUES ('4018', '押金使用申请查询', '2051', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:query', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4019', '押金使用申请新增', '2051', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:add', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4020', '押金使用申请修改', '2051', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:edit', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4021', '押金使用申请删除', '2051', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:remove', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4022', '押金使用申请导出', '2051', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:export', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4023', '押金使用申请审批', '2051', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:approve', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4024', '押金使用申请撤回', '2051', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:withdraw', '#', 'admin', '2025-11-16 01:09:43', '', null, '');
INSERT INTO `sys_menu` VALUES ('4025', '押金使用列表查询', '2052', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:query', '#', 'admin', '2025-11-16 01:10:31', '', null, '');
INSERT INTO `sys_menu` VALUES ('4026', '押金使用列表导出', '2052', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:export', '#', 'admin', '2025-11-16 01:10:31', '', null, '');
INSERT INTO `sys_menu` VALUES ('4027', '订单列表查询', '2041', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:query', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4028', '订单列表新增', '2041', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:add', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4029', '订单列表修改', '2041', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:edit', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4030', '订单列表删除', '2041', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:remove', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4031', '订单列表导出', '2041', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:export', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4032', '订单支付', '2041', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:pay', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4033', '订单取消', '2041', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:cancel', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4034', '生成订单', '2041', '8', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:generate', '#', 'admin', '2025-11-16 01:21:22', '', null, '');
INSERT INTO `sys_menu` VALUES ('4035', '入住人列表查询', '2031', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:query', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4036', '入住人新增', '2031', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:add', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4037', '入住人删除', '2031', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:remove', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4038', '入住人导出', '2031', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:export', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4039', '入住人续费', '2031', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:renew', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4040', '入住人支付', '2031', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:payment', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4041', '入住人导入', '2031', '7', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:import', '#', 'admin', '2025-11-16 01:21:35', '', null, '');
INSERT INTO `sys_menu` VALUES ('4042', '入住申请查询', '2032', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:query', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4043', '入住申请新增', '2032', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:add', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4044', '入住申请修改', '2032', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:edit', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4045', '入住申请删除', '2032', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:remove', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4046', '入住申请导出', '2032', '5', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:export', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4047', '入住申请审批', '2032', '6', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:approve', '#', 'admin', '2025-11-16 01:23:02', '', null, '');
INSERT INTO `sys_menu` VALUES ('4048', '入住人列表权限', '2031', '0', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:resident:list', '#', 'admin', '2025-11-16 01:46:11', '', null, '');
INSERT INTO `sys_menu` VALUES ('4049', '入住申请列表权限', '2032', '0', '#', '', null, '', '1', '0', 'F', '0', '0', 'elder:checkin:list', '#', 'admin', '2025-11-16 01:46:57', '', null, '');
INSERT INTO `sys_menu` VALUES ('4050', '订单列表权限', '2041', '0', '#', '', null, '', '1', '0', 'F', '0', '0', 'order:info:list', '#', 'admin', '2025-11-16 01:47:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4051', '设施图标配置', '0', '3', 'icon', 'pension/facility/icon/index', '', '', '1', '0', 'C', '0', '0', 'pension:facility:icon', 'tree', 'admin', '2025-12-07 20:02:06', 'admin', '2026-01-25 17:43:17', '');
INSERT INTO `sys_menu` VALUES ('4052', '设施图标查询', '4051', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'pension:facility:icon:list', '#', 'admin', '2025-12-07 20:02:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4053', '设施图标新增', '4051', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'pension:facility:icon:add', '#', 'admin', '2025-12-07 20:02:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4054', '设施图标修改', '4051', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'pension:facility:icon:edit', '#', 'admin', '2025-12-07 20:02:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4055', '设施图标删除', '4051', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'pension:facility:icon:remove', '#', 'admin', '2025-12-07 20:02:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4056', '设施图标导出', '4051', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'pension:facility:icon:export', '#', 'admin', '2025-12-07 20:02:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('4057', '订单明细查询', '2041', '9', '', null, null, '', '1', '0', 'F', '0', '0', 'order:item:query', '#', 'admin', '2025-12-14 02:33:16', 'admin', '2025-12-14 02:33:16', '查看订单详情时需要查询订单明细');
INSERT INTO `sys_menu` VALUES ('4058', '支付记录查询', '2041', '10', '', null, null, '', '1', '0', 'F', '0', '0', 'payment:record:list', '#', 'admin', '2025-12-14 02:33:16', 'admin', '2025-12-14 02:33:16', '查看订单详情时需要查询支付记录');
INSERT INTO `sys_menu` VALUES ('4059', '押金申请审批', '3400', '5', 'depositApproval', 'supervision/deposit/approval', null, '', '1', '0', 'C', '0', '0', 'pension:deposit:list', 'money', 'admin', '2025-12-15 02:09:23', 'admin', '2025-12-15 02:09:23', '民政???管-押金申请审批页面');
INSERT INTO `sys_menu` VALUES ('4060', '押金审批查询', '4059', '1', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:query', '#', 'admin', '2025-12-15 02:09:23', 'admin', '2025-12-15 02:09:23', '');
INSERT INTO `sys_menu` VALUES ('4061', '押金审批通过', '4059', '2', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:approve', '#', 'admin', '2025-12-15 02:09:23', 'admin', '2025-12-15 02:09:23', '');
INSERT INTO `sys_menu` VALUES ('4062', '押金审批拒绝', '4059', '3', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:reject', '#', 'admin', '2025-12-15 02:09:23', 'admin', '2025-12-15 02:09:23', '');
INSERT INTO `sys_menu` VALUES ('4063', '押金审批导出', '4059', '4', '#', '', null, '', '1', '0', 'F', '0', '0', 'pension:deposit:export', '#', 'admin', '2025-12-15 02:09:23', 'admin', '2025-12-15 02:09:23', '');
INSERT INTO `sys_menu` VALUES ('4064', '机构评价管理', '3000', '7', 'review', 'supervision/feedback/review/index', '', '', '1', '0', 'C', '0', '0', null, 'star', 'admin', '2025-12-22 23:27:22', 'admin', '2025-12-22 23:27:22', '民政监管-反馈管理-机构评价管理页面');
INSERT INTO `sys_menu` VALUES ('4065', '机构评价查询', '4064', '1', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:list', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4066', '机构评价详情', '4064', '2', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:query', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4067', '评价审核通过', '4064', '3', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:approve', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4068', '评价审核拒绝', '4064', '4', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:reject', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4069', '评价删除', '4064', '5', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:remove', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4070', '评价导出', '4064', '6', '', '', '', '', '1', '0', 'F', '0', '0', 'supervision:review:export', '#', 'admin', '2025-12-22 23:27:29', 'admin', '2025-12-22 23:27:29', '');
INSERT INTO `sys_menu` VALUES ('4071', '预约管理', '2000', '100', 'visit', 'pension/visit/index', null, '', '1', '0', 'C', '0', '0', 'pension:visit:list', 'shopping', 'admin', '2025-12-23 01:44:40', 'admin', '2025-12-23 01:44:40', '预约参观管理');
INSERT INTO `sys_menu` VALUES ('4072', '预约管理查询', '4071', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:visit:query', '#', 'admin', '2025-12-23 01:44:40', '', null, '');
INSERT INTO `sys_menu` VALUES ('4073', '标记完成', '4071', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:visit:complete', '#', 'admin', '2025-12-23 01:44:40', '', null, '');
INSERT INTO `sys_menu` VALUES ('4074', '预约管理导出', '4071', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'pension:visit:export', '#', 'admin', '2025-12-23 01:44:40', '', null, '');
INSERT INTO `sys_menu` VALUES ('4075', '数据大屏', '4000', '1', '/bigscreen', '', null, '', '1', '0', 'C', '0', '0', 'data:screen:list', 'chart', 'admin', '2026-01-19 01:41:24', 'admin', '2026-01-19 01:41:24', '数据大屏菜单');
INSERT INTO `sys_menu` VALUES ('4076', '订单审核', '2041', '10', '', null, null, '', '1', '0', 'F', '0', '0', 'order:info:audit', '#', 'admin', '2026-01-19 17:45:36', '', null, '订单审核权限');
INSERT INTO `sys_menu` VALUES ('4077', '审核通过', '2041', '11', '', null, null, '', '1', '0', 'F', '0', '0', 'order:info:approve', '#', 'admin', '2026-01-19 17:45:36', '', null, '订单审核通过');
INSERT INTO `sys_menu` VALUES ('4078', '审核拒绝', '2041', '12', '', null, null, '', '1', '0', 'F', '0', '0', 'order:info:reject', '#', 'admin', '2026-01-19 17:45:36', '', null, '订单审核拒绝');
INSERT INTO `sys_menu` VALUES ('5000', 'H5用户管理', '0', '11', 'h5user', null, null, '', '1', '0', 'M', '0', '0', null, 'peoples', 'admin', '2026-01-25 03:42:08', '', null, 'H5家属用户管理');
INSERT INTO `sys_menu` VALUES ('5001', '用户列表', '5000', '1', 'list', 'system/h5user/index', null, '', '1', '0', 'C', '0', '0', 'h5user:list', 'peoples', 'admin', '2026-01-25 03:42:11', 'admin', '2026-01-25 17:46:12', 'H5家属用户列表');
INSERT INTO `sys_menu` VALUES ('5002', '用户查询', '5001', '1', '', null, null, '', '1', '0', 'F', '0', '0', 'h5user:query', '#', 'admin', '2026-01-25 03:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('5003', '用户新增', '5001', '2', '', null, null, '', '1', '0', 'F', '0', '0', 'h5user:add', '#', 'admin', '2026-01-25 03:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('5004', '用户修改', '5001', '3', '', null, null, '', '1', '0', 'F', '0', '0', 'h5user:edit', '#', 'admin', '2026-01-25 03:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('5005', '用户删除', '5001', '4', '', null, null, '', '1', '0', 'F', '0', '0', 'h5user:remove', '#', 'admin', '2026-01-25 03:42:15', '', null, '');
INSERT INTO `sys_menu` VALUES ('5006', '重置密码', '5001', '5', '', null, null, '', '1', '0', 'F', '0', '0', 'h5user:resetPwd', '#', 'admin', '2026-01-25 03:42:15', '', null, '');

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
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='通知公告表';

-- ----------------------------
-- Records of sys_notice
-- ----------------------------
INSERT INTO `sys_notice` VALUES ('1', '温馨提醒：资金监管系统即将上线', '2', 0xE696B0E78988E69CACE58685E5AEB9, '0', 'admin', '2025-10-28 02:47:08', 'admin', '2026-01-19 15:31:14', '管理员');
INSERT INTO `sys_notice` VALUES ('11', '这是一条测试内容', '2', 0x3C703EE6B58BE8AF95E9809AE79FA5E585ACE5918A3C2F703E, '0', 'admin', '2026-01-19 15:32:14', '', null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=578 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='操作日志记录';

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
INSERT INTO `sys_oper_log` VALUES ('132', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2010,\"menuName\":\"机构管理\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":2000,\"path\":\"institution\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:11:43', '35');
INSERT INTO `sys_oper_log` VALUES ('133', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"people\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2020,\"menuName\":\"床位管理\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":2000,\"path\":\"bed\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:12:01', '26');
INSERT INTO `sys_oper_log` VALUES ('134', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"job\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2040,\"menuName\":\"订单管理\",\"menuType\":\"M\",\"orderNum\":5,\"params\":{},\"parentId\":2000,\"path\":\"order\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:12:16', '21');
INSERT INTO `sys_oper_log` VALUES ('135', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"skill\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2060,\"menuName\":\"资金划拨\",\"menuType\":\"M\",\"orderNum\":7,\"params\":{},\"parentId\":2000,\"path\":\"fund\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:12:31', '30');
INSERT INTO `sys_oper_log` VALUES ('136', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2070,\"menuName\":\"银行对账\",\"menuType\":\"M\",\"orderNum\":8,\"params\":{},\"parentId\":2000,\"path\":\"bank\",\"perms\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:12:49', '24');
INSERT INTO `sys_oper_log` VALUES ('137', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"pension/announcement/index\",\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"button\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2080,\"menuName\":\"公告查询\",\"menuType\":\"C\",\"orderNum\":9,\"params\":{},\"parentId\":2000,\"path\":\"announcement\",\"perms\":\"pension:announcement:view\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 16:13:00', '21');
INSERT INTO `sys_oper_log` VALUES ('138', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"bug\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3200,\"menuName\":\"预警核验\",\"menuType\":\"M\",\"orderNum\":2,\"params\":{},\"parentId\":3000,\"path\":\"warning\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:53:08', '63');
INSERT INTO `sys_oper_log` VALUES ('139', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"money\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3300,\"menuName\":\"账户管理\",\"menuType\":\"M\",\"orderNum\":3,\"params\":{},\"parentId\":3000,\"path\":\"account\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:53:20', '11');
INSERT INTO `sys_oper_log` VALUES ('140', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/institution/blacklistList\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"bug\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3105,\"menuName\":\"黑名单管理\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":3100,\"path\":\"blacklistList\",\"perms\":\"supervision:institution:blacklist\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:53:43', '20');
INSERT INTO `sys_oper_log` VALUES ('141', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/warning/depositExcess\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"drag\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3203,\"menuName\":\"押金超额预警\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3200,\"path\":\"depositExcess\",\"perms\":\"supervision:warning:depositExcess\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:53:55', '26');
INSERT INTO `sys_oper_log` VALUES ('142', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/warning/supervisionExcess\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"druid\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3205,\"menuName\":\"监管超额预警\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":3200,\"path\":\"supervisionExcess\",\"perms\":\"supervision:warning:supervisionExcess\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:03', '21');
INSERT INTO `sys_oper_log` VALUES ('143', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/warning/riskDepositExcess\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"date\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3206,\"menuName\":\"风险保证金预警\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":3200,\"path\":\"riskDepositExcess\",\"perms\":\"supervision:warning:riskDepositExcess\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:12', '25');
INSERT INTO `sys_oper_log` VALUES ('144', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/warning/largePayment\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"download\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3207,\"menuName\":\"大额支付预警\",\"menuType\":\"C\",\"orderNum\":7,\"params\":{},\"parentId\":3200,\"path\":\"largePayment\",\"perms\":\"supervision:warning:largePayment\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:19', '11');
INSERT INTO `sys_oper_log` VALUES ('145', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/account/supervisionAccount\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"dict\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3303,\"menuName\":\"监管账户维护\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3300,\"path\":\"supervisionAccount\",\"perms\":\"supervision:account:supervision\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:29', '20');
INSERT INTO `sys_oper_log` VALUES ('146', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/account/orderList\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"list\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3304,\"menuName\":\"订单管理\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":3300,\"path\":\"orderList\",\"perms\":\"supervision:account:order\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:38', '14');
INSERT INTO `sys_oper_log` VALUES ('147', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/account/balanceList\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"question\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3305,\"menuName\":\"账户余额\",\"menuType\":\"C\",\"orderNum\":5,\"params\":{},\"parentId\":3300,\"path\":\"balanceList\",\"perms\":\"supervision:account:balance\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:45', '20');
INSERT INTO `sys_oper_log` VALUES ('148', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/fund/allocationRule\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"email\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3403,\"menuName\":\"分配规则\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3400,\"path\":\"allocationRule\",\"perms\":\"supervision:fund:allocation\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:54:56', '21');
INSERT INTO `sys_oper_log` VALUES ('149', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/announcement/template\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"button\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3503,\"menuName\":\"公告模板\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3500,\"path\":\"template\",\"perms\":\"supervision:announcement:template\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:55:07', '20');
INSERT INTO `sys_oper_log` VALUES ('150', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/feedback/satisfaction\",\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"education\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3604,\"menuName\":\"满意度评价\",\"menuType\":\"C\",\"orderNum\":4,\"params\":{},\"parentId\":3600,\"path\":\"satisfaction\",\"perms\":\"supervision:feedback:satisfaction\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-03 17:55:19', '25');
INSERT INTO `sys_oper_log` VALUES ('151', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"supervision/institution/infoManage\",\"createTime\":\"2025-11-04 00:16:35\",\"icon\":\"list\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3107,\"menuName\":\"机构信息管理\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":3100,\"path\":\"infoManage\",\"perms\":\"supervision:institution:info\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-04 00:20:58', '30');
INSERT INTO `sys_oper_log` VALUES ('152', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-04 00:39:58', '33');
INSERT INTO `sys_oper_log` VALUES ('153', '订单主表', '1', 'com.ruoyi.web.controller.OrderInfoController.add()', 'POST', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-11-04 12:30:56\",\"orderAmount\":0,\"orderNo\":\"ORD17622306567572028\",\"orderStatus\":\"1\",\"orderType\":\"入住费\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"alipay\",\"unpaidAmount\":0}', null, '1', '\r\n### Error updating database.  Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'order_type\' at row 1\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,                                                                 order_amount,             paid_amount,             order_status,             payment_method,                                                                                                        create_time )           values ( ?,             ?,                                                                 ?,             ?,             ?,             ?,                                                                                                        ? )\r\n### Cause: com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'order_type\' at row 1\n; Data truncation: Data too long for column \'order_type\' at row 1; nested exception is com.mysql.cj.jdbc.exceptions.MysqlDataTruncation: Data truncation: Data too long for column \'order_type\' at row 1', '2025-11-04 12:30:56', '107');
INSERT INTO `sys_oper_log` VALUES ('154', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"测试地址\",\"applyTime\":\"2025-11-09 22:37:42\",\"bedCount\":3,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-09 22:37:42\",\"creditCode\":\"91410100MA46TE2X81\",\"fixedAssets\":5.0,\"institutionId\":4,\"institutionName\":\"阿斯蒂芬\",\"institutionType\":\"nursing_home\",\"params\":{},\"recordNumber\":\"1231323\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":4.0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-09 22:37:42', '44');
INSERT INTO `sys_oper_log` VALUES ('155', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"\",\"applyTime\":\"2025-11-09 22:49:07\",\"bedCount\":0,\"contactPerson\":\"\",\"contactPhone\":\"\",\"createTime\":\"2025-11-09 22:49:07\",\"creditCode\":\"\",\"fixedAssets\":0.0,\"institutionName\":\"\",\"institutionType\":\"\",\"params\":{},\"recordNumber\":\"\",\"registeredAddress\":\"\",\"registeredCapital\":0.0,\"status\":\"0\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'institution_name\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\PensionInstitutionMapper.xml]\r\n### The error may involve com.ruoyi.mapper.PensionInstitutionMapper.insertPensionInstitution-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into pension_institution          ( registered_capital,                                                                                                                     bed_count,                                       fixed_assets,                                       status,             apply_time,                                                                              create_time )           values ( ?,                                                                                                                     ?,                                       ?,                                       ?,             ?,                                                                              ? )\r\n### Cause: java.sql.SQLException: Field \'institution_name\' doesn\'t have a default value\n; Field \'institution_name\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'institution_name\' doesn\'t have a default value', '2025-11-09 22:49:07', '128');
INSERT INTO `sys_oper_log` VALUES ('156', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"21213\",\"applyTime\":\"2025-11-09 22:55:53\",\"bedCount\":4,\"contactPerson\":\"ASD\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-09 22:55:53\",\"creditCode\":\"91410100MA45TE2X81\",\"fixedAssets\":4.0,\"institutionId\":5,\"institutionName\":\"阿斯蒂芬\",\"institutionType\":\"nursing_home\",\"params\":{},\"recordNumber\":\"4564564\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":3.0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-09 22:55:53', '17');
INSERT INTO `sys_oper_log` VALUES ('157', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-09 23:34:27\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/09/banner2_20251109233422A002.png\",\"bedCount\":8,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/09/01_20251109233419A001.jpg\",\"contactPerson\":\"稍等\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-09 23:34:27\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":6.0,\"institutionId\":6,\"institutionName\":\"测试1\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"54545566\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":2.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/09/banner3_20251109233425A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-09 23:34:27', '34');
INSERT INTO `sys_oper_log` VALUES ('158', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-09 23:37:09\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/09/banner2_20251109233704A005.png\",\"bedCount\":9,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/09/01_20251109233701A004.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-09 23:37:09\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"3000\",\"fixedAssets\":10.0,\"institutionId\":7,\"institutionName\":\"森岛帆高\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"456456465\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":3.0,\"responsibleAddress\":\"水电费\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"艾师傅\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/09/banner3_20251109233707A006.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-09 23:37:09', '17');
INSERT INTO `sys_oper_log` VALUES ('159', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-09 23:55:49\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/09/banner2_20251109235543A002.png\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/09/01_20251109235540A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-09 23:55:49\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":8,\"institutionName\":\"买买买\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45646456\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":4.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/09/banner3_20251109235546A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-09 23:55:49', '24');
INSERT INTO `sys_oper_log` VALUES ('160', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 00:30:36\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110003027A002.png\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110003022A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:30:36\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-01\",\"feeRange\":\"2000\",\"fixedAssets\":5.0,\"institutionId\":9,\"institutionName\":\"121来来来\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":12.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110003031A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:30:36', '103');
INSERT INTO `sys_oper_log` VALUES ('161', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 00:36:32\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110003027A002.png\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110003022A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:36:32\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-01\",\"feeRange\":\"2000\",\"fixedAssets\":5.0,\"institutionId\":10,\"institutionName\":\"121来来来\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":12.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110003031A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:36:33', '15');
INSERT INTO `sys_oper_log` VALUES ('162', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 00:42:22\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110004216A002.png\",\"bedCount\":3,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110004213A001.jpg\",\"contactPerson\":\"暗室逢灯\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:42:22\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":6.0,\"institutionId\":11,\"institutionName\":\"来了来了\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"阿斯蒂芬\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":3.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110004220A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:42:22', '94');
INSERT INTO `sys_oper_log` VALUES ('163', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 00:43:20\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110004216A002.png\",\"bedCount\":3,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110004213A001.jpg\",\"contactPerson\":\"暗室逢灯\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:43:20\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":6.0,\"institutionId\":12,\"institutionName\":\"来了来了\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"阿斯蒂芬\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":3.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110004220A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:43:20', '28');
INSERT INTO `sys_oper_log` VALUES ('164', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 00:44:43\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110004439A005.png\",\"bedCount\":3,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110004437A004.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:44:43\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-03\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":13,\"institutionName\":\"男男女女\",\"institutionType\":\"service_center\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"2123132\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":2.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110004442A006.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:44:43', '19');
INSERT INTO `sys_oper_log` VALUES ('165', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110005422A002.png\",\"bedCount\":6,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110005419A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 00:54:27\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000-5000\",\"fixedAssets\":7.0,\"institutionId\":14,\"institutionName\":\"八百标兵奔北坡\",\"institutionType\":\"nursing_home\",\"organizer\":\"暗室逢灯\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":4.0,\"responsibleAddress\":\"水电费\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110005425A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 00:54:28', '105');
INSERT INTO `sys_oper_log` VALUES ('166', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110005422A002.png\",\"bedCount\":6,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110005419A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000-5000\",\"fixedAssets\":7.0,\"institutionId\":14,\"institutionName\":\"八百标兵奔北坡\",\"institutionType\":\"nursing_home\",\"organizer\":\"暗室逢灯\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":4.0,\"responsibleAddress\":\"水电费\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"阿斯蒂芬\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110005425A003.png\",\"updateTime\":\"2025-11-10 01:01:23\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 01:01:23', '31');
INSERT INTO `sys_oper_log` VALUES ('167', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110005422A002.png\",\"bankAccount\":\"456456\",\"bedCount\":6,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110005419A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000-5000\",\"fixedAssets\":7.0,\"institutionId\":14,\"institutionName\":\"八百标兵奔北坡\",\"institutionType\":\"nursing_home\",\"organizer\":\"暗室逢灯\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":4.0,\"responsibleAddress\":\"水电费\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"阿斯蒂芬\",\"superviseAccount\":\"4564564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110005425A003.png\",\"updateTime\":\"2025-11-10 01:11:05\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 01:11:05', '33');
INSERT INTO `sys_oper_log` VALUES ('168', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 02:08:41\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110005422A002.png\",\"bankAccount\":\"456456\",\"bedCount\":6,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110005419A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000-5000\",\"fixedAssets\":7.0,\"institutionId\":14,\"institutionName\":\"八百标兵奔北坡\",\"institutionType\":\"nursing_home\",\"organizer\":\"暗室逢灯\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬阿斯蒂芬\",\"registeredCapital\":4.0,\"responsibleAddress\":\"水电费\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"superviseAccount\":\"4564564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110005425A003.png\",\"updateTime\":\"2025-11-10 02:08:41\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:08:41', '37');
INSERT INTO `sys_oper_log` VALUES ('169', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/13', '127.0.0.1', '内网IP', '13', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:12:07', '13');
INSERT INTO `sys_oper_log` VALUES ('170', '角色管理', '1', 'com.ruoyi.web.controller.system.SysRoleController.add()', 'POST', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2013,2020,2021,2030,2031,2032,2040,2041,2050,2051,2052,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:20:16', '41');
INSERT INTO `sys_oper_log` VALUES ('171', '部门管理', '2', 'com.ruoyi.web.controller.system.SysDeptController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/dept', '127.0.0.1', '内网IP', '{\"ancestors\":\"0\",\"children\":[],\"deptId\":100,\"deptName\":\"测试管理员\",\"email\":\"ry@qq.com\",\"leader\":\"测试管理员\",\"orderNum\":0,\"params\":{},\"parentId\":0,\"phone\":\"15888888888\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:20:57', '15');
INSERT INTO `sys_oper_log` VALUES ('172', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', '0', null, '2025-11-10 02:21:48', '5');
INSERT INTO `sys_oper_log` VALUES ('173', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-10-28 02:47:08\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"baidu.com\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"visible\":\"1\"}', '{\"msg\":\"修改菜单\'若依官网\'失败，地址必须以http(s)://开头\",\"code\":500}', '0', null, '2025-11-10 02:22:10', '7');
INSERT INTO `sys_oper_log` VALUES ('174', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-10-28 02:47:08\",\"icon\":\"guide\",\"isCache\":\"0\",\"isFrame\":\"0\",\"menuId\":4,\"menuName\":\"若依官网\",\"menuType\":\"M\",\"orderNum\":4,\"params\":{},\"parentId\":0,\"path\":\"http://baidu.com\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"1\",\"updateBy\":\"admin\",\"visible\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:22:21', '25');
INSERT INTO `sys_oper_log` VALUES ('175', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"菜单已分配,不允许删除\",\"code\":601}', '0', null, '2025-11-10 02:22:24', '6');
INSERT INTO `sys_oper_log` VALUES ('176', '用户管理', '1', 'com.ruoyi.web.controller.system.SysUserController.add()', 'POST', '1', 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"nickName\":\"机构测试员\",\"params\":{},\"postIds\":[],\"roleIds\":[100],\"status\":\"0\",\"userId\":101,\"userName\":\"jigouuser\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:23:12', '67');
INSERT INTO `sys_oper_log` VALUES ('177', '岗位管理', '1', 'com.ruoyi.web.controller.system.SysPostController.add()', 'POST', '1', 'admin', '研发部门', '/system/post', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"flag\":false,\"params\":{},\"postCode\":\"jgglgw\",\"postId\":5,\"postName\":\"机构管理岗位\",\"postSort\":0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:23:52', '41');
INSERT INTO `sys_oper_log` VALUES ('178', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'jigouuser', null, '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110025927A002.jpg\",\"bankAccount\":\"45645646\",\"bedCount\":5,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110025925A001.png\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 02:59:30\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-01\",\"feeRange\":\"2000\",\"fixedAssets\":8.0,\"institutionId\":15,\"institutionName\":\"重中之重\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45465\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":2.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"水电费\",\"superviseAccount\":\"456456456\",\"supervisionAgreement\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 02:59:30', '44');
INSERT INTO `sys_oper_log` VALUES ('179', '个人信息', '2', 'com.ruoyi.web.controller.system.SysProfileController.updateProfile()', 'PUT', '1', 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"超管\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 03:01:33', '28');
INSERT INTO `sys_oper_log` VALUES ('180', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'jigouuser', null, '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110025927A002.jpg\",\"bankAccount\":\"45645646\",\"bedCount\":5,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110025925A001.png\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-01\",\"feeRange\":\"2000\",\"fixedAssets\":8.0,\"institutionId\":15,\"institutionName\":\"重中之重\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45465\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":2.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"水电费\",\"superviseAccount\":\"456456456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110031308A001.png\",\"updateTime\":\"2025-11-10 03:13:10\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 03:13:10', '36');
INSERT INTO `sys_oper_log` VALUES ('181', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'jigouuser', null, '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 03:13:31\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110025927A002.jpg\",\"bankAccount\":\"45645646\",\"bedCount\":5,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110025925A001.png\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-01\",\"feeRange\":\"2000\",\"fixedAssets\":8.0,\"institutionId\":15,\"institutionName\":\"重中之重\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45465\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":2.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"水电费\",\"superviseAccount\":\"456456456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110031308A001.png\",\"updateTime\":\"2025-11-10 03:13:31\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 03:13:31', '15');
INSERT INTO `sys_oper_log` VALUES ('182', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110032850A002.png\",\"bankAccount\":\"64546\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110032847A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-10 03:28:54\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45645\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":6.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110032853A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 03:28:54', '123');
INSERT INTO `sys_oper_log` VALUES ('183', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-11-10 03:29:10\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110032850A002.png\",\"bankAccount\":\"64546\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110032847A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45645\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":6.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110032853A003.png\",\"updateTime\":\"2025-11-10 03:29:10\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 03:29:10', '12');
INSERT INTO `sys_oper_log` VALUES ('184', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-10-29 04:49:09\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[3000,3100,3101,3106,3107,3102,3103,3104,3105,3200,3201,3202,3203,3204,3205,3206,3207,3208,3300,3301,3302,3303,3304,3305,3400,3401,3402,3403,3404,3500,3501,3502,3503,3504,3600,3601,3602,3603,3604],\"params\":{},\"remark\":\"民政监管部门角色\",\"roleId\":3,\"roleKey\":\"supervision\",\"roleName\":\"民政监管员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:24:45', '232');
INSERT INTO `sys_oper_log` VALUES ('185', '用户管理', '1', 'com.ruoyi.web.controller.system.SysUserController.add()', 'POST', '1', 'admin', '研发部门', '/system/user', '127.0.0.1', '内网IP', '{\"admin\":false,\"createBy\":\"admin\",\"deptId\":103,\"nickName\":\"民政监管-李宝\",\"params\":{},\"postIds\":[],\"roleIds\":[],\"status\":\"0\",\"userId\":102,\"userName\":\"jguser\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:25:56', '86');
INSERT INTO `sys_oper_log` VALUES ('186', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-10-29 04:49:09\",\"dataScope\":\"2\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[3000,3100,3101,3106,3107,3102,3103,3104,3105,3200,3201,3202,3203,3204,3205,3206,3207,3208,3300,3301,3302,3303,3304,3305,3400,3401,3402,3403,3404,3500,3501,3502,3503,3504,3600,3601,3602,3603,3604],\"params\":{},\"remark\":\"民政监管部门角色\",\"roleId\":3,\"roleKey\":\"supervision\",\"roleName\":\"民政监管员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:29:51', '41');
INSERT INTO `sys_oper_log` VALUES ('187', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.dataScope()', 'PUT', '1', 'admin', '研发部门', '/system/role/dataScope', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-10-29 04:49:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"deptIds\":[],\"flag\":false,\"menuCheckStrictly\":true,\"params\":{},\"remark\":\"民政监管部门角色\",\"roleId\":3,\"roleKey\":\"supervision\",\"roleName\":\"民政监管员\",\"roleSort\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:31:04', '40');
INSERT INTO `sys_oper_log` VALUES ('188', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-10-29 04:49:09\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[1,100,1000,1001,1002,1003,1004,1005,1006,101,1007,1008,1009,1010,1011,102,1012,1013,1014,1015,103,1016,1017,1018,1019,104,1020,1021,1022,1023,1024,105,1025,1026,1027,1028,1029,106,1030,1031,1032,1033,1034,107,1035,1036,1037,1038,108,500,1039,1040,1041,501,1042,1043,1044,1045,3000,3100,3101,3106,3107,3102,3103,3104,3105,3200,3201,3202,3203,3204,3205,3206,3207,3208,3300,3301,3302,3303,3304,3305,3400,3401,3402,3403,3404,3500,3501,3502,3503,3504,3600,3601,3602,3603,3604],\"params\":{},\"remark\":\"民政监管部门角色\",\"roleId\":3,\"roleKey\":\"supervision\",\"roleName\":\"民政监管员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:34:51', '32');
INSERT INTO `sys_oper_log` VALUES ('189', '角色管理', '4', 'com.ruoyi.web.controller.system.SysRoleController.selectAuthUserAll()', 'PUT', '1', 'admin', '研发部门', '/system/role/authUser/selectAll', '127.0.0.1', '内网IP', '{\"roleId\":\"3\",\"userIds\":\"102\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:38:22', '5');
INSERT INTO `sys_oper_log` VALUES ('190', '用户管理', '3', 'com.ruoyi.web.controller.system.SysUserController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/user/100', '127.0.0.1', '内网IP', '[100]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 11:38:31', '40');
INSERT INTO `sys_oper_log` VALUES ('191', '用户头像', '2', 'com.ruoyi.web.controller.system.SysProfileController.avatar()', 'POST', '1', 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', null, '1', '文件[favicon.ico]后缀[ico]不正确，请上传[bmp, gif, jpg, jpeg, png]格式', '2025-11-10 15:05:06', '5');
INSERT INTO `sys_oper_log` VALUES ('192', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/16', '127.0.0.1', '内网IP', '16', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-10 17:02:20', '48');
INSERT INTO `sys_oper_log` VALUES ('193', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"wenwang\",\"contactPhone\":\"18539279011\",\"createBy\":\"admin\",\"createTime\":\"2025-11-10 22:36:18\",\"creditCode\":\"91410101MA45TE2X81\",\"fixedAssets\":null,\"institutionId\":17,\"institutionName\":\"测试\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"279011\",\"institutionId\":17,\"institutionName\":\"测试\",\"userName\":\"jg279011\",\"message\":\"机构账号创建成功\"}}', '0', null, '2025-11-10 22:36:18', '90');
INSERT INTO `sys_oper_log` VALUES ('194', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"陈飞\",\"contactPhone\":\"13855555555\",\"creditCode\":\"91410100MA45TE2X81\",\"fixedAssets\":null,\"institutionName\":\"港湾养老\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"统一信用代码已存在: 91410100MA45TE2X81\",\"code\":500}', '0', null, '2025-11-10 23:10:00', '4');
INSERT INTO `sys_oper_log` VALUES ('195', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"陈飞\",\"contactPhone\":\"13855555555\",\"createBy\":\"admin\",\"createTime\":\"2025-11-10 23:10:06\",\"creditCode\":\"91410100MA45TE2X91\",\"fixedAssets\":null,\"institutionId\":18,\"institutionName\":\"港湾养老\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"555555\",\"institutionId\":18,\"institutionName\":\"港湾养老\",\"userName\":\"jg555555\",\"message\":\"机构账号创建成功\"}}', '0', null, '2025-11-10 23:10:06', '76');
INSERT INTO `sys_oper_log` VALUES ('196', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"林秒\",\"contactPhone\":\"18565656565\",\"createBy\":\"admin\",\"createTime\":\"2025-11-10 23:23:09\",\"creditCode\":\"91410100MA45TE8989\",\"fixedAssets\":null,\"institutionId\":19,\"institutionName\":\"浏览\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"656565\",\"institutionId\":19,\"institutionName\":\"浏览\",\"userName\":\"jg656565\",\"message\":\"机构账号创建成功\"}}', '0', null, '2025-11-10 23:23:09', '143');
INSERT INTO `sys_oper_log` VALUES ('197', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care\",\"buildingArea\":10,\"createTime\":\"2025-11-11 00:38:28\",\"environmentImgs\":\"/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111003827A001.png\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"params\":{},\"publicId\":1}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 00:38:28', '81');
INSERT INTO `sys_oper_log` VALUES ('198', '保存机构申请草稿', '1', 'com.ruoyi.web.controller.PensionInstitutionController.saveDraft()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/draft', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"approvalCertificate\":\"\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-11-11 00:49:15\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":20,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"4\",\"street\":\"admin第二个机构\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 00:49:15', '50');
INSERT INTO `sys_oper_log` VALUES ('199', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 01:44:47', '35');
INSERT INTO `sys_oper_log` VALUES ('200', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111003827A001.png,/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111014458A001.png,/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111014502A002.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-11-11 01:45:04\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 01:45:04', '17');
INSERT INTO `sys_oper_log` VALUES ('201', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 01:45:21', '29');
INSERT INTO `sys_oper_log` VALUES ('202', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111003827A001.png,/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111014458A001.png,/profile/upload/2025/11/11/养老机构预收费资金监管平台功能清单_Sheet1_20251111014502A002.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-11-11 01:45:29\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 01:45:29', '17');
INSERT INTO `sys_oper_log` VALUES ('203', '菜单管理', '1', 'com.ruoyi.web.controller.system.SysMenuController.add()', 'POST', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"公示信息查询\",\"menuType\":\"F\",\"orderNum\":1,\"params\":{},\"parentId\":2013,\"perms\":\"pension:publicity:query\",\"status\":\"0\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 02:37:21', '25');
INSERT INTO `sys_oper_log` VALUES ('204', '菜单管理', '1', 'com.ruoyi.web.controller.system.SysMenuController.add()', 'POST', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createBy\":\"admin\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuName\":\"公示信息新增\",\"menuType\":\"F\",\"orderNum\":2,\"params\":{},\"parentId\":2013,\"perms\":\"pension:publicity:add\",\"status\":\"0\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 02:37:50', '19');
INSERT INTO `sys_oper_log` VALUES ('205', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,2020,2021,2030,2031,2032,2040,2041,2050,2051,2052,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 02:38:44', '22');
INSERT INTO `sys_oper_log` VALUES ('206', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2030,2031,2032,2040,2041,2050,2051,2052,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 02:49:00', '41');
INSERT INTO `sys_oper_log` VALUES ('207', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2121,2122,2123,2124,2125,4017,2030,2031,2032,2040,2041,2050,2051,2052,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 02:56:09', '44');
INSERT INTO `sys_oper_log` VALUES ('208', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":1,\"bedNumber\":\"1\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-11 03:04:24\",\"facilities\":\"12\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":2000,\"roomArea\":20,\"roomNumber\":\"12\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 03:04:24', '52');
INSERT INTO `sys_oper_log` VALUES ('209', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jigouuser', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":2,\"bedNumber\":\"2\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-11 03:16:42\",\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":3,\"params\":{},\"price\":2000,\"roomNumber\":\"45\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 03:16:42', '22');
INSERT INTO `sys_oper_log` VALUES ('210', '床位信息', '5', 'com.ruoyi.web.controller.BedInfoController.export()', 'POST', '1', 'admin', '研发部门', '/elder/bed/export', '127.0.0.1', '内网IP', '{\"pageSize\":\"10\",\"pageNum\":\"1\"}', null, '0', null, '2025-11-11 11:28:09', '38');
INSERT INTO `sys_oper_log` VALUES ('211', '床位信息', '6', 'com.ruoyi.web.controller.BedInfoController.importData()', 'POST', '1', 'admin', '研发部门', '/elder/bed/importData', '127.0.0.1', '内网IP', '{\"institutionId\":\"16\",\"updateSupport\":\"0\"}', '{\"msg\":\"恭喜您,数据已全部导入成功!共 1 条,数据如下:<br/>1、床位 101-01 导入成功\",\"code\":200}', '0', null, '2025-11-11 11:59:14', '139');
INSERT INTO `sys_oper_log` VALUES ('212', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":3,\"bedNumber\":\"01\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-11-11 11:59:15\",\"facilities\":\"电视、空调、衣柜\",\"floorNumber\":1,\"hasBalcony\":\"\",\"hasBathroom\":\"\",\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"params\":{},\"price\":2000,\"roomArea\":25,\"roomNumber\":\"101\",\"updateBy\":\"\",\"updateTime\":\"2025-11-11 11:59:25\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 11:59:25', '11');
INSERT INTO `sys_oper_log` VALUES ('213', '床位信息', '6', 'com.ruoyi.web.controller.BedInfoController.importData()', 'POST', '1', 'admin', '研发部门', '/elder/bed/importData', '127.0.0.1', '内网IP', '{\"institutionId\":\"16\",\"updateSupport\":\"0\"}', '{\"msg\":\"恭喜您,数据已全部导入成功!共 1 条,数据如下:<br/>1、床位 101-2 导入成功\",\"code\":200}', '0', null, '2025-11-11 12:01:48', '26');
INSERT INTO `sys_oper_log` VALUES ('214', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":3,\"bedNumber\":\"01\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-11-11 11:59:15\",\"facilities\":\"电视、空调、衣柜\",\"floorNumber\":1,\"hasBalcony\":\"N\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"params\":{},\"price\":2000,\"roomArea\":25,\"roomNumber\":\"101\",\"updateBy\":\"\",\"updateTime\":\"2025-11-11 12:02:32\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 12:02:32', '9');
INSERT INTO `sys_oper_log` VALUES ('215', '床位信息', '6', 'com.ruoyi.web.controller.BedInfoController.importData()', 'POST', '1', 'admin', '研发部门', '/elder/bed/importData', '127.0.0.1', '内网IP', '{\"institutionId\":\"16\",\"updateSupport\":\"0\"}', '{\"msg\":\"恭喜您,数据已全部导入成功!共 1 条,数据如下:<br/>1、床位 101-3 导入成功\",\"code\":200}', '0', null, '2025-11-11 12:06:54', '22');
INSERT INTO `sys_oper_log` VALUES ('216', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"pension/elder/checkin\",\"createTime\":\"2025-11-03 16:03:36\",\"icon\":\"edit\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2032,\"menuName\":\"新增入住\",\"menuType\":\"C\",\"orderNum\":2,\"params\":{},\"parentId\":2030,\"path\":\"checkin\",\"perms\":\"pension:elder:checkin\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 12:24:44', '13');
INSERT INTO `sys_oper_log` VALUES ('217', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":36,\"bedId\":4,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-10\",\"depositAmount\":5000,\"elderName\":\"张三测试\",\"emergencyContact\":\"陈飞\",\"emergencyPhone\":\"18525656556\",\"feeDescription\":\"\",\"finalAmount\":30000,\"gender\":\"1\",\"healthStatus\":\"\",\"idCard\":\"412829198908160073\",\"memberFee\":7000,\"monthCount\":13,\"monthlyFee\":2000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 15:40:37', '80');
INSERT INTO `sys_oper_log` VALUES ('218', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"bedId\":5,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":12,\"elderName\":\"两列测试\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18565656565\",\"feeDescription\":\"\",\"finalAmount\":25082,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829198908160073\",\"memberFee\":5000,\"monthCount\":10,\"monthlyFee\":2007,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\ElderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,             birth_date,             age,             phone,             address,             emergency_contact,             emergency_phone,             health_status,             care_level,                                       status,             create_by,             create_time )           values ( ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,                                       ?,             ?,             ? )\r\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\n; Duplicate entry \'412829198908160073\' for key \'uk_id_card\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'', '2025-11-11 15:48:52', '89');
INSERT INTO `sys_oper_log` VALUES ('219', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"bedId\":5,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":12,\"elderName\":\"两列测试\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18565656565\",\"feeDescription\":\"\",\"finalAmount\":25082,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829198908160073\",\"memberFee\":5000,\"monthCount\":10,\"monthlyFee\":2007,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', null, '1', '该老人已在住,身份证号: 412829198908160073', '2025-11-11 15:51:16', '14');
INSERT INTO `sys_oper_log` VALUES ('220', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"bedId\":5,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":12,\"elderName\":\"两列测试\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18565656565\",\"feeDescription\":\"\",\"finalAmount\":25082,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829198908160079\",\"memberFee\":5000,\"monthCount\":10,\"monthlyFee\":2007,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 15:51:23', '37');
INSERT INTO `sys_oper_log` VALUES ('221', '订单主表', '2', 'com.ruoyi.web.controller.OrderInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"bedId\":4,\"bedNumber\":\"2\",\"createBy\":\"admin\",\"createTime\":\"2025-11-11 15:40:37\",\"elderId\":4,\"elderName\":\"张三测试\",\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"orderAmount\":30000,\"orderDate\":\"2025-11-11\",\"orderId\":1,\"orderNo\":\"ORD1762846837366\",\"orderStatus\":\"1\",\"orderType\":\"1\",\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"later\",\"remark\":\"\",\"roomNumber\":\"101\",\"unpaidAmount\":30000,\"updateBy\":\"\",\"updateTime\":\"2025-11-11 15:54:43\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 15:54:43', '27');
INSERT INTO `sys_oper_log` VALUES ('222', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"bedId\":3,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":50000,\"elderName\":\"张三01\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18539279801\",\"feeDescription\":\"阿斯蒂芬\",\"finalAmount\":70000,\"gender\":\"1\",\"healthStatus\":\"是\",\"idCard\":\"412829198908160070\",\"memberFee\":5000,\"monthCount\":11,\"monthlyFee\":2000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 16:33:07', '111');
INSERT INTO `sys_oper_log` VALUES ('223', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":37,\"bedId\":1,\"birthDate\":\"1988-01-07\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-04\",\"depositAmount\":50000,\"elderName\":\"大马猴\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"13646565656\",\"feeDescription\":\"\",\"finalAmount\":128000,\"gender\":\"1\",\"healthStatus\":\"\",\"idCard\":\"412829198536545263\",\"memberFee\":60000,\"monthCount\":9,\"monthlyFee\":2000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 16:48:03', '48');
INSERT INTO `sys_oper_log` VALUES ('224', '订单主表', '2', 'com.ruoyi.web.controller.OrderInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/order/info', '127.0.0.1', '内网IP', '{\"bedId\":3,\"bedNumber\":\"01\",\"createBy\":\"admin\",\"createTime\":\"2025-11-11 16:33:07\",\"discountAmount\":7000,\"elderId\":7,\"elderName\":\"张三01\",\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"monthCount\":11,\"orderAmount\":70000,\"orderDate\":\"2025-11-11\",\"orderId\":3,\"orderNo\":\"ORD1762849987063\",\"orderStatus\":\"1\",\"orderType\":\"1\",\"originalAmount\":77000,\"paidAmount\":0,\"params\":{},\"paymentMethod\":\"cash\",\"remark\":\"阿斯蒂芬\",\"roomNumber\":\"101\",\"unpaidAmount\":70000,\"updateBy\":\"\",\"updateTime\":\"2025-11-11 17:13:45\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 17:13:45', '9');
INSERT INTO `sys_oper_log` VALUES ('225', '老人信息', '2', 'com.ruoyi.web.controller.PensionElderController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/elder/info', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"careLevel\":\"1\",\"elderId\":7,\"elderName\":\"张三01\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18539279801\",\"gender\":\"1\",\"healthStatus\":\"45\",\"idCard\":\"412829198908160070\",\"params\":{},\"phone\":\"18539279011\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 18:37:22', '20');
INSERT INTO `sys_oper_log` VALUES ('226', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"amount\":1000,\"elderId\":7,\"elderName\":\"张三01\",\"paymentMethod\":\"cash\",\"renewType\":\"deposit\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 18:50:20', '72');
INSERT INTO `sys_oper_log` VALUES ('227', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/4', '127.0.0.1', '内网IP', '4 {\"params\":{},\"paymentMethod\":\"alipay\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-11 22:27:22\",\"elderId\":8,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":4,\"orderNo\":\"ORD1762850883224\",\"params\":{},\"paymentAmount\":128000.00,\"paymentId\":2,\"paymentMethod\":\"alipay\",\"paymentNo\":\"PAY20251111222722184\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-11 22:27:22\",\"transactionId\":\"TXN20251111222722184\"}}', '0', null, '2025-11-11 22:27:22', '128');
INSERT INTO `sys_oper_log` VALUES ('228', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":6,\"bedNumber\":\"789\",\"bedStatus\":\"0\",\"bedType\":\"2\",\"createTime\":\"2025-11-11 23:02:07\",\"floorNumber\":7,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":3000,\"roomArea\":100,\"roomNumber\":\"78\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:02:07', '95');
INSERT INTO `sys_oper_log` VALUES ('229', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":47,\"bedId\":6,\"birthDate\":\"1978-04-05\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":5000,\"elderName\":\"燕子\",\"emergencyContact\":\"艳子\",\"emergencyPhone\":\"18539255555\",\"feeDescription\":\"\",\"finalAmount\":37200,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829197802645565\",\"memberFee\":2200,\"monthCount\":10,\"monthlyFee\":3000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:03:41', '44');
INSERT INTO `sys_oper_log` VALUES ('230', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/6', '127.0.0.1', '内网IP', '6 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-11 23:05:16\",\"elderId\":9,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":6,\"orderNo\":\"ORD1762873421558\",\"params\":{},\"paymentAmount\":37200.00,\"paymentId\":3,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251111230516709\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-11 23:05:16\",\"transactionId\":\"TXN20251111230516709\"}}', '0', null, '2025-11-11 23:05:16', '24');
INSERT INTO `sys_oper_log` VALUES ('231', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":7,\"bedNumber\":\"545\",\"bedStatus\":\"0\",\"bedType\":\"2\",\"createTime\":\"2025-11-11 23:20:01\",\"facilities\":\"阿斯蒂芬\",\"floorNumber\":10,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":3000,\"remark\":\"阿斯蒂芬\",\"roomArea\":120,\"roomNumber\":\"2563\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:20:01', '113');
INSERT INTO `sys_oper_log` VALUES ('232', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿第三方阿斯蒂芬\",\"age\":65,\"bedId\":7,\"birthDate\":\"1960-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":2000,\"elderName\":\"继续测试\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18536952222\",\"feeDescription\":\"阿斯蒂芬阿斯蒂芬\",\"finalAmount\":37000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829196008160045\",\"memberFee\":5000,\"monthCount\":10,\"monthlyFee\":3000,\"paymentMethod\":\"later\",\"phone\":\"18539256565\",\"remark\":\"阿斯蒂芬阿斯蒂芬\",\"specialNeeds\":\"阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯顿发斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:21:21', '49');
INSERT INTO `sys_oper_log` VALUES ('233', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/7', '127.0.0.1', '内网IP', '7 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-11 23:21:56\",\"elderId\":10,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":7,\"orderNo\":\"ORD1762874481098\",\"params\":{},\"paymentAmount\":37000.00,\"paymentId\":4,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251111232156844\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-11 23:21:56\",\"transactionId\":\"TXN20251111232156844\"}}', '0', null, '2025-11-11 23:21:56', '32');
INSERT INTO `sys_oper_log` VALUES ('234', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":8,\"bedNumber\":\"02\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-11 23:37:35\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":2500,\"roomArea\":20,\"roomNumber\":\"2365\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:37:35', '19');
INSERT INTO `sys_oper_log` VALUES ('235', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":75,\"bedId\":8,\"birthDate\":\"1950-02-20\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-03\",\"depositAmount\":20000,\"elderName\":\"奇趣\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18523232323\",\"feeDescription\":\"阿斯蒂芬\",\"finalAmount\":50000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829195002205655\",\"memberFee\":5000,\"monthCount\":12,\"monthlyFee\":2500,\"paymentMethod\":\"later\",\"phone\":\"18539245454\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:39:10', '43');
INSERT INTO `sys_oper_log` VALUES ('236', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/8', '127.0.0.1', '内网IP', '8 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-11 23:39:57\",\"elderId\":11,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":8,\"orderNo\":\"ORD1762875550014\",\"params\":{},\"paymentAmount\":50000.00,\"paymentId\":5,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251111233957990\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-11 23:39:57\",\"transactionId\":\"TXN20251111233957990\"}}', '0', null, '2025-11-11 23:39:58', '28');
INSERT INTO `sys_oper_log` VALUES ('237', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":9,\"bedNumber\":\"12\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-11 23:45:24\",\"floorNumber\":10,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":2300,\"roomArea\":20,\"roomNumber\":\"45\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:45:24', '152');
INSERT INTO `sys_oper_log` VALUES ('238', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":65,\"bedId\":9,\"birthDate\":\"1960-02-20\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-11\",\"depositAmount\":2000,\"elderName\":\"前文\",\"emergencyContact\":\"阿斯\",\"emergencyPhone\":\"18536565656\",\"feeDescription\":\"阿斯蒂芬\",\"finalAmount\":30000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829196002202355\",\"memberFee\":5000,\"monthCount\":10,\"monthlyFee\":2300,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:46:21', '62');
INSERT INTO `sys_oper_log` VALUES ('239', '用户头像', '2', 'com.ruoyi.web.controller.system.SysProfileController.avatar()', 'POST', '1', 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2025/11/11/1eff83cb59164ef080c26356af4d7a54.png\",\"code\":200}', '0', null, '2025-11-11 23:50:58', '63');
INSERT INTO `sys_oper_log` VALUES ('240', '个人信息', '2', 'com.ruoyi.web.controller.system.SysProfileController.updateProfile()', 'PUT', '1', 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"超管\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-11 23:51:01', '14');
INSERT INTO `sys_oper_log` VALUES ('241', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":10,\"bedNumber\":\"6\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 00:08:50\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":200,\"roomArea\":20,\"roomNumber\":\"96\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:08:50', '26');
INSERT INTO `sys_oper_log` VALUES ('242', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":69,\"bedId\":10,\"birthDate\":\"1956-05-02\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-15\",\"depositAmount\":2000,\"elderName\":\"亲亲亲\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18539279011\",\"feeDescription\":\"\",\"finalAmount\":37000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829195605020073\",\"memberFee\":5000,\"monthCount\":15,\"monthlyFee\":2000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:10:20', '67');
INSERT INTO `sys_oper_log` VALUES ('243', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":11,\"bedNumber\":\"1\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 00:12:10\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":2500,\"roomArea\":20,\"roomNumber\":\"1212\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:12:10', '143');
INSERT INTO `sys_oper_log` VALUES ('244', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":65,\"bedId\":11,\"birthDate\":\"1960-02-20\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-12\",\"depositAmount\":50000,\"elderName\":\"匹配\",\"emergencyContact\":\"阿斯蒂芬\",\"emergencyPhone\":\"18539279011\",\"feeDescription\":\"阿斯蒂芬\",\"finalAmount\":125000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829196002205656\",\"memberFee\":50000,\"monthCount\":10,\"monthlyFee\":2500,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:13:19', '87');
INSERT INTO `sys_oper_log` VALUES ('245', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":12,\"bedNumber\":\"20\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 00:17:46\",\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":16,\"params\":{},\"price\":2000,\"roomNumber\":\"63\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:17:46', '165');
INSERT INTO `sys_oper_log` VALUES ('246', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":12,\"bedNumber\":\"20\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-11-12 00:17:46\",\"floorNumber\":2,\"hasBalcony\":\"N\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"params\":{},\"price\":2000,\"roomArea\":20,\"roomNumber\":\"63\",\"updateBy\":\"\",\"updateTime\":\"2025-11-12 00:17:59\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:17:59', '20');
INSERT INTO `sys_oper_log` VALUES ('247', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":69,\"bedId\":12,\"birthDate\":\"1956-02-20\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-12\",\"depositAmount\":20000,\"elderName\":\"啊啊啊\",\"emergencyContact\":\"是是是\",\"emergencyPhone\":\"18539279011\",\"feeDescription\":\"阿斯蒂芬\",\"finalAmount\":50000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829195602200073\",\"memberFee\":20000,\"monthCount\":10,\"monthlyFee\":2000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"艾师傅\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 00:19:09', '80');
INSERT INTO `sys_oper_log` VALUES ('248', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/12', '127.0.0.1', '内网IP', '12 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-12 00:19:34\",\"elderId\":15,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":12,\"orderNo\":\"ORD1762877949379\",\"params\":{},\"paymentAmount\":50000.00,\"paymentId\":6,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251112001934125\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-12 00:19:34\",\"transactionId\":\"TXN20251112001934128\"}}', '0', null, '2025-11-12 00:19:34', '49');
INSERT INTO `sys_oper_log` VALUES ('249', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"elderId\":15,\"monthCount\":3,\"paymentMethod\":\"cash\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\OrderInfoMapper.xml]\r\n### The error may involve com.ruoyi.mapper.OrderInfoMapper.insertOrderInfo-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into order_info          ( order_no,             order_type,             elder_id,             institution_id,                                                                 order_status,             payment_method,             payment_time,             order_date,                                       billing_cycle,                                       discount_amount,                          create_by,             create_time )           values ( ?,             ?,             ?,             ?,                                                                 ?,             ?,             ?,             ?,                                       ?,                                       ?,                          ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value\n; Field \'order_amount\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'order_amount\' doesn\'t have a default value', '2025-11-12 12:18:45', '55');
INSERT INTO `sys_oper_log` VALUES ('250', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":2000,\"elderId\":15,\"finalAmount\":5000,\"memberFee\":0,\"monthCount\":2,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 12:26:21', '63');
INSERT INTO `sys_oper_log` VALUES ('251', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":15,\"finalAmount\":2000,\"memberFee\":0,\"monthCount\":1,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 12:28:02', '22');
INSERT INTO `sys_oper_log` VALUES ('252', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":10000,\"elderId\":15,\"finalAmount\":10000,\"memberFee\":0,\"monthCount\":0,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 12:28:49', '28');
INSERT INTO `sys_oper_log` VALUES ('253', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":3000,\"elderId\":15,\"finalAmount\":2000,\"memberFee\":0,\"monthCount\":0,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 12:29:20', '34');
INSERT INTO `sys_oper_log` VALUES ('254', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"applyTime\":\"2025-11-12 22:46:45\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224631A002.png\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224628A001.png\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":20,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"45465\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"admin第二个机构\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224634A003.png\",\"updateTime\":\"2025-11-12 22:46:45\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 22:46:45', '17');
INSERT INTO `sys_oper_log` VALUES ('255', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 22:46:58', '13');
INSERT INTO `sys_oper_log` VALUES ('256', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":13,\"bedNumber\":\"56\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 22:47:36\",\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":20,\"params\":{},\"price\":1000,\"roomNumber\":\"105\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 22:47:36', '42');
INSERT INTO `sys_oper_log` VALUES ('257', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":14,\"bedNumber\":\"23\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 22:59:57\",\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":20,\"params\":{},\"price\":230,\"roomNumber\":\"632\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 22:59:57', '33');
INSERT INTO `sys_oper_log` VALUES ('258', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":15,\"bedNumber\":\"632\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 23:00:23\",\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":16,\"params\":{},\"price\":620,\"roomNumber\":\"87978\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 23:00:23', '7');
INSERT INTO `sys_oper_log` VALUES ('259', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":16,\"bedNumber\":\"323\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 23:00:46\",\"floorNumber\":2,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":20,\"params\":{},\"price\":620,\"roomArea\":20,\"roomNumber\":\"789\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 23:00:46', '5');
INSERT INTO `sys_oper_log` VALUES ('260', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":17,\"bedNumber\":\"01\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-11-12 23:01:27\",\"floorNumber\":2,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":16,\"params\":{},\"price\":620,\"roomArea\":20,\"roomNumber\":\"96\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 23:01:27', '21');
INSERT INTO `sys_oper_log` VALUES ('261', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"阿斯蒂芬\",\"age\":36,\"bedId\":13,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-11-12\",\"depositAmount\":6000,\"elderName\":\"测试第二个\",\"emergencyContact\":\"超出\",\"emergencyPhone\":\"18563652636\",\"feeDescription\":\"\",\"finalAmount\":18000,\"gender\":\"1\",\"healthStatus\":\"阿斯蒂芬\",\"idCard\":\"412829198908160052\",\"memberFee\":5000,\"monthCount\":7,\"monthlyFee\":1000,\"paymentMethod\":\"later\",\"phone\":\"18539279011\",\"remark\":\"\",\"specialNeeds\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-12 23:03:08', '57');
INSERT INTO `sys_oper_log` VALUES ('262', '入住人管理', '3', 'com.ruoyi.web.controller.PensionResidentController.delete()', 'DELETE', '1', 'admin', '研发部门', '/pension/resident/delete/3', '127.0.0.1', '内网IP', '3', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 01:19:34', '32');
INSERT INTO `sys_oper_log` VALUES ('263', '入住人管理', '3', 'com.ruoyi.web.controller.PensionResidentController.delete()', 'DELETE', '1', 'admin', '研发部门', '/pension/resident/delete/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 01:19:37', '21');
INSERT INTO `sys_oper_log` VALUES ('264', '入住人管理', '3', 'com.ruoyi.web.controller.PensionResidentController.delete()', 'DELETE', '1', 'admin', '研发部门', '/pension/resident/delete/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 01:19:40', '20');
INSERT INTO `sys_oper_log` VALUES ('265', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":7,\"finalAmount\":14000,\"memberFee\":0,\"monthCount\":7,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 01:20:23', '51');
INSERT INTO `sys_oper_log` VALUES ('266', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/17', '127.0.0.1', '内网IP', '17 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-11-13 01:42:24\",\"elderId\":16,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":20,\"operator\":\"system\",\"orderId\":17,\"orderNo\":\"ORD1762959787954\",\"params\":{},\"paymentAmount\":18000.00,\"paymentId\":7,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251113014224315\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-11-13 01:42:24\",\"transactionId\":\"TXN20251113014224315\"}}', '0', null, '2025-11-13 01:42:24', '47');
INSERT INTO `sys_oper_log` VALUES ('267', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyNo\":\"DEP1762973188200\",\"applyStatus\":\"draft\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 02:46:28\",\"params\":{},\"purpose\":\"医疗费用\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\pension\\DepositApplyMapper.xml]\r\n### The error may involve com.ruoyi.mapper.pension.DepositApplyMapper.insertDepositApply-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into deposit_apply          ( apply_no,                                                                                                        purpose,                                                    apply_status,                                                                                                                                               create_by,             create_time )           values ( ?,                                                                                                        ?,                                                    ?,                                                                                                                                               ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\n; Field \'elder_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value', '2025-11-13 02:46:28', '101');
INSERT INTO `sys_oper_log` VALUES ('268', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyNo\":\"DEP1762973205908\",\"applyStatus\":\"draft\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 02:46:45\",\"params\":{},\"purpose\":\"医疗费用\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\pension\\DepositApplyMapper.xml]\r\n### The error may involve com.ruoyi.mapper.pension.DepositApplyMapper.insertDepositApply-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into deposit_apply          ( apply_no,                                                                                                        purpose,                                                    apply_status,                                                                                                                                               create_by,             create_time )           values ( ?,                                                                                                        ?,                                                    ?,                                                                                                                                               ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\n; Field \'elder_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value', '2025-11-13 02:46:45', '5');
INSERT INTO `sys_oper_log` VALUES ('269', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyNo\":\"DEP1762974250663\",\"applyStatus\":\"draft\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:04:10\",\"params\":{},\"purpose\":\"个人物品购买\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\pension\\DepositApplyMapper.xml]\r\n### The error may involve com.ruoyi.mapper.pension.DepositApplyMapper.insertDepositApply-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into deposit_apply          ( apply_no,                                                                                                        purpose,                                                    apply_status,                                                                                                                                               create_by,             create_time )           values ( ?,                                                                                                        ?,                                                    ?,                                                                                                                                               ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value\n; Field \'elder_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'elder_id\' doesn\'t have a default value', '2025-11-13 03:04:10', '11');
INSERT INTO `sys_oper_log` VALUES ('270', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyAmount\":300,\"applyNo\":\"DEP1762975689373\",\"applyReason\":\"阿斯蒂芬阿斯蒂芬阿斯蒂芬\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"logo.png\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/logo_20251113032445A001.png\\\",\\\"uid\\\":1762975485559,\\\"status\\\":\\\"success\\\"},{\\\"name\\\":\\\"【资料 教程 学习 资源汇总】.docx\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/【资料 教程 学习 资源汇总】_20251113032454A002.docx\\\",\\\"uid\\\":1762975494289,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:28:09\",\"description\":\"阿斯蒂芬\",\"elderId\":16,\"expectedUseDate\":\"2025-11-25\",\"institutionId\":20,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"阿道夫\",\"urgencyLevel\":\"一般\"}', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLException: Field \'account_id\' doesn\'t have a default value\r\n### The error may exist in file [D:\\newhm\\newzijin\\ruoyi-admin\\target\\classes\\mapper\\pension\\DepositApplyMapper.xml]\r\n### The error may involve com.ruoyi.mapper.pension.DepositApplyMapper.insertDepositApply-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into deposit_apply          ( apply_no,             elder_id,             institution_id,                          apply_amount,             apply_reason,             apply_type,             urgency_level,             purpose,             description,             expected_use_date,             attachments,             apply_status,                                                                                                                                               create_by,             create_time,             remark )           values ( ?,             ?,             ?,                          ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,             ?,                                                                                                                                               ?,             ?,             ? )\r\n### Cause: java.sql.SQLException: Field \'account_id\' doesn\'t have a default value\n; Field \'account_id\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'account_id\' doesn\'t have a default value', '2025-11-13 03:28:10', '270');
INSERT INTO `sys_oper_log` VALUES ('271', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyAmount\":3000,\"applyId\":4,\"applyNo\":\"DEP1762975954286\",\"applyReason\":\"456456465456\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"养老机构预收费资金监管平台功能清单_Sheet1.png\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/养老机构预收费资金监管平台功能清单_Sheet1_20251113033218A003.png\\\",\\\"uid\\\":1762975938012,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:32:34\",\"description\":\"5456456\",\"elderId\":16,\"expectedUseDate\":\"2025-11-19\",\"institutionId\":20,\"params\":{},\"purpose\":\"个人物品购买\",\"remark\":\"546545\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 03:32:34', '22');
INSERT INTO `sys_oper_log` VALUES ('272', '撤回押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.withdraw()', 'PUT', '1', 'admin', '研发部门', '/pension/deposit/apply/withdraw/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 03:47:29', '21');
INSERT INTO `sys_oper_log` VALUES ('273', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyAmount\":100,\"applyId\":5,\"applyNo\":\"DEP1762977286219\",\"applyReason\":\"564654564564654654\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"logo.png\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/logo_20251113035443A001.png\\\",\\\"uid\\\":1762977283466,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:54:46\",\"description\":\"看看回家看好看就 \",\"elderId\":11,\"expectedUseDate\":\"2025-11-29\",\"institutionId\":16,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 03:54:46', '23');
INSERT INTO `sys_oper_log` VALUES ('274', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyAmount\":200,\"applyId\":6,\"applyNo\":\"DEP1762977335512\",\"applyReason\":\"阿斯蒂芬阿斯蒂芬阿斯蒂芬\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"logo.png\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/logo_20251113035533A001.png\\\",\\\"uid\\\":1762977333152,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:55:35\",\"description\":\"阿斯蒂芬阿斯蒂芬\",\"elderId\":11,\"expectedUseDate\":\"2025-11-25\",\"institutionId\":16,\"params\":{},\"purpose\":\"个人物品购买\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 03:55:35', '103');
INSERT INTO `sys_oper_log` VALUES ('275', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"applyAmount\":200,\"applyId\":7,\"applyNo\":\"DEP1762977550238\",\"applyReason\":\"费水电费收到发给收到发给\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"养老机构预收费资金监管平台功能清单_Sheet1.png\\\",\\\"url\\\":\\\"/profile/upload/2025/11/13/养老机构预收费资金监管平台功能清单_Sheet1_20251113035904A001.png\\\",\\\"uid\\\":1762977544600,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-11-13 03:59:10\",\"description\":\"收到发给收到发给收到发给\",\"elderId\":11,\"expectedUseDate\":\"2025-11-24\",\"institutionId\":16,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"收到发给\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 03:59:10', '100');
INSERT INTO `sys_oper_log` VALUES ('276', '撤回押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.withdraw()', 'PUT', '1', 'admin', '研发部门', '/pension/deposit/apply/withdraw/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-13 04:04:35', '42');
INSERT INTO `sys_oper_log` VALUES ('277', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-11-14 16:44:55\",\"elderId\":16,\"familyId\":3,\"isDefault\":\"1\",\"isMainContact\":\"1\",\"params\":{},\"phonenumber\":\"15981934928\",\"relationName\":\"子女\",\"relationType\":\"1\",\"remark\":\"01\",\"status\":\"0\",\"userId\":106}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-14 16:44:55', '99');
INSERT INTO `sys_oper_log` VALUES ('278', '参数管理', '2', 'com.ruoyi.web.controller.system.SysConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/config', '127.0.0.1', '内网IP', '{\"configId\":4,\"configKey\":\"sys.account.captchaEnabled\",\"configName\":\"账号自助-验证码开关\",\"configType\":\"N\",\"configValue\":\"false\",\"createBy\":\"admin\",\"createTime\":\"2025-10-28 02:47:08\",\"params\":{},\"remark\":\"是否开启验证码功能（true开启，false关闭）\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-16 00:25:39', '31');
INSERT INTO `sys_oper_log` VALUES ('279', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"contactPerson\":\"陈幸福\",\"contactPhone\":\"13666666666\",\"createBy\":\"admin\",\"createTime\":\"2025-11-16 00:50:24\",\"creditCode\":\"91410100MA45TE2X89\",\"fixedAssets\":null,\"institutionId\":21,\"institutionName\":\"幸福养老\",\"legalPerson\":\"两列\",\"params\":{},\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":1000000.0}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"666666\",\"institutionId\":21,\"institutionName\":\"幸福养老\",\"userName\":\"jg666666\",\"message\":\"机构账号创建成功\"}}', '0', null, '2025-11-16 00:50:24', '133');
INSERT INTO `sys_oper_log` VALUES ('280', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2121,2122,2123,2124,2125,4017,2030,2031,4035,4036,4037,4038,4039,4040,4041,2032,4042,4043,4044,4045,4046,4047,2040,2041,4027,4028,4029,4030,4031,4032,4033,4034,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-16 01:26:32', '27');
INSERT INTO `sys_oper_log` VALUES ('281', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2121,2122,2123,2124,2125,4017,2030,2031,4035,4036,4037,4038,4039,4040,4041,2032,4042,4043,4044,4045,4046,4047,2040,2041,4027,4028,4029,4030,4031,4032,4033,4034,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-16 01:38:07', '44');
INSERT INTO `sys_oper_log` VALUES ('282', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.reject()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/reject/15', '127.0.0.1', '内网IP', '15 {\"fixedAssets\":null,\"institutionId\":15,\"params\":{},\"registeredCapital\":null,\"remark\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-11-16 02:33:25', '49');
INSERT INTO `sys_oper_log` VALUES ('283', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-12-02 19:46:30\",\"elderId\":10,\"familyId\":4,\"isDefault\":\"0\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"15981934928\",\"relationName\":\"子女\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":106}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-02 19:46:30', '19');
INSERT INTO `sys_oper_log` VALUES ('284', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"12\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012212_20251202231754A001.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012218_20251202231754A002.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033646_20251202231754A003.png\"}', '{\"msg\":\"新增老人信息失败：\\r\\n### Error updating database.  Cause: java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\\r\\n### The error may exist in file [D:\\\\lasthm\\\\zijin\\\\fund-supervision\\\\ruoyi-admin\\\\target\\\\classes\\\\mapper\\\\ElderInfoMapper.xml]\\r\\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,                          age,             phone,             address,                                                    care_level,                          photo_path,             status,                          create_time )           values ( ?,             ?,             ?,                          ?,             ?,             ?,                                                    ?,                          ?,             ?,                          ? )\\r\\n### Cause: java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\\n; Field \'birth_date\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\",\"code\":500}', '0', null, '2025-12-02 23:17:55', '129');
INSERT INTO `sys_oper_log` VALUES ('285', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012222_20251202231925A001.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202231925A002.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202231925A003.png\"}', '{\"msg\":\"新增老人信息失败：\\r\\n### Error updating database.  Cause: java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\\r\\n### The error may exist in file [D:\\\\lasthm\\\\zijin\\\\fund-supervision\\\\ruoyi-admin\\\\target\\\\classes\\\\mapper\\\\ElderInfoMapper.xml]\\r\\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,                          age,             phone,             address,                                                    care_level,                          photo_path,             status,                          create_time )           values ( ?,             ?,             ?,                          ?,             ?,             ?,                                                    ?,                          ?,             ?,                          ? )\\r\\n### Cause: java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\\n; Field \'birth_date\' doesn\'t have a default value; nested exception is java.sql.SQLException: Field \'birth_date\' doesn\'t have a default value\",\"code\":500}', '0', null, '2025-12-02 23:19:25', '112');
INSERT INTO `sys_oper_log` VALUES ('286', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012222_20251202232213A001.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232214A002.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202232214A003.png\"}', '{\"msg\":\"新增老人信息失败：\\r\\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\r\\n### The error may exist in file [D:\\\\lasthm\\\\zijin\\\\fund-supervision\\\\ruoyi-admin\\\\target\\\\classes\\\\mapper\\\\ElderInfoMapper.xml]\\r\\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,             birth_date,             age,             phone,             address,                                                    care_level,                          photo_path,             status,                          create_time )           values ( ?,             ?,             ?,             ?,             ?,             ?,             ?,                                                    ?,                          ?,             ?,                          ? )\\r\\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\n; Duplicate entry \'412829198908160073\' for key \'uk_id_card\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\",\"code\":500}', '0', null, '2025-12-02 23:22:14', '20');
INSERT INTO `sys_oper_log` VALUES ('287', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012222_20251202232218A004.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232218A005.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202232219A006.png\"}', '{\"msg\":\"新增老人信息失败：\\r\\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\r\\n### The error may exist in file [D:\\\\lasthm\\\\zijin\\\\fund-supervision\\\\ruoyi-admin\\\\target\\\\classes\\\\mapper\\\\ElderInfoMapper.xml]\\r\\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,             birth_date,             age,             phone,             address,                                                    care_level,                          photo_path,             status,                          create_time )           values ( ?,             ?,             ?,             ?,             ?,             ?,             ?,                                                    ?,                          ?,             ?,                          ? )\\r\\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\n; Duplicate entry \'412829198908160073\' for key \'uk_id_card\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\",\"code\":500}', '0', null, '2025-12-02 23:22:19', '15');
INSERT INTO `sys_oper_log` VALUES ('288', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232302A007.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232302A008.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-09-15T164139_20251202232303A009.png\"}', '{\"msg\":\"新增老人信息失败：\\r\\n### Error updating database.  Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\r\\n### The error may exist in file [D:\\\\lasthm\\\\zijin\\\\fund-supervision\\\\ruoyi-admin\\\\target\\\\classes\\\\mapper\\\\ElderInfoMapper.xml]\\r\\n### The error may involve com.ruoyi.mapper.ElderInfoMapper.insertElderInfo-Inline\\r\\n### The error occurred while setting parameters\\r\\n### SQL: insert into elder_info          ( elder_name,             gender,             id_card,             birth_date,             age,             phone,             address,                                                    care_level,                          photo_path,             status,                          create_time )           values ( ?,             ?,             ?,             ?,             ?,             ?,             ?,                                                    ?,                          ?,             ?,                          ? )\\r\\n### Cause: java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\\n; Duplicate entry \'412829198908160073\' for key \'uk_id_card\'; nested exception is java.sql.SQLIntegrityConstraintViolationException: Duplicate entry \'412829198908160073\' for key \'uk_id_card\'\",\"code\":500}', '0', null, '2025-12-02 23:23:03', '24');
INSERT INTO `sys_oper_log` VALUES ('289', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232432A001.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232432A002.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-09-15T164139_20251202232432A003.png\"}', '{\"msg\":\"该身份证号已被使用，请检查身份证号是否正确\",\"code\":500}', '0', null, '2025-12-02 23:24:32', '9');
INSERT INTO `sys_oper_log` VALUES ('290', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198908160077\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232439A004.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033638_20251202232439A005.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-09-15T164139_20251202232440A006.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2025-12-02 23:24:40', '47');
INSERT INTO `sys_oper_log` VALUES ('291', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"4\",\"age\":\"38\",\"idCard\":\"412829198908260073\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202232915A007.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033646_20251202232915A008.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T132012_20251202232915A009.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2025-12-02 23:29:15', '14');
INSERT INTO `sys_oper_log` VALUES ('292', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"陈飞宇\",\"relationType\":\"2\",\"age\":\"62\",\"idCard\":\"412829198908160056\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012222_20251202233004A010.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033646_20251202233005A011.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-09-15T164139_20251202233005A012.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2025-12-02 23:30:05', '20');
INSERT INTO `sys_oper_log` VALUES ('293', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wen\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198906090053\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202233402A013.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202233403A014.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-10-17T232528_20251202233403A015.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2025-12-02 23:34:03', '18');
INSERT INTO `sys_oper_log` VALUES ('294', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"李实实\",\"relationType\":\"5\",\"age\":\"120\",\"idCard\":\"412829196002200236\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012212_20251202234237A016.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-17T170621_20251202234237A017.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T132012_20251202234238A018.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2025-12-02 23:42:38', '23');
INSERT INTO `sys_oper_log` VALUES ('295', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '192.168.31.146', '内网IP', '{\"elderName\":\"wen\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198906090053\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"良好\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202233402A013.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202233403A014.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-10-17T232528_20251202233403A015.png\",\"elderId\":\"23\"}', '{\"msg\":\"更新老人信息失败：nested exception is org.apache.ibatis.binding.BindingException: Parameter \'elderId\' not found. Available parameters are [arg1, arg0, param1, param2]\",\"code\":500}', '0', null, '2025-12-03 00:05:42', '15');
INSERT INTO `sys_oper_log` VALUES ('296', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '192.168.31.146', '内网IP', '{\"elderName\":\"wen\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198906090053\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"良好\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202233402A013.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202233403A014.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-10-17T232528_20251202233403A015.png\",\"elderId\":\"23\"}', '{\"msg\":\"更新老人信息失败：nested exception is org.apache.ibatis.binding.BindingException: Parameter \'elderId\' not found. Available parameters are [arg1, arg0, param1, param2]\",\"code\":500}', '0', null, '2025-12-03 00:06:02', '8');
INSERT INTO `sys_oper_log` VALUES ('297', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '192.168.31.146', '内网IP', '{\"elderName\":\"wen\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198906090053\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"良好\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202233402A013.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202233403A014.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-10-17T232528_20251202233403A015.png\",\"elderId\":\"23\"}', '{\"msg\":\"更新老人信息失败：nested exception is org.apache.ibatis.binding.BindingException: Parameter \'elderId\' not found. Available parameters are [arg1, arg0, param1, param2]\",\"code\":500}', '0', null, '2025-12-03 00:06:12', '6');
INSERT INTO `sys_oper_log` VALUES ('298', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '192.168.31.146', '内网IP', '{\"elderName\":\"wen\",\"relationType\":\"2\",\"age\":\"121\",\"idCard\":\"412829198906090053\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"良好\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T012229_20251202233402A013.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-08-18T033657_20251202233403A014.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2025/12/02/Screenshot_2025-10-17T232528_20251202233403A015.png\",\"elderId\":\"23\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2025-12-03 00:17:29', '26');
INSERT INTO `sys_oper_log` VALUES ('299', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '192.168.31.146', '内网IP', '{\"elderName\":\"测试2\",\"relationType\":\"1\",\"age\":\"36\",\"idCard\":\"412829198908160052\",\"phone\":\"18539279011\",\"address\":\"阿斯蒂芬\",\"healthStatus\":\"阿斯蒂芬\",\"medicalHistory\":\"\",\"elderId\":\"16\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2025-12-03 00:17:48', '4');
INSERT INTO `sys_oper_log` VALUES ('300', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-12-03 00:24:34\",\"elderId\":24,\"familyId\":10,\"isDefault\":\"1\",\"isMainContact\":\"1\",\"params\":{},\"phonenumber\":\"18539279011\",\"relationName\":\"朋友\",\"relationType\":\"5\",\"status\":\"0\",\"userId\":103}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-03 00:24:34', '13');
INSERT INTO `sys_oper_log` VALUES ('301', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-12-03 00:25:19\",\"elderId\":24,\"familyId\":11,\"isDefault\":\"1\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"15600000000\",\"relationName\":\"朋\",\"relationType\":\"5\",\"status\":\"0\",\"userId\":108}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-03 00:25:19', '31');
INSERT INTO `sys_oper_log` VALUES ('302', '设置老人密码', '2', 'com.ruoyi.web.controller.ElderInfoController.setPassword()', 'POST', '1', 'admin', '研发部门', '/elder/info/setPassword', '127.0.0.1', '内网IP', '{\"elderId\":16}', '{\"msg\":\"密码设置成功\",\"code\":200}', '0', null, '2025-12-03 19:46:35', '28');
INSERT INTO `sys_oper_log` VALUES ('303', '设置老人密码', '2', 'com.ruoyi.web.controller.ElderInfoController.setPassword()', 'POST', '1', 'admin', '研发部门', '/elder/info/setPassword', '127.0.0.1', '内网IP', '{\"elderId\":16}', '{\"msg\":\"密码设置成功\",\"code\":200}', '0', null, '2025-12-03 20:00:10', '26');
INSERT INTO `sys_oper_log` VALUES ('304', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":0,\"bedFeeMin\":0,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-04 16:48:31\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:48:31', '27');
INSERT INTO `sys_oper_log` VALUES ('305', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-04 16:48:57\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:48:57', '14');
INSERT INTO `sys_oper_log` VALUES ('306', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:49:00', '16');
INSERT INTO `sys_oper_log` VALUES ('307', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-04 16:49:50\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:49:50', '16');
INSERT INTO `sys_oper_log` VALUES ('308', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:49:53', '12');
INSERT INTO `sys_oper_log` VALUES ('309', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-04 16:49:57\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:49:57', '5');
INSERT INTO `sys_oper_log` VALUES ('310', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:49:59', '16');
INSERT INTO `sys_oper_log` VALUES ('311', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:53:08', '25');
INSERT INTO `sys_oper_log` VALUES ('312', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 16:53:16', '0');
INSERT INTO `sys_oper_log` VALUES ('313', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 17:04:09', '15');
INSERT INTO `sys_oper_log` VALUES ('314', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"0\",\"landArea\":8,\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-04 17:18:19\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 17:18:19', '12');
INSERT INTO `sys_oper_log` VALUES ('315', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 17:18:49', '13');
INSERT INTO `sys_oper_log` VALUES ('316', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care\",\"bedFeeMax\":1500,\"bedFeeMin\":1000,\"buildingArea\":1500,\"createTime\":\"2025-12-04 18:04:11\",\"environmentImgs\":\"/profile/upload/2025/12/04/Screenshot_2025-12-01T025540_20251204180410A002.png\",\"institutionId\":20,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"0\",\"landArea\":1000,\"mainPicture\":\"/profile/upload/2025/12/04/yuwen_20251204180353A001.png\",\"mealFeeMax\":18000,\"mealFeeMin\":1212,\"nursingFeeMax\":2000,\"nursingFeeMin\":1000,\"params\":{},\"publicId\":2,\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 18:04:11', '17');
INSERT INTO `sys_oper_log` VALUES ('317', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 18:05:24', '22');
INSERT INTO `sys_oper_log` VALUES ('318', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'admin', '研发部门', '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"applyTime\":\"2025-12-04 19:40:36\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224631A002.png\",\"areaCode\":\"410102\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/12/04/04_20251204194031A001.png\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createTime\":\"2025-12-04 19:40:36\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":22,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"45465\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"航海西路街道办事处\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224634A003.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 19:40:36', '50');
INSERT INTO `sys_oper_log` VALUES ('319', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 19:40:57', '8');
INSERT INTO `sys_oper_log` VALUES ('320', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"applyTime\":\"2025-12-04 19:46:43\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224631A002.png\",\"areaCode\":\"410102\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/12/04/04_20251204194031A001.png\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":22,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"45465\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"航海西路街道办事处\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224634A003.png\",\"updateTime\":\"2025-12-04 19:46:43\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 19:46:44', '6');
INSERT INTO `sys_oper_log` VALUES ('321', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"applyTime\":\"2025-12-04 19:53:31\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/12/04/yuwen_20251204195330A002.png\",\"areaCode\":\"410105\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224628A001.png\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":20,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"45465\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"北林路街道办事处\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224634A003.png\",\"updateTime\":\"2025-12-04 19:53:31\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 19:53:31', '5');
INSERT INTO `sys_oper_log` VALUES ('322', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"只能审批待审批状态的申请\",\"code\":500}', '0', null, '2025-12-04 20:13:51', '2');
INSERT INTO `sys_oper_log` VALUES ('323', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"只能审批待审批状态的申请\",\"code\":500}', '0', null, '2025-12-04 20:13:57', '5');
INSERT INTO `sys_oper_log` VALUES ('324', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 20:22:14', '12');
INSERT INTO `sys_oper_log` VALUES ('325', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2025-12-04 20:26:54\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110032850A002.png\",\"areaCode\":\"410105\",\"bankAccount\":\"64546\",\"bedCount\":4,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110032847A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"institutionType\":\"nursing_home\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45645\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":6.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"北林路街道办事处\",\"superviseAccount\":\"454544564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110032853A003.png\",\"updateTime\":\"2025-12-04 20:26:54\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 20:26:54', '16');
INSERT INTO `sys_oper_log` VALUES ('326', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/16', '127.0.0.1', '内网IP', '16', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 20:27:07', '14');
INSERT INTO `sys_oper_log` VALUES ('327', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-04 20:27:09', '7');
INSERT INTO `sys_oper_log` VALUES ('328', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"createTime\":\"2025-12-05 01:49:12\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"0\",\"landArea\":2000,\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"publicId\":3}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 01:49:12', '15');
INSERT INTO `sys_oper_log` VALUES ('329', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"actualAddress\":\"admin第二个机构\",\"bedCount\":500,\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-05 01:49:13\",\"creditCode\":\"91410100MA45TE2X84\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"feeRange\":\"200-5000\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"0\",\"landArea\":2000,\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"publicId\":3,\"rating\":\"3\",\"recordNumber\":\"789789\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-05 02:08:27\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 02:08:27', '17');
INSERT INTO `sys_oper_log` VALUES ('330', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"actualAddress\":\"admin第二个机构\",\"bedCount\":500,\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-05 01:49:13\",\"creditCode\":\"91410100MA45TE2X84\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"feeRange\":\"200-5000\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"0\",\"landArea\":2000,\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"publicId\":3,\"rating\":\"3\",\"recordNumber\":\"789789\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-05 02:09:17\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 02:09:17', '5');
INSERT INTO `sys_oper_log` VALUES ('331', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-05 02:09:43\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 02:09:43', '5');
INSERT INTO `sys_oper_log` VALUES ('332', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care\",\"actualAddress\":\"admin第二个机构\",\"bedCount\":500,\"bedFeeMax\":1500,\"bedFeeMin\":1000,\"buildingArea\":1500,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-04 18:04:12\",\"creditCode\":\"91410100MA45TE2X84\",\"environmentImgs\":\"/profile/upload/2025/12/04/Screenshot_2025-12-01T025540_20251204180410A002.png\",\"feeRange\":\"200-5000\",\"institutionId\":20,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"1\",\"landArea\":1000,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"紧急呼叫\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/yuwen_20251204180353A001.png\",\"mealFeeMax\":18000,\"mealFeeMin\":1212,\"medicalFacilities\":\"[\\\"医疗室\\\"]\",\"nursingFeeMax\":2000,\"nursingFeeMin\":1000,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205021955A001.png\",\"publicId\":2,\"rating\":\"3\",\"recordNumber\":\"789789\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-05 02:19:57\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 02:19:57', '18');
INSERT INTO `sys_oper_log` VALUES ('333', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care\",\"actualAddress\":\"admin第二个机构\",\"bedCount\":500,\"bedFeeMax\":1500,\"bedFeeMin\":1000,\"buildingArea\":1500,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-04 18:04:12\",\"creditCode\":\"91410100MA45TE2X84\",\"dailyServices\":\"[{\\\"time\\\":\\\"02:20\\\",\\\"content\\\":\\\"森岛帆高\\\"},{\\\"time\\\":\\\"02:21\\\",\\\"content\\\":\\\"3245345\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/04/Screenshot_2025-12-01T025540_20251204180410A002.png\",\"feeRange\":\"200-5000\",\"institutionId\":20,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"1\",\"landArea\":1000,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"紧急呼叫\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/yuwen_20251204180353A001.png\",\"mealFeeMax\":18000,\"mealFeeMin\":1212,\"medicalFacilities\":\"[\\\"医疗室\\\"]\",\"nursingFeeMax\":2000,\"nursingFeeMin\":1000,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205021955A001.png\",\"publicId\":2,\"rating\":\"3\",\"recordNumber\":\"789789\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-05 02:20:18\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-05 02:20:18', '8');
INSERT INTO `sys_oper_log` VALUES ('334', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"actualAddress\":\"admin第二个机构\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207181251A003.png,/profile/upload/2025/12/07/profile_20251207181253A004.png\",\"bedCount\":500,\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-05 01:49:13\",\"creditCode\":\"91410100MA45TE2X84\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:13\\\",\\\"content\\\":\\\"12313\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"feeRange\":\"200-5000\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"0\",\"landArea\":2000,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"medicalFacilities\":\"[\\\"康复室\\\",\\\"理疗室\\\"]\",\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo_20251207181255A005.png\",\"publicId\":3,\"rating\":\"3\",\"recordNumber\":\"789789\",\"roomFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207181248A001.png,/profile/upload/2025/12/07/logo_20251207181249A002.png\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 18:13:06\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:13:06', '22');
INSERT INTO `sys_oper_log` VALUES ('335', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/3', '127.0.0.1', '内网IP', '3', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:14:31', '17');
INSERT INTO `sys_oper_log` VALUES ('336', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207185526A003.png,/profile/upload/2025/12/07/profile_20251207185534A006.png\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"无线网络\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"康复室\\\"]\",\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207185530A004.png,/profile/upload/2025/12/07/logo_20251207185532A005.png\",\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"roomFacilities\":\"/profile/upload/2025/12/07/profile_20251207185522A001.png,/profile/upload/2025/12/07/logo_20251207185523A002.png\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 18:55:41\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:55:41', '35');
INSERT INTO `sys_oper_log` VALUES ('337', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:55:54', '22');
INSERT INTO `sys_oper_log` VALUES ('338', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:55:56', '13');
INSERT INTO `sys_oper_log` VALUES ('339', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207185526A003.png,/profile/upload/2025/12/07/profile_20251207185534A006.png\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:57\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"},{\\\"time\\\":\\\"15:56\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"无线网络\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"康复室\\\"]\",\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207185530A004.png,/profile/upload/2025/12/07/logo_20251207185532A005.png\",\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"roomFacilities\":\"/profile/upload/2025/12/07/profile_20251207185522A001.png,/profile/upload/2025/12/07/logo_20251207185523A002.png\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 18:58:02\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:58:02', '32');
INSERT INTO `sys_oper_log` VALUES ('340', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:58:05', '16');
INSERT INTO `sys_oper_log` VALUES ('341', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/1', '127.0.0.1', '内网IP', '1', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 18:58:07', '7');
INSERT INTO `sys_oper_log` VALUES ('342', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"医疗室\",\"facilityType\":\"medical\",\"iconName\":\"checkbox\",\"id\":15,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 20:06:27', '22');
INSERT INTO `sys_oper_log` VALUES ('343', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"独立卫浴\",\"facilityType\":\"life\",\"iconName\":\"like-o\",\"id\":1,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 20:20:08', '19');
INSERT INTO `sys_oper_log` VALUES ('344', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"独立卫浴\",\"facilityType\":\"life\",\"iconName\":\"row\",\"id\":1,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 21:42:23', '15');
INSERT INTO `sys_oper_log` VALUES ('345', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"紧急呼叫\",\"facilityType\":\"life\",\"iconName\":\"radio\",\"id\":2,\"params\":{},\"sortOrder\":2,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 21:42:33', '5');
INSERT INTO `sys_oper_log` VALUES ('346', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"无线网络\",\"facilityType\":\"life\",\"iconName\":\"tool\",\"id\":9,\"params\":{},\"sortOrder\":9,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 21:42:46', '25');
INSERT INTO `sys_oper_log` VALUES ('347', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"医疗室\",\"facilityType\":\"medical\",\"iconName\":\"rate\",\"id\":15,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 21:43:03', '14');
INSERT INTO `sys_oper_log` VALUES ('348', '设施图标配置', '1', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.add()', 'POST', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"测试设施\",\"facilityType\":\"life\",\"iconName\":\"chart\",\"id\":30,\"params\":{},\"sortOrder\":0,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 21:43:30', '24');
INSERT INTO `sys_oper_log` VALUES ('349', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207185526A003.png,/profile/upload/2025/12/07/profile_20251207185534A006.png\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:57\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"},{\\\"time\\\":\\\"15:56\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"无线网络\\\",\\\"测试设施\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"康复室\\\"]\",\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207185530A004.png,/profile/upload/2025/12/07/logo_20251207185532A005.png\",\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"roomFacilities\":\"/profile/upload/2025/12/07/profile_20251207185522A001.png,/profile/upload/2025/12/07/logo_20251207185523A002.png\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 22:06:43\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:06:43', '53');
INSERT INTO `sys_oper_log` VALUES ('350', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207185526A003.png,/profile/upload/2025/12/07/profile_20251207185534A006.png\",\"bedCount\":4,\"bedFeeMax\":3000,\"bedFeeMin\":2000,\"buildingArea\":10,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-11-11 00:38:29\",\"creditCode\":\"91410100MA45TE2X81\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:57\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"},{\\\"time\\\":\\\"15:56\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/04/04_20251204164825A001.png\",\"feeRange\":\"2000\",\"feeRangeMax\":0,\"feeRangeMin\":0,\"institutionId\":16,\"institutionIntro\":\"阿斯蒂芬阿斯蒂芬\",\"institutionName\":\"admin增加的养老机构\",\"isPublished\":\"1\",\"landArea\":8,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"无线网络\\\",\\\"测试设施\\\",\\\"紧急呼叫\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/04/logo (1)_20251204171817A002.png\",\"mealFeeMax\":2500,\"mealFeeMin\":2000,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"康复室\\\"]\",\"nursingFeeMax\":5000,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207185530A004.png,/profile/upload/2025/12/07/logo_20251207185532A005.png\",\"publicId\":1,\"rating\":\"3\",\"recordNumber\":\"45645\",\"roomFacilities\":\"/profile/upload/2025/12/07/profile_20251207185522A001.png,/profile/upload/2025/12/07/logo_20251207185523A002.png\",\"serviceFeatures\":\"阿斯蒂芬\",\"serviceScope\":\"阿斯蒂芬\",\"superviseAccount\":\"454544564\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 22:07:16\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:07:16', '12');
INSERT INTO `sys_oper_log` VALUES ('351', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"actualAddress\":\"admin第二个机构\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207181251A003.png,/profile/upload/2025/12/07/profile_20251207181253A004.png\",\"bedCount\":500,\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-05 01:49:13\",\"creditCode\":\"91410100MA45TE2X84\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:13\\\",\\\"content\\\":\\\"12313\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"feeRange\":\"200-5000\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"1\",\"landArea\":2000,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"测试设施\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"medicalFacilities\":\"[\\\"康复室\\\",\\\"理疗室\\\"]\",\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo_20251207181255A005.png\",\"publicId\":3,\"rating\":\"3\",\"recordNumber\":\"789789\",\"roomFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207181248A001.png,/profile/upload/2025/12/07/logo_20251207181249A002.png\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 22:07:21\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:07:21', '6');
INSERT INTO `sys_oper_log` VALUES ('352', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"独立卫浴\",\"facilityType\":\"life\",\"iconName\":\"dict\",\"id\":1,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:25:16', '8');
INSERT INTO `sys_oper_log` VALUES ('353', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"独立卫浴\",\"facilityType\":\"life\",\"iconName\":\"enter\",\"id\":1,\"params\":{},\"sortOrder\":1,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:38:25', '7');
INSERT INTO `sys_oper_log` VALUES ('354', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"洗衣服务\",\"facilityType\":\"life\",\"iconName\":\"code\",\"id\":3,\"params\":{},\"sortOrder\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:49:24', '12');
INSERT INTO `sys_oper_log` VALUES ('355', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"洗衣服务\",\"facilityType\":\"life\",\"iconName\":\"drag\",\"id\":3,\"params\":{},\"sortOrder\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:57:34', '8');
INSERT INTO `sys_oper_log` VALUES ('356', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"semi_disabled,self_care,disabled,dementia\",\"actualAddress\":\"admin第二个机构\",\"basicFacilities\":\"/profile/upload/2025/12/07/logo_20251207181251A003.png,/profile/upload/2025/12/07/profile_20251207181253A004.png\",\"bedCount\":500,\"bedFeeMax\":22000,\"bedFeeMin\":2000,\"buildingArea\":5000,\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-05 01:49:13\",\"creditCode\":\"91410100MA45TE2X84\",\"dailyServices\":\"[{\\\"time\\\":\\\"18:13\\\",\\\"content\\\":\\\"将来添加新图标时可以放心使用将来添加新图标时可以放心使用\\\"},{\\\"time\\\":\\\"22:59\\\",\\\"content\\\":\\\"代码完全符合ESLint规范，没有任何转义字符警告\\\"},{\\\"time\\\":\\\"21:59\\\",\\\"content\\\":\\\"SVG内容中的双引号不再被转义SVG内容中的双引号不再被转义\\\"}]\",\"environmentImgs\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025526_20251205014830A002.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025531_20251205014832A003.png,/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014834A004.png\",\"feeRange\":\"200-5000\",\"institutionId\":22,\"institutionIntro\":\"阿斯蒂芬\",\"institutionName\":\"admin第二个机构\",\"isPublished\":\"1\",\"landArea\":2000,\"lifeFacilities\":\"[\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"测试设施\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/05/Screenshot_2025-12-01T025540_20251205014826A001.png\",\"mealFeeMax\":12321,\"mealFeeMin\":5000,\"medicalFacilities\":\"[\\\"康复室\\\",\\\"理疗室\\\"]\",\"nursingFeeMax\":2000,\"nursingFeeMin\":200,\"params\":{},\"parkFacilities\":\"/profile/upload/2025/12/07/logo_20251207181255A005.png\",\"publicId\":3,\"rating\":\"3\",\"recordNumber\":\"789789\",\"roomFacilities\":\"/profile/upload/2025/12/07/logo (2)_20251207181248A001.png,/profile/upload/2025/12/07/logo_20251207181249A002.png\",\"superviseAccount\":\"45456\",\"updateBy\":\"\",\"updateTime\":\"2025-12-07 22:59:42\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 22:59:42', '18');
INSERT INTO `sys_oper_log` VALUES ('357', '新增机构评级', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addRating()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/rating/add', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:15\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-07 23:51:15', '89');
INSERT INTO `sys_oper_log` VALUES ('358', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:23:44\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:23:44', '23');
INSERT INTO `sys_oper_log` VALUES ('359', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:24:38\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:24:38', '17');
INSERT INTO `sys_oper_log` VALUES ('360', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:26:35\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:26:35', '5');
INSERT INTO `sys_oper_log` VALUES ('361', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:28:43\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:28:43', '24');
INSERT INTO `sys_oper_log` VALUES ('362', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":4,\"ratingRemark\":\"阿斯蒂芬阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:30:58\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:30:58', '29');
INSERT INTO `sys_oper_log` VALUES ('363', '修改机构评级', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.updateRating()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/rating/update', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-12-07 23:51:16\",\"creditCode\":\"91410100MA45TE2X81\",\"expireDate\":\"2026-12-30\",\"facilityScore\":20,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"managementScore\":20,\"params\":{},\"ratingDate\":\"2025-12-30\",\"ratingId\":1,\"ratingLevel\":2,\"ratingRemark\":\"阿斯蒂芬阿斯蒂芬\",\"ratingStatus\":\"1\",\"safetyScore\":20,\"serviceScore\":20,\"totalScore\":80,\"updateBy\":\"admin\",\"updateTime\":\"2025-12-08 00:37:25\",\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-08 00:37:25', '23');
INSERT INTO `sys_oper_log` VALUES ('364', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2025-12-09 01:25:06', '84');
INSERT INTO `sys_oper_log` VALUES ('365', '取消收藏机构', '3', 'com.ruoyi.web.controller.h5.UserFavoriteController.removeFavorite()', 'DELETE', '1', '15981934928', null, '/h5/favorite/remove/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"取消收藏成功\",\"code\":200}', '0', null, '2025-12-09 01:25:55', '42');
INSERT INTO `sys_oper_log` VALUES ('366', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2025-12-09 01:26:01', '8');
INSERT INTO `sys_oper_log` VALUES ('367', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:26:09', '3');
INSERT INTO `sys_oper_log` VALUES ('368', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2025-12-09 01:29:55', '39');
INSERT INTO `sys_oper_log` VALUES ('369', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:30:57', '7');
INSERT INTO `sys_oper_log` VALUES ('370', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:34:34', '8');
INSERT INTO `sys_oper_log` VALUES ('371', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:39:14', '5');
INSERT INTO `sys_oper_log` VALUES ('372', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:40:02', '3');
INSERT INTO `sys_oper_log` VALUES ('373', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/16', '127.0.0.1', '内网IP', '16', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2025-12-09 01:44:58', '15');
INSERT INTO `sys_oper_log` VALUES ('374', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:45:12', '2');
INSERT INTO `sys_oper_log` VALUES ('375', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:47:58', '2');
INSERT INTO `sys_oper_log` VALUES ('376', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:48:03', '2');
INSERT INTO `sys_oper_log` VALUES ('377', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/20', '127.0.0.1', '内网IP', '20', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:50:20', '2');
INSERT INTO `sys_oper_log` VALUES ('378', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-09 01:50:31', '3');
INSERT INTO `sys_oper_log` VALUES ('379', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":32,\"bedNumber\":\"456\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2025-12-10 20:17:01\",\"depositFee\":2000,\"floorNumber\":2,\"fullCarePrice\":100,\"halfCarePrice\":50,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":22,\"memberFee\":200,\"params\":{},\"price\":1000,\"roomArea\":20,\"roomNumber\":\"01\",\"selfCarePrice\":20}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-10 20:17:01', '24');
INSERT INTO `sys_oper_log` VALUES ('380', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":13,\"bedNumber\":\"56\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-11-12 22:47:36\",\"depositFee\":1000,\"fullCarePrice\":150,\"halfCarePrice\":100,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":20,\"institutionName\":\"admin第二个机构\",\"memberFee\":20,\"params\":{},\"price\":1000,\"roomNumber\":\"105\",\"selfCarePrice\":50,\"updateBy\":\"\",\"updateTime\":\"2025-12-10 20:28:17\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-10 20:28:17', '27');
INSERT INTO `sys_oper_log` VALUES ('381', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":32,\"bedNumber\":\"456\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2025-12-10 20:17:01\",\"depositFee\":10000,\"floorNumber\":2,\"fullCarePrice\":3000,\"halfCarePrice\":2000,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":22,\"institutionName\":\"admin第二个机构\",\"memberFee\":600,\"params\":{},\"price\":1000,\"roomArea\":20,\"roomNumber\":\"01\",\"selfCarePrice\":1000,\"updateBy\":\"\",\"updateTime\":\"2025-12-10 22:40:49\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-10 22:40:49', '16');
INSERT INTO `sys_oper_log` VALUES ('382', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'admin', '研发部门', '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"\",\"age\":36,\"bedId\":18,\"birthDate\":\"1989-08-16\",\"careLevel\":\"1\",\"checkInDate\":\"2025-12-11\",\"depositAmount\":15000,\"elderName\":\"陈飞雨\",\"emergencyContact\":\"流量\",\"emergencyPhone\":\"18539295555\",\"feeDescription\":\"\",\"finalAmount\":25700,\"gender\":\"2\",\"healthStatus\":\"\",\"idCard\":\"412829198908168989\",\"memberFee\":8000,\"monthCount\":1,\"monthlyFee\":2800,\"paymentMethod\":\"later\",\"phone\":\"18539856565\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-11 00:16:22', '83');
INSERT INTO `sys_oper_log` VALUES ('383', '模拟支付', '2', 'com.ruoyi.web.controller.PaymentRecordController.simulatePayment()', 'POST', '1', 'admin', '研发部门', '/payment/record/simulate/19', '127.0.0.1', '内网IP', '19 {\"params\":{},\"paymentMethod\":\"cash\"}', '{\"msg\":\"支付成功\",\"code\":200,\"data\":{\"createTime\":\"2025-12-11 01:00:13\",\"elderId\":25,\"gatewayResponse\":\"模拟支付成功\",\"institutionId\":16,\"operator\":\"system\",\"orderId\":19,\"orderNo\":\"ORD1765383382268\",\"params\":{},\"paymentAmount\":25700.00,\"paymentId\":8,\"paymentMethod\":\"cash\",\"paymentNo\":\"PAY20251211010013883\",\"paymentStatus\":\"1\",\"paymentTime\":\"2025-12-11 01:00:13\",\"transactionId\":\"TXN20251211010013883\"}}', '0', null, '2025-12-11 01:00:13', '29');
INSERT INTO `sys_oper_log` VALUES ('384', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":25,\"finalAmount\":28000,\"memberFee\":0,\"monthCount\":10,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-11 01:22:22', '28');
INSERT INTO `sys_oper_log` VALUES ('385', '设置老人密码', '2', 'com.ruoyi.web.controller.ElderInfoController.setPassword()', 'POST', '1', 'admin', '研发部门', '/elder/info/setPassword', '127.0.0.1', '内网IP', '{\"elderId\":22}', '{\"msg\":\"密码设置成功\",\"code\":200}', '0', null, '2025-12-12 12:06:27', '13');
INSERT INTO `sys_oper_log` VALUES ('386', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2025-12-12 12:34:18\",\"elderId\":16,\"familyId\":65,\"isDefault\":\"0\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"15611111111\",\"relationName\":\"zik\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":111}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-12 12:34:18', '41');
INSERT INTO `sys_oper_log` VALUES ('387', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2025-12-13 19:01:23', '102');
INSERT INTO `sys_oper_log` VALUES ('388', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2030,2031,2032,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,4051,4052,4053,4054,4055,4056,2020,2021,2121,2122,2123,2124,2125,4017,4035,4036,4037,4038,4039,4040,4041,4042,4043,4044,4045,4046,4047,2040,2041,4050,4027,4028,4029,4030,4031,4032,4033,4034,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-14 01:51:21', '32');
INSERT INTO `sys_oper_log` VALUES ('389', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'jigouuser', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"bedFeeMax\":0,\"bedFeeMin\":0,\"buildingArea\":0,\"createTime\":\"2025-12-14 20:16:16\",\"institutionId\":2,\"institutionIntro\":\"测试机构简介\",\"institutionName\":\"测试服务机构\",\"isPublished\":\"0\",\"landArea\":0,\"mainPicture\":\"/profile/upload/2025/12/14/矩形 21@2x_20251214201555A001.png\",\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"publicId\":4}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-14 20:16:16', '22');
INSERT INTO `sys_oper_log` VALUES ('390', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'jigouuser', null, '/pension/publicity/publish/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-14 20:16:36', '37');
INSERT INTO `sys_oper_log` VALUES ('391', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '127.0.0.1', '内网IP', '{\"elderName\":\"测试2\",\"relationType\":\"1\",\"age\":\"36\",\"idCard\":\"412829198908160052\",\"phone\":\"18539279011\",\"address\":\"阿斯蒂芬\",\"healthStatus\":\"阿斯蒂芬\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/14/首页-选中@2x_20251214201721A002.png\",\"elderId\":\"16\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2025-12-14 20:17:21', '6');
INSERT INTO `sys_oper_log` VALUES ('392', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '127.0.0.1', '内网IP', '{\"elderName\":\"张秀英\",\"relationType\":\"1\",\"age\":\"75\",\"idCard\":\"110101195003154521\",\"phone\":\"13801123456\",\"address\":\"北京市东城区某某街道123号\",\"healthStatus\":\"高血压，糖尿病\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/14/办事_20251214215616A001.png\",\"elderId\":\"26\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2025-12-14 21:56:17', '7');
INSERT INTO `sys_oper_log` VALUES ('393', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '127.0.0.1', '内网IP', '{\"elderName\":\"张秀英\",\"relationType\":\"1\",\"age\":\"75\",\"idCard\":\"110101195003154521\",\"phone\":\"13801123456\",\"address\":\"北京市东城区某某街道123号\",\"healthStatus\":\"高血压，糖尿病\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2025/12/14/办事_20251214215616A001.png\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2025/12/14/人才公寓_20251214221001A001.png\",\"elderId\":\"26\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2025-12-14 22:10:01', '32');
INSERT INTO `sys_oper_log` VALUES ('394', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'jigouuser', null, '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":17,\"applyAmount\":2000,\"applyId\":8,\"applyNo\":\"DEP1765728201173\",\"applyReason\":\"菜单阿斯蒂阿斯蒂芬阿斯蒂\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"资料上传@2x.png\\\",\\\"url\\\":\\\"/profile/upload/2025/12/15/资料上传@2x_20251215000317A001.png\\\",\\\"uid\\\":1765728197854,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"jigouuser\",\"createTime\":\"2025-12-15 00:03:21\",\"description\":\"测试\",\"elderId\":35,\"expectedUseDate\":\"2025-12-31\",\"institutionId\":2,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-15 00:03:21', '38');
INSERT INTO `sys_oper_log` VALUES ('395', '撤回押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.withdraw()', 'PUT', '1', 'jigouuser', null, '/pension/deposit/apply/withdraw/8', '127.0.0.1', '内网IP', '8', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-15 00:32:46', '14');
INSERT INTO `sys_oper_log` VALUES ('396', '押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.edit()', 'PUT', '1', 'jigouuser', null, '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":17,\"applyAmount\":2000,\"applyId\":8,\"applyReason\":\"菜单阿斯蒂阿斯蒂芬阿斯蒂\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"资料上传@2x.png\\\",\\\"url\\\":\\\"/profile/upload/2025/12/15/资料上传@2x_20251215000317A001.png\\\",\\\"uid\\\":1765728197854,\\\"status\\\":\\\"success\\\"}]\",\"description\":\"测试\",\"elderId\":35,\"expectedUseDate\":\"2025-12-31\",\"institutionId\":2,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"updateBy\":\"jigouuser\",\"updateTime\":\"2025-12-15 00:40:24\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-15 00:40:24', '47');
INSERT INTO `sys_oper_log` VALUES ('397', '撤回押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.withdraw()', 'PUT', '1', 'jigouuser', null, '/pension/deposit/apply/withdraw/8', '127.0.0.1', '内网IP', '8', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-15 01:25:49', '16');
INSERT INTO `sys_oper_log` VALUES ('398', '押金使用申请', '2', 'com.ruoyi.web.controller.pension.DepositApplyController.edit()', 'PUT', '1', 'jigouuser', null, '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":17,\"applyAmount\":2003,\"applyId\":8,\"applyReason\":\"菜单阿斯蒂阿斯蒂芬阿斯蒂\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"资料上传@2x.png\\\",\\\"url\\\":\\\"/profile/upload/2025/12/15/资料上传@2x_20251215000317A001.png\\\",\\\"uid\\\":1765728197854,\\\"status\\\":\\\"success\\\"},{\\\"name\\\":\\\"养老机构预收费平台今日工作总结.pdf\\\",\\\"url\\\":\\\"/profile/upload/2025/12/15/养老机构预收费平台今日工作总结_20251215013214A004.pdf\\\",\\\"uid\\\":1765733534729,\\\"status\\\":\\\"success\\\"},{\\\"name\\\":\\\"02.png\\\",\\\"url\\\":\\\"/profile/upload/2025/12/15/02_20251215013224A005.png\\\",\\\"uid\\\":1765733544339,\\\"status\\\":\\\"success\\\"}]\",\"description\":\"测试\",\"elderId\":35,\"expectedUseDate\":\"2025-12-31\",\"institutionId\":2,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"updateBy\":\"jigouuser\",\"updateTime\":\"2025-12-15 01:32:33\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-15 01:32:33', '21');
INSERT INTO `sys_oper_log` VALUES ('399', '生成划拨单', '1', 'com.ruoyi.web.controller.pension.FundTransferController.generate()', 'POST', '1', 'admin', '研发部门', '/pension/fundTransfer/generate', '127.0.0.1', '内网IP', '{\"transferPeriod\":\"2025-04\"}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"createBy\":\"system\",\"createTime\":\"2025-12-16 14:45:27\",\"elderCount\":0,\"institutionId\":1,\"params\":{},\"remark\":\"系统自动生成的月度划拨\",\"transferAmount\":0,\"transferDate\":\"2025-12-16\",\"transferId\":2,\"transferNo\":\"TRF1765867527350896\",\"transferPeriod\":\"2025-04\",\"transferStatus\":\"0\",\"transferType\":\"1\"}}', '0', null, '2025-12-16 14:45:27', '42');
INSERT INTO `sys_oper_log` VALUES ('400', '押金使用审批', '2', 'com.ruoyi.web.controller.pension.SupervisionDepositController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/deposit/approval/approve/8', '127.0.0.1', '内网IP', '8', '{\"msg\":\"审批通过成功，押金已扣除\",\"code\":200}', '0', null, '2025-12-19 01:09:36', '49');
INSERT INTO `sys_oper_log` VALUES ('401', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":31,\"finalAmount\":12500,\"memberFee\":0,\"monthCount\":5,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 08:53:43', '51');
INSERT INTO `sys_oper_log` VALUES ('402', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":31,\"finalAmount\":5000,\"memberFee\":0,\"monthCount\":2,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 09:46:38', '139');
INSERT INTO `sys_oper_log` VALUES ('403', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":1000,\"elderId\":31,\"finalAmount\":11000,\"memberFee\":5000,\"monthCount\":2,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 09:58:23', '44');
INSERT INTO `sys_oper_log` VALUES ('404', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":16,\"applyAmount\":500,\"applyId\":9,\"applyNo\":\"DEP1766196626733\",\"applyReason\":\"医疗费用医疗费用医疗费用医疗费用医疗费用医疗费用\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"daigua.jpg\\\",\\\"url\\\":\\\"/profile/upload/2025/12/20/daigua_20251220101022A001.jpg\\\",\\\"uid\\\":1766196622370,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2025-12-20 10:10:26\",\"description\":\"医疗费用医疗费用医疗费用\",\"elderId\":31,\"expectedUseDate\":\"2025-12-31\",\"institutionId\":16,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 10:10:27', '23');
INSERT INTO `sys_oper_log` VALUES ('405', '押金使用审批', '2', 'com.ruoyi.web.controller.pension.SupervisionDepositController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/deposit/approval/approve/9', '127.0.0.1', '内网IP', '9', '{\"msg\":\"审批通过成功，押金已扣除\",\"code\":200}', '0', null, '2025-12-20 10:26:31', '52');
INSERT INTO `sys_oper_log` VALUES ('406', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:25:18', '26');
INSERT INTO `sys_oper_log` VALUES ('407', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/publish/4', '127.0.0.1', '内网IP', '4', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:25:58', '16');
INSERT INTO `sys_oper_log` VALUES ('408', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"actualAddress\":\"阿斯蒂芬\",\"bedCount\":0,\"bedFeeMax\":0,\"bedFeeMin\":0,\"buildingArea\":0,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-14 20:16:16\",\"creditCode\":\"12123123123\",\"institutionId\":2,\"institutionIntro\":\"测试机构简介\",\"institutionName\":\"测试服务机构\",\"isPublished\":\"1\",\"landArea\":0,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"储物柜\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/14/矩形 21@2x_20251214201555A001.png\",\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"publicId\":4,\"rating\":\"3\",\"updateBy\":\"\",\"updateTime\":\"2025-12-20 11:26:17\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:26:17', '39');
INSERT INTO `sys_oper_log` VALUES ('409', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"活动室\",\"facilityType\":\"life\",\"iconName\":\"date\",\"id\":4,\"params\":{},\"sortOrder\":4,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:26:40', '21');
INSERT INTO `sys_oper_log` VALUES ('410', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"图书阅览室\",\"facilityType\":\"life\",\"iconName\":\"education\",\"id\":5,\"params\":{},\"sortOrder\":5,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:26:45', '15');
INSERT INTO `sys_oper_log` VALUES ('411', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"电视/娱乐设备\",\"facilityType\":\"life\",\"iconName\":\"language\",\"id\":6,\"params\":{},\"sortOrder\":6,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:26:54', '0');
INSERT INTO `sys_oper_log` VALUES ('412', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"空调设备\",\"facilityType\":\"life\",\"iconName\":\"user\",\"id\":7,\"params\":{},\"sortOrder\":7,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:27:04', '12');
INSERT INTO `sys_oper_log` VALUES ('413', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"暖气设备\",\"facilityType\":\"life\",\"iconName\":\"tool\",\"id\":8,\"params\":{},\"sortOrder\":8,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:27:10', '44');
INSERT INTO `sys_oper_log` VALUES ('414', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"衣柜\",\"facilityType\":\"life\",\"iconName\":\"question\",\"id\":11,\"params\":{},\"sortOrder\":11,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:27:18', '14');
INSERT INTO `sys_oper_log` VALUES ('415', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"吧台/茶水间\",\"facilityType\":\"life\",\"iconName\":\"redis\",\"id\":12,\"params\":{},\"sortOrder\":12,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:27:25', '5');
INSERT INTO `sys_oper_log` VALUES ('416', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"阳台/露台\",\"facilityType\":\"life\",\"iconName\":\"textarea\",\"id\":13,\"params\":{},\"sortOrder\":13,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-20 11:27:41', '0');
INSERT INTO `sys_oper_log` VALUES ('417', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"121233\",\"createBy\":\"15981934928\",\"createTime\":\"2025-12-20 16:26:24\",\"environmentRating\":4,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766219183769_0\",\"url\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAGNAAABGLCAYAAADFcaW0AAAACXBIWXMAADsOAAA7DgHMtqGDAAAgAElEQVR4nOzdQQHAIADEsDH/ng8ZfZAoqIOebfsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAg8tcBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAvM1AAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIGWgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQMpAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAA', null, '1', '\r\n### Error updating database.  Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_by\' in \'field list\'\r\n### The error may exist in file [D:\\lasthm\\zijin\\fund-supervision\\ruoyi-admin\\target\\classes\\mapper\\pension\\InstitutionReviewMapper.xml]\r\n### The error may involve com.ruoyi.mapper.pension.InstitutionReviewMapper.insertInstitutionReview-Inline\r\n### The error occurred while setting parameters\r\n### SQL: insert into institution_review          ( order_id,                                       user_id,             environment_rating,             service_rating,             price_rating,             content,             images,             status,                                                                 create_by )           values ( ?,                                       ?,             ?,             ?,             ?,             ?,             ?,             ?,                                                                 ? )\r\n### Cause: java.sql.SQLSyntaxErrorException: Unknown column \'create_by\' in \'field list\'\n; bad SQL grammar []; nested exception is java.sql.SQLSyntaxErrorException: Unknown column \'create_by\' in \'field list\'', '2025-12-20 16:26:24', '502');
INSERT INTO `sys_oper_log` VALUES ('418', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"121233\",\"environmentRating\":4,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766219372442_0\",\"url\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAGNAAABGLCAYAAADFcaW0AAAACXBIWXMAADsOAAA7DgHMtqGDAAAgAElEQVR4nOzdQQHAIADEsDH/ng8ZfZAoqIOebfsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAg8tcBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAvM1AAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIGWgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQMpAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAA', '{\"msg\":\"订单不存在\",\"code\":500}', '0', null, '2025-12-20 16:29:32', '22');
INSERT INTO `sys_oper_log` VALUES ('419', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"阿斯蒂芬\",\"environmentRating\":5,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766219389834_0\",\"url\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAGNAAABGLCAYAAADFcaW0AAAACXBIWXMAADsOAAA7DgHMtqGDAAAgAElEQVR4nOzdQQHAIADEsDH/ng8ZfZAoqIOebfsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAg8tcBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAvM1AAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIGWgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQMpAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAA', '{\"msg\":\"订单不存在\",\"code\":500}', '0', null, '2025-12-20 16:29:49', '6');
INSERT INTO `sys_oper_log` VALUES ('420', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"阿斯蒂芬\",\"environmentRating\":5,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766219394278_0\",\"url\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAGNAAABGLCAYAAADFcaW0AAAACXBIWXMAADsOAAA7DgHMtqGDAAAgAElEQVR4nOzdQQHAIADEsDH/ng8ZfZAoqIOebfsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAg8tcBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAvM1AAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIGWgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQMpAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAA', '{\"msg\":\"订单不存在\",\"code\":500}', '0', null, '2025-12-20 16:29:54', '9');
INSERT INTO `sys_oper_log` VALUES ('421', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"阿斯蒂芬\",\"createTime\":\"2025-12-20 21:52:49\",\"elderId\":31,\"environmentRating\":4,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766238769141_0\",\"url\":\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/4gI0SUNDX1BST0ZJTEUAAQEAAAIkYXBwbAQAAABtbnRyUkdCIFhZWiAH4QAHAAcADQAWACBhY3NwQVBQTAAAAABBUFBMAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLWFwcGzKGpWCJX8QTTiZE9XR6hWCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApkZXNjAAAA/AAAAGVjcHJ0AAABZAAAACN3dHB0AAABiAAAABRyWFlaAAABnAAAABRnWFlaAAABsAAAABRiWFlaAAABxAAAABRyVFJDAAAB2AAAACBjaGFkAAAB+AAAACxiVFJDAAAB2AAAACBnVFJDAAAB2AAAACBkZXNjAAAAAAAAAAtEaXNwbGF5IFAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHRleHQAAAAAQ29weXJpZ2h0IEFwcGxlIEluYy4sIDIwMTcAAFhZWiAAAAAAAADzUQABAAAAARbMWFlaIAAAAAAAAIPfAAA9v////7tYWVogAAAAAAAASr8AALE3AAAKuVhZWiAAAAAAAAAoOAAAEQsAAMi5cGFyYQAAAAAAAwAAAAJmZgAA8qcAAA1ZAAAT0AAACltzZjMyAAAAAAABDEIAAAXe///zJgAAB5MAAP2Q///7ov///aMAAAPcAADAbv/bAEMACAUGBwYFCAcGBwkICAkMEwwMCwsMGBESDhMcGB0dGxgbGh8jLCUfISohGhsmNCcqLi8xMjEeJTY6NjA6LDAxMP/bAEMBCAkJDAoMFwwMFzAgGyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMP/AABEIBPwGpgMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APbqSlNFQMKDSikoAbQaKDQAlFFFACUUUUAFFFFACUYp9JQAmKMUtJmgAooooAKDRRQAUd6KKADvR2oooATNGaKM0AGaM0ZozQAlFFFAAaSlNJQAUmKdikoASilpMUAJRRRQAUUUUAKKKBRQAtLSUtABRRRQAUUUUAFFFFABRRRQAGgUUtABRSZpRQAc0tFFABRRRQAppKKWgApKKUUAFGaKKADNGaKKAFooooGFFFFABR0', '{\"msg\":\"评价提交成功，等待审核\",\"code\":200}', '0', null, '2025-12-20 21:52:49', '153');
INSERT INTO `sys_oper_log` VALUES ('422', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"阿斯蒂艾师傅\",\"createTime\":\"2025-12-22 18:38:59\",\"elderId\":31,\"environmentRating\":4,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766399939347_0\",\"url\":\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/4gI0SUNDX1BST0ZJTEUAAQEAAAIkYXBwbAQAAABtbnRyUkdCIFhZWiAH4QAHAAcADQAWACBhY3NwQVBQTAAAAABBUFBMAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLWFwcGzKGpWCJX8QTTiZE9XR6hWCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApkZXNjAAAA/AAAAGVjcHJ0AAABZAAAACN3dHB0AAABiAAAABRyWFlaAAABnAAAABRnWFlaAAABsAAAABRiWFlaAAABxAAAABRyVFJDAAAB2AAAACBjaGFkAAAB+AAAACxiVFJDAAAB2AAAACBnVFJDAAAB2AAAACBkZXNjAAAAAAAAAAtEaXNwbGF5IFAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHRleHQAAAAAQ29weXJpZ2h0IEFwcGxlIEluYy4sIDIwMTcAAFhZWiAAAAAAAADzUQABAAAAARbMWFlaIAAAAAAAAIPfAAA9v////7tYWVogAAAAAAAASr8AALE3AAAKuVhZWiAAAAAAAAAoOAAAEQsAAMi5cGFyYQAAAAAAAwAAAAJmZgAA8qcAAA1ZAAAT0AAACltzZjMyAAAAAAABDEIAAAXe///zJgAAB5MAAP2Q///7ov///aMAAAPcAADAbv/bAEMACAUGBwYFCAcGBwkICAkMEwwMCwsMGBESDhMcGB0dGxgbGh8jLCUfISohGhsmNCcqLi8xMjEeJTY6NjA6LDAxMP/bAEMBCAkJDAoMFwwMFzAgGyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMP/AABEIBPwGpgMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APbqSlNFQMKDSikoAbQaKDQAlFFFACUUUUAFFFFACUYp9JQAmKMUtJmgAooooAKDRRQAUd6KKADvR2oooATNGaKM0AGaM0ZozQAlFFFAAaSlNJQAUmKdikoASilpMUAJRRRQAUUUUAKKKBRQAtLSUtABRRRQAUUUUAFFFFABRRRQAGgUUtABRSZpRQAc0tFFABRRRQAppKKWgApKKUUAFGaKKADNGaKKAFooooGFFFFAB', '{\"msg\":\"评价提交成功，等待审核\",\"code\":200}', '0', null, '2025-12-22 18:38:59', '115');
INSERT INTO `sys_oper_log` VALUES ('423', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"阿斯蒂芬\",\"createTime\":\"2025-12-22 18:50:36\",\"elderId\":31,\"environmentRating\":4,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766400636065_0\",\"url\":\"data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQABAAD/4gI0SUNDX1BST0ZJTEUAAQEAAAIkYXBwbAQAAABtbnRyUkdCIFhZWiAH4QAHAAcADQAWACBhY3NwQVBQTAAAAABBUFBMAAAAAAAAAAAAAAAAAAAAAAAA9tYAAQAAAADTLWFwcGzKGpWCJX8QTTiZE9XR6hWCAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApkZXNjAAAA/AAAAGVjcHJ0AAABZAAAACN3dHB0AAABiAAAABRyWFlaAAABnAAAABRnWFlaAAABsAAAABRiWFlaAAABxAAAABRyVFJDAAAB2AAAACBjaGFkAAAB+AAAACxiVFJDAAAB2AAAACBnVFJDAAAB2AAAACBkZXNjAAAAAAAAAAtEaXNwbGF5IFAzAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAHRleHQAAAAAQ29weXJpZ2h0IEFwcGxlIEluYy4sIDIwMTcAAFhZWiAAAAAAAADzUQABAAAAARbMWFlaIAAAAAAAAIPfAAA9v////7tYWVogAAAAAAAASr8AALE3AAAKuVhZWiAAAAAAAAAoOAAAEQsAAMi5cGFyYQAAAAAAAwAAAAJmZgAA8qcAAA1ZAAAT0AAACltzZjMyAAAAAAABDEIAAAXe///zJgAAB5MAAP2Q///7ov///aMAAAPcAADAbv/bAEMACAUGBwYFCAcGBwkICAkMEwwMCwsMGBESDhMcGB0dGxgbGh8jLCUfISohGhsmNCcqLi8xMjEeJTY6NjA6LDAxMP/bAEMBCAkJDAoMFwwMFzAgGyAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMDAwMP/AABEIBPwGpgMBIgACEQEDEQH/xAAfAAABBQEBAQEBAQAAAAAAAAAAAQIDBAUGBwgJCgv/xAC1EAACAQMDAgQDBQUEBAAAAX0BAgMABBEFEiExQQYTUWEHInEUMoGRoQgjQrHBFVLR8CQzYnKCCQoWFxgZGiUmJygpKjQ1Njc4OTpDREVGR0hJSlNUVVZXWFlaY2RlZmdoaWpzdHV2d3h5eoOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3uLm6wsPExcbHyMnK0tPU1dbX2Nna4eLj5OXm5+jp6vHy8/T19vf4+fr/xAAfAQADAQEBAQEBAQEBAAAAAAAAAQIDBAUGBwgJCgv/xAC1EQACAQIEBAMEBwUEBAABAncAAQIDEQQFITEGEkFRB2FxEyIygQgUQpGhscEJIzNS8BVictEKFiQ04SXxFxgZGiYnKCkqNTY3ODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqCg4SFhoeIiYqSk5SVlpeYmZqio6Slpqeoqaqys7S1tre4ubrCw8TFxsfIycrS09TV1tfY2dri4+Tl5ufo6ery8/T19vf4+fr/2gAMAwEAAhEDEQA/APbqSlNFQMKDSikoAbQaKDQAlFFFACUUUUAFFFFACUYp9JQAmKMUtJmgAooooAKDRRQAUd6KKADvR2oooATNGaKM0AGaM0ZozQAlFFFAAaSlNJQAUmKdikoASilpMUAJRRRQAUUUUAKKKBRQAtLSUtABRRRQAUUUUAFFFFABRRRQAGgUUtABRSZpRQAc0tFFABRRRQAppKKWgApKKUUAFGaKKADNGaKKAFooooGFFFFABR0', '{\"msg\":\"评价提交成功，等待审核\",\"code\":200}', '0', null, '2025-12-22 18:50:36', '69');
INSERT INTO `sys_oper_log` VALUES ('424', '创建评价', '1', 'com.ruoyi.web.controller.h5.H5ReviewController.createReview()', 'POST', '1', '15981934928', null, '/h5/review/create', '127.0.0.1', '内网IP', '{\"content\":\"456456\",\"createTime\":\"2025-12-22 19:04:41\",\"elderId\":35,\"environmentRating\":2,\"imageList\":[{\"name\":\"image_1.jpg\",\"uid\":\"1766401481074_0\",\"url\":\"data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAGNAAABGLCAYAAADFcaW0AAAACXBIWXMAADsOAAA7DgHMtqGDAAAgAElEQVR4nOzdQQHAIADEsDH/ng8ZfZAoqIOebfsAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAg8tcBAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAvM1AAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAkDLQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAIGWgAQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAQMpAAwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgJSBBgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAFIGGgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEgZaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAJAy0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACBloAEAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAEDKQAMAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAICUgQYAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABSBhoAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACkDDQAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABIGWgAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACQMtAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAgZaABAAAAAAAAAAAAAAAAAAAAAAAAAAAAAABAykADAAAAAAAAAAAAAAAAAAAAAAAAAAAAAACAlIEGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAKQMNAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAUgYaAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAApAw0AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAASBloAAAAAA', '{\"msg\":\"评价提交成功，等待审核\",\"code\":200}', '0', null, '2025-12-22 19:04:41', '743');
INSERT INTO `sys_oper_log` VALUES ('425', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"question\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3600,\"menuName\":\"反馈管理\",\"menuType\":\"C\",\"orderNum\":6,\"params\":{},\"parentId\":3000,\"path\":\"feedback\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-22 23:43:23', '14');
INSERT INTO `sys_oper_log` VALUES ('426', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-11-03 16:51:30\",\"icon\":\"question\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":3600,\"menuName\":\"反馈管理\",\"menuType\":\"M\",\"orderNum\":6,\"params\":{},\"parentId\":3000,\"path\":\"feedback\",\"perms\":\"\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-22 23:44:09', '14');
INSERT INTO `sys_oper_log` VALUES ('427', '机构评价审核通过', '2', 'com.ruoyi.web.controller.supervision.InstitutionReviewController.approve()', 'PUT', '1', 'admin', '研发部门', '/supervision/review/approve/4', '127.0.0.1', '内网IP', '4 {\"approved\":false,\"params\":{},\"rejected\":false,\"reviewRemark\":\"00\",\"reviewed\":false,\"statusText\":\"未知\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-23 00:29:32', '373');
INSERT INTO `sys_oper_log` VALUES ('428', '幻灯片管理', '1', 'com.ruoyi.web.controller.system.SysBannerController.add()', 'POST', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2100,\"createBy\":\"admin\",\"imageUrl\":\"/profile/upload/2025/12/24/__85.t01479758978f719f5b_20251224121952A001.jpg\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"position\":\"1\",\"sort\":0,\"status\":\"0\",\"title\":\"水电费\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-24 12:19:55', '19');
INSERT INTO `sys_oper_log` VALUES ('429', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2100,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 12:19:55\",\"imageUrl\":\"/profile/upload/2025/12/24/750A320_20251224175155A002.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"position\":\"1\",\"sort\":0,\"status\":\"0\",\"title\":\"水电费\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-24 17:51:56', '29');
INSERT INTO `sys_oper_log` VALUES ('430', '幻灯片管理', '1', 'com.ruoyi.web.controller.system.SysBannerController.add()', 'POST', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"imageUrl\":\"/profile/upload/2025/12/24/750A320_20251224175226A003.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"position\":\"1\",\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-24 17:52:33', '19');
INSERT INTO `sys_oper_log` VALUES ('431', '通知公告', '2', 'com.ruoyi.web.controller.system.SysNoticeController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-10-28 02:47:08\",\"noticeContent\":\"<p>维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容<img src=\\\"/dev-api/profile/upload/2025/12/25/02_20251225013737A001.png\\\"></p>\",\"noticeId\":2,\"noticeTitle\":\"维护通知：2018-07-01 测试内容\",\"noticeType\":\"1\",\"params\":{},\"remark\":\"管理员\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-25 01:37:39', '22');
INSERT INTO `sys_oper_log` VALUES ('432', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"您已经收藏过该机构\",\"code\":500}', '0', null, '2025-12-25 02:20:45', '3');
INSERT INTO `sys_oper_log` VALUES ('433', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"system/banner/index\",\"createTime\":\"2025-12-24 12:12:22\",\"icon\":\"example\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":2200,\"menuName\":\"幻灯片管理\",\"menuType\":\"C\",\"orderNum\":10,\"params\":{},\"parentId\":1,\"path\":\"banner\",\"perms\":\"system:banner:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2025-12-28 23:33:11', '29');
INSERT INTO `sys_oper_log` VALUES ('434', '重置机构密码', '2', 'com.ruoyi.web.controller.supervision.InstitutionManageController.resetPassword()', 'PUT', '1', 'admin', '研发部门', '/supervision/institution/account/resetPassword/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"newPassword\":\"123456\",\"message\":\"密码重置成功\"}}', '0', null, '2026-01-06 10:56:50', '66');
INSERT INTO `sys_oper_log` VALUES ('435', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2026/01/18/2025111417630880739200001_20260118014212A006.jpg,/profile/upload/2026/01/18/qing_20260118014215A007.jpg\",\"bedCount\":0,\"bedFeeMax\":0,\"bedFeeMin\":0,\"buildingArea\":0,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-14 20:16:16\",\"creditCode\":\"12123123123\",\"environmentImgs\":\"/profile/upload/2026/01/18/2025112217637815608530001_20260118014159A001.jpg,/profile/upload/2026/01/18/2025110717624997895436968334261_20260118014202A002.jpg\",\"institutionId\":2,\"institutionIntro\":\"测试机构简介\",\"institutionName\":\"测试服务机构\",\"isPublished\":\"1\",\"landArea\":0,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"储物柜\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/14/矩形 21@2x_20251214201555A001.png\",\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/18/mianbao_20260118014217A008.jpg\",\"publicId\":4,\"rating\":\"3\",\"roomFacilities\":\"/profile/upload/2026/01/18/niuzai_20260118014205A003.jpg,/profile/upload/2026/01/18/2025111417630878474350001_20260118014207A004.jpg,/profile/upload/2026/01/18/ScreenShot_2025-11-16_201712_577_20260118014210A005.png\",\"updateBy\":\"\",\"updateTime\":\"2026-01-18 01:42:25\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-18 01:42:25', '29');
INSERT INTO `sys_oper_log` VALUES ('436', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2026/01/18/2025111417630880739200001_20260118014212A006.jpg,/profile/upload/2026/01/18/qing_20260118014215A007.jpg\",\"bedCount\":0,\"bedFeeMax\":0,\"bedFeeMin\":0,\"buildingArea\":0,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-14 20:16:16\",\"creditCode\":\"12123123123\",\"dailyServices\":\"[{\\\"time\\\":\\\"01:42\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"},{\\\"time\\\":\\\"01:43\\\",\\\"content\\\":\\\"阿萨法地方阿斯蒂芬\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/18/2025112217637815608530001_20260118014159A001.jpg,/profile/upload/2026/01/18/2025110717624997895436968334261_20260118014202A002.jpg\",\"institutionId\":2,\"institutionIntro\":\"测试机构简介\",\"institutionName\":\"测试服务机构\",\"isPublished\":\"1\",\"landArea\":0,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"储物柜\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/14/矩形 21@2x_20251214201555A001.png\",\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/18/mianbao_20260118014217A008.jpg\",\"publicId\":4,\"rating\":\"3\",\"roomFacilities\":\"/profile/upload/2026/01/18/niuzai_20260118014205A003.jpg,/profile/upload/2026/01/18/2025111417630878474350001_20260118014207A004.jpg,/profile/upload/2026/01/18/ScreenShot_2025-11-16_201712_577_20260118014210A005.png\",\"updateBy\":\"\",\"updateTime\":\"2026-01-18 01:43:06\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-18 01:43:06', '5');
INSERT INTO `sys_oper_log` VALUES ('437', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"createTime\":\"2025-10-29 22:21:46\",\"icon\":\"chart\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4000,\"menuName\":\"数据统计\",\"menuType\":\"M\",\"orderNum\":8,\"params\":{},\"parentId\":0,\"path\":\"bigscreen\",\"perms\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 01:20:56', '20');
INSERT INTO `sys_oper_log` VALUES ('438', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/4001', '127.0.0.1', '内网IP', '4001', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 02:54:37', '19');
INSERT INTO `sys_oper_log` VALUES ('439', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/4002', '127.0.0.1', '内网IP', '4002', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 02:54:43', '16');
INSERT INTO `sys_oper_log` VALUES ('440', '用户头像', '2', 'com.ruoyi.web.controller.system.SysProfileController.avatar()', 'POST', '1', 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', null, '1', '文件[favicon.ico]后缀[ico]不正确，请上传[bmp, gif, jpg, jpeg, png]格式', '2026-01-19 02:55:31', '0');
INSERT INTO `sys_oper_log` VALUES ('441', '用户头像', '2', 'com.ruoyi.web.controller.system.SysProfileController.avatar()', 'POST', '1', 'admin', '研发部门', '/system/user/profile/avatar', '127.0.0.1', '内网IP', '', '{\"msg\":\"操作成功\",\"imgUrl\":\"/profile/avatar/2026/01/19/93e1797edaf34cc8b42d158f8030a881.jpg\",\"code\":200}', '0', null, '2026-01-19 02:55:43', '65');
INSERT INTO `sys_oper_log` VALUES ('442', '个人信息', '2', 'com.ruoyi.web.controller.system.SysProfileController.updateProfile()', 'PUT', '1', 'admin', '研发部门', '/system/user/profile', '127.0.0.1', '内网IP', '{\"admin\":false,\"email\":\"ry@163.com\",\"nickName\":\"超管\",\"params\":{},\"phonenumber\":\"15888888888\",\"sex\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 02:55:45', '12');
INSERT INTO `sys_oper_log` VALUES ('443', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"admin第二个机构\",\"applyTime\":\"2026-01-19 03:29:20\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224631A002.png\",\"areaCode\":\"410102\",\"bankAccount\":\"564564\",\"bedCount\":500,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/12/04/04_20251204194031A001.png\",\"contactPerson\":\"ASDF\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X84\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"200-5000\",\"fixedAssets\":100.0,\"institutionId\":22,\"institutionName\":\"admin第二个机构\",\"institutionType\":\"nursing_home\",\"institutionTypeText\":\"未知类型(nursing_home)\",\"organizer\":\"45465\",\"params\":{},\"recordNumber\":\"789789\",\"registeredAddress\":\"admin第二个机构\",\"registeredCapital\":100.0,\"responsibleAddress\":\"admin第二个机构ASDF \",\"responsibleIdCard\":\"412829198908160071\",\"responsibleName\":\"ASDF \",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"航海西路街道办事处\",\"superviseAccount\":\"45456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/12/logo_20251112224634A003.png\",\"updateTime\":\"2026-01-19 03:29:20\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 03:29:20', '36');
INSERT INTO `sys_oper_log` VALUES ('444', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"阿斯蒂芬\",\"applyTime\":\"2026-01-19 03:29:44\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2025/11/10/banner2_20251110032850A002.png\",\"areaCode\":\"410105\",\"bankAccount\":\"64546\",\"bedCount\":27,\"businessLicense\":\"http://localhost:8080/profile/upload/2025/11/10/01_20251110032847A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91410100MA45TE2X81\",\"establishedDate\":\"2025-11-02\",\"feeRange\":\"2000\",\"fixedAssets\":3.0,\"institutionId\":16,\"institutionName\":\"admin增加的养老机构\",\"institutionType\":\"nursing_home\",\"institutionTypeText\":\"未知类型(nursing_home)\",\"organizer\":\"阿斯蒂芬\",\"params\":{},\"recordNumber\":\"45645\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":6.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"阿斯蒂芬\",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"北林路街道办事处\",\"superviseAccount\":\"454544564\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2025/11/10/banner3_20251110032853A003.png\",\"updateTime\":\"2026-01-19 03:29:44\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 03:29:44', '17');
INSERT INTO `sys_oper_log` VALUES ('445', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"陈伟\",\"contactPhone\":\"18565656565\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 03:41:35\",\"creditCode\":\"49565223221111121\",\"fixedAssets\":null,\"institutionId\":23,\"institutionName\":\"郑州好妈妈养老\",\"institutionTypeText\":\"\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"656565\",\"institutionId\":23,\"institutionName\":\"郑州好妈妈养老\",\"userName\":\"jg6565651\",\"message\":\"机构账号创建成功\"}}', '0', null, '2026-01-19 03:41:36', '150');
INSERT INTO `sys_oper_log` VALUES ('446', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 03:47:09', '29');
INSERT INTO `sys_oper_log` VALUES ('447', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"黑猩猩\",\"contactPhone\":\"18539564512\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 03:55:36\",\"creditCode\":\"455112202\",\"fixedAssets\":null,\"institutionId\":24,\"institutionName\":\"郑州黑猩猩养老\",\"institutionTypeText\":\"\",\"params\":{},\"registeredCapital\":null,\"status\":\"1\"}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"564512\",\"institutionId\":24,\"institutionName\":\"郑州黑猩猩养老\",\"userName\":\"jg564512\",\"message\":\"机构账号创建成功\"}}', '0', null, '2026-01-19 03:55:36', '83');
INSERT INTO `sys_oper_log` VALUES ('448', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"大飞\",\"contactPhone\":\"18562655625\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 03:59:58\",\"creditCode\":\"4165565622\",\"fixedAssets\":null,\"institutionId\":25,\"institutionName\":\"郑州大飞养老集团\",\"institutionTypeText\":\"\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"655625\",\"institutionId\":25,\"institutionName\":\"郑州大飞养老集团\",\"userName\":\"jg655625\",\"message\":\"机构账号创建成功\"}}', '0', null, '2026-01-19 03:59:58', '137');
INSERT INTO `sys_oper_log` VALUES ('449', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'jg655625', null, '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"测试地址\",\"applyTime\":\"2026-01-19 04:10:34\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2026/01/19/logo (2)_20260119040915A002.png\",\"areaCode\":\"410103\",\"bankAccount\":\"1213\",\"bedCount\":100,\"businessLicense\":\"http://localhost:8080/profile/upload/2026/01/19/profile_20260119040909A001.jpg\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"15565656566\",\"createTime\":\"2026-01-19 04:10:34\",\"creditCode\":\"412565645545255555\",\"establishedDate\":\"2026-01-20\",\"feeRange\":\"2000-5000/月\",\"fixedAssets\":1000.0,\"institutionId\":26,\"institutionName\":\"郑州达梦养老\",\"institutionType\":\"nursing_home\",\"institutionTypeText\":\"未知类型(nursing_home)\",\"organizer\":\"设置\",\"params\":{},\"recordNumber\":\"1123\",\"registeredAddress\":\"测试地址\",\"registeredCapital\":100.0,\"responsibleAddress\":\"阿斯蒂芬\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"wang wen\",\"responsiblePhone\":\"18539279011\",\"status\":\"0\",\"street\":\"福华街办事处\",\"superviseAccount\":\"2000\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2026/01/19/复杂微服务架构通信网络图_20260119040929A003.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 04:10:34', '26');
INSERT INTO `sys_oper_log` VALUES ('450', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/26', '127.0.0.1', '内网IP', '26', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 04:11:33', '34');
INSERT INTO `sys_oper_log` VALUES ('451', '取消收藏机构', '3', 'com.ruoyi.web.controller.h5.UserFavoriteController.removeFavorite()', 'DELETE', '1', '15981934928', null, '/h5/favorite/remove/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"取消收藏成功\",\"code\":200}', '0', null, '2026-01-19 04:35:57', '5');
INSERT INTO `sys_oper_log` VALUES ('452', '更新老人信息', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateElder()', 'POST', '1', '15981934928', null, '/h5/user/updateElder', '127.0.0.1', '内网IP', '{\"elderName\":\"继续测试\",\"relationType\":\"1\",\"age\":\"65\",\"idCard\":\"412829196008160045\",\"phone\":\"18539256565\",\"address\":\"阿第三方阿斯蒂芬\",\"healthStatus\":\"阿斯蒂芬\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/19/ScreenShot_2026-01-12_015115_648_20260119045010A005.png\",\"elderId\":\"10\"}', '{\"msg\":\"更新老人信息成功\",\"code\":200}', '0', null, '2026-01-19 04:50:11', '6');
INSERT INTO `sys_oper_log` VALUES ('453', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"actualAddress\":\"阿斯蒂芬\",\"basicFacilities\":\"/profile/upload/2026/01/18/2025111417630880739200001_20260118014212A006.jpg,/profile/upload/2026/01/18/qing_20260118014215A007.jpg\",\"bedCount\":0,\"bedFeeMax\":1000,\"bedFeeMin\":500,\"buildingArea\":0,\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2025-12-14 20:16:16\",\"creditCode\":\"12123123123\",\"dailyServices\":\"[{\\\"time\\\":\\\"01:42\\\",\\\"content\\\":\\\"阿斯蒂芬\\\"},{\\\"time\\\":\\\"01:43\\\",\\\"content\\\":\\\"阿萨法地方阿斯蒂芬\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/18/2025112217637815608530001_20260118014159A001.jpg,/profile/upload/2026/01/18/2025110717624997895436968334261_20260118014202A002.jpg\",\"institutionId\":2,\"institutionIntro\":\"测试机构简介\",\"institutionName\":\"测试服务机构\",\"isPublished\":\"1\",\"landArea\":0,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"储物柜\\\"]\",\"mainPicture\":\"/profile/upload/2025/12/14/矩形 21@2x_20251214201555A001.png\",\"mealFeeMax\":0,\"mealFeeMin\":0,\"nursingFeeMax\":0,\"nursingFeeMin\":0,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/18/mianbao_20260118014217A008.jpg\",\"publicId\":4,\"rating\":\"3\",\"roomFacilities\":\"/profile/upload/2026/01/18/niuzai_20260118014205A003.jpg,/profile/upload/2026/01/18/2025111417630878474350001_20260118014207A004.jpg,/profile/upload/2026/01/18/ScreenShot_2025-11-16_201712_577_20260118014210A005.png\",\"updateBy\":\"\",\"updateTime\":\"2026-01-19 11:15:37\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 11:15:37', '31');
INSERT INTO `sys_oper_log` VALUES ('454', '提交维护申请', '2', 'com.ruoyi.web.controller.PensionInstitutionController.submitMaintain()', 'PUT', '1', 'admin', '研发部门', '/pension/institution/maintain/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"郑州市黄河路1562号\",\"applyTime\":\"2026-01-19 11:33:33\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2026/01/19/df079c167cae476cb21790c62b610481_20260119113243A005.png\",\"areaCode\":\"410103\",\"bankAccount\":\"4564564456456\",\"bedCount\":0,\"businessLicense\":\"http://localhost:8080/profile/upload/2026/01/19/750263_20260119113239A004.png\",\"contactPerson\":\"阿斯蒂芬\",\"contactPhone\":\"18539279011\",\"creditCode\":\"91150800MA0NK4FR4F\",\"establishedDate\":\"2026-01-27\",\"feeRange\":\"2000-5000元/月\",\"fixedAssets\":0.0,\"institutionId\":2,\"institutionName\":\"测试服务机构\",\"institutionType\":\"3\",\"institutionTypeText\":\"公建民营\",\"organizer\":\"砍砍价\",\"params\":{},\"recordNumber\":\"456456\",\"registeredAddress\":\"阿斯蒂芬\",\"registeredCapital\":15.0,\"responsibleAddress\":\"郑州市黄河路\",\"responsibleIdCard\":\"412829198908160073\",\"responsibleName\":\"wenwang\",\"responsiblePhone\":\"18539279011\",\"status\":\"6\",\"street\":\"大学路办事处\",\"superviseAccount\":\"456456456\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2026/01/19/quanjing_20260119113246A006.png\",\"updateTime\":\"2026-01-19 11:33:33\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 11:33:33', '19');
INSERT INTO `sys_oper_log` VALUES ('455', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/2', '127.0.0.1', '内网IP', '2', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 11:34:38', '28');
INSERT INTO `sys_oper_log` VALUES ('456', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/22', '127.0.0.1', '内网IP', '22', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2026-01-19 12:07:37', '11');
INSERT INTO `sys_oper_log` VALUES ('457', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"晨晨\",\"relationType\":\"2\",\"age\":\"85\",\"idCard\":\"412829196008160012\",\"phone\":\"18539279011\",\"address\":\"测试地址\",\"healthStatus\":\"良好\",\"medicalHistory\":\"123\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/19/QP61014284_20260119121409A007.jpg\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2026/01/19/750262_20260119121409A008.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2026/01/19/750263_20260119121410A009.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2026-01-19 12:14:10', '10');
INSERT INTO `sys_oper_log` VALUES ('458', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":47,\"bedNumber\":\"65\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-19 12:15:25\",\"depositFee\":5000,\"floorNumber\":2,\"fullCarePrice\":15000,\"halfCarePrice\":1000,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":2,\"memberFee\":200,\"params\":{},\"price\":500,\"roomArea\":60,\"roomNumber\":\"963\",\"selfCarePrice\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 12:15:25', '40');
INSERT INTO `sys_oper_log` VALUES ('459', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":48,\"bedNumber\":\"45\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-19 12:18:18\",\"depositFee\":2000,\"floorNumber\":56,\"fullCarePrice\":3500,\"halfCarePrice\":2500,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":2,\"memberFee\":5000,\"params\":{},\"price\":600,\"roomArea\":26,\"roomNumber\":\"630\",\"selfCarePrice\":1500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 12:18:19', '28');
INSERT INTO `sys_oper_log` VALUES ('460', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'admin', '研发部门', '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":49,\"bedNumber\":\"63\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-19 13:03:12\",\"depositFee\":500,\"floorNumber\":3,\"fullCarePrice\":2000,\"halfCarePrice\":1500,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":2,\"memberFee\":2000,\"params\":{},\"price\":900,\"roomArea\":30,\"roomNumber\":\"963\",\"selfCarePrice\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 13:03:12', '15');
INSERT INTO `sys_oper_log` VALUES ('461', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":20,\"applyAmount\":100,\"applyId\":10,\"applyNo\":\"DEP1768799231437\",\"applyReason\":\"00011321321\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"750262.png\\\",\\\"url\\\":\\\"/profile/upload/2026/01/19/750262_20260119130707A001.png\\\",\\\"uid\\\":1768799227368,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 13:07:11\",\"description\":\"123132\",\"elderId\":36,\"expectedUseDate\":\"2026-01-28\",\"institutionId\":2,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 13:07:11', '10');
INSERT INTO `sys_oper_log` VALUES ('462', '押金使用审批', '2', 'com.ruoyi.web.controller.pension.SupervisionDepositController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/deposit/approval/approve/10', '127.0.0.1', '内网IP', '10', '{\"msg\":\"审批通过成功，押金已扣除\",\"code\":200}', '0', null, '2026-01-19 13:08:36', '50');
INSERT INTO `sys_oper_log` VALUES ('463', '通知公告', '2', 'com.ruoyi.web.controller.system.SysNoticeController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-10-28 02:47:08\",\"noticeContent\":\"新版本内容\",\"noticeId\":1,\"noticeTitle\":\"温馨提醒：资金监管系统即将上线\",\"noticeType\":\"2\",\"params\":{},\"remark\":\"管理员\",\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:31:14', '8');
INSERT INTO `sys_oper_log` VALUES ('464', '通知公告', '2', 'com.ruoyi.web.controller.system.SysNoticeController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2025-10-28 02:47:08\",\"noticeContent\":\"<p>维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容维护通知：2018-07-01 测试内容<img src=\\\"/dev-api/profile/upload/2025/12/25/02_20251225013737A001.png\\\"></p>\",\"noticeId\":2,\"noticeTitle\":\"维护通知：2018-07-01 测试内容\",\"noticeType\":\"1\",\"params\":{},\"remark\":\"管理员\",\"status\":\"0\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-25 01:37:39\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:31:23', '5');
INSERT INTO `sys_oper_log` VALUES ('465', '通知公告', '1', 'com.ruoyi.web.controller.system.SysNoticeController.add()', 'POST', '1', 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"noticeContent\":\"<p><img src=\\\"/dev-api/profile/upload/2026/01/19/750262_20260119153136A001.png\\\"></p>\",\"noticeTitle\":\"这是一条测试内容\",\"noticeType\":\"2\",\"params\":{},\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:31:38', '5');
INSERT INTO `sys_oper_log` VALUES ('466', '通知公告', '3', 'com.ruoyi.web.controller.system.SysNoticeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/notice/2', '127.0.0.1', '内网IP', '[2]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:31:57', '29');
INSERT INTO `sys_oper_log` VALUES ('467', '通知公告', '3', 'com.ruoyi.web.controller.system.SysNoticeController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/notice/10', '127.0.0.1', '内网IP', '[10]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:31:59', '5');
INSERT INTO `sys_oper_log` VALUES ('468', '通知公告', '1', 'com.ruoyi.web.controller.system.SysNoticeController.add()', 'POST', '1', 'admin', '研发部门', '/system/notice', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"noticeContent\":\"<p>测试通知公告</p>\",\"noticeTitle\":\"这是一条测试内容\",\"noticeType\":\"2\",\"params\":{},\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:32:14', '4');
INSERT INTO `sys_oper_log` VALUES ('469', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"陈飞飞\",\"contactPhone\":\"15693626566\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 15:40:04\",\"creditCode\":\"91410100MA45TE2681\",\"fixedAssets\":null,\"institutionId\":27,\"institutionName\":\"郑州市幸福养老院\",\"institutionTypeText\":\"\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"626566\",\"institutionId\":27,\"institutionName\":\"郑州市幸福养老院\",\"userName\":\"jg626566\",\"message\":\"机构账号创建成功\"}}', '0', null, '2026-01-19 15:40:04', '111');
INSERT INTO `sys_oper_log` VALUES ('470', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'jg626566', null, '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"郑州市黄河路67号\",\"applyTime\":\"2026-01-19 15:44:37\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2026/01/19/750263_20260119154431A003.png\",\"areaCode\":\"410105\",\"bankAccount\":\"45121221\",\"bedCount\":100,\"businessLicense\":\"http://localhost:8080/profile/upload/2026/01/19/750262_20260119154428A002.png\",\"contactPerson\":\"陈清泉\",\"contactPhone\":\"18539279011\",\"createTime\":\"2026-01-19 15:44:37\",\"creditCode\":\"91410100M45TE86568\",\"establishedDate\":\"2026-01-26\",\"feeRange\":\"2000-5000/月\",\"fixedAssets\":100.0,\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"institutionType\":\"nursing_home\",\"institutionTypeText\":\"未知类型(nursing_home)\",\"organizer\":\"金水区工商局\",\"params\":{},\"recordNumber\":\"6565\",\"registeredAddress\":\"郑州市黄河路66号\",\"registeredCapital\":100.0,\"responsibleAddress\":\"郑州市黄河路68号\",\"responsibleIdCard\":\"412829199602062351\",\"responsibleName\":\"陈清泉\",\"responsiblePhone\":\"18539279019\",\"status\":\"0\",\"street\":\"杨金路街道办事处\",\"superviseAccount\":\"1256456654\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2026/01/19/df079c167cae476cb21790c62b610481_20260119154435A004.png\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:44:37', '12');
INSERT INTO `sys_oper_log` VALUES ('471', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/28', '127.0.0.1', '内网IP', '28', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:45:07', '15');
INSERT INTO `sys_oper_log` VALUES ('472', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'jg626566', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"\",\"bedFeeMax\":1500,\"bedFeeMin\":500,\"buildingArea\":400,\"createTime\":\"2026-01-19 15:50:35\",\"dailyServices\":\"[{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"圣01\\\"},{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"02\\\"}]\",\"institutionId\":28,\"institutionIntro\":\"测试机构讲解\",\"institutionName\":\"郑州市幸福养老公司\",\"isPublished\":\"0\",\"landArea\":500,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/19/5033.jpg_wh300_20260119154949A018.jpg\",\"mealFeeMax\":2500,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"体检设备\\\"]\",\"nursingFeeMax\":1500,\"nursingFeeMin\":500,\"params\":{},\"publicId\":5}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:50:35', '20');
INSERT INTO `sys_oper_log` VALUES ('473', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'jg626566', null, '/pension/publicity/publish/5', '127.0.0.1', '内网IP', '5', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:50:42', '5');
INSERT INTO `sys_oper_log` VALUES ('474', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":50,\"bedNumber\":\"03\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-19 15:55:10\",\"depositFee\":600,\"fullCarePrice\":3000,\"halfCarePrice\":1500,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":5000,\"params\":{},\"price\":500,\"roomNumber\":\"101\",\"selfCarePrice\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 15:55:10', '8');
INSERT INTO `sys_oper_log` VALUES ('475', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2026-01-19 16:02:06\",\"elderId\":36,\"familyId\":67,\"isDefault\":\"1\",\"isMainContact\":\"1\",\"params\":{},\"phonenumber\":\"18612341234\",\"relationName\":\"女儿\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":116}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 16:02:06', '36');
INSERT INTO `sys_oper_log` VALUES ('476', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '18612341234', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"杨爸爸\",\"relationType\":\"1\",\"age\":\"80\",\"idCard\":\"41282919600220532X\",\"phone\":\"16596326523\",\"address\":\"\",\"healthStatus\":\"健康\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/19/5033.jpg_wh300_20260119160400A019.jpg\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2026/01/19/750262_20260119160400A020.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2026/01/19/750263_20260119160400A021.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2026-01-19 16:04:00', '6');
INSERT INTO `sys_oper_log` VALUES ('477', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '18612341234', null, '/h5/favorite/add/28', '127.0.0.1', '内网IP', '28', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2026-01-19 16:06:25', '6');
INSERT INTO `sys_oper_log` VALUES ('478', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":51,\"bedNumber\":\"50\",\"bedStatus\":\"0\",\"bedType\":\"2\",\"createTime\":\"2026-01-19 16:11:31\",\"depositFee\":1000,\"fullCarePrice\":1500,\"halfCarePrice\":100,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":500,\"params\":{},\"price\":1500,\"roomNumber\":\"632\",\"selfCarePrice\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 16:11:31', '18');
INSERT INTO `sys_oper_log` VALUES ('479', '押金使用申请', '1', 'com.ruoyi.web.controller.pension.DepositApplyController.add()', 'POST', '1', 'admin', '研发部门', '/pension/deposit/apply', '127.0.0.1', '内网IP', '{\"accountId\":20,\"applyAmount\":100,\"applyId\":11,\"applyNo\":\"DEP1768811102328\",\"applyReason\":\"医疗费用申请医疗费用申请医疗费用申请医疗费用申请\",\"applyStatus\":\"pending_family\",\"applyType\":\"押金使用\",\"attachments\":\"[{\\\"name\\\":\\\"750262.png\\\",\\\"url\\\":\\\"/profile/upload/2026/01/19/750262_20260119162457A022.png\\\",\\\"uid\\\":1768811097581,\\\"status\\\":\\\"success\\\"}]\",\"createBy\":\"admin\",\"createTime\":\"2026-01-19 16:25:02\",\"description\":\"医疗费用申请医疗费用申请医疗费用申请\",\"elderId\":36,\"expectedUseDate\":\"2026-01-28\",\"institutionId\":2,\"params\":{},\"purpose\":\"医疗费用\",\"remark\":\"\",\"urgencyLevel\":\"一般\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 16:25:02', '18');
INSERT INTO `sys_oper_log` VALUES ('480', '押金使用审批', '2', 'com.ruoyi.web.controller.pension.SupervisionDepositController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/deposit/approval/approve/11', '127.0.0.1', '内网IP', '11', '{\"msg\":\"审批通过成功，押金已扣除\",\"code\":200}', '0', null, '2026-01-19 16:25:55', '61');
INSERT INTO `sys_oper_log` VALUES ('481', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":28,\"finalAmount\":3650,\"memberFee\":0,\"monthCount\":5,\"paymentMethod\":\"cash\"}', null, '1', '无法获取机构信息,请联系管理员', '2026-01-19 16:39:14', '7');
INSERT INTO `sys_oper_log` VALUES ('482', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '18612341234', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"吖吖\",\"relationType\":\"5\",\"age\":\"66\",\"idCard\":\"412829195601050033\",\"phone\":\"18564523163\",\"address\":\"\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/19/5033.jpg_wh300_20260119171814A023.jpg\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2026/01/19/4255.jpg_wh860_20260119171814A024.jpg\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2026/01/19/df079c167cae476cb21790c62b610481_20260119171814A025.png\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2026-01-19 17:18:14', '20');
INSERT INTO `sys_oper_log` VALUES ('483', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2030,2032,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,4051,4052,4053,4054,4055,4056,2020,2021,2121,2122,2123,2124,2125,4017,2031,4048,4035,4036,4037,4038,4039,4040,4041,4042,4043,4044,4045,4046,4047,2040,2041,4050,4027,4028,4029,4030,4031,4032,4033,4034,4057,4058,4076,4077,4078,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090,4071,4072,4073,4074],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 18:47:45', '27');
INSERT INTO `sys_oper_log` VALUES ('484', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":3,\"orderAmount\":7400,\"orderId\":52,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：100元/月\\\\n护理费：500元/月\\\\n护理等级：自理\\\\n服务费合计：600.00元/月\\\\n缴费月数：3个月\\\\n押金：600元\\\\n会员费：5000元\\\\n总计：7400.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-19 21:31:56\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 21:31:56', '33');
INSERT INTO `sys_oper_log` VALUES ('485', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":52,\"bedNumber\":\"123\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-19 21:50:21\",\"depositFee\":1000,\"fullCarePrice\":1500,\"halfCarePrice\":500,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":1500,\"params\":{},\"price\":300,\"roomNumber\":\"26\",\"selfCarePrice\":200}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 21:50:21', '12');
INSERT INTO `sys_oper_log` VALUES ('486', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":3,\"orderAmount\":5500,\"orderId\":53,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：500元/月\\\\n护理费：500元/月\\\\n护理等级：自理\\\\n服务费合计：1000.00元/月\\\\n缴费月数：3个月\\\\n押金：1000元\\\\n会员费：1500元\\\\n总计：5500.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-19 21:53:07\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 21:53:07', '29');
INSERT INTO `sys_oper_log` VALUES ('487', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'jg626566', null, '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":9,\"finalAmount\":6000,\"memberFee\":0,\"monthCount\":6,\"paymentMethod\":\"online\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-19 23:49:39', '15');
INSERT INTO `sys_oper_log` VALUES ('488', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":53,\"bedNumber\":\"12\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-20 00:21:56\",\"depositFee\":1000,\"fullCarePrice\":600,\"halfCarePrice\":400,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":1000,\"params\":{},\"price\":500,\"roomNumber\":\"789\",\"selfCarePrice\":200}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:21:56', '15');
INSERT INTO `sys_oper_log` VALUES ('489', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":3,\"orderAmount\":6200,\"orderId\":55,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：1000元/月\\\\n护理费：400元/月\\\\n护理等级：自理\\\\n服务费合计：1400.00元/月\\\\n缴费月数：3个月\\\\n押金：1000元\\\\n会员费：1000元\\\\n总计：6200.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-20 00:23:35\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:23:35', '19');
INSERT INTO `sys_oper_log` VALUES ('490', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":54,\"bedNumber\":\"12\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-20 00:25:30\",\"depositFee\":100,\"fullCarePrice\":300,\"halfCarePrice\":200,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":100,\"params\":{},\"price\":100,\"roomNumber\":\"96\",\"selfCarePrice\":100}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:25:30', '29');
INSERT INTO `sys_oper_log` VALUES ('491', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":3,\"orderAmount\":1400,\"orderId\":56,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：200元/月\\\\n护理费：200元/月\\\\n护理等级：自理\\\\n服务费合计：400.00元/月\\\\n缴费月数：3个月\\\\n押金：100元\\\\n会员费：100元\\\\n总计：1400.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-20 00:27:57\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:27:57', '24');
INSERT INTO `sys_oper_log` VALUES ('492', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"wenwang\",\"relationType\":\"0\",\"age\":\"66\",\"idCard\":\"412829199001110073\",\"phone\":\"18539279099\",\"address\":\"测试地址\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/20/4255.jpg_wh860_20260120004132A001.jpg\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2026/01/20/750263_20260120004132A002.png\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2026/01/20/1666946350486_5586_20260120004132A003.jpeg\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2026-01-20 00:41:32', '9');
INSERT INTO `sys_oper_log` VALUES ('493', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":55,\"bedNumber\":\"10\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-20 00:42:11\",\"depositFee\":600,\"fullCarePrice\":300,\"halfCarePrice\":200,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":400,\"params\":{},\"price\":100,\"roomNumber\":\"100\",\"selfCarePrice\":100}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:42:11', '10');
INSERT INTO `sys_oper_log` VALUES ('494', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":1,\"orderAmount\":1400,\"orderId\":57,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：200元/月\\\\n护理费：200元/月\\\\n护理等级：自理\\\\n服务费合计：400.00元/月\\\\n缴费月数：1个月\\\\n押金：600元\\\\n会员费：400元\\\\n总计：1400.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-20 00:43:00\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:43:00', '31');
INSERT INTO `sys_oper_log` VALUES ('495', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'jg626566', null, '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":20,\"finalAmount\":400,\"memberFee\":0,\"monthCount\":1,\"paymentMethod\":\"online\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 00:44:26', '46');
INSERT INTO `sys_oper_log` VALUES ('496', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":56,\"bedNumber\":\"12\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-20 01:09:46\",\"depositFee\":1500,\"fullCarePrice\":900,\"halfCarePrice\":600,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"memberFee\":1000,\"params\":{},\"price\":300,\"roomNumber\":\"1563\",\"selfCarePrice\":300}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:09:46', '18');
INSERT INTO `sys_oper_log` VALUES ('497', '新增老人信息', '1', 'com.ruoyi.web.controller.h5.H5UserController.addElder()', 'POST', '1', '15981934928', null, '/h5/user/addElder', '127.0.0.1', '内网IP', '{\"elderName\":\"大脸猫\",\"relationType\":\"2\",\"age\":\"59\",\"idCard\":\"412829198008161123\",\"phone\":\"18539256444\",\"address\":\"\",\"healthStatus\":\"\",\"medicalHistory\":\"\",\"photoPath\":\"http://localhost:8080/profile/upload/2026/01/20/4255.jpg_wh860_20260120011044A001.jpg\",\"idCardFrontPath\":\"http://localhost:8080/profile/upload/2026/01/20/4255.jpg_wh860_20260120011044A002.jpg\",\"idCardBackPath\":\"http://localhost:8080/profile/upload/2026/01/20/4255.jpg_wh860_20260120011044A003.jpg\"}', '{\"msg\":\"新增老人信息成功\",\"code\":200}', '0', null, '2026-01-20 01:10:45', '13');
INSERT INTO `sys_oper_log` VALUES ('498', '审核订单', '2', 'com.ruoyi.web.controller.OrderInfoController.approve()', 'PUT', '1', 'jg626566', null, '/order/info/approve', '127.0.0.1', '内网IP', '{\"monthCount\":3,\"orderAmount\":4900,\"orderId\":59,\"orderStatus\":\"5\",\"params\":{},\"remark\":\"床位费：400元/月\\\\n护理费：400元/月\\\\n护理等级：自理\\\\n服务费合计：800.00元/月\\\\n缴费月数：3个月\\\\n押金：1500元\\\\n会员费：1000元\\\\n总计：4900.00元\",\"unpaidAmount\":0,\"updateTime\":\"2026-01-20 01:11:34\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:11:34', '47');
INSERT INTO `sys_oper_log` VALUES ('499', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'jg626566', null, '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":40,\"finalAmount\":800,\"memberFee\":0,\"monthCount\":1,\"paymentMethod\":\"online\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:20:54', '36');
INSERT INTO `sys_oper_log` VALUES ('500', '设置老人密码', '2', 'com.ruoyi.web.controller.ElderInfoController.setPassword()', 'POST', '1', 'admin', '研发部门', '/elder/info/setPassword', '127.0.0.1', '内网IP', '{\"elderId\":40}', '{\"msg\":\"密码设置成功\",\"code\":200}', '0', null, '2026-01-20 01:25:57', '9');
INSERT INTO `sys_oper_log` VALUES ('501', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'admin', '研发部门', '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2026-01-20 01:33:00\",\"elderId\":39,\"familyId\":73,\"isDefault\":\"0\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"18945645624\",\"relationName\":\"子女\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":118}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:33:00', '35');
INSERT INTO `sys_oper_log` VALUES ('502', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":40,\"finalAmount\":800,\"memberFee\":0,\"monthCount\":1,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:39:48', '41');
INSERT INTO `sys_oper_log` VALUES ('503', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'admin', '研发部门', '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":40,\"finalAmount\":1600,\"memberFee\":0,\"monthCount\":2,\"paymentMethod\":\"cash\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-20 01:57:51', '44');
INSERT INTO `sys_oper_log` VALUES ('504', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'admin', '研发部门', '/pension/publicity/unpublish/3', '127.0.0.1', '内网IP', '3', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-24 23:20:23', '20');
INSERT INTO `sys_oper_log` VALUES ('505', '新增机构评级', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addRating()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/rating/add', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-01-24 23:23:55\",\"creditCode\":\"91410100M45TE86568\",\"expireDate\":\"2027-01-20\",\"facilityScore\":25,\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"managementScore\":25,\"params\":{},\"ratingDate\":\"2026-01-20\",\"ratingId\":2,\"ratingLevel\":5,\"ratingStatus\":\"1\",\"safetyScore\":25,\"serviceScore\":25,\"totalScore\":100,\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-24 23:23:55', '25');
INSERT INTO `sys_oper_log` VALUES ('506', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.unpublish()', 'PUT', '1', 'jg626566', null, '/pension/publicity/unpublish/5', '127.0.0.1', '内网IP', '5', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 00:34:53', '14');
INSERT INTO `sys_oper_log` VALUES ('507', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'jg626566', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled,disabled,dementia\",\"actualAddress\":\"郑州市黄河路67号\",\"basicFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003525A005.jpg,/profile/upload/2026/01/25/R-C (1)_20260125003529A006.jpg\",\"bedCount\":100,\"bedFeeMax\":1500,\"bedFeeMin\":500,\"buildingArea\":400,\"contactPerson\":\"陈清泉\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:50:35\",\"creditCode\":\"91410100M45TE86568\",\"dailyServices\":\"[{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"圣01\\\"},{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"02\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/25/4235.jpg_wh860_20260125003512A001.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003517A002.jpg\",\"feeRange\":\"2000-5000/月\",\"institutionId\":28,\"institutionIntro\":\"测试机构讲解\",\"institutionName\":\"郑州市幸福养老公司\",\"isPublished\":\"0\",\"landArea\":500,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/19/5033.jpg_wh300_20260119154949A018.jpg\",\"mealFeeMax\":2500,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"体检设备\\\"]\",\"nursingFeeMax\":1500,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-24_013502_461_20260125003532A007.png\",\"publicId\":5,\"rating\":\"3\",\"recordNumber\":\"6565\",\"roomFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003519A003.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003521A004.jpg\",\"superviseAccount\":\"1256456654\",\"updateBy\":\"\",\"updateTime\":\"2026-01-25 00:35:47\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 00:35:48', '231');
INSERT INTO `sys_oper_log` VALUES ('508', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'jg626566', null, '/pension/publicity/publish/5', '127.0.0.1', '内网IP', '5', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 00:35:51', '26');
INSERT INTO `sys_oper_log` VALUES ('509', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'jg626566', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled,disabled,dementia\",\"actualAddress\":\"郑州市黄河路67号\",\"basicFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003525A005.jpg,/profile/upload/2026/01/25/R-C (1)_20260125003529A006.jpg\",\"bedCount\":100,\"bedFeeMax\":1500,\"bedFeeMin\":500,\"buildingArea\":400,\"contactPerson\":\"陈清泉\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:50:35\",\"creditCode\":\"91410100M45TE86568\",\"dailyServices\":\"[{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"圣01\\\"},{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"02\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/25/4235.jpg_wh860_20260125003512A001.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003517A002.jpg\",\"feeRange\":\"2000-5000/月\",\"institutionId\":28,\"institutionIntro\":\"测试机构讲解\",\"institutionName\":\"郑州市幸福养老公司\",\"isPublished\":\"1\",\"landArea\":500,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/19/5033.jpg_wh300_20260119154949A018.jpg\",\"mealFeeMax\":2500,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"体检设备\\\"]\",\"nursingFeeMax\":1500,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-24_013502_461_20260125003532A007.png\",\"publicId\":5,\"rating\":\"3\",\"recordNumber\":\"6565\",\"roomFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003519A003.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003521A004.jpg\",\"superviseAccount\":\"1256456654\",\"updateBy\":\"\",\"updateTime\":\"2026-01-25 01:23:11\",\"vrImage\":\"/profile/upload/2026/01/25/QP61014284_20260125012308A001.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 01:23:11', '17');
INSERT INTO `sys_oper_log` VALUES ('510', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'jg626566', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled,disabled,dementia\",\"actualAddress\":\"郑州市黄河路67号\",\"basicFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003525A005.jpg,/profile/upload/2026/01/25/R-C (1)_20260125003529A006.jpg\",\"bedCount\":100,\"bedFeeMax\":1500,\"bedFeeMin\":500,\"buildingArea\":400,\"contactPerson\":\"陈清泉\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:50:35\",\"creditCode\":\"91410100M45TE86568\",\"dailyServices\":\"[{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"圣01\\\"},{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"02\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/25/4235.jpg_wh860_20260125003512A001.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003517A002.jpg\",\"feeRange\":\"2000-5000/月\",\"institutionId\":28,\"institutionIntro\":\"测试机构讲解\",\"institutionName\":\"郑州市幸福养老公司\",\"isPublished\":\"1\",\"landArea\":500,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/19/5033.jpg_wh300_20260119154949A018.jpg\",\"mealFeeMax\":2500,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"体检设备\\\"]\",\"nursingFeeMax\":1500,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-24_013502_461_20260125003532A007.png\",\"publicId\":5,\"rating\":\"3\",\"recordNumber\":\"6565\",\"roomFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003519A003.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003521A004.jpg\",\"superviseAccount\":\"1256456654\",\"updateBy\":\"\",\"updateTime\":\"2026-01-25 01:31:55\",\"vrImage\":\"/profile/upload/2026/01/25/QP61014284_20260125013151A001.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 01:31:55', '18');
INSERT INTO `sys_oper_log` VALUES ('511', '收藏机构', '1', 'com.ruoyi.web.controller.h5.UserFavoriteController.addFavorite()', 'POST', '1', '15981934928', null, '/h5/favorite/add/28', '192.168.31.67', '内网IP', '28', '{\"msg\":\"收藏成功\",\"code\":200}', '0', null, '2026-01-25 01:43:32', '14');
INSERT INTO `sys_oper_log` VALUES ('512', '预约参观', '2', 'com.ruoyi.web.controller.pension.VisitReservationController.complete()', 'POST', '1', 'jg626566', null, '/pension/visit/complete', '127.0.0.1', '内网IP', '{\"reservationId\":7,\"reservationNo\":\"AP1769276711605\",\"visitorName\":\"信息\",\"handleRemark\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 01:45:29', '23');
INSERT INTO `sys_oper_log` VALUES ('513', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125022426A001.jpg\",\"linkType\":\"1\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:24:33', '12');
INSERT INTO `sys_oper_log` VALUES ('514', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125022426A001.jpg\",\"linkType\":\"1\",\"linkValue\":\"/page/\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:24:50', '3');
INSERT INTO `sys_oper_log` VALUES ('515', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125022657A002.jpg\",\"linkType\":\"1\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:26:59', '2');
INSERT INTO `sys_oper_log` VALUES ('516', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/750+371_20260125022708A003.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:27:09', '1');
INSERT INTO `sys_oper_log` VALUES ('517', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/750+371_20260125022708A003.png\",\"linkType\":\"1\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:27:11', '1');
INSERT INTO `sys_oper_log` VALUES ('518', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/750+371_20260125023012A004.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:30:13', '1');
INSERT INTO `sys_oper_log` VALUES ('519', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/750+371_20260125023045A001.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', null, '1', 'nested exception is org.apache.ibatis.reflection.ReflectionException: There is no getter for property named \'position\' in \'class com.ruoyi.system.domain.SysBanner\'', '2026-01-25 02:30:47', '11');
INSERT INTO `sys_oper_log` VALUES ('520', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/750+371_20260125030316A001.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 03:03:18', '20');
INSERT INTO `sys_oper_log` VALUES ('521', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2100,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 12:19:55\",\"imageUrl\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125030323A002.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"水电费\",\"updateBy\":\"admin\",\"updateTime\":\"2025-12-24 17:51:56\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 03:03:24', '13');
INSERT INTO `sys_oper_log` VALUES ('522', '幻灯片管理', '2', 'com.ruoyi.web.controller.system.SysBannerController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2101,\"createBy\":\"admin\",\"createTime\":\"2025-12-24 17:52:33\",\"imageUrl\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-25_021541_711_20260125030339A003.png\",\"linkType\":\"2\",\"linkValue\":\"baidu.com\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"阿斯蒂芬阿斯蒂芬\",\"updateBy\":\"admin\",\"updateTime\":\"2026-01-25 03:03:18\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 03:03:40', '15');
INSERT INTO `sys_oper_log` VALUES ('523', '幻灯片管理', '1', 'com.ruoyi.web.controller.system.SysBannerController.add()', 'POST', '1', 'admin', '研发部门', '/system/banner', '127.0.0.1', '内网IP', '{\"bannerId\":2102,\"createBy\":\"admin\",\"imageUrl\":\"/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125030349A004.jpg\",\"linkType\":\"1\",\"params\":{},\"sort\":0,\"status\":\"0\",\"title\":\"ces\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 03:03:52', '9');
INSERT INTO `sys_oper_log` VALUES ('524', '菜单管理', '3', 'com.ruoyi.web.controller.system.SysMenuController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/menu/2103', '127.0.0.1', '内网IP', '2103', '{\"msg\":\"存在子菜单,不允许删除\",\"code\":601}', '0', null, '2026-01-25 03:24:38', '3');
INSERT INTO `sys_oper_log` VALUES ('525', 'H5用户更新资料', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateProfile()', 'PUT', '1', '15981934928', null, '/h5/user/profile', '192.168.31.146', '内网IP', '{\"nickName\":\"123\"}', '{\"msg\":\"更新成功\",\"code\":200}', '0', null, '2026-01-25 04:23:44', '25');
INSERT INTO `sys_oper_log` VALUES ('526', 'H5用户上传头像', '2', 'com.ruoyi.web.controller.h5.H5UserController.uploadAvatar()', 'POST', '1', '15981934928', null, '/h5/user/avatar', '192.168.31.146', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200,\"avatar\":\"/profile/avatar/2026/01/25/d6dd6f91f3f640e9a0f5773e5f74fbdb.png\"}', '0', null, '2026-01-25 04:24:31', '12');
INSERT INTO `sys_oper_log` VALUES ('527', 'H5用户上传头像', '2', 'com.ruoyi.web.controller.h5.H5UserController.uploadAvatar()', 'POST', '1', '15981934928', null, '/h5/user/avatar', '192.168.31.146', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200,\"avatar\":\"/profile/avatar/2026/01/25/ec780a7935e1493d923e57ddf94166f7.png\"}', '0', null, '2026-01-25 04:24:41', '23');
INSERT INTO `sys_oper_log` VALUES ('528', 'H5用户上传头像', '2', 'com.ruoyi.web.controller.h5.H5UserController.uploadAvatar()', 'POST', '1', '15981934928', null, '/h5/user/avatar', '192.168.31.146', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200,\"avatar\":\"/profile/avatar/2026/01/25/4fe1d9604ba8467e8f789593b95798ad.png\"}', '0', null, '2026-01-25 04:24:52', '7');
INSERT INTO `sys_oper_log` VALUES ('529', 'H5用户更新资料', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateProfile()', 'PUT', '1', '15981934928', null, '/h5/user/profile', '192.168.31.146', '内网IP', '{\"realName\":\"789\"}', '{\"msg\":\"更新成功\",\"code\":200}', '0', null, '2026-01-25 04:25:07', '14');
INSERT INTO `sys_oper_log` VALUES ('530', 'H5用户上传头像', '2', 'com.ruoyi.web.controller.h5.H5UserController.uploadAvatar()', 'POST', '1', '15981934928', null, '/h5/user/avatar', '192.168.31.146', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200,\"avatar\":\"/profile/avatar/2026/01/25/380af260c1064798b440799ea2128f99.png\"}', '0', null, '2026-01-25 04:29:14', '5');
INSERT INTO `sys_oper_log` VALUES ('531', 'H5用户上传头像', '2', 'com.ruoyi.web.controller.h5.H5UserController.uploadAvatar()', 'POST', '1', '15981934928', null, '/h5/user/avatar', '192.168.31.146', '内网IP', '', '{\"msg\":\"操作成功\",\"code\":200,\"avatar\":\"/profile/avatar/2026/01/25/5c487d1f978842488863248fd2b81e2e.png\"}', '0', null, '2026-01-25 04:34:46', '24');
INSERT INTO `sys_oper_log` VALUES ('532', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"呼叫系统\",\"facilityType\":\"life\",\"iconName\":\"button\",\"id\":14,\"params\":{},\"sortOrder\":14,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:57:05', '20');
INSERT INTO `sys_oper_log` VALUES ('533', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"康复室\",\"facilityType\":\"medical\",\"iconName\":\"select\",\"id\":16,\"params\":{},\"sortOrder\":2,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:57:16', '5');
INSERT INTO `sys_oper_log` VALUES ('534', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"理疗室\",\"facilityType\":\"medical\",\"iconName\":\"tool\",\"id\":17,\"params\":{},\"sortOrder\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:57:28', '14');
INSERT INTO `sys_oper_log` VALUES ('535', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"健康监测\",\"facilityType\":\"medical\",\"iconName\":\"chart\",\"id\":18,\"params\":{},\"sortOrder\":4,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:57:37', '5');
INSERT INTO `sys_oper_log` VALUES ('536', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"药房\",\"facilityType\":\"medical\",\"iconName\":\"shopping\",\"id\":19,\"params\":{},\"sortOrder\":5,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:57:53', '12');
INSERT INTO `sys_oper_log` VALUES ('537', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"急救设备\",\"facilityType\":\"medical\",\"iconName\":\"time\",\"id\":20,\"params\":{},\"sortOrder\":6,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:58:12', '13');
INSERT INTO `sys_oper_log` VALUES ('538', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"专业医生\",\"facilityType\":\"medical\",\"iconName\":\"phone\",\"id\":21,\"params\":{},\"sortOrder\":7,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:58:27', '5');
INSERT INTO `sys_oper_log` VALUES ('539', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"护士站\",\"facilityType\":\"medical\",\"iconName\":\"user\",\"id\":22,\"params\":{},\"sortOrder\":8,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 11:58:34', '14');
INSERT INTO `sys_oper_log` VALUES ('540', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":55,\"bedNumber\":\"10\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 00:42:12\",\"depositFee\":600,\"floorNumber\":2,\"fullCarePrice\":300,\"halfCarePrice\":200,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":400,\"params\":{},\"price\":100,\"roomNumber\":\"100\",\"selfCarePrice\":100,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:01:14\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:01:14', '13');
INSERT INTO `sys_oper_log` VALUES ('541', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":50,\"bedNumber\":\"03\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:55:11\",\"depositFee\":600,\"floorNumber\":1,\"fullCarePrice\":3000,\"halfCarePrice\":1500,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":5000,\"params\":{},\"price\":500,\"roomNumber\":\"101\",\"selfCarePrice\":500,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:02:03\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:02:03', '19');
INSERT INTO `sys_oper_log` VALUES ('542', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":56,\"bedNumber\":\"12\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 01:09:46\",\"depositFee\":1500,\"fullCarePrice\":900,\"halfCarePrice\":600,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1000,\"params\":{},\"price\":300,\"roomNumber\":\"1563\",\"selfCarePrice\":300,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:02:32\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:02:32', '5');
INSERT INTO `sys_oper_log` VALUES ('543', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":56,\"bedNumber\":\"12\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 01:09:46\",\"depositFee\":1500,\"fullCarePrice\":900,\"halfCarePrice\":600,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1000,\"params\":{},\"price\":300,\"roomArea\":50,\"roomNumber\":\"1563\",\"selfCarePrice\":300,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:02:40\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:02:40', '33');
INSERT INTO `sys_oper_log` VALUES ('544', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":52,\"bedNumber\":\"123\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-19 21:50:21\",\"depositFee\":1000,\"fullCarePrice\":1500,\"halfCarePrice\":500,\"hasBalcony\":\"N\",\"hasBathroom\":\"N\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1500,\"params\":{},\"price\":300,\"roomNumber\":\"26\",\"selfCarePrice\":200,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:03:22\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:03:22', '13');
INSERT INTO `sys_oper_log` VALUES ('545', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":56,\"bedNumber\":\"12\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 01:09:46\",\"depositFee\":1500,\"floorNumber\":1,\"fullCarePrice\":900,\"halfCarePrice\":600,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1000,\"params\":{},\"price\":300,\"roomArea\":50,\"roomNumber\":\"1563\",\"selfCarePrice\":300,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:04:25\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:04:25', '33');
INSERT INTO `sys_oper_log` VALUES ('546', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":52,\"bedNumber\":\"123\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-19 21:50:21\",\"depositFee\":1000,\"floorNumber\":22,\"fullCarePrice\":1500,\"halfCarePrice\":500,\"hasBalcony\":\"N\",\"hasBathroom\":\"N\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1500,\"params\":{},\"price\":300,\"roomNumber\":\"26\",\"selfCarePrice\":200,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 12:07:42\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 12:07:42', '6');
INSERT INTO `sys_oper_log` VALUES ('547', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":51,\"bedNumber\":\"50\",\"bedStatus\":\"1\",\"bedType\":\"2\",\"createBy\":\"\",\"createTime\":\"2026-01-19 16:11:32\",\"depositFee\":1000,\"floorNumber\":1,\"fullCarePrice\":1500,\"halfCarePrice\":100,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":500,\"params\":{},\"price\":1500,\"roomNumber\":\"632\",\"selfCarePrice\":500,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 13:53:47\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 13:53:47', '9');
INSERT INTO `sys_oper_log` VALUES ('548', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":53,\"bedNumber\":\"12\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 00:21:56\",\"depositFee\":1000,\"floorNumber\":3,\"fullCarePrice\":600,\"halfCarePrice\":400,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1000,\"params\":{},\"price\":500,\"roomNumber\":\"789\",\"selfCarePrice\":200,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 13:54:14\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 13:54:14', '33');
INSERT INTO `sys_oper_log` VALUES ('549', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":52,\"bedNumber\":\"123\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-19 21:50:21\",\"depositFee\":1000,\"floorNumber\":23,\"fullCarePrice\":1500,\"halfCarePrice\":500,\"hasBalcony\":\"N\",\"hasBathroom\":\"N\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":1500,\"params\":{},\"price\":300,\"roomNumber\":\"26\",\"selfCarePrice\":200,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 13:54:38\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 13:54:38', '16');
INSERT INTO `sys_oper_log` VALUES ('550', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":54,\"bedNumber\":\"12\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-20 00:25:31\",\"depositFee\":100,\"floorNumber\":3,\"fullCarePrice\":300,\"halfCarePrice\":200,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":100,\"params\":{},\"price\":100,\"roomNumber\":\"96\",\"selfCarePrice\":100,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 13:54:56\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 13:54:56', '17');
INSERT INTO `sys_oper_log` VALUES ('551', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"pension/facility/icon/index\",\"createTime\":\"2025-12-07 20:02:06\",\"icon\":\"tree\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":4051,\"menuName\":\"设施图标配置\",\"menuType\":\"C\",\"orderNum\":3,\"params\":{},\"parentId\":0,\"path\":\"icon\",\"perms\":\"pension:facility:icon\",\"query\":\"\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:43:17', '11');
INSERT INTO `sys_oper_log` VALUES ('552', '菜单管理', '2', 'com.ruoyi.web.controller.system.SysMenuController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/menu', '127.0.0.1', '内网IP', '{\"children\":[],\"component\":\"system/h5user/index\",\"createTime\":\"2026-01-25 03:42:11\",\"icon\":\"peoples\",\"isCache\":\"0\",\"isFrame\":\"1\",\"menuId\":5001,\"menuName\":\"用户列表\",\"menuType\":\"C\",\"orderNum\":1,\"params\":{},\"parentId\":5000,\"path\":\"list\",\"perms\":\"h5user:list\",\"routeName\":\"\",\"status\":\"0\",\"updateBy\":\"admin\",\"visible\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:46:12', '16');
INSERT INTO `sys_oper_log` VALUES ('553', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2030,2032,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2121,2122,2123,2124,2125,4017,2031,4048,4035,4036,4037,4038,4039,4040,4041,4042,4043,4044,4045,4046,4047,2040,2041,4050,4027,4028,4029,4030,4031,4032,4033,4034,4057,4058,4076,4077,4078,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090,4071,4072,4073,4074],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:47:59', '19');
INSERT INTO `sys_oper_log` VALUES ('554', '设施图标配置', '2', 'com.ruoyi.web.controller.pension.FacilityIconConfigController.edit()', 'PUT', '1', 'admin', '研发部门', '/pension/facility/icon', '127.0.0.1', '内网IP', '{\"facilityName\":\"洗衣服务\",\"facilityType\":\"life\",\"iconName\":\"component\",\"id\":3,\"params\":{},\"sortOrder\":3,\"status\":\"0\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:48:13', '10');
INSERT INTO `sys_oper_log` VALUES ('555', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.edit()', 'PUT', '1', 'jg626566', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled,disabled,dementia\",\"actualAddress\":\"郑州市黄河路67号\",\"basicFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003525A005.jpg,/profile/upload/2026/01/25/R-C (1)_20260125003529A006.jpg\",\"bedCount\":100,\"bedFeeMax\":1500,\"bedFeeMin\":500,\"buildingArea\":400,\"contactPerson\":\"陈清泉\",\"contactPhone\":\"18539279011\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:50:35\",\"creditCode\":\"91410100M45TE86568\",\"dailyServices\":\"[{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"圣01\\\"},{\\\"time\\\":\\\"15:50\\\",\\\"content\\\":\\\"02\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/25/4235.jpg_wh860_20260125003512A001.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003517A002.jpg\",\"feeRange\":\"2000-5000/月\",\"institutionId\":28,\"institutionIntro\":\"测试机构讲解\",\"institutionName\":\"郑州市幸福养老公司\",\"isPublished\":\"1\",\"landArea\":500,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/19/5033.jpg_wh300_20260119154949A018.jpg\",\"mealFeeMax\":2500,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"体检设备\\\",\\\"理疗室\\\",\\\"健康监测\\\"]\",\"nursingFeeMax\":1500,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-24_013502_461_20260125003532A007.png\",\"publicId\":5,\"rating\":\"3\",\"recordNumber\":\"6565\",\"roomFacilities\":\"/profile/upload/2026/01/25/33857552_100634078108_2_20260125003519A003.jpg,/profile/upload/2026/01/25/5852.jpg_wh860_20260125003521A004.jpg\",\"superviseAccount\":\"1256456654\",\"updateBy\":\"\",\"updateTime\":\"2026-01-25 17:48:58\",\"vrImage\":\"/profile/upload/2026/01/25/QP61014284_20260125013151A001.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:48:58', '14');
INSERT INTO `sys_oper_log` VALUES ('556', '床位信息', '2', 'com.ruoyi.web.controller.BedInfoController.edit()', 'PUT', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":50,\"bedNumber\":\"03\",\"bedStatus\":\"1\",\"bedType\":\"1\",\"createBy\":\"\",\"createTime\":\"2026-01-19 15:55:11\",\"depositFee\":600,\"facilities\":\"暂无\",\"floorNumber\":1,\"fullCarePrice\":3000,\"halfCarePrice\":1500,\"hasBalcony\":\"0\",\"hasBathroom\":\"0\",\"institutionId\":28,\"institutionName\":\"郑州市幸福养老公司\",\"memberFee\":5000,\"params\":{},\"price\":500,\"roomNumber\":\"101\",\"selfCarePrice\":500,\"updateBy\":\"\",\"updateTime\":\"2026-01-25 17:49:30\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:49:30', '36');
INSERT INTO `sys_oper_log` VALUES ('557', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg626566', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":57,\"bedNumber\":\"10\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-25 17:53:28\",\"depositFee\":1000,\"floorNumber\":2,\"fullCarePrice\":1500,\"halfCarePrice\":1000,\"hasBalcony\":\"N\",\"hasBathroom\":\"Y\",\"institutionId\":28,\"memberFee\":500,\"params\":{},\"price\":500,\"roomArea\":60,\"roomNumber\":\"125\",\"selfCarePrice\":500}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 17:53:28', '6');
INSERT INTO `sys_oper_log` VALUES ('558', 'H5用户更新资料', '2', 'com.ruoyi.web.controller.h5.H5UserController.updateProfile()', 'PUT', '1', '15981934928', null, '/h5/user/profile', '127.0.0.1', '内网IP', '{\"sex\":\"1\"}', '{\"msg\":\"更新成功\",\"code\":200}', '0', null, '2026-01-25 18:07:07', '12');
INSERT INTO `sys_oper_log` VALUES ('559', '用户管理', '3', 'com.ruoyi.web.controller.system.SysUserController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/user/103,104,105,107', '127.0.0.1', '内网IP', '[103,104,105,107]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 19:14:03', '28');
INSERT INTO `sys_oper_log` VALUES ('560', '用户管理', '3', 'com.ruoyi.web.controller.system.SysUserController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/user/113,114,115', '127.0.0.1', '内网IP', '[113,114,115]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 19:14:18', '29');
INSERT INTO `sys_oper_log` VALUES ('561', '用户管理', '3', 'com.ruoyi.web.controller.system.SysUserController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/user/112', '127.0.0.1', '内网IP', '[112]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 19:15:02', '16');
INSERT INTO `sys_oper_log` VALUES ('562', '用户管理', '3', 'com.ruoyi.web.controller.system.SysUserController.remove()', 'DELETE', '1', 'admin', '研发部门', '/system/user/116,118,117,111,110,109,108,106,102,101,2', '127.0.0.1', '内网IP', '[116,118,117,111,110,109,108,106,102,101,2]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 19:15:31', '10');
INSERT INTO `sys_oper_log` VALUES ('563', '新增机构账号', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addInstitutionAccount()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/account/add', '127.0.0.1', '内网IP', '{\"contactPerson\":\"程星\",\"contactPhone\":\"15623696562\",\"createBy\":\"admin\",\"createTime\":\"2026-01-25 19:16:59\",\"creditCode\":\"91410100MA45815623\",\"fixedAssets\":null,\"institutionId\":29,\"institutionName\":\"郑州兴福养老集团\",\"institutionTypeText\":\"\",\"params\":{},\"registeredCapital\":null}', '{\"msg\":\"操作成功\",\"code\":200,\"data\":{\"password\":\"696562\",\"institutionId\":29,\"institutionName\":\"郑州兴福养老集团\",\"userName\":\"jg696562\",\"message\":\"机构账号创建成功\"}}', '0', null, '2026-01-25 19:16:59', '112');
INSERT INTO `sys_oper_log` VALUES ('564', '提交机构入驻申请', '1', 'com.ruoyi.web.controller.PensionInstitutionController.submitApply()', 'POST', '1', 'jg696562', null, '/pension/institution/apply/submit', '127.0.0.1', '内网IP', '{\"actualAddress\":\"郑州市花园路56号\",\"applyTime\":\"2026-01-25 19:25:44\",\"approvalCertificate\":\"http://localhost:8080/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125192535A002.png\",\"areaCode\":\"410105\",\"bankAccount\":\"12356321\",\"bedCount\":200,\"businessLicense\":\"http://localhost:8080/profile/upload/2026/01/25/750+371_20260125192531A001.png\",\"contactPerson\":\"陈飞雨\",\"contactPhone\":\"15626963212\",\"createTime\":\"2026-01-25 19:25:44\",\"creditCode\":\"91410100MA46852591\",\"establishedDate\":\"2009-11-30\",\"feeRange\":\"1000-5000元/月\",\"fixedAssets\":1000.0,\"institutionId\":30,\"institutionName\":\"兴福养老集团郑州花园路园区\",\"institutionType\":\"nursing_home\",\"institutionTypeText\":\"未知类型(nursing_home)\",\"organizer\":\"兴福养老集团\",\"params\":{},\"recordNumber\":\"562323\",\"registeredAddress\":\"郑州市花园路56号\",\"registeredCapital\":500.0,\"responsibleAddress\":\"郑州市花园路566号\",\"responsibleIdCard\":\"412829198905060056\",\"responsibleName\":\"李兴福\",\"responsiblePhone\":\"15623965489\",\"status\":\"0\",\"street\":\"花园路街道办事处\",\"superviseAccount\":\"569865623\",\"supervisionAgreement\":\"http://localhost:8080/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125192539A003.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 19:25:44', '69');
INSERT INTO `sys_oper_log` VALUES ('565', '养老机构入驻申请', '2', 'com.ruoyi.web.controller.pension.SupervisionInstitutionController.approve()', 'PUT', '1', 'admin', '研发部门', '/pension/supervision/institution/approval/approve/30', '127.0.0.1', '内网IP', '30', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:12:12', '17');
INSERT INTO `sys_oper_log` VALUES ('566', '养老机构公示信息', '1', 'com.ruoyi.web.controller.pension.PublicityController.add()', 'POST', '1', 'jg696562', null, '/pension/publicity', '127.0.0.1', '内网IP', '{\"acceptElderType\":\"self_care,semi_disabled,disabled,dementia\",\"basicFacilities\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-12_015115_648_20260125222632A010.png,/profile/upload/2026/01/25/1666946350486_5586_20260125222635A011.jpeg\",\"bedFeeMax\":2000,\"bedFeeMin\":700,\"buildingArea\":800,\"createTime\":\"2026-01-25 22:28:03\",\"dailyServices\":\"[{\\\"time\\\":\\\"06:00\\\",\\\"content\\\":\\\"集体起床\\\"},{\\\"time\\\":\\\"07:28\\\",\\\"content\\\":\\\"集体用餐\\\"},{\\\"time\\\":\\\"08:27\\\",\\\"content\\\":\\\"遛弯跑操\\\"}]\",\"environmentImgs\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125222613A006.png,/profile/upload/2026/01/25/750+371_20260125222615A007.png\",\"institutionId\":30,\"institutionIntro\":\"这是新建的测试机构简介\",\"institutionName\":\"兴福养老集团郑州花园路园区\",\"isPublished\":\"0\",\"landArea\":1000,\"lifeFacilities\":\"[\\\"测试设施\\\",\\\"独立卫浴\\\",\\\"紧急呼叫\\\",\\\"洗衣服务\\\",\\\"活动室\\\",\\\"图书阅览室\\\",\\\"电视/娱乐设备\\\",\\\"空调设备\\\",\\\"暖气设备\\\",\\\"无线网络\\\",\\\"呼叫系统\\\",\\\"阳台/露台\\\",\\\"吧台/茶水间\\\",\\\"衣柜\\\",\\\"储物柜\\\"]\",\"mainPicture\":\"/profile/upload/2026/01/25/ScreenShot_2026-01-25_021541_711_20260125222442A004.png\",\"mealFeeMax\":4000,\"mealFeeMin\":1500,\"medicalFacilities\":\"[\\\"医疗室\\\",\\\"专业医生\\\",\\\"护士站\\\",\\\"康复室\\\",\\\"理疗室\\\",\\\"体检设备\\\",\\\"健康监测\\\",\\\"心电图机\\\",\\\"除颤仪\\\",\\\"呼吸机\\\",\\\"氧气设备\\\",\\\"输液设备\\\"]\",\"nursingFeeMax\":2500,\"nursingFeeMin\":500,\"params\":{},\"parkFacilities\":\"/profile/upload/2026/01/25/df079c167cae476cb21790c62b610481_20260125222641A012.png\",\"publicId\":6,\"roomFacilities\":\"/profile/upload/2026/01/25/1d4a86835c7039b8bacdb9642fbf5c3d_20260125222617A008.jpg,/profile/upload/2026/01/25/ScreenShot_2026-01-25_021926_586_20260125222620A009.png\",\"serviceFeatures\":\"中医理疗、文化娱乐、营养膳食\",\"serviceScope\":\"生活照料、医疗护理、康复训练\",\"vrImage\":\"/profile/upload/2026/01/25/QP61014284_20260125222453A005.jpg\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:28:03', '28');
INSERT INTO `sys_oper_log` VALUES ('567', '养老机构公示信息', '2', 'com.ruoyi.web.controller.pension.PublicityController.publish()', 'PUT', '1', 'jg696562', null, '/pension/publicity/publish/6', '127.0.0.1', '内网IP', '6', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:28:07', '6');
INSERT INTO `sys_oper_log` VALUES ('568', '新增机构评级', '1', 'com.ruoyi.web.controller.supervision.InstitutionManageController.addRating()', 'POST', '1', 'admin', '研发部门', '/supervision/institution/rating/add', '127.0.0.1', '内网IP', '{\"createBy\":\"admin\",\"createTime\":\"2026-01-25 22:30:21\",\"creditCode\":\"91410100MA46852591\",\"expireDate\":\"2027-01-14\",\"facilityScore\":25,\"institutionId\":30,\"institutionName\":\"兴福养老集团郑州花园路园区\",\"managementScore\":25,\"params\":{},\"ratingDate\":\"2026-01-14\",\"ratingId\":3,\"ratingLevel\":5,\"ratingStatus\":\"1\",\"safetyScore\":25,\"serviceScore\":25,\"totalScore\":100,\"validityPeriod\":12}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:30:21', '11');
INSERT INTO `sys_oper_log` VALUES ('569', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg696562', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":58,\"bedNumber\":\"01\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-25 22:43:47\",\"depositFee\":500,\"facilities\":\"生活照料、医疗护理、康复训练\",\"floorNumber\":1,\"fullCarePrice\":1000,\"halfCarePrice\":500,\"hasBalcony\":\"N\",\"hasBathroom\":\"Y\",\"institutionId\":30,\"memberFee\":500,\"params\":{},\"price\":500,\"remark\":\"中医理疗、文化娱乐、营养膳食\",\"roomArea\":60,\"roomNumber\":\"101\",\"selfCarePrice\":200}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:43:47', '23');
INSERT INTO `sys_oper_log` VALUES ('570', '角色管理', '2', 'com.ruoyi.web.controller.system.SysRoleController.edit()', 'PUT', '1', 'admin', '研发部门', '/system/role', '127.0.0.1', '内网IP', '{\"admin\":false,\"createTime\":\"2025-11-10 02:20:16\",\"dataScope\":\"1\",\"delFlag\":\"0\",\"deptCheckStrictly\":true,\"flag\":false,\"menuCheckStrictly\":true,\"menuIds\":[2000,2001,2010,2011,2012,2014,2015,2016,2017,2018,2013,4009,4010,4012,4013,4014,4015,4016,2020,2021,2121,2122,2123,2124,2125,4017,2030,2031,4048,4035,4036,4037,4038,4039,4040,4041,2032,4049,4042,4043,4044,4045,4046,4047,2040,2041,4050,4027,4028,4029,4030,4031,4032,4033,4034,4057,4058,4076,4077,4078,2050,2051,4018,4019,4020,4021,4022,4023,4024,2052,4025,4026,2060,2061,2062,2070,2071,2072,2080,2090,4071,4072,4073,4074],\"params\":{},\"roleId\":100,\"roleKey\":\"jigoumanage\",\"roleName\":\"机构管理员\",\"roleSort\":3,\"status\":\"0\",\"updateBy\":\"admin\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 22:50:16', '37');
INSERT INTO `sys_oper_log` VALUES ('571', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'jg696562', null, '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"郑州市金水路65号\",\"age\":65,\"bedId\":58,\"birthDate\":\"1960-02-20\",\"careLevel\":\"1\",\"checkInDate\":\"2026-01-25\",\"depositAmount\":500,\"elderName\":\"陈菲\",\"emergencyContact\":\"详细\",\"emergencyPhone\":\"15645156456\",\"feeDescription\":\"\",\"finalAmount\":8200,\"gender\":\"2\",\"healthStatus\":\"健康\",\"idCard\":\"412829196002202356\",\"memberFee\":500,\"monthCount\":12,\"monthlyFee\":600,\"paymentMethod\":\"later\",\"phone\":\"13656456325\",\"remark\":\"\",\"specialNeeds\":\"\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:00:26', '71');
INSERT INTO `sys_oper_log` VALUES ('572', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'jg696562', null, '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2026-01-25 23:01:00\",\"elderId\":41,\"familyId\":74,\"isDefault\":\"1\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"15981934928\",\"relationName\":\"儿子\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":120}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:01:00', '44');
INSERT INTO `sys_oper_log` VALUES ('573', '床位信息', '1', 'com.ruoyi.web.controller.BedInfoController.add()', 'POST', '1', 'jg696562', null, '/elder/bed', '127.0.0.1', '内网IP', '{\"bedId\":59,\"bedNumber\":\"23\",\"bedStatus\":\"0\",\"bedType\":\"1\",\"createTime\":\"2026-01-25 23:04:14\",\"depositFee\":2000,\"floorNumber\":1,\"fullCarePrice\":2400,\"halfCarePrice\":1200,\"hasBalcony\":\"Y\",\"hasBathroom\":\"Y\",\"institutionId\":30,\"memberFee\":1000,\"params\":{},\"price\":500,\"roomArea\":60,\"roomNumber\":\"102\",\"selfCarePrice\":600}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:04:14', '45');
INSERT INTO `sys_oper_log` VALUES ('574', '养老机构入驻', '1', 'com.ruoyi.web.controller.PensionCheckinController.create()', 'POST', '1', 'jg696562', null, '/pension/checkin/create', '127.0.0.1', '内网IP', '{\"address\":\"郑州市黄河路\",\"age\":73,\"bedFee\":300,\"bedId\":59,\"birthDate\":\"1952-02-20\",\"careFee\":400,\"careLevel\":\"1\",\"checkInDate\":\"2026-01-25\",\"depositAmount\":2000,\"elderName\":\"明明\",\"emergencyContact\":\"李玲玲\",\"emergencyPhone\":\"13652325412\",\"feeDescription\":\"\",\"finalAmount\":11400,\"gender\":\"2\",\"healthStatus\":\"健康\",\"idCard\":\"412829195202202352\",\"memberFee\":1000,\"monthCount\":12,\"monthlyFee\":700,\"paymentMethod\":\"later\",\"phone\":\"13656235623\",\"remark\":\"\",\"specialNeeds\":\"无\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:18:16', '56');
INSERT INTO `sys_oper_log` VALUES ('575', '家属管理', '1', 'com.ruoyi.web.controller.elder.ElderFamilyController.add()', 'POST', '1', 'jg696562', null, '/elder/family', '127.0.0.1', '内网IP', '{\"createTime\":\"2026-01-25 23:19:11\",\"elderId\":42,\"familyId\":75,\"isDefault\":\"0\",\"isMainContact\":\"0\",\"params\":{},\"phonenumber\":\"15981934928\",\"relationName\":\"女儿\",\"relationType\":\"1\",\"status\":\"0\",\"userId\":120}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:19:11', '44');
INSERT INTO `sys_oper_log` VALUES ('576', '订单主表', '3', 'com.ruoyi.web.controller.OrderInfoController.remove()', 'DELETE', '1', 'jg696562', null, '/order/info/63', '127.0.0.1', '内网IP', '[63]', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:23:37', '805');
INSERT INTO `sys_oper_log` VALUES ('577', '入住人续费', '1', 'com.ruoyi.web.controller.PensionResidentController.renew()', 'POST', '1', 'jg696562', null, '/pension/resident/renew', '127.0.0.1', '内网IP', '{\"depositAmount\":0,\"elderId\":42,\"finalAmount\":1400,\"memberFee\":0,\"monthCount\":2,\"paymentMethod\":\"online\"}', '{\"msg\":\"操作成功\",\"code\":200}', '0', null, '2026-01-25 23:24:59', '40');

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
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='岗位信息表';

-- ----------------------------
-- Records of sys_post
-- ----------------------------
INSERT INTO `sys_post` VALUES ('1', 'ceo', '董事长', '1', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('2', 'se', '项目经理', '2', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('3', 'hr', '人力资源', '3', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('4', 'user', '普通员工', '4', '0', 'admin', '2025-10-28 02:47:08', '', null, '');
INSERT INTO `sys_post` VALUES ('5', 'jgglgw', '机构管理岗位', '0', '0', 'admin', '2025-11-10 02:23:51', '', null, null);

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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='角色信息表';

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1', '超级管理员', 'admin', '1', '1', '1', '1', '0', '0', 'admin', '2025-10-28 02:47:08', '', null, '超级管理员');
INSERT INTO `sys_role` VALUES ('2', '普通角色', 'common', '2', '2', '1', '1', '0', '0', 'admin', '2025-10-28 02:47:08', '', null, '普通角色');
INSERT INTO `sys_role` VALUES ('3', '民政监管员', 'supervision', '3', '1', '1', '1', '0', '0', 'admin', '2025-10-29 04:49:09', 'admin', '2025-11-10 11:34:51', '民政监管部门角色');
INSERT INTO `sys_role` VALUES ('100', '机构管理员', 'jigoumanage', '3', '1', '1', '1', '0', '0', 'admin', '2025-11-10 02:20:16', 'admin', '2026-01-25 22:50:16', null);

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
INSERT INTO `sys_role_menu` VALUES ('1', '2200');
INSERT INTO `sys_role_menu` VALUES ('1', '2201');
INSERT INTO `sys_role_menu` VALUES ('1', '2202');
INSERT INTO `sys_role_menu` VALUES ('1', '2203');
INSERT INTO `sys_role_menu` VALUES ('1', '2204');
INSERT INTO `sys_role_menu` VALUES ('1', '3000');
INSERT INTO `sys_role_menu` VALUES ('1', '3600');
INSERT INTO `sys_role_menu` VALUES ('1', '3601');
INSERT INTO `sys_role_menu` VALUES ('1', '3602');
INSERT INTO `sys_role_menu` VALUES ('1', '3603');
INSERT INTO `sys_role_menu` VALUES ('1', '3604');
INSERT INTO `sys_role_menu` VALUES ('1', '4051');
INSERT INTO `sys_role_menu` VALUES ('1', '4052');
INSERT INTO `sys_role_menu` VALUES ('1', '4053');
INSERT INTO `sys_role_menu` VALUES ('1', '4054');
INSERT INTO `sys_role_menu` VALUES ('1', '4055');
INSERT INTO `sys_role_menu` VALUES ('1', '4056');
INSERT INTO `sys_role_menu` VALUES ('1', '4064');
INSERT INTO `sys_role_menu` VALUES ('1', '4065');
INSERT INTO `sys_role_menu` VALUES ('1', '4066');
INSERT INTO `sys_role_menu` VALUES ('1', '4067');
INSERT INTO `sys_role_menu` VALUES ('1', '4068');
INSERT INTO `sys_role_menu` VALUES ('1', '4069');
INSERT INTO `sys_role_menu` VALUES ('1', '4070');
INSERT INTO `sys_role_menu` VALUES ('1', '4075');
INSERT INTO `sys_role_menu` VALUES ('1', '4076');
INSERT INTO `sys_role_menu` VALUES ('1', '5000');
INSERT INTO `sys_role_menu` VALUES ('1', '5001');
INSERT INTO `sys_role_menu` VALUES ('1', '5002');
INSERT INTO `sys_role_menu` VALUES ('1', '5003');
INSERT INTO `sys_role_menu` VALUES ('1', '5004');
INSERT INTO `sys_role_menu` VALUES ('1', '5005');
INSERT INTO `sys_role_menu` VALUES ('1', '5006');
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
INSERT INTO `sys_role_menu` VALUES ('3', '1');
INSERT INTO `sys_role_menu` VALUES ('3', '100');
INSERT INTO `sys_role_menu` VALUES ('3', '101');
INSERT INTO `sys_role_menu` VALUES ('3', '102');
INSERT INTO `sys_role_menu` VALUES ('3', '103');
INSERT INTO `sys_role_menu` VALUES ('3', '104');
INSERT INTO `sys_role_menu` VALUES ('3', '105');
INSERT INTO `sys_role_menu` VALUES ('3', '106');
INSERT INTO `sys_role_menu` VALUES ('3', '107');
INSERT INTO `sys_role_menu` VALUES ('3', '108');
INSERT INTO `sys_role_menu` VALUES ('3', '500');
INSERT INTO `sys_role_menu` VALUES ('3', '501');
INSERT INTO `sys_role_menu` VALUES ('3', '1000');
INSERT INTO `sys_role_menu` VALUES ('3', '1001');
INSERT INTO `sys_role_menu` VALUES ('3', '1002');
INSERT INTO `sys_role_menu` VALUES ('3', '1003');
INSERT INTO `sys_role_menu` VALUES ('3', '1004');
INSERT INTO `sys_role_menu` VALUES ('3', '1005');
INSERT INTO `sys_role_menu` VALUES ('3', '1006');
INSERT INTO `sys_role_menu` VALUES ('3', '1007');
INSERT INTO `sys_role_menu` VALUES ('3', '1008');
INSERT INTO `sys_role_menu` VALUES ('3', '1009');
INSERT INTO `sys_role_menu` VALUES ('3', '1010');
INSERT INTO `sys_role_menu` VALUES ('3', '1011');
INSERT INTO `sys_role_menu` VALUES ('3', '1012');
INSERT INTO `sys_role_menu` VALUES ('3', '1013');
INSERT INTO `sys_role_menu` VALUES ('3', '1014');
INSERT INTO `sys_role_menu` VALUES ('3', '1015');
INSERT INTO `sys_role_menu` VALUES ('3', '1016');
INSERT INTO `sys_role_menu` VALUES ('3', '1017');
INSERT INTO `sys_role_menu` VALUES ('3', '1018');
INSERT INTO `sys_role_menu` VALUES ('3', '1019');
INSERT INTO `sys_role_menu` VALUES ('3', '1020');
INSERT INTO `sys_role_menu` VALUES ('3', '1021');
INSERT INTO `sys_role_menu` VALUES ('3', '1022');
INSERT INTO `sys_role_menu` VALUES ('3', '1023');
INSERT INTO `sys_role_menu` VALUES ('3', '1024');
INSERT INTO `sys_role_menu` VALUES ('3', '1025');
INSERT INTO `sys_role_menu` VALUES ('3', '1026');
INSERT INTO `sys_role_menu` VALUES ('3', '1027');
INSERT INTO `sys_role_menu` VALUES ('3', '1028');
INSERT INTO `sys_role_menu` VALUES ('3', '1029');
INSERT INTO `sys_role_menu` VALUES ('3', '1030');
INSERT INTO `sys_role_menu` VALUES ('3', '1031');
INSERT INTO `sys_role_menu` VALUES ('3', '1032');
INSERT INTO `sys_role_menu` VALUES ('3', '1033');
INSERT INTO `sys_role_menu` VALUES ('3', '1034');
INSERT INTO `sys_role_menu` VALUES ('3', '1035');
INSERT INTO `sys_role_menu` VALUES ('3', '1036');
INSERT INTO `sys_role_menu` VALUES ('3', '1037');
INSERT INTO `sys_role_menu` VALUES ('3', '1038');
INSERT INTO `sys_role_menu` VALUES ('3', '1039');
INSERT INTO `sys_role_menu` VALUES ('3', '1040');
INSERT INTO `sys_role_menu` VALUES ('3', '1041');
INSERT INTO `sys_role_menu` VALUES ('3', '1042');
INSERT INTO `sys_role_menu` VALUES ('3', '1043');
INSERT INTO `sys_role_menu` VALUES ('3', '1044');
INSERT INTO `sys_role_menu` VALUES ('3', '1045');
INSERT INTO `sys_role_menu` VALUES ('3', '3000');
INSERT INTO `sys_role_menu` VALUES ('3', '3100');
INSERT INTO `sys_role_menu` VALUES ('3', '3101');
INSERT INTO `sys_role_menu` VALUES ('3', '3102');
INSERT INTO `sys_role_menu` VALUES ('3', '3103');
INSERT INTO `sys_role_menu` VALUES ('3', '3104');
INSERT INTO `sys_role_menu` VALUES ('3', '3105');
INSERT INTO `sys_role_menu` VALUES ('3', '3106');
INSERT INTO `sys_role_menu` VALUES ('3', '3107');
INSERT INTO `sys_role_menu` VALUES ('3', '3200');
INSERT INTO `sys_role_menu` VALUES ('3', '3201');
INSERT INTO `sys_role_menu` VALUES ('3', '3202');
INSERT INTO `sys_role_menu` VALUES ('3', '3203');
INSERT INTO `sys_role_menu` VALUES ('3', '3204');
INSERT INTO `sys_role_menu` VALUES ('3', '3205');
INSERT INTO `sys_role_menu` VALUES ('3', '3206');
INSERT INTO `sys_role_menu` VALUES ('3', '3207');
INSERT INTO `sys_role_menu` VALUES ('3', '3208');
INSERT INTO `sys_role_menu` VALUES ('3', '3300');
INSERT INTO `sys_role_menu` VALUES ('3', '3301');
INSERT INTO `sys_role_menu` VALUES ('3', '3302');
INSERT INTO `sys_role_menu` VALUES ('3', '3303');
INSERT INTO `sys_role_menu` VALUES ('3', '3304');
INSERT INTO `sys_role_menu` VALUES ('3', '3305');
INSERT INTO `sys_role_menu` VALUES ('3', '3400');
INSERT INTO `sys_role_menu` VALUES ('3', '3401');
INSERT INTO `sys_role_menu` VALUES ('3', '3402');
INSERT INTO `sys_role_menu` VALUES ('3', '3403');
INSERT INTO `sys_role_menu` VALUES ('3', '3404');
INSERT INTO `sys_role_menu` VALUES ('3', '3500');
INSERT INTO `sys_role_menu` VALUES ('3', '3501');
INSERT INTO `sys_role_menu` VALUES ('3', '3502');
INSERT INTO `sys_role_menu` VALUES ('3', '3503');
INSERT INTO `sys_role_menu` VALUES ('3', '3504');
INSERT INTO `sys_role_menu` VALUES ('3', '3600');
INSERT INTO `sys_role_menu` VALUES ('3', '3601');
INSERT INTO `sys_role_menu` VALUES ('3', '3602');
INSERT INTO `sys_role_menu` VALUES ('3', '3603');
INSERT INTO `sys_role_menu` VALUES ('3', '3604');
INSERT INTO `sys_role_menu` VALUES ('3', '4004');
INSERT INTO `sys_role_menu` VALUES ('3', '4005');
INSERT INTO `sys_role_menu` VALUES ('3', '4006');
INSERT INTO `sys_role_menu` VALUES ('3', '4007');
INSERT INTO `sys_role_menu` VALUES ('3', '4008');
INSERT INTO `sys_role_menu` VALUES ('3', '4051');
INSERT INTO `sys_role_menu` VALUES ('3', '4052');
INSERT INTO `sys_role_menu` VALUES ('3', '4053');
INSERT INTO `sys_role_menu` VALUES ('3', '4054');
INSERT INTO `sys_role_menu` VALUES ('3', '4055');
INSERT INTO `sys_role_menu` VALUES ('3', '4056');
INSERT INTO `sys_role_menu` VALUES ('100', '2000');
INSERT INTO `sys_role_menu` VALUES ('100', '2001');
INSERT INTO `sys_role_menu` VALUES ('100', '2010');
INSERT INTO `sys_role_menu` VALUES ('100', '2011');
INSERT INTO `sys_role_menu` VALUES ('100', '2012');
INSERT INTO `sys_role_menu` VALUES ('100', '2013');
INSERT INTO `sys_role_menu` VALUES ('100', '2014');
INSERT INTO `sys_role_menu` VALUES ('100', '2015');
INSERT INTO `sys_role_menu` VALUES ('100', '2016');
INSERT INTO `sys_role_menu` VALUES ('100', '2017');
INSERT INTO `sys_role_menu` VALUES ('100', '2018');
INSERT INTO `sys_role_menu` VALUES ('100', '2020');
INSERT INTO `sys_role_menu` VALUES ('100', '2021');
INSERT INTO `sys_role_menu` VALUES ('100', '2030');
INSERT INTO `sys_role_menu` VALUES ('100', '2031');
INSERT INTO `sys_role_menu` VALUES ('100', '2032');
INSERT INTO `sys_role_menu` VALUES ('100', '2040');
INSERT INTO `sys_role_menu` VALUES ('100', '2041');
INSERT INTO `sys_role_menu` VALUES ('100', '2050');
INSERT INTO `sys_role_menu` VALUES ('100', '2051');
INSERT INTO `sys_role_menu` VALUES ('100', '2052');
INSERT INTO `sys_role_menu` VALUES ('100', '2060');
INSERT INTO `sys_role_menu` VALUES ('100', '2061');
INSERT INTO `sys_role_menu` VALUES ('100', '2062');
INSERT INTO `sys_role_menu` VALUES ('100', '2070');
INSERT INTO `sys_role_menu` VALUES ('100', '2071');
INSERT INTO `sys_role_menu` VALUES ('100', '2072');
INSERT INTO `sys_role_menu` VALUES ('100', '2080');
INSERT INTO `sys_role_menu` VALUES ('100', '2090');
INSERT INTO `sys_role_menu` VALUES ('100', '2121');
INSERT INTO `sys_role_menu` VALUES ('100', '2122');
INSERT INTO `sys_role_menu` VALUES ('100', '2123');
INSERT INTO `sys_role_menu` VALUES ('100', '2124');
INSERT INTO `sys_role_menu` VALUES ('100', '2125');
INSERT INTO `sys_role_menu` VALUES ('100', '4009');
INSERT INTO `sys_role_menu` VALUES ('100', '4010');
INSERT INTO `sys_role_menu` VALUES ('100', '4012');
INSERT INTO `sys_role_menu` VALUES ('100', '4013');
INSERT INTO `sys_role_menu` VALUES ('100', '4014');
INSERT INTO `sys_role_menu` VALUES ('100', '4015');
INSERT INTO `sys_role_menu` VALUES ('100', '4016');
INSERT INTO `sys_role_menu` VALUES ('100', '4017');
INSERT INTO `sys_role_menu` VALUES ('100', '4018');
INSERT INTO `sys_role_menu` VALUES ('100', '4019');
INSERT INTO `sys_role_menu` VALUES ('100', '4020');
INSERT INTO `sys_role_menu` VALUES ('100', '4021');
INSERT INTO `sys_role_menu` VALUES ('100', '4022');
INSERT INTO `sys_role_menu` VALUES ('100', '4023');
INSERT INTO `sys_role_menu` VALUES ('100', '4024');
INSERT INTO `sys_role_menu` VALUES ('100', '4025');
INSERT INTO `sys_role_menu` VALUES ('100', '4026');
INSERT INTO `sys_role_menu` VALUES ('100', '4027');
INSERT INTO `sys_role_menu` VALUES ('100', '4028');
INSERT INTO `sys_role_menu` VALUES ('100', '4029');
INSERT INTO `sys_role_menu` VALUES ('100', '4030');
INSERT INTO `sys_role_menu` VALUES ('100', '4031');
INSERT INTO `sys_role_menu` VALUES ('100', '4032');
INSERT INTO `sys_role_menu` VALUES ('100', '4033');
INSERT INTO `sys_role_menu` VALUES ('100', '4034');
INSERT INTO `sys_role_menu` VALUES ('100', '4035');
INSERT INTO `sys_role_menu` VALUES ('100', '4036');
INSERT INTO `sys_role_menu` VALUES ('100', '4037');
INSERT INTO `sys_role_menu` VALUES ('100', '4038');
INSERT INTO `sys_role_menu` VALUES ('100', '4039');
INSERT INTO `sys_role_menu` VALUES ('100', '4040');
INSERT INTO `sys_role_menu` VALUES ('100', '4041');
INSERT INTO `sys_role_menu` VALUES ('100', '4042');
INSERT INTO `sys_role_menu` VALUES ('100', '4043');
INSERT INTO `sys_role_menu` VALUES ('100', '4044');
INSERT INTO `sys_role_menu` VALUES ('100', '4045');
INSERT INTO `sys_role_menu` VALUES ('100', '4046');
INSERT INTO `sys_role_menu` VALUES ('100', '4047');
INSERT INTO `sys_role_menu` VALUES ('100', '4048');
INSERT INTO `sys_role_menu` VALUES ('100', '4049');
INSERT INTO `sys_role_menu` VALUES ('100', '4050');
INSERT INTO `sys_role_menu` VALUES ('100', '4057');
INSERT INTO `sys_role_menu` VALUES ('100', '4058');
INSERT INTO `sys_role_menu` VALUES ('100', '4071');
INSERT INTO `sys_role_menu` VALUES ('100', '4072');
INSERT INTO `sys_role_menu` VALUES ('100', '4073');
INSERT INTO `sys_role_menu` VALUES ('100', '4074');
INSERT INTO `sys_role_menu` VALUES ('100', '4076');
INSERT INTO `sys_role_menu` VALUES ('100', '4077');
INSERT INTO `sys_role_menu` VALUES ('100', '4078');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user` (
  `user_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `dept_id` bigint(20) DEFAULT NULL COMMENT '部门ID',
  `user_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户账号',
  `nick_name` varchar(30) COLLATE utf8mb4_general_ci NOT NULL COMMENT '用户昵称',
  `real_name` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL COMMENT '真实姓名',
  `user_type` varchar(2) COLLATE utf8mb4_general_ci DEFAULT '00' COMMENT '用户类型（00系统用户）',
  `user_category` char(1) COLLATE utf8mb4_general_ci DEFAULT '1' COMMENT '用户类别(1管理用户 2H5用户)',
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
) ENGINE=InnoDB AUTO_INCREMENT=121 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='用户信息表';

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1', '103', 'admin', '超管', null, '00', '1', 'ry@163.com', '15888888888', '1', '/profile/avatar/2026/01/19/93e1797edaf34cc8b42d158f8030a881.jpg', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', '127.0.0.1', '2026-01-25 23:33:14', '2025-10-28 02:47:08', 'admin', '2025-10-28 02:47:08', 'admin', '2026-01-19 02:55:45', '管理员');
INSERT INTO `sys_user` VALUES ('2', '105', 'ry', '若依', null, '00', '1', 'ry@qq.com', '15666666666', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '2', '127.0.0.1', '2025-10-28 02:47:08', '2025-10-28 02:47:08', 'admin', '2025-10-28 02:47:08', '', null, '测试员');
INSERT INTO `sys_user` VALUES ('100', '103', 'supervision', '民政监管员', null, '00', '1', 'supervision@qq.com', '13800138001', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TP57rDdaTpe', '0', '2', '127.0.0.1', '2025-10-29 04:49:09', null, 'admin', '2025-10-29 04:49:09', '', null, '民政监管测试账号，密码：admin123');
INSERT INTO `sys_user` VALUES ('101', null, 'jigouuser', '机构测试员', null, '00', '1', '', '', '0', '', '$2a$10$xNzJB66SbFN9kTTqpZgkb..0Z.8Ex3TmjC1Yszvr5TtrDoCxL7YH6', '0', '2', '127.0.0.1', '2025-12-29 16:02:42', null, 'admin', '2025-11-10 02:23:12', '', null, null);
INSERT INTO `sys_user` VALUES ('102', '103', 'jguser', '民政监管-李宝', null, '00', '1', '', '', '0', '', '$2a$10$j4gOwdktTbOgFk6pPRj/oOOPE6FxyrdmAQSm4K1oMcJprwuwkD6Dy', '0', '2', '127.0.0.1', '2026-01-19 03:35:46', null, 'admin', '2025-11-10 11:25:55', '', null, null);
INSERT INTO `sys_user` VALUES ('103', null, 'jg279011', '测试-wenwang', null, '00', '1', '', '18539279011', '0', '', '$2a$10$26IUVNUlbedBRKvGV45OxOH89q9ugWMq.1/KhCIg/hMvTsM49Mrh.', '0', '2', '127.0.0.1', '2025-11-11 02:49:23', null, 'admin', '2025-11-10 22:36:18', '', null, null);
INSERT INTO `sys_user` VALUES ('104', null, 'jg555555', '港湾养老-陈飞', null, '00', '1', '', '13855555555', '0', '', '$2a$10$MXsmOj2bu6R8ysPuZkqxJO3ueJjK4jzHbADxRH92u06eeks5KaS6a', '0', '2', '127.0.0.1', '2025-11-10 23:10:31', null, 'admin', '2025-11-10 23:10:06', '', null, null);
INSERT INTO `sys_user` VALUES ('105', null, 'jg656565', '浏览-林秒', null, '00', '1', '', '18565656565', '0', '', '$2a$10$Jj0hgFvVPD4Xdz.bbcKWOOpQSoGZ0bKfRsO7.arfP.4IbzcCZTGiW', '0', '2', '127.0.0.1', '2025-11-10 23:23:33', null, 'admin', '2025-11-10 23:23:09', '', null, null);
INSERT INTO `sys_user` VALUES ('106', null, '15981934928', '123', '789', '00', '1', '', '15981934928', '1', '/profile/avatar/2026/01/25/5c487d1f978842488863248fd2b81e2e.png', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, '', '2025-11-14 16:44:55', '', '2026-01-25 18:07:07', null);
INSERT INTO `sys_user` VALUES ('107', null, 'jg666666', '幸福养老-陈幸福', null, '00', '1', '', '13666666666', '0', '', '$2a$10$6Yt7WIoGEfKJEFxqXO8fm.F5od86Nu6xIf3dfwk7oXX6f0NRLD3EW', '0', '2', '192.168.31.217', '2025-11-16 01:42:07', null, 'admin', '2025-11-16 00:50:24', '', null, null);
INSERT INTO `sys_user` VALUES ('108', null, '15600000000', '朋', null, '00', '1', '', '15600000000', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, '', '2025-12-03 00:25:19', '', null, null);
INSERT INTO `sys_user` VALUES ('109', null, 'elder_412829198908160052', '测试2', null, '00', '1', '', '18539279011', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, 'system', '2025-12-03 20:18:47', '', null, null);
INSERT INTO `sys_user` VALUES ('110', null, 'elder_412829198908160056', '陈飞宇', null, '00', '1', '', '18539279011', '0', '', '96e79218965eb72c92a549dd5a330112', '0', '2', '', null, null, 'system', '2025-12-12 12:06:42', '', null, null);
INSERT INTO `sys_user` VALUES ('111', null, '15611111111', 'zik', null, '00', '1', '', '15611111111', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, '', '2025-12-12 12:34:18', '', null, null);
INSERT INTO `sys_user` VALUES ('112', null, 'jg6565651', '郑州好妈妈养老-陈伟', null, '00', '1', '', '18565656565', '0', '', '$2a$10$AVYzKBNU9f77yjMSWT/JQublmob2IxrAedwyOuAYdA/0pp2diounS', '0', '2', '', null, null, 'admin', '2026-01-19 03:41:36', '', null, null);
INSERT INTO `sys_user` VALUES ('113', null, 'jg564512', '郑州黑猩猩养老-黑猩猩', null, '00', '1', '', '18539564512', '0', '', '$2a$10$mBST9FYk0UEWogVcPrQ3W.BpEVJ7Eh34o1rv9Q3Lqo9u3ojXP4yeO', '0', '2', '127.0.0.1', '2026-01-19 03:55:55', null, 'admin', '2026-01-19 03:55:36', '', null, null);
INSERT INTO `sys_user` VALUES ('114', null, 'jg655625', '郑州大飞养老集团-大飞', null, '00', '1', '', '18562655625', '0', '', '$2a$10$GHRgG2EJ7o8P9BxBwzhT6Ot1Q2wcF9XNFOzsEXWj44OMaG25dB1VC', '0', '2', '127.0.0.1', '2026-01-19 04:13:12', null, 'admin', '2026-01-19 03:59:58', '', null, null);
INSERT INTO `sys_user` VALUES ('115', null, 'jg626566', '郑州市幸福养老院-陈飞飞', null, '00', '1', '', '15693626566', '0', '', '$2a$10$4E/NUoihS5j18r8rk3fmouYRoJDUzlTFJbhCOZ.19xfrbQxB8qDgC', '0', '2', '127.0.0.1', '2026-01-25 19:13:29', null, 'admin', '2026-01-19 15:40:04', '', null, null);
INSERT INTO `sys_user` VALUES ('116', null, '18612341234', '女儿', null, '00', '1', '', '18612341234', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, '', '2026-01-19 16:02:06', '', null, null);
INSERT INTO `sys_user` VALUES ('117', null, 'elder_412829198008161123', '大脸猫', null, '00', '1', '', '18539256444', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, 'system', '2026-01-20 01:25:59', '', null, null);
INSERT INTO `sys_user` VALUES ('118', null, '18945645624', '子女', null, '00', '1', '', '18945645624', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '2', '', null, null, '', '2026-01-20 01:33:00', '', null, null);
INSERT INTO `sys_user` VALUES ('119', null, 'jg696562', '郑州兴福养老集团-程星', null, '00', '1', '', '15623696562', '0', '', '$2a$10$LdL9BDVJJHuMUol4oiy2qu3JCXtyrZ4hY9CI66PFz9OwzVjnR6LK2', '0', '0', '127.0.0.1', '2026-01-25 23:34:39', null, 'admin', '2026-01-25 19:16:59', '', null, null);
INSERT INTO `sys_user` VALUES ('120', null, '15981934928', '儿子', null, '00', '1', '', '15981934928', '0', '', 'e10adc3949ba59abbe56e057f20f883e', '0', '0', '', null, null, '', '2026-01-25 23:01:00', '', null, null);

-- ----------------------------
-- Table structure for sys_user_institution
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_institution`;
CREATE TABLE `sys_user_institution` (
  `user_id` bigint(20) NOT NULL COMMENT '鐢ㄦ埛ID',
  `institution_id` bigint(20) NOT NULL COMMENT '鏈烘瀯ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '鍒涘缓鏃堕棿',
  PRIMARY KEY (`user_id`,`institution_id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_institution_id` (`institution_id`),
  CONSTRAINT `fk_user_inst_institution` FOREIGN KEY (`institution_id`) REFERENCES `pension_institution` (`institution_id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_inst_user` FOREIGN KEY (`user_id`) REFERENCES `sys_user` (`user_id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='鐢ㄦ埛-鏈烘瀯鍏宠仈琛';

-- ----------------------------
-- Records of sys_user_institution
-- ----------------------------
INSERT INTO `sys_user_institution` VALUES ('119', '29', '2026-01-25 19:16:59');
INSERT INTO `sys_user_institution` VALUES ('119', '30', '2026-01-25 19:25:44');

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
INSERT INTO `sys_user_role` VALUES ('119', '100');

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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='浜ゆ槗娴佹按璁板綍琛';

-- ----------------------------
-- Records of transaction_record
-- ----------------------------
INSERT INTO `transaction_record` VALUES ('1', '1', '1', '1', 'TRX001', '1', '1', '5000.00', '20000.00', '25000.00', '0.00', '0.00', '0.00', '2024-01-15 10:00:00', null, null, null, 'Family Recharge', 'Zhang Son', null, 'Recharge 5000');
INSERT INTO `transaction_record` VALUES ('2', '2', '2', '1', 'TRX002', '4', '1', '2800.00', '16500.00', '13700.00', '0.00', '0.00', '0.00', '2024-02-05 10:15:00', null, null, null, 'Monthly Fee Deduction', 'System Auto', null, 'Feb Fee 2800');
INSERT INTO `transaction_record` VALUES ('3', '3', '3', '1', 'TRX003', '1', '1', '8000.00', '12500.00', '20500.00', '0.00', '0.00', '0.00', '2024-01-25 16:45:00', null, null, null, 'Family Recharge', 'Wang Grandson', null, 'Recharge 8000');

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite` (
  `favorite_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `institution_id` bigint(20) NOT NULL COMMENT '机构ID',
  `create_time` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`favorite_id`),
  UNIQUE KEY `uk_user_institution` (`user_id`,`institution_id`) COMMENT '用户机构收藏唯一约束',
  KEY `idx_user_id` (`user_id`) COMMENT '用户ID索引',
  KEY `idx_institution_id` (`institution_id`) COMMENT '机构ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户机构收藏表';

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES ('2', '106', '20', '2025-12-09 01:26:01');
INSERT INTO `user_favorite` VALUES ('3', '106', '2', '2025-12-09 01:29:55');
INSERT INTO `user_favorite` VALUES ('4', '106', '16', '2025-12-09 01:44:58');
INSERT INTO `user_favorite` VALUES ('6', '106', '22', '2026-01-19 12:07:37');
INSERT INTO `user_favorite` VALUES ('7', '116', '28', '2026-01-19 16:06:25');
INSERT INTO `user_favorite` VALUES ('8', '106', '28', '2026-01-25 01:43:33');
