package com.astro.app.core.planning

import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Test

/** Ordre des notes sur une planète : manuel s'il existe, sinon par heure. */
class PlanningOrderTest {

    private var nextId = 0L
    private fun e(text: String, h: Int? = null) = PlanningEntry(nextId++, text, h?.let { LocalTime.of(it, 0) })
    private fun order(entries: List<PlanningEntry>, untimedFirst: Boolean = false, manual: Boolean = false) =
        PlanningOrder.forDisplay(DayPlanning(entries, manual), untimedFirst).map { it.text }

    @Test
    fun `sans ordre manuel, les notes vont du plus tot au plus tard`() {
        assertEquals(listOf("8", "10", "20"), order(listOf(e("20", 20), e("8", 8), e("10", 10))))
    }

    @Test
    fun `un rendez-vous passe reste a sa place chronologique`() {
        // Plus de règle « les passés descendent » : l'ordre ne dépend pas de l'heure actuelle.
        assertEquals(listOf("10", "13", "18"), order(listOf(e("13", 13), e("18", 18), e("10", 10))))
    }

    @Test
    fun `reglage desactive, les notes sans heure sont en bas dans l'ordre de saisie`() {
        assertEquals(listOf("9", "b", "a"), order(listOf(e("b"), e("9", 9), e("a"))))
    }

    @Test
    fun `reglage active, les notes sans heure sont en haut dans l'ordre de saisie`() {
        assertEquals(listOf("b", "a", "9"), order(listOf(e("b"), e("9", 9), e("a")), untimedFirst = true))
    }

    @Test
    fun `a heure egale, l'ordre de saisie est conserve`() {
        assertEquals(listOf("premier", "second"), order(listOf(e("premier", 9), e("second", 9))))
    }

    @Test
    fun `l'ordre manuel l'emporte sur le tri et sur le reglage`() {
        val entries = listOf(e("soir", 20), e("sans"), e("matin", 8))
        assertEquals(listOf("soir", "sans", "matin"), order(entries, manual = true))
        assertEquals(listOf("soir", "sans", "matin"), order(entries, untimedFirst = true, manual = true))
    }

    @Test
    fun `une ligne sans texte n'est pas affichee`() {
        assertEquals(listOf("a"), order(listOf(e("", 9), e("a"))))
    }
}
