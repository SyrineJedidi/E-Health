@echo off
REM Delegue au Maven Wrapper de patient-service (meme repertoire parent Backend/)
cd /d "%~dp0"
call "%~dp0..\patient-service\mvnw.cmd" -f "%~dp0pom.xml" %*
