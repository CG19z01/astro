package com.astro.app.feature.calendar

import com.astro.app.core.settings.KeywordColorRule
import com.astro.app.core.settings.PlanetColorMode
import com.astro.app.core.settings.PlanetColors
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Test

/** Les marques d'un jour, identiques quelle que soit la vue qui l'affiche. */
class CalendarMarksTest {

    private val preferences = UserPreferences(zoneId = ZoneId.of("Europe/Paris"))
    private val october18 = LocalDate.of(2026, 10, 18)

    private fun marksOf(scale: TimeScale, date: LocalDate, entries: Map<LocalDate, String>, prefs: UserPreferences = preferences): DayMarks {
        val state = CalendarState.of(scale, date)
        return CalendarMarks.forPeriod(state.firstDay, state.lastDay, entries, prefs).on(date)
    }

    @Test
    fun `chaque jour de 2026 porte les memes marques dans les quatre vues`() {
        val entries = mapOf(october18 to "Dîner", LocalDate.of(2026, 1, 1) to "Fête")
        var day = LocalDate.of(2026, 1, 1)
        while (day.year == 2026) {
            val expected = marksOf(TimeScale.YEAR, day, entries)
            listOf(TimeScale.MONTH, TimeScale.WEEK, TimeScale.DAY).forEach { scale ->
                assertEquals("$day en $scale", expected, marksOf(scale, day, entries))
            }
            day = day.plusDays(1)
        }
    }

    @Test
    fun `un jour avec du texte porte une bulle, un jour sans note n'en porte pas`() {
        val entries = mapOf(october18 to "Dîner")
        assertNotNull(marksOf(TimeScale.MONTH, october18, entries).planningColor)
        assertNull(marksOf(TimeScale.MONTH, october18.plusDays(1), entries).planningColor)
    }

    @Test
    fun `un texte fait d'espaces ne declenche pas de bulle`() {
        assertNull(marksOf(TimeScale.MONTH, october18, mapOf(october18 to "  \n\t ")).planningColor)
    }

    @Test
    fun `chaque date renseignee est marquee independamment`() {
        val entries = mapOf(october18 to "a", october18.plusDays(2) to "b")
        assertNotNull(marksOf(TimeScale.WEEK, october18, entries).planningColor)
        assertNotNull(marksOf(TimeScale.WEEK, october18.plusDays(2), entries).planningColor)
        assertNull(marksOf(TimeScale.WEEK, october18.plusDays(1), entries).planningColor)
    }

    @Test
    fun `la bulle prend la couleur de la planete, regles de mot comprises`() {
        val colors = PlanetColors(
            mode = PlanetColorMode.WEEKDAY_WEEKEND, weekday = 0x11, weekend = 0x22,
            keywordRules = listOf(KeywordColorRule("anniversaire", 0x33)),
        )
        val prefs = preferences.copy(planetColors = colors)
        val sunday = october18
        val monday = october18.plusDays(1)
        assertEquals(0x22, marksOf(TimeScale.MONTH, sunday, mapOf(sunday to "Courses"), prefs).planningColor)
        assertEquals(0x11, marksOf(TimeScale.MONTH, monday, mapOf(monday to "Courses"), prefs).planningColor)
        assertEquals(0x33, marksOf(TimeScale.MONTH, monday, mapOf(monday to "Anniversaire de Léa"), prefs).planningColor)
    }

    @Test
    fun `la pleine lune du 26 octobre 2026 est marquee et pas ses voisins`() {
        val fullMoon = LocalDate.of(2026, 10, 26)
        assertNotNull(marksOf(TimeScale.MONTH, fullMoon, emptyMap()).fullMoon)
        assertNull(marksOf(TimeScale.MONTH, fullMoon.minusDays(1), emptyMap()).fullMoon)
        assertNull(marksOf(TimeScale.MONTH, fullMoon.plusDays(1), emptyMap()).fullMoon)
    }

    @Test
    fun `couper un marqueur dans les Parametres le retire de toutes les vues`() {
        val fullMoon = LocalDate.of(2026, 10, 26)
        val entries = mapOf(fullMoon to "Dîner")
        val off = preferences.copy(showFullMoons = false, showPlanningMarkers = false)
        TimeScale.entries.forEach { scale ->
            assertEquals(DayMarks.NONE, marksOf(scale, fullMoon, entries, off))
        }
    }
}
