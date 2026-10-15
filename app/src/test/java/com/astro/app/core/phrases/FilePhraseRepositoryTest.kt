package com.astro.app.core.phrases

import com.astro.app.core.data.FilePhraseRepository
import java.io.File
import java.time.LocalDate
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

@OptIn(ExperimentalCoroutinesApi::class)
class FilePhraseRepositoryTest {

    @get:Rule val folder = TemporaryFolder()
    private val day = LocalDate.of(2026, 10, 11)

    private fun repository(dir: File = folder.root) = FilePhraseRepository(dir, UnconfinedTestDispatcher())

    @Test
    fun `les phrases et le journal survivent a une relecture`() = runTest {
        repository().apply {
            replacePhrases(listOf(Phrase("Ose.", "Moi")))
            record(day, Phrase("Ose.", "Moi"))
        }
        val reread = repository()
        assertEquals(listOf(Phrase("Ose.", "Moi")), reread.phrases.first())
        assertEquals(mapOf(day to Phrase("Ose.", "Moi")), reread.journal.first())
    }

    @Test
    fun `un import remplace la liste mais garde le journal`() = runTest {
        val repo = repository()
        repo.replacePhrases(listOf(Phrase("A")))
        repo.record(day, Phrase("A"))
        repo.replacePhrases(listOf(Phrase("B")))
        assertEquals(listOf(Phrase("B")), repo.phrases.first())
        assertEquals(Phrase("A"), repo.journal.first()[day])
    }

    @Test
    fun `une journee deja inscrite ne change plus`() = runTest {
        val repo = repository()
        repo.record(day, Phrase("A"))
        repo.record(day, Phrase("B"))
        assertEquals(Phrase("A"), repo.journal.first()[day])
    }

    @Test
    fun `un fichier corrompu est mis de cote, pas ecrase`() = runTest {
        File(folder.root, "phrases.json").writeText("{ pas du json")
        assertEquals(emptyList<Phrase>(), repository().phrases.first())
        assertTrue(File(folder.root, "phrases.json.illisible").exists())
    }

    @Test
    fun `la phrase du jour est inscrite une seule fois`() = runTest {
        val repo = repository()
        repo.replacePhrases(listOf(Phrase("A"), Phrase("B")))
        val daily = DailyPhrase(repo) { day }
        val first = daily.ofToday().first()
        repo.replacePhrases(listOf(Phrase("C")))
        assertEquals(first, daily.ofToday().first())
        assertEquals(1, repo.journal.first().size)
    }

    @Test
    fun `changer de genre remplace la phrase du jour, pas celle des jours passes`() = runTest {
        val repo = repository()
        val fier = Phrase("Fier", gender = PhraseGender.MASCULINE)
        val fiere = Phrase("Fière", gender = PhraseGender.FEMININE)
        repo.replacePhrases(listOf(fier, fiere))
        repo.record(day.minusDays(1), fier)
        val wishes = kotlinx.coroutines.flow.MutableStateFlow(PhraseWishes(ReaderGender.MASCULINE))
        val daily = DailyPhrase(repo, wishes) { day }
        assertEquals(fier, daily.ofToday().first())
        wishes.value = PhraseWishes(ReaderGender.FEMININE)
        assertEquals(fiere, daily.ofToday().first())
        assertEquals(fiere, repo.journal.first()[day])
        assertEquals(fier, repo.journal.first()[day.minusDays(1)])
    }

    @Test
    fun `favoris ajoutes, relus, retires, et gardes apres un import`() = runTest {
        val repo = repository()
        repo.replacePhrases(listOf(Phrase("A"), Phrase("B")))
        repo.toggleFavorite(Phrase("A"))
        repo.toggleFavorite(Phrase("B"))
        assertEquals(listOf(Phrase("B"), Phrase("A")), repository().favorites.first())

        repo.replacePhrases(listOf(Phrase("C")))
        assertEquals(listOf(Phrase("B"), Phrase("A")), repo.favorites.first())

        repo.toggleFavorite(Phrase("B"))
        assertEquals(listOf(Phrase("A")), repo.favorites.first())
    }

    @Test
    fun `changer de genre ne remplace jamais une phrase neutre`() = runTest {
        val repo = repository()
        val neutre = Phrase("Neutre")
        repo.replacePhrases(listOf(neutre, Phrase("Fier", gender = PhraseGender.MASCULINE)))
        val wishes = kotlinx.coroutines.flow.MutableStateFlow(PhraseWishes(ReaderGender.OTHER))
        val daily = DailyPhrase(repo, wishes) { day }
        assertEquals(neutre, daily.ofToday().first())
        listOf(ReaderGender.MASCULINE, ReaderGender.OTHER, ReaderGender.FEMININE).forEach { gender ->
            wishes.value = PhraseWishes(gender)
            assertEquals(neutre, daily.ofToday().first())
        }
    }
}
