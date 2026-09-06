package com.astro.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.SectionLabel

/**
 * La phrase du jour : point d'entrée vers l'historique.
 *
 * Traitée comme une citation et non comme un bouton — exergue, texte en serif, et
 * un liseré très fin sur un fond à peine teinté. C'est l'élément le plus large de
 * l'écran : il sert d'ancre horizontale au milieu d'une composition faite de cercles.
 */
@Composable
fun PhraseCapsule(
    overline: String,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val shape = RoundedCornerShape(dimens.radiusXl)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                Brush.horizontalGradient(
                    listOf(
                        palette.surface.copy(alpha = if (palette.isDark) 0.62f else 0.78f),
                        palette.surface.copy(alpha = if (palette.isDark) 0.34f else 0.58f),
                    ),
                ),
            )
            .border(dimens.hairline, palette.surfaceBorder.copy(alpha = 0.75f), shape)
            .clickable(onClick = onClick)
            .padding(horizontal = dimens.spaceLg, vertical = dimens.spaceMd),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(dimens.spaceSm),
    ) {
        SectionLabel(text = overline)
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium,
            color = palette.ink,
            textAlign = TextAlign.Center,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
