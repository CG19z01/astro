package com.astro.app.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.time.TimeScale

/**
 * Les quatre temporalités, comme un cadran : on voit où l'on se trouve et ce qui vient
 * après. L'entrée active s'allume et porte un point ; les autres restent en retrait.
 *
 * Partagé entre le Calendrier, où il change la vue affichée, et les Paramètres, où il
 * choisit la vue d'ouverture : le même choix se présente de la même façon partout.
 */
@Composable
fun TimeScaleSelector(
    selected: TimeScale,
    onSelect: (TimeScale) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        TimeScale.entries.forEach { entry ->
            ScaleEntry(
                entry = entry,
                active = entry == selected,
                onClick = { onSelect(entry) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ScaleEntry(entry: TimeScale, active: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val color by animateColorAsState(
        targetValue = if (active) palette.accentSolar else palette.inkFaint,
        animationSpec = AstroMotion.quick(),
        label = "scaleEntry",
    )
    Column(
        modifier = modifier
            .heightIn(min = dimens.touchTarget)
            .selectable(selected = active, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = stringResource(entry.labelRes),
            style = MaterialTheme.typography.labelLarge,
            color = color,
        )
        Canvas(modifier = Modifier.padding(top = dimens.spaceXs).size(dimens.spaceXs)) {
            drawCircle(color = color.copy(alpha = if (active) 1f else 0f))
        }
    }
}
