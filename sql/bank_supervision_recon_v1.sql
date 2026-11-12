-- 监管对账（OBK0071706 yldztz + 开放规范 filedownload/v1）批次表。
-- 目的：记录每次 706 申请、银行返回、文件下载/落盘结果；差异表待银行文件样例确认后再加。
-- 执行顺序：部署监管对账页面前执行。回滚：DROP TABLE bank_supervision_recon_run;

CREATE TABLE IF NOT EXISTS bank_supervision_recon_run (
    run_id bigint NOT NULL AUTO_INCREMENT COMMENT '对账批次ID',
    institution_id bigint NOT NULL COMMENT '机构ID',
    account_no varchar(40) NOT NULL COMMENT '监管账号',
    account_name varchar(256) NULL COMMENT '监管账户名称',
    trandt date NOT NULL COMMENT '对账日期',
    status varchar(20) NOT NULL DEFAULT 'running' COMMENT 'running/processing/success/empty/failed',
    file_name varchar(256) NULL COMMENT '银行返回的文件名',
    file_size bigint NULL COMMENT '文件字节数',
    file_md5 varchar(64) NULL COMMENT '文件MD5',
    saved_path varchar(512) NULL COMMENT '服务器落盘路径',
    resp_code varchar(32) NULL COMMENT '银行返回码',
    resp_msg varchar(500) NULL COMMENT '银行返回信息',
    total_rows int NOT NULL DEFAULT 0 COMMENT '文件行数（解析后）',
    matched_rows int NOT NULL DEFAULT 0 COMMENT '匹配行数（解析后）',
    diff_rows int NOT NULL DEFAULT 0 COMMENT '差异行数（解析后）',
    retry_count int NOT NULL DEFAULT 0 COMMENT '重新获取次数',
    create_by varchar(64) NULL,
    create_time datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
    update_time datetime NULL,
    PRIMARY KEY (run_id),
    KEY idx_institution_date (institution_id, trandt)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='监管对账批次';
