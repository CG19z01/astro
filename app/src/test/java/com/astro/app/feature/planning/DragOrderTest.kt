package com.astro.app.feature.planning

import org.junit.Assert.assertEquals
import org.junit.Test

/** Où tombe une ligne glissée, et comment les autres lui cèdent la place. */
class DragOrderTest {

    private val heights = listOf(100, 100, 100, 100)
    private val spacing = 10f
    private fun target(from: Int, offset: Float) = DragOrder.targetIndex(heights, from, offset, spacing, maxIndex = 2)

    @Test
    fun `sans deplacement, la ligne reste a sa place`() {
        assertEquals(1, target(1, 0f))
    }

    @Test
    fun `il faut franchir la moitie de la voisine pour changer de place`() {
        assertEquals(1, target(1, 54f))
        assertEquals(2, target(1, 56f))
        assertEquals(1, target(1, -54f))
        assertEquals(0, target(1, -56f))
    }

    @Test
    fun `un long glissement franchit plusieurs lignes`() {
        assertEquals(2, target(0, 230f))
        assertEquals(0, target(2, -230f))
    }

    @Test
    fun `on ne descend jamais sous la ligne vide finale`() {
        assertEquals(2, target(0, 10_000f))
    }

    @Test
    fun `on ne monte pas au-dessus de la premiere ligne`() {
        assertEquals(0, target(2, -10_000f))
    }

    @Test
    fun `les lignes franchies glissent d'une hauteur dans le bon sens`() {
        // Ligne 0 descendue en 2 : les lignes 1 et 2 remontent, la 3 ne bouge pas.
        assertEquals(-110f, DragOrder.shiftOf(1, from = 0, target = 2, draggedHeight = 110f))
        assertEquals(-110f, DragOrder.shiftOf(2, from = 0, target = 2, draggedHeight = 110f))
        assertEquals(0f, DragOrder.shiftOf(3, from = 0, target = 2, draggedHeight = 110f))
        // Ligne 2 remontée en 0 : les lignes 0 et 1 descendent.
        assertEquals(110f, DragOrder.shiftOf(0, from = 2, target = 0, draggedHeight = 110f))
        assertEquals(0f, DragOrder.shiftOf(2, from = 2, target = 0, draggedHeight = 110f))
    }
}
