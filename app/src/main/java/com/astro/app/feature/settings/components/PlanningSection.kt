package com.astro.app.feature.settings.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.astro.app.R
import com.astro.app.core.designsystem.AstroTheme
import com.astro.app.core.designsystem.components.AstroCard
import com.astro.app.core.designsystem.components.SectionLabel
import com.astro.app.core.settings.UserPreferences

/**
 * Section « Notes » : où placer, sur l'accueil, les notes sans heure, et l'affichage
 * du premier réveil sur la planète J+1.
 *
 * Ne s'applique qu'aux journées en ordre automatique : une journée dont l'ordre a été
 * choisi à la main dans le Planning s'affiche telle quelle.
 */
@Composable
fun PlanningSection(
    preferences: UserPreferences,
    onChange: ((UserPreferences) -> UserPreferences) -> Unit,
    modifier: Modifier = Modifier,
) {
    AstroCard(modifier = modifier) {
        SectionLabel(text = stringResource(R.string.settings_notes_title))
        SettingRow(
            label = stringResource(R.string.settings_untimed_first),
            description = stringResource(R.string.settings_untimed_first_hint),
            modifier = Modifier.padding(top = AstroTheme.dimens.spaceMd),
        ) {
            AstroSwitch(preferences.untimedNotesFirst) { first ->
                onChange { it.copy(untimedNotesFirst = first) }
            }
        }
        SettingRow(
            label = stringResource(R.string.settings_next_alarm),
            description = stringResource(R.string.settings_next_alarm_hint),
            modifier = Modifier.padding(top = AstroTheme.dimens.spaceMd),
        ) {
            AstroSwitch(preferences.showNextAlarm) { shown ->
                onChange { it.copy(showNextAlarm = shown) }
            }
        }
    }
}
