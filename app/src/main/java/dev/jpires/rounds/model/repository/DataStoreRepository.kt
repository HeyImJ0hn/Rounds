package dev.jpires.rounds.model.repository

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore

object DataStoreRepository {

    val KEY_ACTIVE_PRESET = intPreferencesKey("active_preset")
    val KEY_THEME_MODE = intPreferencesKey("theme_mode")

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "rounds-preferences")

    fun dataStore(context: Context): DataStore<Preferences> {
        return context.dataStore
    }
}