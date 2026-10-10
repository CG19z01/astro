package com.astro.app.feature.planning.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import com.astro.app.R
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.planning.PlanningEntry
import java.time.format.DateTimeFormatter

/**
 * Une ligne du planning : l'heure à gauche, le texte à droite (demandé le 2026-10-10) —
 * ou l'heure au-dessus quand l'écran est trop étroit ([PlanningConfig.INLINE_TIME_MIN_WIDTH]).
 */
@Composable
fun PlanningLineRow(
    line: PlanningEntry,
    onTextChange: (String) -> Unit,
    onPickTime: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        if (maxWidth >= PlanningConfig.INLINE_TIME_MIN_WIDTH) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
            ) {
                TimeChip(line, onPickTime)
                LineField(line, onTextChange, onLeave, Modifier.weight(1f))
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceXs)) {
                TimeChip(line, onPickTime)
                LineField(line, onTextChange, onLeave, Modifier.fillMaxWidth())
            }
        }
    }
}

@Composable
private fun LineField(line: PlanningEntry, onTextChange: (String) -> Unit, onLeave: () -> Unit, modifier: Modifier) {
    val palette = AstroTheme.palette
    // Le focus perdu, et seulement lui, déclenche le ménage des lignes vides : jamais
    // pendant la frappe, sans quoi une ligne disparaîtrait sous les doigts.
    var focused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = line.text,
        onValueChange = onTextChange,
        singleLine = true,
        placeholder = { Text(stringResource(R.string.planning_placeholder)) },
        textStyle = MaterialTheme.typography.bodyLarge,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        modifier = modifier.onFocusChanged { state ->
            if (focused && !state.isFocused) onLeave()
            focused = state.isFocused
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.accentSolar,
            unfocusedBorderColor = palette.surfaceBorder,
            focusedTextColor = palette.ink,
            unfocusedTextColor = palette.ink,
            cursorColor = palette.accentSolar,
        ),
    )
}

/** L'heure de la ligne, ou une invitation à en choisir une. Appuyer ouvre les roues. */
@Composable
private fun TimeChip(line: PlanningEntry, onPickTime: () -> Unit) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val label = line.time?.format(DateTimeFormatter.ofPattern("HH:mm"))
        ?: stringResource(R.string.planning_time_add)
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (line.time != null) palette.accentSolar else palette.inkFaint,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .width(PlanningConfig.TIME_CHIP_WIDTH)
            .heightIn(min = dimens.touchTarget)
            .border(dimens.hairline, palette.surfaceBorder, MaterialTheme.shapes.small)
            .clickable(role = Role.Button, onClick = onPickTime)
            .padding(vertical = dimens.spaceMd),
    )
}
