-- 养老机构字典数据插入脚本 (英文版本)

-- 机构类型字典
INSERT INTO `sys_dict_type` VALUES (100, 'Institution Type', 'pension_institution_type', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Institution type list');
INSERT INTO `sys_dict_data` VALUES (100, 1, 'Private Institution', '1', 'pension_institution_type', '', 'primary', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Private pension institution');
INSERT INTO `sys_dict_data` VALUES (101, 2, 'Public Institution', '2', 'pension_institution_type', '', 'success', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Public pension institution');
INSERT INTO `sys_dict_data` VALUES (102, 3, 'Public-Private Partnership', '3', 'pension_institution_type', '', 'info', 'N', '0', 'admin', '2025-10-28 10:00:00', 'ry', '2025-10-28 10:00:00', 'Public-private partnership institution');

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