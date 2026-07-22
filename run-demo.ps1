## run-demo.ps1
# Opens two PowerShell windows: one for the backend, one for the frontend dev server.
$root = Split-Path -Parent $MyInvocation.MyCommand.Definition

Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$root'; .\mvnw.cmd spring-boot:run"

Start-Process powershell -ArgumentList "-NoExit","-Command","cd '$root\\frontend'; npm install; npm run dev"

Write-Host "Started backend and frontend windows (check each window for startup logs)."
