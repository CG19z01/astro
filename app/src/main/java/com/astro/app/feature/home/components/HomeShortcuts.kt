package com.astro.app.feature.home.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import com.astro.app.R
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureId
import com.astro.app.core.config.HomeLayoutConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.screenReveal

/**
 * Les points d'entrée de la page d'accueil : Lune, phrase du jour, dodécaèdre, Soleil.
 *
 * Chacun n'apparaît que si sa fonctionnalité est activée : couper l'Astrologie fait
 * disparaître la Lune, et pas seulement l'entrée de la barre de navigation.
 */
@Composable
fun HomeShortcuts(
    width: Dp,
    height: Dp,
    states: Map<FeatureId, Boolean>,
    onOpenFeature: (String) -> Unit,
    onResetToToday: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens

    Box(modifier = modifier.fillMaxSize()) {
        Shortcut(FeatureId.ASTROLOGY, states) {
            val bounds = HomeLayoutConfig.moon.bounds(width, height)
            MoonButton(
                size = bounds.size,
                contentDescription = stringResource(R.string.cd_moon),
                onClick = { onOpenFeature(routeOf(FeatureId.ASTROLOGY)) },
                modifier = Modifier.offset(x = bounds.x, y = bounds.y).screenReveal(1),
            )
        }

        Shortcut(FeatureId.SETTINGS, states) {
            val bounds = HomeLayoutConfig.sun.bounds(width, height)
            SunButton(
                size = bounds.size,
                contentDescription = stringResource(R.string.cd_sun),
                onClick = { onOpenFeature(routeOf(FeatureId.SETTINGS)) },
                modifier = Modifier.offset(x = bounds.x, y = bounds.y).screenReveal(3),
            )
        }

        Shortcut(FeatureId.MANIFESTATION, states) {
            val bounds = HomeLayoutConfig.dodecahedron.bounds(width, height)
            DodecahedronButton(
                size = bounds.size,
                contentDescription = stringResource(R.string.cd_dodecahedron),
                longPressLabel = stringResource(R.string.cd_dodecahedron_long_press),
                onClick = { onOpenFeature(routeOf(FeatureId.MANIFESTATION)) },
                onLongClick = onResetToToday,
                modifier = Modifier.offset(x = bounds.x, y = bounds.y).screenReveal(2),
            )
        }

        Shortcut(FeatureId.PHRASES, states) {
            PhraseCapsule(
                overline = stringResource(R.string.home_phrase_overline),
                text = stringResource(R.string.home_phrase_empty),
                onClick = { onOpenFeature(routeOf(FeatureId.PHRASES)) },
                modifier = Modifier
                    .padding(horizontal = dimens.screenPadding)
                    .offset(y = height * HomeLayoutConfig.PHRASE_CENTER_Y)
                    .screenReveal(0),
            )
        }
    }
}

@Composable
private fun Shortcut(
    id: FeatureId,
    states: Map<FeatureId, Boolean>,
    content: @Composable () -> Unit,
) {
    if (states[id] == true) content()
}

private fun routeOf(id: FeatureId): String = FeatureCatalog.definitionOf(id).route
