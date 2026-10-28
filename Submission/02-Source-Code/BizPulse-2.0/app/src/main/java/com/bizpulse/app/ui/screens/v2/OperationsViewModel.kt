package com.bizpulse.app.ui.screens.v2
import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.ui.screens.ScreenViewModel
import com.bizpulse.app.util.*
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
@HiltViewModel class OperationsViewModel @Inject constructor(store:AppStore,private val operations:OperationsRepository,private val users:UserRepository,private val ai:LocalAiService,@ApplicationContext private val context:Context):ScreenViewModel(store){
 val insight=MutableStateFlow(context.getSharedPreferences("insights",0).getString("manual","").orEmpty())
 val healthExplanation=MutableStateFlow("")
 fun receive()=runAction("Stock received",true){operations.receive(value("productId").toLongOrNull()?:0,value("supplier"),value("quantity").toIntOrNull()?:error("Enter a whole quantity"),value("cost").toDoubleOrNull()?:error("Invalid cost"),value("expiry"))}
 fun start()=runAction("Shift started"){operations.timeIn()}
 fun end(id:Long)=runAction("Shift ended"){operations.timeOut(id)}
 fun edit(s:SupplierEntity?=null)=dialog("supplier",s?.id?:0,mapOf("name" to s?.name.orEmpty(),"address" to s?.address.orEmpty(),"ip" to s?.ipAddress.orEmpty(),"lat" to (s?.latitude?.toString()?:""),"lon" to (s?.longitude?.toString()?:"")))
 fun save()=runAction("Supplier saved",true){operations.saveSupplier(SupplierEntity(ui.value.id,value("name").trim(),value("address").trim(),value("ip").trim(),value("lat").toDoubleOrNull()?:error("Invalid latitude"),value("lon").toDoubleOrNull()?:error("Invalid longitude")))}
 fun delete()=runAction("Supplier deleted",true){operations.deleteSupplier(data.value.suppliers.find{it.id==ui.value.id}?:error("Supplier missing"))}
 fun generate()=runAction {users.requireOwner();val d=data.value;val sales=d.transactions.filter{it.transaction.date==DateUtils.today()};val previous=d.transactions.filter{it.transaction.date==java.time.LocalDate.now().minusDays(1).toString()};val facts="Today revenue PHP ${rounded(sales.sumOf{it.transaction.total})}; yesterday PHP ${rounded(previous.sumOf{it.transaction.total})}; today transactions ${sales.size}; low stock products ${d.products.count{it.stock<=it.minAlert}}; out of stock ${d.products.count{it.stock==0}}. Stock status ${health(d)}.";val result=ai.generateInsight(facts);insight.value=result;context.getSharedPreferences("insights",0).edit().putString("manual",result).apply()}
 fun explainHealth()=runAction{users.requireOwner();val d=data.value;healthExplanation.value=ai.generateAppHealth("Stock status ${health(d)}; out of stock ${d.products.count{it.stock==0}}; low stock ${d.products.count{it.stock<=it.minAlert}}.")}
 fun qr(uri:Uri)=runAction("Payment QR updated"){users.requireOwner();val path=withContext(Dispatchers.IO){MediaFiles.importImage(context,uri)};context.getSharedPreferences("branding",0).edit().putString("qr",path).apply()}
 companion object {fun health(d:AppData)=when{d.products.any{it.stock==0}->"Critical";d.products.any{it.stock<=it.minAlert}->"Warning";else->"Good"}}
}
