package com.bizpulse.app.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
val BizTypography = Typography(
    headlineLarge=TextStyle(fontSize=24.sp,fontWeight=FontWeight.Bold),
    headlineMedium=TextStyle(fontSize=20.sp,fontWeight=FontWeight.SemiBold),
    titleLarge=TextStyle(fontSize=20.sp,fontWeight=FontWeight.SemiBold),
    titleMedium=TextStyle(fontSize=16.sp,fontWeight=FontWeight.SemiBold),
    bodyLarge=TextStyle(fontSize=14.sp,lineHeight=21.sp),
    bodyMedium=TextStyle(fontSize=14.sp,lineHeight=20.sp),
    labelSmall=TextStyle(fontSize=11.sp,fontWeight=FontWeight.Medium),
    labelLarge=TextStyle(fontSize=14.sp,fontWeight=FontWeight.SemiBold)
)
