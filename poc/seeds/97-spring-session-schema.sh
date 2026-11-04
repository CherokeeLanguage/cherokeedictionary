#!/bin/bash
# The spring-session JDBC store's DDL (#130) — applied from the CANONICAL
# committed file (sql/spring-session-schema-mysql.sql, mounted by compose at
# /spring-session-schema.sql) at container init, idempotently.
#
# Why this exists (cherokeedictionary#133 audit, 2026-11-04): the operator
# prod-dump seed document predates #130 and carries no SPRING_SESSION DDL —
# a dump-mode stack booted without it 500s on EVERY request (the session
# store's response-commit INSERT fails; the site is down). The fixture
# document inlines the same DDL at generation time; this script no-ops
# there (table already exists).
set -e

: "${MARIADB_DATABASE:?MARIADB_DATABASE not set}"
: "${MARIADB_ROOT_PASSWORD:?MARIADB_ROOT_PASSWORD not set}"
SCHEMA=/spring-session-schema.sql
[ -f "$SCHEMA" ] || { echo "97-spring-session: $SCHEMA missing (compose mount gone?) — skipping"; exit 0; }

EXISTS=$(mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" -N -e \
  "SELECT COUNT(*) FROM information_schema.tables \
   WHERE table_schema='$MARIADB_DATABASE' AND table_name='SPRING_SESSION'")
if [ "$EXISTS" != "0" ]; then
  echo "97-spring-session: SPRING_SESSION already present — no-op"
  exit 0
fi

echo "97-spring-session: applying $SCHEMA to $MARIADB_DATABASE"
mariadb -uroot -p"$MARIADB_ROOT_PASSWORD" "$MARIADB_DATABASE" < "$SCHEMA"
