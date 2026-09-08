package com.astro.app.core.time

import java.time.LocalDate
import java.time.temporal.TemporalAdjusters

/**
 * Position dans le temps : une temporalité et la période sur laquelle on est posé.
 *
 * [anchor] est toujours normalisée sur le DÉBUT de la période courante — premier jour
 * de la semaine, du mois ou de l'année selon [scale]. Sans cette normalisation,
 * « mois précédent » depuis le 31 mars donnerait le 28 février, et l'aller-retour ne
 * ramènerait pas au point de départ.
 *
 * Deux mouvements distincts, à ne pas confondre :
 *  - [previous] / [next] déplacent le curseur À L'INTÉRIEUR d'une temporalité ;
 *  - [withScale] change de temporalité.
 *
 * Objet pur, sans dépendance à Android : testable en test unitaire JVM.
 */
data class TimeCursor(
    val scale: TimeScale,
    val anchor: LocalDate,
) {
    /** Période précédente, de la durée de la temporalité courante. */
    fun previous(): TimeCursor = shifted(-1)

    /** Période suivante, de la durée de la temporalité courante. */
    fun next(): TimeCursor = shifted(1)

    /** Change de temporalité en restant sur la période qui contient la date courante. */
    fun withScale(newScale: TimeScale): TimeCursor = of(newScale, anchor)

    /** Curseur décalé de [steps] périodes, dans un sens ou dans l'autre. */
    fun shifted(steps: Long): TimeCursor {
        val moved = when (scale) {
            TimeScale.DAY -> anchor.plusDays(steps)
            TimeScale.WEEK -> anchor.plusWeeks(steps)
            TimeScale.MONTH -> anchor.plusMonths(steps)
            TimeScale.YEAR -> anchor.plusYears(steps)
        }
        return of(scale, moved)
    }

    companion object {
        /**
         * Construit un curseur en normalisant la date sur le début de sa période.
         *
         * Le début de semaine est le lundi, conformément à la norme ISO 8601 et à
         * l'usage en France. Une localisation où la semaine commence le dimanche
         * demanderait de passer la locale ici.
         */
        fun of(scale: TimeScale, date: LocalDate): TimeCursor {
            val normalized = when (scale) {
                TimeScale.DAY -> date
                TimeScale.WEEK -> date.with(TemporalAdjusters.previousOrSame(java.time.DayOfWeek.MONDAY))
                TimeScale.MONTH -> date.withDayOfMonth(1)
                TimeScale.YEAR -> date.withDayOfYear(1)
            }
            return TimeCursor(scale, normalized)
        }
    }
}
