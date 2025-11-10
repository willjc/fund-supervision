-- Pension Institution Management Menu Configuration

-- Add main menu: Pension Management
INSERT INTO `sys_menu` VALUES (2000, 'Pension Management', 0, 1, 'pension', null, null, null, 1, 0, 'M', '0', '0', '', 'tree', 'admin', '2025-10-28 10:00:00', '', null, 'Pension institution management directory');

-- Add sub menu: Institution Application
INSERT INTO `sys_menu` VALUES (2001, 'Institution Application', 2000, 1, 'institution', 'pension/institution/index', null, null, 1, 0, 'C', '0', '0', 'pension:institution:list', 'build', 'admin', '2025-10-28 10:00:00', '', null, 'Pension institution application menu');

-- Add button permissions for institution application
INSERT INTO `sys_menu` VALUES (2002, 'Institution Query', 2001, 1, '', '', null, null, 1, 0, 'F', '0', '0', 'pension:institution:query', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2003, 'Institution Add', 2001, 2, '', '', null, null, 1, 0, 'F', '0', '0', 'pension:institution:add', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2004, 'Institution Edit', 2001, 3, '', '', null, null, 1, 0, 'F', '0', '0', 'pension:institution:edit', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2005, 'Institution Delete', 2001, 4, '', '', null, null, 1, 0, 'F', '0', '0', 'pension:institution:remove', '#', 'admin', '2025-10-28 10:00:00', '', null, '');
INSERT INTO `sys_menu` VALUES (2006, 'Institution Export', 2001, 5, '', '', null, null, 1, 0, 'F', '0', '0', 'pension:institution:export', '#', 'admin', '2025-10-28 10:00:00', '', null, '');

-- Assign menu permissions to admin role (assuming admin role ID is 1)
INSERT INTO `sys_role_menu` VALUES (1, 2000);
INSERT INTO `sys_role_menu` VALUES (1, 2001);
INSERT INTO `sys_role_menu` VALUES (1, 2002);
INSERT INTO `sys_role_menu` VALUES (1, 2003);
INSERT INTO `sys_role_menu` VALUES (1, 2004);
INSERT INTO `sys_role_menu` VALUES (1, 2005);
INSERT INTO `sys_role_menu` VALUES (1, 2006);