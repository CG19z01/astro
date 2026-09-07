package com.astro.app.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

/** Nombre d'étoiles à l'écran. Volontairement bas : le vide fait partie de la composition. */
private const val STAR_COUNT = 15

/** Graine fixe : le ciel doit être le même d'un lancement à l'autre. */
private const val STAR_SEED = 20260907L

/**
 * Temps de traversée complète de l'écran, en secondes.
 *
 * Entre sept et vingt minutes : le mouvement existe mais ne s'attrape pas du regard.
 * Descendre sous la minute donnerait une pluie d'étoiles, exactement ce qu'il faut éviter.
 */
private const val MIN_CROSSING_SECONDS = 420f
private const val MAX_CROSSING_SECONDS = 1_200f

/** Au-delà de ce seuil de luminosité, une étoile reçoit un léger halo. */
private const val HALO_THRESHOLD = 0.72f

private const val NANOS_PER_SECOND = 1_000_000_000f

/** Une étoile : sa position de départ, sa dérive, sa taille et son éclat. */
private class Star(
    val x: Float,
    val y: Float,
    val driftX: Float,
    val driftY: Float,
    val size: Float,
    val brightness: Float,
)

/**
 * Champ d'étoiles en dérive lente.
 *
 * Les positions sont intégrées image par image à partir du temps écoulé, et non
 * animées entre deux bornes : une animation bouclée provoquerait un saut au moment
 * de sa reprise. Chaque étoile qui sort d'un bord réapparaît par le bord opposé, ce
 * qui rend le mouvement continu sans jamais vider le ciel.
 *
 * Quinze cercles par image : le coût est négligeable.
 */
@Composable
fun StarField(color: Color, dimmed: Boolean, modifier: Modifier = Modifier) {
    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) {
            val direction = random.nextFloat() * 2f * Math.PI.toFloat()
            val crossing = MIN_CROSSING_SECONDS +
                random.nextFloat() * (MAX_CROSSING_SECONDS - MIN_CROSSING_SECONDS)
            val speed = 1f / crossing
            Star(
                x = random.nextFloat(),
                y = random.nextFloat(),
                driftX = cos(direction) * speed,
                driftY = sin(direction) * speed,
                size = random.nextFloat(),
                brightness = random.nextFloat(),
            )
        }
    }
    val elapsed = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var previous = 0L
        while (true) {
            withFrameNanos { now ->
                if (previous != 0L) elapsed.floatValue += (now - previous) / NANOS_PER_SECOND
                previous = now
            }
        }
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val seconds = elapsed.floatValue
        stars.forEach { star -> drawStar(star, seconds, color, dimmed) }
    }
}

private fun DrawScope.drawStar(star: Star, seconds: Float, color: Color, dimmed: Boolean) {
    val center = Offset(
        x = wrap(star.x + star.driftX * seconds) * size.width,
        y = wrap(star.y + star.driftY * seconds) * size.height,
    )
    val radius = (0.4f + star.size * star.size * 1.9f).dp.toPx()
    val alpha = (0.14f + star.brightness * 0.62f) * if (dimmed) 0.6f else 1f

    if (star.brightness > HALO_THRESHOLD) {
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

/** Ramène une coordonnée dans [0, 1[ : l'étoile sortie d'un bord revient par l'autre. */
private fun wrap(value: Float): Float = ((value % 1f) + 1f) % 1f
