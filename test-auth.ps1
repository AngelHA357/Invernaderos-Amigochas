#!/bin/powershell
# test-auth.ps1 - Script para probar la autenticación con gRPC

Write-Host "Iniciando prueba de autenticación" -ForegroundColor Blue

# Primero, obtenemos un token JWT desde el servicio de Login
Write-Host "`n1. Obteniendo token de autenticación..." -ForegroundColor Cyan
$loginUrl = "http://localhost:8080/api/v1/login"
$loginBody = @{
    username = "admin"
    password = "admin123"
} | ConvertTo-Json

try {
    $response = Invoke-RestMethod -Uri $loginUrl -Method Post -Body $loginBody -ContentType "application/json"
    $token = $response.token
    Write-Host "Token obtenido con éxito!" -ForegroundColor Green
    Write-Host "Token: $token`n"
} catch {
    Write-Host "Error al obtener token: $_" -ForegroundColor Red
    exit 1
}

# Segundo, probamos el acceso a un recurso protegido sin token
Write-Host "`n2. Probando acceso sin autenticación..." -ForegroundColor Cyan
$protectedUrl = "http://localhost:8080/api/v1/gestionSensores/sensores"

try {
    Invoke-RestMethod -Uri $protectedUrl -Method Get
    Write-Host "¡Error! Se permitió el acceso sin token" -ForegroundColor Red
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401) {
        Write-Host "Prueba correcta: Acceso denegado (401 Unauthorized)" -ForegroundColor Green
    } else {
        Write-Host "Error inesperado: $statusCode - $_" -ForegroundColor Red
    }
}

# Tercero, probamos el acceso con el token obtenido
Write-Host "`n3. Probando acceso con token válido..." -ForegroundColor Cyan
$headers = @{
    Authorization = "Bearer $token"
}

try {
    $response = Invoke-RestMethod -Uri $protectedUrl -Method Get -Headers $headers
    Write-Host "Prueba correcta: Acceso permitido con token válido" -ForegroundColor Green
    Write-Host "Datos obtenidos:" -ForegroundColor Green
    Write-Host ($response | ConvertTo-Json -Depth 3)
} catch {
    Write-Host "Error al acceder con token: $_" -ForegroundColor Red
}

# Finalmente, probamos con un token inválido
Write-Host "`n4. Probando con token inválido..." -ForegroundColor Cyan
$invalidToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJ1c3VhcmlvX2ludmFsaWRvIiwicm9sZSI6InVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9.invalidSignature"
$invalidHeaders = @{
    Authorization = "Bearer $invalidToken"
}

try {
    Invoke-RestMethod -Uri $protectedUrl -Method Get -Headers $invalidHeaders
    Write-Host "¡Error! Se permitió el acceso con token inválido" -ForegroundColor Red
} catch {
    $statusCode = $_.Exception.Response.StatusCode.value__
    if ($statusCode -eq 401) {
        Write-Host "Prueba correcta: Acceso denegado con token inválido (401 Unauthorized)" -ForegroundColor Green
    } else {
        Write-Host "Error inesperado: $statusCode - $_" -ForegroundColor Red
    }
}

Write-Host "`nPruebas completadas!" -ForegroundColor Blue
