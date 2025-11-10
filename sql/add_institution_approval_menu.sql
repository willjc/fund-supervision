-- 添加机构入驻审批菜单
-- 父菜单ID: 3100 (机构管理)

-- 删除可能存在的旧菜单
DELETE FROM sys_menu WHERE menu_id = 3106;

-- 插入机构入驻审批菜单
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (
    3106,
    '机构入驻审批',
    3100,
    0,
    'approval',
    'supervision/institution/approval',
    1,
    0,
    'C',
    '0',
    '0',
    'supervision:institution:approval',
    'form',
    'admin',
    NOW(),
    '',
    NULL,
    ''
);

-- 更新其他菜单的排序
UPDATE sys_menu SET order_num = 1 WHERE menu_id = 3101;
UPDATE sys_menu SET order_num = 2 WHERE menu_id = 3106;
UPDATE sys_menu SET order_num = 3 WHERE menu_id = 3102;
UPDATE sys_menu SET order_num = 4 WHERE menu_id = 3103;
UPDATE sys_menu SET order_num = 5 WHERE menu_id = 3104;
UPDATE sys_menu SET order_num = 6 WHERE menu_id = 3105;

-- 添加机构入驻审批的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES
    ('机构入驻查询', 3106, 1, '#', '', 1, 0, 'F', '0', '0', 'supervision:institution:query', '#', 'admin', NOW(), ''),
    ('机构入驻审批', 3106, 2, '#', '', 1, 0, 'F', '0', '0', 'supervision:institution:approve', '#', 'admin', NOW(), ''),
    ('机构入驻驳回', 3106, 3, '#', '', 1, 0, 'F', '0', '0', 'supervision:institution:reject', '#', 'admin', NOW(), ''),
    ('机构入驻详情', 3106, 4, '#', '', 1, 0, 'F', '0', '0', 'supervision:institution:detail', '#', 'admin', NOW(), '');
