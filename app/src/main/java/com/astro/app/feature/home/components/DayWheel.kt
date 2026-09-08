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

/** Atténuation des planètes autres que celle du jour courant. */
private const val SIDE_EMPHASIS = 0.45f

/**
 * Les quatre planètes-journées de l'accueil : la journée courante, la suivante, J+3 et
 * J+4, placées d'après la maquette `planetesupp`.
 *
 * La planète du jour courant est la plus grande et porte son planning entier ; les
 * trois autres ne montrent que le jour et le début du planning tronqué, leur surface
 * visible étant réduite.
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
        // Les journées les plus lointaines sont posées d'abord : celle du jour courant,
        // dessinée en dernier, reste au premier plan là où elles se chevauchent.
        DayWheelConfig.PLANETS.sortedByDescending { it.offset }.forEach { spec ->
            val target = cursor.shifted(spec.offset.toLong())
            val text = plannings.getOrElse(spec.offset) { "" }
            val main = spec.offset == 0
            val diameter = width * spec.radius * 2f

            DayBead(
                size = diameter,
                emphasis = if (main) 1f else SIDE_EMPHASIS,
                seed = target.anchor.toEpochDay().toInt(),
                tint = Color(
                    PlanetColorResolver.colorFor(target.anchor.dayOfWeek, text, planetColors),
                ),
                contentDescription = PeriodLabels.title(target, locale),
                onClick = { onOpenPlanning(target.anchor) },
                modifier = Modifier.offset(
                    x = width * spec.centerX - diameter / 2f,
                    y = height * spec.centerY - diameter / 2f,
                ),
            ) {
                if (main) {
                    MainDayContent(spec = spec, diameter = diameter, cursor = target, locale = locale, planning = text)
                } else {
                    SideDayContent(spec = spec, diameter = diameter, cursor = target, locale = locale, planning = text)
                }
            }
        }
    }
}

