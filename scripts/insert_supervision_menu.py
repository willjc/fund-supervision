#!/usr/bin/env python3
# -*- coding: utf-8 -*-

"""
民政监管菜单插入脚本
插入民政监管功能模块的菜单配置到数据库
"""

import pymysql
import logging
from datetime import datetime

# 配置日志
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(levelname)s - %(message)s')
logger = logging.getLogger(__name__)

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'newzijin',
    'charset': 'utf8mb4'
}

# 民政监管菜单数据 (从3000开始，避免与其他模块冲突)
SUPERVISION_MENUS = [
    # 民政监管主菜单 (3000)
    (3000, '民政监管', 0, 2, 'supervision', None, None, 0, 1, 'M', '0', '0', '', 'monitor', 'admin', datetime.now(), '', None, '民政监管目录'),

    # 机构管理 (3100-3199)
    (3100, '机构管理', 3000, 1, 'institution', None, None, 1, 0, 'M', '0', '0', '', 'build', 'admin', datetime.now(), '', None, '机构管理目录'),
    (3101, '批量导入', 3100, 1, 'batchImport', 'supervision/institution/batchImport', None, 1, 0, 'C', '0', '0', 'supervision:institution:batchImport', 'upload', 'admin', datetime.now(), '', None, '机构批量导入'),
    (3102, '机构查询', 3100, 2, 'queryList', 'supervision/institution/queryList', None, 1, 0, 'C', '0', '0', 'supervision:institution:query', 'search', 'admin', datetime.now(), '', None, '机构信息查询'),
    (3103, '储备监管审批', 3100, 3, 'reserveList', 'supervision/institution/reserveList', None, 1, 0, 'C', '0', '0', 'supervision:institution:reserve', 'money', 'admin', datetime.now(), '', None, '机构储备监管审批'),
    (3104, '机构评级', 3100, 4, 'ratingList', 'supervision/institution/ratingList', None, 1, 0, 'C', '0', '0', 'supervision:institution:rating', 'star', 'admin', datetime.now(), '', None, '机构评级管理'),
    (3105, '黑名单管理', 3100, 5, 'blacklistList', 'supervision/institution/blacklistList', None, 1, 0, 'C', '0', '0', 'supervision:institution:blacklist', 'no', 'admin', datetime.now(), '', None, '黑名单管理'),

    # 预警核验 (3200-3299)
    (3200, '预警核验', 3000, 2, 'warning', None, None, 1, 0, 'M', '0', '0', '', 'warning', 'admin', datetime.now(), '', None, '预警核验目录'),
    (3201, '预警列表', 3200, 1, 'index', 'supervision/warning/index', None, 1, 0, 'C', '0', '0', 'supervision:warning:list', 'list', 'admin', datetime.now(), '', None, '预警列表'),
    (3202, '费用超额预警', 3200, 2, 'feeExcess', 'supervision/warning/feeExcess', None, 1, 0, 'C', '0', '0', 'supervision:warning:feeExcess', 'money', 'admin', datetime.now(), '', None, '费用超额预警'),
    (3203, '押金超额预警', 3200, 3, 'depositExcess', 'supervision/warning/depositExcess', None, 1, 0, 'C', '0', '0', 'supervision:warning:depositExcess', 'coin', 'admin', datetime.now(), '', None, '押金超额预警'),
    (3204, '入驻超额预警', 3200, 4, 'checkinExcess', 'supervision/warning/checkinExcess', None, 1, 0, 'C', '0', '0', 'supervision:warning:checkinExcess', 'peoples', 'admin', datetime.now(), '', None, '入驻超额预警'),
    (3205, '监管超额预警', 3200, 5, 'supervisionExcess', 'supervision/warning/supervisionExcess', None, 1, 0, 'C', '0', '0', 'supervision:warning:supervisionExcess', 'bank', 'admin', datetime.now(), '', None, '监管超额预警'),
    (3206, '风险保证金预警', 3200, 6, 'riskDepositExcess', 'supervision/warning/riskDepositExcess', None, 1, 0, 'C', '0', '0', 'supervision:warning:riskDepositExcess', 'shield', 'admin', datetime.now(), '', None, '风险保证金预警'),
    (3207, '大额支付预警', 3200, 7, 'largePayment', 'supervision/warning/largePayment', None, 1, 0, 'C', '0', '0', 'supervision:warning:largePayment', 'credit-card', 'admin', datetime.now(), '', None, '大额支付预警'),
    (3208, '突发风险预警', 3200, 8, 'emergencyRisk', 'supervision/warning/emergencyRisk', None, 1, 0, 'C', '0', '0', 'supervision:warning:emergencyRisk', 'bug', 'admin', datetime.now(), '', None, '突发风险预警'),

    # 账户管理 (3300-3399)
    (3300, '账户管理', 3000, 3, 'account', None, None, 1, 0, 'M', '0', '0', '', 'account', 'admin', datetime.now(), '', None, '账户管理目录'),
    (3301, '机构账户查询', 3300, 1, 'institutionAccount', 'supervision/account/institutionAccount', None, 1, 0, 'C', '0', '0', 'supervision:account:institution', 'list', 'admin', datetime.now(), '', None, '机构账户查询'),
    (3302, '会员费管理', 3300, 2, 'memberFee', 'supervision/account/memberFee', None, 1, 0, 'C', '0', '0', 'supervision:account:memberFee', 'money', 'admin', datetime.now(), '', None, '会员费管理'),
    (3303, '监管账户维护', 3300, 3, 'supervisionAccount', 'supervision/account/supervisionAccount', None, 1, 0, 'C', '0', '0', 'supervision:account:supervision', 'bank', 'admin', datetime.now(), '', None, '监管账户维护'),
    (3304, '订单管理', 3300, 4, 'orderList', 'supervision/account/orderList', None, 1, 0, 'C', '0', '0', 'supervision:account:order', 'shopping-cart', 'admin', datetime.now(), '', None, '订单管理'),
    (3305, '账户余额', 3300, 5, 'balanceList', 'supervision/account/balanceList', None, 1, 0, 'C', '0', '0', 'supervision:account:balance', 'wallet', 'admin', datetime.now(), '', None, '账户余额查询'),

    # 资金管理 (3400-3499)
    (3400, '资金管理', 3000, 4, 'fund', None, None, 1, 0, 'M', '0', '0', '', 'money', 'admin', datetime.now(), '', None, '资金管理目录'),
    (3401, '资金记录', 3400, 1, 'recordList', 'supervision/fund/recordList', None, 1, 0, 'C', '0', '0', 'supervision:fund:record', 'list', 'admin', datetime.now(), '', None, '资金记录查看'),
    (3402, '资金流动明细', 3400, 2, 'flowDetail', 'supervision/fund/flowDetail', None, 1, 0, 'C', '0', '0', 'supervision:fund:flow', 'chart', 'admin', datetime.now(), '', None, '资金流动明细'),
    (3403, '分配规则', 3400, 3, 'allocationRule', 'supervision/fund/allocationRule', None, 1, 0, 'C', '0', '0', 'supervision:fund:allocation', 'setting', 'admin', datetime.now(), '', None, '分配规则配置'),
    (3404, '资金统计', 3400, 4, 'statistics', 'supervision/fund/statistics', None, 1, 0, 'C', '0', '0', 'supervision:fund:statistics', 'chart', 'admin', datetime.now(), '', None, '资金统计概��'),

    # 公告管理 (3500-3599)
    (3500, '公告管理', 3000, 5, 'announcement', None, None, 1, 0, 'M', '0', '0', '', 'message', 'admin', datetime.now(), '', None, '公告管理目录'),
    (3501, '公告列表', 3500, 1, 'list', 'supervision/announcement/list', None, 1, 0, 'C', '0', '0', 'supervision:announcement:list', 'list', 'admin', datetime.now(), '', None, '公告列表'),
    (3502, '发布公告', 3500, 2, 'add', 'supervision/announcement/add', None, 1, 0, 'C', '0', '0', 'supervision:announcement:add', 'edit', 'admin', datetime.now(), '', None, '发布公告'),
    (3503, '公告模板', 3500, 3, 'template', 'supervision/announcement/template', None, 1, 0, 'C', '0', '0', 'supervision:announcement:template', 'document', 'admin', datetime.now(), '', None, '公告模板管理'),
    (3504, '阅读统计', 3500, 4, 'statistics', 'supervision/announcement/statistics', None, 1, 0, 'C', '0', '0', 'supervision:announcement:statistics', 'chart', 'admin', datetime.now(), '', None, '公告阅读统计'),

    # 反馈管理 (3600-3699)
    (3600, '反馈管理', 3000, 6, 'feedback', None, None, 1, 0, 'M', '0', '0', '', 'question', 'admin', datetime.now(), '', None, '反馈管理目录'),
    (3601, '反馈列表', 3600, 1, 'list', 'supervision/feedback/list', None, 1, 0, 'C', '0', '0', 'supervision:feedback:list', 'list', 'admin', datetime.now(), '', None, '反馈列表'),
    (3602, '反馈统计', 3600, 2, 'statistics', 'supervision/feedback/statistics', None, 1, 0, 'C', '0', '0', 'supervision:feedback:statistics', 'chart', 'admin', datetime.now(), '', None, '反馈统计'),
    (3603, '热点反馈', 3600, 3, 'hot', 'supervision/feedback/hot', None, 1, 0, 'C', '0', '0', 'supervision:feedback:hot', 'star', 'admin', datetime.now(), '', None, '热点反馈'),
    (3604, '满意度评价', 3600, 4, 'satisfaction', 'supervision/feedback/satisfaction', None, 1, 0, 'C', '0', '0', 'supervision:feedback:satisfaction', 'like', 'admin', datetime.now(), '', None, '满意度评价')
]

