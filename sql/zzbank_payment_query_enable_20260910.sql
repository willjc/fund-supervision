-- 支付补查启用脚本（部署审批后执行，不修改订单、余额或旧支付交易）。
-- 前置：已备份数据库、旧 JAR、H5 和环境配置；后端部署本次查询入账修复。
-- 环境：ZZBANK_RECONCILIATION_ENABLED=true，ZZBANK_PAYMENT_QUERY_MIN_ID=12，
--       ZZBANK_PAYOUT_ENABLED=false。确认 ID 12 仍对应下列原银行请求。
-- 本脚本更新 sys_job；应用使用内存 Quartz，提交后重启后端才能重新载入任务。
SET NAMES utf8mb4;
SELECT transaction_id, request_no, business_id, amount, status, bank_status, booking_status
FROM bank_transaction WHERE transaction_id=12;

START TRANSACTION;
UPDATE sys_job SET status='0', update_by='admin', update_time=NOW()
WHERE invoke_target='bankSettlementTask.reconcile()' AND status='1'
  AND EXISTS (SELECT 1 FROM bank_transaction WHERE transaction_id=12
              AND request_no='BP1A746389E4594FA49A896413E726E7'
              AND business_type='PAY' AND business_id=145 AND amount=0.05);
SELECT ROW_COUNT() AS activated_jobs; -- 首次执行应为 1；不是 1 则检查后再启用。
COMMIT;

SELECT job_id, invoke_target, cron_expression, status FROM sys_job
WHERE invoke_target IN ('bankSettlementTask.reconcile()', 'bankSettlementTask.dispatch()');
-- 验证：reconcile 为 0；dispatch 仍为 1。交易 12 最终 SUCCESS / DONE；
-- 订单 145 paid_amount=0.05，唯一支付记录，0.03 首月拨付仍 pending / 未支付。
-- 停止/回滚：先将 ZZBANK_RECONCILIATION_ENABLED=false 并重启，暂停该 Quartz 任务；
-- 不删除成功入账记录，不还原余额，不回退至本地假拨付代码。
-- UPDATE sys_job SET status='1' WHERE invoke_target='bankSettlementTask.reconcile()';
