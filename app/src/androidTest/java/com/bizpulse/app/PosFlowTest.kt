package com.bizpulse.app
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bizpulse.app.ui.session.SessionManager
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class PosFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    @Before fun login() {runBlocking(Dispatchers.IO) {SessionManager(compose.activity).login(2)
        val db=com.bizpulse.app.data.db.BizPulseDatabase.getInstance(compose.activity)
        if(db.operations().active(2)==null)db.operations().insertShift(com.bizpulse.app.data.db.entity.ShiftEntity(cashierId=2,cashierName="Cashier",timeIn=System.currentTimeMillis()))};compose.waitUntil(15000) {compose.onAllNodesWithText("Dashboard").fetchSemanticsNodes().isNotEmpty()}}
    @Test fun addingAProductUpdatesCartTotal() {
        compose.onNodeWithContentDescription("POS").performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Point of Sale").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Beverage - Cola 1.5L").performClick()
        compose.onNodeWithText("Total: ₱99.68").performScrollTo().assertIsDisplayed()
        compose.onNodeWithTag("button-Complete Sale").performScrollTo().assertIsEnabled()
    }
}
