package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.astro.app.core.config.DayWheelConfig
import com.astro.app.core.settings.PlanetColorResolver
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import java.time.LocalDate
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

/** Atténuation des planètes autres que celle du jour courant. */
private const val SIDE_EMPHASIS = 0.45f

/**
 * Roue des sept journées : une planète par jour, de la période courante à la sixième
 * suivante, posées sur une ellipse qui épouse l'écran.
 *
 * La planète du jour courant est la plus grande et porte son planning entier ; les six
 * autres ne montrent que le jour et le début du planning tronqué, leur surface visible
 * étant réduite.
 *
 * Aucune date n'est écrite ici : tout dérive du curseur temporel. Chaque planète est
 * cliquable et mène au planning de SA date.
 */
@Composable
fun DayWheel(
    width: Dp,
    height: Dp,
    cursor: TimeCursor,
    locale: Locale,
    plannings: List<String>,
    planetColors: PlanetColors,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier.fillMaxSize()) {
        // Les planètes les plus lointaines sont posées d'abord : celle du jour courant,
        // dessinée en dernier, reste au premier plan là où elles se chevauchent.
        DayWheelConfig.ANGLES_DEGREES.indices.sortedDescending().forEach { offset ->
            val target = cursor.shifted(offset.toLong())
            val text = plannings.getOrElse(offset) { "" }
            val main = offset == 0
            val diameter = width * (if (main) DayWheelConfig.MAIN_RADIUS else DayWheelConfig.PLANET_RADIUS) * 2f
            val position = positionOf(offset, width, height, diameter)

            DayBead(
                size = diameter,
                emphasis = if (main) 1f else SIDE_EMPHASIS,
                seed = target.anchor.toEpochDay().toInt(),
                tint = Color(
                    PlanetColorResolver.colorFor(target.anchor.dayOfWeek, text, planetColors),
                ),
                contentDescription = PeriodLabels.title(target, locale),
                onClick = { onOpenPlanning(target.anchor) },
                modifier = Modifier.offset(x = position.first, y = position.second),
            ) {
                if (main) {
                    MainDayContent(diameter = diameter, cursor = target, locale = locale, planning = text)
                } else {
                    SideDayContent(diameter = diameter, cursor = target, locale = locale, planning = text)
                }
            }
        }
    }
}

/**
 * Coin supérieur gauche d'une planète, déduit de sa position sur l'ellipse.
 *
 * L'axe Y de l'écran descend, alors que l'angle monte : le sinus est donc soustrait.
 */
private fun positionOf(offset: Int, width: Dp, height: Dp, diameter: Dp): Pair<Dp, Dp> {
    val radians = DayWheelConfig.ANGLES_DEGREES[offset] * Math.PI.toFloat() / 180f
    val centerX = width * DayWheelConfig.CENTER_X + width * DayWheelConfig.RADIUS_X * cos(radians)
    val centerY = height * DayWheelConfig.CENTER_Y - height * DayWheelConfig.RADIUS_Y * sin(radians)
    return (centerX - diameter / 2f) to (centerY - diameter / 2f)
}
