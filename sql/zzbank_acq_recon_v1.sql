-- 郑州银行收单对账（uMtBillApply/uMtBillQuery/newsddzfiledown）：
-- 对账批次 + 差异明细两张表。文件按清算日期下载，与本地 bank_transaction 比对。
-- 执行顺序：部署对账版本前执行。回滚：DROP TABLE bank_recon_diff; DROP TABLE bank_recon_run;

CREATE TABLE IF NOT EXISTS bank_recon_run (
    run_id bigint NOT NULL AUTO_INCREMENT COMMENT '对账批次ID',
    mer_id varchar(32) NOT NULL COMMENT '收单商户号',
    clearing_date date NOT NULL COMMENT '清算日期',
    status varchar(20) NOT NULL DEFAULT 'running' COMMENT '状态(running/success/failed/no_bill)',
    file_name varchar(256) NULL COMMENT '对账文件名',
    bill_stat varchar(2) NULL COMMENT '对账单生成标志(00未生成/01已生成)',
    total_rows int NOT NULL DEFAULT 0 COMMENT '文件交易笔数',
    matched_rows int NOT NULL DEFAULT 0 COMMENT '匹配笔数',
    diff_rows int NOT NULL DEFAULT 0 COMMENT '差异笔数',
    resp_code varchar(16) NULL COMMENT '最近银行响应码',
    resp_message varchar(500) NULL COMMENT '最近银行响应信息',
    download_md5 varchar(64) NULL COMMENT '下载文件MD5',
    create_by varchar(64) NULL,
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NULL,
    PRIMARY KEY (run_id),
    KEY idx_run_date (mer_id, clearing_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收单对账批次';

CREATE TABLE IF NOT EXISTS bank_recon_diff (
    diff_id bigint NOT NULL AUTO_INCREMENT COMMENT '差异ID',
    run_id bigint NOT NULL COMMENT '对账批次ID',
    diff_type varchar(30) NOT NULL COMMENT '差异类型(BANK_ONLY银行有本地无/LOCAL_ONLY本地有银行无/AMOUNT_MISMATCH金额不符/SERIAL_MISMATCH流水不符)',
    request_no varchar(32) NOT NULL COMMENT '商户订单号',
    bank_serial_no varchar(64) NULL COMMENT '银行交易流水号',
    bank_amount decimal(15,2) NULL COMMENT '银行清算金额(元)',
    bank_detail varchar(500) NULL COMMENT '银行行摘要',
    local_amount decimal(15,2) NULL COMMENT '本地金额(元)',
    local_serial_no varchar(64) NULL COMMENT '本地银行流水号',
    local_status varchar(20) NULL COMMENT '本地交易状态',
    handled tinyint NOT NULL DEFAULT 0 COMMENT '是否已处理(0否/1是)',
    handle_remark varchar(500) NULL,
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (diff_id),
    KEY idx_diff_run (run_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='收单对账差异明细';
