-- 入住人"办理退住"按钮权限 + 机构管理员角色授权
-- 执行顺序：部署退住功能版本前/后执行均可（权限先于按钮出现更稳）。
-- 回滚：DELETE FROM sys_role_menu WHERE menu_id=(SELECT menu_id FROM sys_menu WHERE perms='elder:resident:checkout');
--       DELETE FROM sys_menu WHERE perms='elder:resident:checkout';

INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type,
                      visible, status, perms, icon, create_by, create_time, remark)
VALUES ('入住人退住', 2031, 8, '', NULL, 1, 0, 'F', '0', '0', 'elder:resident:checkout', '#',
        'admin', NOW(), '办理退住（强校验：无待支付订单且账户余额清零）');

-- 机构管理员角色（role_id=100）授权
INSERT INTO sys_role_menu (role_id, menu_id)
SELECT 100, menu_id FROM sys_menu WHERE perms='elder:resident:checkout' LIMIT 1;

-- 验证：
-- SELECT menu_id,menu_name,perms FROM sys_menu WHERE perms='elder:resident:checkout';
-- SELECT role_id FROM sys_role_menu rm JOIN sys_menu m ON m.menu_id=rm.menu_id WHERE m.perms='elder:resident:checkout';
