package com.astro.app.feature.home.components

import androidx.compose.ui.unit.Dp
import com.astro.app.core.config.HomeLayoutConfig.CircleSpec

/** Rectangle englobant d'un élément de la Home, exprimé en dp. */
data class HomeBounds(val x: Dp, val y: Dp, val size: Dp)

/**
 * Convertit une spécification en fractions d'écran en position et taille concrètes.
 *
 * Rappel de la convention : `centerX` et `radius` sont des fractions de la largeur,
 * `centerY` une fraction de la hauteur. Le diamètre est donc toujours dérivé de la
 * largeur, ce qui garde les cercles ronds quel que soit le format de l'écran.
 */
fun CircleSpec.bounds(width: Dp, height: Dp): HomeBounds {
    val diameter = width * (radius * 2f)
    return HomeBounds(
        x = width * centerX - diameter / 2f,
        y = height * centerY - diameter / 2f,
        size = diameter,
    )
}