def get_db_connection():
    """获取数据库连接"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        logger.info("数据库连接成功")
        return connection
    except Exception as e:
        logger.error(f"数据库连接失败: {e}")
        return None

def clear_existing_menus(connection):
    """清除现有的民政监管菜单"""
    try:
        with connection.cursor() as cursor:
            # 删除现有的民政监管菜单（menu_id 3000-3999）
            delete_sql = "DELETE FROM sys_menu WHERE menu_id >= 3000 AND menu_id < 4000"
            cursor.execute(delete_sql)
            deleted_count = cursor.rowcount
            connection.commit()
            logger.info("清除了 {} 个现有民政监管菜单".format(deleted_count))
            return True
    except Exception as e:
        logger.error("清除现有菜单失败: {}".format(e))
        connection.rollback()
        return False

def insert_supervision_menus(connection):
    """插入民政监管菜单"""
    success_count = 0
    error_count = 0

    try:
        with connection.cursor() as cursor:
            # 插入菜单的SQL语句（20个字段）
            insert_sql = """
            INSERT INTO sys_menu
            (menu_id, menu_name, parent_id, order_num, path, component, query, route_name,
             is_frame, is_cache, menu_type, visible, status, perms, icon, create_by,
             create_time, update_by, update_time, remark)
            VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
            """

            for menu in SUPERVISION_MENUS:
                try:
                    cursor.execute(insert_sql, menu)
                    success_count += 1
                    logger.info("[OK] 菜单插入成功: {} (ID: {})".format(menu[1], menu[0]))
                except Exception as e:
                    error_count += 1
                    logger.error("[FAIL] 菜单插入失败: {} (ID: {}) - {}".format(menu[1], menu[0], e))

            connection.commit()

    except Exception as e:
        logger.error("插入菜单过程中发生错误: {}".format(e))
        connection.rollback()
        return False

    logger.info("菜单插入完成: 成功 {} 个，失败 {} 个".format(success_count, error_count))
    return error_count == 0

def main():
    """主函数"""
    logger.info("开始插入民政监管菜单...")

    # 获取数据库连接
    connection = get_db_connection()
    if not connection:
        logger.error("无法连接到数据库，程序退出")
        return

    try:
        # 清除现有菜单
        if not clear_existing_menus(connection):
            logger.error("清除现有菜单失败，程序退出")
            return

        # 插入新菜单
        if insert_supervision_menus(connection):
            logger.info("民政监管菜单插入成功完成！")
        else:
            logger.error("民政监管菜单插入失败！")

    finally:
        connection.close()
        logger.info("数据库连接已关闭")

if __name__ == "__main__":
    main()