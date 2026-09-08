package com.astro.app.core.config

/**
 * Roue des sept journées.
 *
 * Les sept planètes sont posées sur une **ellipse** et non sur un cercle : un cercle
 * parfait dans un écran en portrait sortirait du cadre sur les côtés ou resterait
 * tassé en hauteur. Les rayons sont donc distincts, l'un en fraction de largeur,
 * l'autre en fraction de hauteur, conformément à la convention du projet.
 *
 * **Les angles ne sont pas régulièrement espacés, et c'est voulu.** Sept planètes
 * réparties uniformément donneraient un pas de 360/7 ≈ 51°, alors que les deux
 * positions imposées — J+3 en haut à droite, J+4 à gauche — sont distantes d'environ
 * 135°. Comme les jours se suivent sur la roue, ces deux planètes sont voisines : leur
 * écart est donc fixé à 135°, et les six autres intervalles se partagent les 225°
 * restants, soit 37,5° chacun.
 *
 * Angles en degrés, convention mathématique : 0° à droite, 90° en haut.
 */
object DayWheelConfig {

    /** Centre de la roue, en fractions d'écran. */
    const val CENTER_X = 0.50f
    const val CENTER_Y = 0.50f

    /** Demi-axes de l'ellipse : largeur pour l'un, hauteur pour l'autre. */
    const val RADIUS_X = 0.42f
    const val RADIUS_Y = 0.44f

    /**
     * Position angulaire de chaque journée, de la période courante à la sixième suivante.
     * L'indice 3 est en haut à droite, l'indice 4 à gauche : les deux positions imposées.
     *
     * Les deux derniers angles ont été remontés : au bas du cercle, ces planètes
     * passaient derrière la planète principale, bien plus grande, et leur jour n'était
     * plus lisible.
     */
    val ANGLES_DEGREES = listOf(-57.5f, -20f, 17.5f, 55f, 190f, 212f, 240f)

    /** Rayon de la planète du jour courant. Plus grande : elle porte le planning entier. */
    const val MAIN_RADIUS = 0.34f

    /** Rayon des six autres planètes. */
    const val PLANET_RADIUS = 0.145f

    /**
     * Ancrage du contenu dans une planète, en fraction de son diamètre.
     *
     * Une planète peut déborder de l'écran : son centre géométrique n'est pas au centre
     * de ce que l'utilisateur voit. Ces valeurs replacent le texte dans la partie visible.
     */
    const val MAIN_CONTENT_X = 0.14f
    const val MAIN_CONTENT_Y = 0.20f
    // Marge suffisante pour que le jour de la planète de gauche, largement hors
    // écran, ne soit pas rogné par le bord.
    const val PLANET_CONTENT_X = 0.26f
    const val PLANET_CONTENT_Y = 0.38f

    /** Nombre de journées portées par la roue. */
    val DAY_COUNT: Int get() = ANGLES_DEGREES.size
}
