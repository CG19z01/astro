package com.astro.app.navigation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.astro.app.core.config.FeatureId
import com.astro.app.core.ui.toothedPath

/** Taille des pictogrammes de la barre de navigation. */
private val ICON_SIZE = 22.dp

/**
 * Pictogrammes de la barre basse, dessinés à la main d'après la maquette.
 *
 * Aucun pack d'icônes n'est tiré pour cinq symboles simples. Une fonctionnalité sans
 * pictogramme déclaré retombe sur un cercle neutre : ajouter une page reste donc
 * possible sans toucher à ce fichier.
 */
@Composable
fun FeatureIcon(id: FeatureId, color: Color, modifier: Modifier = Modifier) {
    Canvas(modifier = modifier.size(ICON_SIZE)) {
        val stroke = Stroke(width = 1.6f.dp.toPx())
        when (id) {
            FeatureId.HOME -> drawHouse(color, stroke)
            FeatureId.ASTROLOGY -> drawMoon(color, stroke)
            FeatureId.PHRASES -> drawLines(color, stroke)
            FeatureId.CALENDAR -> drawCalendar(color, stroke)
            FeatureId.SETTINGS -> drawGear(color, stroke)
            else -> drawCircle(color = color, style = stroke)
        }
    }
}

private fun DrawScope.drawHouse(color: Color, stroke: Stroke) {
    val w = size.width
    val h = size.height
    val roof = Path().apply {
        moveTo(w * 0.08f, h * 0.45f)
        lineTo(w * 0.5f, h * 0.10f)
        lineTo(w * 0.92f, h * 0.45f)
    }
    drawPath(roof, color, style = stroke)
    drawRect(
        color = color,
        topLeft = Offset(w * 0.20f, h * 0.45f),
        size = Size(w * 0.60f, h * 0.45f),
        style = stroke,
    )
}

private fun DrawScope.drawMoon(color: Color, stroke: Stroke) {
    val radius = size.minDimension * 0.42f
    val center = Offset(size.width / 2f, size.height / 2f)
    drawCircle(color, radius, center, style = stroke)
    drawCircle(color, radius * 0.22f, center.translate(-0.35f, -0.30f, radius), style = stroke)
    drawCircle(color, radius * 0.16f, center.translate(0.30f, 0.10f, radius), style = stroke)
}

private fun Offset.translate(fx: Float, fy: Float, radius: Float) =
    Offset(x + radius * fx, y + radius * fy)

private fun DrawScope.drawLines(color: Color, stroke: Stroke) {
    val w = size.width
    listOf(0.28f, 0.5f, 0.72f).forEachIndexed { index, fy ->
        val end = if (index == 2) 0.66f else 0.86f
        drawLine(color, Offset(w * 0.14f, size.height * fy), Offset(w * end, size.height * fy), stroke.width)
    }
}

private fun DrawScope.drawCalendar(color: Color, stroke: Stroke) {
    val w = size.width
    val h = size.height
    drawRect(color, Offset(w * 0.12f, h * 0.20f), Size(w * 0.76f, h * 0.68f), style = stroke)
    drawLine(color, Offset(w * 0.12f, h * 0.42f), Offset(w * 0.88f, h * 0.42f), stroke.width)
    drawLine(color, Offset(w * 0.32f, h * 0.10f), Offset(w * 0.32f, h * 0.28f), stroke.width)
    drawLine(color, Offset(w * 0.68f, h * 0.10f), Offset(w * 0.68f, h * 0.28f), stroke.width)
}

private fun DrawScope.drawGear(color: Color, stroke: Stroke) {
    val center = Offset(size.width / 2f, size.height / 2f)
    val outer = size.minDimension * 0.46f
    drawPath(toothedPath(center, outer, teeth = 8, innerRatio = 0.72f), color, style = stroke)
    drawCircle(color, outer * 0.32f, center, style = stroke)
}
