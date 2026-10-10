package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.config.FeatureId
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.designsystem.components.TimeScaleSelector
import com.astro.app.core.settings.UserPreferences

/**
 * Section « Calendrier » : la vue d'ouverture et les deux marqueurs.
 *
 * N'apparaît que si le Calendrier est activé : sans lui, ces réglages n'ont pas d'objet.
 * C'est la même règle que pour les fonctionnalités, qui disparaissent avec la barre.
 *
 * Le sélecteur de vue est celui du Calendrier lui-même : le choix se présente ici
 * exactement comme on le retrouvera sur la page.
 */
@Composable
fun CalendarSection(
    featureStates: Map<FeatureId, Boolean>,
    preferences: UserPreferences,
    onChange: ((UserPreferences) -> UserPreferences) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (featureStates[FeatureId.CALENDAR] != true) return
    val dimens = AstroTheme.dimens

    AstroCard(modifier = modifier) {
        SectionLabel(text = stringResource(R.string.settings_calendar_title))
        SettingRow(
            label = stringResource(R.string.settings_calendar_initial_scale),
            description = stringResource(R.string.settings_calendar_initial_scale_hint),
            modifier = Modifier.padding(top = dimens.spaceMd),
        ) {}
        TimeScaleSelector(
            selected = preferences.calendarInitialScale,
            onSelect = { scale -> onChange { it.copy(calendarInitialScale = scale) } },
        )
        SettingRow(
            label = stringResource(R.string.settings_calendar_full_moons),
            description = stringResource(R.string.settings_calendar_full_moons_hint),
            modifier = Modifier.padding(top = dimens.spaceSm),
        ) {
            AstroSwitch(preferences.showFullMoons) { shown -> onChange { it.copy(showFullMoons = shown) } }
        }
        SettingRow(
            label = stringResource(R.string.settings_calendar_markers),
            description = stringResource(R.string.settings_calendar_markers_hint),
        ) {
            AstroSwitch(preferences.showPlanningMarkers) { shown ->
                onChange { it.copy(showPlanningMarkers = shown) }
            }
        }
    }
}
