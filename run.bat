@echo off
REM Script pour compiler et exécuter l'application Campus Room Manager

setlocal enabledelayedexpansion

REM Vérifier si Maven est installé
where mvn >nul 2>nul
if %ERRORLEVEL% neq 0 (
    echo [ERREUR] Maven n'est pas installé ou n'est pas dans le PATH
    echo Veuillez installer Maven depuis: https://maven.apache.org/download.cgi
    pause
    exit /b 1
)

echo.
echo ========================================
echo Campus Room Management System
echo ========================================
echo.
echo Compilation du projet...
echo.

call mvn clean compile

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERREUR] La compilation a échoué
    pause
    exit /b 1
)

echo.
echo Lancement de l'application...
echo.

call mvn javafx:run

pause
