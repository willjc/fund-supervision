-- 账户管理职责拆分：复用现有菜单和页面，不新增菜单 ID。
UPDATE sys_menu
SET menu_name = '监管账户管理', remark = '维护机构监管账户和基本账户'
WHERE menu_id = 3301;

UPDATE sys_menu
SET menu_name = '银行商户配置', remark = '维护机构与银行商户号绑定'
WHERE menu_id = 3303;

-- 回滚：
-- UPDATE sys_menu SET menu_name = '机构账户查询', remark = '机构账户查询' WHERE menu_id = 3301;
-- UPDATE sys_menu SET menu_name = '监管账户维护', remark = '监管账户维护' WHERE menu_id = 3303;
