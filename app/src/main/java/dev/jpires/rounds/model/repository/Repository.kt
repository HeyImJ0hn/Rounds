package dev.jpires.rounds.model.repository

import android.content.Context
import androidx.datastore.dataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.jpires.rounds.model.data.PresetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Logger
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class Repository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context,
        Database::class.java, "rounds-database"
    ).build()

    private val presetDao = db.presetDao()
    private lateinit var presetEntities: MutableList<PresetEntity>

    private val Context.dataStore by preferencesDataStore(name = "data")

    companion object {
        val KEY_ACTIVE_PRESET = intPreferencesKey("active_preset")
    }

    val activePresetId: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[KEY_ACTIVE_PRESET] ?: 1
        }

    suspend fun initDatabase() {
        withContext(Dispatchers.IO) {
            preloadDatabase()
            loadAllPresets()
        }
    }

    suspend fun loadAllPresets() {
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

    suspend fun saveActivePreset(value: Int) {
        context.dataStore.edit { preferences ->
            preferences[KEY_ACTIVE_PRESET] = value
        }
    }

}