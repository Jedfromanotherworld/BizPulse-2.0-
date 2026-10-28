package com.bizpulse.app.ui.screens
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bizpulse.app.data.repository.AppStore
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
data class ScreenUi(val tab: String = "", val search: String = "", val dialog: String = "", val id: Long = 0,
    val fields: Map<String,String> = emptyMap(), val busy: Boolean = false, val message: String? = null,
    val passwordVisible: Boolean = false, val quick: Boolean = false, val selection: String = "")
open class ScreenViewModel(store: AppStore): ViewModel() {
    val data=store.data
    protected val mutableUi=MutableStateFlow(ScreenUi())
    val ui: StateFlow<ScreenUi> = mutableUi.asStateFlow()
    fun field(key: String,value: String) { mutableUi.update { it.copy(fields=it.fields+(key to value)) } }
    fun value(key: String,default: String=""): String = ui.value.fields[key] ?: default
    fun tab(value: String) { mutableUi.update { it.copy(tab=value) } }
    fun search(value: String) { mutableUi.update { it.copy(search=value) } }
    fun select(value: String) { mutableUi.update { it.copy(selection=value) } }
    fun togglePassword() { mutableUi.update { it.copy(passwordVisible=!it.passwordVisible) } }
    fun quick(value: Boolean) { mutableUi.update { it.copy(quick=value) } }
    fun dialog(name: String,id: Long=0,fields: Map<String,String> = emptyMap()) { mutableUi.update { it.copy(dialog=name,id=id,fields=fields,message=null) } }
    fun close() { if(!ui.value.busy) mutableUi.update { it.copy(dialog="",message=null) } }
    fun dismissMessage() { mutableUi.update { it.copy(message=null) } }
    fun message(value: String) { mutableUi.update { it.copy(message=value) } }
    fun runAction(success: String? = null, close: Boolean = false, action: suspend () -> Unit) {
        if(ui.value.busy) return
        mutableUi.update { it.copy(busy=true,message=null) }
        viewModelScope.launch {
            try { action(); mutableUi.update { it.copy(message=success,dialog=if(close) "" else it.dialog) } }
            catch(e: CancellationException) { throw e }
            catch(e: Exception) { mutableUi.update { it.copy(message=e.message ?: "Operation failed. Please try again.") } }
            finally { mutableUi.update { it.copy(busy=false) } }
        }
    }
}
