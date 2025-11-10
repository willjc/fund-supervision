-- 老人入住管理功能菜单配置
-- 创建时间: 2025-10-28

-- 添加主菜单：老人管理
INSERT INTO `sys_menu` VALUES (2100, '老人管理', 0, 9, 'elder', null, null, null, 1, 0, 'M', '0', '0', '', 'peoples', 'admin', '2025-10-28 16:00:00', '', null, '老人管理功能模块');

-- 添加子菜单：老人信息管理
INSERT INTO `sys_menu` VALUES (2101, '老人信息', 2100, 1, 'info', 'elder/info/index', null, null, 1, 0, 'C', '0', '0', 'elder:info:list', 'user', 'admin', '2025-10-28 16:00:00', '', null, '老人信息管理菜单');

-- 添加子菜单：床位管理
INSERT INTO `sys_menu` VALUES (2102, '床位管理', 2100, 2, 'bed', 'elder/bed/index', null, null, 1, 0, 'C', '0', '0', 'elder:bed:list', 'tree', 'admin', '2025-10-28 16:00:00', '', null, '床位管理菜单');

-- 添加子菜单：入住申请
INSERT INTO `sys_menu` VALUES (2103, '入住申请', 2100, 3, 'checkin', 'elder/checkin/index', null, null, 1, 0, 'C', '0', '0', 'elder:checkin:list', 'component', 'admin', '2025-10-28 16:00:00', '', null, '入住申请管理菜单');

-- 老人信息管理按钮权限
INSERT INTO `sys_menu` VALUES (2111, '老人信息查询', 2101, 1, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:info:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2112, '老人信息新增', 2101, 2, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:info:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2113, '老人信息修改', 2101, 3, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:info:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2114, '老人信息删除', 2101, 4, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:info:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2115, '老人信息导出', 2101, 5, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:info:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 床位管理按钮权限
INSERT INTO `sys_menu` VALUES (2121, '床位查询', 2102, 1, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:bed:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2122, '床位新增', 2102, 2, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:bed:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2123, '床位修改', 2102, 3, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:bed:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2124, '床位删除', 2102, 4, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:bed:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2125, '床位导出', 2102, 5, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:bed:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 入住申请按钮权限
INSERT INTO `sys_menu` VALUES (2131, '入住申请查询', 2103, 1, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2132, '入住申请新增', 2103, 2, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2133, '入住申请修改', 2103, 3, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2134, '入住申请删除', 2103, 4, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2135, '入住申请审批', 2103, 5, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:approve', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2136, '入住申请导出', 2103, 6, '', '', null, null, 1, 0, 'F', '0', '0', 'elder:checkin:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 为管理员角色分配菜单权限
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id >= 2100;