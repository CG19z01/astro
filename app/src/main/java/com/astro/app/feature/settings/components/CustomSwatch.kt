package com.astro.app.feature.settings.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp

/** Pastille d'ouverture de la roue : un plus, ou la couleur personnalisée en cours. */
@Composable
internal fun CustomSwatch(
    color: Color,
    selected: Boolean,
    ring: Color,
    plus: Color,
    onClick: () -> Unit,
) {
    Canvas(
        modifier = Modifier
            .size(SWATCH_SIZE)
            .clip(CircleShape)
            .clickable(onClick = onClick),
    ) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)
        drawCircle(color = color, radius = radius, center = center)
        drawCircle(
            color = if (selected) ring else ring.copy(alpha = 0.45f),
            radius = radius - 1.dp.toPx(),
            center = center,
            style = Stroke(width = if (selected) 2.dp.toPx() else 1.dp.toPx()),
        )
        val arm = radius * 0.42f
        val thickness = 1.6f.dp.toPx()
        drawLine(plus, Offset(center.x - arm, center.y), Offset(center.x + arm, center.y), thickness)
        drawLine(plus, Offset(center.x, center.y - arm), Offset(center.x, center.y + arm), thickness)
    }
}

