package com.astro.app.core.designsystem.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.astro.app.core.designsystem.AstroTheme

/**
 * Ossature commune à tous les écrans hors accueil : fond céleste, marges communes,
 * et un en-tête composé de l'exergue et du titre.
 *
 * Passer par cette ossature garantit qu'un nouvel écran hérite de l'identité visuelle
 * sans avoir à la redéfinir — c'est ce qui évite qu'un écran finisse par diverger.
 */
@Composable
fun AstroScreen(
    overline: String,
    title: String,
    modifier: Modifier = Modifier,
    /** Action posée à droite du titre (menu de la page) ; nulle, le titre est seul. */
    action: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val palette = AstroTheme.palette
    val dimens = AstroTheme.dimens

    Box(modifier = modifier.fillMaxSize()) {
        CelestialBackground()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(horizontal = dimens.screenPadding),
        ) {
            SectionLabel(
                text = overline,
                modifier = Modifier
                    .padding(top = dimens.spaceXl)
                    .screenReveal(order = 0),
            )
            // L'action est poussée au bord droit (demande du 2026-10-15) et centrée sur la
            // hauteur du titre.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = dimens.spaceSm, bottom = dimens.spaceLg)
                    .screenReveal(order = 1),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.displayMedium,
                    color = palette.ink,
                    modifier = Modifier.weight(1f),
                )
                action?.let {
                    Spacer(Modifier.width(dimens.spaceMd))
                    it()
                }
            }
            content()
        }
    }
}
