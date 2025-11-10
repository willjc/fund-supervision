-- 订单支付功能菜单配置（简化版）
-- 先查询当前最大菜单ID
SELECT MAX(menu_id) FROM sys_menu;

-- 假设当前最大ID为2100，则订单管理主菜单ID为2101
-- 请根据实际查询结果调整下面的ID值

-- 1. 插入订单管理主菜单（ID: 2101）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2101, '订单管理', 0, 5, 'order', NULL, 1, 0, 'M', '0', '0', NULL, 'shopping', 'admin', sysdate(), '', NULL, '订单管理目录');

-- 2. 插入订单列表子菜单（ID: 2102）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2102, '订单列表', 2101, 1, 'orderInfo', 'order/orderInfo/index', 1, 0, 'C', '0', '0', 'order:info:list', 'list', 'admin', sysdate(), '', NULL, '订单管理菜单');

-- 3. 订单管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2103, '订单查询', 2102, 1, '#', '', 1, 0, 'F', '0', '0', 'order:info:query', '#', 'admin', sysdate(), '', NULL, ''),
(2104, '订单新增', 2102, 2, '#', '', 1, 0, 'F', '0', '0', 'order:info:add', '#', 'admin', sysdate(), '', NULL, ''),
(2105, '订单修改', 2102, 3, '#', '', 1, 0, 'F', '0', '0', 'order:info:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2106, '订单删除', 2102, 4, '#', '', 1, 0, 'F', '0', '0', 'order:info:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2107, '订单导出', 2102, 5, '#', '', 1, 0, 'F', '0', '0', 'order:info:export', '#', 'admin', sysdate(), '', NULL, ''),
(2108, '订单支付', 2102, 6, '#', '', 1, 0, 'F', '0', '0', 'order:info:pay', '#', 'admin', sysdate(), '', NULL, ''),
(2109, '订单取消', 2102, 7, '#', '', 1, 0, 'F', '0', '0', 'order:info:cancel', '#', 'admin', sysdate(), '', NULL, '');

-- 4. 插入支付记录子菜单（ID: 2110）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2110, '支付记录', 2101, 2, 'paymentRecord', 'order/paymentRecord/index', 1, 0, 'C', '0', '0', 'payment:record:list', 'money', 'admin', sysdate(), '', NULL, '支付记录菜单');

-- 5. 支付记录按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2111, '支付查询', 2110, 1, '#', '', 1, 0, 'F', '0', '0', 'payment:record:query', '#', 'admin', sysdate(), '', NULL, ''),
(2112, '支付新增', 2110, 2, '#', '', 1, 0, 'F', '0', '0', 'payment:record:add', '#', 'admin', sysdate(), '', NULL, ''),
(2113, '支付修改', 2110, 3, '#', '', 1, 0, 'F', '0', '0', 'payment:record:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2114, '支付删除', 2110, 4, '#', '', 1, 0, 'F', '0', '0', 'payment:record:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2115, '支付导出', 2110, 5, '#', '', 1, 0, 'F', '0', '0', 'payment:record:export', '#', 'admin', sysdate(), '', NULL, ''),
(2116, '支付状态', 2110, 6, '#', '', 1, 0, 'F', '0', '0', 'payment:record:status', '#', 'admin', sysdate(), '', NULL, ''),
(2117, '支付统计', 2110, 7, '#', '', 1, 0, 'F', '0', '0', 'payment:record:statistics', '#', 'admin', sysdate(), '', NULL, '');

-- 6. 插入订单明细子菜单（ID: 2118）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2118, '订单明细', 2101, 3, 'orderItem', 'order/orderItem/index', 1, 0, 'C', '0', '0', 'order:item:list', 'form', 'admin', sysdate(), '', NULL, '订单明细菜单');

-- 7. 订单明细按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2119, '明细查询', 2118, 1, '#', '', 1, 0, 'F', '0', '0', 'order:item:query', '#', 'admin', sysdate(), '', NULL, ''),
(2120, '明细新增', 2118, 2, '#', '', 1, 0, 'F', '0', '0', 'order:item:add', '#', 'admin', sysdate(), '', NULL, ''),
(2121, '明细修改', 2118, 3, '#', '', 1, 0, 'F', '0', '0', 'order:item:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2122, '明细删除', 2118, 4, '#', '', 1, 0, 'F', '0', '0', 'order:item:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2123, '明细导出', 2118, 5, '#', '', 1, 0, 'F', '0', '0', 'order:item:export', '#', 'admin', sysdate(), '', NULL, '');

-- 8. 为管理员角色分配菜单权限（角色ID为1）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2101), -- 订单管理主菜单
(1, 2102), -- 订单列表
(1, 2103), (1, 2104), (1, 2105), (1, 2106), (1, 2107), (1, 2108), (1, 2109), -- 订单按钮权限
(1, 2110), -- 支付记录
(1, 2111), (1, 2112), (1, 2113), (1, 2114), (1, 2115), (1, 2116), (1, 2117), -- 支付按钮权限
(1, 2118), -- 订单明细
(1, 2119), (1, 2120), (1, 2121), (1, 2122), (1, 2123); -- 明细按钮权限

-- 执行完成后，请重新登录系统以刷新菜单权限