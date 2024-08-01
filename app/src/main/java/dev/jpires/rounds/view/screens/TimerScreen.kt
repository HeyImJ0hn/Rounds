package dev.jpires.rounds.view.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Replay
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowWidthSizeClass
import dev.jpires.rounds.model.data.TimerType
import dev.jpires.rounds.ui.theme.RoundsTheme
import dev.jpires.rounds.utils.SoundUtils
import dev.jpires.rounds.utils.TextUtils
import dev.jpires.rounds.view.composables.Alert
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun MainScreen(viewModel: ViewModel) {
    val themeMode by viewModel.themeMode.collectAsState()

    RoundsTheme(
        themeMode = themeMode
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            TimerScreen(viewModel)
        }
    }
}

@Composable
fun TimerScreen(viewModel: ViewModel) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (windowSize != WindowWidthSizeClass.EXPANDED)
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            TimerTop(viewModel, Modifier.weight(1f))
            TimerCenter(viewModel, Modifier.weight(1f))
            TimerBottom(viewModel, Modifier.weight(1f))
        }
    else
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TimerCenter(viewModel, Modifier.weight(1f))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                TimerTop(viewModel, Modifier.weight(1f))
                TimerBottom(viewModel, Modifier.weight(1f))
            }
        }
}

@Composable
fun TimerTop(viewModel: ViewModel, modifier: Modifier = Modifier) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerTopText(viewModel)
            SkipRoundButton(viewModel)
            if (windowSize != WindowWidthSizeClass.EXPANDED)
                TimerTopTextRoundType(viewModel)
        }
    }
}

@Composable
fun TimerTopText(viewModel: ViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = viewModel.getCurrentRound(),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Black,
            fontSize = 64.sp
        )
        Text(
            text = "/${viewModel.getFormattedRounds()}",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 64.sp
        )
    }
}

@Composable
fun SkipRoundButton(viewModel: ViewModel) {
    val started by viewModel.hasStarted().collectAsState()

    OutlinedButton(
        enabled = started,
        onClick = { viewModel.skipTimer() },
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground)
    ) {
        Text(
            text = "SKIP",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )
    }
}

@Composable
fun TimerTopTextRoundType(viewModel: ViewModel, modifier: Modifier = Modifier) {
    val currentTimer by viewModel.currentTimer.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.padding(16.dp)
    ) {
        if (currentTimer != TimerType.FINISHED)
            Text(
                text = currentTimer.toString(),
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )
        else
            Text(
                text = "Total Time: ${TextUtils.formattedTotalTime(viewModel.calculateTotalTime())}",
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Black,
                fontSize = 24.sp
            )
    }
}

@Composable
fun CentralTimer(viewModel: ViewModel) {
    val currentPrepTime by viewModel.currentPrepTime.collectAsState()
    val currentRoundTime by viewModel.currentRoundTime.collectAsState()
    val currentRestTime by viewModel.currentRestTime.collectAsState()
    val currentTimer by viewModel.currentTimer.collectAsState()

    Text(
        text = when (currentTimer) {
            TimerType.PREP -> viewModel.getFormattedCurrentPrepTime(currentPrepTime)
            TimerType.ROUND -> viewModel.getFormattedCurrentRoundTime(currentRoundTime)
            TimerType.REST -> viewModel.getFormattedCurrentRestTime(currentRestTime)
            TimerType.FINISHED -> "DONE"
        },
        fontSize = 96.sp,
        fontWeight = FontWeight.Black
    )
}

@Composable
fun TimerCenter(viewModel: ViewModel, modifier: Modifier = Modifier) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        if (windowSize == WindowWidthSizeClass.EXPANDED)
            TimerTopTextRoundType(viewModel, Modifier.align(Alignment.TopCenter).padding(top = 48.dp))
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CentralTimer(viewModel)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimerBottom(viewModel: ViewModel, modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val paused by viewModel.isPaused().collectAsState()
    val started by viewModel.hasStarted().collectAsState()
    val isTimerFinished by viewModel.isTimerFinished.collectAsState()

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by rememberSaveable { mutableStateOf(false) }

    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            when {
                !started -> UiButtonGroup(listOf(
                    {
                        UiButton(icon = Icons.Rounded.PlayArrow) {
                            viewModel.startTimer { soundResId ->
                                SoundUtils.playSound(context, soundResId)
                            }
                        }
                    },
                    {
                        UiButton(icon = Icons.Rounded.Settings) {
                            showBottomSheet = true
                        }
                    }
                ))
                isTimerFinished -> UiButtonGroup(listOf { UiButton(icon = Icons.Rounded.Replay) { viewModel.reset() } })
                paused -> UiButtonGroup(listOf(
                    {
                        UiButton(icon = Icons.Rounded.PlayArrow) {
                            viewModel.startTimer { soundResId ->
                                SoundUtils.playSound(context, soundResId )
                            }
                        }
                    },
                    {
                        UiButton(icon = Icons.Rounded.Stop) {
                            showDialog = true
                        }
                    }
                ))
                else -> UiButtonGroup(listOf { UiButton(icon = Icons.Rounded.Pause) { viewModel.pauseTimer() } })
            }
        }
    }

    if (showDialog) {
        Alert(
            text = "Are you sure you want to stop training?",
            dismissButtonText = "No",
            confirmButtonText = "Yes",
            icon = Icons.Rounded.Stop,
            onDismiss = { showDialog = false },
            onConfirm = {
                showDialog = false
                viewModel.stopTimer(true)
            }
        )
    }

    if (showBottomSheet) {
        BottomSheet(
            onDismiss = { showBottomSheet = false },
            viewModel = viewModel,
            sheetState = sheetState
        )
    }
}

@Composable
fun UiButtonGroup(buttons: List<@Composable () -> Unit>) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(48.dp)
    ) {
        buttons.forEach { button ->
            button()
        }
    }
}

@Composable
fun UiButton(icon: ImageVector, action: () -> Unit) {
    IconButton(
        onClick = action,
        modifier = Modifier.size(96.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = icon.name,
            tint = MaterialTheme.colorScheme.tertiary,
            modifier = Modifier.size(48.dp)
        )
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheet(onDismiss: () -> Unit, viewModel: ViewModel, sheetState: SheetState) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        tonalElevation = 16.dp
    ) {
        Column(
        ) {
            Screen(viewModel)
        }
    }
}