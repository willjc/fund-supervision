-- 仅供隔离 MySQL 实例，全部为合成结构/数据，不连接业务库。
CREATE DATABASE bank_payout_test CHARACTER SET utf8mb4;
USE bank_payout_test;
CREATE TABLE account_info(account_id bigint PRIMARY KEY,elder_id bigint,institution_id bigint,
  account_status char(1),total_balance decimal(12,2),service_balance decimal(12,2),
  deposit_balance decimal(12,2),member_balance decimal(12,2),update_time datetime);
CREATE TABLE fund_transfer(transfer_id bigint PRIMARY KEY,institution_id bigint,elder_id bigint,
  transfer_amount decimal(15,2),transfer_date date,status varchar(30),is_paid char(1),transfer_status char(1),
  approve_user varchar(64),approve_time datetime,execute_user varchar(64),execute_time datetime,
  bank_order_no varchar(128),failure_reason varchar(500),paid_time datetime,update_time datetime,
  apply_id bigint,paid_method varchar(20),order_id bigint,transfer_no varchar(64),transfer_type varchar(20),
  transfer_period varchar(20),elder_count int,billing_month varchar(20),create_by varchar(64),
  create_time datetime,update_by varchar(64),remark varchar(500));
CREATE TABLE sys_job(job_id bigint AUTO_INCREMENT PRIMARY KEY,job_name varchar(64),job_group varchar(64),
  invoke_target varchar(500),cron_expression varchar(255),misfire_policy char(1),concurrent char(1),
  status char(1),create_by varchar(64),create_time datetime,remark varchar(500));
CREATE TABLE pension_institution (institution_id BIGINT PRIMARY KEY, institution_name VARCHAR(200));
CREATE TABLE elder_info (elder_id BIGINT PRIMARY KEY, elder_name VARCHAR(100));
