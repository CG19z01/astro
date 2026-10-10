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
     * Descendue de 0,115 à 0,13, puis à 0,1421 (+25 px) le 2026-10-10, à la demande de
     * l'utilisateur. Les décalages demandés en pixels sont convertis sur la hauteur
     * mesurée de la zone (2070 px sur l'émulateur Pixel 8).
     */
    val moon = CircleSpec(centerX = 0.17f, centerY = 0.1421f, radius = 0.115f)

    /** Le Soleil, à droite : accès aux Paramètres. Descendu de 30 px le 2026-10-10. */
    val sun = CircleSpec(centerX = 0.80f, centerY = 0.4095f, radius = 0.125f)

    /**
     * Le dodécaèdre, à gauche : accès à la Manifestation.
     * Descendu de 0,405 à 0,425, puis à 0,4443 (+40 px) le 2026-10-10, à la demande de
     * l'utilisateur.
     */
    val dodecahedron = CircleSpec(centerX = 0.25f, centerY = 0.4443f, radius = 0.155f)

    /**
     * Hauteur du centre de la capsule « phrase du jour ». Elle occupe la largeur utile.
     * Descendue de 20 px le 2026-10-10.
     */
    const val PHRASE_CENTER_Y = 0.2247f
}
