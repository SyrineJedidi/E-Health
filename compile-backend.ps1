# Compile tous les microservices Backend (Java 17 requis).
# Usage : depuis la racine du depot, en PowerShell :
#   .\compile-backend.ps1
# Ou : powershell -ExecutionPolicy Bypass -File .\compile-backend.ps1

$ErrorActionPreference = "Stop"
$Root = $PSScriptRoot
$Mvnw = Join-Path $Root "Backend\patient-service\mvnw.cmd"

if (-not (Test-Path $Mvnw)) {
    Write-Error "Maven Wrapper introuvable : $Mvnw"
}

$Modules = @(
    "eureka-server",
    "config-server",
    "api-gateway",
    "patient-service",
    "doctor-service",
    "auth-service",
    "prescription-service"
)

foreach ($mod in $Modules) {
    $pom = Join-Path $Root "Backend\$mod\pom.xml"
    if (-not (Test-Path $pom)) {
        Write-Warning "Ignore (pom absent) : $mod"
        continue
    }
    Write-Host "`n=== compile : $mod ===" -ForegroundColor Cyan
    & $Mvnw -f $pom compile -DskipTests
    if ($LASTEXITCODE -ne 0) {
        Write-Error "Echec compilation : $mod"
    }
}

Write-Host "`nCompilation Backend terminee avec succes." -ForegroundColor Green
