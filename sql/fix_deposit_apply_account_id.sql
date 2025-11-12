-- 修复押金使用申请表 account_id 字段
-- 执行时间: 2025-11-13
-- 问题: account_id 字段设置为 NOT NULL，但前端提交时可能没有该字段
-- 解决方案: 将 account_id 改为可空字段

SET NAMES utf8mb4;

-- 修改 account_id 字段为可空
ALTER TABLE `deposit_apply`
MODIFY COLUMN `account_id` bigint(20) DEFAULT NULL COMMENT '账户ID';

COMMIT;
