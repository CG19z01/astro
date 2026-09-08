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

    /** La Lune, en haut à gauche : accès à l'Astrologie. */
    val moon = CircleSpec(centerX = 0.17f, centerY = 0.115f, radius = 0.115f)

    /** Le Soleil, à droite : accès aux Paramètres. */
    val sun = CircleSpec(centerX = 0.80f, centerY = 0.395f, radius = 0.125f)

    /** Le dodécaèdre, à gauche : accès à la Manifestation. */
    val dodecahedron = CircleSpec(centerX = 0.25f, centerY = 0.405f, radius = 0.155f)

    /** Nombre maximal de caractères du titre affiché dans une planète de la roue. */
    const val COMPANION_TITLE_LENGTH = 5

    /** Hauteur du centre de la capsule « phrase du jour ». Elle occupe la largeur utile. */
    const val PHRASE_CENTER_Y = 0.215f

    /** Ligne « heure du réveil », en bas. */
    const val WAKE_TIME_Y = 0.90f
}
