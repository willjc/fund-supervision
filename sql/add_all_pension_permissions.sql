-- =====================================================
-- 养老机构模块权限按钮批量添加脚本
-- 日期: 2025-01-15
-- 说明: 为押金管理、订单管理、入住管理模块添加权限按钮
-- =====================================================

-- ===== 1. 押金管理模块 =====

-- 1.1 押金使用申请 (menu_id=2051) - 7个权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(4018, '押金使用申请查询', 2051, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:query', '#', 'admin', NOW(), '', NULL, ''),
(4019, '押金使用申请新增', 2051, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:add', '#', 'admin', NOW(), '', NULL, ''),
(4020, '押金使用申请修改', 2051, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:edit', '#', 'admin', NOW(), '', NULL, ''),
(4021, '押金使用申请删除', 2051, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:remove', '#', 'admin', NOW(), '', NULL, ''),
(4022, '押金使用申请导出', 2051, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:export', '#', 'admin', NOW(), '', NULL, ''),
(4023, '押金使用申请审批', 2051, 6, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:approve', '#', 'admin', NOW(), '', NULL, ''),
(4024, '押金使用申请撤回', 2051, 7, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:withdraw', '#', 'admin', NOW(), '', NULL, '');

-- 1.2 押金使用列表 (menu_id=2052) - 2个权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(4025, '押金使用列表查询', 2052, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:query', '#', 'admin', NOW(), '', NULL, ''),
(4026, '押金使用列表导出', 2052, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:export', '#', 'admin', NOW(), '', NULL, '');

-- ===== 2. 订单管理模块 =====

-- 2.1 订单列表 (menu_id=2041) - 8个权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(4027, '订单列表查询', 2041, 1, '#', '', 1, 0, 'F', '0', '0', 'order:info:query', '#', 'admin', NOW(), '', NULL, ''),
(4028, '订单列表新增', 2041, 2, '#', '', 1, 0, 'F', '0', '0', 'order:info:add', '#', 'admin', NOW(), '', NULL, ''),
(4029, '订单列表修改', 2041, 3, '#', '', 1, 0, 'F', '0', '0', 'order:info:edit', '#', 'admin', NOW(), '', NULL, ''),
(4030, '订单列表删除', 2041, 4, '#', '', 1, 0, 'F', '0', '0', 'order:info:remove', '#', 'admin', NOW(), '', NULL, ''),
(4031, '订单列表导出', 2041, 5, '#', '', 1, 0, 'F', '0', '0', 'order:info:export', '#', 'admin', NOW(), '', NULL, ''),
(4032, '订单支付', 2041, 6, '#', '', 1, 0, 'F', '0', '0', 'order:info:pay', '#', 'admin', NOW(), '', NULL, ''),
(4033, '订单取消', 2041, 7, '#', '', 1, 0, 'F', '0', '0', 'order:info:cancel', '#', 'admin', NOW(), '', NULL, ''),
(4034, '生成订单', 2041, 8, '#', '', 1, 0, 'F', '0', '0', 'order:info:generate', '#', 'admin', NOW(), '', NULL, '');

-- ===== 3. 入住管理模块 =====

-- 3.1 入住人列表 (menu_id=2031) - 7个权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(4035, '入住人列表查询', 2031, 1, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:query', '#', 'admin', NOW(), '', NULL, ''),
(4036, '入住人新增', 2031, 2, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:add', '#', 'admin', NOW(), '', NULL, ''),
(4037, '入住人删除', 2031, 3, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:remove', '#', 'admin', NOW(), '', NULL, ''),
(4038, '入住人导出', 2031, 4, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:export', '#', 'admin', NOW(), '', NULL, ''),
(4039, '入住人续费', 2031, 5, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:renew', '#', 'admin', NOW(), '', NULL, ''),
(4040, '入住人支付', 2031, 6, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:payment', '#', 'admin', NOW(), '', NULL, ''),
(4041, '入住人导入', 2031, 7, '#', '', 1, 0, 'F', '0', '0', 'elder:resident:import', '#', 'admin', NOW(), '', NULL, '');

-- 3.2 新增入住 (menu_id=2032) - 6个权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(4042, '入住申请查询', 2032, 1, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:query', '#', 'admin', NOW(), '', NULL, ''),
(4043, '入住申请新增', 2032, 2, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:add', '#', 'admin', NOW(), '', NULL, ''),
(4044, '入住申请修改', 2032, 3, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:edit', '#', 'admin', NOW(), '', NULL, ''),
(4045, '入住申请删除', 2032, 4, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:remove', '#', 'admin', NOW(), '', NULL, ''),
(4046, '入住申请导出', 2032, 5, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:export', '#', 'admin', NOW(), '', NULL, ''),
(4047, '入住申请审批', 2032, 6, '#', '', 1, 0, 'F', '0', '0', 'elder:checkin:approve', '#', 'admin', NOW(), '', NULL, '');

-- =====================================================
-- 验证SQL
-- =====================================================

-- 查看押金使用申请权限
SELECT menu_id, menu_name, perms FROM sys_menu WHERE parent_id = 2051 ORDER BY order_num;

-- 查看押金使用列表权限
SELECT menu_id, menu_name, perms FROM sys_menu WHERE parent_id = 2052 ORDER BY order_num;

-- 查看订单列表权限
SELECT menu_id, menu_name, perms FROM sys_menu WHERE parent_id = 2041 ORDER BY order_num;

-- 查看入住人列表权限
SELECT menu_id, menu_name, perms FROM sys_menu WHERE parent_id = 2031 ORDER BY order_num;

-- 查看新增入住权限
SELECT menu_id, menu_name, perms FROM sys_menu WHERE parent_id = 2032 ORDER BY order_num;

-- 统计所有新增的权限按钮
SELECT COUNT(*) as total_permissions FROM sys_menu WHERE menu_id BETWEEN 4018 AND 4047;
