package com.astro.app.core.config

/** Réglages des phrases du jour. */
object PhrasesConfig {
    /**
     * Taille maximale d'un fichier importé. Un fichier de phrases écrit à la main pèse
     * quelques dizaines de kilo-octets ; au-delà d'un mégaoctet, c'est presque
     * sûrement un autre fichier, choisi par erreur.
     */
    const val MAX_FILE_BYTES: Int = 1_000_000

    /**
     * Plus petite taille de la phrase sur l'accueil, en sp. Une phrase plus longue que
     * ce que permet cette taille finit par « … » ; elle reste entière dans l'historique.
     */
    const val MIN_CAPSULE_FONT_SP: Float = 14f

    /** Interligne de la phrase, proportionnel à sa taille puisque celle-ci varie. */
    const val CAPSULE_LINE_HEIGHT_EM: Float = 1.25f
}
