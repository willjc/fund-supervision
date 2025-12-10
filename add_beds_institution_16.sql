-- 为机构ID 16 (admin增加的养老机构) 添加空置的豪华床位和医疗床位
-- 执行日期：2025-12-09
-- 目的：确保H5端能够正常选择各种房间类型

-- 插入豪华床位（bed_type=2） - 对应"单人间"
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type,
  bed_status, price, room_area, has_toilet, has_balcony,
  has_window, has_air_conditioner, create_by, create_time
) VALUES
(16, '401-1', '401', 4, '2', '0', 2200.00, 24.0, 1, 0, 1, 1, 'admin', NOW()),
(16, '401-2', '401', 4, '2', '0', 2200.00, 24.0, 1, 0, 1, 1, 'admin', NOW()),
(16, '402-1', '402', 4, '2', '0', 2400.00, 26.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '402-2', '402', 4, '2', '0', 2400.00, 26.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '403-1', '403', 4, '2', '0', 2600.00, 28.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '403-2', '403', 4, '2', '0', 2600.00, 28.0, 1, 1, 1, 1, 'admin', NOW());

-- 插入医疗床位（bed_type=3） - 对应"VIP房间"
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type,
  bed_status, price, room_area, has_toilet, has_balcony,
  has_window, has_air_conditioner, create_by, create_time
) VALUES
(16, '501-1', '501', 5, '3', '0', 3200.00, 32.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '501-2', '501', 5, '3', '0', 3200.00, 32.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '502-1', '502', 5, '3', '0', 3500.00, 35.0, 1, 1, 1, 1, 'admin', NOW()),
(16, '502-2', '502', 5, '3', '0', 3500.00, 35.0, 1, 1, 1, 1, 'admin', NOW());

-- 插入更多普通床位（bed_type=1） - 对应"双人间"/"三人间"
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type,
  bed_status, price, room_area, has_toilet, has_balcony,
  has_window, has_air_conditioner, create_by, create_time
) VALUES
(16, '303-1', '303', 3, '1', '0', 1000.00, 18.0, 0, 0, 1, 0, 'admin', NOW()),
(16, '303-2', '303', 3, '1', '0', 1000.00, 18.0, 0, 0, 1, 0, 'admin', NOW()),
(16, '304-1', '304', 3, '1', '0', 1200.00, 20.0, 0, 0, 1, 0, 'admin', NOW()),
(16, '304-2', '304', 3, '1', '0', 1200.00, 20.0, 0, 0, 1, 0, 'admin', NOW());

-- 更新机构统计信息
UPDATE pension_institution
SET bed_count = (
  SELECT COUNT(*) FROM bed_info WHERE institution_id = 16
),
available_beds = (
  SELECT COUNT(*) FROM bed_info WHERE institution_id = 16 AND bed_status = '0'
),
update_by = 'admin',
update_time = NOW()
WHERE institution_id = 16;

-- 验证插入结果
SELECT institution_id, institution_name, bed_count, available_beds
FROM pension_institution
WHERE institution_id = 16;

-- 显示床位类型分布
SELECT
  bed_type,
  CASE bed_type
    WHEN '1' THEN '普通床位(双人间/三人间)'
    WHEN '2' THEN '豪华床位(单人间)'
    WHEN '3' THEN '医疗床位(VIP房间)'
  END as bed_type_name,
  COUNT(*) as total_count,
  SUM(CASE WHEN bed_status = '0' THEN 1 ELSE 0 END) as available_count,
  MIN(price) as min_price,
  MAX(price) as max_price,
  ROUND(AVG(price), 2) as avg_price
FROM bed_info
WHERE institution_id = 16
GROUP BY bed_type
ORDER BY bed_type;