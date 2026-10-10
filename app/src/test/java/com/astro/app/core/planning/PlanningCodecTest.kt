package com.astro.app.core.planning

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Format d'enregistrement et lecture des anciennes notes : rien ne doit se perdre. */
class PlanningCodecTest {

    private fun entry(text: String, time: LocalTime? = null, id: Long = 0) = PlanningEntry(id, text, time)

    @Test
    fun `un aller-retour conserve textes, heures et ordre de saisie`() {
        val entries = listOf(entry("Sport", LocalTime.of(18, 30)), entry("Courses"), entry("Café", LocalTime.of(8, 0)))
        val decoded = PlanningCodec.decode(PlanningCodec.encode(entries))
        assertEquals(entries.map { it.text to it.time }, decoded.map { it.text to it.time })
    }

    @Test
    fun `les heures limites sont relues exactement`() {
        listOf(LocalTime.of(0, 0), LocalTime.of(23, 55)).forEach { time ->
            assertEquals(time, PlanningCodec.decode(PlanningCodec.encode(listOf(entry("x", time)))).single().time)
        }
    }

    @Test
    fun `une entree sans heure reste sans heure`() {
        assertNull(PlanningCodec.decode(PlanningCodec.encode(listOf(entry("Courses")))).single().time)
    }

    @Test
    fun `une ligne sans texte n'est pas enregistree, meme avec une heure`() {
        val encoded = PlanningCodec.encode(listOf(entry("A"), entry("  ", LocalTime.of(9, 0)), entry("")))
        assertEquals(listOf("A"), PlanningCodec.decode(encoded).map { it.text })
        assertEquals("", PlanningCodec.encode(listOf(entry("", LocalTime.of(9, 0)))))
    }

    @Test
    fun `aucun caractere du texte ne casse la lecture`() {
        val text = "--:-- 12:00 | ; = , \u001E # é"
        assertEquals(text, PlanningCodec.decode(PlanningCodec.encode(listOf(entry(text)))).single().text)
    }

    @Test
    fun `un retour a la ligne dans le texte ne cree pas d'entree`() {
        assertEquals(listOf("a b"), PlanningCodec.decode(PlanningCodec.encode(listOf(entry("a\nb")))).map { it.text })
    }

    @Test
    fun `une ancienne note d'une ligne devient une entree sans heure`() {
        val decoded = PlanningCodec.decode("Finir l'application")
        assertEquals(listOf("Finir l'application"), decoded.map { it.text })
        assertNull(decoded.single().time)
    }

    @Test
    fun `une ancienne note de plusieurs lignes devient une entree par ligne`() {
        val legacy = "Sport le matin\n\nDéjeuner avec Paul\nCinéma"
        val decoded = PlanningCodec.decode(legacy)
        assertEquals(listOf("Sport le matin", "Déjeuner avec Paul", "Cinéma"), decoded.map { it.text })
        assertEquals(decoded.size, decoded.map { it.id }.toSet().size)
    }

    @Test
    fun `une ancienne note ressemblant a une heure reste du texte`() {
        assertEquals("09:30 Réunion", PlanningCodec.decode("09:30 Réunion").single().text)
    }

    @Test
    fun `l'affichage met l'heure devant le texte`() {
        val text = PlanningCodec.display(listOf(entry("Dîner", LocalTime.of(9, 30)), entry("Courses")))
        assertEquals("09:30 Dîner\nCourses", text)
    }

    @Test
    fun `une ancienne note s'affiche telle qu'elle etait ecrite`() {
        val legacy = "Réunion équipe\nCourses"
        assertEquals(legacy, PlanningCodec.display(PlanningCodec.decode(legacy)))
        assertTrue(PlanningCodec.display(PlanningCodec.decode("")).isEmpty())
    }

    @Test
    fun `l'ordre manuel est enregistre et relu`() {
        val day = DayPlanning(listOf(entry("b"), entry("a", LocalTime.of(8, 0))), manualOrder = true)
        val decoded = PlanningCodec.decodeDay(PlanningCodec.encodeDay(day))
        assertTrue(decoded.manualOrder)
        assertEquals(listOf("b", "a"), decoded.entries.map { it.text })
    }

    @Test
    fun `une note sans ordre manuel et une ancienne note sont en ordre automatique`() {
        assertEquals(false, PlanningCodec.decodeDay(PlanningCodec.encode(listOf(entry("a")))).manualOrder)
        assertEquals(false, PlanningCodec.decodeDay("ancienne note").manualOrder)
    }
}
