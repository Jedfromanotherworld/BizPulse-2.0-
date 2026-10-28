package com.bizpulse.app.ui.components
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.bizpulse.app.ui.theme.*
import com.bizpulse.app.util.*
@Composable fun ConfirmDialog(title:String,text:String,onDismiss:()->Unit,onConfirm:()->Unit,busy:Boolean=false,confirmLabel:String="Confirm",error:String?=null) {
    AlertDialog(onDismissRequest={ if(!busy) onDismiss() },title={ Text(title) },text={ Column(verticalArrangement=Arrangement.spacedBy(8.dp)) {Text(text);error?.let {Text(it,color=MaterialTheme.colorScheme.error)}} },
        confirmButton={ TextButton(onClick=onConfirm,enabled=!busy) { Text(if(busy) "Please wait…" else confirmLabel) } },
        dismissButton={ TextButton(onClick=onDismiss,enabled=!busy) { Text("Cancel") } })
}
