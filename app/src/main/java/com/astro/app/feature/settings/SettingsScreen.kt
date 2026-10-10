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
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.R
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.settings.UserPreferences
import com.astro.app.core.designsystem.components.AstroScreen
import com.astro.app.core.designsystem.components.screenReveal
import com.astro.app.core.location.ObservationPoints
import com.astro.app.feature.settings.components.CalendarSection
import com.astro.app.feature.settings.components.LocationSourceRow
import com.astro.app.feature.settings.components.PlanningSection
import com.astro.app.feature.settings.components.DisplaySection
import com.astro.app.feature.settings.components.PlanetColorSection
import com.astro.app.feature.settings.components.EphemerisSection
import com.astro.app.feature.settings.components.LocationSection
import com.astro.app.feature.settings.components.ZonePickerDialog

/**
 * Page Paramètres, atteinte en appuyant sur le Soleil.
 *
 * Sections : éphémérides du Soleil, affichage, Calendrier, notes de l'accueil, couleur
 * des planètes-jours, lieu d'observation. Chaque nouvelle famille de réglages viendra s'ajouter sous la même forme.
 */
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
) {
    val states by viewModel.featureStates.collectAsState()
    val preferences by viewModel.preferences.collectAsState()
    val sessionPoint by viewModel.sessionPoint.collectAsState()
    val locationRequest by viewModel.locationRequest.collectAsState()
    val point = ObservationPoints.resolve(sessionPoint, preferences)
    val dimens = AstroTheme.dimens
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
                point = point,
                zoneId = preferences.zoneId,
                locale = locale,
                modifier = Modifier.screenReveal(order = 2),
            )

            DisplaySection(
                showNavigationBar = preferences.showNavigationBar,
                featureStates = states,
                onToggleNavigationBar = { shown -> viewModel.update { it.copy(showNavigationBar = shown) } },
                onToggleFeature = viewModel::setFeatureEnabled,
                modifier = Modifier.screenReveal(order = 3),
            )

            CalendarSection(
                featureStates = states,
                preferences = preferences,
                onChange = viewModel::update,
                modifier = Modifier.screenReveal(order = 4),
            )

            PlanningSection(
                preferences = preferences,
                onChange = viewModel::update,
                modifier = Modifier.screenReveal(order = 5),
            )

            PlanetColorSection(
                colors = preferences.planetColors,
                locale = locale,
                onChange = { updated -> viewModel.update { it.copy(planetColors = updated) } },
                modifier = Modifier.screenReveal(order = 6),
            )

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
                modifier = Modifier.screenReveal(order = 7),
            ) {
                LocationSourceRow(
                    hasSessionPoint = sessionPoint != null,
                    request = locationRequest,
                    onForget = viewModel::forgetDeviceLocation,
                    onGranted = viewModel::requestDeviceLocation,
                    onDenied = viewModel::onLocationDenied,
                )
            }
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
