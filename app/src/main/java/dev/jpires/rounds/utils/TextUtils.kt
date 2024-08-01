package dev.jpires.rounds.utils

import java.util.Locale
import kotlin.time.Duration
import kotlin.time.DurationUnit

class TextUtils {

    companion object {
        fun formattedDuration(duration: Duration): String {
            val totalSeconds = duration.toLong(DurationUnit.SECONDS)
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
        }

        fun formattedTotalTime(duration: Duration): String {
            val totalSeconds = duration.toLong(DurationUnit.SECONDS)
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            return if (hours > 0) {
                String.format(Locale.ENGLISH,"%d:%02d:%02d", hours, minutes, seconds)
            } else {
                String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
            }
        }
    }

}