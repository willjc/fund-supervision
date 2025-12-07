#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
SVG Icon Sprite Generator
自动读取SVG文件并生成svgSprite.js文件
"""

import os
import re
import json
from pathlib import Path

def read_svg_file(file_path):
    """读取SVG文件内容"""
    try:
        with open(file_path, 'r', encoding='utf-8') as f:
            content = f.read().strip()
        return content
    except Exception as e:
        print(f"读取文件 {file_path} 时出错: {e}")
        return None

def extract_svg_content(svg_content):
    """从SVG内容中提取关键部分"""
    if not svg_content:
        return None

    # 尝试匹配SVG标签内的内容
    match = re.search(r'<svg[^>]*>(.*?)</svg>', svg_content, re.DOTALL)
    if match:
        return match.group(1)

    # 如果没有匹配到，返回原始内容
    return svg_content

def get_view_box(svg_content):
    """提取SVG的viewBox属性"""
    if not svg_content:
        return "0 0 128 128"  # 默认viewBox

    # 尝试提取viewBox属性
    match = re.search(r'viewBox=["\']([^"\']+)["\']', svg_content)
    if match:
        return match.group(1)

    # 尝试提取width和height属性
    width_match = re.search(r'width=["\']([^"\']+)["\']', svg_content)
    height_match = re.search(r'height=["\']([^"\']+)["\']', svg_content)

    if width_match and height_match:
        width = width_match.group(1)
        height = height_match.group(1)
        # 移除单位（如px）
        width = re.sub(r'[a-zA-Z]+', '', width)
        height = re.sub(r'[a-zA-Z]+', '', height)
        return f"0 0 {width} {height}"

    return "0 0 128 128"  # 默认viewBox

def escape_js_string(content):
    """转义JavaScript字符串中的特殊字符"""
    if not content:
        return ""

    # 只转义必要的字符
    content = content.replace('\\', '\\\\')
    content = content.replace("'", "\\'")
    content = content.replace('\n', '')
    content = content.replace('\r', '')

    return content

def generate_svg_sprite_js(svg_dir, output_file):
    """生成svgSprite.js文件"""
    svg_path = Path(svg_dir)

    if not svg_path.exists():
        print(f"SVG目录不存在: {svg_dir}")
        return False

    # 获取所有SVG文件
    svg_files = list(svg_path.glob("*.svg"))

    if not svg_files:
        print(f"在目录 {svg_dir} 中没有找到SVG文件")
        return False

    print(f"找到 {len(svg_files)} 个SVG文件")

    # 生成图标映射
    icons = {}

    for svg_file in svg_files:
        icon_name = svg_file.stem  # 文件名（不包含扩展名）
        svg_content = read_svg_file(svg_file)

        if svg_content:
            # 使用完整的SVG内容而不是仅仅提取内容
            # 这样可以保留所有的属性和样式
            icons[icon_name] = escape_js_string(svg_content)
            print(f"处理图标: {icon_name}")

    # 生成JavaScript文件内容
    js_content = f"""// H5 SVG Icon Sprite Loader
// 自动生成 - 包含所有SVG图���
// 生成时间: 2025-12-07

// 存储所有图标的映射
let svgIcons = {{}};

// 自动加载所有SVG图标
function initSvgIcons() {{
  svgIcons = {{
"""

    # 添加所有图标
    for icon_name, svg_content in icons.items():
        js_content += f'    \'{icon_name}\': `{svg_content}`,\n'

    js_content += f"""  }};

  console.log(`Initialized ${{Object.keys(svgIcons).length}} SVG icons:`, Object.keys(svgIcons));
}}

// 创建SVG sprite
function createSvgSprite() {{
  const symbols = Object.entries(svgIcons).map(([id, svg]) => {{
    // 提取SVG内容并转换为symbol
    const match = svg.match(/<svg[^>]*>([\\s\\S]*?)<\\/svg>/);
    if (match) {{
      // 尝试提取原始viewBox
      let viewBox = '0 0 128 128'; // 默认viewBox
      const viewBoxMatch = svg.match(/viewBox="([^"]+)"/);
      if (viewBoxMatch) {{
        viewBox = viewBoxMatch[1];
      }}

      const content = match[1];
      return `<symbol id="icon-${{id}}" viewBox="${{viewBox}}">${{content}}</symbol>`;
    }}
    return '';
  }}).join('\\n  ');

  return `
<svg style="position: absolute; width: 0; height: 0; overflow: hidden;" version="1.1" xmlns="http://www.w3.org/2000/svg">
  <defs>
    ${{symbols}}
  </defs>
</svg>`;
}}

// 初始化SVG sprite
function initSvgSprite() {{
  // 检查是否已经存在sprite
  if (document.getElementById('svg-sprite')) {{
    return;
  }}

  // 初始化图标数据
  initSvgIcons();

  // 创建sprite
  const sprite = createSvgSprite();
  const div = document.createElement('div');
  div.innerHTML = sprite;
  div.id = 'svg-sprite';
  div.style.display = 'none';
  document.body.insertBefore(div, document.body.firstChild);

  console.log('SVG Sprite initialized with icons:', Object.keys(svgIcons));
}}

// 检查图标是否存在的函数
function hasIcon(iconName) {{
  return Object.prototype.hasOwnProperty.call(svgIcons, iconName);
}}

// 获取所有可用图标
function getAvailableIcons() {{
  return Object.keys(svgIcons);
}}

// 导出函数
export {{
  initSvgSprite,
  hasIcon,
  getAvailableIcons,
  svgIcons // 导出图标映射，用于调试
}};
"""

    # 写入输出文件
    try:
        with open(output_file, 'w', encoding='utf-8') as f:
            f.write(js_content)
        print(f"\\n成功生成文件: {output_file}")
        print(f"包含 {len(icons)} 个图标")
        return True
    except Exception as e:
        print(f"写入文件时出错: {e}")
        return False

def main():
    """主函数"""
    # 当前目录
    current_dir = os.path.dirname(os.path.abspath(__file__))

    # SVG文件目录
    svg_dir = os.path.join(current_dir, "src", "assets", "icons", "svg")

    # 输出文件
    output_file = os.path.join(current_dir, "src", "utils", "svgSprite.js")

    print("=== SVG Icon Sprite Generator ===")
    print(f"SVG目录: {svg_dir}")
    print(f"输出文件: {output_file}")
    print()

    # 生成文件
    success = generate_svg_sprite_js(svg_dir, output_file)

    if success:
        print("\\n✅ svgSprite.js 生成成功！")
        print("\\n现在所有SVG图标都已被包含在sprite中，包括：")
        print("- code (洗衣服务)")
        print("- enter (独立卫浴)")
        print("- 以及所有其他图标...")
    else:
        print("\\n❌ 生成失败，请检查错误信息")

if __name__ == "__main__":
    main()