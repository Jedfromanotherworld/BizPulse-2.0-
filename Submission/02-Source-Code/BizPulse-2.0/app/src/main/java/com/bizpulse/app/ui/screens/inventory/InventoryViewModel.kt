package com.bizpulse.app.ui.screens.inventory
import com.bizpulse.app.data.repository.*
import com.bizpulse.app.data.db.entity.ProductEntity
import com.bizpulse.app.ui.screens.ScreenViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
@HiltViewModel class InventoryViewModel @Inject constructor(store: AppStore, private val products: ProductRepository): ScreenViewModel(store) {
    fun prefill(code:String){val existing=data.value.products.find{it.barcode==code};edit(existing);if(existing==null)field("barcode",code)}
    fun edit(p: ProductEntity? = null) = dialog("product",p?.id ?: 0,mapOf("imageUrl" to (p?.imageUrl ?: ""),"name" to (p?.name ?: ""),"barcode" to (p?.barcode ?: ""),"price" to (p?.price?.toString() ?: ""),"cost" to (p?.cost?.toString() ?: ""),"stock" to (p?.stock?.toString() ?: "0"),"minAlert" to (p?.minAlert?.toString() ?: "5"),"categoryId" to (p?.categoryId ?: data.value.categories.firstOrNull()?.id ?: 0).toString()))
    fun save() = runAction("Product saved",true) {
        val old=data.value.products.find { it.id==ui.value.id }
        products.save(ProductEntity(ui.value.id,value("name").trim(),value("barcode").trim(),value("price").toDoubleOrNull() ?: error("Invalid price"),value("cost").toDoubleOrNull() ?: error("Invalid cost"),value("stock").toIntOrNull() ?: error("Invalid stock"),value("minAlert").toIntOrNull() ?: error("Invalid minimum alert"),value("categoryId").toLongOrNull() ?: 0,old?.createdAt ?: System.currentTimeMillis(),value("imageUrl").ifBlank{null}))
    }
    fun delete() = runAction("Product deleted",true) { products.delete(data.value.products.find { it.id==ui.value.id } ?: error("Product no longer exists")) }
}
