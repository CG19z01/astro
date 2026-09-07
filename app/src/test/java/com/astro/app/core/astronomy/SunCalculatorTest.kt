package com.astro.app.core.astronomy

import java.time.LocalDate
import kotlin.math.abs
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Vérifications par propriétés physiques plutôt que par comparaison à des horaires
 * publiés : ces invariants sont vrais par construction du système solaire, donc
 * vérifiables sans dépendre d'une source externe.
 */
class SunCalculatorTest {

    private val equinox = LocalDate.of(2026, 3, 20)
    private val juneSolstice = LocalDate.of(2026, 6, 21)
    private val decemberSolstice = LocalDate.of(2026, 12, 21)

    @Test
    fun `le lever precede midi solaire, qui precede le coucher`() {
        val events = SunCalculator.eventsFor(equinox, 48.86, 2.35)
        assertTrue(events.sunrise!! < events.solarNoon)
        assertTrue(events.solarNoon < events.sunset!!)
    }

    @Test
    fun `lever et coucher sont symetriques autour de midi solaire`() {
        val events = SunCalculator.eventsFor(LocalDate.of(2026, 5, 12), 48.86, 2.35)
        val before = events.solarNoon.epochSecond - events.sunrise!!.epochSecond
        val after = events.sunset!!.epochSecond - events.solarNoon.epochSecond
        assertTrue("écart de ${abs(before - after)} s", abs(before - after) < 60)
    }

    @Test
    fun `a l'equateur, le jour dure environ douze heures toute l'annee`() {
        listOf(equinox, juneSolstice, decemberSolstice).forEach { date ->
            val length = SunCalculator.eventsFor(date, 0.0, 0.0).dayLengthMinutes!!
            assertTrue("$date : $length min", abs(length - 720) < 15)
        }
    }

    @Test
    fun `a l'equinoxe, le jour dure environ douze heures a toute latitude`() {
        listOf(-40.0, 0.0, 35.0, 60.0).forEach { latitude ->
            val length = SunCalculator.eventsFor(equinox, latitude, 0.0).dayLengthMinutes!!
            assertTrue("latitude $latitude : $length min", abs(length - 720) < 25)
        }
    }

    @Test
    fun `au solstice de juin, le jour est long au nord et court au sud`() {
        val north = SunCalculator.eventsFor(juneSolstice, 60.0, 0.0).dayLengthMinutes!!
        val south = SunCalculator.eventsFor(juneSolstice, -60.0, 0.0).dayLengthMinutes!!
        assertTrue("nord : $north min", north > 1_100)
        assertTrue("sud : $south min", south < 400)
    }

    @Test
    fun `au-dela du cercle polaire, le solstice de juin donne un jour permanent`() {
        val events = SunCalculator.eventsFor(juneSolstice, 78.0, 15.0)
        assertNull(events.sunrise)
        assertNull(events.sunset)
        assertTrue(events.polarDay)
        assertTrue(!events.polarNight)
    }

    @Test
    fun `au-dela du cercle polaire, le solstice de decembre donne une nuit permanente`() {
        val events = SunCalculator.eventsFor(decemberSolstice, 78.0, 15.0)
        assertTrue(events.polarNight)
        assertTrue(!events.polarDay)
        assertNotNull(events.solarNoon)
    }

    @Test
    fun `midi solaire au meridien de Greenwich reste proche de midi UTC`() {
        // L'équation du temps ne dépasse jamais 17 minutes en valeur absolue.
        listOf(1, 4, 7, 10).forEach { month ->
            val events = SunCalculator.eventsFor(LocalDate.of(2026, month, 15), 45.0, 0.0)
            val minutes = (events.solarNoon.epochSecond % 86_400) / 60
            assertTrue("mois $month : $minutes min", abs(minutes - 720) <= 18)
        }
    }
}
