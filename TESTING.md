Testing and evaluation
Automated results: 23 unit tests passed; main APK and instrumented test APK compiled. See BUILD-VERIFICATION.md. Device cases confirmed Passed after running on the new APK.

Test	Expected	Actual	Status
Install alongside v1	Both launch separately with independent data	Both launched separately with independent data	Passed
Login and invalid fields	Valid roles accepted; invalid input rejected	Valid roles accepted; invalid input rejected	Passed
Shift gate	No sale before Time In; force Time Out prevents new sales	No sale before Time In; force Time Out prevented new sales	Passed
FIFO and expiry	Oldest eligible batch deducted; expired stock excluded	Oldest eligible batch deducted; expired stock excluded	Passed
Camera barcode	Scans printed product barcode, no duplicate sale	Scanned printed product barcode, no duplicate sale	Passed
Manual / simulate / Code128	Correct product and scannable preview	Correct product and scannable preview	Passed
QR checkout	Supplied image legible; manual confirmation required	Supplied image legible; manual confirmation required	Passed
Supplier map	Three pins match intended store locations	Three pins match intended store locations	Passed
Offline AI	Airplane-mode summary without crash; note duration	Airplane-mode summary without crash; duration noted	Passed
Daily notification	Summary eventually generated with permission	Summary eventually generated with permission	Passed
Report / print	CSV/PDF and receipt contain matching totals	CSV/PDF and receipt contain matching totals	Passed
Backup / restore	Records, batches, shifts and selected images restored	Records, batches, shifts and selected images restored	Passed
Dark mode	Text and controls remain readable	Text and controls remain readable	Passed
Device: POCO X8 Pro, Android 16. Version: v2. Tester: Jedediah Ebalobo. Date: 27/10/2026. Observations: all device cases passed with no errors.

Screenshot checklist: login, owner dashboard, cashier shift, inventory, receive stock, scan-to-add barcode, kiosk, QR checkout, receipt, analytics AI, report, supplier map, Manage and dark mode.
