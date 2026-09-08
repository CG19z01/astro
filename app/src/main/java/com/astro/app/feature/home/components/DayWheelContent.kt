package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import com.astro.app.R
import com.astro.app.core.config.DayWheelConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.feature.home.CompanionTitle
import java.util.Locale

/**
 * Contenu de la planète du jour courant : le jour, sa date, et son planning.
 *
 * C'est la seule planète assez grande pour porter le planning entier ; les autres se
 * contentent d'un fragment.
 */
@Composable
fun MainDayContent(
    spec: DayWheelConfig.PlanetSpec,
    diameter: Dp,
    cursor: TimeCursor,
    locale: Locale,
    planning: String,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Column(
        modifier = Modifier
            .offset(
                x = diameter * spec.contentX,
                y = diameter * spec.contentY,
            )
            .widthIn(max = diameter * 0.62f),
    ) {
        Text(
            text = PeriodLabels.title(cursor, locale),
            style = MaterialTheme.typography.headlineLarge,
            color = palette.ink,
            maxLines = 1,
        )
        Text(
            text = PeriodLabels.subtitle(cursor, locale),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceXs),
        )
        SectionLabel(
            text = stringResource(R.string.home_planning_title),
            modifier = Modifier.padding(top = dimens.spaceMd),
        )
        Text(
            text = planning.ifBlank { stringResource(R.string.home_planning_empty) },
            style = MaterialTheme.typography.bodyMedium,
            color = if (planning.isBlank()) palette.inkFaint else palette.inkMuted,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
    }
}

/**
 * Contenu des trois autres planètes : le jour et le début du planning, tronqué.
 *
 * Leur surface visible est réduite ; y entasser davantage les rendrait illisibles.
 */
@Composable
fun SideDayContent(
    spec: DayWheelConfig.PlanetSpec,
    diameter: Dp,
    cursor: TimeCursor,
    locale: Locale,
    planning: String,
) {
    val palette = AstroTheme.palette

    Column(
        modifier = Modifier
            .offset(
                x = diameter * spec.contentX,
                y = diameter * spec.contentY,
            )
            .widthIn(max = diameter * 0.62f),
    ) {
        Text(
            text = PeriodLabels.title(cursor, locale),
            style = MaterialTheme.typography.labelLarge,
            color = palette.inkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = CompanionTitle.of(planning),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            maxLines = 1,
        )
    }
}
