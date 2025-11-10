-- 简化的测试数据插入脚本
USE newzijin;

-- 1. 先清空现有测试数据（如果存在）
DELETE FROM balance_warning WHERE warning_id <= 10;
DELETE FROM fund_transfer WHERE transfer_id <= 10;
DELETE FROM transaction_record WHERE transaction_id <= 10;
DELETE FROM account_info WHERE account_id <= 10;
DELETE FROM elder_info WHERE elder_id <= 10;

-- 重置自增ID
ALTER TABLE elder_info AUTO_INCREMENT = 1;
ALTER TABLE account_info AUTO_INCREMENT = 1;
ALTER TABLE transaction_record AUTO_INCREMENT = 1;
ALTER TABLE fund_transfer AUTO_INCREMENT = 1;
ALTER TABLE balance_warning AUTO_INCREMENT = 1;

-- 2. 插入老人信息测试数据
INSERT INTO `elder_info` (`elder_name`, `gender`, `id_card`, `birth_date`, `age`, `phone`, `family_phone`, `family_name`, `relation`, `address`, `health_status`, `care_level`, `institution_id`, `check_in_date`, `room_no`, `bed_no`, `status`, `remark`) VALUES
('张大爷', '1', '110101192001011234', '1920-01-01', 104, '13900139001', '13900139001', '张儿子', '父子', '北京市朝阳区某某小区1号楼', '3', '3', 1, '2020-01-15', 'A101', '01', '1', '身体健康状况良好'),
('李奶奶', '2', '110101192502022345', '1925-02-02', 99, '13900139002', '13900139002', '李女儿', '母女', '北京市西城区某某小区2号楼', '2', '2', 1, '2020-03-20', 'B202', '02', '1', '需要半护理'),
('王爷爷', '1', '110101193003033456', '1930-03-03', 94, '13900139003', '13900139003', '王孙子', '祖孙', '北京市东城区某某小区3号楼', '4', '3', 1, '2021-05-10', 'C303', '03', '1', '需要全护理');

-- 3. 插入账户信息测试数据
INSERT INTO `account_info` (`account_no`, `elder_id`, `institution_id`, `service_balance`, `deposit_balance`, `member_balance`, `reserve_balance`, `total_balance`, `monthly_fee`, `account_status`, `open_date`, `remark`) VALUES
('ACC001', 1, 1, 15000.00, 5000.00, 3000.00, 2000.00, 25000.00, 3000.00, '1', NOW(), '张大爷账户'),
('ACC002', 2, 1, 8000.00, 5000.00, 2000.00, 1500.00, 16500.00, 2800.00, '1', NOW(), '李奶奶账户'),
('ACC003', 3, 1, 12000.00, 5000.00, 2500.00, 1000.00, 20500.00, 3500.00, '1', NOW(), '王爷爷账户');

-- 4. 插入交易记录测试数据
INSERT INTO `transaction_record` (`transaction_no`, `account_id`, `elder_id`, `institution_id`, `transaction_type`, `transaction_amount`, `account_type`, `before_balance`, `after_balance`, `transaction_date`, `description`, `operator`, `remark`) VALUES
('TRX001', 1, 1, 1, '1', 5000.00, '1', 20000.00, 25000.00, '2024-01-15 10:00:00', '家属充值', '张儿子', '充值5000元'),
('TRX002', 1, 1, 1, '4', 3000.00, '1', 25000.00, 22000.00, '2024-02-01 09:30:00', '月度服务费扣除', '系统自动', '扣除2月服务费'),
('TRX003', 2, 2, 1, '1', 3000.00, '1', 13500.00, 16500.00, '2024-01-20 14:20:00', '家属充值', '李女儿', '充值3000元'),
('TRX004', 2, 2, 1, '4', 2800.00, '1', 16500.00, 13700.00, '2024-02-05 10:15:00', '月度服务费扣除', '系统自动', '扣除2月服务费'),
('TRX005', 3, 3, 1, '1', 8000.00, '1', 12500.00, 20500.00, '2024-01-25 16:45:00', '家属充值', '王孙子', '充值8000元'),
('TRX006', 3, 3, 1, '4', 3500.00, '1', 20500.00, 17000.00, '2024-02-10 11:30:00', '月度服务费扣除', '系统自动', '扣除2月服务费');

-- 5. 插入余额预警测试数据
INSERT INTO `balance_warning` (`warning_no`, `account_id`, `elder_id`, `institution_id`, `warning_type`, `current_balance`, `available_months`, `monthly_fee`, `warning_reason`, `suggestion`, `warning_status`, `remark`) VALUES
('WAR001', 2, 2, 1, '3', 13700.00, 4, 2800.00, '账户余额偏低，仅够使用4个月', '建议联系家属适时充值', '0', '余额预警提示'),
('WAR002', 1, 1, 1, '2', 22000.00, 7, 3000.00, '账户余额适中，可使用7个月', '建议关注账户余额变化', '0', '余额警告提醒'),
('WAR003', 3, 3, 1, '1', 17000.00, 4, 3500.00, '账户余额一般，可使用4个月', '建议适时关注余额情况', '0', '余额提示信息');

-- 确保数据插入成功
SELECT '老人信息数据' as table_name, COUNT(*) as count FROM elder_info
UNION ALL
SELECT '账户信息数据', COUNT(*) FROM account_info
UNION ALL
SELECT '交易记录数据', COUNT(*) FROM transaction_record
UNION ALL
SELECT '余额预警数据', COUNT(*) FROM balance_warning;