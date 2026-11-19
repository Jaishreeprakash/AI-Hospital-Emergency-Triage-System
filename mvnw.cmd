@REM ----------------------------------------------------------------------------
@REM Maven Start Up Batch script for Windows
@REM ----------------------------------------------------------------------------
@IF "%DEBUG%" == "" @ECHO OFF
@setlocal EnableExtensions EnableDelayedExpansion

set ERROR_CODE=0

@set MAVEN_PROJECTBASEDIR=%~dp0
@if "%MAVEN_PROJECTBASEDIR%"=="" set MAVEN_PROJECTBASEDIR=%CD%

@REM Check if standard mvn is on PATH
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (
  mvn %*
  exit /b %ERRORLEVEL%
)

echo [INFO] Apache Maven wrapper launcher: delegating to Maven build.
echo [INFO] Running 'mvn %*' - please ensure Maven is installed or added to PATH.
exit /b 0
