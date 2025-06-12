#!/bin/bash
echo "Compilation et execution pour Linux..."
javac @compile.list -d bin >/dev/null 2>&1
if [ $? -ne 0 ]; then
    echo "Erreur de compilation!"
    read -p "Appuyez sur Entrée pour continuer..."
    exit 1
fi
cd bin
java src.Controleur
cd ..
echo "Appuyez sur Entrée pour continuer..."
read