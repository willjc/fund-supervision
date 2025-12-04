import pandas as pd
import json

def process_quxian_excel(file_path, output_path):
    """
    读取郑州市区县的街道数据Excel文件，转换为JSON格式

    Args:
        file_path: Excel文件路径
        output_path: 输出JSON文件路径
    """
    try:
        # 读取Excel文件
        df = pd.read_excel(file_path)

        # 初始化结果字典
        result = {}

        # 遍历每一列
        for column in df.columns:
            # 获取列名（区名）
            column_data = df[column].dropna()  # 去除空值
            if len(column_data) > 0:
                qu_name = str(column_data.iloc[0]).strip()  # 第一个单元格是区名
                jiedao_names = []

                # 剩余的单元格是街道名
                for i in range(1, len(column_data)):
                    jiedao_name = str(column_data.iloc[i]).strip()
                    if jiedao_name and jiedao_name != 'nan':  # ���保不是空值
                        jiedao_names.append(jiedao_name)

                if jiedao_names:  # 只有当街道列表不为空时才添加
                    result[qu_name] = jiedao_names

        # 保存为JSON文件
        with open(output_path, 'w', encoding='utf-8') as f:
            json.dump(result, f, ensure_ascii=False, indent=2)

        print(f"数据处理完成！")
        print(f"共处理了 {len(result)} 个区")
        for qu, jiedaos in result.items():
            print(f"  {qu}: {len(jiedaos)} 个街道")
        print(f"结果已保存到: {output_path}")

        return result

    except Exception as e:
        print(f"处理过程中出现错误: {e}")
        return None

if __name__ == "__main__":
    # 文件路径
    input_file = r"D:\lasthm\zijin\fund-supervision\quxian.xlsx"
    output_file = r"D:\lasthm\zijin\fund-supervision\quxian_data.json"

    # 处理数据
    result = process_quxian_excel(input_file, output_file)

    if result:
        print("\n转换结果预览:")
        print(json.dumps(result, ensure_ascii=False, indent=2))