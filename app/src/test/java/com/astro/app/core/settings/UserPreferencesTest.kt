package com.astro.app.core.settings

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class UserPreferencesTest {

    @Test
    fun `une latitude dans les bornes est acceptee`() {
        assertEquals(48.86, UserPreferences.validLatitude(48.86))
        assertEquals(-90.0, UserPreferences.validLatitude(-90.0))
        assertEquals(90.0, UserPreferences.validLatitude(90.0))
    }

    @Test
    fun `une latitude impossible est rejetee`() {
        assertNull(UserPreferences.validLatitude(91.0))
        assertNull(UserPreferences.validLatitude(-90.1))
        assertNull(UserPreferences.validLatitude(null))
    }

    @Test
    fun `une longitude hors des bornes est rejetee`() {
        assertEquals(2.35, UserPreferences.validLongitude(2.35))
        assertEquals(-180.0, UserPreferences.validLongitude(-180.0))
        assertNull(UserPreferences.validLongitude(180.5))
    }

    @Test
    fun `sans coordonnees completes, la position n'est pas exploitable`() {
        assertEquals(false, UserPreferences().hasLocation)
        assertEquals(false, UserPreferences(latitude = 48.86).hasLocation)
        assertEquals(true, UserPreferences(latitude = 48.86, longitude = 2.35).hasLocation)
    }
}
