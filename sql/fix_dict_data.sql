-- 修复字典数据乱码问题，重新插入中文数据

-- 设置字符集
SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 机构类型字典
INSERT INTO `sys_dict_type` VALUES (103, '机构类型', 'pension_institution_type', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构类型列表');
INSERT INTO `sys_dict_data` VALUES (103, 1, '民办机构', '1', 'pension_institution_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '民办养老机构');
INSERT INTO `sys_dict_data` VALUES (104, 2, '公办机构', '2', 'pension_institution_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '公办养老机构');
INSERT INTO `sys_dict_data` VALUES (105, 3, '公建民营', '3', 'pension_institution_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '公建民营机构');

-- 机构状态字典
INSERT INTO `sys_dict_type` VALUES (104, '机构状态', 'pension_institution_status', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构状态列表');
INSERT INTO `sys_dict_data` VALUES (106, 1, '待审批', '0', 'pension_institution_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构入驻申请待审批');
INSERT INTO `sys_dict_data` VALUES (107, 2, '已入驻', '1', 'pension_institution_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构已正式入驻');
INSERT INTO `sys_dict_data` VALUES (108, 3, '已驳回', '2', 'pension_institution_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构入驻申请被驳回');

-- 附件类型字典
INSERT INTO `sys_dict_type` VALUES (105, '附件类型', 'pension_attach_type', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '附件材料类型列表');
INSERT INTO `sys_dict_data` VALUES (109, 1, '营业执照', '1', 'pension_attach_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构营业执照附件');
INSERT INTO `sys_dict_data` VALUES (110, 2, '批准证书', '2', 'pension_attach_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '社会福利机构设置批准证书');
INSERT INTO `sys_dict_data` VALUES (111, 3, '三方协议', '3', 'pension_attach_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', '机构+银行+民政三方监管协议');

SET FOREIGN_KEY_CHECKS = 1;