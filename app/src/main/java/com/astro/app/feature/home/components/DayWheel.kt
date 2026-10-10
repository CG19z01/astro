package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.astro.app.core.config.DayWheelConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.PlanningEntry
import com.astro.app.core.planning.PlanningOrder
import com.astro.app.core.settings.PlanetColorResolver
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Locale

/** Atténuation des planètes autres que celle du jour courant. */
private const val SIDE_EMPHASIS = 0.45f

/**
 * Les quatre planètes-journées de l'accueil : la journée courante, la suivante, J+3 et
 * J+4, placées d'après la maquette `planetesupp`.
 *
 * Chaque planète porte le jour, sa date et ses messages, qui défilent sous le doigt
 * ([PlanetMessages]) dans l'ordre de [PlanningOrder] : à venir, passés, puis sans heure.
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
    plannings: List<List<PlanningEntry>>,
    /** Instant présent, à la minute : décide quels messages sont déjà passés. */
    now: LocalDateTime,
    planetColors: PlanetColors,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens

    Box(modifier = modifier.fillMaxSize()) {
        // Les journées les plus lointaines sont posées d'abord : celle du jour courant,
        // dessinée en dernier, reste au premier plan là où elles se chevauchent.
        DayWheelConfig.PLANETS.sortedByDescending { it.offset }.forEach { spec ->
            val target = cursor.shifted(spec.offset.toLong())
            val entries = plannings.getOrElse(spec.offset) { emptyList() }
            // La couleur suit le texte enregistré ; l'affichage, l'ordre des heures.
            val text = PlanningCodec.display(entries)
            val shown = PlanningCodec.display(PlanningOrder.forDisplay(entries, target.anchor, now))
            val main = spec.offset == 0
            val diameter = width * spec.radius * 2f
            val left = width * spec.centerX - diameter / 2f + diameter * spec.contentX
            val factor = if (main) DayWheelConfig.MAIN_CONTENT_WIDTH else DayWheelConfig.SIDE_CONTENT_WIDTH
            // Deux bornes : le cercle, qui découpe son contenu, et le bord droit de
            // l'écran, qu'une planète peut dépasser. La plus étroite l'emporte.
            val contentWidth = minOf(diameter * factor, width - left - dimens.spaceMd)

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
                val place = DayContentPlacement(diameter * spec.contentX, diameter * spec.contentY, contentWidth)
                if (main) {
                    MainDayContent(place, target, locale, planning = shown, lines = spec.messageLines)
                } else {
                    SideDayContent(place, target, locale, planning = shown, lines = spec.messageLines)
                }
            }
        }
    }
}

