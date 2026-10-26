package com.bizpulse.app.ui.screens.manage
import androidx.compose.runtime.*
import androidx.compose.material3.Text
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
@Composable fun TransactionEditDialog(vm:ManageViewModel) {
    val ui by vm.ui.collectAsStateWithLifecycle(); val d by vm.data.collectAsStateWithLifecycle()
    val locked=d.transactions.find {it.transaction.id==ui.id}?.lineItems?.isNotEmpty()==true
    FormDialog(if(ui.id==0L) "Add Transaction" else "Edit Transaction",ui,vm::close,vm::saveTransaction) {
        Text(if(locked) "POS receipts are locked to preserve batch and shift history." else "Manual entries don't include line items. Cost is assumed zero; stock is unchanged.")
        listOf("date" to "Date (YYYY-MM-DD)","time" to "Time (HH:mm)","total" to "Total (including VAT)","items" to "Items").forEach { (key,label) -> Field(label,ui.fields[key] ?: "",{vm.field(key,it)},numeric=key in listOf("total","items"),enabled=!locked) }
        if(!locked)Choice("Payment",listOf("Cash","QR").map {it to it},ui.fields["payment"] ?: "Cash",vm) {vm.field("payment",it)}
        if(!locked)Choice("Cashier",d.users.map {it.id.toString() to it.name},ui.fields["cashierId"] ?: "",vm) {vm.field("cashierId",it)}
    }
}
