package com.astro.app.core.config

/**
 * Identifiant stable d'une fonctionnalité de l'application.
 *
 * ATTENTION : le nom de chaque constante sert de clé de persistance dans les
 * préférences utilisateur. Renommer une constante existante ferait perdre à
 * l'utilisateur son réglage pour cette fonctionnalité. Ajouter, oui. Renommer, non.
 */
enum class FeatureId {
    HOME,
    ASTROLOGY,
    PHRASES,
    CALENDAR,
    SETTINGS,
    MANIFESTATION,
    PLANNING,
}
