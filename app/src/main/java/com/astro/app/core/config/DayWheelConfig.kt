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
     *
     * `messageLines` est la hauteur, en lignes, de la zone où défilent les messages.
     * `messagesX` / `messagesY` (fractions du diamètre) placent cette zone à part du
     * jour et de la date ; nuls, elle suit le jour, juste dessous.
     */
    data class PlanetSpec(
        val offset: Int,
        val centerX: Float,
        val centerY: Float,
        val radius: Float,
        val contentX: Float,
        val contentY: Float,
        val messageLines: Int,
        val messagesX: Float? = null,
        val messagesY: Float? = null,
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
     * `centerY` est une fraction de la hauteur **disponible pour la roue**, qui exclut
     * la barre d'état et la barre système : elle ne se confond pas avec la fraction
     * relevée sur la maquette. Les valeurs retenues sont celles qui reproduisent à
     * l'écran les centres mesurés, vérifiées par relevé des cercles sur une capture.
     *
     * **Écarts assumés avec la maquette.** Les trois planètes secondaires portent la
     * date complète — « 08 septembre 2026 », soit environ 310 px, le plus long de leurs
     * textes — et un extrait du planning. Aux tailles et positions exactement relevées,
     * elles ne laissent voir qu'un croissant de 126 à 293 px de large : la date n'y tient
     * pas, et pour deux d'entre elles ce texte est plus large que la planète entière.
     *
     * Elles ont donc été **agrandies et ramenées vers l'intérieur** jusqu'à ce que les
     * trois lignes tiennent. Le rayon des deux plus petites passe de 0,131 et 0,143 à
     * 0,205 ; elles restent plus petites que celle du jour suivant, comme sur la
     * maquette. Ce qui est conservé : quelle journée occupe quelle place, l'ordre des
     * tailles, et le bord par lequel chaque planète est coupée.
     *
     * J+4, la planète du haut, a été décalée vers la gauche (`centerX` de 0,467 à
     * 0,430) le 2026-10-10, à la demande de l'utilisateur.
     *
     * Le 2026-10-10 encore, le bloc jour/date a été remonté dans J+1 (`contentY` 0,36 →
     * 0,30) et J+3 (0,34 → 0,28), abaissé dans J+4 (0,50 → 0,56), pour laisser de la
     * place aux messages qui défilent. Les planètes elles-mêmes n'ont pas bougé.
     *
     * Correction du même jour, mesurée sur capture : abaissé à 0,56, le bloc de J+4
     * faisait sortir ses deux lignes de messages du disque (texte à x = 288 px pour un
     * bord à 298 puis 357 px). Il remonte à 0,42 et glisse vers le centre (`contentX`
     * 0,13) : chaque ligne reste dans la corde visible. J+3 et J+1 glissent aussi vers
     * le centre (0,13 et 0,12) — la 3e ligne de J+3 n'avait que 8 px de marge.
     *
     * Toujours le 2026-10-10, à la demande de l'utilisateur : le jour de J+1 est centré
     * et remonté, ses messages ont leur propre zone, plus à gauche, là où la partie
     * visible du disque est la plus large — cinq lignes au lieu de quatre. J+4 remonte
     * encore (0,34) pour montrer trois lignes. Positions vérifiées ligne par ligne contre
     * le bord du disque, sur capture. Enfin, J+3 et J+4 descendent de 20 px (`centerY`
     * +0,0097, sur une hauteur mesurée de 2070 px).
     */
    val PLANETS = listOf(
        PlanetSpec(0, centerX = 0.251f, centerY = 0.957f, radius = 0.505f, contentX = 0.30f, contentY = 0.06f, messageLines = 4),
        PlanetSpec(
            1, centerX = 0.910f, centerY = 0.675f, radius = 0.293f, contentX = 0.19f, contentY = 0.21f,
            messageLines = 5, messagesX = 0.07f, messagesY = 0.37f,
        ),
        PlanetSpec(3, centerX = 0.840f, centerY = 0.1047f, radius = 0.205f, contentX = 0.13f, contentY = 0.28f, messageLines = 3),
        PlanetSpec(4, centerX = 0.430f, centerY = 0.0237f, radius = 0.205f, contentX = 0.15f, contentY = 0.34f, messageLines = 3),
    )

    /**
     * Largeur du texte, en fraction du diamètre de la planète.
     *
     * Le disque découpe son contenu : au-delà, les lignes basses sortiraient du cercle.
     * C'est une borne parmi deux — l'autre est le bord droit de l'écran, la planète
     * pouvant en déborder. La plus petite des deux l'emporte.
     */
    const val MAIN_CONTENT_WIDTH = 0.62f
    const val SIDE_CONTENT_WIDTH = 0.70f

    /** Hauteur, en lignes, du fondu qui signale du texte au-delà de la zone visible. */
    const val MESSAGE_FADE_LINES: Float = 0.8f

    /** Nombre de journées dont le planning doit être chargé, indices compris. */
    val DAY_COUNT: Int get() = PLANETS.maxOf { it.offset } + 1
}
