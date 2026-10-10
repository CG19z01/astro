package com.astro.app.feature.calendar.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.config.CalendarConfig
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.feature.calendar.DayMarks
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

/**
 * Vue Jour : le détail d'une journée.
 *
 * La pleine lune y est donnée avec son heure locale, la place le permettant : c'est la
 * seule vue où l'instant exact est utile. Le planning s'affiche en entier, et appuyer
 * sur la carte l'ouvre pour l'écrire.
 */
@Composable
fun DayView(
    marks: DayMarks,
    planning: String?,
    zone: ZoneId,
    locale: Locale,
    onOpenPlanning: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier.fillMaxWidth().clickable(onClick = onOpenPlanning)) {
        marks.fullMoon?.let { instant ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
                modifier = Modifier.padding(bottom = dimens.spaceMd),
            ) {
                FullMoonMark(size = CalendarConfig.FULL_MOON_DETAIL_SIZE)
                Text(
                    text = stringResource(
                        R.string.calendar_full_moon_at,
                        instant.atZone(zone).format(DateTimeFormatter.ofPattern("HH:mm", locale)),
                    ),
                    style = MaterialTheme.typography.bodyMedium,
                    color = palette.accentLunar,
                )
            }
        }
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        ) {
            PlanningBubbleFor(marks = marks)
            SectionLabel(text = stringResource(R.string.home_planning_title))
        }
        Text(
            text = planning ?: stringResource(R.string.home_planning_empty),
            style = MaterialTheme.typography.bodyLarge,
            color = if (planning == null) palette.inkFaint else palette.ink,
            modifier = Modifier.padding(top = dimens.spaceMd),
        )
    }
}
