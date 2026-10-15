package com.astro.app.core.phrases

import java.util.Locale

/** Ce que l'utilisateur déclare, pour recevoir des phrases accordées à lui. */
enum class ReaderGender { MASCULINE, FEMININE, OTHER }

/**
 * Phrases qui conviennent à l'utilisateur. Fonction pure, testée.
 *
 * - **Genre** : les phrases neutres conviennent à tous ; « masculin » à qui se déclare
 *   masculin, « féminin » à qui se déclare féminine. « Autre », ou rien de renseigné :
 *   les neutres seulement — une phrase accordée au mauvais genre ne s'adresse pas à lui.
 * - **Thèmes** : aucun choisi, tous ; sinon ceux-là seulement (casse et espaces ignorés).
 *   Une phrase sans thème n'apparaît qu'avec « tous les thèmes ».
 */
object PhraseAudience {

    fun eligible(phrases: List<Phrase>, reader: ReaderGender?, themes: Set<String>): List<Phrase> {
        val wanted = themes.map(::key).toSet()
        return phrases.filter { phrase ->
            suits(phrase.gender, reader) && (wanted.isEmpty() || key(phrase.theme) in wanted)
        }
    }

    /** Thèmes distincts présents dans la liste, dans l'ordre alphabétique. */
    fun themes(phrases: List<Phrase>): List<String> =
        phrases.map { it.theme.trim() }.filter { it.isNotEmpty() }.distinctBy(::key).sortedBy(::key)

    private fun suits(gender: PhraseGender, reader: ReaderGender?): Boolean = when (gender) {
        PhraseGender.NEUTRAL -> true
        PhraseGender.MASCULINE -> reader == ReaderGender.MASCULINE
        PhraseGender.FEMININE -> reader == ReaderGender.FEMININE
    }

    private fun key(theme: String) = theme.trim().lowercase(Locale.ROOT)
}
