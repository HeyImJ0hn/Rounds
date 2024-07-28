package dev.jpires.rounds.model.data

import androidx.room.TypeConverter
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class Converters {

    @TypeConverter
    fun fromDuration(duration: Duration?): Long? {
        return duration?.inWholeSeconds
    }

    @TypeConverter
    fun toDuration(seconds: Long?): Duration? {
        return seconds?.seconds
    }
}
