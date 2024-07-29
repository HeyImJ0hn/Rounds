package dev.jpires.rounds.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jpires.rounds.R
import dev.jpires.rounds.model.data.Preset
import dev.jpires.rounds.model.data.PresetEntity
import dev.jpires.rounds.model.data.TimerType
import dev.jpires.rounds.model.repository.Repository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.logging.Logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

class ViewModel(context: Context) : ViewModel(){
    private val repository = Repository(context)

    private var _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private var _activePreset = MutableStateFlow<Preset?>(null)
    val activePreset = _activePreset.asStateFlow()

    private var _allPresets = MutableStateFlow<MutableList<Preset>>(mutableListOf())
    val allPresets = _allPresets.asStateFlow()

    private var roundLength: Duration by mutableStateOf(Duration.ZERO)
    private var restTime: Duration by mutableStateOf(Duration.ZERO)
    private var prepTime: Duration by mutableStateOf(Duration.ZERO)
    private var rounds: Int by mutableIntStateOf(1)

    private var currentRound: Int by mutableIntStateOf(1)

    private var _currentRoundTime = MutableStateFlow(roundLength)
    val currentRoundTime = _currentRoundTime.asStateFlow()

    private var _currentRestTime = MutableStateFlow(restTime)
    val currentRestTime = _currentRestTime.asStateFlow()

    private var _currentPrepTime = MutableStateFlow(prepTime)
    val currentPrepTime = _currentPrepTime.asStateFlow()

    private var paused  = MutableStateFlow(false)
    private var started = MutableStateFlow(false)
    private var timerJob: Job? = null

    private var _currentTimer = MutableStateFlow(TimerType.PREP)
    val currentTimer = _currentTimer.asStateFlow()

    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished = _isTimerFinished.asStateFlow()

    init {
        viewModelScope.launch {
            Logger.getGlobal().info("ViewModel init")
            initializeRepository()
            loadUI()
            _isReady.value = true
        }
    }

    private suspend fun initializeRepository() {
        withContext(Dispatchers.IO) {
            repository.initDatabase()
            _allPresets.value = repository.getAllPresets().map { it.toDomainModel() }.toMutableList()
        }
    }

    suspend fun loadUI() {
        withContext(Dispatchers.Main) {
            _activePreset.value = _allPresets.value.first()

            roundLength = activePreset.value!!.roundLength
            restTime = activePreset.value!!.restTime
            prepTime = activePreset.value!!.prepTime
            rounds = activePreset.value!!.rounds

            _currentRoundTime.value = roundLength
            _currentRestTime.value = restTime
            _currentPrepTime.value = prepTime
        }
    }

