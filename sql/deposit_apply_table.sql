-- 民政监管端 - 押金使用申请表
-- 执行时间: 2025-10-29
-- 字符集: utf8mb4

SET NAMES utf8mb4;

-- 删除已存在的表和数据
DROP TABLE IF EXISTS deposit_apply;
DELETE FROM sys_dict_type WHERE dict_id IN (100, 101);
DELETE FROM sys_dict_data WHERE dict_code >= 1000 AND dict_code < 1030;

-- 创建押金使用申请表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='押金使用申请表';

-- 插入字典类型 (9个字段: dict_id, dict_name, dict_type, status, create_by, create_time, update_by, update_time, remark)
INSERT INTO sys_dict_type VALUES (100, '押金申请类型', 'deposit_apply_type', '0', 'admin', NOW(), '', NULL, '押金使用申请类型列表');
INSERT INTO sys_dict_type VALUES (101, '紧急程度', 'urgency_level', '0', 'admin', NOW(), '', NULL, '紧急程度分类列表');

-- 插入字典数据 - 申请类型 (14个字段: dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, update_by, update_time, remark)
INSERT INTO sys_dict_data VALUES (1000, 1, '医疗费用', '1', 'deposit_apply_type', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '紧急医疗费用');
INSERT INTO sys_dict_data VALUES (1001, 2, '生活用品', '2', 'deposit_apply_type', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '日常生活用品');
INSERT INTO sys_dict_data VALUES (1002, 3, '康复护理', '3', 'deposit_apply_type', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '康复护理费用');
INSERT INTO sys_dict_data VALUES (1003, 4, '其他费用', '4', 'deposit_apply_type', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '其他特殊费用');

-- 插入字典数据 - 紧急程度
INSERT INTO sys_dict_data VALUES (1010, 1, '紧急', '1', 'urgency_level', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '紧急情况需立即处理');
INSERT INTO sys_dict_data VALUES (1011, 2, '普通', '2', 'urgency_level', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '普通情况按流程处理');
INSERT INTO sys_dict_data VALUES (1012, 3, '一般', '3', 'urgency_level', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '一般情况可延后处理');

-- 插入字典数据 - 申请状态
INSERT INTO sys_dict_data VALUES (1020, 1, '待审批', '0', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '等待审批处理');
INSERT INTO sys_dict_data VALUES (1021, 2, '已批准', '1', 'deposit_apply_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '审批通过可以使用');
INSERT INTO sys_dict_data VALUES (1022, 3, '已拒绝', '2', 'deposit_apply_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '审批拒绝不能使用');
INSERT INTO sys_dict_data VALUES (1023, 4, '已撤销', '3', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '申请人已撤销申请');

-- 插入测试数据
INSERT INTO deposit_apply VALUES
(1, 'DEP20251029001', 1, 1, 1, 3000.00, '老人突发疾病需要紧急医疗救治', '医疗费用', '1', '0', NULL, NULL, NULL, NULL, NULL, 'admin', NOW(), 'admin', NOW(), '紧急医疗费用申请'),
(2, 'DEP20251029002', 2, 1, 2, 1500.00, '购买轮椅等康复器材', '康复护理', '2', '0', NULL, NULL, NULL, NULL, NULL, 'admin', NOW(), 'admin', NOW(), '康复器材采购申请'),
(3, 'DEP20251029003', 3, 1, 3, 800.00, '冬季衣物和生活用品采购', '生活用品', '3', '1', 'admin', NOW() + INTERVAL 1 HOUR, '申请合理，批准使用', 800.00, NOW() + INTERVAL 2 HOUR, 'admin', NOW(), 'admin', NOW() + INTERVAL 1 HOUR, '生活用品申请已批准并使用');

COMMIT;