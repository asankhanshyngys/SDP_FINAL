$ErrorActionPreference = 'Stop'
$projectRoot = Split-Path -Parent $PSScriptRoot
& (Join-Path $PSScriptRoot 'build.ps1')
Push-Location $projectRoot
try {
    New-Item -ItemType Directory -Force -Path build/test-classes | Out-Null
    $sources = @(Get-ChildItem -LiteralPath src/test/java -Recurse -Filter *.java | ForEach-Object { $_.FullName })
    & javac --release 17 -encoding UTF-8 -cp build/classes -d build/test-classes @sources
    if ($LASTEXITCODE -ne 0) { throw 'Test compilation failed.' }
    & java '-Djava.awt.headless=true' -cp 'build/classes;build/test-classes' restaurant.SystemTest
    if ($LASTEXITCODE -ne 0) { throw 'Checks failed.' }
} finally { Pop-Location }
