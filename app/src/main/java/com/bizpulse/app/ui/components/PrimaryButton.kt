package com.bizpulse.app.ui.components
import androidx.compose.runtime.*
import androidx.compose.ui.platform.testTag
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
@Composable fun PrimaryButton(text:String,enabled:Boolean=true,onClick:()->Unit) {
    Button(onClick=onClick,enabled=enabled,shape=ButtonShape,modifier=Modifier.fillMaxWidth().heightIn(min=48.dp).testTag("button-$text")) { Text(text) }
}
