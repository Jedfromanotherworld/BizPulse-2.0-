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
@Composable fun InsightCard(text:String) {
    Surface(shape=CardShape,color=Color(0xFFDBEAFE)) { Column(Modifier.fillMaxWidth().padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp)) { Text("Business Insight",fontWeight=FontWeight.Bold,color=Color(0xFF1E40AF)); Text(text,color=Color(0xFF1E3A8A)) } }
}
