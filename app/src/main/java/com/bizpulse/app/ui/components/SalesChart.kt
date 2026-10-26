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
@Composable fun SalesChart(values:List<Pair<String,Double>>,selection:String,onSelect:(String)->Unit) {
    Panel {
        Text("Last 7 days",style=MaterialTheme.typography.titleMedium)
        val max=values.maxOfOrNull { it.second }?.coerceAtLeast(1.0) ?: 1.0
        Row(Modifier.fillMaxWidth().height(150.dp),horizontalArrangement=Arrangement.spacedBy(8.dp),verticalAlignment=Alignment.Bottom) {
            values.forEach { (day,value) ->
                Column(Modifier.weight(1f).clickable { onSelect("$day • ${money(value)}") },horizontalAlignment=Alignment.CenterHorizontally,verticalArrangement=Arrangement.spacedBy(8.dp)) {
                    Box(Modifier.fillMaxWidth().height((value/max*110).coerceAtLeast(4.0).dp).background(Primary,RoundedCornerShape(6.dp)))
                    Text(day.takeLast(5),style=MaterialTheme.typography.labelSmall)
                }
            }
        }
        Text(selection.ifEmpty { "Tap a bar to view revenue" },style=MaterialTheme.typography.labelSmall)
    }
}
