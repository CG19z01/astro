package com.astro.app.core.manifestation

import java.util.Locale

/**
 * Filtre par sujet. Fonction pure, testée.
 *
 * Le sujet est un texte libre : « Amour » et « amour » sont le même sujet, quelle que
 * soit la casse ou les espaces autour. L'écriture retenue pour l'afficher est celle de
 * la manifestation la plus récente.
 */
object SubjectFilter {

    /** Sujets distincts, dans l'ordre alphabétique. Les sujets vides n'en sont pas. */
    fun subjects(all: List<Manifestation>): List<String> =
        all.map { it.subject.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy { key(it) }
            .sortedBy { key(it) }

    /** [subject] nul : toutes. */
    fun filter(all: List<Manifestation>, subject: String?): List<Manifestation> {
        if (subject == null) return all
        val wanted = key(subject)
        return all.filter { key(it.subject) == wanted }
    }

    /** Locale.ROOT : la comparaison ne dépend pas de la langue du téléphone. */
    private fun key(subject: String) = subject.trim().lowercase(Locale.ROOT)
}
