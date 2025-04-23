# Script para iniciar todos los servicios del proyecto Invernaderos-Amigochas
# Guardar como start-all-services.ps1 en la carpeta raiz

$rootDir = Get-Location
$global:serviceLogs = @{}

# Configuracion de JAVA_HOME si no esta definido
if (-not $env:JAVA_HOME) {
    $javaPath = "C:\Program Files\Java\jdk-21"
    Write-Host "JAVA_HOME no esta definido. Estableciendo temporalmente a: $javaPath" -ForegroundColor Yellow
    $env:JAVA_HOME = $javaPath
} else {
    Write-Host "JAVA_HOME está configurado como: $env:JAVA_HOME" -ForegroundColor Green
}

# Añadir Java bin al PATH si no está
if (-not ($env:PATH -like "*$env:JAVA_HOME\bin*")) {
    $env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
    Write-Host "Añadida la carpeta bin de Java al PATH: $env:JAVA_HOME\bin" -ForegroundColor Yellow
}

# Verificar que Java está accesible
try {
    $javaVersion = & java -version 2>&1
    Write-Host "Java detectado correctamente:" -ForegroundColor Green
    $javaVersion | ForEach-Object { Write-Host "  $_" -ForegroundColor Green }
} catch {
    Write-Host "ERROR: No se pudo ejecutar Java. Por favor verifica la instalación de Java." -ForegroundColor Red
    Write-Host "Error: $_" -ForegroundColor Red
    exit 1
}

# Funcion para iniciar un servicio en una nueva ventana de PowerShell con los logs visibles
function Start-Service {
    param (
        [string]$projectPath,
        [string]$name,
        [string]$command = "spring-boot:run",  # Comando por defecto
        [int]$waitTime = 20  # Tiempo de espera predeterminado en segundos
    )
    
    Write-Host "Iniciando $name..." -ForegroundColor Cyan
    
    # Verificar si existe mvnw.cmd en el directorio del proyecto
    $mvnwPath = Join-Path $projectPath "mvnw.cmd"
    if (Test-Path $mvnwPath) {
        # Crear el comando que establece JAVA_HOME y luego ejecuta mvnw mostrando el output directamente
        $comando = "Set-Location '$projectPath'; `$env:JAVA_HOME='$env:JAVA_HOME'; Write-Host 'Iniciando $name con JAVA_HOME=$env:JAVA_HOME' -ForegroundColor Green; & '.\mvnw.cmd' $command"
    } else {
        # Si no existe mvnw.cmd, intentar usar maven global
        $comando = "Set-Location '$projectPath'; `$env:JAVA_HOME='$env:JAVA_HOME'; Write-Host 'Iniciando $name con JAVA_HOME=$env:JAVA_HOME' -ForegroundColor Green; mvn $command"
    }
    
    # Iniciar el servicio en una nueva ventana mostrando el output directamente
    $process = Start-Process powershell -ArgumentList "-NoExit", "-Command", $comando -PassThru
    
    # Guardar información del proceso
    $global:serviceLogs[$name] = @{
        Process = $process
        StartTime = Get-Date
    }
    
    # Esperar un poco para que el proceso comience
    Start-Sleep -Seconds 2
    
    # Verificar si el proceso se inicio correctamente
    if ($process.HasExited) {
        Write-Host "ERROR - El servicio $name fallo al iniciar!" -ForegroundColor Red
        return $false
    }
    
    Write-Host "Proceso $name iniciado con PID $($process.Id)" -ForegroundColor Green
    Write-Host "Logs visibles directamente en la ventana del servicio" -ForegroundColor Yellow
    Write-Host "Esperando $waitTime segundos para la inicializacion completa..." -ForegroundColor Yellow
    Start-Sleep -Seconds $waitTime
    return $true
}

# Funcion principal para iniciar todos los servicios
function Start-AllServices {
    Write-Host "Este script inicia los microservicios Spring Boot" -ForegroundColor Cyan
    Write-Host "Para iniciar Gateway y SimuladorSensores, usa: ejecutar-java-proyectos.bat" -ForegroundColor Yellow
    Write-Host ""
    
    # Menu de opciones
    Write-Host "INICIAR MICROSERVICIOS" -ForegroundColor Green
    Write-Host "-------------------" -ForegroundColor Green
    Write-Host "1. Iniciar Config" -ForegroundColor White
    Write-Host "2. Iniciar Eureka" -ForegroundColor White
    Write-Host "3. Iniciar GestionSensores" -ForegroundColor White
    Write-Host "4. Iniciar Lecturas" -ForegroundColor White
    Write-Host "5. Iniciar Alarmas" -ForegroundColor White
    Write-Host "6. Iniciar Anomalyzer" -ForegroundColor White
    Write-Host "7. Iniciar APIGateway" -ForegroundColor White
    Write-Host "8. Iniciar todos los microservicios" -ForegroundColor White
    Write-Host "9. Salir" -ForegroundColor White
    Write-Host ""
    
    $opcion = Read-Host "Selecciona una opcion (1-9)"
    
    switch ($opcion) {
        "1" { 
            Start-Service -projectPath "$rootDir\Microservicios\Config" -name "Config"
        }
        "2" { 
            Start-Service -projectPath "$rootDir\Microservicios\Eureka" -name "Eureka" 
        }
        "3" { 
            Start-Service -projectPath "$rootDir\Microservicios\GestionSensores" -name "GestionSensores" 
        }
        "4" { 
            Start-Service -projectPath "$rootDir\Microservicios\Lecturas" -name "Lecturas" -waitTime 30
        }
        "5" { 
            Start-Service -projectPath "$rootDir\Microservicios\Alarmas" -name "Alarmas" 
        }
        "6" { 
            Start-Service -projectPath "$rootDir\Anomalyzer" -name "Anomalyzer" 
        }
        "7" { 
            Start-Service -projectPath "$rootDir\Microservicios\APIGateway" -name "APIGateway" 
        }
        "8" {
            # Iniciar todos en secuencia
            Start-Service -projectPath "$rootDir\Microservicios\Config" -name "Config"
            Start-Service -projectPath "$rootDir\Microservicios\Eureka" -name "Eureka"
            Start-Service -projectPath "$rootDir\Microservicios\GestionSensores" -name "GestionSensores"
            Start-Service -projectPath "$rootDir\Microservicios\Lecturas" -name "Lecturas" -waitTime 30
            Start-Service -projectPath "$rootDir\Microservicios\Alarmas" -name "Alarmas"
            Start-Service -projectPath "$rootDir\Anomalyzer" -name "Anomalyzer"
            Start-Service -projectPath "$rootDir\Microservicios\APIGateway" -name "APIGateway"
        }
        "9" {
            Write-Host "Saliendo..." -ForegroundColor Cyan
            return
        }
        default {
            Write-Host "Opcion no valida. Saliendo..." -ForegroundColor Red
            return
        }
    }
    
    Write-Host ""
    Write-Host "Microservicios iniciados. Recuerda que puedes iniciar Gateway y SimuladorSensores con 'ejecutar-java-proyectos.bat'" -ForegroundColor Green
    
    # Volver al menu principal
    Start-AllServices
}

# Iniciar el proceso principal
Start-AllServices