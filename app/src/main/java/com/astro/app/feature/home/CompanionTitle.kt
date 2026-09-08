package com.astro.app.feature.home

import com.astro.app.core.config.HomeLayoutConfig

/**
 * Titre affiché dans une planète d'accompagnement.
 *
 * Leur surface visible est réduite : seule la **première ligne non vide** du planning
 * est retenue, et tronquée à la longueur configurée. Un planning sur plusieurs lignes
 * n'a pas de titre au sens strict, sa première ligne en tient lieu.
 *
 * Aucune ellipse n'est ajoutée : le titre est assumé comme un fragment, pas présenté
 * comme un texte coupé.
 *
 * Fonction pure, testable sans Android.
 */
object CompanionTitle {

    fun of(planning: String): String = planning
        .lineSequence()
        .firstOrNull { it.isNotBlank() }
        ?.trim()
        ?.take(HomeLayoutConfig.COMPANION_TITLE_LENGTH)
        .orEmpty()
}
