package com.astro.app.core.astronomy

import java.time.LocalDate
import java.time.ZoneId
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La Lune est vérifiée par des propriétés physiques, faute de pouvoir comparer à des
 * éphémérides publiées depuis ce projet. Ces propriétés sont fortes : une erreur de
 * modèle les casse immédiatement.
 */
class MoonCalculatorTest {

    private val zone: ZoneId = ZoneId.of("Europe/Paris")
    private val latitude = 48.86
    private val longitude = 2.35

    private fun eventsOn(date: LocalDate) = MoonCalculator.eventsFor(date, zone, latitude, longitude)

    @Test
    fun `la fraction eclairee reste dans son intervalle`() {
        (0..60).forEach { day ->
            val illumination = eventsOn(LocalDate.of(2026, 1, 1).plusDays(day.toLong())).illumination
            assertTrue("jour $day : $illumination", illumination in 0.0..1.0)
        }
    }

    @Test
    fun `le cycle des phases dure environ 29 jours et demi`() {
        // On cherche deux minimums d'éclairement successifs, donc deux nouvelles lunes.
        val start = LocalDate.of(2026, 1, 1)
        val illuminations = (0..80).map { eventsOn(start.plusDays(it.toLong())).illumination }
        val newMoons = illuminations.indices.filter { index ->
            index in 1 until illuminations.size - 1 &&
                illuminations[index] < illuminations[index - 1] &&
                illuminations[index] < illuminations[index + 1]
        }
        assertTrue("nouvelles lunes trouvées : $newMoons", newMoons.size >= 2)
        val cycle = newMoons[1] - newMoons[0]
        assertTrue("cycle de $cycle jours", abs(cycle - 29.53) < 1.5)
    }

    @Test
    fun `la pleine lune se leve peu apres le coucher du soleil`() {
        // Propriété d'opposition : à pleine lune, Soleil et Lune sont de part et d'autre
        // de la Terre. Une erreur de modèle sur l'élongation ou l'ascension droite la casse.
        val start = LocalDate.of(2026, 2, 1)
        val fullMoon = (0..35).map { start.plusDays(it.toLong()) }
            .maxBy { eventsOn(it).illumination }

        val moonrise = eventsOn(fullMoon).moonrise
        val sunset = SunCalculator.eventsFor(fullMoon, latitude, longitude).sunset
        assertTrue("pas de lever ce jour-là", moonrise != null)
        val gapMinutes = abs(moonrise!!.epochSecond - sunset!!.epochSecond) / 60
        assertTrue("écart de $gapMinutes min", gapMinutes < 90)
    }

    @Test
    fun `le lever de la Lune recule d'environ cinquante minutes par jour`() {
        val start = LocalDate.of(2026, 4, 5)
        val rises = (0..6).mapNotNull { eventsOn(start.plusDays(it.toLong())).moonrise?.epochSecond }
        assertTrue("levers trouvés : ${rises.size}", rises.size >= 5)

        // Deux levers de jours civils consécutifs sont séparés d'environ 24 h 50 :
        // c'est l'écart AU-DELÀ des 1440 minutes d'une journée qui vaut ~50 minutes.
        // Les écarts très supérieurs correspondent aux jours sans lever, écartés ici.
        val deltas = rises.zipWithNext { a, b -> (b - a) / 60 - 1_440 }.filter { it in 0..200 }
        val average = deltas.average()
        assertTrue("recul moyen de $average min", abs(average - 50) < 20)
    }

    @Test
    fun `un lever precede toujours le jour suivant`() {
        val date = LocalDate.of(2026, 7, 14)
        val events = eventsOn(date)
        val dayStart = date.atStartOfDay(zone).toInstant()
        val dayEnd = dayStart.plusSeconds(86_400)
        events.moonrise?.let { assertTrue(it >= dayStart && it <= dayEnd) }
        events.moonset?.let { assertTrue(it >= dayStart && it <= dayEnd) }
    }
}
