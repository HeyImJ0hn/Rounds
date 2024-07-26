package dev.jpires.rounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import dev.jpires.rounds.ui.theme.RoundsTheme
import dev.jpires.rounds.view.navigation.BottomNav
import dev.jpires.rounds.view.navigation.SetupNavHost
import dev.jpires.rounds.view.navigation.currentRoute
import dev.jpires.rounds.viewmodel.ViewModel

class MainActivity : ComponentActivity() {

    private val viewModel = ViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        setContent {
            RoundsTheme {
                val navController = rememberNavController()
                val currentRoute = currentRoute(navController)
                Scaffold(
                    modifier = Modifier.fillMaxSize()
//                    bottomBar = { if (currentRoute != "timer_screen" && currentRoute != "finished_screen") BottomNav(navController) }
                ) { innerPadding ->
                    SetupNavHost(navController = navController, modifier = Modifier.padding(innerPadding), viewModel)
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}