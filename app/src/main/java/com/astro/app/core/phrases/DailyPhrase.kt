package com.astro.app.core.phrases

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf

/** Pour qui choisir : genre déclaré et thèmes voulus. */
data class PhraseWishes(val reader: ReaderGender? = null, val themes: Set<String> = emptySet())

/**
 * Phrase du jour, choisie parmi celles qui conviennent à l'utilisateur
 * ([PhraseAudience]), et inscrite au journal la première fois qu'elle est demandée.
 *
 * Seule la journée en cours est inscrite : on ne remplit jamais le journal d'avance.
 * Si l'utilisateur change ses préférences et que la phrase du jour ne lui convient
 * plus, elle est remplacée — aujourd'hui seulement ; les jours passés ne changent pas.
 */
class DailyPhrase(
    private val repository: PhraseRepository,
    private val wishes: Flow<PhraseWishes> = flowOf(PhraseWishes()),
    private val today: () -> LocalDate = LocalDate::now,
) {
    fun ofToday(): Flow<Phrase?> = combine(repository.phrases, repository.journal, wishes) { phrases, journal, wish ->
        val date = today()
        val eligible = PhraseAudience.eligible(phrases, wish.reader, wish.themes)
        val recorded = journal[date]
        // Ne convient plus au genre ou aux thèmes choisis. Une phrase seulement retirée de la
        // liste par un nouvel import, elle, reste : l'import ne réécrit pas la journée.
        val stale = recorded != null && eligible.isNotEmpty() &&
            PhraseAudience.eligible(listOf(recorded), wish.reader, wish.themes).isEmpty()
        val past = if (stale) journal - date else journal
        val phrase = DailyPhrasePicker.pick(date, eligible, past)
        if (phrase != null && (recorded == null || stale)) repository.record(date, phrase, replace = stale)
        phrase
    }
}
