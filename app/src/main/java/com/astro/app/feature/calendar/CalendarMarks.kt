package com.astro.app.feature.calendar

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.astro.app.core.astronomy.FullMoonCalculator
import com.astro.app.core.settings.PlanetColorResolver
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.settings.UserPreferences
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/**
 * Marques des jours d'une période : pleines lunes et bulles de planning.
 *
 * **Une seule règle pour les quatre vues.** Chaque vue interroge [on] pour chacun de
 * ses jours ; aucune ne calcule ses marques à sa façon.
 *
 * **Bulle.** Un jour en porte une si son planning enregistré contient autre chose que
 * des espaces. Sa couleur est celle de sa planète-journée, résolue par
 * [PlanetColorResolver] — mode semaine/week-end ou par jour, et règles de mot comprises.
 * Le texte est lu, jamais modifié.
 *
 * **Pleine lune.** Le jour civil, dans le fuseau des Paramètres, où tombe l'instant de
 * pleine lune ([FullMoonCalculator]).
 */
class CalendarMarks(
    private val fullMoons: Map<LocalDate, Instant>,
    private val entries: Map<LocalDate, String>,
    private val colors: PlanetColors,
) : DayMarksSource {

    override fun on(date: LocalDate): DayMarks = DayMarks(
        fullMoon = fullMoons[date],
        planningColor = entries[date]
            ?.takeIf { it.isNotBlank() }
            ?.let { text -> PlanetColorResolver.colorFor(date.dayOfWeek, text, colors) },
    )

    companion object {
        /**
         * Marques de la période [from]..[to]. Les réglages des Paramètres s'appliquent
         * ici : marqueur coupé, aucune marque de ce type, dans aucune vue.
         */
        fun forPeriod(
            from: LocalDate,
            to: LocalDate,
            entries: Map<LocalDate, String>,
            preferences: UserPreferences,
        ): CalendarMarks = CalendarMarks(
            fullMoons = if (preferences.showFullMoons) fullMoons(from, to, preferences.zoneId) else emptyMap(),
            entries = if (preferences.showPlanningMarkers) entries else emptyMap(),
            colors = preferences.planetColors,
        )

        private fun fullMoons(from: LocalDate, to: LocalDate, zone: ZoneId) =
            FullMoonCalculator.between(from, to, zone)
    }
}

/**
 * Marques de la période affichée, recalculées seulement quand la période, les notes ou
 * les réglages changent — pas à chaque image.
 */
@Composable
fun rememberDayMarks(
    state: CalendarState,
    entries: Map<LocalDate, String>,
    preferences: UserPreferences,
): DayMarksSource = remember(state.firstDay, state.lastDay, entries, preferences) {
    CalendarMarks.forPeriod(state.firstDay, state.lastDay, entries, preferences)
}
