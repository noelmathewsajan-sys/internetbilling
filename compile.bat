@echo off
title Compile Internet Billing Management System
setlocal enabledelayedexpansion
echo ===================================================
echo Compiling Internet Billing Management System sources...
echo ===================================================

if not exist "build\classes" mkdir "build\classes"
if not exist "dist\lib" mkdir "dist\lib"
copy /Y "lib\*.jar" "dist\lib\" >nul

javac -encoding UTF-8 -cp "lib\*" -d "build\classes" src\internetbilling\*.java src\internetbilling\dao\*.java src\internetbilling\database\*.java src\internetbilling\gui\*.java src\internetbilling\model\*.java src\internetbilling\service\*.java src\internetbilling\test\*.java

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ===================================================
    echo Compilation FAILED! Please check errors above.
    echo ===================================================
    pause
    exit /b 1
)

echo Compilation successful!

:: Locate jar.exe
set "JAR_CMD=jar"
where jar >nul 2>nul
if errorlevel 1 (
    set "JAR_CMD="
    if defined JAVA_HOME (
        if exist "!JAVA_HOME!\bin\jar.exe" set "JAR_CMD=!JAVA_HOME!\bin\jar.exe"
    )
    if not defined JAR_CMD (
        for /d %%D in ("C:\Program Files\Java"\jdk*) do (
            if exist "%%D\bin\jar.exe" set "JAR_CMD=%%D\bin\jar.exe"
        )
    )
)
if not defined JAR_CMD set "JAR_CMD=jar"

echo Packaging JAR using: !JAR_CMD!
"!JAR_CMD!" cfe "dist\InternetBillingManagementSystem.jar" internetbilling.Main -C "build\classes" .
if not errorlevel 1 (
    echo Packaged into dist\InternetBillingManagementSystem.jar
) else (
    echo Note: JAR packaging skipped or jar command unavailable.
    echo Classes are compiled and ready to run from build\classes.
)

echo ===================================================
echo Done! You can run the application with run.bat
echo ===================================================
pause
