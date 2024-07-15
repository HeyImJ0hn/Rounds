package dev.jpires.rounds.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jpires.rounds.model.data.Preset
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
import java.util.logging.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class ViewModel : ViewModel(){

    private var roundLength: Duration by mutableStateOf(2.minutes)
    private var restTime: Duration by mutableStateOf(1.minutes)
    private var rounds: Int by mutableIntStateOf(5)
    private var prepTime: Duration by mutableStateOf(15.seconds)

    private var currentRound: Int by mutableIntStateOf(1)
    private var currentRoundTime: Duration by mutableStateOf(roundLength)
    private var currentRestTime: Duration by mutableStateOf(restTime)

    private var _currentPrepTime = MutableStateFlow(prepTime)
    val currentPrepTime
        get() = _currentPrepTime.asStateFlow()

    private var paused: Boolean by mutableStateOf(false)

    private var timerJob: Job? = null

    fun startTimer() {
        paused = false
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_currentPrepTime.value > Duration.ZERO) {
                delay(1000)
                decrementCurrentPrepTime()
            }
        }
    }

    fun pauseTimer() {
        paused = true
        timerJob?.cancel()
    }

    fun stopTimer() {
        _currentPrepTime.value = prepTime
        timerJob?.cancel()
    }

    fun getCurrentRoundTimeDuration(): Duration = currentRoundTime
    fun getFormattedCurrentRoundTime(): String = formatDuration(currentRoundTime)
    fun decrementCurrentRoundTime() {
        if (currentRoundTime > 0.seconds) {
            currentRoundTime -= 1.seconds
        }
    }

    fun getFormattedCurrentRestTime() = formatDuration(currentRestTime)
    fun decrementCurrentRestTime() {
        if (currentRestTime > 0.seconds) {
            currentRestTime -= 1.seconds
        }
    }

    fun getCurrentPrepTimeDuration() = currentPrepTime
    fun getFormattedCurrentPrepTime(duration: Duration) = formatDuration(duration)
    fun decrementCurrentPrepTime() {
        if (_currentPrepTime.value > 0.seconds) {
            _currentPrepTime.value -= 1.seconds
        }
    }

    fun resetCurrentRoundTime() {
        currentRoundTime = roundLength
    }

    fun togglePause() {
        paused = !paused
    }

    fun isPaused() = paused

    fun incrementCurrentRound() {
        currentRound++
    }

    fun resetCurrentRound() {
        currentRound = 1
    }

    fun getCurrentRound() = currentRound.toString()

    fun incrementRoundLength() {
         roundLength += 5.seconds
    }

    fun decrementRoundLength() {
        if (roundLength > 5.seconds) {
            roundLength -= 5.seconds
        }
    }

    fun incrementRestTime() {
        restTime += 5.seconds
    }

    fun decrementRestTime() {
        if (restTime > 5.seconds) {
            restTime -= 5.seconds
        }
    }

    fun incrementRounds() {
        rounds++
    }

    fun decrementRounds() {
        if (rounds > 1) {
            rounds--
        }
    }

    fun incrementPrepTime() {
        prepTime += 5.seconds
        _currentPrepTime.value = prepTime
    }

    fun decrementPrepTime() {
        if (prepTime > 5.seconds) {
            prepTime -= 5.seconds
            _currentPrepTime.value = prepTime
        }
    }

    private fun calculateTotalTime(): Duration {
        return (roundLength + restTime) * rounds
    }

    fun getFormattedTotalTime(): String {
        val totalSeconds = calculateTotalTime().toLong(DurationUnit.SECONDS)
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60

        return if (hours > 0) {
            String.format(Locale.ENGLISH,"%d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
        }
    }

    fun getFormattedRoundLength() = formatDuration(roundLength)
    fun getFormattedRestTime() = formatDuration(restTime)
    fun getFormattedPrepTime() = formatDuration(prepTime)
    fun getFormattedRounds() = rounds.toString()
    private fun formatDuration(duration: Duration): String {
        val totalSeconds = duration.toLong(DurationUnit.SECONDS)
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format(Locale.ENGLISH, "%02d:%02d", minutes, seconds)
    }

}