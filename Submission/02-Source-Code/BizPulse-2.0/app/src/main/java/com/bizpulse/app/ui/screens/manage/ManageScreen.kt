package com.bizpulse.app.ui.screens.manage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.ui.screens.v2.*
import com.bizpulse.app.util.*
@Composable fun ManageScreen(onLogout:()->Unit,vm:ManageViewModel=hiltViewModel()) {
    val d by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    var shiftUser by remember{mutableStateOf<Long?>(null)}
    val context=LocalContext.current
    val picker=rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {uri -> if(uri!=null) vm.dialog("import",fields=mapOf("uri" to uri.toString()))}
    if(d.current?.role!="owner") { Page { EmptyState("Restricted Access","Only a Business Owner can manage this app."); PrimaryButton("Log Out",onClick=onLogout) }; return }
    val tab=ui.tab.ifEmpty { "Users" }
    Page {
        ScreenHeader("Manage","Business settings and records")
        Tabs(listOf("Users","Sales Report","Shifts","QR Settings","Transactions","Audit Log","Backup","Categories"),tab,vm::tab)
        Notice(ui,vm::dismissMessage)
        when(tab) {
            "Sales Report" -> SalesReport()
            "Shifts" -> ShiftScreen(owner=true)
            "QR Settings" -> QrSettings()
            "Users" -> {
                PrimaryButton("Add User (${d.users.size})",onClick={vm.editUser()})
                d.users.forEach {u -> Panel { Text(u.name,style=MaterialTheme.typography.titleMedium); RoleBadge(u.role); Text("@${u.username}"); if(u.role=="cashier")TextButton(onClick={shiftUser=u.id}){Text("View Shifts")}; Row {TextButton(onClick={vm.editUser(u)}) {Text("Edit")}; TextButton(onClick={vm.dialog("deleteUser",u.id)}) {Text("Delete")}} } }
            }
            "Transactions" -> {
                PrimaryButton("Add Transaction (${d.transactions.size})",onClick={vm.editTransaction()})
                if(d.transactions.isEmpty()) EmptyState("No transactions")
                d.transactions.forEach {row -> val t=row.transaction; Panel {Text("#${t.id} • ${money(t.total)}",style=MaterialTheme.typography.titleMedium); Text("${t.date} ${t.time} • ${t.items} items • ${t.payment}"); Text("${t.cashierName} • ${row.lineItems.size} product rows"); Row {TextButton(onClick={vm.editTransaction(t)}) {Text("Edit")}; TextButton(onClick={vm.dialog("deleteTransaction",t.id)}) {Text("Delete")}} } }
            }
            "Audit Log" -> {
                Field("Search action, details or user",ui.search,vm::search)
                val logs=d.logs.filter {it.action.contains(ui.search,true) || it.details.contains(ui.search,true) || it.userName.contains(ui.search,true)}
                Text("${logs.size} of ${d.logs.size} entries")
                if(logs.isEmpty()) EmptyState("No matching audit entries")
                logs.forEach {log -> Panel {Text("● ${log.action}",style=MaterialTheme.typography.titleMedium,color=when(log.type){"delete"->MaterialTheme.colorScheme.error;"sale"->MaterialTheme.colorScheme.primary;else->MaterialTheme.colorScheme.onSurface}); Text(log.details); Text("${log.userName} • ${DateUtils.stamp(log.timestamp)}",style=MaterialTheme.typography.labelSmall)} }
            }
            "Categories" -> {
                PrimaryButton("Add Category",onClick={vm.editCategory()})
                d.categories.forEach {c -> Panel {Text(c.name,style=MaterialTheme.typography.titleMedium); if(c.description.isNotEmpty()) Text(c.description); Row {TextButton(onClick={vm.editCategory(c)}) {Text("Edit")}; TextButton(onClick={vm.dialog("deleteCategory",c.id)}) {Text("Delete")}} } }
            }
            "Backup" -> {
                Panel {Text("Daily Sales Target",style=MaterialTheme.typography.titleMedium); Text(money(d.target)); OutlineButton("Change target") {vm.dialog("target",fields=mapOf("target" to d.target.toString()))}}
                Panel {Text("Export Data",style=MaterialTheme.typography.titleMedium); Text("Create a JSON backup of all users, products, categories, transactions, receipt items, and audit entries. It contains password hashes; keep it private."); PrimaryButton("Export JSON",!ui.busy) {vm.export {uri -> context.startActivity(vm.backup.share(uri))}}}
                Panel {Text("Import Data",style=MaterialTheme.typography.titleMedium); Text("Replace the database with a validated BizPulse JSON backup. You will be logged out."); OutlineButton("Choose JSON backup",!ui.busy) {picker.launch(arrayOf("application/json","text/plain","application/octet-stream"))}}
                Panel {Text("Danger Zone",color=MaterialTheme.colorScheme.error,style=MaterialTheme.typography.titleMedium); Text("Reset removes current records and restores demo data."); OutlineButton("Reset All Data",!ui.busy) {vm.dialog("resetFirst")}}
            }
        }
    }
    shiftUser?.let{id->AlertDialog(onDismissRequest={shiftUser=null},title={Text("Cashier shifts")},text={Column(Modifier.heightIn(max=450.dp).verticalScroll(rememberScrollState())){ShiftScreen(owner=true,filterUser=id)}},confirmButton={TextButton(onClick={shiftUser=null}){Text("Close")}})}
    when(ui.dialog) {
        "user" -> UserEditDialog(vm)
        "transaction" -> TransactionEditDialog(vm)
        "category" -> FormDialog(if(ui.id==0L) "Add Category" else "Edit Category",ui,vm::close,vm::saveCategory) {Field("Name",ui.fields["name"] ?: "",{vm.field("name",it)}); Field("Description",ui.fields["description"] ?: "",{vm.field("description",it)})}
        "target" -> FormDialog("Daily Sales Target",ui,vm::close,vm::setTarget) {Field("Target in pesos",ui.fields["target"] ?: "",{vm.field("target",it)},numeric=true)}
        "deleteUser" -> ConfirmDialog("Delete user?","Historical cashier names remain on receipts.",vm::close,vm::deleteUser,ui.busy,"Delete",ui.message)
        "deleteTransaction" -> ConfirmDialog("Delete transaction?","Receipt items will be removed and quantities restored to products that still exist. This changes analytics.",vm::close,vm::deleteTransaction,ui.busy,"Delete",ui.message)
        "deleteCategory" -> ConfirmDialog("Delete category?","Categories used by products or historical receipts cannot be deleted.",vm::close,vm::deleteCategory,ui.busy,"Delete",ui.message)
        "import" -> ConfirmDialog("Replace all data?","Importing replaces ALL database records. Export your current data first. A successful import signs you out.",vm::close,vm::importBackup,ui.busy,"Replace data",ui.message)
        "resetFirst" -> ConfirmDialog("Reset All Data?","This removes your current data. Export a backup first.",vm::close,{vm.dialog("resetFinal")},ui.busy,"Continue",ui.message)
        "resetFinal" -> ConfirmDialog("Final confirmation","Permanently replace all current data with the original demo records?",vm::close,vm::reset,ui.busy,"Reset now",ui.message)
    }
}
