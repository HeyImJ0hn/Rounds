package dev.jpires.rounds

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import dev.jpires.rounds.view.screens.MainScreen
import dev.jpires.rounds.viewmodel.ViewModel
import dev.jpires.rounds.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val factory = ViewModelFactory(applicationContext)
        viewModel = ViewModelProvider(this, factory)[ViewModel::class.java]

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                !viewModel.isReady.value
            }
        }

        setContent {
            MainScreen(viewModel)
        }
    }
}