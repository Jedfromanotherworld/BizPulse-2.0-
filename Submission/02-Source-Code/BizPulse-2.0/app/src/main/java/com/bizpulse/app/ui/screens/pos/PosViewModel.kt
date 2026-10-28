package com.bizpulse.app.ui.screens.pos
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.data.db.entity.ProductEntity
import com.bizpulse.app.data.db.relation.TransactionWithItems
import com.bizpulse.app.data.model.*
import com.bizpulse.app.ui.screens.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.*
@HiltViewModel class PosViewModel @Inject constructor(store: AppStore, private val transactions: TransactionRepository): ScreenViewModel(store) {
    private val mutableCart=MutableStateFlow<List<CartItem>>(emptyList())
    val cart=mutableCart.asStateFlow()
    private val mutableReceipt=MutableStateFlow<TransactionWithItems?>(null)
    val receipt=mutableReceipt.asStateFlow()
    fun add(product: ProductEntity) {
        if(ui.value.busy) return
        val current=data.value.products.find { it.id==product.id } ?: return message("Product no longer exists")
        val q=cart.value.find { it.product.id==current.id }?.quantity ?: 0
        if(q>=current.stock) return message("Not enough stock for ${current.name}")
        mutableCart.update { list -> if(q==0) list+CartItem(current) else list.map { if(it.product.id==current.id) it.copy(quantity=q+1) else it } }
    }
    fun remove(id:Long) { if(!ui.value.busy) mutableCart.update { it.filterNot { row -> row.product.id==id } } }
    fun clearCart() { if(!ui.value.busy) mutableCart.value=emptyList() }
    fun scan(code:String) { data.value.products.find { it.barcode==code }?.let(::add) ?: message("No product matches barcode $code") }
    fun simulate() { data.value.products.filter { it.stock>(cart.value.find { c -> c.product.id==it.id }?.quantity ?: 0) }.randomOrNull()?.let(::add) ?: message("No available stock") }
    fun checkout() = runAction {
        mutableReceipt.value=transactions.checkout(cart.value,value("payment","Cash"),value("cash").toDoubleOrNull() ?: 0.0)
        mutableCart.value=emptyList()
        dialog("receipt")
    }
    fun dismissReceipt() { mutableReceipt.value=null; close() }
}
