-- 添加订单来源字段到elder_check_in表
ALTER TABLE elder_check_in 
ADD COLUMN order_source VARCHAR(10) DEFAULT '1' 
COMMENT '订单来源：1-线下（管理后台），2-H5线上';
