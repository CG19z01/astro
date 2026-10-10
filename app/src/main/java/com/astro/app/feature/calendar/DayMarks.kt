package com.astro.app.feature.calendar

import java.time.Instant
import java.time.LocalDate

/**
 * Ce qu'il faut signaler sur un jour, quelle que soit la vue qui l'affiche.
 *
 * Les quatre vues lisent la même information par la même fonction : c'est ce qui
 * garantit qu'un jour marqué en vue Mois l'est aussi en vue Semaine, Jour ou Année.
 */
data class DayMarks(
    /** Instant exact de la pleine lune si elle tombe ce jour-là, en heure locale. */
    val fullMoon: Instant? = null,
    /** Couleur ARGB de la bulle si le jour a un planning, `null` sinon. */
    val planningColor: Int? = null,
) {
    companion object {
        val NONE = DayMarks()
    }
}

/** Source des marques d'un jour, telle que les vues la consomment. */
fun interface DayMarksSource {
    fun on(date: LocalDate): DayMarks
}
