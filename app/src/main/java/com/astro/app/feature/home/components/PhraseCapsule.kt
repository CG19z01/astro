package com.astro.app.feature.home.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.astro.app.core.config.PhrasesConfig
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
    /** Auteur de la phrase ; vide, la ligne n'apparaît pas. */
    author: String,
    /** Hauteur maximale du texte de la phrase. */
    textMaxHeight: Dp,
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
        // Une phrase longue rétrécit au lieu de pousser la capsule sur le dodécaèdre et
        // le Soleil : la hauteur du texte est bornée, la taille s'y ajuste.
        val headline = MaterialTheme.typography.headlineMedium
        BasicText(
            text = text,
            style = headline.copy(
                color = palette.ink,
                textAlign = TextAlign.Center,
                lineHeight = PhrasesConfig.CAPSULE_LINE_HEIGHT_EM.em,
            ),
            overflow = TextOverflow.Ellipsis,
            autoSize = TextAutoSize.StepBased(
                minFontSize = PhrasesConfig.MIN_CAPSULE_FONT_SP.sp,
                maxFontSize = headline.fontSize,
            ),
            modifier = Modifier.heightIn(max = textMaxHeight),
        )
        if (author.isNotBlank()) {
            Text(
                text = author,
                style = MaterialTheme.typography.bodySmall,
                color = palette.inkMuted,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
