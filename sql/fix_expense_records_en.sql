-- Fix expense records script
-- Clean up existing wrong expense records and generate correct ones based on order_item

-- 1. Clean up existing expense records
DELETE FROM expense_record;

-- 2. Generate correct income records based on order_item
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
    CONCAT('Order Payment - ', oi.order_no, ' - ',
           CASE
               WHEN item.item_type = 'deposit' THEN 'Deposit Fee'
               WHEN item.item_type = 'member_fee' THEN 'Member Fee'
               WHEN item.item_type = 'bed_fee' THEN 'Bed Fee'
               WHEN item.item_type = 'care_fee' THEN 'Care Fee'
               ELSE 'Service Fee'
           END
    ) as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    'system' as create_by,
    oi.create_time,
    'Generated from order item' as remark
FROM order_info oi
LEFT JOIN order_item item ON oi.order_id = item.order_id
LEFT JOIN account_info a ON oi.elder_id = a.elder_id
WHERE oi.order_status = '1'  -- Paid orders
  AND item.item_id IS NOT NULL
  AND item.total_amount > 0;

-- 3. Generate first month service fee deduction records for check-in orders
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
    CONCAT('First Month Service Fee Deduction - ', oi.order_no) as description,
    oi.order_id as related_id,
    'order_info' as related_type,
    'system' as create_by,
    oi.create_time,
    'First month service fee deduction for check-in' as remark
FROM order_info oi
LEFT JOIN order_item item ON oi.order_id = item.order_id
LEFT JOIN account_info a ON oi.elder_id = a.elder_id
WHERE oi.order_status = '1'  -- Paid orders
  AND oi.order_type = '1'  -- Check-in orders
  AND item.item_type IN ('bed_fee', 'care_fee')
GROUP BY oi.order_id, oi.elder_id, oi.order_no, a.account_id
HAVING amount > 0;

-- Show statistics after fix
SELECT
    'Statistics After Fix' as Item,
    (SELECT COUNT(*) FROM expense_record) as TotalRecords,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service' AND transaction_type = 'income') as ServiceIncome,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'service' AND transaction_type = 'expense') as ServiceExpense,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'deposit') as DepositRecords,
    (SELECT COUNT(*) FROM expense_record WHERE expense_type = 'member') as MemberRecords;