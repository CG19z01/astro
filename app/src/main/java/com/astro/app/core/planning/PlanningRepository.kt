package com.astro.app.core.planning

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Planning associé à une date.
 *
 * Interface indépendante du stockage, comme le reste des données de l'application :
 * l'implémentation actuelle est locale, un passage au distant se fera par une nouvelle
 * implémentation sans toucher aux écrans.
 */
interface PlanningRepository {

    /**
     * Texte lisible du planning de cette date — une entrée par ligne, l'heure devant
     * ([PlanningCodec.display]). Chaîne vide s'il n'y a rien.
     */
    fun entryFor(date: LocalDate): Flow<String>

    /** Planning de cette date : entrées dans l'ordre enregistré, et choix d'ordre. */
    fun dayFor(date: LocalDate): Flow<DayPlanning>

    /** Entrées du planning de cette date, dans l'ordre enregistré. Liste vide s'il n'y a rien. */
    fun entriesFor(date: LocalDate): Flow<List<PlanningEntry>> = dayFor(date).map { it.entries }

    /**
     * Tous les plannings enregistrés, indexés par date. Ne contient que des textes non
     * vides : une date absente de la table n'a pas de planning.
     *
     * Lecture directe du stockage, sans copie : c'est ce qui garantit qu'un marqueur
     * ne peut pas signaler une note qui n'existe pas.
     */
    fun allEntries(): Flow<Map<LocalDate, String>>

    /**
     * Enregistre un texte libre : chaque ligne non blanche devient une entrée sans heure.
     * Un texte vide efface le planning plutôt que de stocker du vide.
     */
    suspend fun save(date: LocalDate, text: String)

    /** Enregistre les entrées. Les lignes sans texte sont omises ; sans entrée, le planning est effacé. */
    suspend fun saveDay(date: LocalDate, day: DayPlanning)

    /** Enregistre des entrées en ordre automatique. */
    suspend fun saveEntries(date: LocalDate, entries: List<PlanningEntry>) = saveDay(date, DayPlanning(entries))
}
