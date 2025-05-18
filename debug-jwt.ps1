#!/usr/bin/env pwsh

Write-Host "=============================================="
Write-Host "Herramienta para depuración de tokens JWT"
Write-Host "=============================================="

function Decode-Jwt {
    param (
        [Parameter(Mandatory=$true)]
        [string]$Token
    )
    
    # En caso de que el token incluya "Bearer ", quitarlo
    if ($Token.StartsWith("Bearer ")) {
        $Token = $Token.Substring(7)
    }
    
    # Dividir el token en tres partes (header, payload, signature)
    $parts = $Token -split '\.'
    
    if ($parts.Length -ne 3) {
        Write-Host "Formato de token inválido. No se pudo decodificar." -ForegroundColor Red
        return
    }
    
    # Decodificar el header (parte 0)
    try {
        # Asegurar que la longitud sea múltiplo de 4 para base64
        $padHeader = $parts[0]
        $padding = 4 - ($padHeader.Length % 4)
        if ($padding -lt 4) {
            $padHeader = $padHeader + ("=" * $padding)
        }
        
        $headerBytes = [Convert]::FromBase64String($padHeader.Replace('-', '+').Replace('_', '/'))
        $header = [System.Text.Encoding]::UTF8.GetString($headerBytes)
    }
    catch {
        Write-Host "Error decodificando el header: $_" -ForegroundColor Red
        Write-Host "Intentando con otro método de decodificación..." -ForegroundColor Yellow
        # Alternativa usando web-safe base64url encoding
        $headerBytes = [Convert]::FromBase64String($parts[0] + "==")
        $header = [System.Text.Encoding]::UTF8.GetString($headerBytes)
    }
    
    # Decodificar el payload (parte 1)
    try {
        # Asegurar que la longitud sea múltiplo de 4 para base64
        $padPayload = $parts[1]
        $padding = 4 - ($padPayload.Length % 4)
        if ($padding -lt 4) {
            $padPayload = $padPayload + ("=" * $padding)
        }
        
        $payloadBytes = [Convert]::FromBase64String($padPayload.Replace('-', '+').Replace('_', '/'))
        $payload = [System.Text.Encoding]::UTF8.GetString($payloadBytes)    }
    catch {
        Write-Host "Error con el primer método: $_" -ForegroundColor Yellow
        # Método alternativo más simple
        $paddedPayload = $parts[1]
        while ($paddedPayload.Length % 4 -ne 0) {
            $paddedPayload += "="
        }
        
        try {
        $payloadBytes = [Convert]::FromBase64String($paddedPayload)
        $payload = [System.Text.Encoding]::UTF8.GetString($payloadBytes)
        
        Write-Host "Header:" -ForegroundColor Cyan
        Write-Host ($header | ConvertFrom-Json | ConvertTo-Json) -ForegroundColor Yellow
        
        Write-Host "`nPayload:" -ForegroundColor Cyan
        Write-Host ($payload | ConvertFrom-Json | ConvertTo-Json) -ForegroundColor Green
        
        # Analizar si contiene el claim "role"
        $payloadJson = $payload | ConvertFrom-Json
        if ($payloadJson.role) {
            Write-Host "`nRol encontrado:" -ForegroundColor Magenta
            Write-Host $payloadJson.role -ForegroundColor White
        } else {
            Write-Host "`nNo se encontró el claim 'role' en el token" -ForegroundColor Red
        }
    } catch {
        Write-Host "Error decodificando el payload: $_" -ForegroundColor Red
    }
}

# Solicitar el token al usuario
$token = Read-Host -Prompt "`nPor favor, ingresa el token JWT que quieres analizar"

Decode-Jwt -Token $token

Write-Host "`n=============================================="
