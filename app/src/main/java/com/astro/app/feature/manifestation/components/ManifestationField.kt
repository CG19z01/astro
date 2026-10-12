package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme

/** Champ de saisie d'une manifestation, aux couleurs de l'ambiance. */
@Composable
fun ManifestationField(
    value: String,
    label: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    val palette = AstroTheme.palette
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        singleLine = singleLine,
        minLines = minLines,
        modifier = modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = palette.accentSolar,
            unfocusedBorderColor = palette.surfaceBorder,
            focusedTextColor = palette.ink,
            unfocusedTextColor = palette.ink,
            focusedLabelColor = palette.accentSolar,
            unfocusedLabelColor = palette.inkMuted,
            cursorColor = palette.accentSolar,
        ),
    )
}
