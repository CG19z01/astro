package com.astro.app.feature.phrases.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.phrases.Phrase

/** Onglet « Favoris » : les phrases étoilées ; l'étoile les retire de la liste. */
@Composable
fun FavoritesPanel(favorites: List<Phrase>, onToggleFavorite: (Phrase) -> Unit, modifier: Modifier = Modifier) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(dimens.spaceMd)) {
        if (favorites.isEmpty()) {
            Text(
                text = stringResource(R.string.phrases_favorites_empty),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkMuted,
            )
        }
        favorites.forEach { phrase ->
            PhraseItem(phrase = phrase, favorite = true, onToggleFavorite = { onToggleFavorite(phrase) })
        }
    }
}
