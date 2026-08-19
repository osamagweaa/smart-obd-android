package com.squillaci.autodiag.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.squillaci.autodiag.ui.bluetooth.BluetoothFlowScreen
import com.squillaci.autodiag.ui.dpf.DpfScreen
import com.squillaci.autodiag.ui.dtc.DtcScreen
import com.squillaci.autodiag.ui.home.HomeScreen
import com.squillaci.autodiag.ui.itv.ItvScreen
import com.squillaci.autodiag.ui.onboarding.OnboardingScreen
import com.squillaci.autodiag.ui.report.ReportScreen
import com.squillaci.autodiag.ui.safetoday.SafeTodayScreen

@Composable
fun AppNavGraph(languageSelected: Boolean) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = if (languageSelected) Route.Home.name else Route.OnboardingScreen.name
    ) {
        composable(Route.OnboardingScreen.name) {
            OnboardingScreen {
                navController.navigate(Route.Home.name) {
                    popUpTo(Route.OnboardingScreen.name) { inclusive = true }
                }
            }
        }
        composable(Route.Home.name) { HomeScreen(navController) }
        composable(Route.Bluetooth.name) { BluetoothFlowScreen(navController) }
        composable(Route.Dtc.name) { DtcScreen(onOpenReport = { navController.navigate(Route.Report.name) }) }
        composable(Route.SafeToday.name) { SafeTodayScreen() }
        composable(Route.Itv.name) { ItvScreen() }
        composable(Route.Dpf.name) { DpfScreen() }
        composable(Route.Report.name) { ReportScreen() }
    }
}
