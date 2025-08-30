package dev.jpires.rounds.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.jpires.rounds.R
import dev.jpires.rounds.model.data.Preset
import dev.jpires.rounds.model.data.PresetEntity
import dev.jpires.rounds.model.data.ThemeMode
import dev.jpires.rounds.model.data.TimerType
import dev.jpires.rounds.model.repository.Repository
import dev.jpires.rounds.utils.TextUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.reflect.KMutableProperty0
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ViewModel(private val repository: Repository) : ViewModel(){
    private var timerJob: Job? = null

    private var rounds: Int by mutableIntStateOf(1)
    private var currentRound: Int by mutableIntStateOf(1)
    private var roundLength: Duration by mutableStateOf(Duration.ZERO)
    private var restTime: Duration by mutableStateOf(Duration.ZERO)
    private var prepTime: Duration by mutableStateOf(Duration.ZERO)

    private val paused  = MutableStateFlow(false)
    private val started = MutableStateFlow(false)

    private val _isReady = MutableStateFlow(false)
    val isReady = _isReady.asStateFlow()

    private val _activePreset = MutableStateFlow<Preset?>(null)
    val activePreset = _activePreset.asStateFlow()

    private val _allPresets = MutableStateFlow<MutableList<Preset>>(mutableListOf())
    val allPresets = _allPresets.asStateFlow()

    private val _currentRoundTime = MutableStateFlow(roundLength)
    val currentRoundTime = _currentRoundTime.asStateFlow()

    private val _currentRestTime = MutableStateFlow(restTime)
    val currentRestTime = _currentRestTime.asStateFlow()

    private val _currentPrepTime = MutableStateFlow(prepTime)
    val currentPrepTime = _currentPrepTime.asStateFlow()

    private val _currentTimer = MutableStateFlow(TimerType.PREP)
    val currentTimer = _currentTimer.asStateFlow()

    private val _isTimerFinished = MutableStateFlow(false)
    val isTimerFinished = _isTimerFinished.asStateFlow()

    private val _themeMode = MutableStateFlow(ThemeMode.SYSTEM)
    val themeMode = _themeMode.asStateFlow()

    private val _alwaysOn = MutableStateFlow(false)
    val alwaysOn = _alwaysOn.asStateFlow()

    init {
        viewModelScope.launch {
            initializeRepository()
            loadTheme()
            loadSettings()
            loadUI()
            delay(1000L) // Delay to allow the UI to update
            _isReady.value = true
        }
    }

    private suspend fun initializeRepository() {
        withContext(Dispatchers.IO) {
            repository.initDatabase()
            _allPresets.value = repository.getAllPresets().map { it.toDomainModel() }.toMutableList()
        }
    }

    // Load theme from preferences
    private suspend fun loadTheme() {
        viewModelScope.launch {
            repository.themeMode.collect { id ->
                _themeMode.value = ThemeMode.fromInt(id)
                cancel() // Cancel the scope when the theme is loaded, otherwise it will keep listening
            }
        }
    }

    private suspend fun loadSettings() {
        viewModelScope.launch {
            repository.alwaysOn.collect { value ->
                _alwaysOn.value = value != 0
                cancel()
            }
        }
    }

    private suspend fun loadUI() {
        viewModelScope.launch {
            // Load active preset from preferences
            repository.activePresetId.collect { id ->
                _activePreset.value = _allPresets.value.find { it.id == id }

                // Load preset values
                _activePreset.value?.let { preset ->
                    updatePresetValues(preset)
                }

                // Cancel the scope when the active preset is loaded, otherwise it will keep listening
                if (_activePreset.value != null)
                    cancel()
            }
        }
    }

    fun startTimer(playSound: (Int) -> Unit) {
        started.value = true
        paused.value = false
        timerJob?.cancel()
        timerJob = viewModelScope.launch {
            runTimer(playSound)
        }
    }

    // Run the timer logic
    private suspend fun runTimer(playSound: (Int) -> Unit) {
        runPrepTimer(playSound)
        while (currentRound <= rounds) {
            playSound(R.raw.round_start)
            runRoundTimer(playSound)
            playSound(R.raw.round_end)
            if (currentRound == rounds) {
                finishTimer()
                break
            }
            runRestTimer(playSound)
            incrementCurrentRound()
            resetCurrentRoundTime()
            resetCurrentRestTime()
        }
    }

    // Run the preparation timer
    private suspend fun runPrepTimer(playSound: (Int) -> Unit) {
        _currentTimer.value = TimerType.PREP
        while (_currentPrepTime.value >= Duration.ZERO) {
            if (_currentPrepTime.value in 0.seconds..2.seconds)
                playSound(R.raw.beep)
            delay(1000)
            decrementCurrentPrepTime()
        }
    }

    // Run the round timer
    private suspend fun runRoundTimer(playSound: (Int) -> Unit) {
        _currentTimer.value = TimerType.ROUND
        while (_currentRoundTime.value >= Duration.ZERO) {
            if (_currentRoundTime.value == 10.seconds)
                playSound(R.raw.ten_second_warning)
            delay(1000)
            decrementCurrentRoundTime()
        }
    }

    // Run the rest timer
    private suspend fun runRestTimer(playSound: (Int) -> Unit) {
        _currentTimer.value = TimerType.REST
        while (_currentRestTime.value >= Duration.ZERO && currentRound < rounds) {
            if (_currentRestTime.value == 10.seconds)
                playSound(R.raw.ten_second_warning)
            if (_currentRestTime.value in 0.seconds..2.seconds)
                playSound(R.raw.beep)
            delay(1000)
            decrementCurrentRestTime()
        }
    }

    private fun finishTimer() {
        _currentTimer.value = TimerType.FINISHED
        _isTimerFinished.value = true
        stopTimer(false)
    }

    fun pauseTimer() {
        paused.value = true
        timerJob?.cancel()
    }

    fun stopTimer(reset: Boolean) {
        timerJob?.cancel()
        if (reset) reset()
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
        resetTimers()
        _currentTimer.value = TimerType.PREP
        _isTimerFinished.value = false
    }

    private fun decrementCurrentRoundTime() {
        if (_currentRoundTime.value >= 0.seconds) {
            _currentRoundTime.value -= 1.seconds
        }
    }

    private fun decrementCurrentRestTime() {
        if (_currentRestTime.value >= 0.seconds) {
            _currentRestTime.value -= 1.seconds
        }
    }

    private fun decrementCurrentPrepTime() {
        if (_currentPrepTime.value >= 0.seconds) {
            _currentPrepTime.value -= 1.seconds
        }
    }

    private fun resetCurrentRestTime() {
        _currentRestTime.value = restTime
    }

    private fun resetCurrentRoundTime() {
        _currentRoundTime.value = roundLength
    }

    private fun incrementCurrentRound() {
        currentRound++
    }

    fun incrementRoundLength() {
        updateDuration(::roundLength, 5.seconds, _currentRoundTime)
    }

    fun decrementRoundLength() {
        updateDuration(::roundLength, (-5).seconds, _currentRoundTime)
    }

    fun incrementRestTime() {
        updateDuration(::restTime, 5.seconds, _currentRestTime)
    }

    fun decrementRestTime() {
        updateDuration(::restTime, (-5).seconds, _currentRestTime)
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
        updateDuration(::prepTime, 5.seconds, _currentPrepTime)
    }

    fun decrementPrepTime() {
        updateDuration(::prepTime, (-5).seconds, _currentPrepTime)
    }

    private fun updateCurrentPreset() {
        val index = _allPresets.value.indexOfFirst { it.id == activePreset.value!!.id }

        activePreset.value!!.rounds = rounds
        activePreset.value!!.roundLength = roundLength
        activePreset.value!!.restTime = restTime
        activePreset.value!!.prepTime = prepTime

        _allPresets.value[index] = activePreset.value!!

        CoroutineScope(Dispatchers.IO).launch {
            repository.updatePreset(activePreset.value!!.toEntityModel())
        }
    }

    fun setActivePreset(preset: Preset) {
        _activePreset.value = preset

        roundLength = activePreset.value!!.roundLength
        restTime = activePreset.value!!.restTime
        prepTime = activePreset.value!!.prepTime
        rounds = activePreset.value!!.rounds

        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
        _currentPrepTime.value = prepTime

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateActivePreset(preset.id)
        }
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
            updatePresetList()

            val insertedPreset = repository.getPresetByName("${preset.name} (Copy)")
            setActivePreset(insertedPreset!!.toDomainModel())
        }
    }

    fun deletePreset(preset: Preset) {
        CoroutineScope(Dispatchers.IO).launch {
            _allPresets.value.remove(preset) // Remove the preset from the list before deleting it because database can take longer to update
            setActivePreset(_allPresets.value[0])
            repository.deletePreset(preset.toEntityModel())
            updatePresetList()
        }
    }

    fun updatePresetName(preset: Preset, name: String) {
        preset.name = name
        _allPresets.value.find { it.id == preset.id }?.name = name
        CoroutineScope(Dispatchers.IO).launch {
            repository.updatePreset(preset.toEntityModel())
        }
    }

    fun toggleThemeMode() {
        _themeMode.value = when (_themeMode.value) {
            ThemeMode.LIGHT -> ThemeMode.DARK
            ThemeMode.DARK -> ThemeMode.SYSTEM
            ThemeMode.SYSTEM -> ThemeMode.LIGHT
        }

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateThemeMode(_themeMode.value.ordinal)
        }
    }

    fun toggleAlwaysOn() {
        _alwaysOn.value = !_alwaysOn.value

        CoroutineScope(Dispatchers.IO).launch {
            repository.updateAlwaysOn(if (_alwaysOn.value) 1 else 0)
        }
    }

    // Helper function to update preset values
    private fun updatePresetValues(preset: Preset) {
        roundLength = preset.roundLength
        restTime = preset.restTime
        prepTime = preset.prepTime
        rounds = preset.rounds
        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
        _currentPrepTime.value = prepTime
    }

    // Helper function to update the list of presets
    private fun updatePresetList() {
        _allPresets.value = repository.getAllPresets().map { it.toDomainModel() }.toMutableList()
    }

    // Helper function to update durations and the corresponding state flow
    private fun updateDuration(
        durationProperty: KMutableProperty0<Duration>,
        increment: Duration,
        stateFlow: MutableStateFlow<Duration>
    ) {
        if (durationProperty.get() + increment <= 0.seconds) return
        durationProperty.set(durationProperty.get().plus(increment))
        stateFlow.value = durationProperty.get()
        updateCurrentPreset()
    }

    // Helper function to reset timers
    private fun resetTimers() {
        _currentPrepTime.value = prepTime
        _currentRoundTime.value = roundLength
        _currentRestTime.value = restTime
    }

    fun calculateTotalTime(): Duration {
        return (roundLength + restTime) * rounds
    }

    fun isPaused() = paused
    fun hasStarted() = started
    fun getCurrentRound() = currentRound.toString()
    fun getFormattedRounds() = rounds.toString()
    fun getFormattedRoundLength() = TextUtils.formattedDuration(roundLength)
    fun getFormattedRestTime() = TextUtils.formattedDuration(restTime)
    fun getFormattedPrepTime() = TextUtils.formattedDuration(prepTime)
    fun getFormattedCurrentRoundTime(duration: Duration)= TextUtils.formattedDuration(duration)
    fun getFormattedCurrentRestTime(duration: Duration) = TextUtils.formattedDuration(duration)
    fun getFormattedCurrentPrepTime(duration: Duration) = TextUtils.formattedDuration(duration)
}