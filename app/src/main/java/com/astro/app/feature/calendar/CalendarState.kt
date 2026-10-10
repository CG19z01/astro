package com.astro.app.feature.calendar

import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.time.YearMonth

/**
 * Ce que montre le Calendrier : une période ([cursor]) et un jour de référence ([selected]).
 *
 * Deux notions distinctes, et c'est voulu. [TimeCursor] ramène sa date au DÉBUT de la
 * période : changer de vue à partir de lui seul ferait passer du 18 octobre en vue Mois
 * à la semaine du 1er octobre. [selected] garde le jour choisi, et c'est à partir de
 * lui que se fait tout changement de vue.
 *
 * Invariant : [selected] appartient toujours à la période de [cursor].
 *
 * Objet pur, sans dépendance à Android : testable en test unitaire JVM.
 */
data class CalendarState(
    val cursor: TimeCursor,
    val selected: LocalDate,
) {
    val scale: TimeScale get() = cursor.scale

    /** Premier et dernier jour de la période affichée, bornes incluses. */
    val firstDay: LocalDate get() = cursor.anchor

    val lastDay: LocalDate
        get() = when (scale) {
            TimeScale.DAY -> cursor.anchor
            TimeScale.WEEK -> cursor.anchor.plusDays(6)
            TimeScale.MONTH -> cursor.anchor.plusMonths(1).minusDays(1)
            TimeScale.YEAR -> cursor.anchor.plusYears(1).minusDays(1)
        }

    /**
     * Période voisine, dans la même vue. Le jour de référence suit, à la même place
     * dans la nouvelle période : du 18 octobre on passe au 18 novembre.
     *
     * Quand ce jour n'existe pas — 31 janvier vers février, 29 février vers une année
     * non bissextile — la bibliothèque standard retient le dernier jour valide. Le
     * jour retenu reste donc dans la période visée, ce qui préserve l'invariant.
     */
    fun shifted(direction: SwipeDirection): CalendarState {
        val steps = if (direction == SwipeDirection.FORWARD) 1L else -1L
        val moved = when (scale) {
            TimeScale.DAY -> selected.plusDays(steps)
            TimeScale.WEEK -> selected.plusWeeks(steps)
            TimeScale.MONTH -> selected.plusMonths(steps)
            TimeScale.YEAR -> selected.plusYears(steps)
        }
        return of(scale, moved)
    }

    /** Change de vue en restant sur la période qui contient le jour de référence. */
    fun withScale(newScale: TimeScale): CalendarState = of(newScale, selected)

    /** Choisit un jour de la période affichée. */
    fun select(date: LocalDate): CalendarState = of(scale, date)

    /**
     * Ouvre un mois depuis la vue Année. On garde le quantième du jour de référence,
     * borné à la longueur du mois : du 31 mai, ouvrir février mène au 28 ou au 29.
     */
    fun openMonth(month: YearMonth): CalendarState {
        val day = minOf(selected.dayOfMonth, month.lengthOfMonth())
        return of(TimeScale.MONTH, month.atDay(day))
    }

    companion object {
        fun of(scale: TimeScale, selected: LocalDate) =
            CalendarState(TimeCursor.of(scale, selected), selected)
    }
}
