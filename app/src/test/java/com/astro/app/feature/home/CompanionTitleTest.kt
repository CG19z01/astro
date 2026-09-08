package com.astro.app.feature.home

import com.astro.app.core.config.HomeLayoutConfig
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CompanionTitleTest {

    @Test
    fun `le titre ne depasse jamais la longueur configuree`() {
        listOf("Randonnée en montagne", "Court", "a", "").forEach { planning ->
            assertTrue(CompanionTitle.of(planning).length <= HomeLayoutConfig.COMPANION_TITLE_LENGTH)
        }
    }

    @Test
    fun `un titre long est tronque a cinq caracteres`() {
        assertEquals("Rando", CompanionTitle.of("Randonnée en montagne"))
    }

    @Test
    fun `un titre court reste entier`() {
        assertEquals("Yoga", CompanionTitle.of("Yoga"))
    }

    @Test
    fun `seule la premiere ligne non vide est retenue`() {
        assertEquals("Sport", CompanionTitle.of("\n\nSport intensif\nRepos"))
    }

    @Test
    fun `les espaces de bordure sont retires avant la troncature`() {
        assertEquals("Repos", CompanionTitle.of("   Repos du guerrier   "))
    }

    @Test
    fun `un planning vide ne donne aucun titre`() {
        assertEquals("", CompanionTitle.of(""))
        assertEquals("", CompanionTitle.of("   \n  \n "))
    }
}
