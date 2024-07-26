package dev.jpires.rounds.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jpires.rounds.R
import dev.jpires.rounds.model.data.TimerType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale
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

    private var _currentRoundTime = MutableStateFlow(roundLength)
    val currentRoundTime
        get() = _currentRoundTime.asStateFlow()

    private var _currentRestTime = MutableStateFlow(restTime)
    val currentRestTime
        get() = _currentRestTime.asStateFlow()

    private var _currentPrepTime = MutableStateFlow(prepTime)
    val currentPrepTime
        get() = _currentPrepTime.asStateFlow()

    private var paused: Boolean by mutableStateOf(false)

    private var timerJob: Job? = null

    private var _currentTimer = MutableStateFlow(TimerType.PREP)
    val currentTimer
        get() = _currentTimer.asStateFlow()

    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished
        get() = _isTimerFinished.asStateFlow()

    fun startTimer(playSound: (Int) -> Unit) {
        paused = false
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_currentPrepTime.value >= Duration.ZERO) {
                _currentTimer.value = TimerType.PREP
                if (_currentPrepTime.value < 3.seconds)
                    playSound(R.raw.beep)
                delay(1000)
                decrementCurrentPrepTime()
            }
            while (currentRound <= rounds) {
                playSound(R.raw.round_start)
                while (_currentRoundTime.value >= Duration.ZERO) {
                    _currentTimer.value = TimerType.ROUND
                    delay(1000)
                    if (_currentRoundTime.value == 11.seconds)
                        playSound(R.raw.ten_second_warning)
                    decrementCurrentRoundTime()
                }
                playSound(R.raw.round_end)
                while (_currentRestTime.value >= Duration.ZERO) {
                    _currentTimer.value = TimerType.REST
                    if (_currentRoundTime.value == 11.seconds)
                        playSound(R.raw.ten_second_warning)
                    if (_currentRestTime.value < 3.seconds)
                        playSound(R.raw.beep)
                    delay(1000)
                    decrementCurrentRestTime()
                }
                if (currentRound == rounds) {
                    _isTimerFinished.value = true
                    stopTimer()
                    break
                }
                incrementCurrentRound()
                resetCurrentRoundTime()
                resetCurrentRestTime()
            }
        }
    }

    fun pauseTimer() {
        paused = true
        timerJob?.cancel()
    }

    fun stopTimer() {
        _currentPrepTime.value = prepTime
        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
        timerJob?.cancel()
    }

    fun skipTimer() {
        when (_currentTimer.value) {
            TimerType.PREP -> _currentPrepTime.value = Duration.ZERO
            TimerType.ROUND -> _currentRoundTime.value = Duration.ZERO
            TimerType.REST -> _currentRestTime.value = Duration.ZERO
        }
    }

    fun getFormattedCurrentRoundTime(duration: Duration)= formatDuration(duration)
    fun decrementCurrentRoundTime() {
        if (_currentRoundTime.value >= 0.seconds) {
            _currentRoundTime.value -= 1.seconds
        }
    }

    fun getFormattedCurrentRestTime(duration: Duration) = formatDuration(duration)
    fun decrementCurrentRestTime() {
        if (_currentRestTime.value >= 0.seconds) {
            _currentRestTime.value -= 1.seconds
        }
    }

    fun resetCurrentRestTime() {
        _currentRestTime.value = restTime
    }

    fun getCurrentPrepTimeDuration() = currentPrepTime
    fun getFormattedCurrentPrepTime(duration: Duration) = formatDuration(duration)
    fun decrementCurrentPrepTime() {
        if (_currentPrepTime.value >= 0.seconds) {
            _currentPrepTime.value -= 1.seconds
        }
    }

    fun resetCurrentRoundTime() {
        _currentRoundTime.value = roundLength
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
        _currentRoundTime.value = roundLength
    }

    fun decrementRoundLength() {
        if (roundLength > 5.seconds) {
            roundLength -= 5.seconds
            _currentRoundTime.value = roundLength
        }
    }

    fun incrementRestTime() {
        restTime += 5.seconds
        _currentRestTime.value = restTime
    }

    fun decrementRestTime() {
        if (restTime > 5.seconds) {
            restTime -= 5.seconds
            _currentRestTime.value = restTime
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