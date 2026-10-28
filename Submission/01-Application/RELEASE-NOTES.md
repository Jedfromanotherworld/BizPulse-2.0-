# BizPulse 2.0.0
Native Android classroom demonstration, with the updated login/registration logo, owner-approved payment QR, and bundled offline AI.

## Install
Download BizPulse-2.0.0-arm64-debug.apk. Requires ARM64 Android 8.0 or later. Approximately 499 MB; allow extra space for the offline AI model. Installs alongside original BizPulse.
Demo owner login: owner / owner123.

This debug-signed build may have a different signature from an earlier BizPulse 2.0 download. If installation reports a signature conflict, back up your BizPulse 2.0 data before uninstalling the previous version. Original BizPulse uses a different application ID.

## Features
FIFO stock receiving, cashier shifts, Cash/QR checkout, barcode scanning with manual fallback, Code128 generation, sales exports, supplier geographic maps, and offline AI insights. Profile pictures are omitted.

## Testing
This release is published only after APK compilation, JVM unit tests, and Android test APK compilation succeed. Phone testing is still required for the camera, maps, payment flow, and AI performance. Compiling Android tests does not execute them on a phone.

The payment QR is public with the owner's explicit approval. Displaying it does not confirm a payment; verify receipt of funds separately.
