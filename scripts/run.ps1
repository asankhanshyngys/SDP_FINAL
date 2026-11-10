$ErrorActionPreference = 'Stop'
& (Join-Path $PSScriptRoot 'build.ps1')
& java -jar (Join-Path (Split-Path -Parent $PSScriptRoot) 'dist/restaurant-ordering.jar')
if ($LASTEXITCODE -ne 0) { throw 'Application failed.' }
