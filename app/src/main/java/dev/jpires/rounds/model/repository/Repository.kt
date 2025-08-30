package dev.jpires.rounds.model.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import dev.jpires.rounds.model.data.PresetEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.util.logging.Logger
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class Repository(private val context: Context, private val dataStore: DataStore<Preferences>) {

    private val db = Room.databaseBuilder(
        context,
        Database::class.java, "rounds-database"
    ).build()

    private val presetDao = db.presetDao()
    private lateinit var presetEntities: MutableList<PresetEntity>

    companion object {
        val KEY_ACTIVE_PRESET = intPreferencesKey("active_preset")
        val KEY_THEME_MODE = intPreferencesKey("theme_mode")
        val KEY_ALWAYS_ON = intPreferencesKey("always_on")
    }

    val activePresetId: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[KEY_ACTIVE_PRESET] ?: 1
        }

    val themeMode: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[KEY_THEME_MODE] ?: 0
        }

    val alwaysOn: Flow<Int> = dataStore.data
        .map { preferences ->
            preferences[KEY_ALWAYS_ON] ?: 0
        }

    suspend fun initDatabase() {
        withContext(Dispatchers.IO) {
            preloadDatabase()
            loadAllPresets()
        }
    }

    private suspend fun loadAllPresets() {
        presetEntities = presetDao.getAll()
    }

    private suspend fun preloadDatabase() {
        if (presetDao.getAll().isEmpty()) {
            val presetEntity = PresetEntity(
                name = "Default",
                rounds = 12,
                roundLength = 3.minutes.inWholeSeconds,
                restTime = 1.minutes.inWholeSeconds,
                prepTime = 15.seconds.inWholeSeconds
            )
            presetDao.insert(presetEntity)
        }
    }

    suspend fun addPreset(presetEntity: PresetEntity) {
        presetDao.insert(presetEntity)
        loadAllPresets()
    }

    suspend fun updatePreset(presetEntity: PresetEntity) {
        presetDao.update(presetEntity)
        val index = presetEntities.indexOfFirst { it.id == presetEntity.id }
        presetEntities[index] = presetEntity
    }

    suspend fun deletePreset(presetEntity: PresetEntity) {
        presetDao.delete(presetEntity)
        presetEntities.remove(presetEntity)
    }

    suspend fun getPresetById(id: Int): PresetEntity? {
        return presetDao.getById(id)
    }

    suspend fun getPresetByName(name: String): PresetEntity? {
        return presetDao.getByName(name)
    }

    fun getAllPresets(): MutableList<PresetEntity> {
        return presetEntities
    }

    suspend fun updateActivePreset(value: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_PRESET] = value
        }
    }

    suspend fun updateThemeMode(value: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_THEME_MODE] = value
        }
    }

    suspend fun updateAlwaysOn(value: Int) {
        dataStore.edit { preferences ->
            preferences[KEY_ALWAYS_ON] = value
        }
    }

}