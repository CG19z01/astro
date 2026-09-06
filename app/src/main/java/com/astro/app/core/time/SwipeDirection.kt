package com.astro.app.core.time

/** Sens d'un swipe horizontal, exprimé en intention plutôt qu'en pixels. */
enum class SwipeDirection {
    /** Vers l'avant : période suivante, ou temporalité suivante. */
    FORWARD,

    /** Vers l'arrière : période précédente, ou temporalité précédente. */
    BACKWARD,
}
