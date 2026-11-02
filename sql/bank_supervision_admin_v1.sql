-- ============================================================
-- 银行资金监管后台改造 v1（2026-11-02）
-- 目的：为 708 银行明细查询、收单对账差异处理、线下清退代发起
--       三个后台能力补菜单与按钮权限。
-- 影响范围：sys_menu 5 行、sys_role_menu 5 行（机构管理员 role_id=100）。
-- 执行顺序：本脚本可重复执行（NOT EXISTS 幂等），直接执行即可。
-- 回滚：DELETE FROM sys_role_menu WHERE menu_id IN (2073,2074,2075,2076,5061);
--       DELETE FROM sys_menu WHERE menu_id IN (2073,2074,2075,2076,5061);
-- 验证：SELECT menu_id, menu_name, perms FROM sys_menu WHERE menu_id IN (2073,2074,2075,2076,5061);
--       SELECT COUNT(*) FROM sys_role_menu WHERE role_id=100 AND menu_id IN (2073,2074,2075,2076,5061);  -- 期望 5
-- ============================================================

-- C 型页面：银行明细查询（挂在 银行流水(2070) 下）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2073, '银行明细查询', 2070, 3, 'detail', 'pension/bank/detail', 1, 0, 'C', '0', '0', 'pension:bank:detail', '#', 'admin', NOW(), 'OBK0071708 监管账户银行交易明细'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2073);

-- C 型页面：对账差异处理（挂在 银行流水(2070) 下）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2074, '对账差异处理', 2070, 4, 'recon', 'pension/bank/recon', 1, 0, 'C', '0', '0', 'bank:recon:list', '#', 'admin', NOW(), '收单对账批次与差异人工处理'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2074);

-- F 型按钮：执行对账
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2075, '执行对账', 2074, 1, '#', NULL, 1, 0, 'F', '0', '0', 'bank:recon:run', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2075);

-- F 型按钮：差异标记已处理
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 2076, '差异处理', 2074, 2, '#', NULL, 1, 0, 'F', '0', '0', 'bank:recon:handle', '#', 'admin', NOW(), ''
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 2076);

-- F 型按钮：退款管理-代发起线下清退（挂在 退款管理(5017) 下）
INSERT INTO sys_menu (menu_id, menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
SELECT 5061, '代发起清退', 5017, 4, '#', NULL, 1, 0, 'F', '0', '0', 'pension:refund:add', '#', 'admin', NOW(), '历史/现金余额线下清退入口'
FROM DUAL WHERE NOT EXISTS (SELECT 1 FROM sys_menu WHERE menu_id = 5061);

-- 角色授权：机构管理员(100)，与既有银行流水/退款菜单授权口径一致
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 100, menu_id FROM sys_menu
WHERE perms IN ('pension:bank:detail', 'bank:recon:list', 'bank:recon:run', 'bank:recon:handle', 'pension:refund:add')
  AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 100 AND rm.menu_id = sys_menu.menu_id);

-- 如需让民政监管员(role_id=3)也可用，放开下面注释再执行：
-- INSERT INTO sys_role_menu (role_id, menu_id)
-- SELECT 3, menu_id FROM sys_menu
-- WHERE perms IN ('pension:bank:detail', 'bank:recon:list', 'bank:recon:run', 'bank:recon:handle', 'pension:refund:add')
--   AND NOT EXISTS (SELECT 1 FROM sys_role_menu rm WHERE rm.role_id = 3 AND rm.menu_id = sys_menu.menu_id);
