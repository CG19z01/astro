package com.astro.app.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.astro.app.core.planning.PlanningRepository
import java.io.IOException
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

/** Préfixe des clés. Ne pas modifier : les plannings existants seraient perdus. */
private const val PLANNING_KEY_PREFIX = "planning_"

/**
 * Implémentation locale de [PlanningRepository], adossée à DataStore Preferences.
 *
 * La clé est la date en ISO 8601, format stable et indépendant de la langue de
 * l'appareil : un utilisateur qui change la langue de son téléphone ne perd rien.
 */
class DataStorePlanningRepository(
    private val dataStore: DataStore<Preferences>,
) : PlanningRepository {

    override fun entryFor(date: LocalDate): Flow<String> =
        dataStore.data
            .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }
            .map { preferences -> preferences[keyOf(date)].orEmpty() }

    override suspend fun save(date: LocalDate, text: String) {
        dataStore.edit { preferences ->
            val trimmed = text.trim()
            if (trimmed.isEmpty()) preferences.remove(keyOf(date)) else preferences[keyOf(date)] = trimmed
        }
    }

    private fun keyOf(date: LocalDate) =
        stringPreferencesKey(PLANNING_KEY_PREFIX + date.format(DateTimeFormatter.ISO_LOCAL_DATE))
}
