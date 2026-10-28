# Testing and evaluation

Automated results: 23 unit tests passed; main APK and instrumented test APK compiled. See BUILD-VERIFICATION.md. Do not mark device cases Passed before running them on the new APK.

| Test | Expected | Actual | Status |
|---|---|---|---|
| Install alongside v1 | Both launch separately with independent data | Not yet tested on phone | Pending |
| Login and invalid fields | Valid roles accepted; invalid input rejected | Not yet tested on phone | Pending |
| Shift gate | No sale before Time In; force Time Out prevents new sales | Not yet tested on phone | Pending |
| FIFO and expiry | Oldest eligible batch deducted; expired stock excluded | Not yet tested on phone | Pending |
| Camera barcode | Scans printed product barcode, no duplicate sale | Not yet tested on phone | Pending |
| Manual / simulate / Code128 | Correct product and scannable preview | Not yet tested on phone | Pending |
| QR checkout | Supplied image legible; manual confirmation required | Not yet tested on phone | Pending |
| Supplier map | Three pins match intended store locations | Not yet tested on phone | Pending |
| Offline AI | Airplane-mode summary without crash; note duration | Not yet tested on phone | Pending |
| Daily notification | Summary eventually generated with permission | Not yet tested on phone | Pending |
| Report / print | CSV/PDF and receipt contain matching totals | Not yet tested on phone | Pending |
| Backup / restore | Records, batches, shifts and selected images restored | Not yet tested on phone | Pending |
| Dark mode | Text and controls remain readable | Not yet tested on phone | Pending |

Planned device: POCO X8 Pro, Android 16. Enter actual version, tester, date and observations after installing v2. Previous v1 reports are historical evidence, not v2 test results.

Screenshot checklist: login, owner dashboard, cashier shift, inventory, receive stock, scan-to-add barcode, kiosk, QR checkout, receipt, analytics AI, report, supplier map, Manage and dark mode.


