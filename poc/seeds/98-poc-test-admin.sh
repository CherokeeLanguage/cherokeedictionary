#!/bin/bash
# Seeded DURING container init as a sourced script (docker-entrypoint-initdb.d
# runs .sh with the container env), so the test password can interpolate from
# the operator-held .env via compose — no credential literal in the repo.
#
# The sanctioned test account (cherokeedictionary#133): the regression suite's
# ONLY credential surface, local test containers only (PoC, loopback-published
# MariaDB). AdminController.login compares the password in PLAINTEXT
# (Admin.findAll "... password=?1", AdminController.groovy:120), so a seeded
# row is all it takes. Idempotent: no-op when the account already exists.
set -e

: "${CED_TEST_ADMIN_USER:?CED_TEST_ADMIN_USER not set (operator .env)}"
: "${CED_TEST_ADMIN_PASSWORD:?CED_TEST_ADMIN_PASSWORD not set (operator .env)}"

user_escaped="${CED_TEST_ADMIN_USER//\'/''}"
pw_escaped="${CED_TEST_ADMIN_PASSWORD//\'/''}"

cat > /tmp/98-test-admin.sql <<SQL
INSERT INTO admin (version, username, password)
  SELECT 0, '$user_escaped', '$pw_escaped' FROM DUAL
  WHERE NOT EXISTS (SELECT 1 FROM admin WHERE username = '$user_escaped');
SQL

mysql -uroot -p"$MARIADB_ROOT_PASSWORD" "$MARIADB_DATABASE" < /tmp/98-test-admin.sql
rm -f /tmp/98-test-admin.sql
