package com.astro.app.core.config

/**
 * Placement des quatre planètes-journées de la page d'accueil.
 *
 * Les positions sont **relevées sur la maquette `planetesupp`** : chacun des quatre
 * cercles dessinés y a été ajusté au moindre carré, avec un écart maximal de six pixels
 * sur un tracé à main levée. Les valeurs ci-dessous en sont la transcription directe.
 *
 * Les quatre cercles n'appartiennent pas à une ellipse commune et n'ont pas le même
 * rayon : une roue paramétrée par des angles ne pouvait donc pas les exprimer. Chaque
 * planète porte sa propre géométrie.
 */
object DayWheelConfig {

    /**
     * Une planète : la journée qu'elle porte, son cercle, et l'ancrage de son contenu.
     *
     * `centerX` et `radius` sont des fractions de la **largeur** de l'écran, `centerY`
     * une fraction de la **hauteur** — la convention déjà employée par
     * [HomeLayoutConfig]. Une valeur hors de [0, 1] place volontairement la planète
     * partiellement hors écran, comme sur la maquette.
     *
     * `contentX` et `contentY` sont des fractions du **diamètre** de la planète. Une
     * planète débordant de l'écran, son centre géométrique n'est pas au centre de ce
     * que l'utilisateur voit : ces deux valeurs replacent le texte dans la partie
     * visible, et à l'intérieur du disque, qui découpe son contenu.
     */
    data class PlanetSpec(
        val offset: Int,
        val centerX: Float,
        val centerY: Float,
        val radius: Float,
        val contentX: Float,
        val contentY: Float,
    )

    /**
     * Les quatre planètes affichées, dans l'ordre des journées.
     *
     * - `offset = 0` : le jour principal, le grand cercle en bas à gauche. C'est le seul
     *   assez large pour porter le jour, sa date et le planning entier.
     * - `offset = 1` : le jour suivant, coupé par le bord droit.
     * - `offset = 3` : J+3, la planète en haut à droite.
     * - `offset = 4` : J+4, la planète coupée par le haut.
     *
     * Les deux dernières sont les deux cercles marqués en vert sur la maquette.
     *
     */
    val PLANETS = listOf(
        PlanetSpec(offset = 0, centerX = 0.251f, centerY = 0.957f, radius = 0.505f, contentX = 0.30f, contentY = 0.09f),
        PlanetSpec(offset = 1, centerX = 1.021f, centerY = 0.675f, radius = 0.293f, contentX = 0.10f, contentY = 0.42f),
        PlanetSpec(offset = 3, centerX = 0.930f, centerY = 0.079f, radius = 0.131f, contentX = 0.10f, contentY = 0.42f),
        PlanetSpec(offset = 4, centerX = 0.467f, centerY = 0.010f, radius = 0.143f, contentX = 0.28f, contentY = 0.48f),
    )

    /** Nombre de journées dont le planning doit être chargé, indices compris. */
    val DAY_COUNT: Int get() = PLANETS.maxOf { it.offset } + 1
}
