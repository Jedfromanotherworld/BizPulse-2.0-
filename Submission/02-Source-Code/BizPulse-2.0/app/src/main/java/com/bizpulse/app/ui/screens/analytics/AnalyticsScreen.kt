package com.bizpulse.app.ui.screens.analytics
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
@Composable fun AnalyticsScreen(onReport:()->Unit={},vm:AnalyticsViewModel=hiltViewModel()) {
    val d by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    val period=ui.tab.ifEmpty { "7 Days" }
    val sales=d.transactions.filter { period=="All Time" || it.transaction.date in DateUtils.since(if(period=="7 Days") 7 else 30)..DateUtils.today() }
    val total=sales.sumOf { it.transaction.total }; val lines=sales.flatMap { it.lineItems }
    val category=lines.groupBy { it.categoryId }; val max=category.values.maxOfOrNull { list -> list.sumOf { it.price*it.quantity } }?.coerceAtLeast(1.0) ?: 1.0
    Page {
        if(d.current?.role=="owner")OutlineButton("Sales Report",onClick=onReport)
        AiPanel()
        ScreenHeader("Analytics","Performance from recorded sales")
        Tabs(listOf("7 Days","30 Days","All Time"),period,vm::tab)
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { StatCard("Total Revenue",money(total),modifier=Modifier.weight(1f)); StatCard("Gross Profit",money(sales.sumOf { it.transaction.profit }),modifier=Modifier.weight(1f)) }
        Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { StatCard("Items Sold",sales.sumOf { it.transaction.items }.toString(),modifier=Modifier.weight(1f)); StatCard("Avg Sale",money(if(sales.isEmpty()) 0.0 else total/sales.size),modifier=Modifier.weight(1f)) }
        Panel {
            Text("Revenue by Category",style=MaterialTheme.typography.titleMedium)
            Text("Item sales before VAT; manual entries are unallocated.",style=MaterialTheme.typography.labelSmall)
            if(lines.isEmpty()) EmptyState("No itemized sales in this period")
            category.forEach { (id,items) ->
                val amount=items.sumOf { it.price*it.quantity }
                Text("${d.categories.find { it.id==id }?.name ?: "Archived category"} • ${money(amount)}")
                LinearProgressIndicator(progress=(amount/max).toFloat(),modifier=Modifier.fillMaxWidth(),color=listOf(androidx.compose.ui.graphics.Color(0xFF10B981),androidx.compose.ui.graphics.Color(0xFF3B82F6),androidx.compose.ui.graphics.Color(0xFFF59E0B),androidx.compose.ui.graphics.Color(0xFFA78BFA))[(id%4).toInt()])
                Text("${items.sumOf { it.quantity }} items",style=MaterialTheme.typography.labelSmall)
            }
            Text("VAT on itemized sales: ${money(sales.filter { it.lineItems.isNotEmpty() }.sumOf { it.transaction.total-it.lineItems.sumOf { line -> line.price*line.quantity } })}",style=MaterialTheme.typography.labelSmall)
            Text("Manual entries: ${money(sales.filter { it.transaction.manual }.sumOf { it.transaction.total })}",style=MaterialTheme.typography.labelSmall)
        }
        Text("Recent Transactions",style=MaterialTheme.typography.titleLarge)
        if(sales.isEmpty()) EmptyState("No transactions in this period")
        sales.take(50).forEach { row -> val t=row.transaction; Panel { Text("#${t.id} • ${t.date} ${t.time}"); Text("${t.items} items · ${t.payment} · ${row.lineItems.size} product rows"); Text("${money(t.total)} • Profit ${money(t.profit)}"); if(t.manual) Text("Manual entry • cost assumed zero",style=MaterialTheme.typography.labelSmall) } }
    }
}
