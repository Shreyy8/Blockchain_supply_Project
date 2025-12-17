@echo off
echo ================================================================================
echo BLOCKCHAIN SUPPLY CHAIN MANAGEMENT SYSTEM - WEB APPLICATION
echo ================================================================================
echo.

echo Initializing database...
call mvn compile exec:java -Dexec.mainClass=com.supplychain.util.DatabaseSetup -Dexec.cleanupDaemonThreads=false -q

if %ERRORLEVEL% NEQ 0 (
    echo.
    echo ERROR: Database initialization failed!
    echo Please check your MySQL connection and try again.
    pause
    exit /b 1
)

echo.
echo Database initialized successfully!
echo.
echo Starting web application server...
echo.
echo The application will be available at: http://localhost:8080/supply-chain
echo.
echo Press Ctrl+C to stop the server
echo.

mvn jetty:run

pause