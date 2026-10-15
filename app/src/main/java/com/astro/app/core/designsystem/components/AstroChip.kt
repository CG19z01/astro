package com.astro.app.core.designsystem.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
 * Pastille sélectionnable : filtre par sujet des manifestations, genre et thèmes des
 * phrases. Sélectionnée, elle prend la couleur d'accent ; sinon, un simple liseré.
 */
@Composable
fun AstroChip(label: String, active: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val shape = RoundedCornerShape(dimens.radiusSm)
    Text(
        text = label,
        style = MaterialTheme.typography.labelLarge,
        color = if (active) palette.accentSolar else palette.inkMuted,
        modifier = modifier
            .clip(shape)
            .border(dimens.hairline, if (active) palette.accentSolar else palette.surfaceBorder, shape)
            .clickable(onClick = onClick)
            .semantics { this.selected = active }
            .padding(horizontal = dimens.spaceMd, vertical = dimens.spaceSm),
    )
}
