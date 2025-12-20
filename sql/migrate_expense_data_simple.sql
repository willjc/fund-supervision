-- 费用记录数据迁移脚本（简化版）
-- 将现有的订单数据迁移到 expense_record 表

-- 1. 迁移已支付订单作为服务费记录
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, create_by, create_time, remark
)
SELECT
    oi.elder_id,
    (SELECT account_id FROM account_info WHERE elder_id = oi.elder_id LIMIT 1) as account_id,
    'service' as expense_type,
    'expense' as transaction_type,
    oi.order_amount as amount,
    '订单支付' as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    'system' as create_by,
    oi.create_time,
    '从订单数据迁移' as remark
FROM order_info oi
WHERE oi.order_status = '1'  -- 已支付订单
  AND oi.order_amount > 0
  AND oi.elder_id IS NOT NULL;

-- 2. 迁移已批准的押金申请作为押金使用记录
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, create_by, create_time, remark
)
SELECT
    da.elder_id,
    (SELECT account_id FROM account_info WHERE elder_id = da.elder_id LIMIT 1) as account_id,
    'deposit' as expense_type,
    'expense' as transaction_type,
    COALESCE(da.actual_amount, da.apply_amount, 0) as amount,
    '押金使用' as description,
    da.apply_id as related_id,
    'deposit_apply' as related_type,
    'system' as create_by,
    da.create_time,
    '从押金申请数据迁移' as remark
FROM deposit_apply da
WHERE da.apply_status = 'approved'  -- 已批准的押金申请
  AND da.elder_id IS NOT NULL
  AND COALESCE(da.actual_amount, da.apply_amount, 0) > 0;

-- 显示迁移结果统计
SELECT
    '迁移统计' as 项目,
    (SELECT COUNT(*) FROM expense_record) as 总记录数,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service') as 服务费记录,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'deposit') as 押金记录;