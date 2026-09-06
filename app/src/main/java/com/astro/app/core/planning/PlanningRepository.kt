package com.astro.app.core.planning

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Planning associé à une date.
 *
 * Interface indépendante du stockage, comme le reste des données de l'application :
 * l'implémentation actuelle est locale, un passage au distant se fera par une nouvelle
 * implémentation sans toucher aux écrans.
 */
interface PlanningRepository {

    /** Texte du planning pour cette date. Chaîne vide s'il n'y a rien. */
    fun entryFor(date: LocalDate): Flow<String>

    /** Enregistre le planning. Un texte vide efface l'entrée plutôt que de stocker du vide. */
    suspend fun save(date: LocalDate, text: String)
}
