@echo off
chcp 65001 >nul
echo ===================================================
echo     Сборка Void Launcher (Murzik Edition)
echo ===================================================
echo.
call gradlew.bat assembleDebug
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ===================================================
    echo [УСПЕХ] APK успешно собран!
    echo Файл находится здесь:
    echo app\build\outputs\apk\debug\app-debug.apk
    echo ===================================================
    explorer app\build\outputs\apk\debug
) else (
    echo.
    echo [ИНФО] Если сборка не удалась, откройте проект через Android Studio.
)
pause
