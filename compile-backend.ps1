# Compile tous les microservices Backend (Java 17 requis).
# Usage : depuis la racine du depot, en PowerShell :
#   .\compile-backend.ps1
# Ou : powershell -ExecutionPolicy Bypass -File .\compile-backend.ps1

$ErrorActionPreference = "Stop"
$Root = $PSScriptRoot

# Lombok et Spring Boot 3.2 sont validés en JDK 17. Si JAVA_HOME pointe vers JDK 24+,
# la compilation peut échouer (Lombok). On préfère JDK 17 lorsqu'il est présent.
$Jdk17Candidates = @(
    $env:JAVA_HOME_17
    "C:\Java\jdk-17.0.17+10"
)
foreach ($jdkRoot in $Jdk17Candidates) {
    if (-not $jdkRoot) { continue }
    $javac = Join-Path $jdkRoot "bin\javac.exe"
    if (Test-Path $javac) {
        $env:JAVA_HOME = $jdkRoot.TrimEnd('\')
        $env:PATH = "$(Join-Path $jdkRoot 'bin');$env:PATH"
        Write-Host "JAVA_HOME -> $($env:JAVA_HOME) (JDK 17 pour Maven)" -ForegroundColor DarkGray
        break
    }
}

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
