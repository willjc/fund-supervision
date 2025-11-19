@echo off
chcp 65001 >nul
echo ========================================
echo   若依后端一键启动脚本
echo ========================================
echo.

echo [检查] 正在检查环境...
echo.

REM 设置Java 1.8环境变量(临时)
set JAVA_HOME=C:\Program Files\Java\jdk-1.8
set PATH=%JAVA_HOME%\bin;%PATH%

echo [Java版本]
java -version
echo.

echo [检查] MySQL服务状态...
net start | findstr MySQL >nul
if %errorlevel% neq 0 (
    echo [警告] MySQL服务未启动!
    echo [提示] 请先启动MySQL: net start MySQL80
    echo.
    pause
    exit /b 1
) else (
    echo [成功] MySQL服务已启动
)
echo.

echo [检查] Redis服务状态...
tasklist | findstr redis >nul
if %errorlevel% neq 0 (
    echo [提示] Redis服务未启动 (可选,建议启动)
) else (
    echo [成功] Redis服务已启动
)
echo.

echo ========================================
echo   开始启动若依后端...
echo ========================================
echo.

cd /d D:\newhm\newzijin
call mvn clean spring-boot:run -pl ruoyi-admin

echo.
echo ========================================
echo   后端已停止
echo ========================================
pause
