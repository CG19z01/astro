package com.astro.app.core.designsystem.texture

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/**
 * Direction de la lumière, commune à toute l'application.
 *
 * Une seule source, en haut à droite. C'est ce qui fait que la Lune, le Soleil et les
 * billes semblent appartenir à la même scène : des éclairages contradictoires
 * trahissent immédiatement un assemblage d'éléments dessinés séparément.
 */
object AstroLight {
    const val X = 0.42f
    const val Y = -0.46f
}

/**
 * Matière sphérique : les couches qui donnent le volume.
 *
 * Elles s'emploient dans cet ordre — corps, ombre interne, liseré, halo — chacune
 * étant transparente là où elle n'agit pas. Aucune ne suffit seule : c'est leur
 * superposition qui remplace l'impression de cercle plat par celle d'un objet éclairé.
 */
fun DrawScope.drawSphereBody(
    center: Offset,
    radius: Float,
    lit: Color,
    shade: Color,
) {
    val highlight = Offset(center.x + radius * AstroLight.X, center.y + radius * AstroLight.Y)
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(0f to lit, 0.55f to lerp(lit, shade, 0.6f), 1f to shade),
            center = highlight,
            radius = radius * 1.65f,
        ),
        radius = radius,
        center = center,
    )
}

/**
 * Assombrissement du bord opposé à la lumière.
 * C'est cette couche qui creuse l'objet : sans elle, le corps reste un disque teinté.
 */
fun DrawScope.drawSphereInnerShadow(center: Offset, radius: Float, shadow: Color) {
    val away = Offset(center.x - radius * AstroLight.X * 1.5f, center.y - radius * AstroLight.Y * 1.5f)
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(0f to shadow, 0.75f to shadow.copy(alpha = shadow.alpha * 0.35f), 1f to Color.Transparent),
            center = away,
            radius = radius * 1.5f,
        ),
        radius = radius,
        center = center,
    )
}

/** Liseré lumineux : fin, présent seulement du côté éclairé. */
fun DrawScope.drawSphereRim(center: Offset, radius: Float, rim: Color, width: Float) {
    drawCircle(
        brush = Brush.linearGradient(
            colors = listOf(rim, rim.copy(alpha = rim.alpha * 0.25f), Color.Transparent),
            start = Offset(center.x + radius * AstroLight.X * 2f, center.y + radius * AstroLight.Y * 2f),
            end = Offset(center.x - radius * AstroLight.X * 2f, center.y - radius * AstroLight.Y * 2f),
        ),
        radius = radius - width / 2f,
        center = center,
        style = Stroke(width = width),
    )
}

/** Halo extérieur : décolle l'objet du fond sans le cerner d'un trait. */
fun DrawScope.drawSphereHalo(center: Offset, radius: Float, glow: Color, spread: Float) {
    val outer = radius * spread
    drawCircle(
        brush = Brush.radialGradient(
            colorStops = arrayOf(
                0f to Color.Transparent,
                (radius / outer) to glow,
                1f to Color.Transparent,
            ),
            center = center,
            radius = outer,
        ),
        radius = outer,
        center = center,
    )
}

/** Mélange linéaire de deux couleurs, canal par canal. */
fun lerp(from: Color, to: Color, amount: Float): Color = Color(
    red = from.red + (to.red - from.red) * amount,
    green = from.green + (to.green - from.green) * amount,
    blue = from.blue + (to.blue - from.blue) * amount,
    alpha = from.alpha + (to.alpha - from.alpha) * amount,
)
