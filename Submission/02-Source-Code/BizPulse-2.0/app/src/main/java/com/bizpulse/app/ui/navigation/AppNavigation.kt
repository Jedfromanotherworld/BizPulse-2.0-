package com.bizpulse.app.ui.navigation
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.bizpulse.app.ui.screens.auth.*
import com.bizpulse.app.ui.screens.dashboard.*
import com.bizpulse.app.ui.screens.inventory.*
import com.bizpulse.app.ui.screens.pos.*
import com.bizpulse.app.ui.screens.analytics.*
import com.bizpulse.app.ui.screens.manage.*
import com.bizpulse.app.ui.screens.guide.*
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.ui.screens.v2.*
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun AppNavigation(vm:AuthViewModel=hiltViewModel()) {
    val data by vm.data.collectAsStateWithLifecycle(); val ui by vm.ui.collectAsStateWithLifecycle()
    val nav=rememberNavController(); val back by nav.currentBackStackEntryAsState(); val route=back?.destination?.route ?: Routes.LOGIN
    LaunchedEffect(data.ready,data.current?.id) {
        if(data.ready) listOf(Routes.DASHBOARD,Routes.INVENTORY,Routes.POS,Routes.ANALYTICS,Routes.MANAGE,Routes.GUIDE).forEach { nav.clearBackStack(it) }
        if(data.ready) nav.navigate(if(data.current==null) Routes.LOGIN else Routes.DASHBOARD) {popUpTo(nav.graph.id) {inclusive=true};launchSingleTop=true}
    }
    if(!data.ready) {Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) {CircularProgressIndicator()};return}
    val logged=data.current!=null && route!=Routes.LOGIN
    val logout={vm.dialog("logout")}
    Scaffold(topBar={if(logged) TopAppBar(title={Text("BizPulse")},actions={RoleBadge(data.current!!.role);IconButton(onClick=logout) {Icon(Icons.Default.Logout,"Log Out")}})},bottomBar={if(logged) BottomNavBar(route,data.current?.role=="owner") {dest -> nav.navigate(dest) {popUpTo(Routes.DASHBOARD) {saveState=true};launchSingleTop=true;restoreState=true}}}) {padding ->
        NavHost(navController=nav,startDestination=Routes.LOGIN,modifier=Modifier.padding(padding).consumeWindowInsets(padding).imePadding()) {
            composable(Routes.LOGIN) {LoginScreen()}
            composable(Routes.DASHBOARD) {if(data.current!=null) DashboardScreen(onReport={nav.navigate(Routes.REPORT)})}
            composable(Routes.INVENTORY) {if(data.current!=null) InventoryScreen()}
            composable(Routes.POS) {if(data.current?.role=="cashier") PosScreen()}
            composable(Routes.ANALYTICS) {if(data.current?.role=="owner") AnalyticsScreen(onReport={nav.navigate(Routes.REPORT)})}
            composable(Routes.MANAGE) {if(data.current!=null) ManageScreen(logout)}
            composable(Routes.SUPPLIERS){if(data.current!=null)SupplierScreen()}
            composable(Routes.SHIFT){if(data.current?.role=="cashier")ShiftScreen()}
            composable(Routes.REPORT){if(data.current?.role=="owner")Page{SalesReport()}}
            composable(Routes.GUIDE) {if(data.current!=null) GuideScreen(data.current!!.role,logout)}
        }
    }
    if(ui.dialog=="logout") ConfirmDialog("Log out?","Your unsold cart will be cleared. Completed sales remain saved.",vm::close,{vm.close();vm.logout()},ui.busy,"Log Out")
}
