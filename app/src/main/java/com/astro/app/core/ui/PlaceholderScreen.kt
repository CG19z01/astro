package com.astro.app.core.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal

/**
 * Écran d'attente pour une section routée mais pas encore construite.
 *
 * Il hérite de toute l'identité visuelle — fond, ambiance, typographie, cascade
 * d'apparition — pour que la navigation puisse être jugée dans son rendu définitif
 * sans afficher de contenu inventé.
 */
@Composable
fun PlaceholderScreen(
    @StringRes overlineRes: Int,
    @StringRes titleRes: Int,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    AstroScreen(
        overline = stringResource(overlineRes),
        title = stringResource(titleRes),
        modifier = modifier,
    ) {
        AstroCard(modifier = Modifier.screenReveal(order = 2)) {
            Text(
                text = stringResource(R.string.placeholder_title),
                style = MaterialTheme.typography.titleMedium,
                color = palette.ink,
            )
            Text(
                text = stringResource(R.string.placeholder_body),
                style = MaterialTheme.typography.bodyMedium,
                color = palette.inkMuted,
                modifier = Modifier.padding(top = dimens.spaceSm),
            )
        }
    }
}
