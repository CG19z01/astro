package com.astro.app.core.time

import com.astro.app.core.config.TimeNavigationConfig

/**
 * Détecte une salve de swipes rapides — le geste qui CHANGE de temporalité.
 *
 * Règle appliquée : [swipesRequired] swipes dans le même sens, le premier et le
 * dernier séparés d'au plus [windowMillis] millisecondes.
 *
 * À ne pas confondre avec le swipe simple de la Home, qui déplace le curseur à
 * l'intérieur de la temporalité courante et ne passe pas par ce détecteur.
 *
 * Détails de comportement, volontairement explicites :
 * - un swipe dans le sens opposé annule la salve en cours et en démarre une nouvelle ;
 * - un déclenchement remet le compteur à zéro, donc six swipes rapides valent
 *   deux changements et non un seul ;
 * - les swipes trop anciens sortent de la fenêtre au fil de l'eau, donc une salve
 *   trop lente ne déclenche jamais rien mais ne bloque pas la suivante.
 *
 * Classe sans dépendance à Android ni à l'horloge : l'instant est fourni par
 * l'appelant, ce qui rend la règle testable en test unitaire JVM.
 */
class SwipeBurstDetector(
    private val swipesRequired: Int = TimeNavigationConfig.SCALE_CHANGE_SWIPES,
    private val windowMillis: Long = TimeNavigationConfig.SCALE_CHANGE_WINDOW_MILLIS,
) {
    private val timestamps = ArrayDeque<Long>()
    private var currentDirection: SwipeDirection? = null

    /** Enregistre un swipe. Retourne `true` si la salve déclenche un changement. */
    fun onSwipe(direction: SwipeDirection, nowMillis: Long): Boolean {
        if (direction != currentDirection) {
            reset()
            currentDirection = direction
        }
        timestamps.addLast(nowMillis)
        dropExpired(nowMillis)

        if (timestamps.size < swipesRequired) return false
        reset()
        return true
    }

    /** Vide la salve en cours. À appeler quand l'écran reprend la main. */
    fun reset() {
        timestamps.clear()
        currentDirection = null
    }

    private fun dropExpired(nowMillis: Long) {
        while (timestamps.isNotEmpty() && nowMillis - timestamps.first() > windowMillis) {
            timestamps.removeFirst()
        }
    }
}
