# BizPulse 2 implementation overview

1. Overview: eight v2 revisions extend the native Kotlin app; separate identity keeps v1 installed.
2. Gradle: SDK 34, JDK 17, NDK 27.2, CMake, WorkManager and Coil; ARM64 build with native alignment support.
3. Database: batches, shifts and suppliers extend Room to schema 5 with explicit migrations; product images and receipt batch/shift snapshots included.
4. Repositories/ViewModels: transaction-level FIFO, role checks, shift gating and validation; OperationsRepository/OperationsViewModel handle new flows.
5. New screens: supplier maps, cashier shifts, sales report, offline AI cards and owner Code128 scan-to-add. Profile pictures omitted.
6. Existing screens: inventory receiving/images, kiosk POS, QR checkout, dashboard/report links, Manage tabs, updated login and guide.
7. Navigation: owner has Analytics/Manage; cashier has Shift/POS; both have Stock/Suppliers/Guide.
8. Utilities: LocalAiService JNI, daily WorkManager summary, CSV/PDF reports, image import and complete v2 backup support.
9. Branding: supplied logo in launcher/login, BizPulse 2 label; replacement supplied logo on login/registration; payment QR included in public source and APK with explicit owner approval.
10. Tests: 23 JVM tests passed including FIFO boundaries, Code128 round trip, CSV escaping and shift durations. Android tests compiled but await a device.
11. Documentation: overview, scope, design, UI/UX, evaluation checklist, conclusion and supplier records updated in the new repository.
12. Verification: build, signature, metadata, migration SQL, model checksum and native alignment checks passed. See BUILD-VERIFICATION.md for limits.
13. Installation/publishing: install the downloaded debug APK on an ARM64 Android 8+ device. Open the source ZIP root in Android Studio and run the model download script before rebuilding. GitHub Actions builds and tests the APK with the approved payment QR; the release workflow publishes a permanent APK download.
