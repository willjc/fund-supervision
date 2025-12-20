-- 修正费用记录脚本
-- 清理现有的错误费用记录，然后根据 order_item 生成正确的费用记录

-- 1. 清理现有的费用记录
DELETE FROM expense_record;

-- 2. 根据订单明细生成正确的费用记录
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, create_by, create_time, remark
)
SELECT
    oi.elder_id,
    a.account_id,
    CASE
        WHEN item.item_type = 'deposit' THEN 'deposit'
        WHEN item.item_type = 'member_fee' THEN 'member'
        ELSE 'service'
    END as expense_type,
    'income' as transaction_type,
    item.total_amount as amount,
    CONCAT('订单支付 - ', oi.order_no, ' - ',
           CASE
               WHEN item.item_type = 'deposit' THEN '押金缴纳'
               WHEN item.item_type = 'member_fee' THEN '会员费缴纳'
               WHEN item.item_type = 'bed_fee' THEN '床位费缴纳'
               WHEN item.item_type = 'care_fee' THEN '护理费缴纳'
               ELSE '服务费缴纳'
           END
    ) as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    'system' as create_by,
    oi.create_time,
    '根据订单明细生成的费用记录' as remark
FROM order_info oi
LEFT JOIN order_item item ON oi.order_id = item.order_id
LEFT JOIN account_info a ON oi.elder_id = a.elder_id
WHERE oi.order_status = '1'  -- 已支付订单
  AND item.item_id IS NOT NULL
  AND item.total_amount > 0;

-- 3. 为入驻订单生成首月服务费扣除记录
INSERT INTO expense_record (
    elder_id, account_id, expense_type, transaction_type, amount,
    description, related_id, related_type, create_by, create_time, remark
)
SELECT
    oi.elder_id,
    a.account_id,
    'service' as expense_type,
    'expense' as transaction_type,
    SUM(CASE WHEN item.item_type IN ('bed_fee', 'care_fee') THEN item.total_amount ELSE 0 END) as amount,
    CONCAT('首月服务费扣除 - ', oi.order_no) as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    'system' as create_by,
    oi.create_time,
    '入驻首月服务费扣除' as remark
FROM order_info oi
LEFT JOIN order_item item ON oi.order_id = item.order_id
LEFT JOIN account_info a ON oi.elder_id = a.elder_id
WHERE oi.order_status = '1'  -- 已支付订单
  AND oi.order_type = '1'  -- 入驻订单
  AND item.item_type IN ('bed_fee', 'care_fee')
GROUP BY oi.order_id, oi.elder_id, oi.order_no, a.account_id
HAVING amount > 0;

-- 显示修正后的统计信息
SELECT
    '修正后统计' as 项目,
    (SELECT COUNT(*) FROM expense_record) as 总记录数,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service' AND transaction_type = 'income') as 服务费收入,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service' AND transaction_type = 'expense') as 服务费支出,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'deposit') as 押金记录,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'member') as 会员费记录;