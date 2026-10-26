package com.bizpulse.app
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.bizpulse.app.ui.navigation.AppNavigation
import com.bizpulse.app.ui.theme.BizPulseTheme
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint class MainActivity:ComponentActivity() {
    override fun onCreate(savedInstanceState:Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {BizPulseTheme {AppNavigation()}}
    }
}
