#!/usr/bin/env bash
set -euo pipefail

BASE=/opt/fund-supervision
JAR="$BASE/app/ruoyi-admin.jar"
TS=$(date +%Y%m%d%H%M%S)

mkdir -p "$BASE/releases" "$BASE/logs"

if [ -f "$JAR" ]; then
    cp "$JAR" "$BASE/releases/ruoyi-admin.$TS.jar"
fi

sudo systemctl restart fund-supervision

for i in $(seq 1 30); do
    if curl -fsS http://127.0.0.1:8080/captchaImage >/dev/null 2>&1; then
        echo "deploy ok"
        exit 0
    fi
    sleep 2
done

echo "backend health check failed" >&2
exit 1
