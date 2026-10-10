package com.astro.app.feature.calendar.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.feature.calendar.DayMarks
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Description d'un jour pour les lecteurs d'écran.
 *
 * Les marqueurs sont de petits dessins : sans cette description, un utilisateur de
 * TalkBack n'apprendrait ni la pleine lune ni l'existence d'un planning.
 */
@Composable
fun dayDescription(date: LocalDate, marks: DayMarks, selected: Boolean, locale: Locale): String {
    val parts = buildList {
        add(date.format(DateTimeFormatter.ofPattern("EEEE d MMMM yyyy", locale)))
        if (marks.fullMoon != null) add(stringResource(R.string.calendar_full_moon))
        if (marks.planningColor != null) add(stringResource(R.string.calendar_has_planning))
        if (selected) add(stringResource(R.string.calendar_selected))
    }
    return parts.joinToString(separator = ", ")
}
