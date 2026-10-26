package com.bizpulse.app.ui.screens.manage
import android.net.Uri
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.ui.screens.ScreenViewModel
import com.bizpulse.app.ui.session.SessionManager
import com.bizpulse.app.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel class ManageViewModel @Inject constructor(store: AppStore, private val users: UserRepository, private val transactions: TransactionRepository, private val categories: CategoryRepository, val backup: BackupManager, private val session: SessionManager): ScreenViewModel(store) {
    fun editUser(u: UserEntity?=null) = dialog("user",u?.id ?: 0,mapOf("name" to (u?.name ?: ""),"username" to (u?.username ?: ""),"role" to (u?.role ?: "cashier")))
    fun saveUser()=runAction("User saved",true) { users.save(ui.value.id,value("name"),value("username"),value("password"),value("role","cashier")) }
    fun deleteUser()=runAction("User deleted",true) { users.delete(data.value.users.find { it.id==ui.value.id } ?: error("User no longer exists")) }
    fun editTransaction(t: TransactionEntity?=null) = dialog("transaction",t?.id ?: 0,mapOf("date" to (t?.date ?: DateUtils.today()),"time" to (t?.time ?: DateUtils.time()),"total" to (t?.total?.toString() ?: ""),"items" to (t?.items?.toString() ?: "1"),"payment" to (t?.payment ?: "Cash"),"cashierId" to (t?.cashierId ?: data.value.current?.id ?: 0).toString()))
    fun saveTransaction()=runAction("Transaction saved",true) {
        val old=data.value.transactions.find { it.transaction.id==ui.value.id }?.transaction
        transactions.save(TransactionEntity(ui.value.id,value("date"),value("time"),value("total").toDoubleOrNull() ?: error("Invalid total"),value("items").toIntOrNull() ?: error("Invalid item count"),value("payment","Cash"),"",value("cashierId").toLongOrNull() ?: 0,old?.cost ?: 0.0,0.0,old?.createdAt ?: System.currentTimeMillis(),old?.manual ?: true,old?.tendered ?: 0.0))
    }
    fun deleteTransaction()=runAction("Transaction deleted and available stock restored",true) { transactions.delete(data.value.transactions.find { it.transaction.id==ui.value.id }?.transaction ?: error("Transaction no longer exists")) }
    fun editCategory(c:CategoryEntity?=null)=dialog("category",c?.id ?: 0,mapOf("name" to (c?.name ?: ""),"description" to (c?.description ?: "")))
    fun saveCategory()=runAction("Category saved",true) { categories.save(CategoryEntity(ui.value.id,value("name").trim(),value("description").trim())) }
    fun deleteCategory()=runAction("Category deleted",true) { categories.delete(data.value.categories.find { it.id==ui.value.id } ?: error("Category no longer exists")) }
    fun setTarget()=runAction("Daily target saved",true) { users.requireOwner(); val n=value("target").toDoubleOrNull() ?: error("Invalid target"); require(Validation.price(n)) { "Enter a positive target" }; session.setTarget(n) }
    fun export(onReady:(Uri)->Unit)=runAction { onReady(backup.export()) }
    fun importBackup()=runAction { backup.import(Uri.parse(value("uri"))); close() }
    fun reset()=runAction { backup.reset(); close() }
}
