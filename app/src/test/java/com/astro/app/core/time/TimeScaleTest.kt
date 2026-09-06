package com.astro.app.core.time

import org.junit.Assert.assertEquals
import org.junit.Test

class TimeScaleTest {

    @Test
    fun `la sequence attendue est Jour Semaine Mois Annee`() {
        assertEquals(
            listOf(TimeScale.DAY, TimeScale.WEEK, TimeScale.MONTH, TimeScale.YEAR),
            TimeScale.entries,
        )
    }

    @Test
    fun `le niveau initial est Jour`() {
        assertEquals(TimeScale.DAY, TimeScale.INITIAL)
    }

    @Test
    fun `next progresse dans la sequence et bute sur Annee`() {
        assertEquals(TimeScale.WEEK, TimeScale.DAY.next())
        assertEquals(TimeScale.YEAR, TimeScale.MONTH.next())
        assertEquals(TimeScale.YEAR, TimeScale.YEAR.next())
    }

    @Test
    fun `previous recule dans la sequence et bute sur Jour`() {
        assertEquals(TimeScale.MONTH, TimeScale.YEAR.previous())
        assertEquals(TimeScale.DAY, TimeScale.WEEK.previous())
        assertEquals(TimeScale.DAY, TimeScale.DAY.previous())
    }
}
