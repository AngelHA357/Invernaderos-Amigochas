#!/usr/bin/env pwsh

# Definir los microservicios que vamos a iniciar y su orden
$servicios = @(
    @{
        Nombre = "Config Server";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Config";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 10
    },
    @{
        Nombre = "Eureka Server";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Eureka";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 15
    },
    @{
        Nombre = "Login Service";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Login";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 5
    },
    @{
        Nombre = "Autenticación Service";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Autenticacion";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 5
    },
    @{
        Nombre = "Gestión Sensores Service";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\GestionSensores";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 5
    },
    @{
        Nombre = "API Gateway";
        Ruta = "C:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\APIGateway";
        Comando = ".\mvnw.cmd spring-boot:run";
        EsperarSegundos = 5
    }
)

function Iniciar-Servicio {
    param (
        [Parameter(Mandatory=$true)]
        [hashtable]$ServicioInfo
    )

    $nombre = $ServicioInfo.Nombre
    $ruta = $ServicioInfo.Ruta
    $cmd = $ServicioInfo.Comando
    $espera = $ServicioInfo.EsperarSegundos

    Write-Host "`n"
    Write-Host "=========================================================" -ForegroundColor Cyan
    Write-Host "Iniciando $nombre..." -ForegroundColor Cyan
    Write-Host "=========================================================" -ForegroundColor Cyan

    Start-Process -FilePath "powershell" -ArgumentList "-NoExit", "-Command", "Set-Location '$ruta'; $cmd"

    Write-Host "Esperando $espera segundos para que $nombre arranque..." -ForegroundColor Yellow
    Start-Sleep -Seconds $espera
}

# Iniciar cada servicio
foreach ($servicio in $servicios) {
    Iniciar-Servicio -ServicioInfo $servicio
}

Write-Host "`n"
Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Todos los servicios han sido iniciados!" -ForegroundColor Green
Write-Host "==========================================================" -ForegroundColor Green
Write-Host "Puedes probar el flujo de autenticación con: .\test-auth-flow.ps1"
Write-Host "Para ver detalles de un token JWT, usa: .\debug-jwt.ps1"
