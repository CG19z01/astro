package com.astro.app.core.manifestation

import com.astro.app.core.data.FileManifestationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class FileManifestationRepositoryTest {

    @get:Rule val folder = TemporaryFolder()

    private fun repository() = FileManifestationRepository(folder.root, UnconfinedTestDispatcher())

    @Test
    fun `creer, relire, modifier, supprimer`() = runTest {
        val repo = repository()
        val first = repo.save(Manifestation(title = "Paix", subject = "Calme", text = "Je respire."))
        val second = repo.save(Manifestation(title = "Élan", subject = "Travail", text = "J'avance « vite »."))
        assertEquals(listOf(second, first), repository().all.first().map { it.id })

        repo.save(Manifestation(first, "Paix intérieure", "Calme", "Je respire lentement."))
        assertEquals("Paix intérieure", repository().all.first().first { it.id == first }.title)

        repo.delete(second)
        assertEquals(listOf(first), repository().all.first().map { it.id })
    }

    @Test
    fun `un id supprime n'est pas reattribue au suivant s'il etait le plus grand`() = runTest {
        val repo = repository()
        val a = repo.save(Manifestation(title = "A", subject = "", text = ""))
        val b = repo.save(Manifestation(title = "B", subject = "", text = ""))
        assertEquals(a + 1, b)
    }
}
