package com.bizpulse.app
import com.bizpulse.app.util.ProfitCalculator
import org.junit.Assert.*
import org.junit.Test
class ProfitCalculatorTest {
    @Test fun profitIsRevenueMinusCost() {assertEquals(44.68,ProfitCalculator.profit(99.68,55.0),0.001)}
    @Test fun marginIsSafeAtZero() {assertEquals(40.0,ProfitCalculator.margin(100.0,60.0),0.001);assertEquals(0.0,ProfitCalculator.margin(0.0,50.0),0.001)}
    @Test fun lossesArePreserved() {assertEquals(-5.0,ProfitCalculator.profit(20.0,25.0),0.001)}
}
