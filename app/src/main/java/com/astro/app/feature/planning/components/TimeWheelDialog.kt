package com.astro.app.feature.planning.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.designsystem.AstroTheme
import java.time.LocalTime

/**
 * Choix d'une heure sur deux roues : heures de 00 à 23, minutes de 5 en 5.
 *
 * Une fenêtre plutôt qu'un menu sous la ligne : elle ne pousse pas la liste, ne se
 * fait pas masquer par le clavier et tient sur les petits écrans.
 *
 * Rien n'est attribué tant que l'on n'a pas validé. [current] nul, les roues partent de
 * [suggestion] — l'heure actuelle, arrondie — sans que cette heure soit retenue.
 * « Retirer l'heure » n'apparaît que si la ligne en a une.
 */
@Composable
fun TimeWheelDialog(
    current: LocalTime?,
    suggestion: LocalTime,
    onConfirm: (LocalTime) -> Unit,
    onRemove: () -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = AstroTheme.palette
    val start = current ?: suggestion
    var hour by remember { mutableIntStateOf(start.hour) }
    var minute by remember { mutableIntStateOf(nearestMinute(start.minute)) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.planning_time_title), style = MaterialTheme.typography.titleLarge) },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(hour, minute)) }) {
                Text(stringResource(R.string.action_confirm), color = palette.accentSolar)
            }
        },
        dismissButton = {
            Row {
                if (current != null) {
                    TextButton(onClick = onRemove) {
                        Text(stringResource(R.string.planning_time_remove), color = palette.inkMuted)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.action_cancel), color = palette.inkMuted)
                }
            }
        },
        text = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AstroTheme.dimens.spaceMd, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                WheelPicker(
                    values = PlanningConfig.HOURS,
                    startIndex = PlanningConfig.HOURS.indexOf(hour),
                    description = stringResource(R.string.planning_time_hours),
                    onSelect = { hour = it },
                )
                Text(stringResource(R.string.planning_time_separator), style = MaterialTheme.typography.headlineMedium, color = palette.inkMuted)
                WheelPicker(
                    values = PlanningConfig.MINUTES,
                    startIndex = PlanningConfig.MINUTES.indexOf(minute),
                    description = stringResource(R.string.planning_time_minutes),
                    onSelect = { minute = it },
                )
            }
        },
    )
}

/** Minute proposée la plus proche, en restant dans l'heure. */
internal fun nearestMinute(minute: Int): Int =
    PlanningConfig.MINUTES.minBy { kotlin.math.abs(it - minute) }
