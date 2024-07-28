package dev.jpires.rounds.model.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import dev.jpires.rounds.model.dao.PresetDao
import dev.jpires.rounds.model.data.Converters
import dev.jpires.rounds.model.data.PresetEntity

@Database(entities = [PresetEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun presetDao(): PresetDao
}