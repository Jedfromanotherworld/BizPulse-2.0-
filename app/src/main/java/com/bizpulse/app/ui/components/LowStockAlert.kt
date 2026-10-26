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
@Composable fun LowStockAlert(names:List<String>) {
    if(names.isNotEmpty()) Surface(color=MaterialTheme.colorScheme.errorContainer,shape=InputShape) { Text("Low stock • "+names.take(3).joinToString()+if(names.size>3) " +${names.size-3} more" else "",Modifier.fillMaxWidth().padding(16.dp),color=MaterialTheme.colorScheme.onErrorContainer) }
}
