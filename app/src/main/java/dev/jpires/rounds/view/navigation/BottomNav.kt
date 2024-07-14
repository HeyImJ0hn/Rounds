package dev.jpires.rounds.view.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.List
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import dev.jpires.rounds.Greeting
import dev.jpires.rounds.view.screens.HomeScreen
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun BottomNav(navController: NavHostController) {

    val items = listOf(
        BottomNavItem.Presets,
        BottomNavItem.Home,
        BottomNavItem.Settings
    )

    BottomNavigation(backgroundColor = MaterialTheme.colorScheme.background, elevation = 24.dp) {
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

sealed class BottomNavItem(var title: String, var icon: ImageVector, var route: String) {
    data object Home : BottomNavItem("Home", Icons.Rounded.PlayArrow, "home")
    data object Presets : BottomNavItem("Presets", Icons.AutoMirrored.Rounded.List, "presets")
    data object Settings : BottomNavItem("Settings", Icons.Rounded.Settings, "settings")
}