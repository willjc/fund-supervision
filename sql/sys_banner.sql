-- =============================================
-- 幻灯片管理功能数据库脚本
-- 创建日期：2024-12-24
-- 说明：创建幻灯片管理表、菜单和权限
-- =============================================

-- 1. 创建幻灯片管理表
CREATE TABLE IF NOT EXISTS `sys_banner` (
  `banner_id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '���灯片ID',
  `title` VARCHAR(100) DEFAULT NULL COMMENT '标题',
  `image_url` VARCHAR(500) NOT NULL COMMENT '图片URL',
  `link_type` CHAR(1) DEFAULT '1' COMMENT '链接类型：1-机构详情 2-外部URL',
  `link_value` VARCHAR(200) DEFAULT NULL COMMENT '链接值（机构ID或URL）',
  `position` CHAR(1) DEFAULT '1' COMMENT '位置：1-首页 2-机构页',
  `sort` INT(4) DEFAULT 0 COMMENT '排序号',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态：0-正常 1-停用',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`banner_id`)
) ENGINE=INNODB AUTO_INCREMENT=2100 DEFAULT CHARSET=utf8mb4 COMMENT='幻灯片管理表';

-- 2. 插入测试数据（可选，用于测试）
INSERT INTO `sys_banner` (`title`, `image_url`, `link_type`, `link_value`, `position`, `sort`, `status`, `create_by`, `create_time`) VALUES
('郑州养老院', '/profile/upload/banner1.jpg', '1', '1', '1', 1, '0', 'admin', NOW()),
('幸福养老中心', '/profile/upload/banner2.jpg', '1', '2', '1', 2, '0', 'admin', NOW()),
('康养社区', '/profile/upload/banner3.jpg', '1', '3', '1', 3, '0', 'admin', NOW());

-- 3. 创建幻灯片管理菜单（父菜单：系统管理 menu_id=1）
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `path`, `component`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `update_by`, `update_time`, `remark`)
VALUES (2100, '幻灯片管理', 1, 10, 'banner', 'system/banner/index', 'C', '0', '0', 'system:banner:list', 'picture', 'admin', NOW(), '', NULL, '幻灯片管理菜单');

-- 4. 创建幻灯片管理权限按钮
INSERT INTO `sys_menu` (`menu_id`, `menu_name`, `parent_id`, `order_num`, `menu_type`, `perms`, `create_by`, `create_time`, `remark`)
VALUES
(2101, '幻灯片查询', 2100, 1, 'F', 'system:banner:query', 'admin', NOW(), '查询幻灯片详情'),
(2102, '幻灯片新增', 2100, 2, 'F', 'system:banner:add', 'admin', NOW(), '新增幻灯片'),
(2103, '幻灯片修改', 2100, 3, 'F', 'system:banner:edit', 'admin', NOW(), '修改幻灯片'),
(2104, '幻灯片删除', 2100, 4, 'F', 'system:banner:remove', 'admin', NOW(), '删除幻灯片');

-- 5. 为管理员角色分配权限（role_id=1）
INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT 1, menu_id FROM sys_menu WHERE menu_id BETWEEN 2100 AND 2104;

-- 执行完成提示
SELECT '幻灯片管理功能数据库脚本执行完成！' AS message;
