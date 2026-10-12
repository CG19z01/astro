package com.astro.app.core.phrases

import java.time.LocalDate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

/**
 * Phrase du jour, inscrite au journal la première fois qu'elle est demandée.
 *
 * Seule la journée en cours est inscrite : on ne remplit jamais le journal d'avance,
 * sans quoi l'historique montrerait des phrases que l'utilisateur n'a pas vues.
 */
class DailyPhrase(
    private val repository: PhraseRepository,
    private val today: () -> LocalDate = LocalDate::now,
) {
    fun ofToday(): Flow<Phrase?> = combine(repository.phrases, repository.journal) { phrases, journal ->
        val date = today()
        val phrase = DailyPhrasePicker.pick(date, phrases, journal)
        if (phrase != null && journal[date] == null) repository.record(date, phrase)
        phrase
    }
}
