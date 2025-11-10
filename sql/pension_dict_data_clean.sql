-- 清理并重新插入字典数据

-- 先删除已存在的数据
DELETE FROM sys_dict_data WHERE dict_type IN ('pension_institution_status', 'pension_attach_type');
DELETE FROM sys_dict_type WHERE dict_type IN ('pension_institution_status', 'pension_attach_type');

-- 机构状态字典
INSERT INTO `sys_dict_type` VALUES (101, 'Institution Status', 'pension_institution_status', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution status list');
INSERT INTO `sys_dict_data` VALUES (103, 1, 'Pending Approval', '0', 'pension_institution_status', '', 'warning', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution application pending approval');
INSERT INTO `sys_dict_data` VALUES (104, 2, 'Approved', '1', 'pension_institution_status', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution approved and operational');
INSERT INTO `sys_dict_data` VALUES (105, 3, 'Rejected', '2', 'pension_institution_status', '', 'danger', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution application rejected');

-- 附件类型字典
INSERT INTO `sys_dict_type` VALUES (102, 'Attachment Type', 'pension_attach_type', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Attachment type list');
INSERT INTO `sys_dict_data` VALUES (106, 1, 'Business License', '1', 'pension_attach_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution business license attachment');
INSERT INTO `sys_dict_data` VALUES (107, 2, 'Approval Certificate', '2', 'pension_attach_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Social welfare institution approval certificate');
INSERT INTO `sys_dict_data` VALUES (108, 3, 'Tripartite Agreement', '3', 'pension_attach_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution+Bank+Civil tripartite supervision agreement');