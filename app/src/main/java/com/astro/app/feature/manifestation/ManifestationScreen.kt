package com.astro.app.feature.manifestation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.R
import com.astro.app.core.ui.PlaceholderScreen

/**
 * Page Manifestation. Création de textes, dictée et lecture vocale à venir.
 */
@Composable
fun ManifestationScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        overlineRes = R.string.manifestation_overline,
        titleRes = R.string.feature_manifestation,
        modifier = modifier,
    )
}
