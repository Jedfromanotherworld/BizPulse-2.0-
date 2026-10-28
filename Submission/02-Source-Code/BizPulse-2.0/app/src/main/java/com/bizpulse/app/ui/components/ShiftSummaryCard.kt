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
@Composable fun ShiftSummaryCard(count:Int,revenue:Double,items:Int) {
    Box(Modifier.fillMaxWidth().background(androidx.compose.ui.graphics.Brush.linearGradient(listOf(Success,PrimaryDark)),CardShape).padding(20.dp)) {
        Column(verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Your shift today",color=Color.White,fontWeight=FontWeight.Bold); Text("You've processed $count transactions worth ${money(revenue)} with $items items sold",color=Color.White) }
    }
}
