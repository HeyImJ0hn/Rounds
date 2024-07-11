package dev.jpires.rounds.view.composables

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.List
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dev.jpires.rounds.Greeting

@Composable
fun BottomNav(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Presets,
        BottomNavItem.Home,
        BottomNavItem.Settings
    )

    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.background) {
        val currentRoute = currentRoute(navController)
        items.forEach { item ->
            BottomNavigationItem(
                selected = currentRoute == item.route,
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
                selectedContentColor = Color.Red,
                unselectedContentColor = MaterialTheme.colorScheme.onBackground,
                onClick = {
                    navController.navigate(item.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }

}

@Composable
fun currentRoute(navController: NavHostController): String? {
    val navBackStackEntry = navController.currentBackStackEntryAsState()
    return navBackStackEntry.value?.destination?.route
}

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    data object Home : BottomNavItem("Home", Icons.Rounded.PlayArrow, "home")
    data object Presets : BottomNavItem("Presets", Icons.AutoMirrored.Rounded.List, "presets")
    data object Settings : BottomNavItem("Settings", Icons.Rounded.Settings, "settings")
}

@Composable
fun SetupNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController,
        startDestination = BottomNavItem.Home.route,
        modifier = modifier
    ) {
        composable(BottomNavItem.Presets.route) { Greeting(name = "Presets") }
        composable(BottomNavItem.Home.route) { Greeting(name = "Home") }
        composable(BottomNavItem.Settings.route) { Greeting(name = "Settings") }
    }
}

@Composable
fun PreviewNavHost(navController: NavHostController, modifier: Modifier = Modifier) {
    Scaffold(
        bottomBar = { BottomNav(navController) },
        modifier = modifier
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = BottomNavItem.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(BottomNavItem.Presets.route) { Greeting(name = "Presets") }
            composable(BottomNavItem.Home.route) { Greeting(name = "Home") }
            composable(BottomNavItem.Settings.route) { Greeting(name = "Settings") }
        }
    }
}
