package com.astro.app.feature.home.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.texture.drawSphereBody
import com.astro.app.core.designsystem.texture.drawSphereHalo
import com.astro.app.core.designsystem.texture.drawSphereInnerShadow
import com.astro.app.core.designsystem.texture.drawSphereRim
import com.astro.app.core.designsystem.texture.lerp
import com.astro.app.core.designsystem.texture.rememberDustBrush

/** Intensité de la brume interne. Basse : le texte posé dessus reste prioritaire. */
private const val MIST_INTENSITY = 0.10f

/** Agrandissement de la tuile de brume à l'intérieur d'une bille. */
private const val MIST_SCALE = 6f

/**
 * Une **bille-journée** : l'orbe qui porte une période et son planning.
 *
 * Ce n'est pas un cercle avec un contour. C'est un corps éclairé : un halo qui le
 * décolle du fond, un corps dégradé depuis le point de lumière, une ombre interne au
 * bord opposé, une brume calculée à l'intérieur et un liseré présent seulement du côté
 * éclairé. La lumière vient de la même direction que pour la Lune et le Soleil.
 *
 * [seed] décale la brume : deux billes voisines n'ont pas exactement la même matière,
 * sans que leur famille visuelle en soit rompue.
 *
 * La matière reste volontairement sourde. Le texte est porté par la bille et doit
 * rester le premier élément lu : une texture contrastée le rendrait pénible.
 */
@Composable
fun DayBead(
    size: Dp,
    emphasis: Float,
    seed: Int,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val label = contentDescription
    val mist = rememberDustBrush(palette.accentLunar, MIST_INTENSITY * emphasis, seed)

    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .clickable(onClick = onClick)
            .semantics { this.contentDescription = label },
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val radius = this.size.minDimension / 2f
            val center = Offset(this.size.width / 2f, this.size.height / 2f)

            drawSphereHalo(center, radius * 0.94f, palette.accentCosmic.copy(alpha = 0.16f * emphasis), spread = 1.10f)
            drawSphereBody(
                center = center,
                radius = radius,
                lit = lerp(palette.skyMid, palette.accentLunar, 0.22f * emphasis),
                shade = palette.skyTop.copy(alpha = 0.92f),
            )
            // La brume est agrandie autour du centre : la tuile devient nuageuse au lieu
            // de se lire comme un motif répété.
            scale(MIST_SCALE, MIST_SCALE, pivot = center) {
                drawCircle(brush = mist, radius = radius / MIST_SCALE, center = center)
            }
            drawSphereInnerShadow(center, radius, palette.skyTop.copy(alpha = 0.55f * emphasis))
            drawSphereRim(
                center = center,
                radius = radius,
                rim = palette.accentSolar.copy(alpha = 0.60f * emphasis),
                width = dimens.strokeThin.toPx(),
            )
        }
        content()
    }
}
