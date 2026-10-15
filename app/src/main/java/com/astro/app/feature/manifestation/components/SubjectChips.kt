package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroChip

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
        AstroChip(allLabel, selected == null, onClick = { onSelect(null) })
        subjects.forEach { subject ->
            AstroChip(subject, selected?.equals(subject, ignoreCase = true) == true, onClick = { onSelect(subject) })
        }
    }
}
