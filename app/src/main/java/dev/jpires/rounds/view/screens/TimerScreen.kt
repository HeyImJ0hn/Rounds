package dev.jpires.rounds.view.screens

import android.content.Context
import android.media.MediaPlayer
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.jpires.rounds.model.data.TimerType
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun TimerScreen(viewModel: ViewModel, navController: NavController) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.reset()
        viewModel.startTimer { soundResId -> playSound(context, soundResId) }
    }

    val isTimerFinished by viewModel.isTimerFinished.collectAsState()

    LaunchedEffect(isTimerFinished) {
        if (isTimerFinished) {
            navController.navigate("finished_screen")
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        TimerTop(viewModel, Modifier.weight(1f))
        TimerCenter(viewModel, Modifier.weight(1f))
        TimerBottom(viewModel, Modifier.weight(1f), navController)
    }
}

fun playSound(context: Context, soundResId: Int) {
    MediaPlayer.create(context, soundResId).apply {
        start()
        setOnCompletionListener { release() }
    }
}

@Composable
fun TimerTop(viewModel: ViewModel, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            TimerTopText(viewModel)
            SkipRoundButton(viewModel)
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
    OutlinedButton(
        onClick = { viewModel.skipTimer() },
        border = BorderStroke(2.dp, MaterialTheme.colorScheme.onBackground),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 144.dp)
    ) {
        Text(
            text = "SKIP",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 18.sp
        )
    }
}

@Composable
fun TimerTopTextRoundType(viewModel: ViewModel) {
    val currentTimer by viewModel.currentTimer.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = currentTimer.toString(),
            color = MaterialTheme.colorScheme.tertiary,
            fontWeight = FontWeight.Black,
            fontSize = 32.sp
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
            TimerType.FINISHED -> viewModel.getFormattedZero()
        },
        fontSize = 96.sp,
        fontWeight = FontWeight.Black
    )

}

@Composable
fun TotalTime(viewModel: ViewModel) {
    val currentTimer by viewModel.currentTimer.collectAsState()

    Text(
        text = when (currentTimer) {
            TimerType.PREP -> viewModel.getFormattedPrepTime()
            TimerType.ROUND -> viewModel.getFormattedRoundLength()
            TimerType.REST -> viewModel.getFormattedRestTime()
            TimerType.FINISHED -> viewModel.getFormattedRoundLength()
        },
        fontSize = 24.sp,
        fontWeight = FontWeight.Normal,
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
fun TimerMiddleLine() {
    Canvas(
        modifier = Modifier
            .width(96.dp)
            .height(2.dp)
    ) {
        drawLine(
            color = Color.Red,
            start = Offset(0f, size.height / 2),
            end = Offset(size.width, size.height / 2),
            strokeWidth = 2f
        )
    }
}

@Composable
fun TimerCenter(viewModel: ViewModel, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            CentralTimer(viewModel)
            TimerMiddleLine()
            TotalTime(viewModel)
        }
    }
}

@Composable
fun TimerBottom(viewModel: ViewModel, modifier: Modifier = Modifier, navController: NavController) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (viewModel.isPaused())
                PausedButtons(viewModel, navController)
            else
                Pause(viewModel)
        }
    }
}

@Composable
fun Pause(viewModel: ViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { viewModel.pauseTimer() },
            modifier = Modifier.size(96.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Pause,
                contentDescription = "Pause",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun PausedButtons(viewModel: ViewModel, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Resume
        IconButton(onClick = { viewModel.startTimer { soundResId -> playSound(context, soundResId) } }, modifier = Modifier.size(96.dp)) {
            Icon(
                imageVector = Icons.Rounded.PlayArrow,
                contentDescription = "Resume",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(48.dp)
            )
        }
        Spacer(modifier = Modifier.width(48.dp))
        // Stop
        IconButton(onClick = {
            showDialog = true
        }, modifier = Modifier.size(96.dp)) {
            Icon(
                imageVector = Icons.Rounded.Stop,
                contentDescription = "Stop",
                tint = MaterialTheme.colorScheme.tertiary,
                modifier = Modifier.size(48.dp)
            )
        }

        if (showDialog) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                text = {
                    Text(
                        text = "Are you sure you want to exit training?",
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                dismissButton = {
                    Button(
                        onClick = { showDialog = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                        )
                    ) {
                        Text("No", color = MaterialTheme.colorScheme.onBackground)
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showDialog = false
                            navController.navigate("home")
                            viewModel.stopTimer()
                          },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                    ) {
                        Text("Yes", color = MaterialTheme.colorScheme.onBackground)
                    }
                }
            )
        }
    }
}