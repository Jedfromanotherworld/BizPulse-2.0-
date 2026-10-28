package com.bizpulse.app.ui.screens.manage
import androidx.compose.runtime.*
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
@Composable fun UserEditDialog(vm:ManageViewModel) {
    val ui by vm.ui.collectAsStateWithLifecycle()
    FormDialog(if(ui.id==0L) "Add User" else "Edit User",ui,vm::close,vm::saveUser) {
        Field("Full Name",ui.fields["name"] ?: "",{vm.field("name",it)})
        Field("Username",ui.fields["username"] ?: "",{vm.field("username",it)})
        Field(if(ui.id==0L) "Password (min 6 characters)" else "Password (blank = keep current)",ui.fields["password"] ?: "",{vm.field("password",it)},password=true,visible=ui.passwordVisible,onToggle=vm::togglePassword)
        Choice("Role",listOf("owner" to "Business Owner","cashier" to "Cashier"),ui.fields["role"] ?: "cashier",vm) {vm.field("role",it)}
    }
}
