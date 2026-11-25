@echo off
REM ============================================================
REM  Starts the dictionary app (including the /docrepo document
REM  repository) in a console window you control.
REM
REM  Keep this window open while using the app - closing it stops
REM  the app. Stop with Ctrl+C or by closing the window.
REM
REM  URL:      http://localhost:8080/docrepo
REM  Dev login: admin / admin123  (bootstrap default, dev only -
REM            see DocRepoAuthService.ensureBootstrapAdmin)
REM
REM  Requires local MySQL running (dev datasource: localhost:3306,
REM  db cherokeedictionary - see grails-app/conf/application.yml).
REM  If the port is already taken ("Address already in use"),
REM  an instance is already running - just use it.
REM ============================================================
cd /d "%~dp0"
call gradlew.bat bootRun --no-daemon
pause
