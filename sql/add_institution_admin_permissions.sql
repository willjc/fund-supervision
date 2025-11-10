-- =============================================
-- 为机构管理员角色添加权限
-- 执行日期: 2025-11-11
-- 说明: 添加公示信息维护和床位管理的菜单及按钮权限
-- =============================================

-- 1. 查找机构管理员角色ID (假设角色名称为"机构管理员")
-- SELECT role_id FROM sys_role WHERE role_name = '机构管理员';
-- 如果角色不存在,需要先创建角色

-- 假设机构管理员角色ID为 100 (请根据实际情况修改)
-- 可以通过以下SQL查询实际的角色ID:
-- SELECT role_id, role_name FROM sys_role WHERE role_name LIKE '%机构%';

-- =============================================
-- 第一部分: 添加公示信息维护菜单 (如果不存在)
-- =============================================

-- 查找养老机构父菜单ID
-- SELECT menu_id FROM sys_menu WHERE menu_name = '养老机构' AND parent_id = 0;

-- 查找机构管理父菜单ID
-- SELECT menu_id FROM sys_menu WHERE menu_name = '机构管理' AND menu_type = 'M';

-- 插入公示信息维护菜单 (如果不存在)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '公示信息维护',
       (SELECT menu_id FROM sys_menu WHERE menu_name = '机构管理' AND menu_type = 'M' LIMIT 1),
       4,
       'publicity',
       'pension/institution/publicityManage',
       1,
       0,
       'C',
       '0',
       '0',
       'pension:publicity:list',
       'guide',
       'admin',
       NOW(),
       '',
       NULL,
       '公示信息维护菜单'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:list' AND menu_type = 'C'
);

-- 获取刚插入的菜单ID (用于后续按钮权限)
SET @publicity_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'pension:publicity:list' AND menu_type = 'C' LIMIT 1);

-- 插入公示信息维护的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息查询', @publicity_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息新增', @publicity_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息修改', @publicity_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息删除', @publicity_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息导出', @publicity_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:export');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '发布公示', @publicity_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:publish', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:publish');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '取消公示', @publicity_menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:unpublish', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:unpublish');

-- =============================================
-- 第二部分: 添加床位管理菜单 (如果不存在)
-- =============================================

-- 查找床位管理父菜单ID
-- SELECT menu_id FROM sys_menu WHERE menu_name = '床位管理' AND menu_type = 'M';

-- 插入床位列表菜单 (如果不存在)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
SELECT '床位列表',
       (SELECT menu_id FROM sys_menu WHERE menu_name = '床位管理' AND menu_type = 'M' LIMIT 1),
       1,
       'list',
       'pension/bed/list',
       1,
       0,
       'C',
       '0',
       '0',
       'elder:bed:list',
       'list',
       'admin',
       NOW(),
       '',
       NULL,
       '床位列表菜单'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:list' AND menu_type = 'C'
);

-- 获取刚插入的菜单ID (用于后续按钮权限)
SET @bed_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'elder:bed:list' AND menu_type = 'C' LIMIT 1);

-- 插入床位管理的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位查询', @bed_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:query');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位新增', @bed_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:add');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位修改', @bed_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:edit');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位删除', @bed_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:remove');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位导出', @bed_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:export');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位导入', @bed_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:import', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:import');

-- =============================================
-- 第三部分: 为机构管理员角色分配权限
-- =============================================

-- 请先查询机构管理员的角色ID
-- SELECT role_id, role_name FROM sys_role WHERE role_name LIKE '%机构%';

-- 然后将下面的 @institution_admin_role_id 替换为实际的角色ID
-- 示例: SET @institution_admin_role_id = 100;

-- 如果角色不存在,先创建角色
INSERT INTO sys_role (role_name, role_key, role_sort, data_scope, menu_check_strictly, dept_check_strictly, status, del_flag, create_by, create_time, remark)
SELECT '机构管理员', 'institution_admin', 3, '5', 1, 1, '0', '0', 'admin', NOW(), '养老机构管理员角色'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_role WHERE role_key = 'institution_admin'
);

-- 获取机构管理员角色ID
SET @institution_admin_role_id = (SELECT role_id FROM sys_role WHERE role_key = 'institution_admin' LIMIT 1);

-- 为机构管理员角色分配公示信息维护的所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @institution_admin_role_id, menu_id
FROM sys_menu
WHERE perms LIKE 'pension:publicity:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu
    WHERE role_id = @institution_admin_role_id
    AND menu_id = sys_menu.menu_id
  );

-- 为机构管理员角色分配床位管理的所有权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @institution_admin_role_id, menu_id
FROM sys_menu
WHERE perms LIKE 'elder:bed:%'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu
    WHERE role_id = @institution_admin_role_id
    AND menu_id = sys_menu.menu_id
  );

-- 为机构管理员角色分配父菜单权限(养老机构、机构管理、床位管理)
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT @institution_admin_role_id, menu_id
FROM sys_menu
WHERE menu_name IN ('养老机构', '机构管理', '床位管理')
  AND menu_type = 'M'
  AND NOT EXISTS (
    SELECT 1 FROM sys_role_menu
    WHERE role_id = @institution_admin_role_id
    AND menu_id = sys_menu.menu_id
  );

-- =============================================
-- 验证SQL (执行完毕后可运行以下查询验证)
-- =============================================

-- 查看机构管理员角色的所有权限
-- SELECT
--     r.role_name,
--     m.menu_name,
--     m.perms,
--     m.menu_type
-- FROM sys_role r
-- LEFT JOIN sys_role_menu rm ON r.role_id = rm.role_id
-- LEFT JOIN sys_menu m ON rm.menu_id = m.menu_id
-- WHERE r.role_key = 'institution_admin'
-- ORDER BY m.order_num;

-- 查看公示信息维护的所有权限
-- SELECT menu_id, menu_name, parent_id, perms, menu_type
-- FROM sys_menu
-- WHERE perms LIKE 'pension:publicity:%'
-- ORDER BY order_num;

-- 查看床位管理的所有权限
-- SELECT menu_id, menu_name, parent_id, perms, menu_type
-- FROM sys_menu
-- WHERE perms LIKE 'elder:bed:%'
-- ORDER BY order_num;

COMMIT;
