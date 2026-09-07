package com.astro.app.feature.home.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.AstroLight
import com.astro.app.core.designsystem.texture.lerp
import com.astro.app.core.designsystem.texture.rememberDustBrush

private const val PLASMA_INTENSITY = 0.30f
private const val PLASMA_SCALE = 3.2f

/**
 * Le Soleil : point d'entrée vers les Paramètres.
 *
 * De l'énergie, pas un pictogramme. Trois couches : une couronne diffuse qui déborde
 * largement, un plasma calculé qui rompt l'uniformité du disque, et un cœur dont le
 * point le plus chaud est légèrement décentré — un disque parfaitement centré paraît
 * plat. La couronne respire très lentement au lieu de porter des rayons dessinés.
 */
@Composable
fun SunButton(
    size: Dp,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val label = contentDescription
    val plasma = rememberDustBrush(Color.White, PLASMA_INTENSITY, seed = 31)
    val transition = rememberInfiniteTransition(label = "sun")
    val pulse by transition.animateFloat(
        initialValue = 0.94f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(AstroMotion.AMBIENT_BREATH_MILLIS, easing = AstroMotion.Gentle),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse",
    )

    Canvas(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = label },
    ) {
        val boxRadius = this.size.minDimension / 2f
        val core = boxRadius * 0.40f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)
        val hot = lerp(palette.accentSolar, Color.White, 0.55f)

        // Couronne : elle s'éteint exactement au bord du cadre, sinon la découpe la trancherait.
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(
                    0f to palette.accentSolar.copy(alpha = 0.42f),
                    0.45f to palette.accentSolar.copy(alpha = 0.16f),
                    1f to Color.Transparent,
                ),
                center = center,
                radius = boxRadius * pulse,
            ),
            radius = boxRadius,
            center = center,
        )
        drawCircle(
            brush = Brush.radialGradient(
                colorStops = arrayOf(0f to hot, 0.62f to palette.accentSolar, 1f to lerp(palette.accentSolar, palette.skyTop, 0.35f)),
                // Point le plus chaud décalé vers la source commune à l'application.
                center = Offset(center.x + core * AstroLight.X * 0.6f, center.y + core * AstroLight.Y * 0.6f),
                radius = core * 1.5f,
            ),
            radius = core,
            center = center,
        )
        scale(PLASMA_SCALE, PLASMA_SCALE, pivot = center) {
            drawCircle(brush = plasma, radius = core / PLASMA_SCALE, center = center)
        }
    }
}
