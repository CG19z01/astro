package com.astro.app.core.designsystem

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.tween

/**
 * Principes d'animation communs.
 *
 * Trois règles tenues partout :
 *  1. rien ne bouge vite — les durées sont longues et les courbes douces ;
 *  2. le mouvement d'ambiance est lent au point d'être à peine perceptible ;
 *  3. une réaction à un geste est immédiate, elle, sinon l'interface paraît molle.
 *
 * Les durées sont centralisées : ralentir toute l'application se fait ici.
 */
object AstroMotion {

    /** Réaction directe à un appui. Doit rester sous le seuil de perception. */
    const val INSTANT_MILLIS = 160

    /** Changement d'état visible : bascule, apparition d'un élément. */
    const val QUICK_MILLIS = 320

    /** Transition entre deux écrans, ou bascule d'ambiance. */
    const val TRANSITION_MILLIS = 620

    /** Apparition progressive du contenu à l'entrée d'un écran. */
    const val REVEAL_MILLIS = 900

    /** Rotation du solide 3D autour de l'axe vertical. Volontairement très lente. */
    const val SOLID_ROTATION_MILLIS = 26_000

    /**
     * Basculement du solide autour de l'axe horizontal.
     *
     * Volontairement non multiple de [SOLID_ROTATION_MILLIS] : les deux rotations ne se
     * resynchronisent qu'au bout de plusieurs minutes, donc le mouvement ne paraît
     * jamais se répéter et toutes les faces finissent par se présenter.
     */
    const val SOLID_TUMBLE_MILLIS = 41_000

    /**
     * Période de l'axe de roulis, engagé uniquement pendant l'accélération.
     *
     * Au repos cet axe est à l'arrêt : c'est son entrée en jeu qui fait « changer
     * d'axe » le solide quand on reste appuyé dessus.
     */
    const val SOLID_ROLL_MILLIS = 4_000

    /** Facteur de vitesse pendant l'appui maintenu sur le solide. */
    const val SOLID_BOOST_FACTOR = 20f

    /** Durée pendant laquelle l'accélération se prolonge après le relâchement. */
    const val SOLID_BOOST_TAIL_MILLIS = 3_000L

    /**
     * Montée et descente en vitesse. Sans cette rampe, le passage de 1× à 20× serait
     * un à-coup, en contradiction avec le reste des animations.
     */
    const val SOLID_BOOST_RAMP_MILLIS = 380

    /** Respiration des halos de fond. Doit être imperceptible à l'œil qui lit. */
    const val AMBIENT_BREATH_MILLIS = 14_000

    /** Décalage entre deux éléments qui apparaissent en cascade. */
    const val STAGGER_MILLIS = 90

    /** Courbe d'entrée : démarre franchement, s'installe en douceur. */
    val Emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)

    /** Courbe symétrique, pour ce qui va et vient sans début ni fin marqués. */
    val Gentle: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)

    fun <T> transition(): FiniteAnimationSpec<T> =
        tween(durationMillis = TRANSITION_MILLIS, easing = Emphasized)

    fun <T> quick(): FiniteAnimationSpec<T> =
        tween(durationMillis = QUICK_MILLIS, easing = Gentle)
}
