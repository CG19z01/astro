package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.PhraseStyle
import com.astro.app.core.phrases.Phrase

/**
 * Une phrase de l'historique, ou des favoris : son texte en Nunito italique, son auteur
 * s'il est connu, et l'étoile de favori. **Sans date** (demande du 2026-10-15) : la
 * phrase se lit pour elle-même, pas comme une entrée de journal.
 */
@Composable
fun PhraseItem(phrase: Phrase, favorite: Boolean, onToggleFavorite: () -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = phrase.text, style = PhraseStyle, color = palette.ink)
            if (phrase.author.isNotBlank()) {
                Text(
                    text = phrase.author,
                    style = MaterialTheme.typography.bodySmall,
                    color = palette.inkMuted,
                    modifier = Modifier.padding(top = dimens.spaceXs),
                )
            }
        }
        FavoriteStar(
            favorite = favorite,
            label = stringResource(R.string.phrases_favorite),
            onToggle = onToggleFavorite,
            modifier = Modifier.padding(start = dimens.spaceSm),
        )
    }
}
