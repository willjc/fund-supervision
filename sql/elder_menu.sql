-- 老人入住管理功能菜单配置
-- 创建时间: 2025-10-28

-- 添加主菜单：老人管理
INSERT INTO `sys_menu` VALUES (2000, '老人管理', 0, 8, 'elder', null, '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', '2025-10-28 16:00:00', '', null, '老人管理功能模块');

-- 添加子菜单：老人信息管理
INSERT INTO `sys_menu` VALUES (2001, '老人信息', 2000, 1, 'info', 'elder/info/index', '', 1, 0, 'C', '0', '0', 'elder:info:list', 'user', 'admin', '2025-10-28 16:00:00', '', null, '老人信息管理菜单');

-- 添加子菜单：床位管理
INSERT INTO `sys_menu` VALUES (2002, '床位管理', 2000, 2, 'bed', 'elder/bed/index', '', 1, 0, 'C', '0', '0', 'elder:bed:list', 'tree', 'admin', '2025-10-28 16:00:00', '', null, '床位管理菜单');

-- 添加子菜单：入住申请
INSERT INTO `sys_menu` VALUES (2003, '入住申请', 2000, 3, 'checkin', 'elder/checkin/index', '', 1, 0, 'C', '0', '0', 'elder:checkin:list', 'component', 'admin', '2025-10-28 16:00:00', '', null, '入住申请管理菜单');

-- 老人信息管理按钮权限
INSERT INTO `sys_menu` VALUES (2011, '老人信息查询', 2001, 1, '', '', '', 1, 0, 'F', '0', '0', 'elder:info:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2012, '老人信息新增', 2001, 2, '', '', '', 1, 0, 'F', '0', '0', 'elder:info:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2013, '老人信息修改', 2001, 3, '', '', '', 1, 0, 'F', '0', '0', 'elder:info:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2014, '老人信息删除', 2001, 4, '', '', '', 1, 0, 'F', '0', '0', 'elder:info:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2015, '老人信息导出', 2001, 5, '', '', '', 1, 0, 'F', '0', '0', 'elder:info:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 床位管理按钮权限
INSERT INTO `sys_menu` VALUES (2021, '床位查询', 2002, 1, '', '', '', 1, 0, 'F', '0', '0', 'elder:bed:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2022, '床位新增', 2002, 2, '', '', '', 1, 0, 'F', '0', '0', 'elder:bed:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2023, '床位修改', 2002, 3, '', '', '', 1, 0, 'F', '0', '0', 'elder:bed:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2024, '床位删除', 2002, 4, '', '', '', 1, 0, 'F', '0', '0', 'elder:bed:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2025, '床位导出', 2002, 5, '', '', '', 1, 0, 'F', '0', '0', 'elder:bed:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 入住申请按钮权限
INSERT INTO `sys_menu` VALUES (2031, '入住申请查询', 2003, 1, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:query', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2032, '入住申请新增', 2003, 2, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:add', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2033, '入住申请修改', 2003, 3, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:edit', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2034, '入住申请删除', 2003, 4, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:remove', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2035, '入住申请审批', 2003, 5, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:approve', '#', 'admin', '2025-10-28 16:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2036, '入住申请导出', 2003, 6, '', '', '', 1, 0, 'F', '0', '0', 'elder:checkin:export', '#', 'admin', '2025-10-28 16:00:00', '', null, '');

-- 为管理员角色分配菜单权限
INSERT INTO `sys_role_menu` (role_id, menu_id)
SELECT 1, menu_id FROM sys_menu WHERE menu_id >= 2000;