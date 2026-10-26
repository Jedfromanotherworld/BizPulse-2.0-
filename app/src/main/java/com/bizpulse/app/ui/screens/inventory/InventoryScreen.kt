package com.bizpulse.app.ui.screens.inventory
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.ui.screens.v2.*
@Composable fun InventoryScreen(vm:InventoryViewModel=hiltViewModel()) {
    val d by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    val owner=d.current?.role=="owner"; val tab=ui.tab.ifEmpty { "All" }
    val list=d.products.filter { (tab=="All" || tab=="Low Stock" && it.stock<=it.minAlert || tab=="Out of Stock" && it.stock==0) && (it.name.contains(ui.search,true) || it.barcode.contains(ui.search)) }
    Page {
        if(d.current?.role=="owner"){ReceiveStock();ScanToAdd(vm)}
        ScreenHeader("Inventory","${d.products.size} products · ${d.products.count { it.stock<=it.minAlert }} low stock")
        if(owner) PrimaryButton("Add Product",onClick={ vm.edit() }) else Panel { Text("Inventory editing is restricted to the Business Owner") }
        Field("Search products or barcode",ui.search,vm::search)
        Tabs(listOf("All","Low Stock","Out of Stock"),tab,vm::tab)
        Notice(ui,vm::dismissMessage)
        if(list.isEmpty()) EmptyState("No matching products","Try another tab or search.")
        list.forEach { p -> ProductRow(p,d.categories.find { it.id==p.categoryId }?.name ?: "",owner,{vm.edit(p)},{vm.dialog("deleteProduct",p.id)}) }
    }
    if(ui.dialog=="product") ProductEditDialog(vm)
    if(ui.dialog=="deleteProduct") ConfirmDialog("Delete product?","Past receipt items are kept. This product will no longer be available for new sales.",vm::close,vm::delete,ui.busy,"Delete",ui.message)
}
