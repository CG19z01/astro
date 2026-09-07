package com.astro.app.core.location

import com.astro.app.core.settings.UserPreferences
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ObservationPointsTest {

    private val saved = UserPreferences(latitude = 48.86, longitude = 2.35)
    private val session = GeoPoint(latitude = 43.30, longitude = 5.37)

    @Test
    fun `sans rien, aucun lieu n'est resolu`() {
        assertNull(ObservationPoints.resolve(null, UserPreferences()))
    }

    @Test
    fun `les coordonnees enregistrees servent en l'absence de position de session`() {
        val point = ObservationPoints.resolve(null, saved)!!
        assertEquals(48.86, point.latitude, 1e-9)
        assertTrue("devrait être permanent", !point.temporary)
    }

    @Test
    fun `la position de session prime sur les coordonnees enregistrees`() {
        val point = ObservationPoints.resolve(session, saved)!!
        assertEquals(43.30, point.latitude, 1e-9)
        assertEquals(5.37, point.longitude, 1e-9)
        assertTrue("devrait être temporaire", point.temporary)
    }

    @Test
    fun `la position de session suffit sans coordonnees enregistrees`() {
        val point = ObservationPoints.resolve(session, UserPreferences())!!
        assertEquals(43.30, point.latitude, 1e-9)
        assertTrue(point.temporary)
    }

    @Test
    fun `des coordonnees incompletes ne resolvent aucun lieu`() {
        assertNull(ObservationPoints.resolve(null, UserPreferences(latitude = 48.86)))
        assertNull(ObservationPoints.resolve(null, UserPreferences(longitude = 2.35)))
    }
}
