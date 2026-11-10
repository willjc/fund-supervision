-- ============================================================
-- 机构银行登记户名 v1（2026-11-10）
-- 目的：pension_institution 新增「银行登记户名」列，监管账户与基本账户共用，
--       作为银行商户配置收付款户名的唯一来源；并从现有商户配置回填存量户名。
-- 影响范围：pension_institution 新增 1 个可空列；存量行按现有商户配置回填 account_holder_name。
--           不修改账号/开户行，不改动 bank_merchant_config 及其它任何表的数据。
-- 执行顺序：先在生产库执行本脚本（加列 + 回填），再部署引用该列的后端/前端代码。
--           本脚本幂等，可重复执行。执行前请先 SHOW CREATE TABLE pension_institution
--           确认字符集为 utf8mb4，并 mysqldump 备份 pension_institution。
-- 回滚：ALTER TABLE pension_institution DROP COLUMN account_holder_name;
--       （须先回退引用该列的代码；必要时用备份恢复）
-- 验证：SELECT institution_id, institution_name, account_holder_name
--       FROM pension_institution LIMIT 10;
-- ============================================================

-- 1) 新增列（幂等：列已存在则跳过）
SET @col_exists = (SELECT COUNT(*) FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = DATABASE() AND TABLE_NAME = 'pension_institution'
    AND COLUMN_NAME = 'account_holder_name');
SET @ddl = IF(@col_exists = 0,
  'ALTER TABLE pension_institution ADD COLUMN account_holder_name VARCHAR(200) NULL DEFAULT NULL COMMENT ''银行登记户名（监管账户与基本账户共用，拨付收付款户名）'' AFTER supervise_bank',
  'SELECT 1');
PREPARE stmt FROM @ddl;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) 回填存量户名（幂等：仅填充空值，取该机构现有商户配置的监管账户户名）
UPDATE pension_institution pi
JOIN (
    SELECT institution_id, MAX(settlement_account_name) AS holder_name
    FROM bank_merchant_config
    WHERE settlement_account_name IS NOT NULL AND settlement_account_name <> ''
    GROUP BY institution_id
) bmc ON bmc.institution_id = pi.institution_id
SET pi.account_holder_name = bmc.holder_name
WHERE pi.account_holder_name IS NULL OR pi.account_holder_name = '';