package com.bizpulse.app
import com.bizpulse.app.util.CurrencyFormatter
import org.junit.Assert.*
import org.junit.Test
class CurrencyFormatterTest {
    @Test fun pesoGrouping() {assertEquals("₱12,345.60",CurrencyFormatter.format(12345.6))}
    @Test fun zeroAndNegative() {assertEquals("₱0.00",CurrencyFormatter.format(0.0));assertEquals("₱-50.25",CurrencyFormatter.format(-50.25))}
}
