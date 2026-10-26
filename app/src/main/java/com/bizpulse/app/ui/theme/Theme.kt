package com.bizpulse.app.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val Light=lightColorScheme(primary=Primary,onPrimary=Secondary,secondary=Secondary,background=Background,surface=Surface,onSurface=TextPrimary,onBackground=TextPrimary,error=Danger,outline=Border,surfaceVariant=CardBorder,onSurfaceVariant=TextSecondary)
private val Dark=darkColorScheme(primary=Primary,onPrimary=Secondary,secondary=Color(0xFF94A3B8),background=Color(0xFF0B1220),surface=Color(0xFF162235),onSurface=Color(0xFFF1F5F9),onBackground=Color(0xFFF1F5F9),error=Color(0xFFFF8A8A),outline=Color(0xFF3A4A60),surfaceVariant=Color(0xFF243348),onSurfaceVariant=Color(0xFFCBD5E1))
@Composable fun BizPulseTheme(darkTheme:Boolean=isSystemInDarkTheme(),content:@Composable ()->Unit) {
    MaterialTheme(colorScheme=if(darkTheme) Dark else Light,typography=BizTypography,shapes=BizShapes,content=content)
}
