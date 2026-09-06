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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.astro.app.core.designsystem.AstroTheme

/**
 * Une **bille-journée** : la sphère qui porte une période et son planning.
 *
 * Ce n'est ni une carte ni un bouton posé sur la page. C'est un corps céleste : un
 * halo qui la décolle du fond, un contour en dégradé qui suggère la courbure, et un
 * intérieur à peine plus clair que le ciel. Son contenu vit à l'intérieur d'elle,
 * ce qui la rend cliquable dans son ensemble — texte compris.
 *
 * Elle peut être plus grande que l'écran et déborder : c'est ce qui donne la
 * profondeur de la composition. Le contenu est donc placé par l'appelant, en
 * fractions de la bille, pour rester dans la partie visible.
 */
@Composable
fun DayBead(
    size: Dp,
    emphasis: Float,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val label = contentDescription

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

            // Intérieur : à peine détaché du ciel, sinon la bille devient un disque plein.
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        palette.accentCosmic.copy(alpha = 0.10f * emphasis),
                        palette.accentLunar.copy(alpha = 0.04f * emphasis),
                        Color.Transparent,
                    ),
                    center = center,
                    radius = radius,
                ),
                radius = radius,
                center = center,
            )
            // Contour en dégradé : il naît de l'accent solaire et s'éteint, ce qui
            // suggère une courbure éclairée d'un seul côté.
            drawCircle(
                brush = Brush.linearGradient(
                    colors = listOf(
                        palette.accentSolar.copy(alpha = 0.85f * emphasis),
                        palette.accentLunar.copy(alpha = 0.50f * emphasis),
                        palette.orbit.copy(alpha = 0.28f * emphasis),
                    ),
                    start = Offset(center.x - radius, center.y - radius),
                    end = Offset(center.x + radius, center.y + radius),
                ),
                radius = radius,
                center = center,
                style = Stroke(width = dimens.strokeThin.toPx()),
            )
        }
        content()
    }
}
