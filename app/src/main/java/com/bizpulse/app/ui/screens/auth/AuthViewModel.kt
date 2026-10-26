package com.bizpulse.app.ui.screens.auth
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.ui.screens.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel class AuthViewModel @Inject constructor(store: AppStore, private val users: UserRepository): ScreenViewModel(store) {
    fun submit() = runAction {
        val role=value("role","owner")
        if(ui.value.tab=="Register") {
            require(value("password")==value("confirm")) { "Passwords do not match" }
            users.save(0,value("name"),value("username"),value("password"),role,true)
            tab("Log In"); field("password",""); field("confirm",""); message("Account created. Log in to continue.")
        } else users.login(value("username"),value("password"),role)
    }
    fun logout() = runAction { users.logout() }
}
