package com.astro.app.core.designsystem.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.astro.app.core.designsystem.AstroMotion
import com.astro.app.core.designsystem.AstroTheme

/** Un élément de la barre, décrit par ce qu'il affiche et non par une fonctionnalité. */
data class AstroNavItem(
    val key: String,
    val label: String,
    val selected: Boolean,
    val icon: @Composable (Color) -> Unit,
    val onClick: () -> Unit,
)

/**
 * Barre de navigation de l'application.
 *
 * Elle ne pose pas de bandeau opaque : un dégradé la fait émerger du fond, surmonté
 * d'un filet fin. L'écran actif est signalé par un halo doux derrière son pictogramme
 * plutôt que par une pastille pleine, qui jurerait avec le reste du dessin.
 */
@Composable
fun AstroNavBar(items: List<AstroNavItem>, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    if (items.isEmpty()) return

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    0.35f to palette.skyBottom.copy(alpha = 0.85f),
                    1f to palette.skyBottom,
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 18.dp)
                .height(1.dp)
                .background(palette.orbit.copy(alpha = 0.5f)),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            items.forEach { item -> NavEntry(item) }
        }
    }
}

@Composable
private fun NavEntry(item: AstroNavItem) {
    val palette = AstroTheme.palette
    val tint by animateColorAsState(
        targetValue = if (item.selected) palette.accentSolar else palette.inkFaint,
        animationSpec = AstroMotion.quick(),
        label = "navTint",
    )
    val labelColor by animateColorAsState(
        targetValue = if (item.selected) palette.ink else palette.inkFaint,
        animationSpec = AstroMotion.quick(),
        label = "navLabel",
    )
    val interaction = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
        modifier = Modifier
            .clip(RoundedCornerShape(14.dp))
            .clickable(interactionSource = interaction, indication = null, onClick = item.onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(32.dp)) {
            if (item.selected) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            Brush.radialGradient(
                                listOf(palette.accentSolar.copy(alpha = 0.22f), Color.Transparent),
                            ),
                        ),
                )
            }
            item.icon(tint)
        }
        Text(text = item.label, style = MaterialTheme.typography.labelMedium, color = labelColor)
    }
}
