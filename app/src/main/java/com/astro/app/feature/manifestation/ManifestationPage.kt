package com.astro.app.feature.manifestation

import com.astro.app.core.manifestation.Manifestation

/**
 * Ce que montre la page Manifestation. Les trois vues vivent dans la même destination :
 * Retour ramène d'abord à la liste, puis à l'accueil comme partout ailleurs.
 */
sealed interface ManifestationPage {
    data object List : ManifestationPage

    /** Lecture d'une manifestation enregistrée. */
    data class Read(val id: Long) : ManifestationPage

    /** Création (id 0) ou modification. [draft] est le texte en cours de saisie. */
    data class Edit(val draft: Manifestation) : ManifestationPage
}
