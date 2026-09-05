#!/usr/bin/env bash
# 独立本地 mysqld + 合成库；不读取项目 .env，不访问线上/现有 MySQL 实例。
set -euo pipefail
test_mysql_bin="${BANK_TEST_MYSQL_BIN:-/usr/local/mysql/bin}"
test_port="${BANK_TEST_PORT:-33498}"
test_dir="$(mktemp -d /tmp/bank-payout-test.XXXXXX)"
test_socket="$test_dir/mysql.sock"
test_pid=""
finish() {
  if [[ -S "$test_socket" ]]; then "$test_mysql_bin/mysqladmin" --no-defaults --socket="$test_socket" -uroot shutdown >/dev/null 2>&1 || true; fi
  if [[ -n "$test_pid" ]]; then wait "$test_pid" || true; fi
  printf 'Isolated test artifacts: %s\n' "$test_dir"
}
trap finish EXIT
"$test_mysql_bin/mysqld" --no-defaults --initialize-insecure --datadir="$test_dir/data" --log-error="$test_dir/init.log"
"$test_mysql_bin/mysqld" --no-defaults --datadir="$test_dir/data" --socket="$test_socket" \
  --port="$test_port" --bind-address=127.0.0.1 --mysqlx=OFF --pid-file="$test_dir/mysql.pid" --log-error="$test_dir/server.log" &
test_pid=$!
for attempt in {1..60}; do
  if "$test_mysql_bin/mysqladmin" --no-defaults --socket="$test_socket" -uroot ping >/dev/null 2>&1; then break; fi
  if ! kill -0 "$test_pid" 2>/dev/null; then printf 'Test mysqld failed; inspect %s\n' "$test_dir/server.log"; exit 1; fi
  sleep 1
done
"$test_mysql_bin/mysql" --no-defaults --socket="$test_socket" -uroot < ruoyi-admin/src/test/resources/bank-v2-base.sql
"$test_mysql_bin/mysql" --no-defaults --socket="$test_socket" -uroot bank_payout_test < sql/zzbank_integration_v1.sql
"$test_mysql_bin/mysql" --no-defaults --socket="$test_socket" -uroot bank_payout_test < sql/zzbank_payment_payout_v2.sql
BANK_PAYOUT_TEST_PORT="$test_port" ZZBANK_LIVE_TEST=false mvn test -pl ruoyi-admin -am -o \
  -Dtest=BankSettlementMapperMysqlTest -Dsurefire.failIfNoSpecifiedTests=false
