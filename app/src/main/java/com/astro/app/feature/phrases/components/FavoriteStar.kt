package com.astro.app.feature.phrases.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.astro.app.core.designsystem.AstroTheme
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Étoile de favori : contour fin quand la phrase n'est pas en favori, pleine sinon.
 * Dessinée — une étoile, motif naturel dans une application astrale.
 */
@Composable
fun FavoriteStar(favorite: Boolean, label: String, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.touchTarget)
            .clip(CircleShape)
            .toggleable(value = favorite, role = Role.Checkbox, onValueChange = { onToggle() })
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(dimens.iconSm)) {
            val star = starPath(size.minDimension / 2f, center.x, center.y)
            if (favorite) {
                drawPath(star, palette.accentSolar, style = Fill)
            } else {
                drawPath(star, palette.inkMuted, style = Stroke(width = dimens.hairline.toPx()))
            }
        }
    }
}

/** Étoile à cinq branches ; rayon intérieur au ratio de l'étoile régulière. */
private fun starPath(outer: Float, cx: Float, cy: Float): Path = Path().apply {
    val inner = outer * INNER_RATIO
    for (i in 0 until 10) {
        val radius = if (i % 2 == 0) outer else inner
        val angle = -PI / 2 + i * PI / 5
        val x = cx + (radius * cos(angle)).toFloat()
        val y = cy + (radius * sin(angle)).toFloat()
        if (i == 0) moveTo(x, y) else lineTo(x, y)
    }
    close()
}

private const val INNER_RATIO = 0.382f
