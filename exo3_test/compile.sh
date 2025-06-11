#!/bin/bash

# Créer le dossier bin s'il n'existe pas
mkdir -p bin

# Compiler tous les fichiers Java en respectant la structure des packages
find src -name "*.java" > sources.txt
javac -d bin @sources.txt

# Nettoyer le fichier temporaire
rm sources.txt

echo "Compilation réussie !" 