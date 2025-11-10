-- =============================================
-- 创建公示信息维护和床位列表的菜单及按钮权限
-- 执行日期: 2025-11-11
-- 说明: 仅创建菜单和按钮权限,不分配给任何角色
-- =============================================

-- 设置字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- =============================================
-- 第一部分: 公示信息维护菜单及按钮权限
-- =============================================

-- 1. 查找机构管理父菜单ID
SET @institution_manage_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '机构管理' AND menu_type = 'M' LIMIT 1);

-- 2. 插入公示信息维护菜单 (如果不存在)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息维护',
       @institution_manage_menu_id,
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
       '公示信息维护菜单'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:list' AND menu_type = 'C'
);

-- 3. 获取公示信息维护菜单ID
SET @publicity_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'pension:publicity:list' AND menu_type = 'C' LIMIT 1);

-- 4. 插入公示信息维护的按钮权限

-- 公示信息查询
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息查询', @publicity_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:query');

-- 公示信息新增
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息新增', @publicity_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:add');

-- 公示信息修改
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息修改', @publicity_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:edit');

-- 公示信息删除
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息删除', @publicity_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:remove');

-- 公示信息导出
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '公示信息导出', @publicity_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:export');

-- 发布公示
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '发布公示', @publicity_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:publish', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:publish');

-- 取消公示
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '取消公示', @publicity_menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'pension:publicity:unpublish', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'pension:publicity:unpublish');

-- =============================================
-- 第二部分: 床位列表菜单及按钮权限
-- =============================================

-- 1. 查找床位管理父菜单ID
SET @bed_manage_menu_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '床位管理' AND menu_type = 'M' LIMIT 1);

-- 2. 插入床位列表菜单 (如果不存在)
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位列表',
       @bed_manage_menu_id,
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
       '床位列表菜单'
FROM DUAL
WHERE NOT EXISTS (
    SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:list' AND menu_type = 'C'
);

-- 3. 获取床位列表菜单ID
SET @bed_menu_id = (SELECT menu_id FROM sys_menu WHERE perms = 'elder:bed:list' AND menu_type = 'C' LIMIT 1);

-- 4. 插入床位列表的按钮权限

-- 床位查询
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位查询', @bed_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:query', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:query');

-- 床位新增
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位新增', @bed_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:add', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:add');

-- 床位修改
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位修改', @bed_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:edit', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:edit');

-- 床位删除
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位删除', @bed_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:remove', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:remove');

-- 床位导出
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位导出', @bed_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:export', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:export');

-- 床位导入
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT '床位导入', @bed_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'elder:bed:import', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE perms = 'elder:bed:import');

-- =============================================
-- 验证SQL (可选 - 执行完成后运行以下查询验证结果)
-- =============================================

-- 查看公示信息维护的所有权限
SELECT
    menu_id,
    menu_name,
    parent_id,
    order_num,
    perms,
    menu_type,
    CASE menu_type
        WHEN 'C' THEN '菜单'
        WHEN 'F' THEN '按钮'
        ELSE menu_type
    END AS type_name
FROM sys_menu
WHERE perms LIKE 'pension:publicity:%'
ORDER BY parent_id, order_num;

-- 查看床位管理的所有权限
SELECT
    menu_id,
    menu_name,
    parent_id,
    order_num,
    perms,
    menu_type,
    CASE menu_type
        WHEN 'C' THEN '菜单'
        WHEN 'F' THEN '按钮'
        ELSE menu_type
    END AS type_name
FROM sys_menu
WHERE perms LIKE 'elder:bed:%'
ORDER BY parent_id, order_num;

-- 统计创建的权限数量
SELECT
    '公示信息维护' AS module,
    COUNT(*) AS permission_count
FROM sys_menu
WHERE perms LIKE 'pension:publicity:%'
UNION ALL
SELECT
    '床位管理' AS module,
    COUNT(*) AS permission_count
FROM sys_menu
WHERE perms LIKE 'elder:bed:%';

COMMIT;
