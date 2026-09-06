package com.astro.app.feature.home.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import com.astro.app.core.designsystem.AstroMotion

/** Angles courants du solide, en degrés. */
@Immutable
data class SolidRotation(val yaw: Float, val pitch: Float, val roll: Float)

private const val NANOS_PER_SECOND = 1_000_000_000f

/** Vitesse en degrés par seconde correspondant à une période complète. */
private fun degreesPerSecond(periodMillis: Int): Float = 360f / (periodMillis / 1_000f)

/**
 * Pilote la rotation du solide, image par image.
 *
 * Une animation à durée fixe ne convient pas ici : la vitesse doit pouvoir changer en
 * cours de route. On intègre donc l'angle à chaque image à partir du temps écoulé, ce
 * qui rend la rotation continue même quand la vitesse varie — sans le moindre à-coup
 * ni saut d'angle au moment du changement.
 *
 * Quand [boosted] passe à vrai, deux choses se produisent ensemble :
 *  - les deux axes habituels accélèrent d'un facteur `SOLID_BOOST_FACTOR` ;
 *  - l'axe de roulis, à l'arrêt au repos, entre en jeu — c'est le changement d'axe.
 *
 * Les deux transitions sont amorties par une rampe, jamais instantanées.
 */
@Composable
fun rememberSolidRotation(boosted: Boolean): SolidRotation {
    val ramp = tween<Float>(AstroMotion.SOLID_BOOST_RAMP_MILLIS)
    val speed: State<Float> = animateFloatAsState(
        targetValue = if (boosted) AstroMotion.SOLID_BOOST_FACTOR else 1f,
        animationSpec = ramp,
        label = "solidSpeed",
    )
    val roll: State<Float> = animateFloatAsState(
        targetValue = if (boosted) 1f else 0f,
        animationSpec = ramp,
        label = "solidRoll",
    )

    val yawAngle = remember { mutableFloatStateOf(0f) }
    val pitchAngle = remember { mutableFloatStateOf(0f) }
    val rollAngle = remember { mutableFloatStateOf(0f) }

    LaunchedEffect(Unit) {
        var previous = 0L
        while (true) {
            withFrameNanos { now ->
                if (previous != 0L) {
                    val elapsed = (now - previous) / NANOS_PER_SECOND
                    val factor = speed.value * elapsed
                    yawAngle.floatValue = wrap(
                        yawAngle.floatValue + degreesPerSecond(AstroMotion.SOLID_ROTATION_MILLIS) * factor,
                    )
                    pitchAngle.floatValue = wrap(
                        pitchAngle.floatValue + degreesPerSecond(AstroMotion.SOLID_TUMBLE_MILLIS) * factor,
                    )
                    rollAngle.floatValue = wrap(
                        rollAngle.floatValue +
                            degreesPerSecond(AstroMotion.SOLID_ROLL_MILLIS) * roll.value * elapsed,
                    )
                }
                previous = now
            }
        }
    }

    return SolidRotation(yawAngle.floatValue, pitchAngle.floatValue, rollAngle.floatValue)
}

/** Ramène l'angle dans [0, 360[ pour éviter toute perte de précision à la longue. */
private fun wrap(degrees: Float): Float = degrees % 360f
