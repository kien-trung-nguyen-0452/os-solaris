@echo off
echo Compiling Process Scheduling System...
cd src
javac *.java
if %errorlevel% == 0 (
    echo Compilation successful!
    echo.
    echo To run the program, use: java ProcessSchedulingDemo
) else (
    echo Compilation failed!
    cd ..
    pause
    exit /b 1
)
cd ..

