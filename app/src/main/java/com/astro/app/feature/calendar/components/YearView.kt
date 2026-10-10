package com.astro.app.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.feature.calendar.DayMarksSource
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/**
 * Vue Année : douze mini-mois. Appuyer sur l'un d'eux l'ouvre en vue Mois.
 *
 * Les cases sont trop petites pour y poser un symbole lisible. Les marques passent
 * donc par le chiffre lui-même : un jour renseigné prend la couleur de sa planète, un
 * jour de pleine lune se pose sur un disque lunaire pâle. Les deux se combinent sans
 * se masquer, et aucun minuscule dessin ne vient brouiller la grille.
 */
@Composable
fun YearView(
    year: Int,
    today: LocalDate,
    marks: DayMarksSource,
    locale: Locale,
    onMonthTap: (YearMonth) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(dimens.spaceMd)) {
        Month.entries.chunked(CalendarConfig.YEAR_COLUMNS).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd)) {
                row.forEach { month ->
                    val yearMonth = YearMonth.of(year, month)
                    MiniMonth(
                        month = yearMonth,
                        today = today,
                        marks = marks,
                        locale = locale,
                        modifier = Modifier.weight(1f).clickable { onMonthTap(yearMonth) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniMonth(
    month: YearMonth,
    today: LocalDate,
    marks: DayMarksSource,
    locale: Locale,
    modifier: Modifier,
) {
    val palette = AstroTheme.palette
    val blanks = month.atDay(1).dayOfWeek.value - 1
    val cells: List<LocalDate?> = List(blanks) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }

    Column(modifier = modifier) {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                .replaceFirstChar { it.titlecase(locale) },
            style = MaterialTheme.typography.labelLarge,
            color = if (YearMonth.from(today) == month) palette.accentSolar else palette.inkMuted,
            modifier = Modifier.padding(bottom = AstroTheme.dimens.spaceXs),
        )
        cells.chunked(DAYS_PER_WEEK).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date ->
                    Box(Modifier.weight(1f).height(CalendarConfig.YEAR_CELL_SIZE), Alignment.Center) {
                        if (date != null) MiniDay(date, date == today, marks)
                    }
                }
                repeat(DAYS_PER_WEEK - week.size) { Box(Modifier.weight(1f)) }
            }
        }
    }
}

@Composable
private fun MiniDay(date: LocalDate, isToday: Boolean, marks: DayMarksSource) {
    val palette = AstroTheme.palette
    val dayMarks = marks.on(date)
    val color = when {
        dayMarks.planningColor != null -> Color(dayMarks.planningColor)
        isToday -> palette.accentSolar
        else -> palette.inkFaint
    }
    if (dayMarks.fullMoon != null) {
        Box(
            Modifier
                .size(CalendarConfig.YEAR_CELL_SIZE)
                .background(palette.accentLunar.copy(alpha = CalendarConfig.YEAR_FULL_MOON_ALPHA), CircleShape),
        )
    }
    Text(
        text = date.dayOfMonth.toString(),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = if (dayMarks.planningColor != null || isToday) FontWeight.Bold else null,
        color = color,
        softWrap = false,
    )
}
