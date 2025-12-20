-- 费用记录数据迁移脚本
-- 将现有的订单和押金申请数据迁移到 expense_record 表

-- 1. 迁移已支付订单作为费用记录（服务费、会员费等）
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, balance_before, balance_after,
    create_by, create_time, remark
)
SELECT
    e.elder_id,
    a.account_id,
    CASE
        WHEN item.item_type = 'deposit' THEN 'deposit'
        WHEN item.item_type = 'member_fee' THEN 'member'
        ELSE 'service'
    END as expense_type,
    'expense' as transaction_type,
    item.total_amount as amount,
    CONCAT('订单支付 - ', CONVERT(oi.order_no USING utf8mb4), ' (', CONVERT(IFNULL(item.item_name, '未知项目') USING utf8mb4), ')') as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    NULL as balance_before,  -- 暂时为空，因为无法追溯当时余额
    NULL as balance_after,
    'system' as create_by,
    NOW() as create_time,
    '从订单数据迁移的费用记录' as remark
FROM order_info oi
LEFT JOIN order_item item ON oi.order_id = item.order_id
LEFT JOIN elder_info e ON oi.elder_id = e.elder_id
LEFT JOIN account_info a ON oi.elder_id = a.elder_id
WHERE oi.order_status = '1'  -- 已支付订单
  AND item.item_id IS NOT NULL
  AND item.total_amount > 0;

-- 2. 迁移已批准的押金申请作为费用记录（押金使用）
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, balance_before, balance_after,
    create_by, create_time, remark
)
SELECT
    da.elder_id,
    a.account_id,
    'deposit' as expense_type,
    'expense' as transaction_type,
    COALESCE(da.actual_amount, da.apply_amount) as amount,
    CONCAT('押金使用 - ', CONVERT(IFNULL(da.apply_reason, '押金使用') USING utf8mb4)) as description,
    da.apply_id as related_id,
    'deposit_apply' as related_type,
    NULL as balance_before,  -- 暂时为空，因为无法追溯当时余额
    NULL as balance_after,
    'system' as create_by,
    da.create_time,
    '从押金申请数据迁移的费用记录' as remark
FROM deposit_apply da
LEFT JOIN account_info a ON da.elder_id = a.elder_id
WHERE da.apply_status = 'approved'  -- 已批准的押金申请
  AND (da.actual_amount IS NOT NULL OR da.apply_amount IS NOT NULL)
  AND COALESCE(da.actual_amount, da.apply_amount) > 0;

-- 3. 迁移老人账户中的当前余额作为初始充值记录（可选）
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, balance_before, balance_after,
    create_by, create_time, remark
)
SELECT
    e.elder_id,
    a.account_id,
    CASE
        WHEN a.deposit_balance > 0 THEN 'deposit'
        WHEN a.member_balance > 0 THEN 'member'
        ELSE 'service'
    END as expense_type,
    'income' as transaction_type,
    GREATEST(a.deposit_balance, a.member_balance, a.service_balance, 0) as amount,
    CASE
        WHEN a.deposit_balance > 0 THEN '初始押金余额'
        WHEN a.member_balance > 0 THEN '初始会员费余额'
        ELSE '初始服务费余额'
    END as description,
    a.account_id as related_id,
    'account_info' as related_type,
    0 as balance_before,
    GREATEST(a.total_balance, 0) as balance_after,
    'system' as create_by,
    COALESCE(a.create_time, NOW()) as create_time,
    '系统初始余额记录' as remark
FROM account_info a
LEFT JOIN elder_info e ON a.elder_id = e.elder_id
WHERE a.total_balance > 0
  AND (a.deposit_balance > 0 OR a.member_balance > 0 OR a.service_balance > 0);

-- 显示迁移结果统计
SELECT
    '迁移统计' as description,
    (SELECT COUNT(*) FROM expense_record) as total_records,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service') as service_records,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'deposit') as deposit_records,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'member') as member_records,
    (SELECT COUNT(*) FROM expense_record WHERE transaction_type = 'income') as income_records,
    (SELECT COUNT(*) FROM expense_record WHERE transaction_type = 'expense') as expense_records;