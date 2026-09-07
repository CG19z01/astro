package com.astro.app.feature.astrology.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.celestial.MoonOrb
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel

/** Taille de la Lune dans le panneau. Assez grande pour que la matière se lise. */
private val ORB_SIZE = 132.dp

/**
 * Panneau de la Lune : sa texture, ses horaires, et le calendrier lunaire qu'un appui déplie.
 *
 * La Lune affichée est exactement celle de la page d'accueil — même composant, même
 * matière. Les horaires sont posés à côté d'elle plutôt que sur elle : une texture
 * sous du texte en abîmerait la lecture.
 */
@Composable
fun MoonPanel(
    overline: String,
    riseLabel: String,
    riseValue: String,
    setLabel: String,
    setValue: String,
    expandHint: String,
    expandLabel: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    details: @Composable () -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroCard(
        modifier = modifier
            .clickable(onClick = onToggle)
            .semantics { contentDescription = expandLabel },
    ) {
        SectionLabel(text = overline)
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = dimens.spaceMd),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            MoonOrb(size = ORB_SIZE)
            Column(verticalArrangement = Arrangement.spacedBy(dimens.spaceSm)) {
                TimeLine(riseLabel, riseValue)
                TimeLine(setLabel, setValue)
            }
        }
        Text(
            text = expandHint,
            style = MaterialTheme.typography.bodySmall,
            color = palette.inkFaint,
            modifier = Modifier.padding(top = dimens.spaceMd),
        )
        AnimatedVisibility(visible = expanded) {
            Column(modifier = Modifier.padding(top = dimens.spaceLg)) { details() }
        }
    }
}

@Composable
private fun TimeLine(label: String, value: String) {
    val palette = AstroTheme.palette
    Column {
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = palette.inkMuted)
        Text(text = value, style = MaterialTheme.typography.headlineMedium, color = palette.ink)
    }
}
