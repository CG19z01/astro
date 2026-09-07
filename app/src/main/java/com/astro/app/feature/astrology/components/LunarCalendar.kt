package com.astro.app.feature.astrology.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.astro.app.core.astronomy.MoonCalculator
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.celestial.MoonPhaseGlyph
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

/** Nombre de colonnes : une semaine. */
private const val COLUMNS = 7

/**
 * Calendrier lunaire d'un mois : un disque de phase par jour.
 *
 * Le mois est aligné sur le jour de la semaine de son premier jour, comme un calendrier
 * ordinaire : sans cet alignement, on ne peut pas suivre le cycle d'une semaine à l'autre.
 *
 * Aucune coordonnée n'est nécessaire : la phase est identique partout sur Terre.
 */
@Composable
fun LunarMonth(
    month: YearMonth,
    zone: ZoneId,
    locale: Locale,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val blanks = month.atDay(1).dayOfWeek.value - 1
    val cells: List<LocalDate?> = List(blanks) { null } + (1..month.lengthOfMonth()).map { month.atDay(it) }

    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = month.month.getDisplayName(TextStyle.FULL_STANDALONE, locale)
                .replaceFirstChar { it.titlecase(locale) },
            style = MaterialTheme.typography.titleMedium,
            color = palette.ink,
            modifier = Modifier.padding(bottom = dimens.spaceSm),
        )
        cells.chunked(COLUMNS).forEach { week ->
            Row(modifier = Modifier.fillMaxWidth()) {
                week.forEach { date -> DayCell(date, zone, Modifier.weight(1f)) }
                repeat(COLUMNS - week.size) { Row(modifier = Modifier.weight(1f)) {} }
            }
        }
    }
}

@Composable
private fun DayCell(date: LocalDate?, zone: ZoneId, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    Column(
        modifier = modifier.padding(vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        if (date == null) return@Column
        val phase = MoonCalculator.phaseOn(date, zone)
        MoonPhaseGlyph(size = 18.dp, illumination = phase.illumination, waxing = phase.waxing)
        Text(
            text = date.dayOfMonth.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = palette.inkFaint,
        )
    }
}
