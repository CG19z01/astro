package com.astro.app.feature.home

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.lifecycle.viewmodel.compose.viewModel
import com.astro.app.core.config.FeatureId
import com.astro.app.core.config.LocaleConfig
import com.astro.app.core.designsystem.components.CelestialBackground
import com.astro.app.core.designsystem.components.onHorizontalSwipe
import com.astro.app.feature.home.components.DayWheel
import com.astro.app.feature.home.components.HomeShortcuts
import java.time.LocalDate

/**
 * Page d'accueil.
 *
 * Trois couches : le fond céleste, la roue des sept journées, puis les points d'entrée.
 * Un swipe horizontal déplace le curseur temporel, ce qui met les deux billes à jour.
 * Un maintien long sur le dodécaèdre ramène ce curseur sur la journée en cours.
 */
@Composable
fun HomeScreen(
    states: Map<FeatureId, Boolean>,
    onOpenFeature: (String) -> Unit,
    onOpenPlanning: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory),
) {
    val locale = LocaleConfig.resolve(LocalConfiguration.current.locales[0])
    val cursor by viewModel.cursor.collectAsState()
    val planetColors by viewModel.planetColors.collectAsState()
    val dayPlannings by viewModel.dayPlannings.collectAsState()
    val untimedFirst by viewModel.untimedNotesFirst.collectAsState()
    val wakeAlarm by viewModel.wakeAlarm.collectAsState()

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .onHorizontalSwipe(key = cursor.scale, onSwipe = viewModel::onSwipe),
    ) {
        CelestialBackground()
        DayWheel(
            width = maxWidth,
            height = maxHeight,
            cursor = cursor,
            locale = locale,
            plannings = dayPlannings,
            untimedFirst = untimedFirst,
            planetColors = planetColors,
            wakeAlarm = wakeAlarm,
            onOpenPlanning = onOpenPlanning,
            modifier = Modifier.statusBarsPadding(),
        )
        HomeShortcuts(
            width = maxWidth,
            height = maxHeight,
            states = states,
            onOpenFeature = onOpenFeature,
            onResetToToday = viewModel::resetToToday,
            modifier = Modifier.statusBarsPadding(),
        )
    }
}
