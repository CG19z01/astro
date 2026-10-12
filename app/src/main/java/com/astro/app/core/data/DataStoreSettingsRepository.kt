package com.astro.app.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.emptyPreferences
import com.astro.app.core.config.AlarmConfig
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.PlanningConfig
import com.astro.app.core.config.FeatureId
import com.astro.app.core.settings.FeatureStates
import com.astro.app.core.settings.SettingsRepository
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.time.TimeScale
import java.io.IOException
import java.time.ZoneId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Préfixe des clés de préférence. Ne pas modifier : les réglages existants seraient perdus. */
private const val FEATURE_KEY_PREFIX = "feature_enabled_"

private val SHOW_NAV_BAR = booleanPreferencesKey("show_navigation_bar")
private val ZONE_ID = stringPreferencesKey("zone_id")
private val LATITUDE = doublePreferencesKey("latitude")
private val LONGITUDE = doublePreferencesKey("longitude")
private val CALENDAR_INITIAL_SCALE = stringPreferencesKey("calendar_initial_scale")
private val SHOW_FULL_MOONS = booleanPreferencesKey("calendar_show_full_moons")
private val SHOW_PLANNING_MARKERS = booleanPreferencesKey("calendar_show_planning_markers")
private val UNTIMED_NOTES_FIRST = booleanPreferencesKey("home_untimed_notes_first")
private val SHOW_NEXT_ALARM = booleanPreferencesKey("home_show_next_alarm")
private val SPEECH_VOICE = stringPreferencesKey("speech_voice_name")

/**
 * Implémentation locale de [SettingsRepository], adossée à DataStore Preferences.
 *
 * Les valeurs par défaut ne sont pas écrites au premier lancement : une préférence
 * absente est résolue à la lecture par [FeatureStates.merge]. Changer une valeur
 * par défaut dans le catalogue s'applique donc aux utilisateurs qui n'y ont pas touché.
 */
class DataStoreSettingsRepository(
    private val dataStore: DataStore<Preferences>,
) : SettingsRepository {

    override val featureStates: Flow<Map<FeatureId, Boolean>> =
        dataStore.data
            // Un fichier illisible ne doit pas faire planter l'application :
            // on repart des valeurs par défaut du catalogue.
            .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
            .map { preferences -> FeatureStates.merge(readStored(preferences)) }

    override suspend fun setFeatureEnabled(id: FeatureId, enabled: Boolean) {
        if (!FeatureCatalog.definitionOf(id).canBeDisabled) return
        dataStore.edit { preferences -> preferences[keyOf(id)] = enabled }
    }

    override val preferences: Flow<UserPreferences> =
        dataStore.data
            .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
            .map { stored -> readPreferences(stored) }

    override suspend fun updatePreferences(transform: (UserPreferences) -> UserPreferences) {
        dataStore.edit { stored ->
            val updated = transform(readPreferences(stored))
            stored[SHOW_NAV_BAR] = updated.showNavigationBar
            stored[ZONE_ID] = updated.zoneId.id
            updated.latitude?.let { stored[LATITUDE] = it } ?: stored.remove(LATITUDE)
            updated.longitude?.let { stored[LONGITUDE] = it } ?: stored.remove(LONGITUDE)
            stored.writePlanetColors(updated.planetColors)
            stored[CALENDAR_INITIAL_SCALE] = updated.calendarInitialScale.name
            stored[SHOW_FULL_MOONS] = updated.showFullMoons
            stored[SHOW_PLANNING_MARKERS] = updated.showPlanningMarkers
            stored[UNTIMED_NOTES_FIRST] = updated.untimedNotesFirst
            stored[SHOW_NEXT_ALARM] = updated.showNextAlarm
            updated.speechVoiceName?.let { stored[SPEECH_VOICE] = it } ?: stored.remove(SPEECH_VOICE)
        }
    }

    /**
     * Un identifiant de fuseau devenu inconnu retombe sur celui de l'appareil, un nom
     * de vue inconnu sur la vue par défaut du Calendrier. La vue est stockée par son
     * NOM et non par son rang : réordonner les vues ne changerait pas le choix enregistré.
     */
    private fun readPreferences(stored: Preferences) = UserPreferences(
        planetColors = readPlanetColors(stored),
        showNavigationBar = stored[SHOW_NAV_BAR] ?: true,
        zoneId = stored[ZONE_ID]?.let { runCatching { ZoneId.of(it) }.getOrNull() } ?: ZoneId.systemDefault(),
        latitude = stored[LATITUDE],
        longitude = stored[LONGITUDE],
        calendarInitialScale = TimeScale.entries.firstOrNull { it.name == stored[CALENDAR_INITIAL_SCALE] }
            ?: CalendarConfig.DEFAULT_INITIAL_SCALE,
        showFullMoons = stored[SHOW_FULL_MOONS] ?: CalendarConfig.DEFAULT_SHOW_FULL_MOONS,
        showPlanningMarkers = stored[SHOW_PLANNING_MARKERS] ?: CalendarConfig.DEFAULT_SHOW_PLANNING_MARKERS,
        untimedNotesFirst = stored[UNTIMED_NOTES_FIRST] ?: PlanningConfig.DEFAULT_UNTIMED_FIRST,
        showNextAlarm = stored[SHOW_NEXT_ALARM] ?: AlarmConfig.DEFAULT_SHOW_NEXT_ALARM,
        speechVoiceName = stored[SPEECH_VOICE],
    )

    private fun readStored(preferences: Preferences): Map<FeatureId, Boolean> =
        FeatureId.entries.mapNotNull { id ->
            preferences[keyOf(id)]?.let { stored -> id to stored }
        }.toMap()

    private fun keyOf(id: FeatureId) = booleanPreferencesKey(FEATURE_KEY_PREFIX + id.name)
}
