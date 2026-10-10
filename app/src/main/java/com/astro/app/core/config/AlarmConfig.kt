package com.astro.app.core.config

/** Réglages du réveil affiché sur l'accueil. */
object AlarmConfig {
    /** Affichage du prochain réveil, tant que l'utilisateur n'a rien choisi. */
    const val DEFAULT_SHOW_NEXT_ALARM: Boolean = true

    /**
     * Planète qui porte le réveil, en écart à la journée courante : J+1, à la demande de
     * l'utilisateur (2026-10-10). Le réveil n'est plus écrit sous la planète du jour.
     */
    const val PLANET_OFFSET: Int = 1

    /**
     * Position de l'heure du réveil dans sa planète, en fractions du diamètre, depuis
     * le coin de la planète. Indépendante du jour : près du haut et vers le centre de
     * la planète (demandes du 2026-10-10), vérifiée sur capture dans la corde du disque.
     */
    const val LABEL_X: Float = 0.332f
    const val LABEL_Y: Float = 0.108f
}
