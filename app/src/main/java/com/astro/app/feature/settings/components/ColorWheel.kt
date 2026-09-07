package com.astro.app.feature.settings.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import kotlin.math.atan2
import kotlin.math.min
import kotlin.math.sqrt

/** Teintes du cercle, du rouge au rouge en passant par tout le spectre. */
private val HUES = listOf(0f, 60f, 120f, 180f, 240f, 300f, 360f)

/**
 * Roue chromatique : toutes les teintes et toutes les saturations.
 *
 * Deux dégradés superposés suffisent : un **balayage** pour la teinte, qui fait le tour
 * du cercle, et un **radial** blanc vers transparent pour la saturation, qui décolore
 * vers le centre. Dessiner la roue pixel par pixel donnerait le même résultat en
 * coûtant bien plus cher.
 *
 * La luminosité est réglée à part par un curseur : elle ne peut pas s'exprimer sur un
 * disque déjà occupé par la teinte et la saturation.
 */
@Composable
fun ColorWheel(
    value: Float,
    selected: Color,
    onPick: (hue: Float, saturation: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .pointerInput(Unit) {
                detectTapGestures { position -> pick(position, size.width, size.height, onPick) }
            }
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    pick(change.position, size.width, size.height, onPick)
                }
            },
    ) {
        val radius = size.minDimension / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        drawCircle(
            brush = Brush.sweepGradient(colors = HUES.map { Color.hsv(it % 360f, 1f, value) }, center = center),
            radius = radius,
            center = center,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(Color.hsv(0f, 0f, value), Color.Transparent),
                center = center,
                radius = radius,
            ),
            radius = radius,
            center = center,
        )
        // Repère de la couleur retenue : un anneau clair doublé d'un anneau sombre,
        // pour rester visible aussi bien sur une zone claire que sur une zone saturée.
        val marker = markerPosition(selected, center, radius)
        drawCircle(Color.White, radius = 9.dp.toPx(), center = marker, style = Stroke(width = 3.dp.toPx()))
        drawCircle(Color.Black.copy(alpha = 0.5f), radius = 12.dp.toPx(), center = marker, style = Stroke(width = 1.dp.toPx()))
    }
}

/** Convertit une position sur le disque en teinte et saturation. */
private fun pick(
    position: Offset,
    width: Int,
    height: Int,
    onPick: (hue: Float, saturation: Float) -> Unit,
) {
    val radius = min(width, height) / 2f
    val dx = position.x - width / 2f
    val dy = position.y - height / 2f
    val distance = sqrt(dx * dx + dy * dy)
    val angle = Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()

    onPick(((angle % 360f) + 360f) % 360f, (distance / radius).coerceIn(0f, 1f))
}

/** Position du repère, déduite de la teinte et de la saturation de la couleur retenue. */
private fun markerPosition(color: Color, center: Offset, radius: Float): Offset {
    val hsv = FloatArray(3)
    android.graphics.Color.colorToHSV(color.toArgb(), hsv)
    val angle = Math.toRadians(hsv[0].toDouble())
    return Offset(
        x = center.x + (kotlin.math.cos(angle) * hsv[1] * radius).toFloat(),
        y = center.y + (kotlin.math.sin(angle) * hsv[1] * radius).toFloat(),
    )
}
