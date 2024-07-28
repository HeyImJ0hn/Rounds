package dev.jpires.rounds.model.repository

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import dev.jpires.rounds.model.data.PresetEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.logging.Logger
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class Repository(private val context: Context) {

    private val db = Room.databaseBuilder(
        context,
        Database::class.java, "rounds-database"
    ).addCallback(object : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            CoroutineScope(Dispatchers.Main).launch {
                preloadDatabase()
            }
        }
    }).build()

    private val presetDao = db.presetDao()
    private lateinit var presetEntities: MutableList<PresetEntity>

    init {
        CoroutineScope(Dispatchers.IO).launch {
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
                rounds = 5,
                roundLength = 2.minutes.inWholeSeconds,
                restTime = 1.minutes.inWholeSeconds,
                prepTime = 15.seconds.inWholeSeconds
            )
            presetDao.insert(presetEntity)
        }
    }

    suspend fun addPreset(presetEntity: PresetEntity) {
        withContext(Dispatchers.IO) {
            presetDao.insert(presetEntity)
            presetEntities.add(presetEntity)
        }
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

    suspend fun getAllPresets(): MutableList<PresetEntity> {
        return presetEntities
    }

}