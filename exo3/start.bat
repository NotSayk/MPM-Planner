@echo off
setlocal

REM Check for Mac OS (Unix-like)
if "%OS%"=="Windows_NT" (
    REM Windows commands
    javac @compile.list -d bin >nul 2>&1
    copy listeTache.txt bin/listeTache.txt >nul 2>&1
    cd bin >nul 2>&1
    java src.Controleur
    cd .. >nul 2>&1
) else (
    REM Mac/Unix commands
    javac @compile.list -d bin >/dev/null 2>&1
    cp listeTache.txt bin/listeTache.txt >/dev/null 2>&1
    cd bin >/dev/null 2>&1
    java src.Controleur
    cd .. >/dev/null 2>&1
)

pause
endlocal