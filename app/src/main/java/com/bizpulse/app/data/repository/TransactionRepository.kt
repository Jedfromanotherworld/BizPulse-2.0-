package com.bizpulse.app.data.repository
import androidx.room.withTransaction
import com.bizpulse.app.data.db.BizPulseDatabase
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.data.db.relation.TransactionWithItems
import com.bizpulse.app.data.model.*
import com.bizpulse.app.util.*
import javax.inject.Inject
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.flowOn
import java.time.*
class TransactionRepository @Inject constructor(private val db: BizPulseDatabase, private val users: UserRepository, private val audit: AuditRepository) {
    val transactions = db.transactions().withItems().flowOn(Dispatchers.IO)
    suspend fun checkout(cart: List<CartItem>, payment: String, tendered: Double): TransactionWithItems = withContext(Dispatchers.IO) {
        val actor=users.actor()
        require(actor.role=="cashier"){"Cashiers only"}
        require(cart.isNotEmpty()) { "Add products to the cart first" }
        require(Validation.payment(payment)) { "Choose a payment method" }
        db.withTransaction {
            val shift=db.operations().active(actor.id)?:error("Time in before selling")
            require(cart.map { it.product.id }.distinct().size == cart.size) { "Duplicate cart rows" }
            val fresh=cart.map { item ->
                val p=db.products().getById(item.product.id) ?: error("${item.product.name} was deleted")
                require(item.quantity>0 && item.quantity<=p.stock) { "Not enough stock for ${p.name}" }
                require(p.price==item.product.price && p.cost==item.product.cost) { "Prices changed. Clear the cart and add products again." }
                CartItem(p,item.quantity)
            }
            val allocated=fresh.flatMap { (p,q) -> Fifo.allocate(db.operations().available(p.id).filter{it.expiryDate==null || it.expiryDate>=DateUtils.today()},q).map{(b,n)->Triple(p,b,n)} }
            val total=CartLogic.total(fresh); val cost=rounded(allocated.sumOf{it.second.costPerUnit*it.third})
            require(Validation.amount(total)) { "Sale exceeds the supported total" }
            require(payment!="Cash" || (Validation.amount(tendered) && tendered>=total)) { "Insufficient cash tendered" }
            val t=TransactionEntity(date=DateUtils.today(),time=DateUtils.time(),total=total,items=fresh.sumOf { it.quantity },payment=payment,cashierName=actor.name,cashierId=actor.id,cost=cost,profit=ProfitCalculator.profit(total,cost),tendered=if(payment=="Cash") rounded(tendered) else total,shiftId=shift.id)
            val id=db.transactions().insert(t)
            val lines=allocated.map { (p,b,q) -> require(db.operations().deduct(b.id,q)==1){"Batch stock changed"};LineItemEntity(transactionId=id,productId=p.id,productName=p.name,quantity=q,price=p.price,cost=b.costPerUnit,categoryId=p.categoryId,batchId=b.id,batchReceivedAt=b.dateReceived) }
            db.lines().insert(lines)
            fresh.forEach { require(db.products().deduct(it.product.id,it.quantity)==1) { "Stock changed; retry sale" } }
            audit.log(actor.name,"Sale","Receipt #$id • ${money(total)} • $payment","sale")
            TransactionWithItems(t.copy(id=id),lines)
        }
    }
    suspend fun save(t: TransactionEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        require(Validation.price(t.total) && t.items in 1..1_000_000) { "Enter a positive total and item count" }
        LocalDate.parse(t.date); LocalTime.parse(t.time)
        require(Validation.payment(t.payment)) { "Choose a payment method" }
        db.withTransaction {
            val cashier=db.users().getById(t.cashierId) ?: error("Choose an existing cashier")
            val old=if(t.id==0L) null else db.transactions().getById(t.id) ?: error("Transaction no longer exists")
            val hasLines=old!=null && db.lines().getByTransaction(t.id).isNotEmpty()
            require(!hasLines || (t.total==old!!.total && t.items==old.items && t.date==old.date && t.time==old.time && t.payment==old.payment && t.cashierId==old.cashierId)) { "POS receipt records are locked" }
            val row=t.copy(cashierName=cashier.name,manual=!hasLines,cost=old?.cost ?: 0.0,profit=rounded(t.total-(old?.cost ?: 0.0)),tendered=if(hasLines) old!!.tendered else t.total,shiftId=old?.shiftId)
            if(old==null) db.transactions().insert(row) else db.transactions().update(row)
            audit.log(actor.name,if(old==null) "Transaction Created" else "Transaction Updated","${row.date} • ${money(row.total)}","update")
        }
    }
    suspend fun delete(t: TransactionEntity) = withContext(Dispatchers.IO) {
        val actor=users.requireOwner()
        db.withTransaction {
            val current=db.transactions().getById(t.id) ?: error("Transaction already removed")
            db.lines().getByTransaction(t.id).forEach { db.products().restore(it.productId,it.quantity) }
            db.transactions().delete(current)
            audit.log(actor.name,"Transaction Deleted","Receipt #${t.id}; available products restocked","delete")
        }
    }
}
