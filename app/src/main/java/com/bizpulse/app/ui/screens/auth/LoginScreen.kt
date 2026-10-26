package com.bizpulse.app.ui.screens.auth
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
@Composable fun LoginScreen(vm:AuthViewModel=hiltViewModel()) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    val data by vm.data.collectAsStateWithLifecycle()
    val register=ui.tab=="Register"
    Page {
        Spacer(Modifier.height(16.dp))
        Column(Modifier.fillMaxWidth(),horizontalAlignment=Alignment.CenterHorizontally) {
            androidx.compose.foundation.Image(androidx.compose.ui.res.painterResource(com.bizpulse.app.R.drawable.bizpulse_logo),"BizPulse logo",Modifier.width(240.dp).height(140.dp),contentScale=androidx.compose.ui.layout.ContentScale.Fit)
            Text("BizPulse 2",style=MaterialTheme.typography.headlineLarge)
            Text("Smart Business Management v2.0",color=MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Panel {
            Tabs(listOf("Log In","Register"),if(register) "Register" else "Log In",vm::tab)
            Choice("Role",listOf("owner" to "Business Owner","cashier" to "Cashier"),ui.fields["role"] ?: "owner",vm) { vm.field("role",it) }
            if(register) Field("Full Name",ui.fields["name"] ?: "",{vm.field("name",it)})
            Field("Username",ui.fields["username"] ?: "",{vm.field("username",it)})
            Field("Password",ui.fields["password"] ?: "",{vm.field("password",it)},password=true,visible=ui.passwordVisible,onToggle=vm::togglePassword)
            if(register) {
                Text("At least 6 characters",style=MaterialTheme.typography.labelSmall)
                Field("Confirm Password",ui.fields["confirm"] ?: "",{vm.field("confirm",it)},password=true,visible=ui.passwordVisible,onToggle=vm::togglePassword)
                Text("Academic demo: both roles can self-register.",style=MaterialTheme.typography.labelSmall)
            }
            Notice(ui,vm::dismissMessage)
            data.error?.let { Text(it,color=MaterialTheme.colorScheme.error) }
            PrimaryButton(if(register) "Create Account" else "Log In",enabled=!ui.busy && data.ready && data.error==null,onClick=vm::submit)
        }
        Panel {
            Text("Try the demo",style=MaterialTheme.typography.titleMedium)
            Text("Owner: owner / owner123\nCashier: cashier / cashier123\nCashier: ana / ana123")
            Text("Works offline • Stored on this device",style=MaterialTheme.typography.labelSmall)
        }
    }
}
