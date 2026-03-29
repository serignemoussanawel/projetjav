#!/bin/bash

echo "========================================"
echo "Campus Room Management System"
echo "========================================"
echo ""
echo "Compilation du projet..."
echo ""

mvn clean compile

if [ $? -ne 0 ]; then
    echo ""
    echo "[ERREUR] La compilation a échoué"
    exit 1
fi

echo ""
echo "Lancement de l'application..."
echo ""

mvn javafx:run
