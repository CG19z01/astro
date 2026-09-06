package com.astro.app.core.config

/**
 * Composition de la page d'accueil.
 *
 * La maquette `maquette astro base.png` fixe **où** se trouvent les éléments ; leur
 * apparence relève du design system. Ce fichier ne contient donc que des positions.
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
     * Bille-journée principale : la période courante. Déborde à gauche et en bas,
     * comme le grand cercle de la maquette qui porte le planning du jour.
     */
    val primaryBead = CircleSpec(centerX = 0.34f, centerY = 0.78f, radius = 0.44f)

    /**
     * Bille-journée secondaire : la période suivante, coupée par le bord droit.
     * Reprend le cercle partiellement visible à droite sur la maquette.
     */
    val secondaryBead = CircleSpec(centerX = 0.92f, centerY = 0.60f, radius = 0.24f)

    /**
     * Ancrage du contenu à l'intérieur d'une bille, en fraction de son diamètre.
     *
     * Une bille déborde de l'écran : son centre géométrique n'est donc pas au centre
     * de ce que l'utilisateur voit. Ces valeurs replacent le texte dans la partie
     * réellement visible.
     */
    const val PRIMARY_CONTENT_X = 0.17f
    const val PRIMARY_CONTENT_Y = 0.13f
    const val SECONDARY_CONTENT_X = 0.08f
    const val SECONDARY_CONTENT_Y = 0.40f

    /** La Lune, en haut à gauche : accès à l'Astrologie. */
    val moon = CircleSpec(centerX = 0.17f, centerY = 0.115f, radius = 0.115f)

    /** Le Soleil, à droite : accès aux Paramètres. */
    val sun = CircleSpec(centerX = 0.80f, centerY = 0.395f, radius = 0.125f)

    /** Le dodécaèdre, à gauche : accès à la Manifestation. */
    val dodecahedron = CircleSpec(centerX = 0.25f, centerY = 0.405f, radius = 0.155f)

    /** Hauteur du centre de la capsule « phrase du jour ». Elle occupe la largeur utile. */
    const val PHRASE_CENTER_Y = 0.215f

    /** Ligne « heure du réveil », en bas. */
    const val WAKE_TIME_Y = 0.90f
}
