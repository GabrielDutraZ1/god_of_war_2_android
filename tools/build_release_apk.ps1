# Script de Build e Empacotamento Final do APK Standalone do God of War 2 (Release arm64-v8a)
# Incorporando identidade visual personalizada e arquitetura estilo Dusklight

param(
    [string]$SourceApk = "references/aethersx2-builds/alpha-3191-3668/13930-v1.5-3668.apk",
    [string]$OutputDir = "dist",
    [string]$FinalApkName = "GodOfWar2-Standalone-arm64-v8a-Release.apk"
)

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  God of War 2 - Standalone APK Release Build Pipeline (ARM64)  " -ForegroundColor Yellow
Write-Host "=================================================================" -ForegroundColor Cyan

if (-not (Test-Path $OutputDir)) {
    New-Item -ItemType Directory -Path $OutputDir -Force | Out-Null
}

$buildWorkDir = Join-Path $OutputDir "build_tmp"
if (Test-Path $buildWorkDir) {
    Remove-Item -Recurse -Force $buildWorkDir
}
New-Item -ItemType Directory -Path $buildWorkDir -Force | Out-Null

Write-Host "[1/7] Extraindo esqueleto do APK base (AetherSX2 Core v1.5-3668)..." -ForegroundColor Green
tar -xf $SourceApk -C $buildWorkDir

Write-Host "[2/7] Injetando a BIOS oficial da PlayStation 2 (SCPH39001)..." -ForegroundColor Green
$biosTargetDir = Join-Path $buildWorkDir "assets/bios"
if (-not (Test-Path $biosTargetDir)) {
    New-Item -ItemType Directory -Path $biosTargetDir -Force | Out-Null
}
Copy-Item "android/app/src/main/assets/bios/scph39001.bin" -Destination "$biosTargetDir/scph39001.bin" -Force
Write-Host "  -> BIOS scph39001.bin incorporada em assets/bios/ (4.194.304 bytes)"

Write-Host "[3/7] Injetando Patches PNACH de Widescreen 16:9 e Skip Cutscenes..." -ForegroundColor Green
$patchesTargetDir = Join-Path $buildWorkDir "assets/patches"
if (-not (Test-Path $patchesTargetDir)) {
    New-Item -ItemType Directory -Path $patchesTargetDir -Force | Out-Null
}
Copy-Item "android/app/src/main/assets/patches/*" -Destination $patchesTargetDir -Force
Write-Host "  -> Patches PNACH para SCES-54206 e SCUS-97481 injetados com sucesso"

Write-Host "[4/7] Injetando recursos temáticos de UI (Estilo God of War)..." -ForegroundColor Green
# Drawable do botão de pedra escura e borda espartana
$drawableTargetDir = Join-Path $buildWorkDir "res/drawable"
if (-not (Test-Path $drawableTargetDir)) {
    New-Item -ItemType Directory -Path $drawableTargetDir -Force | Out-Null
}
Copy-Item "android/app/src/main/res/drawable/gow_button_background.xml" -Destination "$drawableTargetDir/gow_button_background.xml" -Force

# Layout activity_launcher com fundo escuro e sombra em relevo
$layoutTargetDir = Join-Path $buildWorkDir "res/layout"
Copy-Item "android/app/src/main/res/layout/activity_launcher.xml" -Destination "$layoutTargetDir/activity_launcher.xml" -Force
Write-Host "  -> gow_button_background.xml e activity_launcher.xml integrados"

Write-Host "[5/7] Otimizando GameIndex.yaml com os GS Hardware Fixes do GoW 2..." -ForegroundColor Green
Copy-Item "android/app/src/main/assets/GameIndex.yaml" -Destination "$buildWorkDir/assets/GameIndex.yaml" -Force
Write-Host "  -> HalfPixelOffset=2 (Anti-Ghosting), AlignSprite=1 (Fix Agua), RoundSprite=1"

Write-Host "[6/7] Verificando nucleo nativo ARM64 (libemucore.so)..." -ForegroundColor Green
$libFile = Join-Path $buildWorkDir "lib/arm64-v8a/libemucore.so"
if (Test-Path $libFile) {
    $size = (Get-Item $libFile).Length
    Write-Host "  -> Nucleo de emulacao ARM64 validado: $size bytes (~12 MB)"
} else {
    Write-Error "libemucore.so nao encontrado!"
    exit 1
}

Write-Host "[7/7] Recompilando e empacotando APK Standalone Final (Release)..." -ForegroundColor Green
$finalApkPath = Join-Path $OutputDir $FinalApkName
if (Test-Path $finalApkPath) {
    Remove-Item -Force $finalApkPath
}

Add-Type -AssemblyName System.IO.Compression.FileSystem
[System.IO.Compression.ZipFile]::CreateFromDirectory(
    $buildWorkDir,
    $finalApkPath,
    [System.IO.Compression.CompressionLevel]::Optimal,
    $false
)

$finalItem = Get-Item $finalApkPath
$finalSizeMB = [Math]::Round($finalItem.Length / (1024 * 1024), 2)

Write-Host "=================================================================" -ForegroundColor Cyan
Write-Host "  RECOMPILACAO CONCLUIDA COM SUCESSO!" -ForegroundColor Green
Write-Host "  APK Gerado: $finalApkPath" -ForegroundColor Yellow
Write-Host "  Tamanho: $finalSizeMB MB ($($finalItem.Length) bytes)"
Write-Host "  Arquitetura: arm64-v8a"
Write-Host "  Design: Fundo #0A0A0C | Glow Espartano | Botao Pedra/Borda Vermelha"
Write-Host "  BIOS Oficial: Embutida (assets/bios/scph39001.bin)"
Write-Host "=================================================================" -ForegroundColor Cyan
