package com.bizpulse.app.ui.screens.pos
import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.bizpulse.app.ui.components.*
import coil.compose.AsyncImage
import com.bizpulse.app.ui.theme.*
import com.bizpulse.app.data.model.CartLogic
import com.bizpulse.app.util.*
@Composable fun PosScreen(vm:PosViewModel=hiltViewModel()) {
    val d by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    val cart by vm.cart.collectAsStateWithLifecycle(); val receipt by vm.receipt.collectAsStateWithLifecycle()
    val active=d.shifts.any{it.cashierId==d.current?.id && it.timeOut==null}
    val context=LocalContext.current
    val scanner=rememberLauncherForActivityResult(ScanContract()) { result -> result.contents?.let(vm::scan) }
    fun launchScan() { scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES).setPrompt("Scan product barcode").setBeepEnabled(false).setOrientationLocked(false)) }
    val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted -> if(granted) launchScan() else vm.message("Camera permission denied. Search or enter the barcode instead.") }
    val matches=d.products.filter { it.name.contains(ui.search,true) || it.barcode.contains(ui.search) }
    val ranks=d.transactions.flatMap { it.lineItems }.groupBy { it.productId }.mapValues { e -> e.value.sumOf { it.quantity } }
    Page {
        ScreenHeader("Point of Sale","Add products, collect payment, issue a receipt")
        if(!active)Text("Time In under My Shift before completing a sale.",color=MaterialTheme.colorScheme.error)
        Field("Search name or barcode",ui.search,vm::search)
        Tabs(listOf("All")+d.categories.map{it.name},ui.tab.ifEmpty{"All"},vm::tab)
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically,horizontalArrangement=Arrangement.SpaceBetween) { Text(if(ui.quick)"Kiosk Mode" else "Scan Mode"); Switch(checked=ui.quick,onCheckedChange=vm::quick) }
        Notice(ui,vm::dismissMessage)
        if(ui.quick) {
            val quick=matches.filter{ui.tab.isBlank()||ui.tab=="All"||d.categories.find{c->c.id==it.categoryId}?.name==ui.tab}
            if(quick.isEmpty()) EmptyState("No products in stock")
            quick.chunked(2).forEach { row -> Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { row.forEach { p ->
                Card(onClick={vm.add(p)},enabled=p.stock>0 && !ui.busy,modifier=Modifier.weight(1f),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { if(p.imageUrl!=null)AsyncImage(model=java.io.File(p.imageUrl),contentDescription=p.name,modifier=Modifier.height(70.dp))else Text(p.name.take(1),style=MaterialTheme.typography.headlineLarge); Text(p.name); Text(money(p.price),color=MaterialTheme.colorScheme.primary); Text("${p.stock} available",style=MaterialTheme.typography.labelSmall) } }
            }; if(row.size==1) Spacer(Modifier.weight(1f)) } }
        } else {
            Field("Search name or barcode",ui.search,vm::search)
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick={if(ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED) launchScan() else permission.launch(Manifest.permission.CAMERA)},modifier=Modifier.weight(1f)) { Text("Scan barcode") }
                OutlinedButton(onClick=vm::simulate,modifier=Modifier.weight(1f)) { Text("Simulate scan") }
            }
            OutlineButton("Enter barcode manually"){vm.dialog("manual")}
            Column(Modifier.heightIn(max=300.dp).verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                if(matches.isEmpty()) EmptyState("No matching products")
                matches.forEach { p -> Card(onClick={vm.add(p)},enabled=p.stock>0 && !ui.busy,modifier=Modifier.fillMaxWidth(),colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(16.dp)) { Text(p.name); Text("${money(p.price)} • ${p.stock} in stock",color=if(p.stock==0) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary) } } }
            }
        }
        Surface(color=Secondary,shape=CardShape) {
            Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) {
                Text("Cart • ${cart.sumOf { it.quantity }} items",style=MaterialTheme.typography.titleMedium,color=Color.White)
                if(cart.isEmpty()) Text("Tap a product to add it.",color=Color(0xFFCBD5E1))
                cart.forEach { item -> CartRow(item) { vm.remove(item.product.id) } }
                Text("Subtotal: ${money(CartLogic.subtotal(cart))}",color=Color.White)
                Text("VAT (12%): ${money(CartLogic.vat(cart))}",color=Color.White)
                Text("Total: ${money(CartLogic.total(cart))}",style=MaterialTheme.typography.titleLarge,color=Primary)
                PrimaryButton("Complete Sale",active && cart.isNotEmpty() && !ui.busy) {vm.dialog("checkout",fields=mapOf("payment" to "Cash"))}
                TextButton(onClick=vm::clearCart,enabled=cart.isNotEmpty() && !ui.busy) {Text("Clear cart",color=Primary)}
            }
        }
    }
    if(ui.dialog=="manual")FormDialog("Enter barcode",ui,vm::close,{vm.scan(vm.value("barcode"));vm.close()}){Field("Barcode",vm.value("barcode"),{vm.field("barcode",it)})}
    if(ui.dialog=="checkout") CheckoutDialog(vm)
    if(ui.dialog=="receipt" && receipt!=null) ReceiptDialog(receipt!!,vm::dismissReceipt,vm::message)
}
