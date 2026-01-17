@echo off
echo ========================================
echo   OS Simulation - Compile and Run
echo ========================================
echo.

echo Compiling...
cd src
javac *.java
if %errorlevel% neq 0 (
    echo Compilation failed!
    cd ..
    pause
    exit /b 1
)

echo Compilation successful!
echo.
echo Running simulation...
echo.
java OSSimulationMain

cd ..
echo.
echo Simulation completed!
pause

