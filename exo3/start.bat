@echo off
javac @compile.list -d bin >nul 2>&1
cp src/liseTache.txt bin >nul 2>&1
cd bin >nul 2>&1
java src.Controleur
cd .. >nul 2>&1
pause