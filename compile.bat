@echo off
echo Compiling OS Simulation Project...
cd src
javac *.java
if %errorlevel% == 0 (
    echo Compilation successful!
    echo.
    echo To run the program, use: java OSSimulationMain
) else (
    echo Compilation failed!
    cd ..
    pause
    exit /b 1
)
cd ..

