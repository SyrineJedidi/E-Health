# Démarre toute la stack eHealth (Backend + Frontend).
# Prérequis : Java 17, Node.js, npm install dans Frontend (fait au 1er npm start).
# Auth-service : MongoDB sur localhost:27017 (Docker utilisé si le port est libre).
# Gateway : port 8085 (aligné avec environment.ts et proxy.conf.js).
#
# Usage (racine du dépôt) :
#   powershell -ExecutionPolicy Bypass -File .\demarrer-projet.ps1

$ErrorActionPreference = "Stop"
$Root = $PSScriptRoot

function Test-PortOpen {
    param([int] $Port)
    try {
        $r = Test-NetConnection -ComputerName 127.0.0.1 -Port $Port -WarningAction SilentlyContinue
        return $r.TcpTestSucceeded
    } catch {
        return $false
    }
}

function Ensure-MongoDocker {
    if (Test-PortOpen -Port 27017) {
        Write-Host "MongoDB : port 27017 déjà ouvert." -ForegroundColor Green
        return
    }
    $docker = Get-Command docker -ErrorAction SilentlyContinue
    if (-not $docker) {
        Write-Warning "MongoDB n'est pas joignable sur 27017 et Docker est introuvable. Démarrez MongoDB manuellement pour l'auth-service."
        return
    }
    Write-Host "Démarrage de MongoDB via Docker (port 27017)..." -ForegroundColor Cyan
    docker start ehealth-mongo 2>$null | Out-Null
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Conteneur ehealth-mongo démarré." -ForegroundColor Green
        return
    }
    docker run -d --name ehealth-mongo -p 27017:27017 mongo:7
    if ($LASTEXITCODE -ne 0) {
        Write-Warning "Impossible de lancer MongoDB (docker run a échoué). Lancez MongoDB à la main."
    } else {
        Start-Sleep -Seconds 2
        Write-Host "Conteneur ehealth-mongo créé et lancé." -ForegroundColor Green
    }
}

function Start-BootRunWindow {
    param(
        [string] $Module,
        [string] $Title,
        [string] $PreCommand = ""
    )
    $dir = Join-Path $Root "Backend\$Module"
    if (-not (Test-Path (Join-Path $dir "mvnw.cmd"))) {
        Write-Warning "Ignoré (mvnw.cmd absent) : $Module"
        return
    }
    $lines = @()
    if ($PreCommand) { $lines += $PreCommand }
    $lines += "Set-Location -LiteralPath '$dir'"
    $lines += "& .\mvnw.cmd spring-boot:run"
    $cmd = $lines -join "; "
    Start-Process powershell -ArgumentList @("-NoExit", "-Command", $cmd) -WorkingDirectory $dir
    Write-Host " Fenêtre : $Title ($Module)" -ForegroundColor Gray
}

Write-Host "`n=== eHealth - demarrage des services ===" -ForegroundColor Cyan
Ensure-MongoDocker

Write-Host "`n1/7 Eureka (8761)..." -ForegroundColor Cyan
Start-BootRunWindow -Module "eureka-server" -Title "Eureka"
Start-Sleep -Seconds 12

Write-Host "2/7 Config server (8888)..." -ForegroundColor Cyan
Start-BootRunWindow -Module "config-server" -Title "Config Server"
Start-Sleep -Seconds 8

Write-Host "3/7 Microservices (8081-8084)..." -ForegroundColor Cyan
Start-BootRunWindow -Module "patient-service" -Title "Patient"
Start-BootRunWindow -Module "doctor-service" -Title "Doctor"
Start-BootRunWindow -Module "auth-service" -Title "Auth"
Start-BootRunWindow -Module "prescription-service" -Title "Prescription"
Start-Sleep -Seconds 18

Write-Host "4/7 API Gateway (8085)..." -ForegroundColor Cyan
Start-BootRunWindow -Module "api-gateway" -Title "Gateway" -PreCommand "`$env:SERVER_PORT='8085'"

Start-Sleep -Seconds 3

$fe = Join-Path $Root "Frontend"
if (-not (Test-Path (Join-Path $fe "package.json"))) {
    Write-Warning "Dossier Frontend introuvable."
} else {
    Write-Host "5/7 Frontend Angular (ng serve)..." -ForegroundColor Cyan
    $feCmd = @"
Set-Location -LiteralPath '$fe'
if (-not (Test-Path 'node_modules')) { npm install }
npm start
"@
    Start-Process powershell -ArgumentList @("-NoExit", "-Command", $feCmd) -WorkingDirectory $fe
}

Write-Host @"

OK : une fenetre PowerShell par service a ete ouverte.
  - Eureka        http://localhost:8761
  - Config        http://localhost:8888
  - API Gateway   http://localhost:8085
  - Frontend      http://localhost:4200  (proxy /api vers la gateway)

Attendez 30 a 60 s que Maven telecharge et que les services s'enregistrent sur Eureka.
"@ -ForegroundColor Green
