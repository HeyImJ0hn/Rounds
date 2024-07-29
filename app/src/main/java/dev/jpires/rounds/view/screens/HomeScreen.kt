package dev.jpires.rounds.view.screens

import android.content.res.Resources.Theme
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.PlaylistAdd
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.PlaylistAdd
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.MenuItemColors
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.window.core.layout.WindowWidthSizeClass
import dev.jpires.rounds.ui.theme.RoundsTheme
import dev.jpires.rounds.view.composables.MainButton
import dev.jpires.rounds.viewmodel.ViewModel

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
    var text by remember { mutableStateOf(viewModel.getActivePresetName()) }

    var showMenuDropdown by rememberSaveable { mutableStateOf(false) }
    var selectedItem by rememberSaveable { mutableStateOf(viewModel.getActivePresetName()) }

    var editEnabled by rememberSaveable { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
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
                for (preset in viewModel.getAllPresets()) {
                    DropdownMenuItem(
                        text = { Text(preset.name) },
                        onClick = { showMenuDropdown = false },
                        colors = MenuDefaults.itemColors(
                            textColor = MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier
                            .clip(RectangleShape)
                            .background(MaterialTheme.colorScheme.background)
                            .padding(horizontal = 16.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            if (editEnabled)
                IconButton(onClick = { editEnabled = false }) {
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
                IconButton(onClick = { editEnabled = true }) {
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
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.PlaylistAdd,
                    contentDescription = "Add",
                    tint = Color.Red,
                    modifier = Modifier
                        .size(48.dp)
                        .weight(1f)
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            IconButton(onClick = { /*TODO*/ }) {
                Icon(
                    imageVector = Icons.Rounded.Delete,
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