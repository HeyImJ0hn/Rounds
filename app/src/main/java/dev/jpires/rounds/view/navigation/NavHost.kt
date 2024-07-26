package dev.jpires.rounds.view.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.jpires.rounds.Greeting
import dev.jpires.rounds.view.screens.FinishedScreen
import dev.jpires.rounds.view.screens.HomeScreen
import dev.jpires.rounds.view.screens.TimerScreen
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun SetupNavHost(navController: NavHostController, modifier: Modifier = Modifier, viewModel: ViewModel) {
    NavHost(
        navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Home.route) { HomeScreen(viewModel, navController) }
        composable(BottomNavItem.Presets.route) { Greeting(name = "Presets") }
        composable(BottomNavItem.Settings.route) { Greeting(name = "Settings") }
        composable("timer_screen") { TimerScreen(viewModel, navController) }
        composable("finished_screen") { FinishedScreen(viewModel, navController) }
    }
}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    return navBackStackEntry.value?.destination?.route
}