package com.astro.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/** Rayons fins et réguliers, plutôt qu'une couronne dentelée. */
private const val RAY_COUNT = 12
private const val RAY_INNER = 1.28f
private const val RAY_OUTER = 1.62f

/**
 * Le Soleil : point d'entrée vers les Paramètres.
 *
 * Composé d'un disque, d'un anneau et de rayons détachés. Le fait que les rayons ne
 * touchent pas le disque allège la figure et évite l'aspect « soleil dessiné ».
 */
@Composable
fun SunButton(
    size: Dp,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val label = contentDescription

    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = label },
    ) {
        // Halo calé sur le cadre, sinon la découpe circulaire du bouton le tranche net.
        val boxRadius = this.size.minDimension / 2f
        val radius = boxRadius * 0.38f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.accentSolar.copy(alpha = 0.24f), Color.Transparent),
                center = center,
                radius = boxRadius,
            ),
            radius = boxRadius,
            center = center,
        )
        drawCircle(color = palette.accentSolar.copy(alpha = 0.92f), radius = radius, center = center)
        drawCircle(
            color = palette.accentSolar.copy(alpha = 0.45f),
            radius = radius * 1.9f,
            center = center,
            style = Stroke(width = dimens.hairline.toPx()),
        )
        drawRays(center, radius, palette.accentSolar, dimens.hairline.toPx())
    }
}

private fun DrawScope.drawRays(center: Offset, radius: Float, color: Color, width: Float) {
    repeat(RAY_COUNT) { index ->
        val angle = (index.toDouble() / RAY_COUNT) * 2.0 * PI
        val dx = cos(angle).toFloat()
        val dy = sin(angle).toFloat()
        drawLine(
            color = color.copy(alpha = 0.7f),
            start = Offset(center.x + dx * radius * RAY_INNER, center.y + dy * radius * RAY_INNER),
            end = Offset(center.x + dx * radius * RAY_OUTER, center.y + dy * radius * RAY_OUTER),
            strokeWidth = width,
            cap = StrokeCap.Round,
        )
    }
}
