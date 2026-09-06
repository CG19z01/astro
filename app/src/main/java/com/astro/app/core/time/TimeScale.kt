package com.astro.app.core.time

import androidx.annotation.StringRes
import com.astro.app.R

/**
 * Niveaux de temporalité de l'application.
 *
 * Placé dans `core` et non dans le Calendrier : la Home s'en sert aussi pour savoir
 * de quelle durée un swipe fait reculer. L'ordre de déclaration est significatif,
 * il définit la séquence Jour → Semaine → Mois → Année.
 */
enum class TimeScale(@param:StringRes val labelRes: Int) {
    DAY(R.string.calendar_scale_day),
    WEEK(R.string.calendar_scale_week),
    MONTH(R.string.calendar_scale_month),
    YEAR(R.string.calendar_scale_year),
    ;

    /** Niveau suivant. Reste inchangé sur le dernier : pas de bouclage. */
    fun next(): TimeScale = entries.getOrElse(ordinal + 1) { this }

    /** Niveau précédent. Reste inchangé sur le premier : pas de bouclage. */
    fun previous(): TimeScale = entries.getOrElse(ordinal - 1) { this }

    companion object {
        /** Niveau au démarrage, et après chaque sortie de la page Calendrier. */
        val INITIAL: TimeScale = DAY
    }
}
