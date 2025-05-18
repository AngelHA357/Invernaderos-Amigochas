#!/bin/powershell
# start-services-for-auth-test.ps1 - Script para iniciar los servicios necesarios para probar la autenticación

# Colores para mejor visualización
$colorConfig = "Yellow"
$colorEureka = "Cyan"
$colorAuth = "Magenta"
$colorGateway = "Green"
$colorOther = "White"

# Función para iniciar un servicio Maven en una nueva ventana
function Start-MavenService {
    param (
        [string]$path,
        [string]$serviceName,
        [string]$color
    )
    
    Write-Host "Iniciando $serviceName..." -ForegroundColor $color
    
    # Verificar si el directorio existe
    if (-not (Test-Path $path)) {
        Write-Host "ERROR: La ruta '$path' no existe!" -ForegroundColor Red
        return $false
    }
    
    # Verificar si existe pom.xml
    if (-not (Test-Path "$path\pom.xml")) {
        Write-Host "ERROR: No se encontró pom.xml en '$path'" -ForegroundColor Red
        return $false
    }
    
    # Usar ./mvnw en vez de mvn para asegurar que se use el wrapper correcto
    $mavenCmd = ".\mvnw"
    if (-not (Test-Path "$path\mvnw")) {
        $mavenCmd = "mvn" # Fallback a mvn global si no existe wrapper
    }
    
    Start-Process powershell.exe -ArgumentList "-NoExit", "-Command", "cd '$path'; Write-Host 'Arrancando $serviceName...' -ForegroundColor $color; $mavenCmd spring-boot:run"
    Start-Sleep -Seconds 2
    
    return $true
}

# Crear directorio para logs
$logDir = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\logs"
if (-not (Test-Path $logDir)) {
    New-Item -ItemType Directory -Path $logDir | Out-Null
}

# 1. Iniciar Config Server
$configPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Config"
$success = Start-MavenService -path $configPath -serviceName "Config Server" -color $colorConfig

if (-not $success) {
    Write-Host "ERROR: No se pudo iniciar Config Server. Abortando..." -ForegroundColor Red
    exit 1
}

Write-Host "Esperando a que Config Server esté listo..." -ForegroundColor $colorConfig
Start-Sleep -Seconds 15  # Dar tiempo a que el Config Server esté listo

# 2. Iniciar Eureka
$eurekaPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Eureka"
$success = Start-MavenService -path $eurekaPath -serviceName "Eureka Server" -color $colorEureka

if (-not $success) {
    Write-Host "ERROR: No se pudo iniciar Eureka Server. Abortando..." -ForegroundColor Red
    exit 1
}

Write-Host "Esperando a que Eureka esté listo..." -ForegroundColor $colorEureka
Start-Sleep -Seconds 15  # Dar tiempo a que Eureka esté listo

# 3. Iniciar Servicio de Autenticación
$authPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Autenticacion"
$success = Start-MavenService -path $authPath -serviceName "Autenticacion Service" -color $colorAuth

if (-not $success) {
    Write-Host "ERROR: No se pudo iniciar el servicio de Autenticación. Abortando..." -ForegroundColor Red
    exit 1
}

Write-Host "Esperando a que Auth Service esté listo..." -ForegroundColor $colorAuth
Start-Sleep -Seconds 15  # Dar tiempo a que Auth esté listo

# 4. Iniciar API Gateway
$gatewayPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\APIGateway"
$success = Start-MavenService -path $gatewayPath -serviceName "API Gateway" -color $colorGateway

if (-not $success) {
    Write-Host "ERROR: No se pudo iniciar API Gateway. Abortando..." -ForegroundColor Red
    exit 1
}

Write-Host "Esperando a que API Gateway esté listo..." -ForegroundColor $colorGateway
Start-Sleep -Seconds 15  # Dar tiempo a que Gateway esté listo

# 5. Iniciar otros servicios necesarios (como el servicio de login y gestión sensores)
$loginPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\Login"
$success = Start-MavenService -path $loginPath -serviceName "Login Service" -color $colorOther

if (-not $success) {
    Write-Host "ADVERTENCIA: No se pudo iniciar Login Service. Las pruebas de autenticación podrían fallar." -ForegroundColor Yellow
}

$sensoresPath = "c:\Users\JoseH\OneDrive\Documentos\GitHub\Invernaderos-Amigochas\Microservicios\GestionSensores"
$success = Start-MavenService -path $sensoresPath -serviceName "Gestion Sensores" -color $colorOther

if (-not $success) {
    Write-Host "ADVERTENCIA: No se pudo iniciar Gestion Sensores. Las pruebas podrían fallar parcialmente." -ForegroundColor Yellow
}

Write-Host "Todos los servicios han sido iniciados. Espera unos 30 segundos antes de ejecutar las pruebas..." -ForegroundColor "Green"
