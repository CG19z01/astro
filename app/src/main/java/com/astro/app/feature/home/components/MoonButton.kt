package com.astro.app.feature.home.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.celestial.MoonOrb

/**
 * La Lune de la page d'accueil : point d'entrée vers l'Astrologie.
 *
 * Ce fichier n'ajoute que l'interaction. La matière appartient à `MoonOrb`, partagée
 * avec les écrans d'information : la Lune est donc identique partout.
 */
@Composable
fun MoonButton(
    size: Dp,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val label = contentDescription
    MoonOrb(
        size = size,
        modifier = modifier
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = label },
    )
}
