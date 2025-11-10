-- 订单支付功能菜单配置（使用ID 2137开始）

-- 1. 插入订单管理主菜单（ID: 2137）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2137, '订单管理', 0, 5, 'order', NULL, 1, 0, 'M', '0', '0', NULL, 'shopping', 'admin', sysdate(), '', NULL, '订单管理目录');

-- 2. 插入订单列表子菜单（ID: 2138）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2138, '订单列表', 2137, 1, 'orderInfo', 'order/orderInfo/index', 1, 0, 'C', '0', '0', 'order:info:list', 'list', 'admin', sysdate(), '', NULL, '订单管理菜单');

-- 3. 订单管理按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2139, '订单查询', 2138, 1, '#', '', 1, 0, 'F', '0', '0', 'order:info:query', '#', 'admin', sysdate(), '', NULL, ''),
(2140, '订单新增', 2138, 2, '#', '', 1, 0, 'F', '0', '0', 'order:info:add', '#', 'admin', sysdate(), '', NULL, ''),
(2141, '订单修改', 2138, 3, '#', '', 1, 0, 'F', '0', '0', 'order:info:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2142, '订单删除', 2138, 4, '#', '', 1, 0, 'F', '0', '0', 'order:info:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2143, '订单导出', 2138, 5, '#', '', 1, 0, 'F', '0', '0', 'order:info:export', '#', 'admin', sysdate(), '', NULL, ''),
(2144, '订单支付', 2138, 6, '#', '', 1, 0, 'F', '0', '0', 'order:info:pay', '#', 'admin', sysdate(), '', NULL, ''),
(2145, '订单取消', 2138, 7, '#', '', 1, 0, 'F', '0', '0', 'order:info:cancel', '#', 'admin', sysdate(), '', NULL, '');

-- 4. 插入支付记录子菜单（ID: 2146）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2146, '支付记录', 2137, 2, 'paymentRecord', 'order/paymentRecord/index', 1, 0, 'C', '0', '0', 'payment:record:list', 'money', 'admin', sysdate(), '', NULL, '支付记录菜单');

-- 5. 支付记录按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2147, '支付查询', 2146, 1, '#', '', 1, 0, 'F', '0', '0', 'payment:record:query', '#', 'admin', sysdate(), '', NULL, ''),
(2148, '支付新增', 2146, 2, '#', '', 1, 0, 'F', '0', '0', 'payment:record:add', '#', 'admin', sysdate(), '', NULL, ''),
(2149, '支付修改', 2146, 3, '#', '', 1, 0, 'F', '0', '0', 'payment:record:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2150, '支付删除', 2146, 4, '#', '', 1, 0, 'F', '0', '0', 'payment:record:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2151, '支付导出', 2146, 5, '#', '', 1, 0, 'F', '0', '0', 'payment:record:export', '#', 'admin', sysdate(), '', NULL, ''),
(2152, '支付状态', 2146, 6, '#', '', 1, 0, 'F', '0', '0', 'payment:record:status', '#', 'admin', sysdate(), '', NULL, ''),
(2153, '支付统计', 2146, 7, '#', '', 1, 0, 'F', '0', '0', 'payment:record:statistics', '#', 'admin', sysdate(), '', NULL, '');

-- 6. 插入订单明细子菜单（ID: 2154）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2154, '订单明细', 2137, 3, 'orderItem', 'order/orderItem/index', 1, 0, 'C', '0', '0', 'order:item:list', 'form', 'admin', sysdate(), '', NULL, '订单明细菜单');

-- 7. 订单明细按钮权限
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark) VALUES
(2155, '明细查询', 2154, 1, '#', '', 1, 0, 'F', '0', '0', 'order:item:query', '#', 'admin', sysdate(), '', NULL, ''),
(2156, '明细新增', 2154, 2, '#', '', 1, 0, 'F', '0', '0', 'order:item:add', '#', 'admin', sysdate(), '', NULL, ''),
(2157, '明细修改', 2154, 3, '#', '', 1, 0, 'F', '0', '0', 'order:item:edit', '#', 'admin', sysdate(), '', NULL, ''),
(2158, '明细删除', 2154, 4, '#', '', 1, 0, 'F', '0', '0', 'order:item:remove', '#', 'admin', sysdate(), '', NULL, ''),
(2159, '明细导出', 2154, 5, '#', '', 1, 0, 'F', '0', '0', 'order:item:export', '#', 'admin', sysdate(), '', NULL, '');

-- 8. 为管理员角色分配菜单权限（角色ID为1）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, 2137), -- 订单管理主菜单
(1, 2138), -- 订单列表
(1, 2139), (1, 2140), (1, 2141), (1, 2142), (1, 2143), (1, 2144), (1, 2145), -- 订单按钮权限
(1, 2146), -- 支付记录
(1, 2147), (1, 2148), (1, 2149), (1, 2150), (1, 2151), (1, 2152), (1, 2153), -- 支付按钮权限
(1, 2154), -- 订单明细
(1, 2155), (1, 2156), (1, 2157), (1, 2158), (1, 2159); -- 明细按钮权限

-- 执行完成后，请重新登录系统以刷新菜单权限