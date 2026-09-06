package com.astro.app.feature.phrases

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.R
import com.astro.app.core.ui.PlaceholderScreen

/**
 * Page Historique des phrases. Les phrases viendront d'une base locale, jamais du code.
 */
@Composable
fun PhrasesScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        overlineRes = R.string.phrases_overline,
        titleRes = R.string.feature_phrases,
        modifier = modifier,
    )
}
