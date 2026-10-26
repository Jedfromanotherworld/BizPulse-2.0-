# Build verification — BizPulse 2.0.0

Verified on 26 October 2026.

- `assembleDebug`: PASS. Installable debug-signed ARM64 APK produced.
- `testDebugUnitTest`: PASS — 23 tests, zero failures/errors.
- `assembleDebugAndroidTest`: PASS — instrumented test APK compiled; not run on a device.
- Migration SQL: PASS — all nine tables, indexes and foreign keys match generated Room v5 schema; opening batch preserves stock/cost.
- APK signature verification: PASS (v2 signing scheme).
- Package metadata: com.bizpulse.app.v2, versionCode 2, versionName 2.0.0, min SDK 26, target SDK 34, arm64-v8a.
- Native ELF LOAD segments: 16 KB alignment. Native library is compressed for compatibility with this AGP version. Physical 16 KB-device testing remains pending.
- Model SHA-256 verified against pinned download. Exactly one model asset is included.
- Supplied QR image: successfully decoded with ZXing. This does not confirm payment account ownership or live payment receipt.

Private-download APK size: 499073542 bytes.
SHA-256: `6a0b7068bfd21d7a2b94196967283f806feee985e83f71415669554915574d6d`.

The private APK includes the supplied payment QR. Public GitHub source/workflow builds exclude that image; the owner can select it in Manage → QR Settings. Public and private APK hashes/signing certificates may differ.

Not verified on a physical device: installation, camera scanning, maps, offline AI performance, notification timing, printing, backup/restore UI and accessibility. The Android test suite is compiled only. Do not mark these device cases Passed without testing on the POCO X8 Pro.
