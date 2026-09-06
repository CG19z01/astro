package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import com.astro.app.core.config.HomeLayoutConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import java.time.LocalDate
import java.util.Locale

/**
 * Les deux billes-journées de la page d'accueil.
 *
 * La bille principale porte la période courante ; la seconde, la période suivante,
 * coupée par le bord droit. Aucune date n'est écrite ici : tout est dérivé du curseur
 * temporel, donc naviguer met les deux billes à jour.
 */
@Composable
fun DayBeadLayer(
    width: Dp,
    height: Dp,
    cursor: TimeCursor,
    locale: Locale,
    planningPreview: String,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens
    val palette = AstroTheme.palette
    val nextCursor = cursor.next()
    val primary = HomeLayoutConfig.primaryBead.bounds(width, height)
    val secondary = HomeLayoutConfig.secondaryBead.bounds(width, height)

    Box(modifier = modifier.fillMaxSize()) {
        DayBead(
            size = secondary.size,
            emphasis = 0.55f,
            contentDescription = PeriodLabels.title(nextCursor, locale),
            onClick = { onOpenPlanning(nextCursor.anchor) },
            modifier = Modifier.offset(x = secondary.x, y = secondary.y),
        ) {
            Column(
                modifier = Modifier
                    .offset(
                        x = secondary.size * HomeLayoutConfig.SECONDARY_CONTENT_X,
                        y = secondary.size * HomeLayoutConfig.SECONDARY_CONTENT_Y,
                    )
                    .widthIn(max = secondary.size * 0.5f),
            ) {
                Text(
                    text = PeriodLabels.title(nextCursor, locale),
                    style = MaterialTheme.typography.titleMedium,
                    color = palette.inkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = PeriodLabels.compact(nextCursor, locale),
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.inkFaint,
                    maxLines = 1,
                )
            }
        }

        DayBead(
            size = primary.size,
            emphasis = 1f,
            contentDescription = PeriodLabels.subtitle(cursor, locale),
            onClick = { onOpenPlanning(cursor.anchor) },
            modifier = Modifier.offset(x = primary.x, y = primary.y),
        ) {
            Column(
                modifier = Modifier
                    .offset(
                        x = primary.size * HomeLayoutConfig.PRIMARY_CONTENT_X,
                        y = primary.size * HomeLayoutConfig.PRIMARY_CONTENT_Y,
                    )
                    .widthIn(max = primary.size * 0.62f),
            ) {
                Text(
                    text = PeriodLabels.title(cursor, locale),
                    style = MaterialTheme.typography.displayMedium,
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
                    modifier = Modifier.padding(top = dimens.spaceLg),
                )
                Text(
                    text = planningPreview.ifBlank { stringResource(R.string.home_planning_empty) },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (planningPreview.isBlank()) palette.inkFaint else palette.inkMuted,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = dimens.spaceSm),
                )
            }
        }
    }
}
