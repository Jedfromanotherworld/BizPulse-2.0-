$ErrorActionPreference = 'Stop'
$apkPath = Join-Path $PSScriptRoot 'BizPulse-2.0.0-arm64-debug.apk'
$expectedHash = 'e29e18543ba150f50e78e7dfc941981f1607cdaa4e8cb276f7d7ed392a189315'
if (Test-Path -LiteralPath $apkPath) {
    if ((Get-FileHash -LiteralPath $apkPath -Algorithm SHA256).Hash.ToLowerInvariant() -eq $expectedHash) {
        Write-Output "Verified existing APK: $apkPath"
        return
    }
    throw 'Existing APK has an unexpected checksum. Move it aside before retrying.'
}
$partialPath = Join-Path $PSScriptRoot ('apk-download-' + [guid]::NewGuid().ToString('N') + '.tmp')
try {
    Invoke-WebRequest -Uri 'https://github.com/Jedfromanotherworld/BizPulse-2.0-/releases/download/v2.0.0/BizPulse-2.0.0-arm64-debug.apk' -OutFile $partialPath
    if ((Get-FileHash -LiteralPath $partialPath -Algorithm SHA256).Hash.ToLowerInvariant() -ne $expectedHash) {
        throw 'APK checksum verification failed.'
    }
    Move-Item -LiteralPath $partialPath -Destination $apkPath
    Write-Output "Downloaded and verified: $apkPath"
} finally {
    if (Test-Path -LiteralPath $partialPath) { Remove-Item -LiteralPath $partialPath }
}
