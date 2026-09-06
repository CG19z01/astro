package com.astro.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.FeatureDefinition
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.designsystem.components.screenReveal

/**
 * Page Paramètres.
 *
 * Une section par famille de réglages, chacune dans sa carte. Les futures sections
 * — compte, signe par défaut, couleurs, voix, horaires du Soleil — viendront s'ajouter
 * sous la même forme, sans toucher aux existantes.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val states by viewModel.featureStates.collectAsState()
    val dimens = AstroTheme.dimens
    val toggleable = FeatureCatalog.definitions.filter { it.canBeDisabled }

    AstroScreen(
        overline = stringResource(R.string.settings_overline),
        title = stringResource(R.string.feature_settings),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            AstroCard(modifier = Modifier.screenReveal(order = 2)) {
                SectionLabel(text = stringResource(R.string.settings_features_title))
                toggleable.forEachIndexed { index, definition ->
                    FeatureToggleRow(
                        definition = definition,
                        enabled = states[definition.id] == true,
                        onChange = { viewModel.setFeatureEnabled(definition.id, it) },
                        modifier = Modifier.padding(top = if (index == 0) dimens.spaceMd else dimens.spaceXs),
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureToggleRow(
    definition: FeatureDefinition,
    enabled: Boolean,
    onChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val palette = AstroTheme.palette
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = stringResource(definition.titleRes),
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) palette.ink else palette.inkFaint,
        )
        Switch(
            checked = enabled,
            onCheckedChange = onChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = palette.skyTop,
                checkedTrackColor = palette.accentSolar,
                checkedBorderColor = palette.accentSolar,
                uncheckedThumbColor = palette.inkFaint,
                uncheckedTrackColor = palette.surface,
                uncheckedBorderColor = palette.surfaceBorder,
            ),
        )
    }
}
