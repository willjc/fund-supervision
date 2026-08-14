#!/usr/bin/env bash
set -euo pipefail

BASE=/opt/fund-supervision
SRC="$BASE/src"
APP="$BASE/app"

export GIT_TERMINAL_PROMPT=0

cd "$SRC"
git fetch origin master
git reset --hard FETCH_HEAD

mvn -q install:install-file -Dfile=ruoyi-admin/src/main/webapp/WEB-INF/lib/szzz-open-gateway-sdk-1.0-SNAPSHOT.jar -DpomFile=META-INF/maven/com.digital.szzz/szzz-open-gateway-sdk/pom.xml
mvn -q clean package -pl ruoyi-admin -am -DskipTests

cp ruoyi-admin/target/ruoyi-admin.jar "$APP/ruoyi-admin.jar"

bash "$BASE/deploy.sh"
