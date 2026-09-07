package com.astro.app.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme

/**
 * Choix d'une couleur quelconque : roue chromatique et curseur de luminosité.
 *
 * La teinte et la saturation viennent de la roue, la luminosité du curseur. Les trois
 * réunies couvrent l'espace des couleurs, ce que la palette de pastilles ne fait pas.
 *
 * L'aperçu est large et posé sur le fond de la carte : juger une couleur sur une
 * pastille de trente points est illusoire.
 */
@Composable
fun ColorWheelDialog(
    initial: Int,
    title: String,
    confirmLabel: String,
    dismissLabel: String,
    brightnessLabel: String,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val start = remember(initial) { hsvOf(initial) }

    var hue by remember { mutableFloatStateOf(start[0]) }
    var saturation by remember { mutableFloatStateOf(start[1]) }
    var value by remember { mutableFloatStateOf(start[2].coerceAtLeast(0.15f)) }
    val current = Color.hsv(hue, saturation, value)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = title, style = MaterialTheme.typography.titleLarge) },
        confirmButton = {
            TextButton(onClick = { onConfirm(current.toArgb()) }) {
                Text(text = confirmLabel, color = palette.accentSolar)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text(text = dismissLabel, color = palette.inkMuted) }
        },
        text = {
            Column {
                ColorWheel(
                    value = value,
                    selected = current,
                    onPick = { pickedHue, pickedSaturation ->
                        hue = pickedHue
                        saturation = pickedSaturation
                    },
                )
                Text(
                    text = brightnessLabel,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.inkMuted,
                    modifier = Modifier.padding(top = dimens.spaceMd),
                )
                Slider(
                    value = value,
                    onValueChange = { value = it },
                    valueRange = 0.15f..1f,
                    colors = SliderDefaults.colors(
                        thumbColor = palette.accentSolar,
                        activeTrackColor = palette.accentSolar,
                        inactiveTrackColor = palette.surfaceBorder,
                    ),
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceSm),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
                ) {
                    Box(modifier = Modifier.size(36.dp).clip(CircleShape).background(current))
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(36.dp)
                            .clip(MaterialTheme.shapes.medium)
                            .background(current),
                    )
                }
            }
        },
    )
}

/** Teinte, saturation et luminosité d'une couleur ARGB. */
private fun hsvOf(argb: Int): FloatArray {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(argb, hsv)
    return hsv
}
