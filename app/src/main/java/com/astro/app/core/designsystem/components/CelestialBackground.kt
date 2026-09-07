package com.astro.app.core.designsystem.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.scale
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.rememberDustBrush
import com.astro.app.core.designsystem.texture.rememberGrainBrush

/** Intensités des matières. Volontairement très basses : elles se devinent, elles ne se voient pas. */
private const val GRAIN_INTENSITY = 0.030f
private const val DUST_INTENSITY = 0.055f

/** Agrandissement de la tuile de poussière : c'est lui qui la rend nuageuse. */
private const val DUST_SCALE = 9f

private const val HALO_ALPHA_LIGHT = 0.34f

/**
 * Fond commun à tous les écrans.
 *
 * **Ambiance nocturne : un noir uni, sans dégradé.** Ni halos, ni nappe de poussière —
 * ce sont des dégradés visibles, et l'espace profond demandé n'en veut aucun. Seul
 * subsiste le grain, à la limite du perceptible, qui évite le banding d'un aplat sombre
 * sur un écran.
 *
 * **Ambiance claire : inchangée.** Elle garde son dégradé d'aube, ses halos et sa
 * poussière : le noir profond n'a de sens que pour la nuit.
 *
 * Le champ d'étoiles est commun aux deux et dérive très lentement.
 */
@Composable
fun CelestialBackground(modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val grain = rememberGrainBrush(palette.star, GRAIN_INTENSITY)

    Box(modifier = modifier.fillMaxSize()) {
        if (palette.isDark) {
            DeepSpace(color = palette.space, grain = grain)
        } else {
            DawnSky(grain = grain)
        }
        StarField(color = palette.star, dimmed = !palette.isDark)
    }
}

/** Aplat sombre uniforme, relevé du seul grain. */
@Composable
private fun DeepSpace(color: Color, grain: ShaderBrush) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(color = color)
        drawRect(brush = grain)
    }
}

/** Ciel d'aube : dégradé, halos qui respirent et poussière cosmique. */
@Composable
private fun DawnSky(grain: ShaderBrush) {
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
    val dust = rememberDustBrush(palette.haloLunar, DUST_INTENSITY, seed = 7)

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawRect(
            brush = Brush.verticalGradient(
                0f to palette.skyTop,
                0.55f to palette.skyMid,
                1f to palette.skyBottom,
            ),
        )
        drawHalo(palette.haloSolar, HALO_ALPHA_LIGHT, Offset(size.width * 0.86f, size.height * 0.14f), size.width * 0.78f * breath)
        drawHalo(palette.haloLunar, HALO_ALPHA_LIGHT * 0.8f, Offset(size.width * 0.08f, size.height * 0.74f), size.width * 0.92f * (2f - breath))
        drawTiled(dust, DUST_SCALE)
        drawRect(brush = grain)
    }
}

/**
 * Applique une tuile répétée sur toute la surface.
 *
 * Un facteur supérieur à 1 agrandit la tuile : c'est ce qui transforme un petit bruit
 * fractal en nappe nuageuse, sans avoir à calculer une texture de la taille de l'écran.
 */
private fun DrawScope.drawTiled(brush: ShaderBrush, magnification: Float) {
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
