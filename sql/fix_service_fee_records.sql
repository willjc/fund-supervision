-- 修正服务费记录
-- 将分拆的床位费和护理费合并为服务费记录

-- 1. 删除现有的分拆服务费记录
DELETE FROM expense_record
WHERE elder_id = 35
  AND expense_type = 'service'
  AND (description LIKE '%Bed Fee%' OR description LIKE '%Care Fee%');

-- 2. 生成合并的服务费记录（充值）
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, create_by, create_time, remark
)
SELECT
    35 as elder_id,
    (SELECT account_id FROM account_info WHERE elder_id = 35 LIMIT 1) as account_id,
    'service' as expense_type,
    'income' as transaction_type,
    2000.00 as amount,
    'Service Fee Payment - 1 month (Bed Fee: 1500 + Care Fee: 500)' as description,
    41 as related_id,
    'order_info' as related_type,
    'system' as create_by,
    '2025-12-13 17:09:16' as create_time,
    'Merged service fee record' as remark;

-- 3. 确认首月扣除记录存在且正确
UPDATE expense_record
SET description = 'First Month Service Fee Deduction - 1 month (2000)',
    amount = 2000.00
WHERE elder_id = 35
  AND expense_type = 'service'
  AND transaction_type = 'expense'
  AND description LIKE '%First Month Service Fee%';

-- 显示修正后的服务费记录
SELECT
    record_id,
    expense_type,
    transaction_type,
    amount,
    description,
    create_time
FROM expense_record
WHERE elder_id = 35 AND expense_type = 'service'
ORDER BY create_time;