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

    @Test
    fun `la date numerique ecrit le jour et le mois sur deux chiffres`() {
        val cursor = TimeCursor.of(TimeScale.DAY, java.time.LocalDate.of(2026, 1, 9))
        assertEquals("09/01/2026", PeriodLabels.numericDate(cursor, java.util.Locale.FRENCH))
        val october = TimeCursor.of(TimeScale.DAY, java.time.LocalDate.of(2026, 10, 31))
        assertEquals("31/10/2026", PeriodLabels.numericDate(october, java.util.Locale.FRENCH))
    }
}
