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
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.rememberDustBrush
import com.astro.app.core.designsystem.texture.rememberGrainBrush
import kotlin.random.Random

/** Peu d'étoiles, volontairement : le vide fait partie de la composition. */
private const val STAR_COUNT = 22
private const val STAR_SEED = 20260906L

/** Intensités des matières. Volontairement très basses : elles se devinent, elles ne se voient pas. */
private const val GRAIN_INTENSITY = 0.030f
private const val DUST_INTENSITY = 0.055f

/** Agrandissement de la tuile de poussière : c'est lui qui la rend nuageuse. */
private const val DUST_SCALE = 9f

/**
 * Fond commun à tous les écrans, en quatre couches superposées :
 *
 * 1. **couleur de base** — un dégradé profond, jamais un noir pur ;
 * 2. **grain** — bruit fin, à la limite du perceptible, qui enlève l'aspect « aplat » ;
 * 3. **poussière** — bruit fractal très étalé, qui crée des variations lumineuses larges ;
 * 4. **éléments spatiaux** — halos et quelques étoiles.
 *
 * Les deux couches de matière viennent de tuiles calculées puis répétées : elles
 * coûtent une image minuscule, s'adaptent à la palette de l'ambiance, et évitent
 * d'embarquer des fichiers de texture.
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
    val grain = rememberGrainBrush(palette.star, GRAIN_INTENSITY)
    val dust = rememberDustBrush(palette.haloLunar, DUST_INTENSITY, seed = 7)
    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) { Star(random.nextFloat(), random.nextFloat(), random.nextFloat(), random.nextFloat()) }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to palette.skyTop,
                0.55f to palette.skyMid,
                1f to palette.skyBottom,
            ),
        )
        drawHalo(palette.haloSolar, haloAlpha(palette.isDark), Offset(size.width * 0.86f, size.height * 0.14f), size.width * 0.78f * breath)
        drawHalo(palette.haloLunar, haloAlpha(palette.isDark) * 0.8f, Offset(size.width * 0.08f, size.height * 0.74f), size.width * 0.92f * (2f - breath))
        drawTiled(dust, DUST_SCALE)
        drawTiled(grain, 1f)
        stars.forEach { drawStar(it, palette.star, palette.isDark) }
    }
}

private class Star(val x: Float, val y: Float, val size: Float, val brightness: Float)

private fun haloAlpha(isDark: Boolean) = if (isDark) 0.20f else 0.34f

/**
 * Applique une tuile répétée sur toute la surface.
 *
 * Un facteur supérieur à 1 agrandit la tuile : c'est ce qui transforme un petit bruit
 * fractal en nappe nuageuse, sans avoir à calculer une texture de la taille de l'écran.
 */
private fun DrawScope.drawTiled(brush: ShaderBrush, magnification: Float) {
    if (magnification == 1f) {
        drawRect(brush = brush)
        return
    }
    scale(magnification, magnification, pivot = Offset.Zero) {
        drawRect(brush = brush, size = size / magnification)
    }
}

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

/**
 * Étoiles volontairement irrégulières : taille, opacité et halo varient. Une
 * distribution trop régulière se lit immédiatement comme un motif, pas comme un ciel.
 */
private fun DrawScope.drawStar(star: Star, color: Color, isDark: Boolean) {
    val center = Offset(star.x * size.width, star.y * size.height)
    val radius = (0.4f + star.size * star.size * 1.9f).dp.toPx()
    val alpha = (0.14f + star.brightness * 0.62f) * if (isDark) 1f else 0.6f

    if (star.brightness > 0.72f) {
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(color.copy(alpha = alpha * 0.5f), Color.Transparent),
                center = center,
                radius = radius * 5f,
            ),
            radius = radius * 5f,
            center = center,
        )
    }
    drawCircle(color = color.copy(alpha = alpha), radius = radius, center = center)
}
