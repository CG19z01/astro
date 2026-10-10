package com.astro.app.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import java.io.File
import java.nio.file.Files
import com.astro.app.core.planning.PlanningEntry
import java.time.LocalDate
import java.time.LocalTime
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * La source des bulles du Calendrier, vérifiée sur un vrai fichier DataStore : une
 * bulle ne doit jamais signaler une note absente du stockage.
 */
class PlanningEntriesStorageTest {

    private val directory: File = Files.createTempDirectory("astro-planning").toFile()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
        File(directory, "test.preferences_pb")
    }
    private val repository = DataStorePlanningRepository(dataStore)
    private val october18 = LocalDate.of(2026, 10, 18)

    @After
    fun tearDown() {
        scope.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun `sans note, aucune date n'est renseignee`() = runBlocking {
        assertTrue(repository.allEntries().first().isEmpty())
    }

    @Test
    fun `une note enregistree apparait a sa date`() = runBlocking {
        repository.save(october18, "Dîner chez Marie")
        assertEquals(mapOf(october18 to "Dîner chez Marie"), repository.allEntries().first())
    }

    @Test
    fun `une note effacee disparait`() = runBlocking {
        repository.save(october18, "Dîner")
        repository.save(october18, "")
        assertTrue(repository.allEntries().first().isEmpty())
    }

    @Test
    fun `une note faite d'espaces n'est pas une note`() = runBlocking {
        repository.save(october18, "   \n  ")
        assertTrue(repository.allEntries().first().isEmpty())
    }

    @Test
    fun `chaque date est lue independamment`() = runBlocking {
        repository.save(october18, "a")
        repository.save(october18.plusDays(2), "b")
        assertEquals(setOf(october18, october18.plusDays(2)), repository.allEntries().first().keys)
    }

    @Test
    fun `les autres preferences et les cles illisibles sont ignorees`() = runBlocking {
        dataStore.edit {
            it[booleanPreferencesKey("show_navigation_bar")] = false
            it[stringPreferencesKey("zone_id")] = "Europe/Paris"
            it[stringPreferencesKey("planning_pas-une-date")] = "texte"
        }
        repository.save(october18, "a")
        assertEquals(setOf(october18), repository.allEntries().first().keys)
    }

    @Test
    fun `la lecture d'une date et la lecture globale concordent`() = runBlocking {
        repository.save(october18, "Rendez-vous")
        assertEquals(repository.entryFor(october18).first(), repository.allEntries().first()[october18])
    }

    @Test
    fun `une ancienne note enregistree avant les lignes reste lisible partout`() = runBlocking {
        val legacy = "Sport le matin\nDéjeuner avec Paul"
        dataStore.edit { it[stringPreferencesKey("planning_2026-10-18")] = legacy }
        assertEquals(listOf("Sport le matin", "Déjeuner avec Paul"), repository.entriesFor(october18).first().map { it.text })
        assertEquals(legacy, repository.entryFor(october18).first())
        assertEquals(legacy, repository.allEntries().first()[october18])
    }

    @Test
    fun `lire une ancienne note ne la reecrit pas`() = runBlocking {
        dataStore.edit { it[stringPreferencesKey("planning_2026-10-18")] = "Dîner" }
        repository.entriesFor(october18).first()
        assertEquals("Dîner", dataStore.data.first()[stringPreferencesKey("planning_2026-10-18")])
    }

    @Test
    fun `des entrees avec heure sont relues et affichees heure devant`() = runBlocking {
        repository.saveEntries(october18, listOf(PlanningEntry(0, "Dîner", LocalTime.of(20, 30)), PlanningEntry(1, "Courses")))
        assertEquals(LocalTime.of(20, 30), repository.entriesFor(october18).first().first().time)
        assertEquals("20:30 Dîner\nCourses", repository.allEntries().first()[october18])
    }

    @Test
    fun `des entrees sans texte effacent le planning`() = runBlocking {
        repository.save(october18, "Dîner")
        repository.saveEntries(october18, listOf(PlanningEntry(0, " ", LocalTime.of(9, 0))))
        assertTrue(repository.allEntries().first().isEmpty())
    }
}
