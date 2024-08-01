package dev.jpires.rounds.utils

import android.content.Context
import android.media.MediaPlayer

class SoundUtils {

    companion object {
        fun playSound(context: Context, soundResId: Int) {
            MediaPlayer.create(context, soundResId).apply {
                start()
                setOnCompletionListener { release() }
            }
        }
    }

}