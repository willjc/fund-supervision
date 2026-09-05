-- 支付确认与拨付 V2；MySQL 8.0，执行一次。先停新请求/定时任务并备份相关表。
-- 不自动执行、不回填历史拨付资格、不更改历史余额。DDL 隐式提交，逐条核查。
SET NAMES utf8mb4;
ALTER TABLE bank_transaction
  ADD bank_time varchar(30) NULL,
  ADD environment varchar(16) NULL,
  ADD snapshot_json text NULL,
  ADD attempt_no int NOT NULL DEFAULT 1,
  ADD bank_status varchar(16) NULL,
  ADD booking_status varchar(16) NOT NULL DEFAULT 'PENDING',
  ADD next_query_time datetime NULL,
  ADD query_count int NOT NULL DEFAULT 0,
  ADD manual_review tinyint NOT NULL DEFAULT 0,
  ADD lease_until datetime NULL,
  ADD return_time varchar(30) NULL,
  ADD return_reason varchar(256) NULL,
  DROP INDEX uk_bank_business,
  ADD UNIQUE KEY uk_bank_business_attempt (business_type,business_id,attempt_no),
  ADD active_business varchar(100) GENERATED ALWAYS AS
    (CASE WHEN status IN ('PENDING','UNKNOWN') THEN CONCAT(business_type,':',business_id) ELSE NULL END) STORED,
  ADD UNIQUE KEY uk_bank_active_business (active_business),
  ADD KEY idx_bank_due (manual_review,next_query_time);
ALTER TABLE account_info
  ADD service_reserved decimal(15,2) NOT NULL DEFAULT 0,
  ADD deposit_reserved decimal(15,2) NOT NULL DEFAULT 0,
  ADD bank_service_balance decimal(15,2) NOT NULL DEFAULT 0 COMMENT '新银行支付服务费来源余额，历史资金不回填',
  ADD bank_deposit_balance decimal(15,2) NOT NULL DEFAULT 0 COMMENT '新银行支付押金来源余额，历史资金不回填';
ALTER TABLE fund_transfer
  ADD bank_eligible tinyint NOT NULL DEFAULT 0,
  ADD bank_transaction_id bigint NULL,
  ADD source_key varchar(128) NULL,
  ADD balance_type varchar(16) NOT NULL DEFAULT 'SERVICE',
  ADD UNIQUE KEY uk_transfer_source (source_key),
  ADD KEY idx_transfer_bank_due (bank_eligible,status);
ALTER TABLE bank_merchant_config
  ADD basic_account_name varchar(200) NULL,
  ADD basic_bank_code varchar(32) NULL,
  ADD cross_bank tinyint NOT NULL DEFAULT 0,
  ADD supervision_agreement_no varchar(64) NULL,
  ADD payout_enabled tinyint NOT NULL DEFAULT 0;
-- 已完成旧流水仅标记本地已记账，绝不重新查单或补发。
UPDATE bank_transaction SET booking_status='DONE' WHERE status='SUCCESS';
-- 新任务默认暂停，启用须完成独立联调验收；旧任务执行入口也有关闭保护。
INSERT INTO sys_job(job_name,job_group,invoke_target,cron_expression,misfire_policy,concurrent,status,create_by,create_time,remark)
SELECT '银行结果补查','DEFAULT','bankSettlementTask.reconcile()','0/15 * * * * ?','2','1','1','admin',NOW(),'验收后人工启用；不发送新拨付'
WHERE NOT EXISTS(SELECT 1 FROM sys_job WHERE invoke_target='bankSettlementTask.reconcile()');
INSERT INTO sys_job(job_name,job_group,invoke_target,cron_expression,misfire_policy,concurrent,status,create_by,create_time,remark)
SELECT '银行到期拨付','DEFAULT','bankSettlementTask.dispatch()','0 * * * * ?','2','1','1','admin',NOW(),'验收后人工启用；只处理新银行资金'
WHERE NOT EXISTS(SELECT 1 FROM sys_job WHERE invoke_target='bankSettlementTask.dispatch()');
-- 回滚：先关闭银行新请求、暂停以上任务，保留补查及全部新增审计列。
-- 银行已发生交易后禁止 DROP 列/恢复旧库或旧假拨付版本；向前修复并核对银行。
-- 尚无 V2 业务数据时才可另行审批恢复备份；不得自动删除银行流水。
