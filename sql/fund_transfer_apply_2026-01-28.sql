-- ========================================
-- 资金划拨系统完善 - 数据库修改脚本
-- 日期: 2026-01-28
-- ========================================

-- 检查并添加 fund_transfer 表字段
-- 注意：如果字段已存在，会报错，可以忽略

-- 1. 修改 fund_transfer 表，增加字段
ALTER TABLE fund_transfer ADD COLUMN is_paid CHAR(1) DEFAULT '0' COMMENT '是否已划拨 0-否 1-是';
ALTER TABLE fund_transfer ADD COLUMN paid_time DATETIME COMMENT '实际划拨时间';
ALTER TABLE fund_transfer ADD COLUMN paid_method VARCHAR(20) COMMENT '划拨方式 auto-自动 manual-手动申请';
ALTER TABLE fund_transfer ADD COLUMN apply_id BIGINT COMMENT '关联的申请ID（手动申请时）';
ALTER TABLE fund_transfer ADD COLUMN elder_id BIGINT COMMENT '老人ID';
ALTER TABLE fund_transfer ADD COLUMN order_id BIGINT COMMENT '关联订单ID';
ALTER TABLE fund_transfer ADD COLUMN billing_month VARCHAR(7) COMMENT '账单月份 (格式: 2025-02)';
ALTER TABLE fund_transfer ADD COLUMN status VARCHAR(30) DEFAULT 'pending' COMMENT '划拨单状态 pending-待划拨 processing-划拨中 completed-已完成 cancelled-已取消';

-- 添加索引
ALTER TABLE fund_transfer ADD INDEX idx_elder_id (elder_id);
ALTER TABLE fund_transfer ADD INDEX idx_order_id (order_id);
ALTER TABLE fund_transfer ADD INDEX idx_billing_month (billing_month);
ALTER TABLE fund_transfer ADD INDEX idx_is_paid (is_paid);
ALTER TABLE fund_transfer ADD INDEX idx_status (status);

-- 2. 创建资金划拨申请表
DROP TABLE IF EXISTS fund_transfer_apply;
CREATE TABLE fund_transfer_apply (
    apply_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '申请ID',
    apply_no VARCHAR(50) NOT NULL UNIQUE COMMENT '申请单号',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    apply_amount DECIMAL(15,2) NOT NULL COMMENT '申请总金额',
    apply_reason VARCHAR(500) COMMENT '申请原因',
    urgency_level VARCHAR(20) DEFAULT '一般' COMMENT '紧急程度',
    expected_use_date DATE COMMENT '期望使用日期',
    attachments TEXT COMMENT '附件材料',
    apply_status VARCHAR(30) DEFAULT 'draft' COMMENT '申请状态 draft-草稿 pending_family-待家属确认 pending_supervision-待监管审核 approved-已批准 rejected-已拒绝 completed-已完成',
    family_confirm_name VARCHAR(50) COMMENT '家属确认人',
    family_relation VARCHAR(20) COMMENT '家属关系',
    family_phone VARCHAR(20) COMMENT '家属电话',
    family_approve_time DATETIME COMMENT '家属审核时间',
    family_approve_opinion VARCHAR(500) COMMENT '家属审核意见',
    approver VARCHAR(50) COMMENT '监管审核人',
    approve_time DATETIME COMMENT '监管审核时间',
    approve_remark VARCHAR(500) COMMENT '监管审核意见',
    actual_amount DECIMAL(10,2) COMMENT '实际批准金额',
    use_time DATETIME COMMENT '实际划拨时间',
    create_by VARCHAR(64) COMMENT '创建人',
    create_time DATETIME COMMENT '创建时间',
    update_by VARCHAR(64) COMMENT '更新人',
    update_time DATETIME COMMENT '更新时间',
    remark VARCHAR(500) COMMENT '备注',
    KEY idx_institution_id (institution_id),
    KEY idx_elder_id (elder_id),
    KEY idx_apply_status (apply_status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨申请表';

-- 3. 创建资金划拨申请明细表
DROP TABLE IF EXISTS fund_transfer_apply_detail;
CREATE TABLE fund_transfer_apply_detail (
    detail_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '明细ID',
    apply_id BIGINT NOT NULL COMMENT '申请ID',
    transfer_id BIGINT NOT NULL COMMENT '划拨单ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    transfer_amount DECIMAL(12,2) NOT NULL COMMENT '划拨金额',
    billing_month VARCHAR(7) NOT NULL COMMENT '账单月份',
    create_time DATETIME COMMENT '创建时间',
    KEY idx_apply_id (apply_id),
    KEY idx_transfer_id (transfer_id),
    KEY idx_elder_id (elder_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资金划拨申请明细表';
