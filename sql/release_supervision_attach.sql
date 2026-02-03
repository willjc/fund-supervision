-- ======================================================
-- 机构解除监管申请附件表
-- 用于存储机构申请解除监管时上传的附件材料
-- ======================================================

CREATE TABLE IF NOT EXISTS release_supervision_attach (
  attach_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '附件ID',
  release_id BIGINT NOT NULL COMMENT '解除监管申请ID',
  file_name VARCHAR(200) COMMENT '文件名称',
  file_path VARCHAR(500) COMMENT '文件路径',
  file_size BIGINT COMMENT '文件大小(字节)',
  upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
  create_by VARCHAR(64) DEFAULT '' COMMENT '创建者',
  INDEX idx_release_id (release_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='解除监管申请附件表';
