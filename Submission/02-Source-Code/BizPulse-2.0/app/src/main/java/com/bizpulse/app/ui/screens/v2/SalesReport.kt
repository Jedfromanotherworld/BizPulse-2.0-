package com.bizpulse.app.ui.screens.v2
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.util.*
import java.time.LocalDate
@Composable fun SalesReport(vm:OperationsViewModel=hiltViewModel()){
 val d by vm.data.collectAsStateWithLifecycle();val ui by vm.ui.collectAsStateWithLifecycle();val context=LocalContext.current
 var period by remember{mutableStateOf("Today")};var from by remember{mutableStateOf(DateUtils.today())};var to by remember{mutableStateOf(DateUtils.today())};var expanded by remember{mutableStateOf<Long?>(null)}
 val start=if(period=="Custom")from else LocalDate.now().minusDays(when(period){"7 Days"->6;"30 Days"->29;else->0}.toLong()).toString();val end=if(period=="Custom")to else DateUtils.today()
 val valid=runCatching{LocalDate.parse(start)<=LocalDate.parse(end)}.getOrDefault(false)
 val rows=if(valid)d.transactions.filter{it.transaction.date in start..end}else emptyList()
 ScreenHeader("Sales Report","Store log book");Tabs(listOf("Today","7 Days","30 Days","Custom"),period){period=it}
 if(period=="Custom"){Field("From YYYY-MM-DD",from,{from=it});Field("To YYYY-MM-DD",to,{to=it})}
 if(!valid)Text("Enter a valid date range",color=MaterialTheme.colorScheme.error)
 Panel{Text("Revenue: ${money(rows.sumOf{it.transaction.total})}");Text("${rows.size} transactions · ${rows.sumOf{it.transaction.items}} items");listOf("Cash","QR").forEach{pay->Text("$pay: ${money(rows.filter{it.transaction.payment==pay}.sumOf{it.transaction.total})}")}}
 Row{TextButton(enabled=valid&&!ui.busy,onClick={vm.runAction{context.startActivity(ReportExporter.export(context,rows,false,"$start to $end"))}}){Text("Export CSV")};TextButton(enabled=valid&&!ui.busy,onClick={vm.runAction{context.startActivity(ReportExporter.export(context,rows,true,"$start to $end"))}}){Text("Export PDF")}}
 Notice(ui,vm::dismissMessage)
 rows.groupBy{it.transaction.date}.toSortedMap(compareByDescending{it}).forEach{(date,sales)->
  Text(date,style=MaterialTheme.typography.titleMedium)
  sales.forEach{r->val t=r.transaction;Panel{
   Text("#${t.id} ${t.time} · ${t.cashierName}");Text("${t.payment}: ${money(t.total)}")
   TextButton(onClick={expanded=if(expanded==t.id)null else t.id}){Text("Receipt details")}
   if(expanded==t.id){if(t.manual)Text("Manual entry without receipt items");r.lineItems.forEach{l->Text("${l.productName}: ${l.quantity} × ${money(l.price)}");Text("Batch ${l.batchId?:"not recorded"}; received ${l.batchReceivedAt?.let{DateUtils.stamp(it)}?:"not recorded"}");Text("Age at sale: ${l.batchReceivedAt?.let{((t.createdAt-it)/86400000).coerceAtLeast(0).toString()+" days"}?:"not recorded"}")}}
  }}
 }
}
