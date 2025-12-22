-- 创建机构评价表
CREATE TABLE institution_review (
    review_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评价ID',
    order_id VARCHAR(50) NOT NULL COMMENT '关联订单号',
    institution_id BIGINT NOT NULL COMMENT '机构ID',
    elder_id BIGINT NOT NULL COMMENT '老人ID',
    user_id BIGINT NOT NULL COMMENT '评价用户ID',
    
    -- 评分字段
    environment_rating INT NOT NULL DEFAULT 5 COMMENT '环境评分(1-5星)',
    service_rating INT NOT NULL DEFAULT 5 COMMENT '服务评分(1-5星)',
    price_rating INT NOT NULL DEFAULT 5 COMMENT '价格评分(1-5星)',
    average_rating DECIMAL(3,2) GENERATED ALWAYS AS ((environment_rating + service_rating + price_rating) / 3.0) STORED COMMENT '平均评分',
    
    -- 评价内容
    content TEXT COMMENT '评价内容',
    images JSON COMMENT '评价图片(JSON数组)',
    
    -- 审核状态
    status TINYINT NOT NULL DEFAULT 0 COMMENT '状态: 0-待审核, 1-已通过, 2-已拒绝',
    review_remark VARCHAR(500) COMMENT '审核备注',
    review_time DATETIME COMMENT '审核时间',
    review_by VARCHAR(50) COMMENT '审核人',
    
    -- 关联反馈（用于审核流程）
    feedback_id BIGINT COMMENT '关联反馈ID（用于审核）',
    
    -- 基本信息
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    
    -- 索引
    INDEX idx_order_id (order_id),
    INDEX idx_institution_id (institution_id),
    INDEX idx_elder_id (elder_id),
    INDEX idx_user_id (user_id),
    INDEX idx_status (status),
    INDEX idx_create_time (create_time),
    UNIQUE KEY uk_order_id (order_id) COMMENT '每个订单只能评价一次'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='机构评价表';
