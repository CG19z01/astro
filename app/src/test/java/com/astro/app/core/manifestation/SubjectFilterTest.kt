package com.astro.app.core.manifestation

import org.junit.Assert.assertEquals
import org.junit.Test

class SubjectFilterTest {

    private fun m(id: Long, subject: String) = Manifestation(id, "T$id", subject, "")

    private val all = listOf(m(3, "Travail"), m(2, " amour "), m(1, "Amour"), m(4, ""), m(5, "Énergie"))

    @Test
    fun `sujets distincts, sans casse ni espaces, sans vide`() {
        assertEquals(listOf("amour", "Travail", "Énergie"), SubjectFilter.subjects(all))
    }

    @Test
    fun `filtrer ignore la casse et les espaces`() {
        assertEquals(listOf(2L, 1L), SubjectFilter.filter(all, "AMOUR").map { it.id })
    }

    @Test
    fun `sans sujet, toutes`() {
        assertEquals(all, SubjectFilter.filter(all, null))
    }
}
