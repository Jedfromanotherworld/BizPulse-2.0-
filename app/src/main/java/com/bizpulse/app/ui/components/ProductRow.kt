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
@Composable fun ProductRow(product:com.bizpulse.app.data.db.entity.ProductEntity,category:String,owner:Boolean,onEdit:()->Unit,onDelete:()->Unit) {
    Panel {
        Text(product.name,style=MaterialTheme.typography.titleMedium)
        Text("${product.barcode} • $category",style=MaterialTheme.typography.labelSmall,color=MaterialTheme.colorScheme.onSurfaceVariant)
        Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.SpaceBetween) {
            Text(money(product.price),fontWeight=FontWeight.Bold)
            Text("${product.stock} in stock",color=if(product.stock<=product.minAlert) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
        }
        Surface(color=Color(0xFFDBEAFE),shape=ButtonShape) {Text("${ProfitCalculator.margin(product.price,product.cost).toInt()}% margin",Modifier.padding(horizontal=10.dp,vertical=5.dp),style=MaterialTheme.typography.labelSmall,color=Color(0xFF1E40AF))}
        if(owner) Row { TextButton(onClick=onEdit) { Text("Edit") }; TextButton(onClick=onDelete) { Text("Delete",color=MaterialTheme.colorScheme.error) } }
    }
}
