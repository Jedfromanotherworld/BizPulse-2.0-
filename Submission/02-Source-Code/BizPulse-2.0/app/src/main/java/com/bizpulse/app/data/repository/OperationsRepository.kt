package com.bizpulse.app.data.repository
import androidx.room.withTransaction
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.util.*
import kotlinx.coroutines.*
import java.time.LocalDate
import javax.inject.Inject
class OperationsRepository @Inject constructor(private val db:BizPulseDatabase,private val users:UserRepository,private val audit:AuditRepository){
 suspend fun receive(productId:Long,supplier:String,quantity:Int,cost:Double,expiry:String)=withContext(Dispatchers.IO){
 val actor=users.requireOwner();require(supplier.isNotBlank()){ "Supplier is required" };require(quantity in 1..1000000 && Validation.amount(cost)){"Invalid quantity or cost"}
 if(expiry.isNotBlank()) require(!LocalDate.parse(expiry).isBefore(LocalDate.now())){"Expiry cannot be before today"}
 db.withTransaction {val p=db.products().getById(productId)?:error("Choose a product");require(p.stock.toLong()+quantity<=1000000){"Stock limit exceeded"}
 db.operations().insertBatch(BatchEntity(productId=productId,supplierName=supplier.trim(),dateReceived=System.currentTimeMillis(),quantityReceived=quantity,costPerUnit=cost,quantityRemaining=quantity,expiryDate=expiry.ifBlank{null}))
 db.products().update(p.copy(stock=p.stock+quantity,cost=cost));audit.log(actor.name,"Stock Received","${p.name}: +$quantity from $supplier","create") }
 }
 suspend fun timeIn()=withContext(Dispatchers.IO){val u=users.actor();require(u.role=="cashier"){"Cashiers only"};db.withTransaction{require(db.operations().active(u.id)==null){"Already on shift"};db.operations().insertShift(ShiftEntity(cashierId=u.id,cashierName=u.name,timeIn=System.currentTimeMillis()));audit.log(u.name,"Time In","Cashier shift started","create")}}
 suspend fun timeOut(id:Long)=withContext(Dispatchers.IO){val u=users.actor();db.withTransaction{val s=db.operations().shift(id)?:error("Shift missing");require(u.role=="owner" || s.cashierId==u.id){"Access denied"};require(db.operations().endShift(id,maxOf(System.currentTimeMillis(),s.timeIn))==1){"Shift already ended"};audit.log(u.name,"Time Out","${s.cashierName} shift #$id ended","update")}}
 suspend fun saveSupplier(s:SupplierEntity)=withContext(Dispatchers.IO){val u=users.requireOwner();require(s.name.isNotBlank() && s.address.isNotBlank()){"Name and address required"};require(s.latitude.isFinite() && s.latitude in -90.0..90.0 && s.longitude.isFinite() && s.longitude in -180.0..180.0){"Invalid geographic coordinates"};require(s.ipAddress.isBlank() || s.ipAddress.split('.').let{it.size==4 && it.all { n-> n.toIntOrNull()?.let{v->v in 0..255}==true }}){"Enter a valid IPv4 address or leave blank"};db.withTransaction{if(s.id==0L)db.operations().insertSupplier(s) else db.operations().updateSupplier(s);audit.log(u.name,"Supplier Saved",s.name,"update")}}
 suspend fun deleteSupplier(s:SupplierEntity)=withContext(Dispatchers.IO){val u=users.requireOwner();db.withTransaction{db.operations().deleteSupplier(s);audit.log(u.name,"Supplier Deleted",s.name,"delete")}}
}
