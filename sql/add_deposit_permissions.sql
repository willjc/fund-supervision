-- 为押金使用申请菜单添加权限按钮
-- 日期: 2025-01-15

-- 1. 查询按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4018, '押金使用申请查询', 2051, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:query', '#', 'admin', NOW(), '', NULL, '');

-- 2. 新增按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4019, '押金使用申请新增', 2051, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:add', '#', 'admin', NOW(), '', NULL, '');

-- 3. 修改按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4020, '押金使用申请修改', 2051, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:edit', '#', 'admin', NOW(), '', NULL, '');

-- 4. 删除按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4021, '押金使用申请删除', 2051, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:remove', '#', 'admin', NOW(), '', NULL, '');

-- 5. 导出按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4022, '押金使用申请导出', 2051, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:export', '#', 'admin', NOW(), '', NULL, '');

-- 6. 审批按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4023, '押金使用申请审批', 2051, 6, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:approve', '#', 'admin', NOW(), '', NULL, '');

-- 7. 撤回按钮
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES (4024, '押金使用申请撤回', 2051, 7, '#', '', 1, 0, 'F', '0', '0', 'pension:deposit:withdraw', '#', 'admin', NOW(), '', NULL, '');

-- 说明:
-- menu_type: F 表示按钮权限
-- parent_id: 2051 表示父菜单为"押金使用申请"
-- perms: 对应Controller中@PreAuthorize注解的权限标识
-- visible: '0' 表示显示
-- status: '0' 表示正常
