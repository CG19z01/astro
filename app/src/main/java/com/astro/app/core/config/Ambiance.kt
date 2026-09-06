package com.astro.app.core.config

/**
 * Ambiance visuelle d'un écran.
 *
 * L'application assume deux univers distincts. Le choix n'est pas laissé à chaque
 * écran : il est déclaré dans le catalogue des fonctionnalités, ce qui garantit
 * qu'un écran ne peut pas dériver de son ambiance sans que cela se voie au même
 * endroit que le reste de sa définition.
 */
enum class Ambiance {
    /** Nuit profonde : Home, Calendrier, Paramètres, et tout ce qui touche au Soleil et à la Lune. */
    DARK,

    /** Aube céleste : Astrologie, Historique des phrases, Manifestation. */
    LIGHT,
}
