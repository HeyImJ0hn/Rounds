package dev.jpires.rounds.view.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.BrightnessAuto
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.DeleteForever
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.LightMode
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.jpires.rounds.model.data.ThemeMode
import dev.jpires.rounds.view.composables.Alert
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun Screen(viewModel: ViewModel) {
    PortraitSheetScreen(viewModel)
}

@Composable
fun PortraitSheetScreen(viewModel: ViewModel) {
    LazyColumn(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        items(1) {
            PresetNameOptions(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            PresetOptionGroup(viewModel)
            Spacer(modifier = Modifier.height(16.dp))
            Divider()
            Spacer(modifier = Modifier.height(16.dp))
            Settings(viewModel)
            Spacer(modifier = Modifier.height(48.dp))
        }
    }
}

@Composable
fun OptionsButtonGroup(onButtonPlusClick: () -> Unit, onButtonMinusClick: () -> Unit) {
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
fun PresetNameOptions(viewModel: ViewModel) {
    val context = LocalContext.current

    val presets by viewModel.allPresets.collectAsState()
    val activePreset by viewModel.activePreset.collectAsState()

    var showMenuDropdown by rememberSaveable { mutableStateOf(false) }
    var editEnabled by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by rememberSaveable { mutableStateOf(false) }
    var text by rememberSaveable { mutableStateOf(activePreset?.name ?: "") }
    var isError by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(activePreset) {
        text = activePreset?.name ?: ""
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            value = if (editEnabled) text.trim() else activePreset!!.name,
            onValueChange = { text = it },
            label = { Text("Preset") },
            modifier = Modifier
                .clickable { showMenuDropdown = true }
                .weight(1f),
            singleLine = true,
            enabled = editEnabled,
            isError = isError
        )
        DropdownMenu(
            expanded = showMenuDropdown,
            onDismissRequest = {
                showMenuDropdown = false
            },
            modifier = Modifier
                .weight(1f)
                .background(MaterialTheme.colorScheme.surfaceVariant),
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
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .fillMaxWidth()
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        if (editEnabled)
            IconButton(onClick = {
                showMenuDropdown = false

                if (text.isNotEmpty()) {
                    editEnabled = false
                    viewModel.updatePresetName(activePreset!!, text.trim())
                }

                isError = text.isEmpty()

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
            if (presets.size == 1) {
                Toast.makeText(
                    context,
                    "You can't delete the only preset",
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                showDeleteDialog = true
            }
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

    if (showDeleteDialog) {
        Alert(
            text = "Are you sure you want to delete this preset?",
            dismissButtonText = "No",
            confirmButtonText = "Yes",
            icon = Icons.Rounded.DeleteForever,
            onDismiss = { showDeleteDialog = false },
            onConfirm = {
                showDeleteDialog = false
                viewModel.deletePreset(activePreset!!)
            }
        )
    }
}

@Composable
fun PresetOptionGroup(viewModel: ViewModel) {
    Column {
        PresetOption(
            textTop = viewModel.getFormattedRoundLength(),
            textBottom = "Round Length",
            onButtonPlusClick = { viewModel.incrementRoundLength() },
            onButtonMinusClick = { viewModel.decrementRoundLength() }
        )
        PresetOption(
            textTop = viewModel.getFormattedRestTime(),
            textBottom = "Rest Time",
            onButtonPlusClick = { viewModel.incrementRestTime() },
            onButtonMinusClick = { viewModel.decrementRestTime() }
        )
        PresetOption(
            textTop = viewModel.getFormattedRounds(),
            textBottom = "Rounds",
            onButtonPlusClick = { viewModel.incrementRounds() },
            onButtonMinusClick = { viewModel.decrementRounds() }
        )
        PresetOption(
            textTop = viewModel.getFormattedPrepTime(),
            textBottom = "Prep Time",
            onButtonPlusClick = { viewModel.incrementPrepTime() },
            onButtonMinusClick = { viewModel.decrementPrepTime() }
        )
    }
}

@Composable
fun PresetOption(
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
        OptionsButtonGroup(onButtonPlusClick, onButtonMinusClick)
    }
}

@Composable
fun Settings(viewModel: ViewModel) {
    Column {
        ButtonSetting(viewModel)
    }
}

@Composable
fun ButtonSetting(viewModel: ViewModel) {
    val theme by viewModel.themeMode.collectAsState()

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        LabelText(textTop = "Theme", textBottom = "Change app theme")
        Spacer(modifier = Modifier.weight(1f))
        IconButton(
            onClick = {
                viewModel.toggleThemeMode()
            }
        ) {
            Icon(
                imageVector = when (theme) {
                    ThemeMode.DARK -> Icons.Rounded.DarkMode
                    ThemeMode.LIGHT -> Icons.Rounded.LightMode
                    ThemeMode.SYSTEM -> Icons.Rounded.BrightnessAuto
                },
                contentDescription = when (theme) {
                    ThemeMode.DARK -> "Dark Mode"
                    ThemeMode.LIGHT -> "Light Mode"
                    ThemeMode.SYSTEM -> "System Mode"
                },
                tint = Color.Red,
                modifier = Modifier.size(48.dp)
            )
        }
    }
}
