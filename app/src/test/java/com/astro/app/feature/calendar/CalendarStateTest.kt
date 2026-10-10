package com.astro.app.feature.calendar

import com.astro.app.core.time.SwipeDirection
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Le jour de référence doit survivre aux changements de vue : c'est ce qui permet de
 * passer du 18 octobre en vue Mois à la semaine qui le contient.
 */
class CalendarStateTest {

    private val october18 = LocalDate.of(2026, 10, 18)

    @Test
    fun `passer de Mois a Semaine garde la semaine du jour selectionne`() {
        val month = CalendarState.of(TimeScale.MONTH, LocalDate.of(2026, 10, 1)).select(october18)
        val week = month.withScale(TimeScale.WEEK)
        assertEquals(LocalDate.of(2026, 10, 12), week.firstDay)
        assertEquals(october18, week.selected)
        assertTrue(october18 in week.firstDay..week.lastDay)
    }

    @Test
    fun `l'aller-retour entre les quatre vues revient au meme jour`() {
        var state = CalendarState.of(TimeScale.MONTH, october18)
        listOf(TimeScale.YEAR, TimeScale.DAY, TimeScale.WEEK, TimeScale.MONTH).forEach { state = state.withScale(it) }
        assertEquals(october18, state.selected)
        assertEquals(LocalDate.of(2026, 10, 1), state.firstDay)
    }

    @Test
    fun `les bornes de chaque vue sont exactes`() {
        val leapFebruary = CalendarState.of(TimeScale.MONTH, LocalDate.of(2028, 2, 10))
        assertEquals(LocalDate.of(2028, 2, 29), leapFebruary.lastDay)
        val year = CalendarState.of(TimeScale.YEAR, october18)
        assertEquals(LocalDate.of(2026, 1, 1), year.firstDay)
        assertEquals(LocalDate.of(2026, 12, 31), year.lastDay)
        val day = CalendarState.of(TimeScale.DAY, october18)
        assertEquals(day.firstDay, day.lastDay)
    }

    @Test
    fun `une semaine a cheval sur deux annees reste entiere`() {
        val week = CalendarState.of(TimeScale.WEEK, LocalDate.of(2027, 1, 1))
        assertEquals(LocalDate.of(2026, 12, 28), week.firstDay)
        assertEquals(LocalDate.of(2027, 1, 3), week.lastDay)
    }

    @Test
    fun `passer au mois suivant depuis le 31 janvier mene en fevrier`() {
        val next = CalendarState.of(TimeScale.MONTH, LocalDate.of(2026, 1, 31)).shifted(SwipeDirection.FORWARD)
        assertEquals(LocalDate.of(2026, 2, 28), next.selected)
        assertEquals(LocalDate.of(2026, 2, 1), next.firstDay)
    }

    @Test
    fun `le 29 fevrier passe au 28 fevrier de l'annee suivante`() {
        val next = CalendarState.of(TimeScale.YEAR, LocalDate.of(2028, 2, 29)).shifted(SwipeDirection.FORWARD)
        assertEquals(LocalDate.of(2029, 2, 28), next.selected)
    }

    @Test
    fun `le jour suivant du 31 decembre est le 1er janvier`() {
        val next = CalendarState.of(TimeScale.DAY, LocalDate.of(2026, 12, 31)).shifted(SwipeDirection.FORWARD)
        assertEquals(LocalDate.of(2027, 1, 1), next.selected)
    }

    @Test
    fun `le jour selectionne reste toujours dans la periode affichee`() {
        var state = CalendarState.of(TimeScale.MONTH, LocalDate.of(2026, 1, 31))
        repeat(30) {
            state = state.shifted(SwipeDirection.FORWARD)
            assertTrue("${state.selected} hors de ${state.firstDay}..${state.lastDay}", state.selected in state.firstDay..state.lastDay)
        }
    }

    @Test
    fun `ouvrir fevrier depuis le 31 mai retient le dernier jour de fevrier`() {
        val year = CalendarState.of(TimeScale.YEAR, LocalDate.of(2028, 5, 31))
        val february = year.openMonth(YearMonth.of(2028, 2))
        assertEquals(TimeScale.MONTH, february.scale)
        assertEquals(LocalDate.of(2028, 2, 29), february.selected)
    }
}
