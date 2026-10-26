package com.bizpulse.app.util
import java.math.BigDecimal
import java.math.RoundingMode
import java.util.Locale
object CurrencyFormatter {
    fun format(value: Double): String = "₱" + String.format(Locale.US, "%,.2f", value)
}
fun money(value: Double) = CurrencyFormatter.format(value)
fun rounded(value: Double): Double = BigDecimal.valueOf(value).setScale(2, RoundingMode.HALF_UP).toDouble()
object ProfitCalculator {
    fun profit(revenue: Double, cost: Double) = rounded(revenue - cost)
    fun margin(price: Double, cost: Double) = if (price > 0) (price-cost)/price*100 else 0.0
}
