package com.bizpulse.app
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.bizpulse.app.ui.session.SessionManager
import kotlinx.coroutines.*
import org.junit.*
import org.junit.runner.RunWith
@RunWith(AndroidJUnit4::class)
class LoginFlowTest {
    @get:Rule val compose=createAndroidComposeRule<MainActivity>()
    @Before fun logout() {runBlocking(Dispatchers.IO) {SessionManager(compose.activity).logout()};compose.waitUntil(15000) {compose.onAllNodesWithText("Username").fetchSemanticsNodes().isNotEmpty()}}
    @Test fun validOwnerLoginShowsDashboard() {
        compose.onNodeWithText("Username").performTextInput("owner")
        compose.onNodeWithText("Password").performTextInput("owner123")
        compose.onNodeWithTag("button-Log In").performScrollTo().performClick()
        compose.waitUntil(15000) {compose.onAllNodesWithText("Dashboard").fetchSemanticsNodes().isNotEmpty()}
        compose.onNodeWithText("Welcome back, Maria Santos").assertExists()
    }
    @Test fun invalidPasswordShowsError() {
        compose.onNodeWithText("Username").performTextInput("owner")
        compose.onNodeWithText("Password").performTextInput("wrongpass")
        compose.onNodeWithTag("button-Log In").performScrollTo().performClick()
        compose.waitUntil(10000) {compose.onAllNodesWithText("Invalid username, password, or role").fetchSemanticsNodes().isNotEmpty()}
    }
}
