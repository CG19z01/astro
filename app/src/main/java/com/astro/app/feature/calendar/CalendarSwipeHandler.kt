package com.astro.app.feature.calendar

import com.astro.app.core.config.TimeNavigationConfig
import com.astro.app.core.time.SwipeBurstDetector
import com.astro.app.core.time.SwipeDirection

/**
 * Les deux gestes horizontaux du Calendrier, réunis sans se gêner.
 *
 *  - **un swipe simple** passe à la période voisine, dans la même vue ;
 *  - **une salve de swipes rapides** change de vue (règle de [SwipeBurstDetector]).
 *
 * Difficulté : on ne sait qu'une salve en est une qu'au dernier swipe. Les précédents
 * ont donc déjà déplacé la période. Au déclenchement, on repart de l'état d'AVANT le
 * premier swipe de la salve : changer de vue ne fait pas voyager dans le temps.
 *
 * Pour cela, on garde l'état précédant chacun des derniers swipes. Une salve se compose
 * toujours des derniers swipes consécutifs, dans le même sens : l'état à restaurer est
 * celui d'avant le plus ancien d'entre eux.
 *
 * Classe pure, l'instant étant fourni par l'appelant : testable en test unitaire JVM.
 */
class CalendarSwipeHandler(
    private val burstSize: Int = TimeNavigationConfig.SCALE_CHANGE_SWIPES,
    private val detector: SwipeBurstDetector = SwipeBurstDetector(swipesRequired = burstSize),
) {
    /** États précédant les derniers swipes simples, du plus ancien au plus récent. */
    private val history = ArrayDeque<CalendarState>()

    fun onSwipe(state: CalendarState, direction: SwipeDirection, nowMillis: Long): CalendarState {
        if (detector.onSwipe(direction, nowMillis)) {
            val origin = history.firstOrNull() ?: state
            history.clear()
            val target = when (direction) {
                SwipeDirection.FORWARD -> origin.scale.next()
                SwipeDirection.BACKWARD -> origin.scale.previous()
            }
            return origin.withScale(target)
        }
        history.addLast(state)
        while (history.size > burstSize - 1) history.removeFirst()
        return state.shifted(direction)
    }

    /** Oublie la salve en cours : à appeler quand la vue change par un autre moyen. */
    fun reset() {
        detector.reset()
        history.clear()
    }
}
