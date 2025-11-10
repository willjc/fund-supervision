-- 养老机构机构类型字典
INSERT INTO sys_dict_type VALUES (100, '养老机构类型', 'pension_institution_type', '0', 'admin', sysdate(), '', null, '养老机构类型列表');
INSERT INTO sys_dict_data VALUES (100, 1, '民办', '1', 'pension_institution_type', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '民办机构');
INSERT INTO sys_dict_data VALUES (101, 2, '公办', '2', 'pension_institution_type', '', 'success', 'N', '0', 'admin', sysdate(), '', null, '公办机构');
INSERT INTO sys_dict_data VALUES (102, 3, '公建民营', '3', 'pension_institution_type', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '公建民营机构');

-- 养老机构状态字典
INSERT INTO sys_dict_type VALUES (101, '养老机构状态', 'pension_institution_status', '0', 'admin', sysdate(), '', null, '养老机构状态列表');
INSERT INTO sys_dict_data VALUES (103, 1, '待审批', '0', 'pension_institution_status', '', 'warning', 'N', '0', 'admin', sysdate(), '', null, '待审批状态');
INSERT INTO sys_dict_data VALUES (104, 2, '已入驻', '1', 'pension_institution_status', '', 'primary', 'Y', '0', 'admin', sysdate(), '', null, '已入驻状态');
INSERT INTO sys_dict_data VALUES (105, 3, '已驳回', '2', 'pension_institution_status', '', 'danger', 'N', '0', 'admin', sysdate(), '', null, '已驳回状态');
INSERT INTO sys_dict_data VALUES (106, 4, '解除监管', '3', 'pension_institution_status', '', 'info', 'N', '0', 'admin', sysdate(), '', null, '解除监管状态');