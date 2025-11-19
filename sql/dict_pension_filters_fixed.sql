-- ===================================================================
-- 养老机构筛选维度字典数据(修正版)
-- 包括:机构性质、收住类型、医疗条件、星级评分
-- ===================================================================

-- 1. 机构性质字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (111, '机构性质', 'pension_institution_nature', '0', 'admin', sysdate(), '养老机构性质分类');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1113, 1, '民办', '1', 'pension_institution_nature', '', 'primary', 'N', '0', 'admin', sysdate(), '民办养老机构'),
(1114, 2, '公办', '2', 'pension_institution_nature', '', 'success', 'N', '0', 'admin', sysdate(), '公办养老机构'),
(1115, 3, '公建民营', '3', 'pension_institution_nature', '', 'info', 'N', '0', 'admin', sysdate(), '公建民营养老机构');

-- 2. 收住类型/护理等级字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (112, '收住类型', 'pension_care_level', '0', 'admin', sysdate(), '养老机构收住老人的护理等级');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1116, 1, '自理', '1', 'pension_care_level', '', 'success', 'N', '0', 'admin', sysdate(), '生活完全自理的老人'),
(1117, 2, '半护理', '2', 'pension_care_level', '', 'warning', 'N', '0', 'admin', sysdate(), '生活半自理,需部分照料'),
(1118, 3, '全护理', '3', 'pension_care_level', '', 'danger', 'N', '0', 'admin', sysdate(), '生活不能自理,需全天照料'),
(1119, 4, '失能', '4', 'pension_care_level', '', 'danger', 'N', '0', 'admin', sysdate(), '失能老人专护'),
(1120, 5, '失智', '5', 'pension_care_level', '', 'danger', 'N', '0', 'admin', sysdate(), '失智老人专护');

-- 3. 医疗条件字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (113, '医疗条件', 'pension_medical_condition', '0', 'admin', sysdate(), '养老机构医疗服务条件');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1121, 1, '内设医疗机构', '1', 'pension_medical_condition', '', 'success', 'N', '0', 'admin', sysdate(), '机构内部设有医疗机构'),
(1122, 2, '与医疗机构合作', '2', 'pension_medical_condition', '', 'primary', 'N', '0', 'admin', sysdate(), '与外部医疗机构签约合作'),
(1123, 3, '自营医疗机构', '3', 'pension_medical_condition', '', 'warning', 'N', '0', 'admin', sysdate(), '机构自营的医疗服务'),
(1124, 4, '无医养结合', '4', 'pension_medical_condition', '', 'info', 'N', '0', 'admin', sysdate(), '暂无医养结合服务');

-- 4. 星级评分字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (114, '机构星级', 'pension_rating_level', '0', 'admin', sysdate(), '养老机构星级评定');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1125, 1, '一星级', '1', 'pension_rating_level', '', 'default', 'N', '0', 'admin', sysdate(), '一星级养老机构'),
(1126, 2, '二星级', '2', 'pension_rating_level', '', 'default', 'N', '0', 'admin', sysdate(), '二星级养老机构'),
(1127, 3, '三星级', '3', 'pension_rating_level', '', 'warning', 'N', '0', 'admin', sysdate(), '三星级养老机构'),
(1128, 4, '四星级', '4', 'pension_rating_level', '', 'primary', 'N', '0', 'admin', sysdate(), '四星级养老机构'),
(1129, 5, '五星级', '5', 'pension_rating_level', '', 'danger', 'N', '0', 'admin', sysdate(), '五星级养老机构');

-- 5. 床位情况字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (115, '床位情况', 'pension_bed_status', '0', 'admin', sysdate(), '机构床位使用情况');

INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1130, 1, '全部机构', 'all', 'pension_bed_status', '', 'default', 'Y', '0', 'admin', sysdate(), '显示所有机构'),
(1131, 2, '有空位机构', 'available', 'pension_bed_status', '', 'success', 'N', '0', 'admin', sysdate(), '有空余床位的机构'),
(1132, 3, '免费入住机构', 'free', 'pension_bed_status', '', 'warning', 'N', '0', 'admin', sysdate(), '提供免费试住的机构');

-- 说明:
-- 1. dict_value 使用数字编码,便于数据库查询和比较
-- 2. list_class 用于前端显示不同颜色标签
-- 3. 收住类型支持多选,在数据库中以逗号分隔存储
-- 4. 所有字典数据可通过管理后台进行维护和扩展
