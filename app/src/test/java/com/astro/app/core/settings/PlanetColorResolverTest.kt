package com.astro.app.core.settings

import com.astro.app.core.config.PlanetColorDefaults
import java.time.DayOfWeek
import org.junit.Assert.assertEquals
import org.junit.Test

class PlanetColorResolverTest {

    private val red = 0xFFFF0000.toInt()
    private val green = 0xFF00FF00.toInt()
    private val blue = 0xFF0000FF.toInt()

    private val twoColours = PlanetColors(
        mode = PlanetColorMode.WEEKDAY_WEEKEND,
        weekday = red,
        weekend = green,
    )

    @Test
    fun `en mode semaine week-end, du lundi au vendredi partagent la meme couleur`() {
        listOf(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY).forEach { day ->
            assertEquals(red, PlanetColorResolver.colorFor(day, "", twoColours))
        }
    }

    @Test
    fun `samedi et dimanche prennent la couleur du week-end`() {
        listOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).forEach { day ->
            assertEquals(green, PlanetColorResolver.colorFor(day, "", twoColours))
        }
    }

    @Test
    fun `en mode une couleur par jour, chaque jour a la sienne`() {
        val colors = PlanetColors(
            mode = PlanetColorMode.PER_DAY,
            perDay = DayOfWeek.entries.associateWith { it.ordinal },
        )
        DayOfWeek.entries.forEach { day ->
            assertEquals(day.ordinal, PlanetColorResolver.colorFor(day, "", colors))
        }
    }

    @Test
    fun `un jour sans couleur definie retombe sur la valeur par defaut`() {
        val colors = PlanetColors(mode = PlanetColorMode.PER_DAY, perDay = emptyMap())
        assertEquals(
            PlanetColorDefaults.PER_DAY.getValue(DayOfWeek.TUESDAY),
            PlanetColorResolver.colorFor(DayOfWeek.TUESDAY, "", colors),
        )
    }

    @Test
    fun `un mot present dans le texte impose sa couleur, quel que soit le mode`() {
        val colors = twoColours.copy(keywordRules = listOf(KeywordColorRule("réunion", blue)))
        assertEquals(blue, PlanetColorResolver.colorFor(DayOfWeek.MONDAY, "Grande réunion à 10h", colors))
        assertEquals(blue, PlanetColorResolver.colorFor(DayOfWeek.SUNDAY, "réunion", colors))
    }

    @Test
    fun `la casse est ignoree`() {
        val colors = twoColours.copy(keywordRules = listOf(KeywordColorRule("Sport", blue)))
        assertEquals(blue, PlanetColorResolver.colorFor(DayOfWeek.MONDAY, "beaucoup de SPORT", colors))
    }

    @Test
    fun `la premiere regle qui correspond l'emporte`() {
        val colors = twoColours.copy(
            keywordRules = listOf(KeywordColorRule("sport", blue), KeywordColorRule("repos", green)),
        )
        assertEquals(blue, PlanetColorResolver.colorFor(DayOfWeek.MONDAY, "sport puis repos", colors))
    }

    @Test
    fun `un mot absent laisse le mode decider`() {
        val colors = twoColours.copy(keywordRules = listOf(KeywordColorRule("vacances", blue)))
        assertEquals(red, PlanetColorResolver.colorFor(DayOfWeek.MONDAY, "réunion", colors))
    }

    @Test
    fun `une regle au mot vide est ignoree`() {
        val colors = twoColours.copy(keywordRules = listOf(KeywordColorRule("   ", blue)))
        assertEquals(red, PlanetColorResolver.colorFor(DayOfWeek.MONDAY, "quoi que ce soit", colors))
    }
}
