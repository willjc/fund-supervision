-- 订单支付功能菜单配置

-- 1. 订单管理主菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('订单管理', 0, 5, 'order', NULL, 1, 0, 'M', '0', '0', NULL, 'shopping', 'admin', sysdate(), '', NULL, '订单管理目录');

-- 获取刚插入的主菜单ID（假设为2000）
SET @parent_menu_id = 2000;

-- 2. 订单管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('订单列表', @parent_menu_id, 1, 'orderInfo', 'order/orderInfo/index', 1, 0, 'C', '0', '0', 'order:info:list', 'list', 'admin', sysdate(), '', NULL, '订单管理菜单');

-- 获取订单列表菜单ID（假设为2001）
SET @order_info_menu_id = 2001;

-- 订单管理按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('订单查询', @order_info_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'order:info:query', '#', 'admin', sysdate(), '', NULL, ''),
('订单新增', @order_info_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'order:info:add', '#', 'admin', sysdate(), '', NULL, ''),
('订单修改', @order_info_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'order:info:edit', '#', 'admin', sysdate(), '', NULL, ''),
('订单删除', @order_info_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'order:info:remove', '#', 'admin', sysdate(), '', NULL, ''),
('订单导出', @order_info_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'order:info:export', '#', 'admin', sysdate(), '', NULL, ''),
('订单支付', @order_info_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'order:info:pay', '#', 'admin', sysdate(), '', NULL, ''),
('订单取消', @order_info_menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'order:info:cancel', '#', 'admin', sysdate(), '', NULL, '');

-- 3. 支付记录管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('支付记录', @parent_menu_id, 2, 'paymentRecord', 'order/paymentRecord/index', 1, 0, 'C', '0', '0', 'payment:record:list', 'money', 'admin', sysdate(), '', NULL, '支付记录菜单');

-- 获取支付记录菜单ID（假设为2010）
SET @payment_record_menu_id = 2010;

-- 支付记录按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('支付查询', @payment_record_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'payment:record:query', '#', 'admin', sysdate(), '', NULL, ''),
('支付新增', @payment_record_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'payment:record:add', '#', 'admin', sysdate(), '', NULL, ''),
('支付修改', @payment_record_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'payment:record:edit', '#', 'admin', sysdate(), '', NULL, ''),
('支付删除', @payment_record_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'payment:record:remove', '#', 'admin', sysdate(), '', NULL, ''),
('支付导出', @payment_record_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'payment:record:export', '#', 'admin', sysdate(), '', NULL, ''),
('支付状态', @payment_record_menu_id, 6, '#', '', 1, 0, 'F', '0', '0', 'payment:record:status', '#', 'admin', sysdate(), '', NULL, ''),
('支付统计', @payment_record_menu_id, 7, '#', '', 1, 0, 'F', '0', '0', 'payment:record:statistics', '#', 'admin', sysdate(), '', NULL, '');

-- 4. 订单明细管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('订单明细', @parent_menu_id, 3, 'orderItem', 'order/orderItem/index', 1, 0, 'C', '0', '0', 'order:item:list', 'form', 'admin', sysdate(), '', NULL, '订单明细菜单');

-- 获取订单明细菜单ID（假设为2020）
SET @order_item_menu_id = 2020;

-- 订单明细按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
('明细查询', @order_item_menu_id, 1, '#', '', 1, 0, 'F', '0', '0', 'order:item:query', '#', 'admin', sysdate(), '', NULL, ''),
('明细新增', @order_item_menu_id, 2, '#', '', 1, 0, 'F', '0', '0', 'order:item:add', '#', 'admin', sysdate(), '', NULL, ''),
('明细修改', @order_item_menu_id, 3, '#', '', 1, 0, 'F', '0', '0', 'order:item:edit', '#', 'admin', sysdate(), '', NULL, ''),
('明细删除', @order_item_menu_id, 4, '#', '', 1, 0, 'F', '0', '0', 'order:item:remove', '#', 'admin', sysdate(), '', NULL, ''),
('明细导出', @order_item_menu_id, 5, '#', '', 1, 0, 'F', '0', '0', 'order:item:export', '#', 'admin', sysdate(), '', NULL, '');

-- 5. 为管理员角色分配菜单权限（假设管理员角色ID为1）
-- 订单管理主菜单
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @parent_menu_id);
-- 订单管理子菜单及权限
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE parent_id IN (@order_info_menu_id, @payment_record_menu_id, @order_item_menu_id);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @order_info_menu_id);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @payment_record_menu_id);
INSERT INTO sys_role_menu (role_id, menu_id) VALUES (1, @order_item_menu_id);

-- 注意：实际执行时需要根据数据库中已有的菜单ID进行调整
-- 可以通过查询 SELECT MAX(menu_id) FROM sys_menu; 来获取当前最大菜单ID