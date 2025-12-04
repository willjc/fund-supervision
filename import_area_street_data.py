#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
郑州市区域街道数据导入工具
将 quxian_data.json 文件中的数据导入到 area_street 表中
"""

import json
import pymysql
from datetime import datetime

def load_json_data(file_path):
    """加载JSON数据"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            return json.load(f)
    except Exception as e:
        print(f"加载JSON文件失败: {e}")
        return None

def generate_area_code(area_name):
    """根据区域名称生成区域代码"""
    # 郑州市标准行政区划代码前缀
    area_code_map = {
        "中原区": "410102",
        "金水区": "410105",
        "二七区": "410103",
        "郑东新区": "410166",
        "高新区": "410171",
        "惠济区": "410108",
        "管城区": "410104",
        "经开区": "410173",
        "上街区": "410106",
        "中牟县": "410122",
        "巩义市": "410181",
        "荥阳市": "410182",
        "新密市": "410183",
        "新郑市": "410184",
        "登封市": "410185",
        "航空港区": "410173"
    }
    return area_code_map.get(area_name, "4101" + str(hash(area_name) % 1000).zfill(3))

def generate_street_code(area_code, street_name):
    """生成街道代码"""
    return area_code + str(hash(street_name) % 100).zfill(2)

def connect_database():
    """连接数据库"""
    try:
        connection = pymysql.connect(
            host='localhost',
            user='root',
            password='123456',
            database='newzijin',
            charset='utf8mb4'
        )
        return connection
    except Exception as e:
        print(f"数据库连接失败: {e}")
        return None

def clear_existing_data(connection):
    """清空现有数据"""
    try:
        with connection.cursor() as cursor:
            cursor.execute("DELETE FROM area_street")
            connection.commit()
            print("已清空现有区域街道数据")
    except Exception as e:
        print(f"清空数据失败: {e}")
        connection.rollback()

def insert_area_street_data(connection, area_street_data):
    """插入区域街道数据"""
    try:
        with connection.cursor() as cursor:
            insert_count = 0

            for area_name, streets in area_street_data.items():
                area_code = generate_area_code(area_name)

                # 插入区域记录（不包含街道信息）
                sql = """INSERT INTO area_street
                         (area_code, area_name, street_code, street_name, sort_order, status,
                          create_by, create_time, update_by, update_time)
                         VALUES (%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)"""

                # 插入区域记录
                cursor.execute(sql, (
                    area_code, area_name, None, None, 1, '0',
                    'system', datetime.now(), 'system', datetime.now()
                ))
                insert_count += 1

                # 插入街道记录
                for i, street_name in enumerate(streets, 1):
                    if street_name.strip():  # 确保街道名称不为空
                        street_code = generate_street_code(area_code, street_name)
                        cursor.execute(sql, (
                            area_code, area_name, street_code, street_name, i, '0',
                            'system', datetime.now(), 'system', datetime.now()
                        ))
                        insert_count += 1

            connection.commit()
            print(f"成功导入 {insert_count} 条区域街道数据")
            return insert_count

    except Exception as e:
        print(f"插入数据失败: {e}")
        connection.rollback()
        return 0

def main():
    """主函数"""
    print("开始导入郑州市区域街道数据...")

    # 加载JSON数据
    json_file = "quxian_data.json"
    area_street_data = load_json_data(json_file)
    if not area_street_data:
        return

    print(f"加载JSON数据成功，包含 {len(area_street_data)} 个区县")

    # 连接数据库
    connection = connect_database()
    if not connection:
        return

    try:
        # 清空现有数据
        clear_existing_data(connection)

        # 插入新数据
        insert_count = insert_area_street_data(connection, area_street_data)

        if insert_count > 0:
            print("数据导入完成！")

            # 验证导入结果
            with connection.cursor() as cursor:
                cursor.execute("SELECT COUNT(*) FROM area_street WHERE status = '0'")
                total_count = cursor.fetchone()[0]
                print(f"数据库中共有 {total_count} 条有效区域街道记录")

                # 显示各区县统计
                cursor.execute("""
                    SELECT area_name, COUNT(*) as count
                    FROM area_street
                    WHERE street_name IS NOT NULL AND status = '0'
                    GROUP BY area_name
                    ORDER BY area_name
                """)
                results = cursor.fetchall()
                print("\n各区县街道数量统计:")
                for area_name, count in results:
                    print(f"  {area_name}: {count}个街道")
        else:
            print("数据导入失败！")

    finally:
        connection.close()
        print("数据库连接已关闭")

if __name__ == "__main__":
    main()