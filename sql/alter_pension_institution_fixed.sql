-- ===================================================================
-- pension_institution表结构变更(修正版)
-- 添加筛选功能所需的新字段
-- ===================================================================

-- 1. 添加区县代码字段(关联字典) - 修正:放在street字段之后
ALTER TABLE pension_institution
ADD COLUMN district_code VARCHAR(50) COMMENT '区县代码(关联pension_district字典)' AFTER street;

-- 2. 添加机构性质字段
ALTER TABLE pension_institution
ADD COLUMN institution_nature CHAR(1) DEFAULT '1' COMMENT '机构性质: 1-民办 2-公办 3-公建民营' AFTER institution_type;

-- 3. 添加收住类型字段(多选,逗号分隔)
ALTER TABLE pension_institution
ADD COLUMN care_levels VARCHAR(100) COMMENT '收住类型(多选): 1-自理,2-半护理,3-全护理,4-失能,5-失智' AFTER bed_count;

-- 4. 添加医疗条件字段
ALTER TABLE pension_institution
ADD COLUMN medical_condition CHAR(1) COMMENT '医疗条件: 1-内设医疗机构,2-与医疗机构合作,3-自营医疗机构,4-无医养结合' AFTER care_levels;

-- 5. 添加星级评分字段
ALTER TABLE pension_institution
ADD COLUMN rating_level INT DEFAULT 3 COMMENT '星级评分: 1-5星' AFTER medical_condition;

-- 6. 添加价格区间字段
ALTER TABLE pension_institution
ADD COLUMN price_range_min DECIMAL(10,2) DEFAULT 0.00 COMMENT '最低价格(元/月)' AFTER rating_level,
ADD COLUMN price_range_max DECIMAL(10,2) DEFAULT 0.00 COMMENT '最高价格(元/月)' AFTER price_range_min;

-- 7. 添加免费试住标志
ALTER TABLE pension_institution
ADD COLUMN free_trial CHAR(1) DEFAULT '0' COMMENT '是否支持免费试住: 0-否 1-是' AFTER price_range_max;

-- 8. 添加索引以优化查询性能
ALTER TABLE pension_institution
ADD INDEX idx_district_code (district_code),
ADD INDEX idx_institution_nature (institution_nature),
ADD INDEX idx_medical_condition (medical_condition),
ADD INDEX idx_rating_level (rating_level),
ADD INDEX idx_price_range (price_range_min, price_range_max);

-- 9. 为现有机构设置默认值
-- 可以根据实际情况批量更新
UPDATE pension_institution
SET
    institution_nature = '1',  -- 默认民办
    care_levels = '1,2,3',     -- 默认支持自理、半护理、全护理
    medical_condition = '2',    -- 默认与医疗机构合作
    rating_level = 3,          -- 默认3星
    price_range_min = 1000.00,
    price_range_max = 3000.00,
    free_trial = '0'
WHERE institution_id IS NOT NULL;

-- 说明:
-- 1. district_code 使用国家标准区划代码,与字典保持一致
-- 2. care_levels 支持多选,格式如 "1,2,3" 表示支持自理、半护理、全护理
-- 3. 价格区间用于筛选,实际价格可能在pension_fee_standard表中详细定义
-- 4. 索引优化了常用筛选条件的查询性能
-- 5. 执行此脚本前请先执行字典数据脚本
