package com.astro.app.core.data

import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.astro.app.core.time.TimeScale
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
import org.junit.Assert.assertFalse
import org.junit.Test

/** Persistance réelle des réglages du Calendrier, sur un fichier DataStore temporaire. */
class CalendarPreferencesStorageTest {

    private val directory: File = Files.createTempDirectory("astro-prefs").toFile()
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val dataStore = PreferenceDataStoreFactory.create(scope = scope) {
        File(directory, "test.preferences_pb")
    }
    private val repository = DataStoreSettingsRepository(dataStore)

    @After
    fun tearDown() {
        scope.cancel()
        directory.deleteRecursively()
    }

    @Test
    fun `sans reglage, la vue d'ouverture est le Mois et les marqueurs sont actifs`() = runBlocking {
        val preferences = repository.preferences.first()
        assertEquals(TimeScale.MONTH, preferences.calendarInitialScale)
        assertEquals(true, preferences.showFullMoons)
        assertEquals(true, preferences.showPlanningMarkers)
    }

    @Test
    fun `les reglages du Calendrier sont relus apres ecriture`() = runBlocking {
        repository.updatePreferences {
            it.copy(calendarInitialScale = TimeScale.WEEK, showFullMoons = false, showPlanningMarkers = false)
        }
        val preferences = repository.preferences.first()
        assertEquals(TimeScale.WEEK, preferences.calendarInitialScale)
        assertFalse(preferences.showFullMoons)
        assertFalse(preferences.showPlanningMarkers)
    }

    @Test
    fun `une vue inconnue dans le fichier retombe sur le Mois`() = runBlocking {
        dataStore.edit { it[stringPreferencesKey("calendar_initial_scale")] = "DECENNIE" }
        assertEquals(TimeScale.MONTH, repository.preferences.first().calendarInitialScale)
    }

    @Test
    fun `le placement des notes sans heure est en bas par defaut et se conserve`() = runBlocking {
        assertEquals(false, repository.preferences.first().untimedNotesFirst)
        repository.updatePreferences { it.copy(untimedNotesFirst = true) }
        assertEquals(true, repository.preferences.first().untimedNotesFirst)
    }
}
