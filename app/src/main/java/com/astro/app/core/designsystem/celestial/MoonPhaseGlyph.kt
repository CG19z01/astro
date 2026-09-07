package com.astro.app.core.designsystem.celestial

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme
import kotlin.math.abs

/**
 * Petit disque de phase, pour un calendrier lunaire.
 *
 * La forme est exacte et non suggérée : le terminateur, qui sépare la partie éclairée
 * de l'ombre, se projette sur le disque comme une **ellipse** dont le demi-axe vaut
 * `R × |1 − 2k|`, avec `k` la fraction éclairée. Le croissant s'obtient en retirant
 * cette ellipse au demi-disque éclairé, la lune gibbeuse en la lui ajoutant.
 *
 * Un simple cercle partiellement rempli donnerait une forme fausse à toutes les phases
 * intermédiaires.
 */
@Composable
fun MoonPhaseGlyph(
    size: Dp,
    illumination: Double,
    waxing: Boolean,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette

    Canvas(modifier = modifier.size(size)) {
        val radius = this.size.minDimension / 2f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        drawCircle(color = palette.inkFaint.copy(alpha = 0.22f), radius = radius, center = center)
        val lit = litPath(center, radius, illumination.toFloat(), waxing)
        drawPath(path = lit, color = palette.accentLunar)
    }
}

/** Partie éclairée du disque : demi-disque du côté du Soleil, corrigé par le terminateur. */
private fun litPath(center: Offset, radius: Float, illumination: Float, waxing: Boolean): Path {
    val disc = Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius)

    // La Lune croissante est éclairée à droite dans l'hémisphère nord.
    val litSide = if (waxing) {
        Rect(center.x, disc.top, disc.right, disc.bottom)
    } else {
        Rect(disc.left, disc.top, center.x, disc.bottom)
    }
    val halfDisc = Path().apply {
        op(
            Path().apply { addOval(disc) },
            Path().apply { addRect(litSide) },
            PathOperation.Intersect,
        )
    }

    val semiAxis = radius * abs(1f - 2f * illumination)
    val terminator = Path().apply {
        addOval(Rect(center.x - semiAxis, center.y - radius, center.x + semiAxis, center.y + radius))
    }

    return Path().apply {
        // Sous la moitié, le terminateur creuse le demi-disque : c'est un croissant.
        // Au-delà, il le complète du côté sombre : c'est une lune gibbeuse.
        val operation = if (illumination <= 0.5f) PathOperation.Difference else PathOperation.Union
        op(halfDisc, terminator, operation)
    }
}
