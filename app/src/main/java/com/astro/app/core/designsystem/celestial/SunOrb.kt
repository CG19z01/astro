package com.astro.app.core.designsystem.celestial

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.AstroLight
import com.astro.app.core.designsystem.texture.rememberDustBrush

private const val PLASMA_INTENSITY = 0.30f
private const val PLASMA_SCALE = 3.2f

/**
 * Le Soleil, rendu comme de l'énergie : couronne diffuse qui déborde, plasma calculé
 * qui rompt l'uniformité du disque, et cœur dont le point le plus chaud est décalé vers
 * la source de lumière commune — un disque parfaitement centré paraît plat.
 *
 * Composant purement visuel, sans interaction, partagé entre la page d'accueil et les
 * écrans d'information : une seule définition de la matière.
 */
@Composable
fun SunOrb(size: Dp, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
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

    Canvas(modifier = modifier.size(size)) {
        val boxRadius = this.size.minDimension / 2f
        val core = boxRadius * 0.38f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        // Couronne : elle s'éteint au bord du cadre, sinon une découpe la trancherait net.
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
                colorStops = arrayOf(
                    0f to CelestialMaterial.SunCore,
                    0.62f to CelestialMaterial.SunSurface,
                    1f to CelestialMaterial.SunLimb,
                ),
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
