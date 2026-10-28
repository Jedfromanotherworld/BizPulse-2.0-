# Testing and evaluation — BizPulse 2.0

## Actual device test

- Application version: 2.0.0
- Tester: Jedediah Ebalobo
- Phone model: POCO X8 Pro
- Android version: 16
- Test date: October 27, 2026
- Overall result: PASS — the tester reports that all application functions were tested and worked, with no functional errors encountered.

The results below record the tester's report, not an independent rerun by the documentation author. Screenshots and a demonstration video were supplied as supporting evidence.

| Test area | Actual result | Status |
|---|---|---|
| Installation and launch | Tested; no problem reported | Pass  |
| Owner/cashier login, registration and validation | Tested; working with no errors reported | Pass  |
| Inventory, products, categories and stock batches | Tested; working with no errors reported | Pass  |
| Cashier shifts and checkout requirements | Tested; working with no errors reported | Pass  |
| Camera barcode, manual entry and barcode tools | Tested; working with no errors reported in version 2.0 | Pass  |
| POS, cash/QR checkout and receipts | Tested; working with no errors reported | Pass  |
| Analytics, reports, printing and exports | Tested; working with no errors reported | Pass  |
| Supplier maps | Tested; working with no errors reported | Pass  |
| User management, settings and backups | Tested; working with no errors reported | Pass  |
| Notifications and other application functions | Included in the tester's all-functions test; no errors reported | Pass  |
| Dark mode and navigation | Tested; working with no errors reported | Pass  |
| Offline AI | Generated output successfully, but generation took a long time; acceptable to the tester | Pass with performance observation |

## Observations and recommendations

No functional errors were reported during this device test. Offline AI response time is the remaining performance observation; the exact duration was not measured. Future work should optimize generation speed and record timed results. The earlier barcode problem concerned version 1 and is not a failure reported for this version 2 test.

Individual step-by-step logs, measured AI response times and a formal user-evaluation survey were not supplied. The overall all-functions report does not provide separate measurements for every edge case. QR payment receipt is manually verified in the payment account; the app does not automatically verify bank transfers.

## Supporting evidence

The supplied October 27 screenshots show current owner and cashier screens. The demonstration recording is approximately 3 minutes 21 seconds and shows navigation, cashier Time In, camera scan activity, a completed sale receipt and a shift summary.

## Automated build verification

The recorded build verification reports 23 JVM unit tests passed, and the main APK and instrumented test APK compiled. See BUILD-VERIFICATION.md. Compilation is separate from the physical-device test reported above.
