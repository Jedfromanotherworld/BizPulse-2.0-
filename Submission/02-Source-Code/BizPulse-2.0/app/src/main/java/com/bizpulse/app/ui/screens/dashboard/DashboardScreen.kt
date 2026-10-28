package com.bizpulse.app.ui.screens.dashboard
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.ui.screens.v2.*
import com.bizpulse.app.util.*
import java.time.LocalDate
@Composable fun DashboardScreen(onReport:()->Unit={},vm:DashboardViewModel=hiltViewModel()) {
    val d by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    val today=d.transactions.filter { it.transaction.date==DateUtils.today() }
    val low=d.products.filter { it.stock<=it.minAlert }
    val revenue=today.sumOf { it.transaction.total }
    val top=d.transactions.flatMap { it.lineItems }.groupBy { it.categoryId }.maxByOrNull { it.value.sumOf { line -> line.price*line.quantity } }
    Page {
        if(d.current?.role=="owner")OutlineButton("Sales Report",onClick=onReport)
        HealthPanel(d)
        if(d.current?.role=="owner")Panel{Text("Currently on shift",style=MaterialTheme.typography.titleMedium);Text(d.shifts.filter{it.timeOut==null}.joinToString{it.cashierName}.ifBlank{"No cashiers on shift"})}
        ScreenHeader("Dashboard","Welcome back, ${d.current?.name.orEmpty()}")
        if(d.current?.role=="cashier") {
            val shift=today.filter { it.transaction.cashierId==d.current?.id }
            ShiftSummaryCard(shift.size,shift.sumOf { it.transaction.total },shift.sumOf { it.transaction.items })
        }
        LowStockAlert(low.map { it.name })
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { StatCard("Today's Revenue",money(revenue),"${today.sumOf { it.transaction.items }} items",Modifier.weight(1f)); StatCard("Today's Profit",money(today.sumOf { it.transaction.profit }),"Revenue − cost",Modifier.weight(1f)) }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { StatCard("Total Products",d.products.size.toString(),"${low.size} low stock",Modifier.weight(1f)); StatCard("Total Profit",money(d.transactions.sumOf { it.transaction.profit }),"All time",Modifier.weight(1f)) }
        Panel { Text("Daily sales target • ${money(d.target)}"); LinearProgressIndicator(progress=(revenue/d.target).toFloat().coerceIn(0f,1f),modifier=Modifier.fillMaxWidth()); Text("${(revenue/d.target*100).toInt()}% of target",style=MaterialTheme.typography.labelSmall) }
        InsightCard(if(top==null) "Complete a sale to discover your top-selling category." else "${d.categories.find { it.id==top.key }?.name ?: "Archived category"} leads with ${money(top.value.sumOf { it.price*it.quantity })} in item sales before VAT.")
        if(d.transactions.isEmpty()) EmptyState("No transactions yet","Start a sale in POS.")
        else SalesChart((6 downTo 0).map { n -> val day=LocalDate.now().minusDays(n.toLong()).toString(); day to d.transactions.filter { it.transaction.date==day }.sumOf { it.transaction.total } },ui.selection,vm::select)
    }
}
