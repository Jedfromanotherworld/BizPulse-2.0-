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
@Composable fun CartRow(item:com.bizpulse.app.data.model.CartItem,onRemove:()->Unit) {
    Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) { Text(item.product.name,color=Color.White); Text("${item.quantity} × ${money(item.product.price)}",color=Color(0xFFCBD5E1)) }
        Text(money(item.product.price*item.quantity),color=Color.White)
        TextButton(onClick=onRemove) { Text("×",color=Color.White) }
    }
}
