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

    /** Remplace toute la liste : le fichier importé fait référence. Le journal reste. */
    suspend fun replacePhrases(phrases: List<Phrase>)

    /** Inscrit la phrase d'une journée, si elle ne l'est pas déjà. */
    suspend fun record(date: LocalDate, phrase: Phrase)
}
