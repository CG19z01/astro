package com.astro.app.feature.planning

import com.astro.app.core.planning.PlanningEntry
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

/** Création et suppression des lignes du planning. */
class PlanningLinesTest {

    private val empty = PlanningLines.fromStored(emptyList())
    private val nine = LocalTime.of(9, 0)

    private fun texts(lines: List<PlanningEntry>) = lines.map { it.text }

    @Test
    fun `un planning vide offre une seule ligne vide`() {
        assertEquals(listOf(""), texts(empty))
    }

    @Test
    fun `ecrire dans la derniere ligne en ajoute une vide`() {
        val lines = PlanningLines.withText(empty, empty.single().id, "Sport")
        assertEquals(listOf("Sport", ""), texts(lines))
    }

    @Test
    fun `des espaces ne creent pas de ligne`() {
        assertEquals(1, PlanningLines.withText(empty, empty.single().id, "   ").size)
    }

    @Test
    fun `choisir une heure sur la ligne vide ne cree pas de ligne`() {
        val lines = PlanningLines.withTime(empty, empty.single().id, nine)
        assertEquals(1, lines.size)
        assertEquals(nine, lines.single().time)
    }

    @Test
    fun `effacer le texte de l'avant-derniere ligne retire la ligne vide en trop`() {
        var lines = PlanningLines.withText(empty, empty.single().id, "Sport")
        val first = lines.first().id
        lines = PlanningLines.withText(lines, first, "")
        assertEquals(listOf(""), texts(lines))
        assertEquals(first, lines.single().id)
    }

    @Test
    fun `il n'y a jamais qu'une ligne vide a la fin`() {
        var lines = empty
        listOf("a", "b", "c").forEach { lines = PlanningLines.withText(lines, lines.last().id, it) }
        assertEquals(listOf("a", "b", "c", ""), texts(lines))
        assertEquals(1, lines.count { it.text.isBlank() })
    }

    @Test
    fun `une ligne vide intermediaire disparait quand on la quitte, pas avant`() {
        var lines = PlanningLines.fromStored(listOf(PlanningEntry(0, "a"), PlanningEntry(1, "b")))
        lines = PlanningLines.withText(lines, 0, "")
        assertEquals(listOf("", "b", ""), texts(lines))
        lines = PlanningLines.dropIfEmpty(lines, 0)
        assertEquals(listOf("b", ""), texts(lines))
    }

    @Test
    fun `une ligne intermediaire qui garde une heure n'est pas retiree`() {
        var lines = PlanningLines.fromStored(listOf(PlanningEntry(0, "a", nine), PlanningEntry(1, "b")))
        lines = PlanningLines.withText(lines, 0, "")
        assertEquals(3, PlanningLines.dropIfEmpty(lines, 0).size)
    }

    @Test
    fun `la ligne finale n'est jamais retiree`() {
        assertEquals(empty, PlanningLines.dropIfEmpty(empty, empty.single().id))
    }

    @Test
    fun `supprimer une ligne ne deplace pas l'heure des autres`() {
        var lines = PlanningLines.fromStored(
            listOf(PlanningEntry(0, "a", LocalTime.of(8, 0)), PlanningEntry(1, "b"), PlanningEntry(2, "c", LocalTime.of(20, 0))),
        )
        lines = PlanningLines.dropIfEmpty(PlanningLines.withText(lines, 1, ""), 1)
        assertEquals(listOf("a" to LocalTime.of(8, 0), "c" to LocalTime.of(20, 0), "" to null), lines.map { it.text to it.time })
    }

    @Test
    fun `retirer une heure ne touche pas au texte`() {
        val lines = PlanningLines.withTime(PlanningLines.fromStored(listOf(PlanningEntry(0, "a", nine))), 0, null)
        assertEquals("a", lines.first().text)
        assertNull(lines.first().time)
    }

    @Test
    fun `l'ordre de saisie est garde, sans tri par heure`() {
        val stored = listOf(PlanningEntry(0, "soir", LocalTime.of(20, 0)), PlanningEntry(1, "matin", LocalTime.of(8, 0)))
        assertEquals(listOf("soir", "matin"), PlanningLines.toPersist(PlanningLines.fromStored(stored)).map { it.text })
    }

    @Test
    fun `seules les lignes avec du texte sont enregistrees`() {
        val lines = PlanningLines.withTime(PlanningLines.fromStored(listOf(PlanningEntry(0, "a"))), 1, nine)
        assertEquals(listOf("a"), PlanningLines.toPersist(lines).map { it.text })
    }

    @Test
    fun `les identifiants restent uniques`() {
        var lines = empty
        repeat(5) { lines = PlanningLines.withText(lines, lines.last().id, "l$it") }
        assertTrue(lines.map { it.id }.toSet().size == lines.size)
    }

    @Test
    fun `deplacer une ligne garde son texte et son heure`() {
        val lines = PlanningLines.fromStored(listOf(PlanningEntry(0, "a", nine), PlanningEntry(1, "b"), PlanningEntry(2, "c")))
        val moved = PlanningLines.move(lines, 0, 2)
        assertEquals(listOf("b", "c", "a", ""), texts(moved))
        assertEquals(nine, moved[2].time)
    }

    @Test
    fun `une ligne ne peut pas passer apres la ligne vide finale`() {
        val lines = PlanningLines.fromStored(listOf(PlanningEntry(0, "a"), PlanningEntry(1, "b")))
        assertEquals(listOf("b", "a", ""), texts(PlanningLines.move(lines, 0, 99)))
    }

    @Test
    fun `la ligne vide finale ne se deplace pas`() {
        val lines = PlanningLines.fromStored(listOf(PlanningEntry(0, "a")))
        assertEquals(lines, PlanningLines.move(lines, lines.last().id, 0))
    }
}
