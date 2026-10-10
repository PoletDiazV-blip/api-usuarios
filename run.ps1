# Cargar variables del .env
Get-Content .env | Where-Object { 
    $_ -match "=" -and $_ -notmatch "^\s*#" 
} | ForEach-Object {
    $key, $value = $_ -split "=", 2
    [System.Environment]::SetEnvironmentVariable($key.Trim(), $value.Trim(), "Process")
}

Write-Host "Variables cargadas:" -ForegroundColor Green
Write-Host "  DB_URL: $env:DB_URL"
Write-Host "  DB_USERNAME: $env:DB_USERNAME"
Write-Host "  DB_PASSWORD: $env:DB_PASSWORD"
Write-Host ""

# Arrancar la aplicacion
./mvnw spring-boot:run
