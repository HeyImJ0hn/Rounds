package dev.jpires.rounds.view.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import dev.jpires.rounds.ui.theme.RoundsTheme
import dev.jpires.rounds.viewmodel.ViewModel
import java.util.logging.Logger

@Composable
fun HomeScreen(viewModel: ViewModel, navController: NavController) {
    RoundsTheme {
        Column(
        ) {
            TopBar(viewModel)
            Screen(viewModel)
        }
    }

}

@Composable
fun Screen(viewModel: ViewModel) {
    val windowSize = currentWindowAdaptiveInfo().windowSizeClass.windowWidthSizeClass

    if (windowSize != WindowWidthSizeClass.EXPANDED)
        PortraitHomeScreen(viewModel)
    else
        LandscapeHomeScreen(viewModel)
}

@Composable
fun PortraitHomeScreen(viewModel: ViewModel) {
    val presets by viewModel.allPresets.collectAsState()
    val activePreset by viewModel.activePreset.collectAsState()

    var showMenuDropdown by rememberSaveable { mutableStateOf(false) }
    var editEnabled by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(activePreset?.name ?: "") }

    LaunchedEffect(activePreset) {
        text = activePreset?.name ?: ""
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = if (editEnabled) text else activePreset!!.name,
                onValueChange = { text = it },
                label = { Text("Preset") },
                modifier = Modifier
                    .clickable { showMenuDropdown = true }
                    .weight(1f),
                singleLine = true,
                enabled = editEnabled
            )
            DropdownMenu(
                expanded = showMenuDropdown,
                onDismissRequest = {
                    showMenuDropdown = false
                },
                modifier = Modifier
                    .weight(1f)
                    .background(MaterialTheme.colorScheme.background),
            ) {
                for (preset in presets) {
                    if (preset.id == activePreset!!.id)
                        continue
                    DropdownMenuItem(
                        text = { Text(preset.name) },
                        onClick = {
                            showMenuDropdown = false
                            viewModel.setActivePreset(preset)
                        },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (editEnabled)
                IconButton(onClick = {
                    showMenuDropdown = false
                    editEnabled = false
                    viewModel.updatePresetName(activePreset!!, text)
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Save,
                        contentDescription = "Save",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(48.dp)
                            .weight(1f)
                    )
                }
            else
                IconButton(onClick = {
                    showMenuDropdown = false
                    editEnabled = true
                }) {
                    Icon(
                        imageVector = Icons.Rounded.Edit,
                        contentDescription = "Edit",
                        tint = Color.Red,
                        modifier = Modifier
                            .size(48.dp)
                            .weight(1f)
                    )
                }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = {
                showMenuDropdown = false
                viewModel.duplicatePreset(activePreset!!)
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    contentDescription = "Duplicate",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(48.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = {
                showMenuDropdown = false
                showDeleteDialog = true
            }) {
                Icon(
                    imageVector = Icons.Rounded.DeleteForever,
                    contentDescription = "Delete",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(48.dp)
                        .weight(1f)
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Divider()
        ScreenRow(
            textTop = viewModel.getFormattedRoundLength(),
            textBottom = "Round Length",
            onButtonPlusClick = { viewModel.incrementRoundLength() },
            onButtonMinusClick = { viewModel.decrementRoundLength() }
        )
        ScreenRow(
            textTop = viewModel.getFormattedRestTime(),
            textBottom = "Rest Time",
            onButtonPlusClick = { viewModel.incrementRestTime() },
            onButtonMinusClick = { viewModel.decrementRestTime() }
        )
        ScreenRow(
            textTop = viewModel.getFormattedRounds(),
            textBottom = "Rounds",
            onButtonPlusClick = { viewModel.incrementRounds() },
            onButtonMinusClick = { viewModel.decrementRounds() }
        )
        ScreenRow(
            textTop = viewModel.getFormattedPrepTime(),
            textBottom = "Prep Time",
            onButtonPlusClick = { viewModel.incrementPrepTime() },
            onButtonMinusClick = { viewModel.decrementPrepTime() }
        )
        Spacer(modifier = Modifier.height(48.dp))
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            text = {
                Text(
                    text = "Are you sure you want to delete this preset?",
                    color = MaterialTheme.colorScheme.onBackground
                )
            },
            icon = {
                Icon(
                    imageVector = Icons.Rounded.DeleteForever,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false },
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
                        showDeleteDialog = false
                        viewModel.deletePreset(activePreset!!)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
                ) {
                    Text("Yes", color = MaterialTheme.colorScheme.onBackground)
                }
            }
        )
    }
}

@Composable
fun LandscapeHomeScreen(viewModel: ViewModel) {
    Row(
        modifier = Modifier.fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .weight(1f)
        ) {
            items(1) {
                ScreenRow(
                    textTop = viewModel.getFormattedRoundLength(),
                    textBottom = "Round Length",
                    onButtonPlusClick = { viewModel.incrementRoundLength() },
                    onButtonMinusClick = { viewModel.decrementRoundLength() }
                )
                ScreenRow(
                    textTop = viewModel.getFormattedRestTime(),
                    textBottom = "Rest Time",
                    onButtonPlusClick = { viewModel.incrementRestTime() },
                    onButtonMinusClick = { viewModel.decrementRestTime() }
                )
                ScreenRow(
                    textTop = viewModel.getFormattedRounds(),
                    textBottom = "Rounds",
                    onButtonPlusClick = { viewModel.incrementRounds() },
                    onButtonMinusClick = { viewModel.decrementRounds() }
                )
                ScreenRow(
                    textTop = viewModel.getFormattedPrepTime(),
                    textBottom = "Prep Time",
                    onButtonPlusClick = { viewModel.incrementPrepTime() },
                    onButtonMinusClick = { viewModel.decrementPrepTime() }
                )
            }
        }
        LazyColumn(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            items(1) {
            }
        }
    }
}

@Composable
fun Buttons(onButtonPlusClick: () -> Unit, onButtonMinusClick: () -> Unit) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onButtonMinusClick, modifier = Modifier.size(72.dp)) {
            Icon(
                imageVector = Icons.Rounded.Remove,
                contentDescription = "Decrement",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        }
        IconButton(onClick = onButtonPlusClick, modifier = Modifier.size(72.dp)) {
            Icon(
                imageVector = Icons.Rounded.Add,
                contentDescription = "Increment",
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

@Composable
fun LabelText(textTop: String, textBottom: String) {
    Column {
        Text(text = textTop, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
        Text(text = textBottom, fontSize = 18.sp, fontWeight = FontWeight.Normal)
    }
}

@Composable
fun ScreenRow(
    textTop: String,
    textBottom: String,
    onButtonPlusClick: () -> Unit,
    onButtonMinusClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        LabelText(textTop, textBottom)
        Spacer(modifier = Modifier.weight(1f))
        Buttons(onButtonPlusClick, onButtonMinusClick)
    }
}

@Composable
fun TopBar(viewModel: ViewModel) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(vertical = 8.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Training Length: ${viewModel.getFormattedTotalTime()}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .drawBottomShadow(4.dp)
        )
    }
}

fun Modifier.drawBottomShadow(elevation: Dp): Modifier = this.then(
    Modifier.drawBehind {
        val shadowColor = Color(0xFF000000).copy(alpha = 0.10f)
        val paint = androidx.compose.ui.graphics.Paint().apply {
            color = shadowColor
        }

        val left = 0f
        val top = size.height
        val right = size.width
        val bottom = size.height + elevation.toPx()

        drawRect(
            brush = Brush.verticalGradient(
                colors = listOf(shadowColor, Color.Transparent),
                startY = top,
                endY = bottom
            ),
            topLeft = androidx.compose.ui.geometry.Offset(left, top),
            size = androidx.compose.ui.geometry.Size(right - left, bottom - top)
        )
    }
)