    fun startTimer(playSound: (Int) -> Unit) {
        started.value = true
        paused.value = false
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            while (_currentPrepTime.value >= Duration.ZERO) {
                _currentTimer.value = TimerType.PREP
                if (_currentPrepTime.value in 0.seconds..2.seconds)
                    playSound(R.raw.beep)
                delay(1000)
                decrementCurrentPrepTime()
            }
            while (currentRound <= rounds) {
                playSound(R.raw.round_start)
                while (_currentRoundTime.value >= Duration.ZERO) {
                    _currentTimer.value = TimerType.ROUND
                    if (_currentRoundTime.value == 10.seconds)
                        playSound(R.raw.ten_second_warning)
                    delay(1000)
                    decrementCurrentRoundTime()
                }
                playSound(R.raw.round_end)
                while (_currentRestTime.value >= Duration.ZERO && currentRound < rounds) {
                    _currentTimer.value = TimerType.REST
                    if (_currentRoundTime.value == 10.seconds)
                        playSound(R.raw.ten_second_warning)
                    if (_currentPrepTime.value in 0.seconds..2.seconds)
                        playSound(R.raw.beep)
                    delay(1000)
                    decrementCurrentRestTime()
                }
                if (currentRound == rounds) {
                    _currentTimer.value = TimerType.FINISHED
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
        paused.value = true
        timerJob?.cancel()
    }

    fun stopTimer() {
        timerJob?.cancel()
//        reset()
    }

    fun skipTimer() {
        when (_currentTimer.value) {
            TimerType.PREP -> _currentPrepTime.value = Duration.ZERO
            TimerType.ROUND -> _currentRoundTime.value = Duration.ZERO
            TimerType.REST -> _currentRestTime.value = Duration.ZERO
            TimerType.FINISHED -> _currentRestTime.value = Duration.ZERO
        }
    }

    fun reset() {
        started.value = false
        paused.value = false
        currentRound = 1
        _currentPrepTime.value = prepTime
        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
        _currentTimer.value = TimerType.PREP
        _isTimerFinished.value = false
    }

    fun getFormattedCurrentRoundTime(duration: Duration)= formatDuration(duration)
    private fun decrementCurrentRoundTime() {
        if (_currentRoundTime.value >= 0.seconds) {
            _currentRoundTime.value -= 1.seconds
        }
    }

    fun getFormattedCurrentRestTime(duration: Duration) = formatDuration(duration)
    private fun decrementCurrentRestTime() {
        if (_currentRestTime.value >= 0.seconds) {
            _currentRestTime.value -= 1.seconds
        }
    }

    private fun resetCurrentRestTime() {
        _currentRestTime.value = restTime
    }

    fun getFormattedCurrentPrepTime(duration: Duration) = formatDuration(duration)
    private fun decrementCurrentPrepTime() {
        if (_currentPrepTime.value >= 0.seconds) {
            _currentPrepTime.value -= 1.seconds
        }
    }

    private fun resetCurrentRoundTime() {
        _currentRoundTime.value = roundLength
    }

    fun isPaused() = paused
    fun hasStarted() = started

    private fun incrementCurrentRound() {
        currentRound++
    }

    fun resetCurrentRound() {
        currentRound = 1
    }

    fun getCurrentRound() = currentRound.toString()

    fun incrementRoundLength() {
         roundLength += 5.seconds
        _currentRoundTime.value = roundLength
        updateCurrentPreset()
    }

    fun decrementRoundLength() {
        if (roundLength > 5.seconds) {
            roundLength -= 5.seconds
            _currentRoundTime.value = roundLength
            updateCurrentPreset()
        }
    }

    fun incrementRestTime() {
        restTime += 5.seconds
        _currentRestTime.value = restTime
        updateCurrentPreset()
    }

    fun decrementRestTime() {
        if (restTime > 5.seconds) {
            restTime -= 5.seconds
            _currentRestTime.value = restTime
            updateCurrentPreset()
        }
    }

    fun incrementRounds() {
        rounds++
        updateCurrentPreset()
    }

    fun decrementRounds() {
        if (rounds > 1) {
            rounds--
            updateCurrentPreset()
        }
    }

    fun incrementPrepTime() {
        prepTime += 5.seconds
        _currentPrepTime.value = prepTime
        updateCurrentPreset()
    }

    fun decrementPrepTime() {
        if (prepTime > 5.seconds) {
            prepTime -= 5.seconds
            _currentPrepTime.value = prepTime
            updateCurrentPreset()
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

    private fun updateCurrentPreset() {
        activePreset.value!!.rounds = rounds
        activePreset.value!!.roundLength = roundLength
        activePreset.value!!.restTime = restTime
        activePreset.value!!.prepTime = prepTime

        CoroutineScope(Dispatchers.IO).launch {
            repository.updatePreset(activePreset.value!!.toEntityModel())
        }
    }

    fun getActivePresetName() = activePreset.value!!.name

    fun setActivePreset(preset: Preset) {
        _activePreset.value = preset

        roundLength = activePreset.value!!.roundLength
        restTime = activePreset.value!!.restTime
        prepTime = activePreset.value!!.prepTime
        rounds = activePreset.value!!.rounds

        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
        _currentPrepTime.value = prepTime
    }

    fun duplicateActivePreset() {
        duplicatePreset(activePreset.value!!)
    }

    fun duplicatePreset(preset: Preset) {
        val currentPreset = preset.toEntityModel()
        val newPreset = PresetEntity(
            name = "${preset.name} (Copy)",
            rounds = preset.rounds,
            roundLength = currentPreset.roundLength,
            restTime = currentPreset.restTime,
            prepTime = currentPreset.prepTime
        )

        CoroutineScope(Dispatchers.IO).launch {
            repository.addPreset(newPreset)
            _allPresets.value = repository.getAllPresets().map { it.toDomainModel() }.toMutableList()

            val insertedPreset = repository.getPresetByName("${preset.name} (Copy)")
            setActivePreset(insertedPreset!!.toDomainModel())
        }
    }

    fun deleteActivePreset() {
        deletePreset(activePreset.value!!)
    }

    fun deletePreset(preset: Preset) {
        _allPresets.value.remove(preset)
        setActivePreset(_allPresets.value[0])

        val toDelete = preset.toEntityModel()
        CoroutineScope(Dispatchers.IO).launch {
            repository.deletePreset(toDelete)
            _allPresets.value = repository.getAllPresets().map { it.toDomainModel() }.toMutableList()
        }
    }

    fun updateActivePresetName(name: String) {
        updatePresetName(activePreset.value!!, name)
    }

    fun updatePresetName(preset: Preset, name: String) {
        preset.name = name
        CoroutineScope(Dispatchers.IO).launch {
            repository.updatePreset(preset.toEntityModel())
        }
    }

    fun getFormattedZero() = formatDuration(Duration.ZERO)

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