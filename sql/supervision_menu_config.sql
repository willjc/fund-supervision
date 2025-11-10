-- 民政监管端菜单和权限配置
-- 执行时间: 2025-10-29
-- 字符集: utf8mb4
SET NAMES utf8mb4;

-- 1. 删除现有的乱码角色数据
DELETE FROM sys_role WHERE role_id = 3;
DELETE FROM sys_user_role WHERE role_id = 3;
DELETE FROM sys_role_menu WHERE role_id = 3;

-- 2. 创建民政监管角色
INSERT INTO sys_role VALUES (3, '民政监管员', 'supervision', 3, '2', 1, 1, '0', '0', 'admin', NOW(), '', NULL, '民政监管部门角色');

-- 3. 删除现有的民政监管菜单数据
DELETE FROM sys_menu WHERE menu_id >= 3000 AND menu_id < 4000;

-- 4. 创建民政监管主菜单
INSERT INTO sys_menu VALUES (3000, '民政监管', 0, 1, 'supervision', NULL, '', '', 1, 0, 'M', '0', '0', '', 'guide', 'admin', NOW(), '', NULL, '民政监管功能');

-- 3. 监管首页
INSERT INTO sys_menu VALUES (3001, '监管首页', 3000, 1, 'dashboard', 'supervision/dashboard/index', '', '', 1, 0, 'C', '0', '0', 'supervision:dashboard:view', 'dashboard', 'admin', NOW(), '', NULL, '监管首页');

-- 4. 机构审批菜单
INSERT INTO sys_menu VALUES (3010, '机构审批', 3000, 2, 'institution', NULL, '', '', 1, 0, 'M', '0', '0', '', 'peoples', 'admin', NOW(), '', NULL, '机构审批管理');

INSERT INTO sys_menu VALUES (3011, '入驻审批', 3010, 1, 'approval', 'supervision/institution/approval', '', '', 1, 0, 'C', '0', '0', 'supervision:institution:approval', 'form', 'admin', NOW(), '', NULL, '机构入驻审批');

-- 机构审批按钮权限
INSERT INTO sys_menu VALUES (3012, '审批通过', 3011, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:institution:approve', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3013, '审批拒绝', 3011, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:institution:reject', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3014, '查看详情', 3011, 3, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:institution:query', '#', 'admin', NOW(), '', NULL, '');

-- 5. 资金审批菜单
INSERT INTO sys_menu VALUES (3020, '资金审批', 3000, 3, 'fund', NULL, '', '', 1, 0, 'M', '0', '0', '', 'money', 'admin', NOW(), '', NULL, '资金审批管理');

INSERT INTO sys_menu VALUES (3021, '划拨审批', 3020, 1, 'transfer', 'supervision/fund/transfer', '', '', 1, 0, 'C', '0', '0', 'supervision:fund:transfer', 'shopping', 'admin', NOW(), '', NULL, '资金划拨审批');

INSERT INTO sys_menu VALUES (3022, '退款审批', 3020, 2, 'refund', 'supervision/fund/refund', '', '', 1, 0, 'C', '0', '0', 'supervision:fund:refund', 'guide', 'admin', NOW(), '', NULL, '退款审批');

INSERT INTO sys_menu VALUES (3023, '押金审批', 3020, 3, 'deposit', 'supervision/fund/deposit', '', '', 1, 0, 'C', '0', '0', 'supervision:fund:deposit', 'list', 'admin', NOW(), '', NULL, '押金使用审批');

-- 资金审批按钮权限
INSERT INTO sys_menu VALUES (3024, '审批通过', 3021, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:fund:approve', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3025, '审批拒绝', 3021, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:fund:reject', '#', 'admin', NOW(), '', NULL, '');

-- 6. 预警管理菜单
INSERT INTO sys_menu VALUES (3030, '预警管理', 3000, 4, 'warning', 'supervision/warning/index', '', '', 1, 0, 'C', '0', '0', 'supervision:warning:view', 'bug', 'admin', NOW(), '', NULL, '预警管理');

INSERT INTO sys_menu VALUES (3031, '预警处理', 3030, 1, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:warning:handle', '#', 'admin', NOW(), '', NULL, '');
INSERT INTO sys_menu VALUES (3032, '预警忽略', 3030, 2, '', '', '', '', 1, 0, 'F', '0', '0', 'supervision:warning:ignore', '#', 'admin', NOW(), '', NULL, '');

-- 7. 为管理员角色分配民政监管菜单权限
-- 注意：需要根据实际的角色ID调整，这里假设管理员角色ID为1
-- DELETE FROM sys_role_menu WHERE role_id = 3;
-- INSERT INTO sys_role_menu SELECT 3, menu_id FROM sys_menu WHERE menu_id >= 3000 AND menu_id < 4000;

-- 8. 创建民政监管测试账号
INSERT INTO sys_user VALUES (100, 103, 'supervision', '民政监管员', '00', 'supervision@qq.com', '15888888888', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TP57rDdaTpe', '0', '0', '127.0.0.1', NOW(), NULL, 'admin', NOW(), '', NULL, '民政监管测试账号，密码：admin123');

-- 9. 为监管员账号分配监管角色
INSERT INTO sys_user_role VALUES (100, 3);

-- 提交
COMMIT;
