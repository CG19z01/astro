package com.astro.app.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.astro.app.core.config.PlanetColorDefaults
import com.astro.app.core.designsystem.AstroTheme

private val SWATCH_SIZE = 30.dp

/**
 * Choix d'une couleur parmi la palette proposée.
 *
 * La couleur retenue porte un anneau clair : sur des pastilles rondes de teintes
 * proches, un simple changement de taille ne se lirait pas.
 *
 * La palette vient de la configuration, pas de ce composant. Une dernière pastille,
 * marquée d'un plus, ouvre la roue chromatique et donne accès à toutes les couleurs.
 * Elle affiche la couleur courante lorsque celle-ci ne figure pas dans la palette :
 * sans cela, un choix personnalisé n'apparaîtrait nulle part.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ColorSwatchRow(
    label: String,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val custom = selected !in PlanetColorDefaults.PALETTE
    var wheelOpen by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = dimens.spaceSm)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.inkMuted,
        )
        // Disposition qui va à la ligne : neuf pastilles ne tiennent pas sur une seule,
        // et les serrer écraserait la dernière — celle qui ouvre la roue.
        FlowRow(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        ) {
            PlanetColorDefaults.PALETTE.forEach { color ->
                Swatch(
                    color = Color(color),
                    selected = color == selected,
                    ring = palette.ink,
                    onClick = { onSelect(color) },
                )
            }
            CustomSwatch(
                color = if (custom) Color(selected) else palette.surface,
                selected = custom,
                ring = palette.ink,
                plus = palette.ink,
                onClick = { wheelOpen = true },
            )
        }
    }

    if (wheelOpen) {
        ColorWheelDialog(
            initial = selected,
            title = stringResource(R.string.settings_color_wheel_title),
            confirmLabel = stringResource(R.string.settings_color_wheel_confirm),
            dismissLabel = stringResource(R.string.action_close),
            brightnessLabel = stringResource(R.string.settings_color_wheel_brightness),
            onConfirm = { color ->
                onSelect(color)
                wheelOpen = false
            },
            onDismiss = { wheelOpen = false },
        )
    }
}

@Composable
private fun Swatch(color: Color, selected: Boolean, ring: Color, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(SWATCH_SIZE)
            .clip(CircleShape)
            .background(color)
            .border(
                width = if (selected) 2.dp else 0.dp,
                color = if (selected) ring else Color.Transparent,
                shape = CircleShape,
            )
            .clickable(onClick = onClick),
    )
}
