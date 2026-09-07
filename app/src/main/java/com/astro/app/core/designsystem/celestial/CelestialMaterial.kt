package com.astro.app.core.designsystem.celestial

import androidx.compose.ui.graphics.Color

/**
 * Couleurs propres à la matière des astres.
 *
 * Elles sont **indépendantes de l'ambiance**, et c'est volontaire : la Lune est de la
 * roche grise, elle ne devient pas crème parce que la page est claire. Dériver sa
 * matière de la couleur du ciel effaçait son relief sur l'ambiance claire, l'ombre
 * devenant aussi pâle que la lumière.
 *
 * Seuls les halos restent teintés par la palette : ce sont eux qui rattachent l'astre
 * à l'ambiance de la page.
 *
 * Centralisées ici, comme toutes les couleurs du projet — aucune n'est écrite dans un
 * composant.
 */
object CelestialMaterial {

    /** Régolithe éclairé. */
    val MoonLit = Color(0xFFEFF1F8)

    /** Régolithe dans l'ombre : bleu très sombre, jamais noir. */
    val MoonShade = Color(0xFF262B42)

    /** Ombre interne, qui creuse le limbe opposé à la lumière. */
    val MoonShadow = Color(0xFF12141F)

    /** Grain de surface. */
    val MoonGrain = Color(0xFF0E1018)

    /** Cœur du Soleil, au point le plus chaud. */
    val SunCore = Color(0xFFFFF3D4)

    /** Photosphère. */
    val SunSurface = Color(0xFFE9B764)

    /** Bord du disque, plus froid. */
    val SunLimb = Color(0xFFB2712C)
}
