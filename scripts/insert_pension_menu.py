#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
养老机构菜单配置脚本
自动连接数据库并插入菜单数据
"""

import pymysql
from datetime import datetime

# 数据库配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'newzijin',
    'charset': 'utf8mb4'
}

def get_connection():
    """获取数据库连接"""
    return pymysql.connect(**DB_CONFIG)

def execute_menu_sql():
    """执行菜单配置SQL"""
    conn = None
    cursor = None

    try:
        conn = get_connection()
        cursor = conn.cursor()

        # 1. 先查询sys_menu表结构
        cursor.execute("DESC sys_menu")
        columns = cursor.fetchall()
        print("sys_menu表结构：")
        for col in columns:
            print(f"  {col[0]} - {col[1]}")
        print()

        # 2. 删除旧的养老机构菜单(包括所有子菜单)
        print("删除旧的养老机构菜单...")
        cursor.execute("DELETE FROM sys_menu WHERE menu_id >= 2000 AND menu_id < 2100")
        print(f"删除了 {cursor.rowcount} 条记录\n")

        # 3. 获取当前时间
        now = datetime.now().strftime('%Y-%m-%d %H:%M:%S')

        # 4. 插入菜单数据 (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
        menus = [
            # 一级菜单
            (2000, '养老机构', 0, 2, 'pension', None, None, None, 1, 0, 'M', '0', '0', '', 'peoples', 'admin', now, '', None, '养老机构管理'),

            # 首页
            (2001, '首页', 2000, 1, 'dashboard', 'pension/dashboard/index', None, None, 1, 0, 'C', '0', '0', 'pension:dashboard:view', 'dashboard', 'admin', now, '', None, '养老机构首页'),

            # 机构管理
            (2010, '机构管理', 2000, 2, 'institution', None, None, None, 1, 0, 'M', '0', '0', '', 'office-building', 'admin', now, '', None, '养老机构管理'),
            (2011, '机构入驻申请', 2010, 1, 'apply', 'pension/institution/apply', None, None, 1, 0, 'C', '0', '0', 'pension:institution:apply', 'edit', 'admin', now, '', None, ''),
            (2012, '机构入驻列表', 2010, 2, 'list', 'pension/institution/index', None, None, 1, 0, 'C', '0', '0', 'pension:institution:list', 'list', 'admin', now, '', None, ''),
            (2013, '公示信息维护', 2010, 3, 'publicity', 'pension/institution/publicity', None, None, 1, 0, 'C', '0', '0', 'pension:institution:publicity', 'documentation', 'admin', now, '', None, ''),

            # 床位管理
            (2020, '床位管理', 2000, 3, 'bed', None, None, None, 1, 0, 'M', '0', '0', '', 'house', 'admin', now, '', None, '床位信息管理'),
            (2021, '床位列表', 2020, 1, 'list', 'pension/bed/list', None, None, 1, 0, 'C', '0', '0', 'pension:bed:list', 'list', 'admin', now, '', None, ''),

            # 入住管理
            (2030, '入住管理', 2000, 4, 'elder', None, None, None, 1, 0, 'M', '0', '0', '', 'user', 'admin', now, '', None, '老人入住管理'),
            (2031, '入住人列表', 2030, 1, 'list', 'pension/elder/list', None, None, 1, 0, 'C', '0', '0', 'pension:elder:list', 'list', 'admin', now, '', None, ''),
            (2032, '查增入住', 2030, 2, 'checkin', 'pension/elder/checkin', None, None, 1, 0, 'C', '0', '0', 'pension:elder:checkin', 'edit', 'admin', now, '', None, ''),

            # 订单管理
            (2040, '订单管理', 2000, 5, 'order', None, None, None, 1, 0, 'M', '0', '0', '', 's-order', 'admin', now, '', None, '订单信息管理'),
            (2041, '订单列表', 2040, 1, 'list', 'pension/order/orderInfo/index', None, None, 1, 0, 'C', '0', '0', 'pension:order:list', 'list', 'admin', now, '', None, ''),

            # 押金管理
            (2050, '押金管理', 2000, 6, 'deposit', None, None, None, 1, 0, 'M', '0', '0', '', 'money', 'admin', now, '', None, '押金使用管理'),
            (2051, '押金使用申请', 2050, 1, 'apply', 'pension/deposit/apply', None, None, 1, 0, 'C', '0', '0', 'pension:deposit:apply', 'edit', 'admin', now, '', None, ''),
            (2052, '押金使用列表', 2050, 2, 'list', 'pension/deposit/list', None, None, 1, 0, 'C', '0', '0', 'pension:deposit:list', 'list', 'admin', now, '', None, ''),

            # 资金划拨
            (2060, '资金划拨', 2000, 7, 'fund', None, None, None, 1, 0, 'M', '0', '0', '', 'wallet', 'admin', now, '', None, '资金划拨管理'),
            (2061, '资金划拨申请', 2060, 1, 'apply', 'pension/fund/transfer/index', None, None, 1, 0, 'C', '0', '0', 'pension:fund:apply', 'edit', 'admin', now, '', None, ''),
            (2062, '资金划拨记录', 2060, 2, 'record', 'pension/fund/record/index', None, None, 1, 0, 'C', '0', '0', 'pension:fund:record', 'list', 'admin', now, '', None, ''),

            # 银行对账
            (2070, '银行对账', 2000, 8, 'bank', None, None, None, 1, 0, 'M', '0', '0', '', 'coin', 'admin', now, '', None, '银行流水对账'),
            (2071, '监管账户流水', 2070, 1, 'supervision', 'pension/bank/supervision', None, None, 1, 0, 'C', '0', '0', 'pension:bank:supervision', 'document', 'admin', now, '', None, ''),
            (2072, '收单交易流水', 2070, 2, 'payment', 'pension/bank/payment', None, None, 1, 0, 'C', '0', '0', 'pension:bank:payment', 'document', 'admin', now, '', None, ''),

            # 公告查询
            (2080, '公告查询', 2000, 9, 'announcement', 'pension/announcement/index', None, None, 1, 0, 'C', '0', '0', 'pension:announcement:view', 'bell', 'admin', now, '', None, '查看公告通知'),

            # 投诉建议
            (2090, '投诉建议', 2000, 10, 'feedback', 'pension/feedback/index', None, None, 1, 0, 'C', '0', '0', 'pension:feedback:view', 'message', 'admin', now, '', None, '投诉建议管理'),
        ]

        # 插入菜单
        sql = """
        INSERT INTO sys_menu
        (menu_id, menu_name, parent_id, order_num, path, component, query, route_name, is_frame, is_cache,
         menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
        VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)
        """

        success_count = 0
        for menu in menus:
            try:
                cursor.execute(sql, menu)
                success_count += 1
                print(f"[OK] 插入菜单: {menu[1]}")
            except Exception as e:
                print(f"[FAIL] 插入菜单失败 {menu[1]}: {e}")

        # 提交事务
        conn.commit()
        print(f"\n成功插入 {success_count}/{len(menus)} 条菜单记录！")

        # 查询验证
        cursor.execute("SELECT menu_id, menu_name, parent_id FROM sys_menu WHERE menu_id >= 2000 AND menu_id < 2100 ORDER BY menu_id")
        results = cursor.fetchall()
        print("\n已插入的菜单：")
        for row in results:
            indent = "  " if row[2] != 0 else ""
            print(f"{indent}{row[0]}: {row[1]} (parent: {row[2]})")

        print("\n[SUCCESS] 养老机构菜单配置完成！")
        print("请重启后端服务并登录系统查看菜单效果。")

    except pymysql.Error as e:
        print(f"[ERROR] 数据库错误: {e}")
        if conn:
            conn.rollback()
    except Exception as e:
        print(f"[ERROR] 执行失败: {e}")
        if conn:
            conn.rollback()
    finally:
        if cursor:
            cursor.close()
        if conn:
            conn.close()

if __name__ == '__main__':
    print("=" * 60)
    print("养老机构菜单配置脚本")
    print("=" * 60)
    print()

    try:
        execute_menu_sql()
    except ModuleNotFoundError:
        print("[ERROR] 缺少pymysql模块，请先安装：")
        print("   pip install pymysql")
