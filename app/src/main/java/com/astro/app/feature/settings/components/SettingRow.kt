package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme

/**
 * Ligne de réglage : un libellé, une explication facultative, et une commande à droite.
 *
 * Composant partagé pour que toutes les sections des Paramètres se ressemblent, quel
 * que soit le type de réglage qu'elles portent.
 */
@Composable
fun SettingRow(
    label: String,
    modifier: Modifier = Modifier,
    description: String? = null,
    control: @Composable () -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = dimens.spaceSm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f).padding(end = dimens.spaceMd)) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = palette.ink)
            if (description != null) {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.inkFaint,
                    modifier = Modifier.padding(top = 2.dp),
                )
            }
        }
        control()
    }
}

/** Interrupteur aux couleurs de l'ambiance courante. */
@Composable
fun AstroSwitch(checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    val palette = AstroTheme.palette
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        colors = SwitchDefaults.colors(
            checkedThumbColor = palette.skyTop,
            checkedTrackColor = palette.accentSolar,
            checkedBorderColor = palette.accentSolar,
            uncheckedThumbColor = palette.inkFaint,
            uncheckedTrackColor = palette.surface,
            uncheckedBorderColor = palette.surfaceBorder,
        ),
    )
}
