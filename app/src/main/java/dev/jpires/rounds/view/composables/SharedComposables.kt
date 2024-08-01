package dev.jpires.rounds.view.composables

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun Alert(text: String, dismissButtonText: String, confirmButtonText: String, icon: ImageVector, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        icon = {
            Icon(
                imageVector = icon,
                contentDescription = icon.name,
                tint = MaterialTheme.colorScheme.onBackground
            )
        },
        onDismissRequest = onDismiss,
        text = {
            Text(
                text = text,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                )
            ) {
                Text(dismissButtonText, color = MaterialTheme.colorScheme.onBackground)
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent)
            ) {
                Text(confirmButtonText, color = MaterialTheme.colorScheme.onBackground)
            }
        }
    )
}