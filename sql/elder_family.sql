-- 用户-老人关联表(家属用户关联老人信息)
-- 用于支持一个家属用户管理多个老人账户

DROP TABLE IF EXISTS elder_family;

CREATE TABLE elder_family (
    family_id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '家属关系ID',
    user_id BIGINT(20) NOT NULL COMMENT '用户ID(关联sys_user表)',
    elder_id BIGINT(20) NOT NULL COMMENT '老人ID(关联elder_info表)',
    relation_type CHAR(1) NOT NULL DEFAULT '1' COMMENT '关系类型(1:子女 2:配偶 3:兄弟姐妹 4:其他亲属 5:朋友)',
    relation_name VARCHAR(50) DEFAULT NULL COMMENT '关系描述(如:儿子、女儿、配偶等)',
    is_default CHAR(1) DEFAULT '0' COMMENT '是否默认老人(0:否 1:是)',
    is_main_contact CHAR(1) DEFAULT '0' COMMENT '是否主要联系人(0:否 1:是)',
    status CHAR(1) DEFAULT '0' COMMENT '关联状态(0:正常 1:已解除)',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (family_id),
    UNIQUE KEY uk_user_elder (user_id, elder_id) COMMENT '一个用户对一个老人只能有一条关联记录',
    KEY idx_user_id (user_id) COMMENT '用户ID索引',
    KEY idx_elder_id (elder_id) COMMENT '老人ID索引'
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8mb4 COMMENT='用户-老人关联表';

-- 插入测试数据(假设已有sys_user和elder_info数据)
-- 注意: 实际使用时需要根据真实的user_id和elder_id来插入

-- 示例: 创建一个家属用户(手机号: 13800138001, 密码: admin123)
INSERT INTO sys_user (user_id, user_name, nick_name, email, phonenumber, sex, avatar, password, status, del_flag, create_by, create_time, remark)
VALUES (100, 'family001', '张丽丽', 'zhanglili@example.com', '13800138001', '1', '', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '0', '0', 'admin', NOW(), '家属用户-测试账号')
ON DUPLICATE KEY UPDATE phonenumber='13800138001';

-- 示例: 创建关联关系(假设elder_id=1的老人存在)
-- INSERT INTO elder_family (user_id, elder_id, relation_type, relation_name, is_default, is_main_contact)
-- VALUES (100, 1, '1', '女儿', '1', '1');

-- 用户类型说明
-- sys_user.user_type:
-- '00' - 系统管理员用户
-- '01' - H5家属用户(新增)

-- 查询某个用户关联的所有老人
-- SELECT ef.*, ei.elder_name, ei.id_card, ei.phone, ei.status as elder_status
-- FROM elder_family ef
-- LEFT JOIN elder_info ei ON ef.elder_id = ei.elder_id
-- WHERE ef.user_id = ? AND ef.status = '0'
-- ORDER BY ef.is_default DESC, ef.create_time ASC;

-- 查询某个老人的所有家属
-- SELECT ef.*, u.user_name, u.nick_name, u.phonenumber
-- FROM elder_family ef
-- LEFT JOIN sys_user u ON ef.user_id = u.user_id
-- WHERE ef.elder_id = ? AND ef.status = '0'
-- ORDER BY ef.is_main_contact DESC, ef.create_time ASC;
