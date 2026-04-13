# Build production de l’unique application Angular (back-office /admin + portail /portail).
# Usage : .\build-frontend.ps1
# Prérequis : Node.js + npm install dans Frontend/

$ErrorActionPreference = "Stop"
Set-Location (Join-Path $PSScriptRoot "Frontend")

if (-not (Test-Path "node_modules")) {
    npm install
}
npm run build
Write-Host "Build Frontend OK -> Frontend\dist\frontend" -ForegroundColor Green
