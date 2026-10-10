package com.astro.app.core.config

/**
 * Composition de la page d'accueil.
 *
 * La maquette `maquette astro base.png` fixe **où** se trouvent les éléments ; leur
 * apparence relève du design system. Ce fichier ne contient donc que des positions.
 *
 * La position des journées ne vit plus ici mais dans `DayWheelConfig` : elles sont
 * désormais réparties sur une roue et non placées une à une.
 *
 * Toutes les valeurs sont des **fractions de l'écran**, jamais des pixels :
 *  - `centerX` et `radius` sont des fractions de la LARGEUR ;
 *  - `centerY` est une fraction de la HAUTEUR.
 *
 * Une valeur hors de [0, 1] est volontaire : elle place un élément partiellement
 * hors écran, comme le cercle de l'agenda.
 */
object HomeLayoutConfig {

    data class CircleSpec(val centerX: Float, val centerY: Float, val radius: Float)

    /**
     * La Lune, en haut à gauche : accès à l'Astrologie.
     * Descendue de 0,115 à 0,13 le 2026-10-10, à la demande de l'utilisateur.
     */
    val moon = CircleSpec(centerX = 0.17f, centerY = 0.13f, radius = 0.115f)

    /** Le Soleil, à droite : accès aux Paramètres. */
    val sun = CircleSpec(centerX = 0.80f, centerY = 0.395f, radius = 0.125f)

    /**
     * Le dodécaèdre, à gauche : accès à la Manifestation.
     * Descendu de 0,405 à 0,425 le 2026-10-10, à la demande de l'utilisateur.
     */
    val dodecahedron = CircleSpec(centerX = 0.25f, centerY = 0.425f, radius = 0.155f)

    /** Hauteur du centre de la capsule « phrase du jour ». Elle occupe la largeur utile. */
    const val PHRASE_CENTER_Y = 0.215f

    /**
     * Ligne « heure du réveil », en bas.
     *
     * Descendue de 0,90 à 0,93 : la planète du jour courant, passée en bas à gauche,
     * y amenait son planning. Les trois lignes de planning passent avant ce bloc, qui
     * n'affiche encore aucune donnée.
     */
    const val WAKE_TIME_Y = 0.93f
}
