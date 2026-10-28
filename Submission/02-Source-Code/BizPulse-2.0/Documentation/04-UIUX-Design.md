# UI/UX and implementation

Native Kotlin and Jetpack Compose, Material 3 light/dark themes, Hilt ViewModels and Room. Owner: Home, Stock, Analytics, Suppliers, Manage, Guide. Cashier: Home, Shift, Stock, POS, Suppliers, Guide. Product forms validate name, category, barcode, price and quantities. Camera has permission handling and manual fallback. Checkout rechecks saved prices/stock and requires an active shift. QR uses the supplied static payment image. Report exports use FileProvider. Maps render geographic tiles and show attribution. Profile pictures are intentionally omitted.
