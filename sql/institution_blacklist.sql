-- 机构黑名单表
CREATE TABLE IF NOT EXISTS `institution_blacklist` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `institution_id` BIGINT NOT NULL COMMENT '机构ID',
    `institution_name` VARCHAR(100) NOT NULL COMMENT '机构名称',
    `blacklist_type` VARCHAR(20) NOT NULL COMMENT '黑名单类型',
    `reason` VARCHAR(500) COMMENT '原因描述',
    `status` CHAR(1) DEFAULT '1' COMMENT '状态:1生效中 2已解除',
    `add_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
    `remove_time` DATETIME COMMENT '移除时间',
    `handle_opinion` VARCHAR(500) COMMENT '处理意见',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `remark` VARCHAR(500) COMMENT '备注',
    PRIMARY KEY (`id`),
    INDEX `idx_institution_id` (`institution_id`),
    INDEX `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='机构黑名单表';
