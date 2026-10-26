package com.bizpulse.app.ui.screens.pos
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.bizpulse.app.data.db.relation.TransactionWithItems
import com.bizpulse.app.util.ReceiptPrinter
@Composable fun ReceiptDialog(receipt:TransactionWithItems,onDismiss:()->Unit,onError:(String)->Unit) {
    val context=LocalContext.current
    AlertDialog(onDismissRequest=onDismiss,title={Text("Sale completed")},text={Column(Modifier.verticalScroll(rememberScrollState())) {ReceiptPrinter.lines(receipt).forEach { Text(it) }}},
        confirmButton={TextButton(onClick={try {ReceiptPrinter.print(context,receipt)} catch(e:Exception) {onError(e.message ?: "Printing unavailable")}}) {Text("Print")}},
        dismissButton={TextButton(onClick=onDismiss) {Text("Done")}})
}
