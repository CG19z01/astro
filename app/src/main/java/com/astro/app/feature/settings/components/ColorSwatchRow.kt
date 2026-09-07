package com.astro.app.feature.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
 * La palette vient de la configuration, pas de ce composant. La saisie d'une couleur
 * libre n'est pas offerte à ce stade.
 */
@Composable
fun ColorSwatchRow(
    label: String,
    selected: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Column(modifier = modifier.fillMaxWidth().padding(vertical = dimens.spaceSm)) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = palette.inkMuted,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceSm),
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        ) {
            PlanetColorDefaults.PALETTE.forEach { color ->
                Swatch(
                    color = Color(color),
                    selected = color == selected,
                    ring = palette.ink,
                    onClick = { onSelect(color) },
                )
            }
        }
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
