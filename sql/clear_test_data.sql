-- =============================================
-- 清理所有测试数据
-- 执行日期: 2025-11-11
-- 说明: 保留表结构,清空所有业务测试数据
-- =============================================

SET NAMES utf8mb4;
SET CHARACTER SET utf8mb4;

-- 临时禁用外键检查
SET FOREIGN_KEY_CHECKS = 0;

-- 开始事务
START TRANSACTION;

-- 1. 清空床位分配数据
DELETE FROM bed_allocation;
ALTER TABLE bed_allocation AUTO_INCREMENT = 1;

-- 2. 清空订单相关数据
DELETE FROM order_info;
ALTER TABLE order_info AUTO_INCREMENT = 1;

-- 3. 清空账户数据
DELETE FROM account_info;
ALTER TABLE account_info AUTO_INCREMENT = 1;

-- 4. 清空老人信息
DELETE FROM elder_info;
ALTER TABLE elder_info AUTO_INCREMENT = 1;

-- 5. 清空床位数据
DELETE FROM bed_info;
ALTER TABLE bed_info AUTO_INCREMENT = 1;

-- 6. 清空养老机构数据
DELETE FROM pension_institution;
ALTER TABLE pension_institution AUTO_INCREMENT = 1;

-- 提交事务
COMMIT;

-- 启用外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- 验证清理结果
SELECT '床位分配数据' as 表名, COUNT(*) as 剩余记录数 FROM bed_allocation
UNION ALL
SELECT '床位数据' as 表名, COUNT(*) as 剩余记录数 FROM bed_info
UNION ALL
SELECT '养老机构数据' as 表名, COUNT(*) as 剩余记录数 FROM pension_institution
UNION ALL
SELECT '老人信息数据' as 表名, COUNT(*) as 剩余记录数 FROM elder_info
UNION ALL
SELECT '订单数据' as 表名, COUNT(*) as 剩余记录数 FROM order_info
UNION ALL
SELECT '账户数据' as 表名, COUNT(*) as 剩余记录数 FROM account_info;

-- =============================================
-- 清理完成说明
-- =============================================
-- 所有业务测试数据已清空,表结构保留
-- 自增ID已重置,下次插入从1开始
-- 可以开始手动添加真实的测试数据
-- =============================================
