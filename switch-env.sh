#!/bin/bash
# 环境切换脚本：在本地开发环境和生产环境之间一键切换
# 用法：./switch-env.sh local   （切换到本地开发环境）
#      ./switch-env.sh prod    （切换到生产环境）

ENV=$1

if [ "$ENV" != "local" ] && [ "$ENV" != "prod" ]; then
    echo ""
    echo "用法: ./switch-env.sh [local|prod]"
    echo ""
    echo "  local  切换到本地开发环境"
    echo "  prod   切换到生产环境"
    echo ""
    exit 1
fi

# 获取脚本所在目录（即项目根目录），支持从任意位置调用
BASE="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

python3 - "$ENV" "$BASE" <<'PYTHON'
import sys, re, os

env = sys.argv[1]
base = sys.argv[2]

def toggle(rel_path, patterns):
    full_path = os.path.join(base, rel_path)
    with open(full_path, 'r', encoding='utf-8') as f:
        content = f.read()
    for pattern, replacement in patterns:
        content = re.sub(pattern, replacement, content, flags=re.MULTILINE)
    with open(full_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"  ✓ {rel_path}")

label = "本地开发" if env == 'local' else "生产"
print(f"\n正在切换到【{label}】环境...\n")

# ── 1. application-druid.yml（数据库连接）────────────────────────────────────
druid = "ruoyi-admin/src/main/resources/application-druid.yml"
if env == 'local':
    toggle(druid, [
        # 取消注释：本地 DB
        (r'^([ \t]+)# (url: jdbc:mysql://localhost)',  r'\1\2'),
        (r'^([ \t]+)# (username: root)',               r'\1\2'),
        (r'^([ \t]+)# (password: 123456789w)',         r'\1\2'),
        # 注释掉：生产 DB
        (r'^([ \t]+)(url: jdbc:mysql://106\.55)',      r'\1# \2'),
        (r'^([ \t]+)(username: newzijinuser)',          r'\1# \2'),
        (r'^([ \t]+)(password: cbs86)',                 r'\1# \2'),
    ])
else:
    toggle(druid, [
        # 注释掉：本地 DB
        (r'^([ \t]+)(url: jdbc:mysql://localhost)',    r'\1# \2'),
        (r'^([ \t]+)(username: root)',                  r'\1# \2'),
        (r'^([ \t]+)(password: 123456789w)',            r'\1# \2'),
        # 取消注释：生产 DB
        (r'^([ \t]+)# (url: jdbc:mysql://106\.55)',    r'\1\2'),
        (r'^([ \t]+)# (username: newzijinuser)',        r'\1\2'),
        (r'^([ \t]+)# (password: cbs86)',               r'\1\2'),
    ])

# ── 2. application.yml（文件上传路径）────────────────────────────────────────
app_yml = "ruoyi-admin/src/main/resources/application.yml"
if env == 'local':
    toggle(app_yml, [
        # 注释掉：生产 profile
        (r'^([ \t]+)(profile: /home/zijin/uploadPath)',
         r'\1# \2'),
        # 取消注释：本地 profile
        (r'^([ \t]+)# (profile: /Users/wangwen/Desktop/supervison/uploadPath)',
         r'\1\2'),
    ])
else:
    toggle(app_yml, [
        # 注释掉：本地 profile
        (r'^([ \t]+)(profile: /Users/wangwen/Desktop/supervison/uploadPath)',
         r'\1# \2'),
        # 取消注释：生产 profile
        (r'^([ \t]+)# (profile: /home/zijin/uploadPath)',
         r'\1\2'),
    ])

# ── 3. logback.xml（日志存放路径）────────────────────────────────────────────
logback = "ruoyi-admin/src/main/resources/logback.xml"
if env == 'local':
    toggle(logback, [
        # 注释掉：生产日志路径
        (r'^([ \t]+)(<property name="log\.path" value="/home/ruoyi/logs" />)',
         r'\1<!-- \2 -->'),
        # 取消注释：本地日志路径
        (r'^([ \t]+)<!-- (<property name="log\.path" value="/Users/wangwen[^"]*" />) -->',
         r'\1\2'),
    ])
else:
    toggle(logback, [
        # 注释掉：本地日志路径
        (r'^([ \t]+)(<property name="log\.path" value="/Users/wangwen[^"]*" />)',
         r'\1<!-- \2 -->'),
        # 取消注释：生产日志路径
        (r'^([ \t]+)<!-- (<property name="log\.path" value="/home/ruoyi/logs" />) -->',
         r'\1\2'),
    ])

# ── 4. ruoyi-ui/vue.config.js（前端接口地址）─────────────────────────────────
vue_cfg = "ruoyi-ui/vue.config.js"
if env == 'local':
    toggle(vue_cfg, [
        # 注释掉：生产 baseUrl
        (r"^(const baseUrl = 'http://jg\.dayushaiwang\.com/' // 后端接口)",
         r'// \1'),
        # 取消注释：本地 baseUrl
        (r"^// (const baseUrl = 'http://localhost:8080/' // 后端接口)",
         r'\1'),
    ])
else:
    toggle(vue_cfg, [
        # 注释掉：本地 baseUrl
        (r"^(const baseUrl = 'http://localhost:8080/' // 后端接口)",
         r'// \1'),
        # 取消注释：生产 baseUrl
        (r"^// (const baseUrl = 'http://jg\.dayushaiwang\.com/' // 后端接口)",
         r'\1'),
    ])

print(f"\n✅ 已成功切换到【{label}】环境！\n")
if env == 'local':
    print("  数据库: localhost:3306/newzijin")
    print("  上传路径: /Users/wangwen/Desktop/supervison/uploadPath")
    print("  日志路径: /Users/wangwen/Desktop/supervison/uploadPath-1/ruoyilog")
    print("  前端代理: http://localhost:8080/")
else:
    print("  数据库: 106.55.180.188:3306/newzijin")
    print("  上传路径: /home/zijin/uploadPath")
    print("  日志路径: /home/ruoyi/logs")
    print("  前端代理: http://jg.dayushaiwang.com/")
print()
PYTHON
