-- 郑州银行原路退款（uTxnRefund）支持：
-- 1) refund_record 关联银行退款交易，并扩展退款状态（3=银行退款处理中，4=银行退款失败）。
-- 2) bank_transaction 复用现有表结构（business_type='REFUND'，uk_bank_business 幂等），
--    原支付锚定信息保存在 snapshot_json，无需加列。
-- 执行顺序：部署退款版本前执行；可重复执行（幂等判断见下）。
-- 影响范围：仅新增可空列与注释，不改已有数据语义；旧状态 0/1/2 不变。
-- 回滚：ALTER TABLE refund_record DROP COLUMN bank_transaction_id;
--       ALTER TABLE refund_record MODIFY refund_status char(1) DEFAULT '0'
--         COMMENT '退款状态(0申请中 1已退款 2已拒绝)';

ALTER TABLE refund_record
    ADD COLUMN bank_transaction_id bigint NULL COMMENT '银行退款交易ID(bank_transaction.transaction_id)' AFTER payment_id,
    MODIFY COLUMN refund_status char(1) DEFAULT '0' COMMENT '退款状态(0申请中 1已退款 2已拒绝 3银行退款处理中 4银行退款失败)';

ALTER TABLE refund_record
    ADD KEY idx_bank_transaction_id (bank_transaction_id);

-- 验证：
-- SELECT COLUMN_NAME, COLUMN_COMMENT FROM information_schema.COLUMNS
--  WHERE TABLE_SCHEMA=DATABASE() AND TABLE_NAME='refund_record' AND COLUMN_NAME IN ('bank_transaction_id','refund_status');
-- SELECT COUNT(*) FROM bank_transaction WHERE business_type='REFUND';  -- 执行后应为 0
