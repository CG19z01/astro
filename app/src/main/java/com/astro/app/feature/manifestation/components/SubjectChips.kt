package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.astro.app.core.designsystem.AstroTheme

/**
 * Filtre par sujet : « Tous », puis un sujet par pastille. Les pastilles vont à la
 * ligne : le nombre de sujets n'est pas borné, puisqu'ils sont écrits librement.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SubjectChips(
    allLabel: String,
    subjects: List<String>,
    selected: String?,
    onSelect: (String?) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(dimens.spaceSm),
        verticalArrangement = Arrangement.spacedBy(dimens.spaceSm),
    ) {
        Chip(allLabel, selected == null) { onSelect(null) }
        subjects.forEach { subject ->
            Chip(subject, selected?.equals(subject, ignoreCase = true) == true) { onSelect(subject) }
        }
    }
}

@Composable
private fun Chip(label: String, active: Boolean, onClick: () -> Unit) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val shape = RoundedCornerShape(dimens.radiusSm)
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (active) palette.accentSolar else palette.inkMuted,
        modifier = Modifier
            .clip(shape)
            .border(dimens.hairline, if (active) palette.accentSolar else palette.surfaceBorder, shape)
            .clickable(onClick = onClick)
            .semantics { this.selected = active }
            .padding(horizontal = dimens.spaceMd, vertical = dimens.spaceSm),
    )
}
