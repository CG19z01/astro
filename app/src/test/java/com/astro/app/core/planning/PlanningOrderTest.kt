package com.astro.app.core.planning

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

/** Ordre des messages sur une planète : à venir, passés, puis sans heure. */
class PlanningOrderTest {

    private val day = LocalDate.of(2026, 10, 10)
    private var nextId = 0L
    private fun e(text: String, h: Int? = null, m: Int = 0) =
        PlanningEntry(nextId++, text, h?.let { LocalTime.of(it, m) })
    private fun order(entries: List<PlanningEntry>, at: LocalDateTime) =
        PlanningOrder.forDisplay(entries, day, at).map { it.text }
    private fun at(h: Int, m: Int = 0) = LocalDateTime.of(day, LocalTime.of(h, m))

    @Test
    fun `les messages a heure sont tries du plus tot au plus tard`() {
        assertEquals(listOf("8", "14", "20"), order(listOf(e("20", 20), e("8", 8), e("14", 14)), at(7)))
    }

    @Test
    fun `a midi, entre deux rendez-vous, ceux a venir passent devant celui de 10 h`() {
        val entries = listOf(e("rdv 10h", 10), e("déjeuner 13h", 13), e("sport 18h", 18))
        assertEquals(listOf("déjeuner 13h", "sport 18h", "rdv 10h"), order(entries, at(12)))
    }

    @Test
    fun `un message passe reste dans la liste, sans modification`() {
        val entries = listOf(e("rdv 10h", 10), e("sport 18h", 18))
        val shown = PlanningOrder.forDisplay(entries, day, at(23, 59))
        assertEquals(entries.toSet(), shown.toSet())
    }

    @Test
    fun `les messages sans heure restent tout en bas, dans l'ordre de saisie`() {
        val entries = listOf(e("b"), e("rdv", 9), e("a"), e("passé", 7))
        assertEquals(listOf("rdv", "passé", "b", "a"), order(entries, at(8)))
    }

    @Test
    fun `a heure egale, l'ordre de saisie est conserve`() {
        assertEquals(listOf("premier", "second"), order(listOf(e("premier", 9), e("second", 9)), at(6)))
    }

    @Test
    fun `sans aucune heure, l'ordre de saisie est conserve`() {
        assertEquals(listOf("c", "a", "b"), order(listOf(e("c"), e("a"), e("b")), at(12)))
    }

    @Test
    fun `un rendez-vous a l'heure exacte est encore a venir`() {
        assertEquals(listOf("maintenant", "avant"), order(listOf(e("avant", 9), e("maintenant", 12)), at(12)))
    }

    @Test
    fun `un jour futur garde tous ses messages a venir dans l'ordre chronologique`() {
        val tomorrowMorning = LocalDateTime.of(day.minusDays(1), LocalTime.of(23, 0))
        assertEquals(listOf("8", "20"), order(listOf(e("20", 20), e("8", 8)), tomorrowMorning))
    }

    @Test
    fun `une ligne sans texte n'est pas affichee`() {
        assertEquals(listOf("a"), order(listOf(e("", 9), e("a")), at(8)))
    }
}
