# BizPulse 2.0

Native Android inventory and point-of-sale app for the BIZPULSE team, BSIS 2C, Bulacan Polytechnic College.

Repository: https://github.com/Jedfromanotherworld/BizPulse-2.0-

## Installation

Version 2.0.0 uses application ID `com.bizpulse.app.v2`. It installs alongside BizPulse 1 and starts with its own local demo data. It does not import the old installation automatically. Export important data before uninstalling either app. Android 8 or later and an ARM64 device are required by this build.

The APK is a debug-signed academic test build, not a production payment application. See BUILD-VERIFICATION.md for actual build/test status. Physical-device testing is separate from compilation.

## Features

- Owner and cashier login, inventory, categories, audit history, receipt printing and backup.
- Barcode camera scanning, manual entry and simulated scan; owner Code128 preview and scan-to-add.
- FIFO stock batches with supplier, received date, cost and optional expiry. Checkout excludes expired batches.
- Cashier Time In/Out; checkout requires an active shift. Owners can inspect shifts and force Time Out.
- Cash or static QR payments. The cashier must independently verify payment receipt.
- Kiosk product grid with images, category filter and search.
- Owner sales report with date range, receipt batch details, CSV and PDF exports.
- Offline Qwen2.5 0.5B Q4_K_M via pinned llama.cpp JNI. No AI API key or network call. Suggestions may be wrong; totals come from recorded data.
- Daily AI summary scheduled around 8 AM local device time; Android can delay work.
- South Supermarket, O!Save and Alfamart supplier addresses in Malolos. OpenStreetMap geographic map tiles need internet.
- New supplied logo and payment QR. Profile pictures omitted at the team's request.

## Demo accounts

| Role | Username | Password |
|---|---|---|
| Owner | owner | owner123 |
| Cashier | cashier | cashier123 |
| Cashier | ana | ana123 |

Owner can edit account names in Manage. Demo registration permits either role; this is not production access control. The inherited password format uses fixed-salt SHA-256. Harden authentication before real deployment.

## Build

Use JDK 17, Android SDK 34 / Build Tools 34.0.0, NDK 27.2.12479018 and CMake 3.22.1. Gradle wrapper 8.2 and Android Gradle Plugin 8.2.2 are pinned.

1. Clone with submodules: `git clone --recurse-submodules https://github.com/Jedfromanotherworld/BizPulse-2.0-.git`.
2. Copy your private payment QR to `app/src/main/res/drawable/owner_qr.png` before building, or select it later in Manage → QR Settings. This image is excluded from the public source. Run `python3 scripts/download_model.py` (about 469 MiB). The model is bundled into the APK; first use copies it into private app storage. Allow over 1 GB storage plus working RAM.
3. Open the project root in Android Studio and let it set `local.properties` for your SDK.
4. Run `./gradlew assembleDebug testDebugUnitTest` (Windows: `gradlew.bat`).
5. Find the APK at `app/build/outputs/apk/debug/app-debug.apk`.

Source ZIPs may already include the pinned vendor source; the model download script reuses an existing file only after its checksum passes. Do not commit SDKs, signing keys, build caches or the large model file. See licenses/ and Documentation/THIRD-PARTY.md.

## Limitations

Data is local to one installation; this is not multi-device synchronization. Supplier pins use centers of supplied Plus Codes, not surveyed entrances. QR is a displayed image, not bank integration. Demonstration VAT is not tax-invoice certification. The old app's reported camera failure does not establish results for v2; camera, QR readability, map positions, notifications and AI performance require fresh device tests.
