package com.astro.app.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import com.astro.app.core.phrases.ReaderGender
import java.io.File
import java.nio.file.Files
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

/** Persistance réelle du genre déclaré, des thèmes et de la voix, sur un DataStore temporaire. */
class PhrasePreferencesStorageTest {

    private val directory: File = Files.createTempDirectory("astro-phrases").toFile()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataStore = PreferenceDataStoreFactory.create(scope = scope) { File(directory, "t.preferences_pb") }
    private val repository = DataStoreSettingsRepository(dataStore)

    @After
    fun tearDown() {
        scope.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun `rien de renseigne au depart`() = runBlocking {
        val prefs = repository.preferences.first()
        assertNull(prefs.readerGender)
        assertEquals(emptySet<String>(), prefs.phraseThemes)
    }

    @Test
    fun `genre et themes relus apres ecriture`() = runBlocking {
        repository.updatePreferences { it.copy(readerGender = ReaderGender.MASCULINE, phraseThemes = setOf("Paix")) }
        val prefs = repository.preferences.first()
        assertEquals(ReaderGender.MASCULINE, prefs.readerGender)
        assertEquals(setOf("Paix"), prefs.phraseThemes)
    }

    @Test
    fun `revenir a tous les themes`() = runBlocking {
        repository.updatePreferences { it.copy(phraseThemes = setOf("Paix")) }
        repository.updatePreferences { it.copy(phraseThemes = emptySet()) }
        assertEquals(emptySet<String>(), repository.preferences.first().phraseThemes)
    }
}
