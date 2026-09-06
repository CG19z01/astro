package com.astro.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme

/**
 * Décalage et taille du disque d'ombre, en fraction du rayon de la Lune.
 *
 * Un disque d'ombre de même rayon donne un croissant qui enveloppe plus de la moitié
 * du disque et se lit comme un anneau. En l'agrandissant, les cornes se rapprochent
 * et le croissant s'affine.
 */
private const val SHADOW_OFFSET = 0.52f
private const val SHADOW_RADIUS_RATIO = 1.28f

/**
 * La Lune : point d'entrée vers l'Astrologie.
 *
 * Un croissant plein plutôt qu'un disque cratéré. Le croissant est obtenu par
 * remplissage pair-impair de deux cercles décalés : c'est exact géométriquement,
 * là où un disque peint par-dessus ne fonctionnerait pas sur un fond en dégradé.
 *
 * Le disque complet reste tracé en filet fin : la partie non éclairée se devine,
 * comme sur une carte lunaire.
 */
@Composable
fun MoonButton(
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
        // Le halo doit s'éteindre exactement au bord du cadre : au-delà, la découpe
        // circulaire du bouton le trancherait net et créerait un disque visible.
        val boxRadius = this.size.minDimension / 2f
        val radius = boxRadius * 0.60f
        val center = Offset(this.size.width / 2f, this.size.height / 2f)

        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(palette.accentLunar.copy(alpha = 0.22f), Color.Transparent),
                center = center,
                radius = boxRadius,
            ),
            radius = boxRadius,
            center = center,
        )
        drawCircle(
            color = palette.inkFaint.copy(alpha = 0.55f),
            radius = radius,
            center = center,
            style = Stroke(width = dimens.hairline.toPx()),
        )
        drawPath(path = crescent(center, radius), color = palette.accentLunar)
    }
}

/**
 * Croissant = disque éclairé MOINS disque d'ombre décalé.
 *
 * C'est bien une soustraction, et non un remplissage pair-impair : ce dernier peint la
 * différence symétrique, donc la partie du disque d'ombre qui déborde hors de la Lune
 * serait peinte elle aussi, et la figure se lirait comme un anneau.
 */
private fun crescent(center: Offset, radius: Float): Path {
    val disc = Path().apply {
        addOval(Rect(center.x - radius, center.y - radius, center.x + radius, center.y + radius))
    }
    val shift = radius * SHADOW_OFFSET
    val shadowRadius = radius * SHADOW_RADIUS_RATIO
    val shadow = Path().apply {
        addOval(
            Rect(
                center.x - shadowRadius + shift, center.y - shadowRadius,
                center.x + shadowRadius + shift, center.y + shadowRadius,
            ),
        )
    }
    return Path().apply { op(disc, shadow, PathOperation.Difference) }
}
