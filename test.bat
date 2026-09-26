@echo off
title Test Internet Billing Management System
echo ===================================================
echo Running System Tests (Admin and User Entry Verification)
echo ===================================================
java -cp "dist\InternetBillingManagementSystem.jar;lib\*" internetbilling.test.TestAdminUserEntry
if errorlevel 1 (
    echo.
    echo Running from build\classes...
    java -cp "build\classes;lib\*" internetbilling.test.TestAdminUserEntry
)
echo.
pause
