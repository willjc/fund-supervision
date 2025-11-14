-- 添加家属关系类型数据字典

-- 添加关系类型字典类型
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, create_by, create_time, remark)
VALUES (NULL, '家属关系类型', 'elder_relation_type', '0', 'admin', NOW(), '家属与老人的关系类型')
ON DUPLICATE KEY UPDATE dict_name='家属关系类型';

-- 添加关系类型字典数据
INSERT INTO sys_dict_data (dict_code, dict_sort, dict_label, dict_value, dict_type, status, create_by, create_time, remark)
VALUES
(NULL, 1, '子女', '1', 'elder_relation_type', '0', 'admin', NOW(), '子女关系'),
(NULL, 2, '配偶', '2', 'elder_relation_type', '0', 'admin', NOW(), '配偶关系'),
(NULL, 3, '兄弟姐妹', '3', 'elder_relation_type', '0', 'admin', NOW(), '兄弟姐妹关系'),
(NULL, 4, '其他亲属', '4', 'elder_relation_type', '0', 'admin', NOW(), '其他亲属关系'),
(NULL, 5, '朋友', '5', 'elder_relation_type', '0', 'admin', NOW(), '朋友关系')
ON DUPLICATE KEY UPDATE dict_label=VALUES(dict_label);
