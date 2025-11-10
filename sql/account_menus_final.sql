-- 设置客户端字符集
SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 账户资金管理菜单SQL
-- 这个脚本会在系统菜单中添加账户资金管理相关的菜单项

-- 1. 插入一级菜单：账户资金管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账户资金管理', 0, 5, 'account', NULL, 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '', NULL, '账户资金管理目录');

-- 获取刚插入的一级菜单ID
SET @parentId = LAST_INSERT_ID();

-- 2. 插入二级菜单：老人账户管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户管理', @parentId, 1, 'accountInfo', 'pension/account/index', 1, 0, 'C', '0', '0', 'pension:account:list', 'user', 'admin', NOW(), '', NULL, '老人账户管理菜单');

-- 获取老人账户管理菜单ID
SET @accountMenuId = LAST_INSERT_ID();

-- 3. 老人账户管理的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户查询', @accountMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:account:query', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户新增', @accountMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:account:add', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户修改', @accountMenuId, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:account:edit', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户删除', @accountMenuId, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:account:remove', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('老人账户导出', @accountMenuId, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:account:export', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账户冻结', @accountMenuId, 6, '#', '', 1, 0, 'F', '0', '0', 'pension:account:freeze', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('账户解冻', @accountMenuId, 7, '#', '', 1, 0, 'F', '0', '0', 'pension:account:unfreeze', '#', 'admin', NOW(), '', NULL, '');

-- 4. 插入二级菜单：资金划拨管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('资金划拨管理', @parentId, 2, 'fundTransfer', 'pension/fundTransfer/index', 1, 0, 'C', '0', '0', 'pension:fundTransfer:list', 'dollar', 'admin', NOW(), '', NULL, '资金划拨管理菜单');

-- 获取资金划拨管理菜单ID
SET @transferMenuId = LAST_INSERT_ID();

-- 5. 资金划拨管理的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('资金划拨查询', @transferMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:fundTransfer:query', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('生成划拨单', @transferMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:fundTransfer:generate', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('审批划拨单', @transferMenuId, 3, '#', '', 1, 0, 'F', '0', '0', 'pension:fundTransfer:approve', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('执行划拨', @transferMenuId, 4, '#', '', 1, 0, 'F', '0', '0', 'pension:fundTransfer:execute', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('资金划拨导出', @transferMenuId, 5, '#', '', 1, 0, 'F', '0', '0', 'pension:fundTransfer:export', '#', 'admin', NOW(), '', NULL, '');

-- 6. 插入二级菜单：交易记录查询
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('交易记录查询', @parentId, 3, 'transactionRecord', 'pension/transactionRecord/index', 1, 0, 'C', '0', '0', 'pension:transaction:list', 'list', 'admin', NOW(), '', NULL, '交易记录查询菜单');

-- 获取交易记录查询菜单ID
SET @transactionMenuId = LAST_INSERT_ID();

-- 7. 交易记录查询的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('交易记录查询权限', @transactionMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:transaction:query', '#', 'admin', NOW(), '', NULL, '');

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('交易记录导出', @transactionMenuId, 2, '#', '', 1, 0, 'F', '0', '0', 'pension:transaction:export', '#', 'admin', NOW(), '', NULL, '');

-- 8. 插入二级菜单：余额预警
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('余额预警', @parentId, 4, 'balanceWarning', 'pension/balanceWarning/index', 1, 0, 'C', '0', '0', 'pension:warning:list', 'warning', 'admin', NOW(), '', NULL, '余额预警菜单');

-- 获取余额预警菜单ID
SET @warningMenuId = LAST_INSERT_ID();

-- 9. 余额预警的按钮权限
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
VALUES ('余额预警查询', @warningMenuId, 1, '#', '', 1, 0, 'F', '0', '0', 'pension:warning:query', '#', 'admin', NOW(), '', NULL, '');

-- 提交事务
COMMIT;