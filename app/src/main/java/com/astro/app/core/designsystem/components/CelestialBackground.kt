package com.astro.app.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroPalette
import com.astro.app.core.designsystem.AstroTheme
import kotlin.random.Random

/** Peu d'étoiles, volontairement : au-delà, l'écran devient un papier peint. */
private const val STAR_COUNT = 26
private const val STAR_SEED = 20260906L

/** Opacités des halos. Très basses : ils doivent se deviner, pas se voir. */
private const val HALO_ALPHA_DARK = 0.20f
private const val HALO_ALPHA_LIGHT = 0.34f

/**
 * Fond commun à tous les écrans : dégradé de ciel, deux halos diffus qui respirent
 * lentement, et quelques étoiles.
 *
 * Tout est dessiné au Canvas plutôt qu'empilé en images : le rendu s'adapte à toute
 * taille d'écran, suit la palette de l'ambiance courante, et ne coûte ni mémoire ni
 * dépendance. La respiration des halos dure 14 secondes — assez lente pour ne pas
 * accrocher le regard de quelqu'un qui lit.
 */
@Composable
fun CelestialBackground(modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val transition = rememberInfiniteTransition(label = "ambient")
    val breath by transition.animateFloat(
        initialValue = 0.92f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(AstroMotion.AMBIENT_BREATH_MILLIS, easing = AstroMotion.Gentle),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "breath",
    )
    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) {
            Star(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat())
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to palette.skyTop,
                0.55f to palette.skyMid,
                1f to palette.skyBottom,
            ),
        )
        val haloAlpha = if (palette.isDark) HALO_ALPHA_DARK else HALO_ALPHA_LIGHT
        drawHalo(palette.haloSolar, haloAlpha, Offset(size.width * 0.86f, size.height * 0.14f), size.width * 0.78f * breath)
        drawHalo(palette.haloLunar, haloAlpha * 0.8f, Offset(size.width * 0.08f, size.height * 0.74f), size.width * 0.92f * (2f - breath))
        stars.forEach { drawStar(it, palette) }
    }
}

private class Star(val x: Float, val y: Float, val size: Float, val brightness: Float)

private fun DrawScope.drawHalo(color: Color, alpha: Float, center: Offset, radius: Float) {
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(color.copy(alpha = alpha), Color.Transparent),
            center = center,
            radius = radius,
        ),
        radius = radius,
        center = center,
    )
}

private fun DrawScope.drawStar(star: Star, palette: AstroPalette) {
    val radius = (0.5f + star.size * 1.4f).dp.toPx()
    val alpha = (0.18f + star.brightness * 0.55f) * if (palette.isDark) 1f else 0.7f
    drawCircle(
        color = palette.star.copy(alpha = alpha),
        radius = radius,
        center = Offset(star.x * size.width, star.y * size.height),
    )
}
