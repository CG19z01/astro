package com.astro.app.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureId
import com.astro.app.core.settings.FeatureStates
import com.astro.app.core.settings.SettingsRepository
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Préfixe des clés de préférence. Ne pas modifier : les réglages existants seraient perdus. */
private const val FEATURE_KEY_PREFIX = "feature_enabled_"

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

    private fun readStored(preferences: Preferences): Map<FeatureId, Boolean> =
        FeatureId.entries.mapNotNull { id ->
            preferences[keyOf(id)]?.let { stored -> id to stored }
        }.toMap()

    private fun keyOf(id: FeatureId) = booleanPreferencesKey(FEATURE_KEY_PREFIX + id.name)
}
