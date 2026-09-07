package com.astro.app.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.celestial.SunOrb

/**
 * Le Soleil de la page d'accueil : point d'entrée vers les Paramètres.
 *
 * Ce fichier n'ajoute que l'interaction ; la matière appartient à `SunOrb`, partagée
 * avec la carte des horaires du Soleil.
 */
@Composable
fun SunButton(
    size: Dp,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = contentDescription
    SunOrb(
        size = size,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = label },
    )
}
