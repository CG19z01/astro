package com.astro.app.feature.calendar.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import java.util.Locale

/**
 * Nom de la période affichée : « Octobre 2026 », « S42 12 oct. – 18 oct. 2026 »…
 *
 * Les libellés viennent de [PeriodLabels], déjà employé par la Home et le Planning :
 * une même période porte le même nom partout. En vue Année, titre et sous-titre sont
 * tous deux l'année : on ne l'écrit qu'une fois.
 */
@Composable
fun PeriodHeading(cursor: TimeCursor, locale: Locale, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Row(
        modifier = modifier.padding(top = dimens.spaceMd, bottom = dimens.spaceMd),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
    ) {
        Text(
            text = PeriodLabels.title(cursor, locale),
            style = MaterialTheme.typography.headlineMedium,
            color = palette.ink,
        )
        if (cursor.scale != TimeScale.YEAR) {
            Text(
                text = PeriodLabels.subtitle(cursor, locale),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkMuted,
            )
        }
    }
}
