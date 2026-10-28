package com.bizpulse.app.ui.screens.v2
import android.content.Intent
import android.net.Uri
import android.Manifest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bizpulse.app.data.repository.AppData
import com.bizpulse.app.data.db.entity.SupplierEntity
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.util.*
import kotlinx.coroutines.delay
import java.time.*

@Composable fun ReceiveStock(vm:OperationsViewModel=hiltViewModel()) {
 val d by vm.data.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle()
 if(d.current?.role!="owner")return
 OutlineButton("Receive Stock"){vm.dialog("receive",fields=mapOf("productId" to (d.products.firstOrNull()?.id?:0).toString(),"supplier" to (d.suppliers.firstOrNull()?.name?:"")))}
 Notice(ui,vm::dismissMessage)
 if(ui.dialog=="receive")FormDialog("Receive Stock",ui,vm::close,vm::receive){
  Choice("Product",d.products.map{it.id.toString() to it.name},vm.value("productId"),vm){vm.field("productId",it)}
  Choice("Supplier",d.suppliers.map{it.name to it.name},vm.value("supplier"),vm){vm.field("supplier",it)}
  Field("Quantity",vm.value("quantity"),{vm.field("quantity",it)},numeric=true)
  Field("Cost per unit",vm.value("cost"),{vm.field("cost",it)},numeric=true)
  Field("Expiry YYYY-MM-DD (optional)",vm.value("expiry"),{vm.field("expiry",it)})
 }
}
@Composable fun AiPanel(vm:OperationsViewModel=hiltViewModel()) {
 val d by vm.data.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle();val insight by vm.insight.collectAsStateWithLifecycle();val context=LocalContext.current
 val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){vm.message(if(it)"Notifications enabled" else "Notifications disabled")}
 if(d.current?.role!="owner")return
 Panel {
  Text("Offline AI Log Book",style=MaterialTheme.typography.titleMedium)
  Text("Qwen2.5 runs on this device. Check its suggestions against recorded figures.")
  PrimaryButton(if(ui.busy)"Generating…" else "Generate insight",!ui.busy){vm.generate()}
  if(insight.isNotBlank())Text(insight)
  val prefs=context.getSharedPreferences("insights",0)
  prefs.getString("daily",null)?.let{Text("Latest daily summary: ${DateUtils.stamp(prefs.getLong("generated",0))}");Text(it)}
  Text("Daily summaries are scheduled around 8 AM. Android may delay background work.")
  if(android.os.Build.VERSION.SDK_INT>=33)TextButton(onClick={permission.launch(Manifest.permission.POST_NOTIFICATIONS)}){Text("Enable notifications")}
  Notice(ui,vm::dismissMessage)
 }
}
@Composable fun HealthPanel(d:AppData,vm:OperationsViewModel=hiltViewModel()) {
 if(d.current?.role!="owner")return
 val result by vm.healthExplanation.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle()
 Panel {
  Text("Store stock health: ${OperationsViewModel.health(d)}",style=MaterialTheme.typography.titleMedium)
  Text("${d.products.count{it.stock==0}} out of stock; ${d.products.count{it.stock<=it.minAlert}} at or below alert level.")
  TextButton(onClick=vm::explainHealth,enabled=!ui.busy){Text("Explain with offline AI")}
  if(result.isNotBlank())Text(result)
  Notice(ui,vm::dismissMessage)
 }
}
@Composable fun QrSettings(vm:OperationsViewModel=hiltViewModel()) {
 val context=LocalContext.current;val ui by vm.ui.collectAsStateWithLifecycle()
 val picker=rememberLauncherForActivityResult(ActivityResultContracts.GetContent()){it?.let(vm::qr)}
 Panel {
  Text("Payment QR",style=MaterialTheme.typography.titleMedium)
  AsyncImage(model=MediaFiles.qr(context),contentDescription="Store payment QR",modifier=Modifier.fillMaxWidth().height(260.dp).background(androidx.compose.ui.graphics.Color.White).padding(20.dp))
  Text("Cashiers must verify receipt in the payment account before confirming a QR sale.")
  PrimaryButton("Choose QR image",!ui.busy){picker.launch("image/*")}
  Notice(ui,vm::dismissMessage)
 }
}
@Composable fun ShiftScreen(owner:Boolean=false,filterUser:Long?=null,vm:OperationsViewModel=hiltViewModel()) {
 val d by vm.data.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle()
 var now by remember{mutableLongStateOf(System.currentTimeMillis())}
 LaunchedEffect(Unit){while(true){now=System.currentTimeMillis();delay(1000)}}
 val isOwner=d.current?.role=="owner"
 val rows=d.shifts.filter{if(isOwner&&owner)filterUser==null||it.cashierId==filterUser else it.cashierId==d.current?.id}
 val content:@Composable ColumnScope.()->Unit={
  ScreenHeader(if(isOwner)"Cashier Shifts" else "My Shift",DateUtils.stamp(now));Notice(ui,vm::dismissMessage)
  if(!isOwner && rows.none{it.timeOut==null})PrimaryButton("Time In",!ui.busy){vm.start()}
  if(rows.isEmpty())EmptyState("No shifts recorded")
  rows.forEach {s->
   val sales=d.transactions.filter{it.transaction.shiftId==s.id}
   Panel {
    Text(s.cashierName,style=MaterialTheme.typography.titleMedium)
    Text("In: ${DateUtils.stamp(s.timeIn)}");Text("Out: ${s.timeOut?.let{DateUtils.stamp(it)}?:"On shift"}")
    Text("Duration: ${shiftDuration(s.timeIn,s.timeOut?:now)}")
    Text("${sales.size} transactions · ${money(sales.sumOf{it.transaction.total})}")
    listOf("Cash","QR").forEach{pay->Text("$pay: ${money(sales.filter{it.transaction.payment==pay}.sumOf{it.transaction.total})}")}
    Text("Items: ${sales.sumOf{it.transaction.items}} · Average: ${money(if(sales.isEmpty())0.0 else sales.sumOf{it.transaction.total}/sales.size)}")
    sales.take(5).forEach{Text("#${it.transaction.id} ${it.transaction.time} ${money(it.transaction.total)}")}
    if(s.timeOut==null)OutlinedButton(onClick={vm.dialog("endShift",s.id)},enabled=!ui.busy){Text(if(isOwner)"Force Time Out" else "Time Out")}
   }
  }
 }
 if(owner)Column(verticalArrangement=Arrangement.spacedBy(12.dp),content=content)else Page(content)
 if(ui.dialog=="endShift")ConfirmDialog("End this shift?","New sales require another Time In.",vm::close,{val id=ui.id;vm.close();vm.end(id)},ui.busy,"Time Out",ui.message)
}
fun shiftDuration(start:Long,end:Long):String{val seconds=(end-start).coerceAtLeast(0)/1000;return "%02d:%02d:%02d".format(seconds/3600,seconds/60%60,seconds%60)}
@Composable fun SupplierScreen(vm:OperationsViewModel=hiltViewModel()) {
 val d by vm.data.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle();val owner=d.current?.role=="owner"
 var map by remember{mutableStateOf<SupplierEntity?>(null)}
 Page {
  ScreenHeader("Suppliers","Store addresses and geographic maps");Notice(ui,vm::dismissMessage)
  if(owner)PrimaryButton("Add supplier"){vm.edit()}
  if(d.suppliers.isEmpty())EmptyState("No suppliers")
  d.suppliers.forEach{s->
   Panel {
    Text(s.name,style=MaterialTheme.typography.titleMedium);Text(s.address)
    if(s.ipAddress.isNotBlank())Text("Network address: ${s.ipAddress}")
    OutlinedButton(onClick={map=s}){Text("View geographic map")}
    if(owner)Row {
     TextButton(onClick={vm.edit(s)}){Text("Edit")}
     TextButton(onClick={vm.dialog("deleteSupplier",s.id)}){Text("Delete")}
    }
   }
  }
 }
 if(ui.dialog=="supplier")FormDialog("Supplier",ui,vm::close,vm::save){
  listOf("name" to "Name","address" to "Address","ip" to "IPv4 address (optional)","lat" to "Latitude","lon" to "Longitude").forEach{(key,label)->Field(label,vm.value(key),{vm.field(key,it)})}
 }
 if(ui.dialog=="deleteSupplier")ConfirmDialog("Delete supplier?","Past batch supplier names remain saved.",vm::close,vm::delete,ui.busy,"Delete",ui.message)
 map?.let{s->MapDialog(s){map=null}}
}
@Composable private fun MapDialog(s:SupplierEntity,close:()->Unit) {
 val context=LocalContext.current;val lat=s.latitude;val lon=s.longitude
 val url="https://www.openstreetmap.org/export/embed.html?bbox=${lon-0.008},${lat-0.006},${lon+0.008},${lat+0.006}&layer=mapnik&marker=$lat,$lon"
 AlertDialog(onDismissRequest=close,title={Text(s.name)},text={Column {
  Text("Map tiles need internet. Addresses remain available offline.")
  AndroidView(factory={c->WebView(c).apply{webViewClient=WebViewClient();settings.javaScriptEnabled=true;settings.allowFileAccess=false;settings.allowContentAccess=false;loadUrl(url)}},modifier=Modifier.fillMaxWidth().height(310.dp))
  Text("© OpenStreetMap contributors")
 }},confirmButton={TextButton(onClick={context.startActivity(Intent(Intent.ACTION_VIEW,Uri.parse("https://www.openstreetmap.org/?mlat=$lat&mlon=$lon#map=17/$lat/$lon")))}){Text("Open maps")}},dismissButton={TextButton(onClick=close){Text("Close")}})
}
