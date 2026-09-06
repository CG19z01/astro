package com.astro.app.core.config

/**
 * Réglages de la navigation temporelle, communs à la Home et au Calendrier.
 *
 * Deux gestes distincts, volontairement séparés :
 *  - **un swipe simple** déplace le curseur DANS la temporalité courante (Home) ;
 *  - **une salve de swipes rapides** CHANGE de temporalité (Calendrier).
 *
 * Le seuil de distance est commun aux deux : un même geste doit être reconnu de la
 * même façon partout.
 */
object TimeNavigationConfig {

    /** Distance horizontale minimale, en dp, pour qu'un geste compte comme un swipe. */
    const val SWIPE_MIN_DISTANCE_DP: Int = 48

    /** Nombre de swipes rapides nécessaires pour changer de temporalité. */
    const val SCALE_CHANGE_SWIPES: Int = 3

    /** Durée maximale, en millisecondes, entre le premier et le dernier swipe de la salve. */
    const val SCALE_CHANGE_WINDOW_MILLIS: Long = 1_500L
}
