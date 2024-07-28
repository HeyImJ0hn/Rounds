package dev.jpires.rounds.model.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import dev.jpires.rounds.model.data.PresetEntity

@Dao
interface PresetDao {
    @Query("SELECT * FROM preset")
    suspend fun getAll(): MutableList<PresetEntity>

    @Query("SELECT * FROM preset WHERE id = (:id) LIMIT 1")
    suspend fun getById(id: Int): PresetEntity?

    @Query("SELECT * FROM preset WHERE name = (:name) LIMIT 1")
    suspend fun getByName(name: String): PresetEntity?

    @Query("SELECT COUNT(*) FROM preset")
    suspend fun count(): Int

    @Update
    suspend fun update(presetEntity: PresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(vararg presetEntities: PresetEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(presetEntity: PresetEntity)

    @Delete
    suspend fun delete(presetEntity: PresetEntity)
}