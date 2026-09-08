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
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import java.util.Locale

/**
 * Où poser le texte dans une planète, et jusqu'où l'étendre.
 *
 * Le décalage est mesuré depuis le coin de la planète, la largeur est déjà bornée par
 * le cercle et par le bord de l'écran : le contenu n'a plus à connaître la géométrie.
 */
data class DayContentPlacement(val x: Dp, val y: Dp, val width: Dp)

/**
 * Contenu de la planète du jour courant : le jour, sa date, et son planning.
 *
 * C'est la seule planète assez grande pour porter le planning entier ; les autres se
 * contentent d'un fragment.
 */
@Composable
fun MainDayContent(place: DayContentPlacement, cursor: TimeCursor, locale: Locale, planning: String) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Column(
        modifier = Modifier
            .offset(x = place.x, y = place.y)
            .widthIn(max = place.width),
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
 * Contenu des trois autres planètes : le jour, sa date et un extrait du planning.
 *
 * Elles portent les mêmes informations que la planète principale, en plus court : leur
 * surface visible est plus réduite, le planning y est donc coupé à deux lignes et
 * l'exergue « planning du jour » est omise, la date suffisant à situer la journée.
 */
@Composable
fun SideDayContent(place: DayContentPlacement, cursor: TimeCursor, locale: Locale, planning: String) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Column(
        modifier = Modifier
            .offset(x = place.x, y = place.y)
            .widthIn(max = place.width),
    ) {
        Text(
            text = PeriodLabels.title(cursor, locale),
            style = MaterialTheme.typography.labelLarge,
            color = palette.ink,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = PeriodLabels.subtitle(cursor, locale),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Text(
            text = planning.ifBlank { stringResource(R.string.home_value_unset) },
            style = MaterialTheme.typography.bodySmall,
            color = if (planning.isBlank()) palette.inkFaint else palette.inkMuted,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = dimens.spaceXs),
        )
    }
}
