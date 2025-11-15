-- =====================================================
-- 检查和配置用户机构关联脚本
-- 日期: 2025-01-15
-- 说明: 用于检查机构用户是否正确配置了机构关联
-- =====================================================

-- ===== 1. 检查sys_user_institution表是否存在 =====
SELECT
    TABLE_NAME,
    TABLE_COMMENT
FROM
    information_schema.TABLES
WHERE
    TABLE_SCHEMA = 'newzijin'
    AND TABLE_NAME = 'sys_user_institution';

-- ===== 2. 查看所有用户的机构关联情况 =====
SELECT
    u.user_id,
    u.user_name,
    u.nick_name,
    r.role_name,
    ui.institution_id,
    pi.institution_name,
    CASE
        WHEN ui.institution_id IS NULL THEN '未配置机构关联'
        ELSE '已配置'
    END as status
FROM
    sys_user u
    LEFT JOIN sys_user_role ur ON u.user_id = ur.user_id
    LEFT JOIN sys_role r ON ur.role_id = r.role_id
    LEFT JOIN sys_user_institution ui ON u.user_id = ui.user_id
    LEFT JOIN pension_institution pi ON ui.institution_id = pi.institution_id
WHERE
    r.role_name LIKE '%机构%'
ORDER BY
    u.user_id;

-- ===== 3. 查看未配置机构关联的机构用户 =====
SELECT
    u.user_id,
    u.user_name,
    u.nick_name,
    r.role_name
FROM
    sys_user u
    INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
    INNER JOIN sys_role r ON ur.role_id = r.role_id
    LEFT JOIN sys_user_institution ui ON u.user_id = ui.user_id
WHERE
    r.role_name LIKE '%机构%'
    AND ui.institution_id IS NULL;

-- ===== 4. 查看所有养老机构列表 =====
SELECT
    institution_id,
    institution_name,
    institution_code,
    contact_phone,
    status,
    CASE status
        WHEN '0' THEN '待审核'
        WHEN '1' THEN '审核通过'
        WHEN '2' THEN '审核拒绝'
        ELSE '未知状态'
    END as status_name
FROM
    pension_institution
ORDER BY
    institution_id;

-- ===== 5. 示例: 为机构用户添加机构关联 =====
-- 使用方法:
-- 1. 先查询机构用户的user_id (从上面的查询结果获取)
-- 2. 再查询机构的institution_id (从上面的查询结果获取)
-- 3. 取消下面SQL的注释并填入实际的ID执行

-- INSERT INTO sys_user_institution (user_id, institution_id)
-- VALUES (用户ID, 机构ID);

-- 示例: 为用户ID=100的用户关联机构ID=1的机构
-- INSERT INTO sys_user_institution (user_id, institution_id)
-- VALUES (100, 1);

-- ===== 6. 验证配置结果 =====
-- 执行此查询验证用户是否能看到正确的数据

-- 查看指定用户关联的机构
-- SELECT
--     ui.user_id,
--     u.user_name,
--     ui.institution_id,
--     pi.institution_name
-- FROM
--     sys_user_institution ui
--     INNER JOIN sys_user u ON ui.user_id = u.user_id
--     INNER JOIN pension_institution pi ON ui.institution_id = pi.institution_id
-- WHERE
--     ui.user_id = 用户ID;

-- ===== 7. 批量为机构用户配置机构关联 =====
-- 如果机构用户的用户名与机构编码有关联,可以批量配置
-- 示例: 假设用户名格式为 "institution_机构编码"

-- INSERT INTO sys_user_institution (user_id, institution_id)
-- SELECT
--     u.user_id,
--     pi.institution_id
-- FROM
--     sys_user u
--     INNER JOIN sys_user_role ur ON u.user_id = ur.user_id
--     INNER JOIN sys_role r ON ur.role_id = r.role_id
--     INNER JOIN pension_institution pi ON SUBSTRING_INDEX(u.user_name, '_', -1) = pi.institution_code
-- WHERE
--     r.role_name LIKE '%机构%'
--     AND NOT EXISTS (
--         SELECT 1 FROM sys_user_institution ui2
--         WHERE ui2.user_id = u.user_id
--     );

-- =====================================================
-- 常见问题排查
-- =====================================================

-- Q1: 机构用户看不到任何数据?
-- A1: 检查是否在sys_user_institution表中有记录,如果没有需要添加

-- Q2: 机构用户能看到所有机构的数据?
-- A2: 检查Controller和Mapper是否正确设置了currentUserId过滤条件

-- Q3: 如何删除错误的机构关联?
-- A3: DELETE FROM sys_user_institution WHERE user_id = ? AND institution_id = ?

-- Q4: 一个用户可以关联多个机构吗?
-- A4: 可以,sys_user_institution表支持一个用户关联多个机构
