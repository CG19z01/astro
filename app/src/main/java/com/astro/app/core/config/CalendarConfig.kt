package com.astro.app.core.config

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.astro.app.core.time.TimeScale

/**
 * Réglages de la page Calendrier.
 *
 * Les gestes (seuil de swipe, salve de trois) restent dans [TimeNavigationConfig] :
 * ils sont communs à la Home et au Calendrier. Ne figurent ici que les valeurs propres
 * à cette page.
 */
object CalendarConfig {

    /**
     * Vue affichée à l'ouverture tant que l'utilisateur n'a rien choisi.
     * Le Mois est la vue qui montre d'un coup d'œil les pleines lunes et les jours
     * renseignés ; c'est la valeur demandée dans le cahier des charges.
     */
    val DEFAULT_INITIAL_SCALE: TimeScale = TimeScale.MONTH

    /** Les pleines lunes et les bulles sont visibles tant que rien n'a été réglé. */
    const val DEFAULT_SHOW_FULL_MOONS: Boolean = true
    const val DEFAULT_SHOW_PLANNING_MARKERS: Boolean = true

    /** Hauteur d'une case de la vue Mois : la cible tactile minimale recommandée. */
    val MONTH_CELL_HEIGHT: Dp = 48.dp

    /** Anneau qui entoure le jour sélectionné. Laisse sous lui la place de la bulle. */
    val SELECTION_RING_SIZE: Dp = 32.dp

    /** Bulle d'un jour renseigné. Petite : elle signale, elle ne décore pas. */
    val PLANNING_BUBBLE_SIZE: Dp = 6.dp

    /** Disque de pleine lune dans une case de la vue Mois ou une ligne de la vue Semaine. */
    val FULL_MOON_MARK_SIZE: Dp = 10.dp

    /** Disque de pleine lune dans la vue Jour, où la place ne manque pas. */
    val FULL_MOON_DETAIL_SIZE: Dp = 18.dp

    /** Hauteur d'une journée dans la vue Semaine. */
    val WEEK_ROW_HEIGHT: Dp = 58.dp

    /** Lignes de planning montrées par journée dans la vue Semaine. */
    const val WEEK_PREVIEW_LINES: Int = 2

    /**
     * Mini-mois par rangée dans la vue Année. Trois tenaient sur un écran, mais leurs
     * nombres à deux chiffres se touchaient (« 22232425 ») : constaté sur émulateur le
     * 2026-10-09. Deux rangées défilent, et chaque chiffre reste lisible.
     */
    const val YEAR_COLUMNS: Int = 2

    /** Taille d'une case de jour dans un mini-mois. */
    val YEAR_CELL_SIZE: Dp = 20.dp

    /** Opacité du disque lunaire posé derrière un jour de pleine lune, vue Année. */
    const val YEAR_FULL_MOON_ALPHA: Float = 0.32f
}
