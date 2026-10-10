package com.astro.app.core.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.astro.app.core.planning.PlanningCodec
import com.astro.app.core.planning.DayPlanning
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
 *
 * Une seule clé par date, qu'elle contienne une ancienne note ou le format à entrées :
 * [PlanningCodec] sait lire les deux. Il n'y a donc qu'une source de vérité, et aucune
 * migration de masse — une note n'est réécrite que lorsqu'on la modifie.
 */
class DataStorePlanningRepository(
    private val dataStore: DataStore<Preferences>,
) : PlanningRepository {

    private val preferences: Flow<Preferences> = dataStore.data
        .catch { cause -> if (cause is IOException) emit(emptyPreferences()) else throw cause }

    override fun entryFor(date: LocalDate): Flow<String> =
        entriesFor(date).map(PlanningCodec::display)

    override fun dayFor(date: LocalDate): Flow<DayPlanning> =
        preferences.map { stored -> PlanningCodec.decodeDay(stored[keyOf(date)].orEmpty()) }

    /**
     * Les plannings sont rangés parmi les autres préférences : on les reconnaît à leur
     * préfixe. Une clé dont la date ne s'analyse pas est ignorée plutôt que de faire
     * échouer toute la lecture, et un planning sans entrée n'en est pas un.
     */
    override fun allEntries(): Flow<Map<LocalDate, String>> =
        preferences.map { stored ->
            stored.asMap().mapNotNull { (key, value) ->
                val date = key.name.removePrefix(PLANNING_KEY_PREFIX)
                    .takeIf { key.name.startsWith(PLANNING_KEY_PREFIX) }
                    ?.let { runCatching { LocalDate.parse(it, DateTimeFormatter.ISO_LOCAL_DATE) }.getOrNull() }
                val text = (value as? String)?.let { PlanningCodec.display(PlanningCodec.decode(it)) }
                if (date != null && !text.isNullOrBlank()) date to text else null
            }.toMap()
        }

    override suspend fun save(date: LocalDate, text: String) =
        saveEntries(date, PlanningCodec.decode(text))

    override suspend fun saveDay(date: LocalDate, day: DayPlanning) {
        val encoded = PlanningCodec.encodeDay(day)
        dataStore.edit { stored ->
            if (encoded.isEmpty()) stored.remove(keyOf(date)) else stored[keyOf(date)] = encoded
        }
    }

    private fun keyOf(date: LocalDate) =
        stringPreferencesKey(PLANNING_KEY_PREFIX + date.format(DateTimeFormatter.ISO_LOCAL_DATE))
}
