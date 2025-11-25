@echo off
echo ========================================
echo Blockchain Supply Chain - Startup
echo ========================================
echo.

echo Step 1: Initializing Database...
call mvn compile exec:java "-Dexec.mainClass=com.supplychain.util.InitializeDatabase"

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Database initialization failed!
    pause
    exit /b 1
)

echo.
echo ========================================
echo Database initialized successfully!
echo ========================================
echo.
echo Step 2: Launching GUI Application...
echo.
echo Login Credentials:
echo   Manager:  admin / admin123
echo   Supplier: supplier1 / pass123
echo   Retailer: retailer1 / pass123
echo.
echo ========================================
echo.

call mvn compile exec:java "-Dexec.mainClass=com.supplychain.Main"

pause
