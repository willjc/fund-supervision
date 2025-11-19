-- ===================================================================
-- 郑州市区域字典数据(修正版)
-- 用于H5端机构列表的区域筛选和后端管理的区域选择
-- ===================================================================

-- 1. 创建区域字典类型
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (110, '养老机构区域', 'pension_district', '0', 'admin', sysdate(), '郑州市各区县街道');

-- 2. 插入郑州市市辖区数据
-- 市辖区(6个)
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1101, 1, '中原区', '410102', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市中原区'),
(1102, 2, '二七区', '410103', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市二七区'),
(1103, 3, '管城回族区', '410104', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市管城回族区'),
(1104, 4, '金水区', '410105', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市金水区'),
(1105, 5, '上街区', '410106', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市上街区'),
(1106, 6, '惠济区', '410108', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市惠济区');

-- 县级市和县(6个)
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, css_class, list_class, is_default, status, create_by, create_time, remark)
VALUES
(1107, 7, '中牟县', '410122', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市中牟县'),
(1108, 8, '巩义市', '410181', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市巩义市'),
(1109, 9, '荥阳市', '410182', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市荥阳市'),
(1110, 10, '新密市', '410183', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市新密市'),
(1111, 11, '新郑市', '410184', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市新郑市'),
(1112, 12, '登封市', '410185', 'pension_district', '', 'default', 'N', '0', 'admin', sysdate(), '郑州市登封市');

-- 说明:
-- dict_value 使用国家标准区划代码
-- 410102 = 河南省(41) + 郑州市(01) + 中原区(02)
-- 这样可以与国家标准数据对接
