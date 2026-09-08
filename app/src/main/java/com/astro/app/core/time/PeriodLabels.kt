package com.astro.app.core.time

import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

/**
 * Libellés d'une période, dans la langue fournie.
 *
 * Tout vient de la bibliothèque standard : aucun nom de jour ni de mois n'est écrit
 * dans le code. Fonctions pures, testables sans Android.
 */
object PeriodLabels {

    /** Libellé principal : « Dimanche », « Semaine 37 », « Septembre », « 2026 ». */
    fun title(cursor: TimeCursor, locale: Locale): String = when (cursor.scale) {
        TimeScale.DAY -> cursor.anchor.dayOfWeek
            .getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { it.titlecase(locale) }

        TimeScale.WEEK -> cursor.anchor.format(DateTimeFormatter.ofPattern("'S'w", locale))

        TimeScale.MONTH -> cursor.anchor.month
            .getDisplayName(TextStyle.FULL_STANDALONE, locale)
            .replaceFirstChar { it.titlecase(locale) }

        TimeScale.YEAR -> cursor.anchor.year.toString()
    }

    /**
     * Libellé secondaire : la date complète, ou l'étendue de la période.
     *
     * Le jour est écrit sur **deux chiffres** — « 08 septembre 2026 » et non
     * « 8 septembre 2026 » : format demandé, et largeur stable d'un jour à l'autre.
     */
    fun subtitle(cursor: TimeCursor, locale: Locale): String = when (cursor.scale) {
        TimeScale.DAY -> cursor.anchor.format(pattern("dd MMMM yyyy", locale))

        TimeScale.WEEK -> {
            val end = cursor.anchor.plusDays(6)
            "${cursor.anchor.format(pattern("d MMM", locale))} – ${end.format(pattern("d MMM yyyy", locale))}"
        }

        TimeScale.MONTH -> cursor.anchor.year.toString()

        TimeScale.YEAR -> cursor.anchor.format(pattern("yyyy", locale))
    }

    /** Forme courte, pour une bille dont une partie seulement est visible. */
    fun compact(cursor: TimeCursor, locale: Locale): String = when (cursor.scale) {
        TimeScale.DAY -> cursor.anchor.format(pattern("d MMM", locale))
        TimeScale.WEEK -> cursor.anchor.format(pattern("d MMM", locale))
        TimeScale.MONTH -> cursor.anchor.format(pattern("MMM yyyy", locale))
        TimeScale.YEAR -> cursor.anchor.year.toString()
    }

    private fun pattern(value: String, locale: Locale): DateTimeFormatter =
        DateTimeFormatter.ofPattern(value, locale)
}
