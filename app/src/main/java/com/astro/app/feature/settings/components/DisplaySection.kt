package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.astro.app.R
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureId
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel

/**
 * Section « Affichage » : la barre de navigation et son contenu.
 *
 * Les deux réglages vivent dans la même carte parce qu'ils portent sur le même sujet.
 * Les interrupteurs de fonctionnalités décident du contenu de la barre : **sans barre,
 * ils n'ont plus d'objet et disparaissent avec elle.**
 *
 * À savoir : ces mêmes interrupteurs pilotent aussi les raccourcis de la page d'accueil.
 * Masquer la barre retire donc le moyen de les rallumer — la sortie de secours tient à
 * ce que les Paramètres ne soient pas désactivables.
 */
@Composable
fun DisplaySection(
    showNavigationBar: Boolean,
    featureStates: Map<FeatureId, Boolean>,
    onToggleNavigationBar: (Boolean) -> Unit,
    onToggleFeature: (FeatureId, Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier) {
        SectionLabel(text = stringResource(R.string.settings_display_title))
        SettingRow(
            label = stringResource(R.string.settings_show_nav_bar),
            description = stringResource(R.string.settings_show_nav_bar_hint),
            modifier = Modifier.padding(top = dimens.spaceMd),
        ) {
            AstroSwitch(showNavigationBar, onToggleNavigationBar)
        }
        if (!showNavigationBar) return@AstroCard

        FeatureCatalog.definitions.filter { it.canBeDisabled }.forEachIndexed { index, definition ->
            SettingRow(
                label = stringResource(definition.titleRes),
                modifier = Modifier.padding(top = if (index == 0) dimens.spaceMd else 0.dp),
            ) {
                AstroSwitch(featureStates[definition.id] == true) { enabled ->
                    onToggleFeature(definition.id, enabled)
                }
            }
        }
    }
}
