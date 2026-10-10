package com.astro.app.feature.calendar

import com.astro.app.core.time.SwipeDirection.BACKWARD
import com.astro.app.core.time.SwipeDirection.FORWARD
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/** Les deux gestes du Calendrier : un swipe change de période, une salve change de vue. */
class CalendarSwipeHandlerTest {

    private val october18 = LocalDate.of(2026, 10, 18)
    private val start = CalendarState.of(TimeScale.MONTH, october18)

    @Test
    fun `un swipe simple passe au mois suivant sans changer de vue`() {
        val next = CalendarSwipeHandler().onSwipe(start, FORWARD, 0)
        assertEquals(TimeScale.MONTH, next.scale)
        assertEquals(LocalDate.of(2026, 11, 18), next.selected)
    }

    @Test
    fun `une salve change de vue et revient a la periode de depart`() {
        val handler = CalendarSwipeHandler()
        var state = start
        listOf(0L, 300L, 600L).forEach { state = handler.onSwipe(state, FORWARD, it) }
        // Vers l'avant, on monte d'un niveau : Mois → Année, sur l'année du jour retenu.
        assertEquals(TimeScale.YEAR, state.scale)
        assertEquals(october18, state.selected)
        assertEquals(LocalDate.of(2026, 1, 1), state.firstDay)
    }

    @Test
    fun `une salve arriere revient a la vue precedente`() {
        val handler = CalendarSwipeHandler()
        var state = start
        listOf(0L, 300L, 600L).forEach { state = handler.onSwipe(state, BACKWARD, it) }
        assertEquals(TimeScale.WEEK, state.scale)
        assertEquals(october18, state.selected)
    }

    @Test
    fun `des swipes lents restent de simples changements de periode`() {
        val handler = CalendarSwipeHandler()
        var state = start
        listOf(0L, 2_000L, 4_000L).forEach { state = handler.onSwipe(state, FORWARD, it) }
        assertEquals(TimeScale.MONTH, state.scale)
        assertEquals(LocalDate.of(2027, 1, 18), state.selected)
    }

    @Test
    fun `une salve apres des swipes lents n'annule que ses propres deplacements`() {
        val handler = CalendarSwipeHandler()
        var state = handler.onSwipe(start, FORWARD, 0)
        val beforeBurst = handler.onSwipe(state, FORWARD, 5_000)
        state = beforeBurst
        listOf(10_000L, 10_300L, 10_600L).forEach { state = handler.onSwipe(state, FORWARD, it) }
        assertEquals(TimeScale.YEAR, state.scale)
        assertEquals(LocalDate.of(2026, 12, 18), state.selected)
    }

    @Test
    fun `six swipes rapides valent deux changements de vue`() {
        val handler = CalendarSwipeHandler()
        var state = CalendarState.of(TimeScale.DAY, october18)
        (0L until 6L).forEach { state = handler.onSwipe(state, FORWARD, it * 200) }
        assertEquals(TimeScale.MONTH, state.scale)
        assertEquals(october18, state.selected)
    }

    @Test
    fun `a la vue Annee une salve vers l'avant ne boucle pas`() {
        val handler = CalendarSwipeHandler()
        var state = CalendarState.of(TimeScale.YEAR, october18)
        listOf(0L, 300L, 600L).forEach { state = handler.onSwipe(state, FORWARD, it) }
        assertEquals(TimeScale.YEAR, state.scale)
        assertEquals(2026, state.selected.year)
    }
}
