#!/usr/bin/env python3
# -*- coding: utf-8 -*-

import pymysql
from datetime import datetime

# 数据库连接配置
DB_CONFIG = {
    'host': 'localhost',
    'port': 3306,
    'user': 'root',
    'password': '123456',
    'database': 'newzijin',
    'charset': 'utf8mb4'
}

def check_menu_table_structure():
    """检查sys_menu表结构"""
    try:
        connection = pymysql.connect(**DB_CONFIG)
        with connection.cursor() as cursor:
            # 查询表结构
            cursor.execute("DESCRIBE sys_menu")
            columns = cursor.fetchall()

            print("sys_menu表结构:")
            for column in columns:
                print(f"  {column[0]} - {column[1]} - {column[2]} - {column[3]} - {column[4]} - {column[5]}")

            # 查询一条现有记录作为参考
            cursor.execute("SELECT * FROM sys_menu WHERE menu_id = 1 LIMIT 1")
            existing = cursor.fetchone()
            if existing:
                print(f"\n现有记录字段数: {len(existing)}")
                print(f"现有记录值: {existing}")

            connection.close()
            return columns

    except Exception as e:
        print(f"错误: {e}")
        return None

if __name__ == "__main__":
    check_menu_table_structure()