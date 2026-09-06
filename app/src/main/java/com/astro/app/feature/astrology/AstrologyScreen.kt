package com.astro.app.feature.astrology

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.astro.app.R
import com.astro.app.core.ui.PlaceholderScreen

/**
 * Page Astrologie. Contenu réel à venir : 12 signes, sélection, informations lunaires.
 */
@Composable
fun AstrologyScreen(modifier: Modifier = Modifier) {
    PlaceholderScreen(
        overlineRes = R.string.astrology_overline,
        titleRes = R.string.feature_astrology,
        modifier = modifier,
    )
}
