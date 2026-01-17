@echo off
echo Running Process Scheduling System...
cd src
if not exist *.class (
    echo Classes not found. Please compile first using compile.bat
    cd ..
    pause
    exit /b 1
)
java ProcessSchedulingDemo
cd ..
pause

