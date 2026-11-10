$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
Push-Location $projectRoot
try {
    New-Item -ItemType Directory -Force -Path build/classes, dist | Out-Null
    $sources = @(Get-ChildItem -LiteralPath src/main/java -Recurse -Filter *.java | ForEach-Object { $_.FullName })
    & javac --release 17 -encoding UTF-8 -d build/classes @sources
    if ($LASTEXITCODE -ne 0) { throw 'Compilation failed.' }
    & jar --create --file dist/restaurant-ordering.jar --main-class restaurant.Main -C build/classes .
    if ($LASTEXITCODE -ne 0) { throw 'JAR creation failed.' }
    Write-Host 'Built dist/restaurant-ordering.jar (Java 17+)'
} finally { Pop-Location }
