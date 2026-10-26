package com.bizpulse.app.ui.navigation
import androidx.compose.runtime.Composable
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
private data class Destination(val route:String,val label:String,val icon:ImageVector)
@Composable fun BottomNavBar(current:String,owner:Boolean,onSelect:(String)->Unit) {
    val items=if(owner) listOf(Destination(Routes.DASHBOARD,"Home",Icons.Default.PieChart),Destination(Routes.INVENTORY,"Stock",Icons.Default.Inventory2),Destination(Routes.ANALYTICS,"Analytics",Icons.Default.BarChart),Destination(Routes.SUPPLIERS,"Suppliers",Icons.Default.Place),Destination(Routes.MANAGE,"Manage",Icons.Default.Settings),Destination(Routes.GUIDE,"Guide",Icons.Default.MenuBook)) else listOf(Destination(Routes.DASHBOARD,"Home",Icons.Default.PieChart),Destination(Routes.SHIFT,"Shift",Icons.Default.Schedule),Destination(Routes.INVENTORY,"Stock",Icons.Default.Inventory2),Destination(Routes.POS,"POS",Icons.Default.PointOfSale),Destination(Routes.SUPPLIERS,"Suppliers",Icons.Default.Place),Destination(Routes.GUIDE,"Guide",Icons.Default.MenuBook))
    NavigationBar {items.forEach { item -> NavigationBarItem(selected=current==item.route,onClick={onSelect(item.route)},icon={Icon(item.icon,contentDescription=item.label)},label={Text(item.label,fontSize=10.sp,maxLines=1,overflow=TextOverflow.Ellipsis)})}}
}
