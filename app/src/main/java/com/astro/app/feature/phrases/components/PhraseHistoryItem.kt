package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.phrases.Phrase
import com.astro.app.core.time.PeriodLabels
import com.astro.app.core.time.TimeCursor
import com.astro.app.core.time.TimeScale
import java.time.LocalDate
import java.util.Locale

/** Une journée de l'historique : sa date, sa phrase, son auteur s'il est connu. */
@Composable
fun PhraseHistoryItem(date: LocalDate, phrase: Phrase, locale: Locale, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val cursor = TimeCursor.of(TimeScale.DAY, date)
    Column(modifier = modifier.fillMaxWidth()) {
        SectionLabel(text = "${PeriodLabels.title(cursor, locale)} ${PeriodLabels.subtitle(cursor, locale)}")
        Text(
            text = phrase.text,
            style = MaterialTheme.typography.headlineSmall,
            color = palette.ink,
            modifier = Modifier.padding(top = dimens.spaceSm),
        )
        if (phrase.author.isNotBlank()) {
            Text(
                text = phrase.author,
                style = MaterialTheme.typography.bodySmall,
                color = palette.inkMuted,
                modifier = Modifier.padding(top = dimens.spaceXs),
            )
        }
    }
}
