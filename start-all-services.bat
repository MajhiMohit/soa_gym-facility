@echo off
echo ============================================================
echo   PulseFit SOA Backend - Service Startup Script
echo ============================================================
echo.
echo Starting all microservices in order...
echo.

echo [1/6] Starting Eureka Server (port 8761)...
start "Eureka Server" cmd /k "cd /d d:\soa project\eureka-server && mvn spring-boot:run"
timeout /t 20 /nobreak >nul

echo [2/6] Starting Auth Service (port 8083)...
start "Auth Service" cmd /k "cd /d d:\soa project\auth-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [3/6] Starting Member Service (port 8081)...
start "Member Service" cmd /k "cd /d d:\soa project\member-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [4/6] Starting Subscription Service (port 8082)...
start "Subscription Service" cmd /k "cd /d d:\soa project\subscription-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [5/6] Starting Attendance Service (port 8084)...
start "Attendance Service" cmd /k "cd /d d:\soa project\attendance-service && mvn spring-boot:run"
timeout /t 15 /nobreak >nul

echo [6/6] Starting API Gateway (port 8080)...
start "API Gateway" cmd /k "cd /d d:\soa project\api-gateway && mvn spring-boot:run"
timeout /t 10 /nobreak >nul

echo.
echo ============================================================
echo   All services started! Access points:
echo   - Eureka Dashboard:  http://localhost:8761
echo   - API Gateway:       http://localhost:8080
echo   - Auth Service:      http://localhost:8083
echo   - Member Service:    http://localhost:8081
echo   - Subscription Svc:  http://localhost:8082
echo   - Attendance Svc:    http://localhost:8084
echo ============================================================
pause
