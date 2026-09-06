package com.astro.app.core.designsystem.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.astro.app.core.designsystem.AstroTheme

/**
 * Surface posée sur le fond céleste.
 *
 * Volontairement translucide : le dégradé et les étoiles transparaissent, ce qui
 * évite l'effet « rectangle collé sur une image ». Le liseré fin remplace l'ombre
 * portée, qui n'a pas de sens sur un ciel.
 */
@Composable
fun AstroCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    val shape = RoundedCornerShape(dimens.radiusLg)

    Column(
        modifier = modifier
            .clip(shape)
            .background(palette.surface.copy(alpha = if (palette.isDark) 0.55f else 0.72f))
            .border(dimens.hairline, palette.surfaceBorder.copy(alpha = 0.7f), shape)
            .padding(dimens.spaceLg),
        content = content,
    )
}
