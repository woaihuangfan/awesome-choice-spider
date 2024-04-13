powershell -Command "Invoke-WebRequest -Uri 'http://localhost:8080/ops/shutdown'"  > nul

timeout /t 3 > nul