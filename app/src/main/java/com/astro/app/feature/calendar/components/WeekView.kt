package com.astro.app.feature.calendar.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.style.TextOverflow
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.feature.calendar.DayMarksSource
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

/**
 * Vue Semaine : sept lignes, une par jour, du lundi au dimanche.
 *
 * La place disponible permet d'afficher le début du planning : la bulle dit qu'une note
 * existe, l'extrait dit ce qu'elle contient. La pleine lune se place en fin de ligne.
 */
@Composable
fun WeekView(
    monday: LocalDate,
    selected: LocalDate,
    today: LocalDate,
    marks: DayMarksSource,
    entries: Map<LocalDate, String>,
    locale: Locale,
    onDayTap: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        (0L until 7L).map { monday.plusDays(it) }.forEach { date ->
            WeekRow(
                date = date,
                isSelected = date == selected,
                isToday = date == today,
                marks = marks,
                preview = entries[date],
                locale = locale,
                onTap = { onDayTap(date) },
            )
        }
    }
}

@Composable
private fun WeekRow(
    date: LocalDate,
    isSelected: Boolean,
    isToday: Boolean,
    marks: DayMarksSource,
    preview: String?,
    locale: Locale,
    onTap: () -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val dayMarks = marks.on(date)
    val shape = RoundedCornerShape(dimens.radiusSm)
    val description = dayDescription(date, dayMarks, isSelected, locale)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = CalendarConfig.WEEK_ROW_HEIGHT)
            .background(if (isSelected) palette.surface.copy(alpha = 0.55f) else Color.Transparent, shape)
            .border(dimens.hairline, if (isSelected) palette.accentSolar else Color.Transparent, shape)
            .clickable(onClick = onTap)
            .clearAndSetSemantics { contentDescription = description }
            .padding(horizontal = dimens.spaceMd, vertical = dimens.spaceSm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
    ) {
        Column(
            modifier = Modifier.width(dimens.spaceXl),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = date.dayOfWeek.getDisplayName(TextStyle.SHORT_STANDALONE, locale),
                style = MaterialTheme.typography.labelMedium,
                color = palette.inkFaint,
            )
            Text(
                text = date.dayOfMonth.toString(),
                style = MaterialTheme.typography.titleLarge,
                color = if (isToday) palette.accentSolar else palette.ink,
            )
            PlanningBubbleFor(marks = dayMarks, modifier = Modifier.padding(top = dimens.spaceXs))
        }
        Text(
            text = preview.orEmpty(),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            maxLines = CalendarConfig.WEEK_PREVIEW_LINES,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        if (dayMarks.fullMoon != null) FullMoonMark()
    }
}
