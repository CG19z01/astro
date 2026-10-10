package com.astro.app.core.astronomy

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlin.math.abs
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Pleines lunes comparées à une source publiée et vérifiable.
 *
 * Référence : U.S. Naval Observatory, « Phases of the Moon »,
 * https://aa.usno.navy.mil/api/moon/phases/year?year=2025 (et 2026), interrogé le
 * 2026-10-09. Instants en temps universel, arrondis à la minute par l'USNO.
 */
class FullMoonCalculatorTest {

    private val utc: ZoneId = ZoneOffset.UTC
    private val paris: ZoneId = ZoneId.of("Europe/Paris")

    private val usno = listOf(
        "2025-01-13T22:27:00Z", "2025-02-12T13:53:00Z", "2025-03-14T06:55:00Z",
        "2025-04-13T00:22:00Z", "2025-05-12T16:56:00Z", "2025-06-11T07:44:00Z",
        "2025-07-10T20:37:00Z", "2025-08-09T07:55:00Z", "2025-09-07T18:09:00Z",
        "2025-10-07T03:47:00Z", "2025-11-05T13:19:00Z", "2025-12-04T23:14:00Z",
        "2026-01-03T10:03:00Z", "2026-02-01T22:09:00Z", "2026-03-03T11:38:00Z",
        "2026-04-02T02:12:00Z", "2026-05-01T17:23:00Z", "2026-05-31T08:45:00Z",
        "2026-06-29T23:56:00Z", "2026-07-29T14:36:00Z", "2026-08-28T04:18:00Z",
        "2026-09-26T16:49:00Z", "2026-10-26T04:12:00Z", "2026-11-24T14:53:00Z",
        "2026-12-24T01:28:00Z",
    ).map(Instant::parse)

    private fun calculated(from: LocalDate, to: LocalDate, zone: ZoneId = utc) =
        FullMoonCalculator.between(from, to, zone)

    @Test
    fun `chaque pleine lune de 2025 et 2026 est a moins de deux minutes de l'USNO`() {
        val found = calculated(LocalDate.of(2025, 1, 1), LocalDate.of(2026, 12, 31)).values.toList()
        assertEquals(usno.size, found.size)
        usno.zip(found).forEach { (reference, instant) ->
            val gapSeconds = abs(instant.epochSecond - reference.epochSecond)
            assertTrue("$instant contre $reference : ${gapSeconds}s", gapSeconds <= 120)
        }
    }

    @Test
    fun `le calcul de position existant confirme l'opposition aux instants trouves`() {
        // Contrôle croisé par un modèle indépendant : à l'instant de pleine lune,
        // l'élongation de la Lune vaut 180° et son disque est presque entièrement éclairé.
        calculated(LocalDate.of(2025, 1, 1), LocalDate.of(2027, 12, 31)).values.forEach { instant ->
            val state = MoonPosition.at(JulianDay.of(instant))
            assertTrue("élongation ${state.elongation} le $instant", abs(state.elongation - 180.0) < 0.5)
            assertTrue("éclairement ${state.illumination}", state.illumination > 0.999)
        }
    }

    @Test
    fun `2026 compte treize pleines lunes dont deux en mai`() {
        val year = calculated(LocalDate.of(2026, 1, 1), LocalDate.of(2026, 12, 31))
        assertEquals(13, year.size)
        assertEquals(2, year.keys.count { it.monthValue == 5 })
    }

    @Test
    fun `une periode a cheval sur deux annees trouve les pleines lunes des deux cotes`() {
        val dates = calculated(LocalDate.of(2025, 12, 1), LocalDate.of(2026, 1, 31)).keys
        assertEquals(setOf(LocalDate.of(2025, 12, 4), LocalDate.of(2026, 1, 3)), dates)
    }

    @Test
    fun `les bornes de la periode sont incluses`() {
        val day = LocalDate.of(2026, 10, 26)
        assertEquals(setOf(day), calculated(day, day).keys)
        assertTrue(calculated(day.plusDays(1), day.plusDays(28)).isEmpty())
    }

    @Test
    fun `une pleine lune proche de minuit change de jour selon le fuseau`() {
        // USNO : 29 juin 2026 à 23:56 UT, soit le 30 juin à 01:56 à Paris.
        val june29 = LocalDate.of(2026, 6, 29)
        val june30 = LocalDate.of(2026, 6, 30)
        assertTrue(june29 in calculated(june29, june30, utc).keys)
        assertTrue(june30 in calculated(june29, june30, paris).keys)
        assertFalse(june29 in calculated(june29, june30, paris).keys)
    }

    @Test
    fun `a l'ouest de Greenwich la pleine lune peut tomber la veille`() {
        // USNO : 2 avril 2026 à 02:12 UT, soit le 1er avril au soir à New York.
        val newYork = ZoneId.of("America/New_York")
        assertEquals(setOf(LocalDate.of(2026, 4, 1)), calculated(LocalDate.of(2026, 3, 25), LocalDate.of(2026, 4, 5), newYork).keys)
    }

    @Test
    fun `les jours voisins d'une Lune presque pleine ne sont pas des pleines lunes`() {
        // La veille et le lendemain, la Lune paraît pleine à plus de 95 % : ce n'est
        // pas pour autant le jour de la pleine lune.
        val fullMoon = LocalDate.of(2026, 10, 26)
        listOf(fullMoon.minusDays(1), fullMoon.plusDays(1)).forEach { neighbour ->
            assertTrue(MoonCalculator.phaseOn(neighbour, paris).illumination > 0.95)
            assertFalse(neighbour in calculated(fullMoon.minusDays(3), fullMoon.plusDays(3), paris).keys)
        }
    }

    @Test
    fun `deux pleines lunes successives sont separees d'environ une lunaison`() {
        val instants = calculated(LocalDate.of(2024, 1, 1), LocalDate.of(2027, 12, 31)).values.toList()
        instants.zipWithNext { a, b -> (b.epochSecond - a.epochSecond) / 86_400.0 }.forEach { days ->
            assertTrue("écart de $days jours", days in 29.2..29.9)
        }
    }
}
