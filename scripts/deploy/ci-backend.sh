#!/usr/bin/env bash
set -euo pipefail

BASE=/opt/fund-supervision
SRC="$BASE/src"
APP="$BASE/app"
JAR="$SRC/ruoyi-admin/target/ruoyi-admin.jar"
FIX_DIR="$SRC/ruoyi-admin/target/logback-fix/BOOT-INF/classes"

export GIT_TERMINAL_PROMPT=0

cd "$SRC"
git fetch origin master
git reset --hard FETCH_HEAD

mvn -q install:install-file -Dfile=ruoyi-admin/src/main/webapp/WEB-INF/lib/szzz-open-gateway-sdk-1.0-SNAPSHOT.jar -DpomFile=META-INF/maven/com.digital.szzz/szzz-open-gateway-sdk/pom.xml
mvn -q clean package -pl ruoyi-admin -am -DskipTests

mkdir -p "$FIX_DIR"
cp "$BASE/logback.xml" "$FIX_DIR/logback.xml"
jar uf "$JAR" -C "$SRC/ruoyi-admin/target/logback-fix" BOOT-INF/classes/logback.xml
rm -rf "$SRC/ruoyi-admin/target/logback-fix"

cp "$JAR" "$APP/ruoyi-admin.jar"

bash "$BASE/deploy.sh"
