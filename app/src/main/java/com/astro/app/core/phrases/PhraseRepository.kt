package com.astro.app.core.phrases

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow

/**
 * Phrases de l'utilisateur et journal des phrases montrées.
 *
 * Les deux vivent sur le téléphone uniquement : rien n'est envoyé ni versionné.
 */
interface PhraseRepository {
    /** La liste importée, dans l'ordre du fichier. Vide tant que rien n'est importé. */
    val phrases: Flow<List<Phrase>>

    /** La phrase montrée chaque jour, depuis le premier import. */
    val journal: Flow<Map<LocalDate, Phrase>>

    /**
     * Phrases mises en favori, la plus récente en premier. Gardées entières : un nouvel
     * import qui retire une phrase de la liste ne la retire pas des favoris.
     */
    val favorites: Flow<List<Phrase>>

    /** Ajoute la phrase aux favoris, ou l'en retire si elle y est (même texte). */
    suspend fun toggleFavorite(phrase: Phrase)

    /** Remplace toute la liste : le fichier importé fait référence. Le journal reste. */
    suspend fun replacePhrases(phrases: List<Phrase>)

    /**
     * Inscrit la phrase d'une journée, si elle ne l'est pas déjà — ou la remplace si
     * [replace] : seule la journée en cours l'est, quand ses préférences ont changé.
     */
    suspend fun record(date: LocalDate, phrase: Phrase, replace: Boolean = false)
}
