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

/**
 * Accélération de la dérive, demandée le 2026-09-07.
 *
 * Appliquée en facteur plutôt qu'en modifiant les durées de traversée : l'écart au
 * réglage d'origine reste ainsi lisible, et un nouvel ajustement se fait ici.
 */
private const val SPEED_FACTOR = 1.05f

/** Au-delà de ce seuil de luminosité, une étoile reçoit un léger halo. */
private const val HALO_THRESHOLD = 0.72f

private const val NANOS_PER_SECOND = 1_000_000_000f

/**
 * Une étoile : sa position de départ, sa vitesse, sa taille et son éclat.
 *
 * La dérive est purement horizontale, de la gauche vers la droite : une seule vitesse
 * suffit donc à la décrire.
 */
private class Star(
    val x: Float,
    val y: Float,
    val speed: Float,
    val size: Float,
    val brightness: Float,
)

/**
 * Champ d'étoiles en dérive lente.
 *
 * Toutes dérivent **de la gauche vers la droite**, à des vitesses différentes : un sens
 * commun donne au ciel une cohérence de mouvement, les vitesses inégales évitent l'effet
 * de bloc qui se déplace d'un seul tenant.
 *
 * Les positions sont intégrées image par image à partir du temps écoulé, et non
 * animées entre deux bornes : une animation bouclée provoquerait un saut au moment
 * de sa reprise. Une étoile sortie par la droite réapparaît par la gauche, ce qui rend
 * le mouvement continu sans jamais vider le ciel.
 *
 * Quinze cercles par image : le coût est négligeable.
 */
@Composable
fun StarField(color: Color, dimmed: Boolean, modifier: Modifier = Modifier) {
    val stars = remember {
        val random = Random(STAR_SEED)
        List(STAR_COUNT) {
            val crossing = MIN_CROSSING_SECONDS +
                random.nextFloat() * (MAX_CROSSING_SECONDS - MIN_CROSSING_SECONDS)
            Star(
                x = random.nextFloat(),
                y = random.nextFloat(),
                speed = SPEED_FACTOR / crossing,
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
        x = wrap(star.x + star.speed * seconds) * size.width,
        y = star.y * size.height,
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

/** Ramène l'abscisse dans [0, 1[ : l'étoile sortie par la droite revient par la gauche. */
private fun wrap(value: Float): Float = ((value % 1f) + 1f) % 1f
