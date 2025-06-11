#!/bin/bash

# Compiler l'application
./compile.sh

# Vérifier si la compilation a réussi
if [ $? -eq 0 ]; then
    echo "Lancement de l'application..."
    # Exécuter l'application
    java -cp bin Main
else
    echo "L'application n'a pas pu être lancée car la compilation a échoué."
    exit 1
fi 