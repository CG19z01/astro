package com.astro.app.feature.calendar.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextAlign
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.feature.calendar.DayMarksSource
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/** Colonnes d'une grille mensuelle : une semaine. */
internal const val DAYS_PER_WEEK = 7

/**
 * Vue Mois : une grille de semaines, du lundi au dimanche.
 *
 * Dans chaque case, le quantième reste l'élément principal. La pleine lune se pose en
 * haut à droite, la bulle de planning sous le chiffre : aucun des deux ne le recouvre.
 */
@Composable
fun MonthView(
    month: YearMonth,
    selected: LocalDate,
    today: LocalDate,
    marks: DayMarksSource,
    locale: Locale,
    onDayTap: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val blanks = month.atDay(1).dayOfWeek.value - 1
    val cells: List<LocalDate?> = List(blanks) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }

    Column(modifier = modifier.fillMaxWidth()) {
        WeekdayHeader(locale)
        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(modifier = Modifier.weight(1f).height(CalendarConfig.MONTH_CELL_HEIGHT)) {
                        if (date != null) {
                            DayCell(date, date == selected, date == today, marks, locale, onDayTap)
                        }
                    }
                }
                repeat(DAYS_PER_WEEK - week.size) { Box(modifier = Modifier.weight(1f)) }
            }
        }
    }
}

/** Initiales des jours, dans la langue de l'interface. */
@Composable
internal fun WeekdayHeader(locale: Locale) {
    val palette = AstroTheme.palette
    Row(modifier = Modifier.fillMaxWidth().padding(bottom = AstroTheme.dimens.spaceSm)) {
        DayOfWeek.entries.forEach { day ->
            Text(
                text = day.getDisplayName(TextStyle.NARROW_STANDALONE, locale).uppercase(locale),
                style = MaterialTheme.typography.labelMedium,
                color = palette.inkFaint,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun DayCell(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    marks: DayMarksSource,
    locale: Locale,
    onDayTap: (LocalDate) -> Unit,
) {
    val palette = AstroTheme.palette
    val dayMarks = marks.on(date)
    val description = dayDescription(date, dayMarks, isSelected, locale)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(CalendarConfig.MONTH_CELL_HEIGHT)
            .clickable { onDayTap(date) }
            .clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(CalendarConfig.SELECTION_RING_SIZE)
                    .border(AstroTheme.dimens.strokeThin, palette.accentSolar, CircleShape),
            )
        }
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.bodyMedium,
            color = if (isToday) palette.accentSolar else palette.ink,
        )
        if (dayMarks.fullMoon != null) {
            FullMoonMark(modifier = Modifier.align(Alignment.TopEnd).padding(AstroTheme.dimens.spaceXs))
        }
        PlanningBubbleFor(
            marks = dayMarks,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = AstroTheme.dimens.hairline),
        )
    }
}
