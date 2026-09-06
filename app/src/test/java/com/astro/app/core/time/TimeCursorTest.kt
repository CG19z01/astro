package com.astro.app.core.time

import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Le curseur est la pièce qui garantit qu'un aller-retour dans le temps ramène au
 * point de départ, quelle que soit la temporalité. C'est ce que vérifient ces tests.
 */
class TimeCursorTest {

    private val sunday = LocalDate.of(2026, 9, 6)

    @Test
    fun `en mode Jour, un pas recule d'une journee`() {
        val cursor = TimeCursor.of(TimeScale.DAY, sunday)
        assertEquals(LocalDate.of(2026, 9, 5), cursor.previous().anchor)
        assertEquals(LocalDate.of(2026, 9, 7), cursor.next().anchor)
    }

    @Test
    fun `en mode Semaine, l'ancre est ramenee au lundi`() {
        val cursor = TimeCursor.of(TimeScale.WEEK, sunday)
        assertEquals(LocalDate.of(2026, 8, 31), cursor.anchor)
        assertEquals(LocalDate.of(2026, 8, 24), cursor.previous().anchor)
    }

    @Test
    fun `en mode Mois, l'ancre est ramenee au premier du mois`() {
        val cursor = TimeCursor.of(TimeScale.MONTH, sunday)
        assertEquals(LocalDate.of(2026, 9, 1), cursor.anchor)
        assertEquals(LocalDate.of(2026, 8, 1), cursor.previous().anchor)
    }

    @Test
    fun `en mode Annee, l'ancre est ramenee au premier janvier`() {
        val cursor = TimeCursor.of(TimeScale.YEAR, sunday)
        assertEquals(LocalDate.of(2026, 1, 1), cursor.anchor)
        assertEquals(LocalDate.of(2025, 1, 1), cursor.previous().anchor)
    }

    @Test
    fun `un aller-retour ramene au point de depart, dans toutes les temporalites`() {
        TimeScale.entries.forEach { scale ->
            val start = TimeCursor.of(scale, sunday)
            assertEquals("échec pour $scale", start, start.next().previous())
            assertEquals("échec pour $scale", start, start.previous().next())
        }
    }

    @Test
    fun `depuis un 31, reculer d'un mois ne derive pas`() {
        // Sans normalisation, 31 mars moins un mois donnerait le 28 février,
        // et l'aller-retour ne reviendrait pas au 31 mars.
        val cursor = TimeCursor.of(TimeScale.MONTH, LocalDate.of(2026, 3, 31))
        assertEquals(LocalDate.of(2026, 2, 1), cursor.previous().anchor)
        assertEquals(LocalDate.of(2026, 3, 1), cursor.previous().next().anchor)
    }

    @Test
    fun `changer de temporalite conserve la periode contenant la date`() {
        val day = TimeCursor.of(TimeScale.DAY, sunday)
        assertEquals(LocalDate.of(2026, 8, 31), day.withScale(TimeScale.WEEK).anchor)
        assertEquals(LocalDate.of(2026, 9, 1), day.withScale(TimeScale.MONTH).anchor)
    }
}
