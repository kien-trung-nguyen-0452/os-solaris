@echo off
echo Running OS Simulation...
cd src
if not exist *.class (
    echo Classes not found. Please compile first using compile.bat
    cd ..
    pause
    exit /b 1
)
java OSSimulationMain
cd ..
pause

