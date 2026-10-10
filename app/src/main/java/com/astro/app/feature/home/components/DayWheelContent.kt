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
 * Les messages défilent sous le doigt ([PlanetMessages]) : aucun n'est coupé ni perdu.
 */
@Composable
fun MainDayContent(place: DayContentPlacement, cursor: TimeCursor, locale: Locale, planning: String, lines: Int) {
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
            text = PeriodLabels.numericDate(cursor, locale),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceXs),
        )
        // Pas d'exergue « Planning du jour » : retirée le 2026-10-10 à la demande de
        // l'utilisateur, la planète suffisant à dire de quelle journée il s'agit.
        PlanetMessages(
            text = planning.ifBlank { stringResource(R.string.home_planning_empty) },
            lines = lines,
            style = MaterialTheme.typography.bodyMedium,
            color = if (planning.isBlank()) palette.inkFaint else palette.inkMuted,
            modifier = Modifier.padding(top = dimens.spaceMd),
        )
    }
}

/**
 * Contenu des trois autres planètes : le jour, sa date et un extrait du planning.
 *
 * Elles portent les mêmes informations que la planète principale, en plus court : leur
 * surface visible est plus réduite, la zone de défilement y est donc plus basse.
 */
@Composable
fun SideDayContent(
    place: DayContentPlacement,
    cursor: TimeCursor,
    locale: Locale,
    planning: String,
    lines: Int,
    /** Zone propre aux messages ; nulle, ils suivent le jour et la date. */
    messages: DayContentPlacement? = null,
) {
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
            text = PeriodLabels.numericDate(cursor, locale),
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (messages == null) SideMessages(planning, lines, Modifier.padding(top = dimens.spaceXs))
    }
    if (messages != null) {
        SideMessages(planning, lines, Modifier.offset(x = messages.x, y = messages.y).widthIn(max = messages.width))
    }
}

@Composable
private fun SideMessages(planning: String, lines: Int, modifier: Modifier) {
    val palette = AstroTheme.palette
    PlanetMessages(
        text = planning.ifBlank { stringResource(R.string.home_value_unset) },
        lines = lines,
        style = MaterialTheme.typography.bodySmall,
        color = if (planning.isBlank()) palette.inkFaint else palette.inkMuted,
        modifier = modifier,
    )
}
