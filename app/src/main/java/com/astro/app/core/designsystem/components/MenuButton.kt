package com.astro.app.core.designsystem.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroTheme

/**
 * Bouton de menu d'une page : un **cercle fin, légèrement brillant** — et non les trois
 * traits habituels (demande du 2026-10-15). Le halo est un dégradé radial qui s'éteint
 * avant le bord de la zone : un halo coupé net se lirait comme un disque.
 *
 * La zone d'appui respecte la taille tactile minimale ; le cercle reste petit.
 */
@Composable
fun MenuButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.touchTarget)
            .clip(CircleShape)
            .clickable(role = Role.Button, onClick = onClick)
            .semantics { contentDescription = label },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(dimens.touchTarget)) {
            // Le cercle a la hauteur des capitales du titre : il s'aligne en haut sur le
            // sommet du « P » et en bas sur la ligne de base, au lieu de flotter entre.
            val ring = TITLE_CAP_HEIGHT.toPx() / 2f
            // Centré dans sa zone d'appui, comme l'étoile des favoris : les deux finissent sur
            // la marge, le cercle tombe donc sur la colonne des étoiles (demande du
            // 2026-10-15). Un peu remonté : le titre place ses capitales légèrement
            // au-dessus du milieu de sa ligne (mesuré).
            val ringCenter = Offset(center.x, center.y - RING_RAISE.toPx())
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(palette.accentLunar.copy(alpha = GLOW_ALPHA), Color.Transparent),
                    center = ringCenter,
                    radius = size.minDimension / 2f,
                ),
                radius = size.minDimension / 2f,
                center = ringCenter,
            )
            drawCircle(color = palette.ink, radius = ring, center = ringCenter, style = Stroke(width = dimens.hairline.toPx()))
        }
    }
}

/**
 * Hauteur des capitales du titre de page (Cormorant 36 sp) : 68 px mesurés sur
 * l'émulateur (densité 2,625), soit 26 dp.
 */
private val TITLE_CAP_HEIGHT = 26.dp

/** Remontée du cercle : 3,5 px mesurés entre son centre et celui des capitales. */
private val RING_RAISE = 1.3.dp

/** Intensité du halo : présent sans attirer l'œil plus que le titre. */
private const val GLOW_ALPHA = 0.35f
