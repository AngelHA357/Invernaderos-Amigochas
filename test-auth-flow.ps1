#!/usr/bin/env pwsh

Write-Host "=============================================="
Write-Host "Test de Autenticación para Microservicios"
Write-Host "=============================================="

$username = Read-Host -Prompt "Ingresa nombre de usuario"
$password = Read-Host -Prompt "Ingresa contraseña"

Write-Host "`n1. Intentando autenticar y obtener token..."
$loginBody = @{
    username = $username
    password = $password
} | ConvertTo-Json

try {
    $loginResponse = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/login" -Method Post -ContentType "application/json" -Body $loginBody
    Write-Host "   Autenticación exitosa!" -ForegroundColor Green
    $token = $loginResponse.token
    Write-Host "   Token obtenido: $token"

    Write-Host "`n2. Probando acceso a gestionSensores con el token..."
    $headers = @{ "Authorization" = "Bearer $token" }
    try {
        $sensores = Invoke-RestMethod -Uri "http://localhost:8080/api/v1/gestionSensores" -Method Get -Headers $headers
        Write-Host "   Acceso a gestionSensores exitoso!" -ForegroundColor Green
        Write-Host "   Respuesta:" -ForegroundColor Cyan
        Write-Host ($sensores | ConvertTo-Json)
    }
    catch {
        Write-Host "   Error accediendo a gestionSensores: $($_.Exception.Message)" -ForegroundColor Red
        Write-Host "   Código de respuesta: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        
        # Mostrar detalles del error
        if ($_.ErrorDetails) {
            Write-Host "   Detalles del error:" -ForegroundColor Red
            Write-Host $_.ErrorDetails.Message -ForegroundColor Red
        }
    }
}
catch {
    Write-Host "   Error en autenticación: $($_.Exception.Message)" -ForegroundColor Red
    Write-Host "   Código de respuesta: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
}
