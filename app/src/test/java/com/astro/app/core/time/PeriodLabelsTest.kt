package com.astro.app.core.time

import java.time.LocalDate
import java.util.Locale
import org.junit.Assert.assertEquals
import org.junit.Test

/** Format des libellés de période. */
class PeriodLabelsTest {

    private val french = Locale.FRENCH

    @Test
    fun `la date d'une journee s'ecrit jour sur deux chiffres, mois, annee`() {
        val cursor = TimeCursor(TimeScale.DAY, LocalDate.of(2026, 9, 8))
        assertEquals("08 septembre 2026", PeriodLabels.subtitle(cursor, french))
    }

    @Test
    fun `le jour reste sur deux chiffres apres le neuf du mois`() {
        val cursor = TimeCursor(TimeScale.DAY, LocalDate.of(2026, 9, 11))
        assertEquals("11 septembre 2026", PeriodLabels.subtitle(cursor, french))
    }
}
