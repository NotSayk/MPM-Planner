@echo off
setlocal
echo Compilation et execution pour Windows...
javac @compile.list -d bin >nul 2>&1
if errorlevel 1 (
    echo Erreur de compilation!
    pause
    exit /b 1
)
cd bin
java src.Controleur
cd ..
pause
endlocal