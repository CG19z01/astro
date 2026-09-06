package com.astro.app.core.time

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Règle validée par l'utilisateur : 3 swipes rapides, dans une fenêtre de 1500 ms,
 * font changer de temporalité. Ce geste est distinct du swipe simple de la Home.
 */
class SwipeBurstDetectorTest {

    private fun detector() = SwipeBurstDetector(swipesRequired = 3, windowMillis = 1_500L)

    @Test
    fun `trois swipes dans la fenetre declenchent le changement`() {
        val detector = detector()
        assertFalse(detector.onSwipe(SwipeDirection.FORWARD, 0L))
        assertFalse(detector.onSwipe(SwipeDirection.FORWARD, 500L))
        assertTrue(detector.onSwipe(SwipeDirection.FORWARD, 1_400L))
    }

    @Test
    fun `trois swipes exactement a la limite de 1500 ms declenchent encore`() {
        val detector = detector()
        detector.onSwipe(SwipeDirection.FORWARD, 0L)
        detector.onSwipe(SwipeDirection.FORWARD, 700L)
        assertTrue(detector.onSwipe(SwipeDirection.FORWARD, 1_500L))
    }

    @Test
    fun `un premier swipe trop ancien sort de la fenetre et rien ne se declenche`() {
        val detector = detector()
        detector.onSwipe(SwipeDirection.FORWARD, 0L)
        detector.onSwipe(SwipeDirection.FORWARD, 1_000L)
        assertFalse(detector.onSwipe(SwipeDirection.FORWARD, 1_501L))
    }

    @Test
    fun `une salve trop lente ne bloque pas la salve suivante`() {
        val detector = detector()
        detector.onSwipe(SwipeDirection.FORWARD, 0L)
        detector.onSwipe(SwipeDirection.FORWARD, 2_000L)
        detector.onSwipe(SwipeDirection.FORWARD, 4_000L)
        assertFalse(detector.onSwipe(SwipeDirection.FORWARD, 4_100L))
        assertTrue(detector.onSwipe(SwipeDirection.FORWARD, 4_200L))
    }

    @Test
    fun `changer de sens annule la salve en cours`() {
        val detector = detector()
        detector.onSwipe(SwipeDirection.FORWARD, 0L)
        detector.onSwipe(SwipeDirection.FORWARD, 100L)
        assertFalse(detector.onSwipe(SwipeDirection.BACKWARD, 200L))
        assertFalse(detector.onSwipe(SwipeDirection.BACKWARD, 300L))
        assertTrue(detector.onSwipe(SwipeDirection.BACKWARD, 400L))
    }

    @Test
    fun `six swipes rapides valent deux changements`() {
        val detector = detector()
        val triggers = (0..5).count { index ->
            detector.onSwipe(SwipeDirection.FORWARD, index * 100L)
        }
        assertEquals(2, triggers)
    }

    @Test
    fun `reset vide la salve en cours`() {
        val detector = detector()
        detector.onSwipe(SwipeDirection.FORWARD, 0L)
        detector.onSwipe(SwipeDirection.FORWARD, 100L)
        detector.reset()
        assertFalse(detector.onSwipe(SwipeDirection.FORWARD, 200L))
    }
}
