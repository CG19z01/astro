package com.astro.app.feature.manifestation.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.astro.app.core.designsystem.AstroTheme

/**
 * Le « + » en bas à droite, qui ouvre une nouvelle manifestation.
 *
 * Dessiné, comme les pictogrammes de la barre : aucun pack d'icônes pour une croix.
 */
@Composable
fun AddButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.touchTarget + dimens.spaceMd)
            .clip(CircleShape)
            .background(palette.accentSolar)
            .border(dimens.hairline, palette.surfaceBorder, CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(dimens.iconMd)) {
            val stroke = dimens.strokeRegular.toPx() * 1.4f
            val mid = size.width / 2f
            drawLine(palette.surface, Offset(mid, 0f), Offset(mid, size.height), stroke, StrokeCap.Round)
            drawLine(palette.surface, Offset(0f, mid), Offset(size.width, mid), stroke, StrokeCap.Round)
        }
    }
}
