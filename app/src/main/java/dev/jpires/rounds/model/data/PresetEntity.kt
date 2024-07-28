package dev.jpires.rounds.model.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlin.time.Duration.Companion.seconds

@Entity(tableName = "preset")
data class PresetEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "rounds") val rounds: Int,
    @ColumnInfo(name = "roundLength") val roundLength: Long,
    @ColumnInfo(name = "restTime") val restTime: Long,
    @ColumnInfo(name = "prepTime") val prepTime: Long
) {
    @Ignore
    fun toDomainModel(): Preset {
        return Preset(
            id = this.id,
            name = this.name,
            rounds = this.rounds,
            roundLength = this.roundLength.seconds,
            restTime = this.restTime.seconds,
            prepTime = this.prepTime.seconds
        )
    }
}
