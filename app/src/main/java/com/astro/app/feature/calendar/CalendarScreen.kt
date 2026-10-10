package com.astro.app.feature.calendar

import android.os.SystemClock
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.TimeScaleSelector
import com.astro.app.core.designsystem.components.onHorizontalSwipe
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.time.TimeScale
import com.astro.app.feature.calendar.components.DayView
import com.astro.app.feature.calendar.components.MonthView
import com.astro.app.feature.calendar.components.PeriodHeading
import com.astro.app.feature.calendar.components.WeekView
import com.astro.app.feature.calendar.components.YearView
import java.time.LocalDate
import java.time.YearMonth

/**
 * Page Calendrier.
 *
 * En haut, le sélecteur des quatre vues ; dessous, la période et sa grille. Deux
 * gestes horizontaux cohabitent sur la grille (voir [CalendarSwipeHandler]) : un swipe
 * simple change de période, une salve de trois change de vue.
 *
 * Tant que la vue initiale n'est pas lue dans les préférences, seul l'en-tête est
 * dessiné : on n'affiche pas une vue pour en changer l'instant d'après.
 */
@Composable
fun CalendarScreen(
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory),
) {
    val state by viewModel.state.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val entries by viewModel.entries.collectAsState()
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val dimens = AstroTheme.dimens

    AstroScreen(
        overline = stringResource(R.string.calendar_overline),
        title = stringResource(R.string.feature_calendar),
        modifier = modifier,
    ) {
        val current = state
        if (current != null) {
            val today = viewModel.todayDate()
            val marks = rememberDayMarks(current, entries, preferences)
            val onDayTap = { date: LocalDate -> if (viewModel.onDayTapped(date)) onOpenPlanning(date) }

            TimeScaleSelector(
                selected = current.scale,
                onSelect = viewModel::onSelectScale,
                modifier = Modifier.screenReveal(order = 2),
            )
            PeriodHeading(cursor = current.cursor, locale = locale, modifier = Modifier.screenReveal(order = 2))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .onHorizontalSwipe(key = viewModel) { direction ->
                        viewModel.onSwipe(direction, SystemClock.uptimeMillis())
                    }
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = dimens.spaceXxl)
                    .screenReveal(order = 3),
            ) {
                when (current.scale) {
                    TimeScale.DAY -> DayView(
                        marks = marks.on(current.selected),
                        planning = entries[current.selected],
                        zone = preferences.zoneId,
                        locale = locale,
                        onOpenPlanning = { onOpenPlanning(current.selected) },
                    )
                    TimeScale.WEEK -> WeekView(
                        monday = current.firstDay, selected = current.selected, today = today,
                        marks = marks, entries = entries, locale = locale, onDayTap = onDayTap,
                    )
                    TimeScale.MONTH -> MonthView(
                        month = YearMonth.from(current.firstDay), selected = current.selected,
                        today = today, marks = marks, locale = locale, onDayTap = onDayTap,
                    )
                    TimeScale.YEAR -> YearView(
                        year = current.firstDay.year, today = today, marks = marks,
                        locale = locale, onMonthTap = viewModel::onMonthTapped,
                    )
                }
                Text(
                    text = stringResource(R.string.calendar_swipe_hint),
                    style = MaterialTheme.typography.bodySmall,
                    color = AstroTheme.palette.inkFaint,
                    modifier = Modifier.padding(top = dimens.spaceLg),
                )
            }
        }
    }
}
