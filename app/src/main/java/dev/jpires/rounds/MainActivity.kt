package dev.jpires.rounds

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import dev.jpires.rounds.model.repository.DataStoreRepository
import dev.jpires.rounds.model.repository.Repository
import dev.jpires.rounds.view.screens.MainScreen
import dev.jpires.rounds.viewmodel.ViewModel
import dev.jpires.rounds.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ViewModel
    private lateinit var dataStore: DataStore<Preferences>
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        dataStore = DataStoreRepository.dataStore(applicationContext)
        repository = Repository(applicationContext, dataStore)

        val factory = ViewModelFactory(repository)
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