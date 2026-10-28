package com.bizpulse.app.data.repository
import androidx.room.withTransaction
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.util.Validation
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
class ProductRepository @Inject constructor(private val db: BizPulseDatabase, private val users: UserRepository, private val audit: AuditRepository) {
    val products = db.products().getAll().flowOn(Dispatchers.IO)
    suspend fun save(p: ProductEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        require(p.barcode.length in 1..80 && p.barcode.all{it.code in 32..126}) { "Barcode must contain 1–80 printable ASCII characters" }
        require(p.name.isNotBlank() && p.barcode.isNotBlank()) { "Name and barcode are required" }
        require(Validation.price(p.price) && Validation.amount(p.cost)) { "Price must be positive; cost cannot be negative" }
        require(Validation.count(p.stock) && Validation.count(p.minAlert)) { "Stock and alert must be whole numbers from 0 to 1,000,000" }
        db.withTransaction {
            require(db.categories().getById(p.categoryId) != null) { "Choose a category" }
            val same=db.products().getByBarcode(p.barcode)
            require(same==null || same.id==p.id) { "Barcode already exists" }
            if(p.id==0L) {val id=db.products().insert(p);if(p.stock>0)db.operations().insertBatch(BatchEntity(productId=id,supplierName="Opening stock",dateReceived=System.currentTimeMillis(),quantityReceived=p.stock,costPerUnit=p.cost,quantityRemaining=p.stock))} else {
                require(db.products().getById(p.id)?.stock==p.stock){"Use Receive Stock to change stock"}
                require(db.products().getById(p.id)!=null) { "Product no longer exists" }; db.products().update(p)
            }
            audit.log(actor.name,if(p.id==0L) "Product Created" else "Product Updated",p.name,if(p.id==0L) "create" else "update")
        }
    }
    suspend fun delete(p: ProductEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        db.withTransaction { db.products().delete(p); audit.log(actor.name,"Product Deleted",p.name,"delete") }
    }
}
