package com.bizpulse.app
import android.content.Context
import androidx.room.Room
import androidx.room.withTransaction
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bizpulse.app.data.db.*
import com.bizpulse.app.data.db.entity.*
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.data.model.CartItem
import com.bizpulse.app.ui.session.SessionManager
import kotlinx.coroutines.*
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class DatabaseTest {
    private lateinit var db:BizPulseDatabase
    private lateinit var session:SessionManager
    private lateinit var sales:TransactionRepository
    @Before fun setup()=runBlocking(Dispatchers.IO) {
        val context=ApplicationProvider.getApplicationContext<Context>()
        db=Room.inMemoryDatabaseBuilder(context,BizPulseDatabase::class.java).build()
        session=SessionManager(context)
        val audit=AuditRepository(db);val users=UserRepository(db,session,audit);sales=TransactionRepository(db,users,audit)
        db.withTransaction { DatabaseSeeder.seed(db.openHelper.writableDatabase) }
        session.login(2)
        db.operations().insertShift(ShiftEntity(cashierId=2,cashierName="Cashier",timeIn=System.currentTimeMillis()))
    }
    @After fun cleanup()=runBlocking(Dispatchers.IO) {session.logout();db.close()}
    @Test fun insertQueryDelete()=runBlocking(Dispatchers.IO) {
        val id=db.categories().insert(CategoryEntity(name="Fresh Produce"))
        assertEquals("Fresh Produce",db.categories().getById(id)!!.name)
        db.categories().delete(db.categories().getById(id)!!)
        assertNull(db.categories().getById(id))
    }
    @Test fun categoryForeignKeyRestrictsDelete()=runBlocking(Dispatchers.IO) {
        val failure=runCatching {db.categories().delete(db.categories().getById(1)!!)}
        assertTrue(failure.isFailure);assertNotNull(db.categories().getById(1))
    }
    @Test fun salePersistsItemsDeductsStockAndVoidsAtomically()=runBlocking(Dispatchers.IO) {
        val p=db.products().getById(1)!!
        val receipt=sales.checkout(listOf(CartItem(p,2)),"Cash",500.0)
        assertEquals(p.stock-2,db.products().getById(1)!!.stock)
        assertEquals(1,db.lines().getByTransaction(receipt.transaction.id).size)
        assertEquals(199.36,receipt.transaction.total,0.001)
        session.login(1)
        sales.delete(receipt.transaction)
        assertEquals(p.stock,db.products().getById(1)!!.stock)
        assertTrue(db.lines().getByTransaction(receipt.transaction.id).isEmpty())
    }
    @Test fun failedCheckoutChangesNothing()=runBlocking(Dispatchers.IO) {
        val p=db.products().getById(1)!!;val before=db.transactions().snapshot().size
        assertTrue(runCatching {sales.checkout(listOf(CartItem(p)),"Cash",1.0)}.isFailure)
        assertEquals(p.stock,db.products().getById(1)!!.stock)
        assertEquals(before,db.transactions().snapshot().size)
    }
    @Test fun noSaleWithoutShift()=runBlocking(Dispatchers.IO){
        val shift=db.operations().active(2)!!;db.operations().endShift(shift.id,System.currentTimeMillis())
        val before=db.transactions().snapshot().size;val p=db.products().getById(1)!!
        assertTrue(runCatching{sales.checkout(listOf(CartItem(p)),"Cash",500.0)}.isFailure)
        assertEquals(before,db.transactions().snapshot().size)
    }
    @Test fun cashierCannotEditSuppliers()=runBlocking(Dispatchers.IO){
        val audit=AuditRepository(db);val users=UserRepository(db,session,audit);val ops=OperationsRepository(db,users,audit)
        val supplier=db.operations().supplierSnapshot().first()
        assertTrue(runCatching{ops.deleteSupplier(supplier)}.isFailure)
        session.login(1);ops.saveSupplier(supplier.copy(name="Updated"));assertEquals("Updated",db.operations().supplierSnapshot().first().name)
    }
    @Test fun cashierCannotMutateInventory()=runBlocking(Dispatchers.IO) {
        session.login(2)
        val audit=AuditRepository(db);val users=UserRepository(db,session,audit)
        val products=ProductRepository(db,users,audit)
        val p=db.products().getById(1)!!
        assertTrue(runCatching {products.delete(p)}.isFailure)
        assertNotNull(db.products().getById(1))
    }
}
