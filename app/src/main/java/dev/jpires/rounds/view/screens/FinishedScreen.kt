package dev.jpires.rounds.view.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import dev.jpires.rounds.view.composables.MainButton
import dev.jpires.rounds.view.navigation.BottomNavItem
import dev.jpires.rounds.viewmodel.ViewModel

@Composable
fun FinishedScreen(viewModel: ViewModel, navController: NavController) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
    ) {
        FinishedTop(viewModel, Modifier.weight(1f))
        FinishedCenter(viewModel, Modifier.weight(1f))
        FinishedBottom(Modifier.weight(1f), navController)
    }
}

@Composable
fun FinishedTop(viewModel: ViewModel, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FinishedTopText(viewModel)
        }
    }
}

@Composable
fun FinishedTopText(viewModel: ViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Time: ${viewModel.getFormattedTotalTime()}",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 24.sp
        )
    }
}

@Composable
fun FinishedCenter(viewModel: ViewModel, modifier: Modifier = Modifier) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            FinishedCenterText(viewModel)
        }
    }
}

@Composable
fun FinishedCenterText(viewModel: ViewModel) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(16.dp)
    ) {
        Text(
            text = "Finished!",
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Black,
            fontSize = 64.sp
        )
    }
}

@Composable
fun FinishedBottom(
    modifier: Modifier = Modifier,
    navController: NavController
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.fillMaxSize()
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            MainButton(text = "HOME") {
                navController.navigate(BottomNavItem.Home.route)
            }
        }
    }
}