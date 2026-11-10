$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'test.ps1')
Push-Location $projectRoot
try {
    if (!(Test-Path -LiteralPath output/pdf/restaurant-project-report.pdf)) {
        throw 'Generate the report using scripts/create_report.py before packaging.'
    }
    Compress-Archive -Path src,scripts,docs,output,README.md -DestinationPath dist/restaurant-source.zip -Force
    Write-Host 'Created dist/restaurant-source.zip'
} finally { Pop-Location }
