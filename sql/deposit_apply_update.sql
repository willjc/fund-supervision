-- 押金使用申请表结构更新 - 支持双审批流程
-- 执行时间: 2025-11-13
-- 字符集: utf8mb4

SET NAMES utf8mb4;

-- 添加新字段
ALTER TABLE `deposit_apply`
ADD COLUMN `purpose` varchar(200) DEFAULT NULL COMMENT '使用事由' AFTER `urgency_level`,
ADD COLUMN `description` text DEFAULT NULL COMMENT '详细说明' AFTER `purpose`,
ADD COLUMN `expected_use_date` date DEFAULT NULL COMMENT '期望使用日期' AFTER `description`,
ADD COLUMN `attachments` text DEFAULT NULL COMMENT '申请材料附件(JSON格式存储文件路径)' AFTER `expected_use_date`,
ADD COLUMN `family_confirm_name` varchar(50) DEFAULT NULL COMMENT '家属确认人姓名' AFTER `apply_status`,
ADD COLUMN `family_relation` varchar(20) DEFAULT NULL COMMENT '家属与老人关系' AFTER `family_confirm_name`,
ADD COLUMN `family_phone` varchar(20) DEFAULT NULL COMMENT '家属联系电话' AFTER `family_relation`,
ADD COLUMN `family_approve_time` datetime DEFAULT NULL COMMENT '家属审批时间' AFTER `family_phone`,
ADD COLUMN `family_approve_opinion` varchar(500) DEFAULT NULL COMMENT '家属审批意见' AFTER `family_approve_time`;

-- 修改紧急程度字段类型和值
ALTER TABLE `deposit_apply`
MODIFY COLUMN `urgency_level` varchar(20) DEFAULT '一般' COMMENT '紧急程度(一般/紧急/非常紧急)';

-- 修改申请状态字段类型和值
ALTER TABLE `deposit_apply`
MODIFY COLUMN `apply_status` varchar(30) DEFAULT 'draft' COMMENT '申请状态(draft-草稿, pending_family-待家属审批, family_approved-家属已审批, pending_supervision-待监管审批, approved-已通过, rejected-已驳回, withdrawn-已撤回)';

-- 更新数据字典 - 紧急程度
DELETE FROM sys_dict_data WHERE dict_type = 'urgency_level' AND dict_code BETWEEN 1010 AND 1012;
INSERT INTO sys_dict_data VALUES (1010, 1, '一般', '一般', 'urgency_level', '', 'info', 'Y', '0', 'admin', NOW(), '', NULL, '一般情况可延后处理');
INSERT INTO sys_dict_data VALUES (1011, 2, '紧急', '紧急', 'urgency_level', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '紧急情况需优先处理');
INSERT INTO sys_dict_data VALUES (1012, 3, '非常紧急', '非常紧急', 'urgency_level', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '非常紧急情况需立即处理');

-- 更新数据字典 - 申请状态
DELETE FROM sys_dict_data WHERE dict_type = 'deposit_apply_status' AND dict_code BETWEEN 1020 AND 1030;
INSERT INTO sys_dict_data VALUES (1020, 1, '草稿', 'draft', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '申请草稿');
INSERT INTO sys_dict_data VALUES (1021, 2, '待家属审批', 'pending_family', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '等待家属或老人确认');
INSERT INTO sys_dict_data VALUES (1022, 3, '家属已审批', 'family_approved', 'deposit_apply_status', '', 'primary', 'N', '0', 'admin', NOW(), '', NULL, '家属已确认,待监管部门审批');
INSERT INTO sys_dict_data VALUES (1023, 4, '待监管审批', 'pending_supervision', 'deposit_apply_status', '', 'warning', 'N', '0', 'admin', NOW(), '', NULL, '等待监管部门审批');
INSERT INTO sys_dict_data VALUES (1024, 5, '已通过', 'approved', 'deposit_apply_status', '', 'success', 'N', '0', 'admin', NOW(), '', NULL, '监管部门审批通过');
INSERT INTO sys_dict_data VALUES (1025, 6, '已驳回', 'rejected', 'deposit_apply_status', '', 'danger', 'N', '0', 'admin', NOW(), '', NULL, '审批被驳回');
INSERT INTO sys_dict_data VALUES (1026, 7, '已撤回', 'withdrawn', 'deposit_apply_status', '', 'info', 'N', '0', 'admin', NOW(), '', NULL, '申请已撤回');

-- 更新现有测试数据的状态值
UPDATE deposit_apply SET apply_status = 'pending_family' WHERE apply_status = '0';
UPDATE deposit_apply SET apply_status = 'approved' WHERE apply_status = '1';
UPDATE deposit_apply SET apply_status = 'rejected' WHERE apply_status = '2';
UPDATE deposit_apply SET apply_status = 'withdrawn' WHERE apply_status = '3';

-- 更新现有测试数据的紧急程度值
UPDATE deposit_apply SET urgency_level = '紧急' WHERE urgency_level = '1';
UPDATE deposit_apply SET urgency_level = '一般' WHERE urgency_level = '2' OR urgency_level = '3';

COMMIT;
