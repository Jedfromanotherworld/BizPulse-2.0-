package com.bizpulse.app.ui.screens.guide
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import com.bizpulse.app.ui.components.*
@Composable fun GuideScreen(role:String,onLogout:()->Unit) {
    Page {
        ScreenHeader("Guide","Get started with BizPulse")
        Panel {Text(if(role=="owner") "Business Owner" else "Cashier",style=MaterialTheme.typography.titleLarge); Text(if(role=="owner") "Use Inventory to add, edit, and remove products. Manage contains users, transactions, category settings, the daily target, backups, and audit history." else "View inventory, monitor stock, and complete sales in POS. Inventory changes and management tools require a Business Owner.")}
        Panel {Text("Key Features",style=MaterialTheme.typography.titleLarge); Text("1. Cashiers must Time In under My Shift before selling. Search, scan a camera barcode, or simulate a scan.\n2. Tap products to add quantities. Kiosk shows products by category.\n3. Review the cart; 12% VAT is added to product prices.\n4. Choose Cash or QR. Verify QR receipt in the payment account.\n5. Complete the sale, then print or save the receipt as a PDF.\n6. Review analytics and low-stock alerts.")}
        Panel {Text("Offline by design",style=MaterialTheme.typography.titleMedium); Text("Sales and accounts are stored locally in Room. Offline AI runs locally. Supplier maps need internet access. Uninstalling the app deletes its data; export backups regularly.")}
        Panel {Text("Demo credentials",style=MaterialTheme.typography.titleMedium); Text("owner / owner123\ncashier / cashier123\nana / ana123")}
        Panel {Text("About this app",style=MaterialTheme.typography.titleMedium); Text("BizPulse 2.0.0 • CT-MAD-213 Business Application. Native Kotlin implementation of the supplied web-prototype specification using Compose, Room, MVVM, and Hilt."); Text("This academic demo permits owner registration and uses the requested fixed-salt SHA-256 password scheme. VAT is a demonstration calculation. It does not process payments or issue certified tax invoices.")}
        PrimaryButton("Log Out",onClick=onLogout)
    }
}
