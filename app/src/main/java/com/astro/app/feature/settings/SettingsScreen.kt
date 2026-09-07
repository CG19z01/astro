package com.astro.app.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.FeatureCatalog
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.feature.settings.components.AstroSwitch
import com.astro.app.feature.settings.components.EphemerisSection
import com.astro.app.feature.settings.components.LocationSection
import com.astro.app.feature.settings.components.SettingRow
import com.astro.app.feature.settings.components.ZonePickerDialog

/**
 * Page Paramètres, atteinte en appuyant sur le Soleil.
 *
 * Quatre sections : affichage, lieu d'observation, éphémérides du Soleil, éphémérides
 * de la Lune. Chaque nouvelle famille de réglages viendra s'ajouter sous la même forme.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val states by viewModel.featureStates.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val dimens = AstroTheme.dimens
    val palette = AstroTheme.palette
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    var pickingZone by remember { mutableStateOf(false) }

    AstroScreen(
        overline = stringResource(R.string.settings_overline),
        title = stringResource(R.string.feature_settings),
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(bottom = dimens.spaceXxl),
            verticalArrangement = Arrangement.spacedBy(dimens.spaceMd),
        ) {
            // Le Soleil ouvre cette page : ses horaires viennent donc en premier.
            EphemerisSection(
                preferences = preferences,
                locale = locale,
                modifier = Modifier.screenReveal(order = 2),
            )

            AstroCard(modifier = Modifier.screenReveal(order = 3)) {
                SectionLabel(text = stringResource(R.string.settings_display_title))
                SettingRow(
                    label = stringResource(R.string.settings_show_nav_bar),
                    description = stringResource(R.string.settings_show_nav_bar_hint),
                    modifier = Modifier.padding(top = dimens.spaceMd),
                ) {
                    AstroSwitch(preferences.showNavigationBar) { shown ->
                        viewModel.update { it.copy(showNavigationBar = shown) }
                    }
                }
                // Ces interrupteurs décident du contenu de la barre : sans barre, ils
                // n'ont plus d'objet et disparaissent avec elle.
                if (preferences.showNavigationBar) {
                    Text(
                        text = stringResource(R.string.settings_features_title),
                        style = MaterialTheme.typography.labelMedium,
                        color = palette.inkFaint,
                        modifier = Modifier.padding(top = dimens.spaceLg, bottom = dimens.spaceXs),
                    )
                    FeatureCatalog.definitions.filter { it.canBeDisabled }.forEach { definition ->
                        SettingRow(label = stringResource(definition.titleRes)) {
                            AstroSwitch(states[definition.id] == true) { enabled ->
                                viewModel.setFeatureEnabled(definition.id, enabled)
                            }
                        }
                    }
                }
            }

            LocationSection(
                overline = stringResource(R.string.settings_location_title),
                hint = stringResource(R.string.settings_location_hint),
                latitudeLabel = stringResource(R.string.settings_latitude),
                longitudeLabel = stringResource(R.string.settings_longitude),
                zoneLabel = stringResource(R.string.settings_zone),
                latitude = preferences.latitude?.toString().orEmpty(),
                longitude = preferences.longitude?.toString().orEmpty(),
                zoneId = preferences.zoneId.id,
                onLatitudeChange = { text ->
                    viewModel.update { it.copy(latitude = UserPreferences.validLatitude(text.toDoubleOrNull())) }
                },
                onLongitudeChange = { text ->
                    viewModel.update { it.copy(longitude = UserPreferences.validLongitude(text.toDoubleOrNull())) }
                },
                onPickZone = { pickingZone = true },
                modifier = Modifier.screenReveal(order = 5),
            )
        }
    }

    if (pickingZone) {
        ZonePickerDialog(
            current = preferences.zoneId,
            title = stringResource(R.string.settings_zone),
            searchLabel = stringResource(R.string.settings_zone_search),
            dismissLabel = stringResource(R.string.action_close),
            onSelect = { zone ->
                viewModel.update { it.copy(zoneId = zone) }
                pickingZone = false
            },
            onDismiss = { pickingZone = false },
        )
    }
}
