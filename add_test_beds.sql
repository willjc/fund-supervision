-- 为机构ID 22(admin第二个机构)添加测试床位数据
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type, 
  bed_status, price, room_area, has_toilet, has_balcony, 
  has_window, has_air_conditioner, create_by, create_time
) VALUES 
-- 豪华床位（bed_type=2）
(22, '101-1', '101', 1, '2', '0', 2500.00, 25.5, 1, 1, 1, 1, 'admin', NOW()),
(22, '102-1', '102', 1, '2', '0', 2800.00, 28.0, 1, 1, 1, 1, 'admin', NOW()),
(22, '201-1', '201', 2, '2', '0', 3000.00, 30.0, 1, 1, 1, 1, 'admin', NOW()),
(22, '202-1', '202', 2, '2', '0', 2600.00, 27.0, 1, 0, 1, 1, 'admin', NOW()),
(22, '301-1', '301', 3, '2', '0', 3200.00, 32.0, 1, 1, 1, 1, 'admin', NOW()),

-- 普通床位（bed_type=1）
(22, '103-1', '103', 1, '1', '0', 1500.00, 20.0, 0, 0, 1, 0, 'admin', NOW()),
(22, '103-2', '103', 1, '1', '0', 1500.00, 20.0, 0, 0, 1, 0, 'admin', NOW()),
(22, '104-1', '104', 1, '1', '0', 1600.00, 21.0, 0, 0, 1, 0, 'admin', NOW()),
(22, '104-2', '104', 1, '1', '0', 1600.00, 21.0, 0, 0, 1, 0, 'admin', NOW()),
(22, '203-1', '203', 2, '1', '0', 1700.00, 22.0, 0, 0, 1, 1, 'admin', NOW()),
(22, '203-2', '203', 2, '1', '0', 1700.00, 22.0, 0, 0, 1, 1, 'admin', NOW()),
(22, '204-1', '204', 2, '1', '0', 1400.00, 19.0, 0, 0, 1, 0, 'admin', NOW()),
(22, '204-2', '204', 2, '1', '0', 1400.00, 19.0, 0, 0, 1, 0, 'admin', NOW()),

-- 医疗床位（bed_type=3）
(22, '205-1', '205', 2, '3', '0', 3500.00, 35.0, 1, 1, 1, 1, 'admin', NOW()),
(22, '205-2', '205', 2, '3', '0', 3500.00, 35.0, 1, 1, 1, 1, 'admin', NOW()),
(22, '303-1', '303', 3, '3', '0', 4000.00, 40.0, 1, 1, 1, 1, 'admin', NOW()),
(22, '303-2', '303', 3, '3', '0', 4000.00, 40.0, 1, 1, 1, 1, 'admin', NOW());

-- 更新机构的床位数信息
UPDATE pension_institution 
SET bed_count = 14, 
    available_beds = 14,
    update_by = 'admin',
    update_time = NOW()
WHERE institution_id = 22;

-- 为其他机构添加测试床位数据

-- 港湾养老 (ID=18)
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type, 
  bed_status, price, room_area, has_toilet, has_balcony, 
  has_window, has_air_conditioner, create_by, create_time
) VALUES 
(18, '101-1', '101', 1, '2', '0', 1800.00, 22.0, 1, 0, 1, 1, 'admin', NOW()),
(18, '101-2', '101', 1, '2', '0', 1800.00, 22.0, 1, 0, 1, 1, 'admin', NOW()),
(18, '102-1', '102', 1, '1', '0', 1200.00, 18.0, 0, 0, 1, 0, 'admin', NOW()),
(18, '102-2', '102', 1, '1', '0', 1200.00, 18.0, 0, 0, 1, 0, 'admin', NOW()),
(18, '201-1', '201', 2, '3', '0', 2500.00, 30.0, 1, 1, 1, 1, 'admin', NOW());

UPDATE pension_institution 
SET bed_count = 5, available_beds = 5
WHERE institution_id = 18;

-- 浏览 (ID=19)
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type, 
  bed_status, price, room_area, has_toilet, has_balcony, 
  has_window, has_air_conditioner, create_by, create_time
) VALUES 
(19, '101-1', '101', 1, '2', '0', 2000.00, 24.0, 1, 0, 1, 1, 'admin', NOW()),
(19, '101-2', '101', 1, '2', '0', 2000.00, 24.0, 1, 0, 1, 1, 'admin', NOW()),
(19, '102-1', '102', 1, '1', '0', 1300.00, 20.0, 0, 0, 1, 0, 'admin', NOW()),
(19, '102-2', '102', 1, '1', '0', 1300.00, 20.0, 0, 0, 1, 0, 'admin', NOW());

UPDATE pension_institution 
SET bed_count = 4, available_beds = 4
WHERE institution_id = 19;

-- 幸福养老 (ID=21)
INSERT INTO bed_info (
  institution_id, bed_number, room_number, floor, bed_type, 
  bed_status, price, room_area, has_toilet, has_balcony, 
  has_window, has_air_conditioner, create_by, create_time
) VALUES 
(21, '301-1', '301', 3, '2', '0', 2200.00, 26.0, 1, 1, 1, 1, 'admin', NOW()),
(21, '301-2', '301', 3, '2', '0', 2200.00, 26.0, 1, 1, 1, 1, 'admin', NOW()),
(21, '302-1', '302', 3, '1', '0', 1400.00, 21.0, 0, 0, 1, 0, 'admin', NOW()),
(21, '302-2', '302', 3, '1', '0', 1400.00, 21.0, 0, 0, 1, 0, 'admin', NOW()),
(21, '303-1', '303', 3, '3', '0', 2800.00, 32.0, 1, 1, 1, 1, 'admin', NOW());

UPDATE pension_institution 
SET bed_count = 5, available_beds = 5
WHERE institution_id = 21;
